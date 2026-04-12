package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;

/**
 * Represents a sphere in 3D space.
 * A sphere is defined by its center point and radius.
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere.
     */
    private final Point _center;

    /**
     * Constructs a sphere with the given center point and radius.
     *
     * @param center  the center point of the sphere
     * @param _radius the radius of the sphere
     */
    public Sphere(Point center, double _radius) {
        super(_radius);
        this._center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Special case: ray starts at the sphere center
        if (_center.equals(p0)) {
            return List.of(ray.getPoint(_radius));
        }

        Vector u = _center.subtract(p0);

        double tm = alignZero(v.dotProduct(u));
        double dSquared = alignZero(u.lengthSquared() - tm * tm);

        // No intersections: the ray misses the sphere or is tangent to it
        if (alignZero(dSquared - _radiusSquared) >= 0) {
            return null;
        }

        double th = Math.sqrt(_radiusSquared - dSquared);
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 > 0 && t2 > 0) {
            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        }

        if (t1 > 0) {
            return List.of(ray.getPoint(t1));
        }

        if (t2 > 0) {
            return List.of(ray.getPoint(t2));
        }

        return null;
    }
}
