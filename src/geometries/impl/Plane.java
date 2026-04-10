package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

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
}

