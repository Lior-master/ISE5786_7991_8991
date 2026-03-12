package geometries.impl;

import primitives.Ray;

public class Tube extends RadialGeometry {
    private final Ray _axis;

    public Tube(Ray _axis, double _radius) {
        this._axis = _axis;
        super(_radius);
    }
}
