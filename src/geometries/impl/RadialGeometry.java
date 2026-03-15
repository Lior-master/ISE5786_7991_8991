package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all radial (round) geometries in 3D space,
 * such as spheres, cylinders, and tubes.
 * Stores the radius and its squared value for optimized calculations.
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public abstract class RadialGeometry extends Geometry {
    /**
     * The radius of the radial geometry.
     */
    protected final double _radius;

    /**
     * The squared radius, stored for performance optimization.
     */
    protected final double _radiusSquared;

    /**
     * Constructs a RadialGeometry with the given radius.
     *
     * @param radius the radius of the geometry (must be positive)
     */
    RadialGeometry(double radius) {
        _radius = radius;
        _radiusSquared = radius * radius;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}