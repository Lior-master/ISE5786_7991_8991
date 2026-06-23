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
class TriangleTest {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    TriangleTest() { /* to satisfy JavaDoc generator */ }

    /**
     * First vertex of the triangle used in normal tests.
     */
    private static final Point P1 = new Point(1, 0, 0);

    /**
     * Second vertex of the triangle used in normal tests.
     */
    private static final Point P2 = new Point(0, 1, 0);

    /**
     * Third vertex of the triangle used in normal tests.
     */
    private static final Point P3 = new Point(0, 0, 1);

    /**
     * Point inside the triangle, on its plane, used in normal test.
     */
    private static final Point POINT_INSIDE = new Point(0.25, 0.25, 0.5);

    /**
     * Triangle used in getNormal test.
     */
    private static final Triangle TRIANGLE_NORMAL = new Triangle(P1, P2, P3);

    /**
     * Triangle used in findIntersections tests.
     */
    private static final Triangle TRIANGLE_INTERSECTIONS = new Triangle(
            new Point(0, 0, 1),
            new Point(2, 0, 1),
            new Point(0, 2, 1)
    );

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for unexpected exception.
     */
    private static final String ERROR_EXCEPTION = "Unexpected exception was thrown";

    /**
     * Error message for wrong triangle normal.
     */
    private static final String ERROR_NORMAL = "Wrong triangle normal";

    /**
     * Error message for wrong triangle intersection result.
     */
    private static final String ERROR_TRIANGLE_INTERSECTION = "Wrong triangle intersection result";

    /**
     * Test method for {@link Triangle#getNormal(Point)}.
     * Verifies that the returned normal vector is unit length and orthogonal
     * to all triangle edges.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: getNormal() at a regular point on the triangle
        assertDoesNotThrow(() -> TRIANGLE_NORMAL.getNormal(POINT_INSIDE), ERROR_EXCEPTION);

        Vector result = TRIANGLE_NORMAL.getNormal(POINT_INSIDE);

        assertEquals(1d, result.length(), DELTA, ERROR_NORMAL);

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
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects inside the triangle
        assertEquals(
                List.of(new Point(0.5, 0.5, 1)),
                TRIANGLE_INTERSECTIONS.findIntersections(new Ray(
                        new Point(0, 0, 0),
                        new Vector(1, 1, 2))),
                ERROR_TRIANGLE_INTERSECTION
        );

        // EP02: Ray intersects the plane outside the triangle against an edge
        assertNull(
                TRIANGLE_INTERSECTIONS.findIntersections(new Ray(
                        new Point(0, 0, 0),
                        new Vector(3, 3, 2))),
                ERROR_TRIANGLE_INTERSECTION
        );

        // EP03: Ray intersects the plane outside the triangle against a vertex
        assertNull(
                TRIANGLE_INTERSECTIONS.findIntersections(new Ray(
                        new Point(0, 0, 0),
                        new Vector(-1, 1, 1))),
                ERROR_TRIANGLE_INTERSECTION
        );

        // =============== Boundary Values Tests ==================

        // BV01: Ray intersects exactly on an edge
        assertNull(
                TRIANGLE_INTERSECTIONS.findIntersections(new Ray(
                        new Point(0, 0, 0),
                        new Vector(1, 0, 1))),
                ERROR_TRIANGLE_INTERSECTION
        );

        // BV02: Ray intersects exactly at a vertex
        assertNull(
                TRIANGLE_INTERSECTIONS.findIntersections(new Ray(
                        new Point(0, 0, 0),
                        new Vector(2, 0, 1))),
                ERROR_TRIANGLE_INTERSECTION
        );

        // BV03: Ray intersects on an edge continuation
        assertNull(
                TRIANGLE_INTERSECTIONS.findIntersections(new Ray(
                        new Point(0, 0, 0),
                        new Vector(3, 0, 1))),
                ERROR_TRIANGLE_INTERSECTION
        );
    }
}