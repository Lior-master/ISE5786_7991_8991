package geometries.impl;

import primitives.Ray;

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
}
