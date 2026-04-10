package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link Sphere}.
 * The tests verify:
 * <ul>
 * <li>{@link Sphere#getNormal(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class SphereTest {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    SphereTest() { /* to satisfy JavaDoc generator */ }

    /**
     * Sphere center used in tests.
     */
    private static final Point CENTER = new Point(1, 2, 3);

    /**
     * Sphere radius used in tests.
     */
    private static final double RADIUS = 2d;

    /**
     * Point on sphere surface: CENTER + (RADIUS, 0, 0).
     */
    private static final Point POINT_ON_SURFACE = new Point(3, 2, 3);

    /**
     * Expected outward unit normal at {@link #POINT_ON_SURFACE}.
     */
    private static final Vector EXPECTED_NORMAL = Vector.AXIS_X;

    /**
     * Delta value for floating-point comparisons.
     */
    private static final double DELTA = 1e-10;

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        // TC01 (EP): Point on the sphere surface
        Sphere sphere = new Sphere(CENTER, RADIUS);
        Vector result = sphere.getNormal(POINT_ON_SURFACE);
        assertEquals(EXPECTED_NORMAL, result,
                "Sphere normal is incorrect for a point on the surface");
        assertEquals(1d, result.length(), DELTA,
                "Sphere normal is not a unit vector");

        // =============== Boundary Values Tests ==================
        // No boundary-value test requested for this method.
    }
}