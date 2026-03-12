package geometries.impl;

import primitives.Ray;

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
    private final Ray _axis;

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
}
