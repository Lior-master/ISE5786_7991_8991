package geometries.impl;

import primitives.Point;

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
}
