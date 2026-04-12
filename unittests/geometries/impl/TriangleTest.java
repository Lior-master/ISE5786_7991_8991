package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for class {@link Triangle}.
 * The tests verify:
 * <ul>
 * <li>{@link Triangle#getNormal(Point)}</li>
 * <li>{@link Triangle#findIntersections(Ray)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class TriangleTests {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    TriangleTests() { /* to satisfy JavaDoc generator */ }

    /**
     * First vertex of the triangle
     */
    private static final Point P1 = new Point(1, 0, 0);
    /**
     * Second vertex of the triangle
     */
    private static final Point P2 = new Point(0, 1, 0);
    /**
     * Third vertex of the triangle
     */
    private static final Point P3 = new Point(0, 0, 1);

    /**
     * A point inside the triangle, on its plane
     */
    private static final Point POINT_INSIDE = new Point(0.25, 0.25, 0.5);

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for unexpected exception
     */
    private static final String ERROR_EXCEPTION = "ERROR: getNormal() threw unexpected exception";
    /**
     * Error message for wrong triangle normal
     */
    private static final String ERROR_NORMAL = "ERROR: Triangle normal is wrong";

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     * Verifies that the returned normal vector is unit length and orthogonal
     * to all triangle edges.
     */
    @Test
    void testGetNormal() {
        Triangle triangle = new Triangle(P1, P2, P3);

        // ============ Equivalence Partitions Tests ==============

        // EP01: getNormal() at a regular point on the triangle
        assertDoesNotThrow(() -> triangle.getNormal(POINT_INSIDE), ERROR_EXCEPTION);

        Vector result = triangle.getNormal(POINT_INSIDE);

        // Ensure |n| = 1
        assertEquals(1d, result.length(), DELTA, ERROR_NORMAL);

        // Ensure normal is orthogonal to triangle edges
        Vector edge1 = P2.subtract(P1);
        Vector edge2 = P3.subtract(P2);
        Vector edge3 = P1.subtract(P3);

        assertEquals(0d, result.dotProduct(edge1), DELTA, ERROR_NORMAL);
        assertEquals(0d, result.dotProduct(edge2), DELTA, ERROR_NORMAL);
        assertEquals(0d, result.dotProduct(edge3), DELTA, ERROR_NORMAL);
    }

    /**
     * Test method for {@link Triangle#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(
                new Point(0, 0, 1),
                new Point(2, 0, 1),
                new Point(0, 2, 1));

        // ============ Equivalence Partitions Tests ==============

        // TC01 (EP): Ray intersects inside the triangle
        assertEquals(
                List.of(new Point(0.5, 0.5, 1)),
                triangle.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 2))),
                "Ray should intersect inside the triangle");

        // TC02 (EP): Ray intersects the plane outside the triangle against an edge
        assertNull(
                triangle.findIntersections(new Ray(new Point(0, 0, 0), new Vector(3, 3, 2))),
                "Ray intersecting outside against an edge must not intersect the triangle");

        // TC03 (EP): Ray intersects the plane outside the triangle against a vertex
        assertNull(
                triangle.findIntersections(new Ray(new Point(0, 0, 0), new Vector(-1, 1, 1))),
                "Ray intersecting outside against a vertex must not intersect the triangle");

        // =============== Boundary Values Tests ==================

        // TC11 (BV): Ray intersects exactly on an edge
        assertNull(
                triangle.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 1))),
                "Ray intersecting on an edge must not intersect the triangle");

        // TC12 (BV): Ray intersects exactly at a vertex
        assertNull(
                triangle.findIntersections(new Ray(new Point(0, 0, 0), new Vector(2, 0, 1))),
                "Ray intersecting at a vertex must not intersect the triangle");

        // TC13 (BV): Ray intersects on an edge continuation
        assertNull(
                triangle.findIntersections(new Ray(new Point(0, 0, 0), new Vector(3, 0, 1))),
                "Ray intersecting on an edge continuation must not intersect the triangle");
    }
}