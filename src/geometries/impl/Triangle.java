package geometries.impl;

import primitives.Point;

public class Triangle extends Polygon {
    /**
     * Constructs a triangle from three vertices.
     *
     * @param v1 the first vertex of the triangle
     * @param v2 the second vertex of the triangle
     * @param v3 the third vertex of the triangle
     * @throws IllegalArgumentException if the vertices do not form a valid triangle
     */
    public Triangle(Point v1, Point v2, Point v3) {
        super(v1, v2, v3);
    }
}
