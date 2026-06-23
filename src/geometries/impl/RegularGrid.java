package geometries.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import geometries.api.Intersectable;
import primitives.AABB;
import primitives.Point;
import primitives.Ray;

import static primitives.Util.alignZero;

/**
 * Regular grid acceleration structure.
 * <p>
 * Read-only after construction and safe for multithreaded traversal.
 */
public final class RegularGrid {

    public static final class Config {
        public final double density; // density multiplier
        public final int minResolution;
        public final int maxResolution;

        public Config(double density, int minResolution, int maxResolution) {
            if (density <= 0) {
                throw new IllegalArgumentException("density must be positive");
            }
            if (minResolution > maxResolution) {
                throw new IllegalArgumentException("minResolution must be <= maxResolution");
            }
            if (minResolution <= 0) {
                throw new IllegalArgumentException("minResolution must be > 0");
            }
            this.density = density;
            this.minResolution = minResolution;
            this.maxResolution = maxResolution;
        }
    }

    private final AABB sceneAABB;
    private final int nx, ny, nz;
    private final double vx, vy, vz;
    private final Map<Long, List<Intersectable>> voxels = new HashMap<>();
    private final List<Intersectable> infiniteGeometries = new ArrayList<>();

    private static long keyOf(int x, int y, int z) {
        return ((long) x << 40) | ((long) y << 20) | (long) z;
    }

    public RegularGrid(List<Intersectable> geometries, Config cfg) {
        // collect finite AABBs
        AABB global = null;
        List<Pair> finite = new ArrayList<>();

        for (Intersectable g : geometries) {
            if (g instanceof geometries.api.Geometry) {
                primitives.AABB aabb = ((geometries.api.Geometry) g).getAABB();
                if (aabb == null) {
                    infiniteGeometries.add(g);
                } else {
                    finite.add(new Pair(g, aabb));
                    global = AABB.union(global, aabb);
                }
            } else {
                // unknown type, treat as infinite
                infiniteGeometries.add(g);
            }
        }

        this.sceneAABB = global;

        if (global == null || finite.isEmpty()) {
            // nothing to grid
            nx = ny = nz = 0;
            vx = vy = vz = 0;
            return;
        }

        // scene dimensions
        double sx = global.max.x() - global.min.x();
        double sy = global.max.y() - global.min.y();
        double sz = global.max.z() - global.min.z();

        int n = Math.max(1, finite.size());

        double volume = sx * sy * sz;
        double scale = Math.cbrt(cfg.density * (double) n / Math.max(volume, 1e-9));

        int cx = Math.max(cfg.minResolution, (int) Math.ceil(sx * scale));
        int cy = Math.max(cfg.minResolution, (int) Math.ceil(sy * scale));
        int cz = Math.max(cfg.minResolution, (int) Math.ceil(sz * scale));

        nx = Math.min(cfg.maxResolution, Math.max(1, cx));
        ny = Math.min(cfg.maxResolution, Math.max(1, cy));
        nz = Math.min(cfg.maxResolution, Math.max(1, cz));

        vx = sx / nx;
        vy = sy / ny;
        vz = sz / nz;

        // assign geometries to voxels
        for (Pair p : finite) {
            int x0 = clamp((int) Math.floor((p.aabb.min.x() - global.min.x()) / vx), 0, nx - 1);
            int x1 = clamp((int) Math.floor((p.aabb.max.x() - global.min.x()) / vx), 0, nx - 1);
            int y0 = clamp((int) Math.floor((p.aabb.min.y() - global.min.y()) / vy), 0, ny - 1);
            int y1 = clamp((int) Math.floor((p.aabb.max.y() - global.min.y()) / vy), 0, ny - 1);
            int z0 = clamp((int) Math.floor((p.aabb.min.z() - global.min.z()) / vz), 0, nz - 1);
            int z1 = clamp((int) Math.floor((p.aabb.max.z() - global.min.z()) / vz), 0, nz - 1);

            for (int i = x0; i <= x1; ++i) {
                for (int j = y0; j <= y1; ++j) {
                    for (int k = z0; k <= z1; ++k) {
                        long key = keyOf(i, j, k);
                        voxels.computeIfAbsent(key, __ -> new ArrayList<>()).add(p.geometry);
                    }
                }
            }
        }
    }

    private static int clamp(int v, int a, int b) {
        return Math.max(a, Math.min(b, v));
    }

    /**
     * Returns intersections of ray with geometries using the grid traversal.
     */
    public List<geometries.api.Intersectable.Intersection> calcIntersections(Ray ray) {
        List<geometries.api.Intersectable.Intersection> result = new ArrayList<>();

        // always test infinite geometries
        for (Intersectable g : infiniteGeometries) {
            var ints = g.calcIntersections(ray);
            if (ints != null) result.addAll(ints);
        }

        if (sceneAABB == null) {
            return result.isEmpty() ? null : result;
        }

        double[] tRange = sceneAABB.intersect(ray);
        if (tRange == null) {
            return result.isEmpty() ? null : result;
        }

        double tEnter = tRange[0];
        double tExit = tRange[1];

        double tStart = Math.max(tEnter, 0.0);
        Point p = ray.getPoint(tStart);

        int ix = clamp((int) Math.floor((p.x() - sceneAABB.min.x()) / vx), 0, nx - 1);
        int iy = clamp((int) Math.floor((p.y() - sceneAABB.min.y()) / vy), 0, ny - 1);
        int iz = clamp((int) Math.floor((p.z() - sceneAABB.min.z()) / vz), 0, nz - 1);

        double rx = ray.direction().x();
        double ry = ray.direction().y();
        double rz = ray.direction().z();

        int stepX = rx > 0 ? 1 : (rx < 0 ? -1 : 0);
        int stepY = ry > 0 ? 1 : (ry < 0 ? -1 : 0);
        int stepZ = rz > 0 ? 1 : (rz < 0 ? -1 : 0);

        double nextBoundaryX = sceneAABB.min.x() + (ix + (stepX > 0 ? 1 : 0)) * vx;
        double nextBoundaryY = sceneAABB.min.y() + (iy + (stepY > 0 ? 1 : 0)) * vy;
        double nextBoundaryZ = sceneAABB.min.z() + (iz + (stepZ > 0 ? 1 : 0)) * vz;

        double tMaxX = stepX == 0 ? Double.POSITIVE_INFINITY : alignZero((nextBoundaryX - ray.origin().x()) / rx);
        double tMaxY = stepY == 0 ? Double.POSITIVE_INFINITY : alignZero((nextBoundaryY - ray.origin().y()) / ry);
        double tMaxZ = stepZ == 0 ? Double.POSITIVE_INFINITY : alignZero((nextBoundaryZ - ray.origin().z()) / rz);

        double tDeltaX = stepX == 0 ? Double.POSITIVE_INFINITY : Math.abs(vx / rx);
        double tDeltaY = stepY == 0 ? Double.POSITIVE_INFINITY : Math.abs(vy / ry);
        double tDeltaZ = stepZ == 0 ? Double.POSITIVE_INFINITY : Math.abs(vz / rz);

        // tested geometries set to avoid duplicate checks
        Set<Intersectable> tested = Collections.newSetFromMap(new IdentityHashMap<>());

        while (ix >= 0 && ix < nx && iy >= 0 && iy < ny && iz >= 0 && iz < nz && tStart <= tExit) {
            long key = keyOf(ix, iy, iz);
            var list = voxels.get(key);
            if (list != null) {
                for (Intersectable g : list) {
                    if (!tested.contains(g)) {
                        tested.add(g);
                        var ints = g.calcIntersections(ray);
                        if (ints != null) result.addAll(ints);
                    }
                }
            }

            // advance to next voxel
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    ix += stepX;
                    tStart = tMaxX;
                    tMaxX += tDeltaX;
                } else {
                    iz += stepZ;
                    tStart = tMaxZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    iy += stepY;
                    tStart = tMaxY;
                    tMaxY += tDeltaY;
                } else {
                    iz += stepZ;
                    tStart = tMaxZ;
                    tMaxZ += tDeltaZ;
                }
            }
        }

        return result.isEmpty() ? null : result;
    }

    private static final class Pair {
        final Intersectable geometry;
        final AABB aabb;

        Pair(Intersectable g, AABB a) {
            geometry = g;
            aabb = a;
        }
    }
}
