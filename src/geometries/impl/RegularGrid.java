package geometries.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import geometries.api.Geometry;
import geometries.api.Intersectable;
import primitives.AABB;
import primitives.Point;
import primitives.Ray;

import static primitives.Util.alignZero;

/**
 * Regular Grid acceleration structure.
 * <p>
 * The grid divides the finite part of the scene into 3D voxels.
 * During ray traversal, only geometries stored in crossed voxels are tested.
 * Infinite geometries are stored separately and are always tested normally.
 * </p>
 * <p>
 * This class is read-only after construction and is therefore safe for
 * multithreaded rendering, as long as the geometries themselves are not modified.
 * </p>
 */
public final class RegularGrid {

    /**
     * Configuration object for {@link RegularGrid}.
     * <p>
     * The configuration is immutable to keep the grid predictable and thread-safe.
     * </p>
     */
    public static final class Config {

        /**
         * Default grid configuration.
         */
        public static final Config DEFAULT = new Config(3.0, 1, 128);

        /**
         * Density multiplier used for automatic grid resolution calculation.
         * <p>
         * Higher values create more voxels.
         * </p>
         */
        public final double density;

        /**
         * Minimum number of voxels allowed on each axis.
         */
        public final int minResolution;

        /**
         * Maximum number of voxels allowed on each axis.
         */
        public final int maxResolution;

        /**
         * Padding used when the scene AABB is flat on one axis.
         * <p>
         * This avoids zero voxel size on scenes made only of flat triangles
         * or polygons.
         * </p>
         */
        public final double flatPadding;

        /**
         * Constructs a configuration with default flat padding.
         *
         * @param density       density multiplier, must be positive
         * @param minResolution minimum resolution, must be positive
         * @param maxResolution maximum resolution, must be at least minResolution
         */
        public Config(double density, int minResolution, int maxResolution) {
            this(density, minResolution, maxResolution, 1e-6);
        }

        /**
         * Constructs a configuration with explicit flat padding.
         *
         * @param density       density multiplier, must be positive
         * @param minResolution minimum resolution, must be positive
         * @param maxResolution maximum resolution, must be at least minResolution
         * @param flatPadding   padding for flat scene AABBs, must be positive
         */
        public Config(double density, int minResolution, int maxResolution, double flatPadding) {
            if (density <= 0) {
                throw new IllegalArgumentException("density must be positive");
            }
            if (minResolution <= 0) {
                throw new IllegalArgumentException("minResolution must be positive");
            }
            if (maxResolution < minResolution) {
                throw new IllegalArgumentException("maxResolution must be >= minResolution");
            }
            if (flatPadding <= 0) {
                throw new IllegalArgumentException("flatPadding must be positive");
            }

            this.density = density;
            this.minResolution = minResolution;
            this.maxResolution = maxResolution;
            this.flatPadding = flatPadding;
        }
    }

    /**
     * Scene AABB after flat-axis padding.
     * <p>
     * This is the box actually used by the grid.
     * It is null when the scene has no finite geometries.
     * </p>
     */
    private final AABB sceneAABB;

    /**
     * Number of voxels on the X axis.
     */
    private final int nx;

    /**
     * Number of voxels on the Y axis.
     */
    private final int ny;

    /**
     * Number of voxels on the Z axis.
     */
    private final int nz;

    /**
     * Size of one voxel on the X axis.
     */
    private final double vx;

    /**
     * Size of one voxel on the Y axis.
     */
    private final double vy;

    /**
     * Size of one voxel on the Z axis.
     */
    private final double vz;

    /**
     * Sparse voxel storage.
     * <p>
     * Only non-empty voxels are stored.
     * The key is a flattened 3D voxel index.
     * </p>
     */
    private final Map<Long, List<Intersectable>> voxels = new HashMap<>();

    /**
     * Geometries without finite AABB.
     * <p>
     * For example: Plane and Tube.
     * These geometries are not inserted into the grid and are always tested normally.
     * </p>
     */
    private final List<Intersectable> infiniteGeometries = new ArrayList<>();

    /**
     * Constructs a Regular Grid from a list of geometries.
     *
     * @param geometries geometries to accelerate
     * @param config     grid configuration
     */
    public RegularGrid(List<Intersectable> geometries, Config config) {
        if (geometries == null) {
            throw new IllegalArgumentException("geometries must not be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }

        List<BoundedGeometry> finiteGeometries = new ArrayList<>();
        AABB globalAABB = collectGeometries(geometries, finiteGeometries);

        if (globalAABB == null || finiteGeometries.isEmpty()) {
            this.sceneAABB = null;
            this.nx = 0;
            this.ny = 0;
            this.nz = 0;
            this.vx = 0;
            this.vy = 0;
            this.vz = 0;
            return;
        }

        this.sceneAABB = globalAABB.paddedIfFlat(config.flatPadding);

        double sceneSizeX = sceneAABB.max.x() - sceneAABB.min.x();
        double sceneSizeY = sceneAABB.max.y() - sceneAABB.min.y();
        double sceneSizeZ = sceneAABB.max.z() - sceneAABB.min.z();

        int geometryCount = finiteGeometries.size();
        double sceneVolume = sceneSizeX * sceneSizeY * sceneSizeZ;
        double scale = Math.cbrt(config.density * geometryCount / Math.max(sceneVolume, 1e-9));

        this.nx = calculateResolution(sceneSizeX, scale, config);
        this.ny = calculateResolution(sceneSizeY, scale, config);
        this.nz = calculateResolution(sceneSizeZ, scale, config);

        this.vx = sceneSizeX / nx;
        this.vy = sceneSizeY / ny;
        this.vz = sceneSizeZ / nz;

        assignGeometriesToVoxels(finiteGeometries);
    }

    /**
     * Collects finite and infinite geometries.
     * <p>
     * Finite geometries are added to the provided list together with their AABB.
     * Infinite geometries are stored in {@link #infiniteGeometries}.
     * </p>
     *
     * @param geometries       geometries to inspect
     * @param finiteGeometries output list for finite geometries
     * @return global AABB of all finite geometries, or null if none exist
     */
    private AABB collectGeometries(List<Intersectable> geometries, List<BoundedGeometry> finiteGeometries) {
        AABB globalAABB = null;

        for (Intersectable geometry : geometries) {
            if (!(geometry instanceof Geometry concreteGeometry)) {
                infiniteGeometries.add(geometry);
                continue;
            }

            AABB aabb = concreteGeometry.getAABB();

            if (aabb == null) {
                infiniteGeometries.add(geometry);
            } else {
                finiteGeometries.add(new BoundedGeometry(geometry, aabb));
                globalAABB = AABB.union(globalAABB, aabb);
            }
        }

        return globalAABB;
    }

    /**
     * Calculates the number of voxels on one axis.
     *
     * @param axisSize size of the scene on this axis
     * @param scale    automatic scale factor
     * @param config   grid configuration
     * @return clamped voxel count for this axis
     */
    private static int calculateResolution(double axisSize, double scale, Config config) {
        int automaticResolution = (int) Math.ceil(axisSize * scale);
        int atLeastMin = Math.max(config.minResolution, automaticResolution);
        return Math.min(config.maxResolution, Math.max(1, atLeastMin));
    }

    /**
     * Assigns all finite geometries to the voxels overlapped by their AABB.
     *
     * @param finiteGeometries finite geometries with their bounding boxes
     */
    private void assignGeometriesToVoxels(List<BoundedGeometry> finiteGeometries) {
        for (BoundedGeometry boundedGeometry : finiteGeometries) {
            int x0 = indexX(boundedGeometry.aabb.min.x());
            int x1 = indexX(boundedGeometry.aabb.max.x());
            int y0 = indexY(boundedGeometry.aabb.min.y());
            int y1 = indexY(boundedGeometry.aabb.max.y());
            int z0 = indexZ(boundedGeometry.aabb.min.z());
            int z1 = indexZ(boundedGeometry.aabb.max.z());

            for (int x = x0; x <= x1; x++) {
                for (int y = y0; y <= y1; y++) {
                    for (int z = z0; z <= z1; z++) {
                        addGeometryToVoxel(x, y, z, boundedGeometry.geometry);
                    }
                }
            }
        }
    }

    /**
     * Adds one geometry to one voxel.
     *
     * @param x        voxel X index
     * @param y        voxel Y index
     * @param z        voxel Z index
     * @param geometry geometry to insert
     */
    private void addGeometryToVoxel(int x, int y, int z, Intersectable geometry) {
        voxels.computeIfAbsent(keyOf(x, y, z), ignored -> new ArrayList<>()).add(geometry);
    }

    /**
     * Converts an X coordinate into a voxel index.
     *
     * @param x coordinate on X axis
     * @return clamped voxel index
     */
    private int indexX(double x) {
        return clamp((int) Math.floor((x - sceneAABB.min.x()) / vx), 0, nx - 1);
    }

    /**
     * Converts a Y coordinate into a voxel index.
     *
     * @param y coordinate on Y axis
     * @return clamped voxel index
     */
    private int indexY(double y) {
        return clamp((int) Math.floor((y - sceneAABB.min.y()) / vy), 0, ny - 1);
    }

    /**
     * Converts a Z coordinate into a voxel index.
     *
     * @param z coordinate on Z axis
     * @return clamped voxel index
     */
    private int indexZ(double z) {
        return clamp((int) Math.floor((z - sceneAABB.min.z()) / vz), 0, nz - 1);
    }

    /**
     * Converts a 3D voxel index into one unique long key.
     * <p>
     * This is the same idea as flattening a 3D array into a 1D array.
     * </p>
     *
     * @param x voxel X index
     * @param y voxel Y index
     * @param z voxel Z index
     * @return unique key for this voxel
     */
    private long keyOf(int x, int y, int z) {
        return ((long) x * ny + y) * nz + z;
    }

    /**
     * Clamps an integer value into a given range.
     *
     * @param value value to clamp
     * @param min   minimal allowed value
     * @param max   maximal allowed value
     * @return clamped value
     */
    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Finds intersections between a ray and the scene using Regular Grid traversal.
     * <p>
     * The grid is used only to reduce the number of geometries tested.
     * Exact intersection calculations are still performed by each geometry.
     * </p>
     *
     * @param ray ray to test
     * @return list of intersections, or null if no intersection exists
     */
    public List<Intersectable.Intersection> calcIntersections(Ray ray) {
        List<Intersectable.Intersection> result = new ArrayList<>();

        addInfiniteGeometryIntersections(ray, result);

        if (sceneAABB == null) {
            return result.isEmpty() ? null : result;
        }

        double[] tRange = sceneAABB.intersect(ray);

        if (tRange == null) {
            return result.isEmpty() ? null : result;
        }

        traverseGrid(ray, tRange[0], tRange[1], result);

        return result.isEmpty() ? null : result;
    }

    /**
     * Adds intersections with infinite geometries.
     *
     * @param ray    ray to test
     * @param result output intersection list
     */
    private void addInfiniteGeometryIntersections(Ray ray, List<Intersectable.Intersection> result) {
        for (Intersectable geometry : infiniteGeometries) {
            List<Intersectable.Intersection> intersections = geometry.calcIntersections(ray);

            if (intersections != null) {
                result.addAll(intersections);
            }
        }
    }

    /**
     * Traverses the grid using the 3DDDA algorithm.
     *
     * @param ray    ray to traverse
     * @param tEnter ray parameter where the ray enters the scene AABB
     * @param tExit  ray parameter where the ray exits the scene AABB
     * @param result output intersection list
     */
    private void traverseGrid(Ray ray, double tEnter, double tExit, List<Intersectable.Intersection> result) {
        double tStart = Math.max(tEnter, 0.0);
        Point startPoint = ray.getPoint(tStart);

        int x = indexX(startPoint.x());
        int y = indexY(startPoint.y());
        int z = indexZ(startPoint.z());

        double dx = ray.direction().x();
        double dy = ray.direction().y();
        double dz = ray.direction().z();

        int stepX = step(dx);
        int stepY = step(dy);
        int stepZ = step(dz);

        double tMaxX = firstBoundaryT(ray.origin().x(), dx, sceneAABB.min.x(), x, vx, stepX);
        double tMaxY = firstBoundaryT(ray.origin().y(), dy, sceneAABB.min.y(), y, vy, stepY);
        double tMaxZ = firstBoundaryT(ray.origin().z(), dz, sceneAABB.min.z(), z, vz, stepZ);

        double tDeltaX = deltaT(dx, vx);
        double tDeltaY = deltaT(dy, vy);
        double tDeltaZ = deltaT(dz, vz);

        Set<Intersectable> testedGeometries = Collections.newSetFromMap(new IdentityHashMap<>());

        while (isInsideGrid(x, y, z) && tStart <= tExit) {
            addVoxelIntersections(ray, x, y, z, testedGeometries, result);

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    tStart = tMaxX;
                    tMaxX += tDeltaX;
                } else {
                    z += stepZ;
                    tStart = tMaxZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    y += stepY;
                    tStart = tMaxY;
                    tMaxY += tDeltaY;
                } else {
                    z += stepZ;
                    tStart = tMaxZ;
                    tMaxZ += tDeltaZ;
                }
            }
        }
    }

    /**
     * Returns the traversal step for one axis.
     *
     * @param direction ray direction component on this axis
     * @return 1, -1, or 0
     */
    private static int step(double direction) {
        return direction > 0 ? 1 : direction < 0 ? -1 : 0;
    }

    /**
     * Calculates the ray parameter of the first voxel boundary on one axis.
     *
     * @param originComponent ray origin component on this axis
     * @param direction       ray direction component on this axis
     * @param gridMin         minimal scene coordinate on this axis
     * @param index           current voxel index on this axis
     * @param voxelSize       voxel size on this axis
     * @param step            traversal step on this axis
     * @return ray parameter of the next voxel boundary
     */
    private static double firstBoundaryT(
            double originComponent,
            double direction,
            double gridMin,
            int index,
            double voxelSize,
            int step
    ) {
        if (step == 0) {
            return Double.POSITIVE_INFINITY;
        }

        double boundary = gridMin + (index + (step > 0 ? 1 : 0)) * voxelSize;
        return alignZero((boundary - originComponent) / direction);
    }

    /**
     * Calculates the ray parameter distance between two voxel boundaries on one axis.
     *
     * @param direction ray direction component on this axis
     * @param voxelSize voxel size on this axis
     * @return delta t for this axis
     */
    private static double deltaT(double direction, double voxelSize) {
        if (step(direction) == 0) {
            return Double.POSITIVE_INFINITY;
        }

        return Math.abs(voxelSize / direction);
    }

    /**
     * Checks whether a voxel index is inside the grid.
     *
     * @param x voxel X index
     * @param y voxel Y index
     * @param z voxel Z index
     * @return true if the voxel is inside the grid
     */
    private boolean isInsideGrid(int x, int y, int z) {
        return x >= 0 && x < nx
                && y >= 0 && y < ny
                && z >= 0 && z < nz;
    }

    /**
     * Adds intersections for geometries stored in one voxel.
     *
     * @param ray              ray to test
     * @param x                voxel X index
     * @param y                voxel Y index
     * @param z                voxel Z index
     * @param testedGeometries geometries already tested for this ray
     * @param result           output intersection list
     */
    private void addVoxelIntersections(
            Ray ray,
            int x,
            int y,
            int z,
            Set<Intersectable> testedGeometries,
            List<Intersectable.Intersection> result
    ) {
        List<Intersectable> geometries = voxels.get(keyOf(x, y, z));

        if (geometries == null) {
            return;
        }

        for (Intersectable geometry : geometries) {
            if (!testedGeometries.add(geometry)) {
                continue;
            }

            List<Intersectable.Intersection> intersections = geometry.calcIntersections(ray);

            if (intersections != null) {
                result.addAll(intersections);
            }
        }
    }

    /**
     * Helper object storing a geometry together with its finite AABB.
     */
    private static final class BoundedGeometry {

        /**
         * Geometry stored in the grid.
         */
        private final Intersectable geometry;

        /**
         * AABB of the geometry.
         */
        private final AABB aabb;

        /**
         * Constructs a bounded geometry pair.
         *
         * @param geometry geometry
         * @param aabb     geometry AABB
         */
        private BoundedGeometry(Intersectable geometry, AABB aabb) {
            this.geometry = geometry;
            this.aabb = aabb;
        }
    }
}