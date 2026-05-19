package geometries.api;

import java.util.List;

import lighting.LightSource;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Base class for objects that can be intersected by a {@link Ray}.
 */
public abstract class Intersectable {

    /**
     * Default constructor for Javadoc tools.
     */
    public Intersectable() {/* to satisfy Javadoc generator */ }

    /**
     * Immutable intersection data: geometry, hit point, and material.
     */
    public static final class Intersection {

        /**
         * Geometry hit by the ray.
         */
        public final Geometry geometry;

        /**
         * Intersection point in 3D space.
         */
        public final Point point;

        /**
         * Material at the hit geometry.
         */
        public final Material material;

        /**
         * Normal vector at the intersection point
         */
        public Vector normal;

        /**
         * Direction of the incoming ray at the intersection point
         */
        public Vector v;

        /**
         * Scalar product between v and normal
         */
        public double vNormal;

        /**
         * Actual light source we study
         */
        public LightSource light;

        /**
         * Direction of the actual light
         */
        public Vector l;

        /**
         * Scalar product between l and normal
         */
        public double lNormal;

        /**
         * Creates an intersection record.
         *
         * @param geometry hit geometry
         * @param point    hit point
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            material = geometry == null ? new Material() : geometry.getMaterial();
        }

        /**
         * Returns a string representation of this intersection.
         *
         * @return intersection as text
         */
        @Override
        public String toString() {
            return "Intersection [geometry=" + geometry + ", point=" + point + ", material=" + material + "]";
        }

        /**
         * Compares this intersection with another object.
         *
         * @param o object to compare
         * @return {@code true} if geometry and point are equal
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return this.geometry.equals(((Intersection) o).geometry) && this.point.equals(((Intersection) o).point);
        }
    }

    /**
     * Finds intersection points of a ray with this object.
     *
     * @param ray input ray
     * @return list of intersection points, or {@code null} if none
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .toList();
    }

    /**
     * Internal intersection calculation implemented by concrete geometries.
     *
     * @param ray input ray
     * @return list of intersection records, or {@code null} if none
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Calculates full intersection records for a ray.
     *
     * @param ray input ray
     * @return list of intersections, or {@code null} if none
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }
}