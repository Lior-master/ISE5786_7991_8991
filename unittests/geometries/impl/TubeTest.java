package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link Tube}.
 * The tests verify:
 * <ul>
 * <li>{@link Tube#getNormal(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class TubeTest {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    TubeTest() { /* to satisfy JavaDoc generator */ }

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Axis ray of the tested tube
     */
    private static final Ray AXIS = new Ray(new Point(1, 1, 1), new Vector(0, 0, 1));

    /**
     * Tube with radius 1 around the Z-like axis
     */
    private static final Tube TUBE = new Tube(1d, AXIS);

    /**
     * Error message for unexpected exception
     */
    private static final String ERROR_EXCEPTION = "ERROR: getNormal() threw unexpected exception";
    /**
     * Error message for wrong tube normal
     */
    private static final String ERROR_NORMAL = "ERROR: Tube normal is wrong";

    /**
     * Test method for {@link Tube#getNormal(Point)}.
     * Verifies that the returned normal vector is unit length and orthogonal
     * to the tube axis in equivalence partitions and boundary value cases.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Point on tube where orthogonal projection is on the axis after its head
        Point p1 = new Point(2, 1, 3);
        assertDoesNotThrow(() -> TUBE.getNormal(p1), ERROR_EXCEPTION);
        Vector result1 = TUBE.getNormal(p1);
        assertEquals(1d, result1.length(), DELTA, ERROR_NORMAL);
        assertEquals(0d, result1.dotProduct(AXIS.direction()), DELTA, ERROR_NORMAL);
        assertEquals(new Vector(1, 0, 0), result1, ERROR_NORMAL);

        // EP02: Point on tube where orthogonal projection is on the axis before its head
        Point p2 = new Point(2, 1, 0);
        assertDoesNotThrow(() -> TUBE.getNormal(p2), ERROR_EXCEPTION);
        Vector result2 = TUBE.getNormal(p2);
        assertEquals(1d, result2.length(), DELTA, ERROR_NORMAL);
        assertEquals(0d, result2.dotProduct(AXIS.direction()), DELTA, ERROR_NORMAL);
        assertEquals(new Vector(1, 0, 0), result2, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // BV01: Point on tube in front - projection point is exactly the axis head
        Point p3 = new Point(2, 1, 1);
        assertDoesNotThrow(() -> TUBE.getNormal(p3), ERROR_EXCEPTION);
        Vector result3 = TUBE.getNormal(p3);
        assertEquals(1d, result3.length(), DELTA, ERROR_NORMAL);
        assertEquals(0d, result3.dotProduct(AXIS.direction()), DELTA, ERROR_NORMAL);
        assertEquals(new Vector(1, 0, 0), result3, ERROR_NORMAL);
    }
}