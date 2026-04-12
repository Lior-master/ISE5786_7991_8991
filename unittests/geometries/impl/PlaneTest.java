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

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)}.
     */
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

    /**
     * Test method for {@link Plane#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {

        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============

        // TC01 (EP): Ray is neither orthogonal nor parallel to the plane and intersects it
        assertEquals(
                List.of(new Point(1, 1, 1)),
                plane.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1))),
                "Ray should intersect the plane");

        // TC02 (EP): Ray is neither orthogonal nor parallel to the plane and does not intersect it
        assertNull(
                plane.findIntersections(new Ray(new Point(2, 2, 2), new Vector(1, 1, 1))),
                "Ray should not intersect the plane");

        // =============== Boundary Values Tests ==================

        // TC11 (BV): Ray is parallel to the plane and included in the plane
        assertNull(
                plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0))),
                "Ray included in the plane should not have intersections");

        // TC12 (BV): Ray is parallel to the plane and not included in the plane
        assertNull(
                plane.findIntersections(new Ray(new Point(1, 1, 2), new Vector(1, 0, 0))),
                "Ray parallel to the plane and outside it should not intersect");

        // TC13 (BV): Ray is orthogonal to the plane and starts before the plane
        assertEquals(
                List.of(new Point(0, 0, 1)),
                plane.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))),
                "Orthogonal ray before the plane should intersect once");

        // TC14 (BV): Ray is orthogonal to the plane and starts in the plane
        assertNull(
                plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1))),
                "Orthogonal ray starting in the plane should not intersect");

        // TC15 (BV): Ray is orthogonal to the plane and starts after the plane
        assertNull(
                plane.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
                "Orthogonal ray starting after the plane should not intersect");

        // TC16 (BV): Ray is neither orthogonal nor parallel and begins on the plane
        assertNull(
                plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
                "Ray starting on the plane should not intersect");

        // TC17 (BV): Ray is neither orthogonal nor parallel and begins at the reference point of the plane
        assertNull(
                plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 2))),
                "Ray starting at the plane reference point should not intersect");
    }
}