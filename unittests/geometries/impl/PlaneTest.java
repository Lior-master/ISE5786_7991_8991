package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link Plane}.
 * The tests verify:
 * <ul>
 * <li>{@link Plane#getNormal(Point)}</li>
 * <li>{@link Plane#Plane(Point, Vector)}</li>
 * <li>{@link Plane#Plane(Point, Point, Point)}</li>
 * <li>{@link Plane#findIntersections(Ray)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PlaneTests {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    PlaneTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Reference point on the test plane z = 3.
     */
    private static final Point Q0 = new Point(1, 2, 3);

    /**
     * Another point on the same plane z = 3.
     */
    private static final Point POINT_ON_PLANE = new Point(5, -1, 3);

    /**
     * Non-unit normal vector used in constructor test.
     */
    private static final Vector NORMAL_NON_UNIT = new Vector(0, 3, 4);

    /**
     * Expected normalized normal vector.
     */
    private static final Vector NORMAL_UNIT = new Vector(0, 0.6, 0.8);

    /**
     * Plane z = 3 used in getNormal and constructor tests.
     */
    private static final Plane PLANE_Z3 = new Plane(Q0, Vector.AXIS_Z);

    /**
     * Plane z = 1 used in intersection tests.
     */
    private static final Plane PLANE_Z1 = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

    /**
     * Delta value for floating-point comparisons.
     */
    private static final double DELTA = 1e-10;

    /**
     * Error message for wrong normal.
     */
    private static final String ERROR_NORMAL = "Wrong plane normal";

    /**
     * Error message for wrong plane intersection result.
     */
    private static final String ERROR_PLANE_INTERSECTION = "Wrong plane intersection result";

    /**
     * Error message for unexpected exception.
     */
    private static final String ERROR_EXCEPTION = "Unexpected exception was thrown";

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Point on plane but different from reference point
        assertEquals(Vector.AXIS_Z, PLANE_Z3.getNormal(POINT_ON_PLANE), ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // BV01: Reference point itself
        assertEquals(Vector.AXIS_Z, PLANE_Z3.getNormal(Q0), ERROR_NORMAL);
    }

    /**
     * Test method for {@link Plane#Plane(Point, Vector)}.
     */
    @Test
    void testConstructorPointVector() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Constructor normalizes the given normal vector
        Plane plane = new Plane(Q0, NORMAL_NON_UNIT);
        assertEquals(NORMAL_UNIT, plane.getNormal(Q0), ERROR_NORMAL);
        assertEquals(1d, plane.getNormal(Q0).length(), DELTA, ERROR_NORMAL);
    }

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)}.
     */
    @Test
    void testConstructorThreePoints() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Three distinct non-collinear points
        assertDoesNotThrow(() -> new Plane(
                        new Point(0, 0, 1),
                        new Point(1, 0, 1),
                        new Point(0, 1, 1)),
                ERROR_EXCEPTION);

        // =============== Boundary Values Tests ==================

        // BV01: First and second points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 1),
                        new Point(0, 0, 1),
                        new Point(0, 1, 1)),
                ERROR_EXCEPTION);

        // BV02: First and third points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 1),
                        new Point(1, 0, 1),
                        new Point(0, 0, 1)),
                ERROR_EXCEPTION);

        // BV03: Second and third points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 1),
                        new Point(1, 0, 1),
                        new Point(1, 0, 1)),
                ERROR_EXCEPTION);

        // BV04: All three points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(2, 2, 2),
                        new Point(2, 2, 2),
                        new Point(2, 2, 2)),
                ERROR_EXCEPTION);

        // BV05: All three points are collinear
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 0),
                        new Point(1, 1, 1),
                        new Point(2, 2, 2)),
                ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Plane#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray is neither orthogonal nor parallel to the plane and intersects it
        assertEquals(
                List.of(new Point(1, 1, 1)),
                PLANE_Z1.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1))),
                ERROR_PLANE_INTERSECTION);

        // EP02: Ray is neither orthogonal nor parallel to the plane and does not intersect it
        assertNull(
                PLANE_Z1.findIntersections(new Ray(new Point(2, 2, 2), new Vector(1, 1, 1))),
                ERROR_PLANE_INTERSECTION);

        // =============== Boundary Values Tests ==================

        // BV01: Ray is parallel to the plane and included in the plane
        assertNull(
                PLANE_Z1.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0))),
                ERROR_PLANE_INTERSECTION);

        // BV02: Ray is parallel to the plane and not included in the plane
        assertNull(
                PLANE_Z1.findIntersections(new Ray(new Point(1, 1, 2), new Vector(1, 0, 0))),
                ERROR_PLANE_INTERSECTION);

        // BV03: Ray is orthogonal to the plane and starts before the plane
        assertEquals(
                List.of(new Point(0, 0, 1)),
                PLANE_Z1.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))),
                ERROR_PLANE_INTERSECTION);

        // BV04: Ray is orthogonal to the plane and starts in the plane
        assertNull(
                PLANE_Z1.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1))),
                ERROR_PLANE_INTERSECTION);

        // BV05: Ray is orthogonal to the plane and starts after the plane
        assertNull(
                PLANE_Z1.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
                ERROR_PLANE_INTERSECTION);

        // BV06: Ray is neither orthogonal nor parallel and begins on the plane
        assertNull(
                PLANE_Z1.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
                ERROR_PLANE_INTERSECTION);

        // BV07: Ray is neither orthogonal nor parallel and begins at the reference point of the plane
        assertNull(
                PLANE_Z1.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 2))),
                ERROR_PLANE_INTERSECTION);
    }
}