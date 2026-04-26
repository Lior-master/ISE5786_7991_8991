package geometries.impl;

import primitives.Point;

/**
 * Represents a triangle in 3D space defined by three vertices. This class extends the {@link Polygon} class, which provides common functionality for polygons, such as storing vertices and defining a plane. The triangle is a specific type of polygon with exactly three vertices. The class includes a constructor to initialize the triangle with its vertices and an implementation of the {@code findIntersections} method to determine if a given ray intersects the triangle, and if so, where the intersection points are located.
 */
public class Triangle extends Polygon {
    /**
     * Constructs a triangle from three vertices.
     *
     * @param p1 the first vertex of the triangle
     * @param p2 the second vertex of the triangle
     * @param p3 the third vertex of the triangle
     * @throws IllegalArgumentException if the vertices do not form a valid triangle
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }
}