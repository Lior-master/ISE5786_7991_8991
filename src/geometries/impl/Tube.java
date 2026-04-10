package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents an infinite tube in 3D space.
 * A tube is defined by a central axis ray and a constant radius.
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis ray of the tube.
     */
    protected final Ray _axis;

    /**
     * Constructs a tube with the given radius and axis ray.
     *
     * @param _radius the radius of the tube
     * @param _axis   the axis ray of the tube
     */
    public Tube(double _radius, Ray _axis) {
        super(_radius);
        this._axis = _axis;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector dir = _axis.direction();

        double t = dir.dotProduct(point.subtract(p0));

        Point o = primitives.Util.isZero(t) ? p0 : p0.add(dir.scale(t));

        return point.subtract(o).normalize();
    }
}
