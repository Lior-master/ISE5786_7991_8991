package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a finite cylinder in 3D space.
 * A cylinder is defined by a central axis ray, a radius, and a finite height.
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public class Cylinder extends Tube {
    /**
     * The height of the cylinder.
     */
    private final double _height;

    /**
     * Constructs a cylinder with the given radius, axis ray, and height.
     *
     * @param _radius the radius of the cylinder
     * @param _axis   the axis ray of the cylinder
     * @param _height the height of the cylinder
     */
    public Cylinder(double _radius, Ray _axis, double _height) {
        super(_radius, _axis);
        this._height = _height;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector dir = _axis.direction();

        if (point.equals(p0)) {
            return dir.scale(-1);
        }

        Point topCenter = p0.add(dir.scale(_height));
        if (point.equals(topCenter)) {
            return dir;
        }

        double t = dir.dotProduct(point.subtract(p0));

        if (primitives.Util.isZero(t)) {
            return dir.scale(-1);
        }

        if (primitives.Util.isZero(t - _height)) {
            return dir;
        }

        Point o = p0.add(dir.scale(t));
        return point.subtract(o).normalize();
    }
}
