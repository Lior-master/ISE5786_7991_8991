package geometries.api;

import java.util.List;

import primitives.Point;
import primitives.Ray;

/**
 * Abstract base class for all geometric objects that can be intersected by a ray.
 */
public abstract class Intersectable {
    /**
     * Finds all intersection points between the geometry and the given ray.
     *
     * @param ray the ray to intersect with
     * @return list of intersection points, or {@code null} if there are no intersections
     */
    public abstract List<Point> findIntersections(Ray ray);
}