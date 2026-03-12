package geometries.impl;

import primitives.Ray;

public class Cylinder extends Tube {
    private final double _height;

    public Cylinder(Ray _axis, double _radius, double _height) {
        super(_axis, _radius);
        this._height = _height;
    }
}
