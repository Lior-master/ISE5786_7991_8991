package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for class {@link Sphere}.
 * The tests verify:
 * <ul>
 * <li>{@link Sphere#getNormal(Point)}</li>
 * <li>{@link Sphere#findIntersections(Ray)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class SphereTests {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    SphereTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Point (1,2,3) - center of tested sphere
     */
    private static final Point CENTER = new Point(1, 2, 3);
    /**
     * Radius of tested sphere
     */
    private static final double RADIUS = 2d;

    /**
     * Point on sphere surface used in normal test
     */
    private static final Point POINT_ON_SURFACE = new Point(3, 2, 3);
    /**
     * Expected normal at the tested point
     */
    private static final Vector EXPECTED_NORMAL = new Vector(1, 0, 0);

    /**
     * Sphere used in tests
     */
    private static final Sphere SPHERE = new Sphere(CENTER, RADIUS);

    /**
     * Error message for wrong normal
     */
    private static final String ERROR_NORMAL = "Wrong sphere normal";
    /**
     * Error message for wrong sphere intersection
     */
    private static final String ERROR_SPHERE_INTERSECTION = "Wrong sphere intersection result";
    /**
     * Error message for unexpected exception
     */
    private static final String ERROR_EXCEPTION = "Unexpected exception was thrown";

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Point on sphere surface
        assertDoesNotThrow(() -> SPHERE.getNormal(POINT_ON_SURFACE), ERROR_EXCEPTION);
        Vector result = SPHERE.getNormal(POINT_ON_SURFACE);
        assertEquals(EXPECTED_NORMAL, result, ERROR_NORMAL);
        assertEquals(1, result.length(), 1e-10, ERROR_NORMAL);
    }

    /**
     * Test method for {@link Sphere#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray's line is outside the sphere (0 points)
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(-2, 2, 3),
                        new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // EP02: Ray starts before and crosses the sphere (2 points)
        Point intersection1 = new Point(-0.9703293088490064, 2.343223563716998, 3);
        Point intersection2 = new Point(2.370329308849005, 3.456776436283002, 3);
        List<Point> expected1 = List.of(intersection1, intersection2);
        
        final var result1 = SPHERE.findIntersections(new Ray(
                new Point(-2, 2, 3),
                new Vector(3, 1, 0)));
        assertNotNull(result1, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result1.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(expected1, result1, ERROR_SPHERE_INTERSECTION);

        // EP03: Ray starts inside the sphere (1 point)
        Point intersection3 = new Point(3, 2, 3);
        List<Point> expected2 = List.of(intersection3);

        final var result2 = SPHERE.findIntersections(new Ray(
                new Point(2, 2, 3),
                new Vector(1, 0, 0)));
        assertNotNull(result2, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result2.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(expected2, result2, ERROR_SPHERE_INTERSECTION);

        // EP04: Ray starts after the sphere (0 points)
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(4, 2, 3),
                        new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)

        // BV11: Ray starts at sphere and goes inside (1 point)
        Point intersection4 = new Point(1 + Math.sqrt(3), 3, 3);
        List<Point> expected3 = List.of(intersection4);

        final var result3 = SPHERE.findIntersections(new Ray(
                new Point(1 - Math.sqrt(3), 3, 3),
                new Vector(1, 0, 0)));
        assertNotNull(result3, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result3.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(expected3, result3, ERROR_SPHERE_INTERSECTION);

        // BV12: Ray starts at sphere and goes outside (0 points)
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(1 - Math.sqrt(3), 3, 3),
                        new Vector(-1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // **** Group 2: Ray's line goes through the center

        // BV21: Ray starts before the sphere (2 points)
        List<Point> expected4 = List.of(
                new Point(-1, 2, 3),
                new Point(3, 2, 3));

        final var result4 = SPHERE.findIntersections(new Ray(
                new Point(-2, 2, 3),
                new Vector(1, 0, 0)));
        assertNotNull(result4, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result4.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(expected4, result4, ERROR_SPHERE_INTERSECTION);

        // BV22: Ray starts at sphere and goes inside (1 point)
        List<Point> expected5 = List.of(new Point(3, 2, 3));

        final var result5 = SPHERE.findIntersections(new Ray(
                new Point(-1, 2, 3),
                new Vector(1, 0, 0)));
        assertNotNull(result5, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result5.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(expected5, result5, ERROR_SPHERE_INTERSECTION);

        // BV23: Ray starts inside (1 point)
        List<Point> expected6 = List.of(new Point(3, 2, 3));

        final var result6 = SPHERE.findIntersections(new Ray(
                new Point(0, 2, 3),
                new Vector(1, 0, 0)));
        assertNotNull(result6, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result6.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(expected6, result6, ERROR_SPHERE_INTERSECTION);

        // BV24: Ray starts at the center (1 point)
        List<Point> expected7 = List.of(new Point(3, 2, 3));

        final var result7 = SPHERE.findIntersections(new Ray(
                new Point(1, 2, 3),
                new Vector(1, 0, 0)));
        assertNotNull(result7, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result7.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(expected7, result7, ERROR_SPHERE_INTERSECTION);

        // BV25: Ray starts at sphere and goes outside (0 points)
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(3, 2, 3),
                        new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV26: Ray starts after sphere (0 points)
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(4, 2, 3),
                        new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)

        // BV31: Ray starts before the tangent point
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(3, 0, 3),
                        new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV32: Ray starts at the tangent point
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(3, 2, 3),
                        new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV33: Ray starts after the tangent point
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(3, 4, 3),
                        new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // **** Group 4: Special cases

        // BV41: Ray's line is outside sphere, and ray is orthogonal to the segment P0O (0 points)
        assertNull(SPHERE.findIntersections(new Ray(
                        new Point(4, 2, 3),
                        new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV42: Ray starts inside, and ray is orthogonal to the segment P0O (1 point)
        Point intersection5 = new Point(2, 2 + Math.sqrt(3), 3);
        List<Point> expected8 = List.of(intersection5);

        final var result8 = SPHERE.findIntersections(new Ray(
                new Point(2, 2, 3),
                new Vector(0, 1, 0)));
        assertNotNull(result8, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, result8.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(expected8, result8, ERROR_SPHERE_INTERSECTION);
    }
}