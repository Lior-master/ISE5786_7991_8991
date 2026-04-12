package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> planeIntersections = _plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();

        Point p1 = _vertices.get(0);
        Point p2 = _vertices.get(1);
        Point p3 = _vertices.get(2);

        try {
            Vector v1 = p1.subtract(p0);
            Vector v2 = p2.subtract(p0);
            Vector v3 = p3.subtract(p0);

            double s1 = alignZero(v.dotProduct(v1.crossProduct(v2)));
            double s2 = alignZero(v.dotProduct(v2.crossProduct(v3)));
            double s3 = alignZero(v.dotProduct(v3.crossProduct(v1)));

            // on edge / on vertex / on edge continuation
            if (isZero(s1) || isZero(s2) || isZero(s3)) return null;

            boolean allPositive = s1 > 0 && s2 > 0 && s3 > 0;
            boolean allNegative = s1 < 0 && s2 < 0 && s3 < 0;

            return allPositive || allNegative ? planeIntersections : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}