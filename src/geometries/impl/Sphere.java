package geometries.impl;

import primitives.Point;

public class Sphere extends RadialGeometry {
    private final Point _center;

    public Sphere(Point center, double radius) {
        this._center = center;
        super(radius);
    }
}
