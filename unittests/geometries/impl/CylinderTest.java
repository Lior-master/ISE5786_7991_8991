package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link Cylinder}.
 * The tests verify:
 * <ul>
 * <li>{@link Cylinder#getNormal(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class CylinderTests {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    CylinderTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Axis ray of the tested cylinder
     */
    private static final Ray AXIS = new Ray(new Point(1, 1, 1), new Vector(0, 0, 1));

    /**
     * Cylinder with radius 1 and height 4
     */
    private static final Cylinder CYLINDER = new Cylinder(1d, AXIS, 4d);

    /**
     * Error message for unexpected exception
     */
    private static final String ERROR_EXCEPTION = "ERROR: getNormal() threw unexpected exception";
    /**
     * Error message for wrong cylinder normal
     */
    private static final String ERROR_NORMAL = "ERROR: Cylinder normal is wrong";

    /**
     * Test method for {@link Cylinder#getNormal(Point)}.
     * Verifies normals on the side surface and on both bases of the cylinder.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Point on the side surface
        Point p1 = new Point(2, 1, 3);
        assertDoesNotThrow(() -> CYLINDER.getNormal(p1), ERROR_EXCEPTION);
        Vector result1 = CYLINDER.getNormal(p1);
        assertEquals(new Vector(1, 0, 0), result1, ERROR_NORMAL);
        assertEquals(1d, result1.length(), DELTA, ERROR_NORMAL);

        // EP02: Point on the bottom base (not center)
        Point p2 = new Point(1.5, 1, 1);
        assertDoesNotThrow(() -> CYLINDER.getNormal(p2), ERROR_EXCEPTION);
        Vector result2 = CYLINDER.getNormal(p2);
        assertEquals(new Vector(0, 0, -1), result2, ERROR_NORMAL);
        assertEquals(1d, result2.length(), DELTA, ERROR_NORMAL);

        // EP03: Point on the top base (not center)
        Point p3 = new Point(1.5, 1, 5);
        assertDoesNotThrow(() -> CYLINDER.getNormal(p3), ERROR_EXCEPTION);
        Vector result3 = CYLINDER.getNormal(p3);
        assertEquals(new Vector(0, 0, 1), result3, ERROR_NORMAL);
        assertEquals(1d, result3.length(), DELTA, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================

        // BV01: Point at the center of the bottom base
        Point p4 = new Point(1, 1, 1);
        assertDoesNotThrow(() -> CYLINDER.getNormal(p4), ERROR_EXCEPTION);
        Vector result4 = CYLINDER.getNormal(p4);
        assertEquals(new Vector(0, 0, -1), result4, ERROR_NORMAL);
        assertEquals(1d, result4.length(), DELTA, ERROR_NORMAL);

        // BV02: Point at the center of the top base
        Point p5 = new Point(1, 1, 5);
        assertDoesNotThrow(() -> CYLINDER.getNormal(p5), ERROR_EXCEPTION);
        Vector result5 = CYLINDER.getNormal(p5);
        assertEquals(new Vector(0, 0, 1), result5, ERROR_NORMAL);
        assertEquals(1d, result5.length(), DELTA, ERROR_NORMAL);

        // BV03: Point on the edge between side and bottom base
        Point p6 = new Point(2, 1, 1);
        assertDoesNotThrow(() -> CYLINDER.getNormal(p6), ERROR_EXCEPTION);
        Vector result6 = CYLINDER.getNormal(p6);
        assertEquals(new Vector(0, 0, -1), result6, ERROR_NORMAL);
        assertEquals(1d, result6.length(), DELTA, ERROR_NORMAL);

        // BV04: Point on the edge between side and top base
        Point p7 = new Point(2, 1, 5);
        assertDoesNotThrow(() -> CYLINDER.getNormal(p7), ERROR_EXCEPTION);
        Vector result7 = CYLINDER.getNormal(p7);
        assertEquals(new Vector(0, 0, 1), result7, ERROR_NORMAL);
        assertEquals(1d, result7.length(), DELTA, ERROR_NORMAL);
    }
}