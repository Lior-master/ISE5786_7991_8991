package geometries.impl;

import java.util.List;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents an infinite plane in 3D space defined by a point and a normal vector.
 * The plane can be constructed from three non-collinear points or from a point and a normal vector.
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public class Plane extends Geometry {

    /**
     * A point on the plane.
     */
    private final Point _point;

    /**
     * The normal vector to the plane.
     */
    private final Vector _normal;

    /**
     * Constructs a plane from three non-collinear points.
     * The normal vector is computed using the cross product of two vectors formed by the points.
     *
     * @param p1 the first point on the plane
     * @param p2 the second point on the plane
     * @param p3 the third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;
        _normal = (p2.subtract(p1)).crossProduct(p3.subtract(p1)).normalize();
    }

    /**
     * Constructs a plane from a point and a normal vector.
     *
     * @param q      the point on the plane
     * @param normal the normal vector to the plane (will be normalized)
     */
    public Plane(Point q, Vector normal) {
        _point = q;
        _normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        if (_point.equals(p0)) return null;

        double nv = _normal.dotProduct(v);

        // no intersection – the ray is parallel to the plane
        if (isZero(nv)) return null;

        double t = alignZero(_normal.dotProduct(_point.subtract(p0)) / nv);

        // there is intersection only if it is in the direction of the ray
        return t <= 0 ? null : List.of(new Intersection(this, ray.getPoint(t)));
    }
}