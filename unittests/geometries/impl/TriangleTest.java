package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link Triangle}.
 * The tests verify:
 * <ul>
 * <li>{@link Triangle#getNormal(Point)}</li>
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
}