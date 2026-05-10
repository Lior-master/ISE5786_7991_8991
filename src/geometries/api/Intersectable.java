package geometries.api;

import java.util.List;

import primitives.Point;
import primitives.Ray;

/**
 * Abstract base class for all geometric objects that can be intersected by a ray.
 */
public abstract class Intersectable {
    public static final class Intersection {
        public final Geometry geometry;
        public final Point point;

        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public String toString() {
            return "Intersection [geometry=" + geometry + ", point=" + point + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return this.geometry.equals(((Intersection) o).geometry) && this.point.equals(((Intersection) o).point);
        }
    }

    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .toList();
    }

    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }
}