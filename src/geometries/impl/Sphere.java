package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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

        // Special case: ray starts at sphere center
        if (_center.equals(p0)) {
            return List.of(ray.getPoint(_radius));
        }

        Vector u = _center.subtract(p0);

        double tm = alignZero(v.dotProduct(u));
        double dSquared = alignZero(u.lengthSquared() - tm * tm);
        double rSquared = _radius * _radius;

        // No intersections: line misses sphere or is tangent
        if (dSquared >= rSquared || isZero(dSquared - rSquared)) {
            return null;
        }

        double th = Math.sqrt(rSquared - dSquared);
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 > 0 && t2 > 0) {
            Point p1 = ray.getPoint(t1);
            Point p2 = ray.getPoint(t2);
            return t1 < t2 ? List.of(p1, p2) : List.of(p2, p1);
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
