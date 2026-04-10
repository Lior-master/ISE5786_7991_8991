package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link Plane}.
 * The tests verify:
 * <ul>
 * <li>{@link Plane#getNormal(Point)}</li>
 * <li>{@link Plane#Plane(Point, Vector)}</li>
 * <li>{@link Plane#Plane(Point, Point, Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PlaneTest {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    PlaneTest() { /* to satisfy JavaDoc generator */ }

    /**
     * A reference point on the test plane.
     */
    private static final Point Q0 = new Point(1, 2, 3);

    /**
     * A second point that lies on the same plane z=3.
     */
    private static final Point POINT_ON_PLANE = new Point(5, -1, 3);

    /**
     * A non-unit normal vector (length = 5).
     */
    private static final Vector NORMAL_NON_UNIT = new Vector(0, 3, 4);

    /**
     * Expected normalized normal vector.
     */
    private static final Vector NORMAL_UNIT = new Vector(0, 0.6, 0.8);

    /**
     * Delta value for floating-point comparisons.
     */
    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============
        Plane plane = new Plane(Q0, Vector.AXIS_Z); // plane z = 3

        // TC01 (EP): Point on plane but different from reference point
        assertEquals(Vector.AXIS_Z, plane.getNormal(POINT_ON_PLANE),
                "getNormal() returned wrong normal for a non-reference point on the plane");

        // =============== Boundary Values Tests ==================

        // TC11 (BV): Reference point itself
        assertEquals(Vector.AXIS_Z, plane.getNormal(Q0),
                "getNormal() returned wrong normal for the plane reference point");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Vector)}.
     */
    @Test
    void testConstructorPointVector() {

        // ============ Equivalence Partitions Tests ==============

        // TC01 (EP): Normal vector is normalized by constructor
        Plane plane = new Plane(Q0, NORMAL_NON_UNIT);
        assertEquals(NORMAL_UNIT, plane.getNormal(Q0),
                "Plane(Point, Vector) did not normalize the normal vector");
        assertEquals(1d, plane.getNormal(Q0).length(), DELTA,
                "Plane(Point, Vector) returned a non-unit normal");
    }

    @Test
    void testConstructorThreePoints() {

        // ============ Equivalence Partitions Tests ==============

        // TC01 (EP): Three distinct non-collinear points
        assertDoesNotThrow(() -> new Plane(
                        new Point(0, 0, 1),
                        new Point(1, 0, 1),
                        new Point(0, 1, 1)),
                "Failed constructing plane from 3 distinct non-collinear points");

        // =============== Boundary Values Tests ==================

        // TC11 (BV): First and second points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 1),
                        new Point(0, 0, 1),
                        new Point(0, 1, 1)),
                "Constructed a plane when points 1 and 2 are identical");

        // TC12 (BV): First and third points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 1),
                        new Point(1, 0, 1),
                        new Point(0, 0, 1)),
                "Constructed a plane when points 1 and 3 are identical");

        // TC13 (BV): Second and third points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 1),
                        new Point(1, 0, 1),
                        new Point(1, 0, 1)),
                "Constructed a plane when points 2 and 3 are identical");

        // TC14 (BV): All three points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(2, 2, 2),
                        new Point(2, 2, 2),
                        new Point(2, 2, 2)),
                "Constructed a plane with three identical points");

        // TC15 (BV): All three points are collinear
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                        new Point(0, 0, 0),
                        new Point(1, 1, 1),
                        new Point(2, 2, 2)),
                "Constructed a plane with three collinear points");
    }
}