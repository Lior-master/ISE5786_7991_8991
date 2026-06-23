package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link Point}.
 * The tests verify:
 * <ul>
 * <li>{@link Point#subtract(Point)}</li>
 * <li>{@link Point#add(Vector)}</li>
 * <li>{@link Point#distanceSquared(Point)}</li>
 * <li>{@link Point#distance(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PointTest {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    PointTest() { /* to satisfy JavaDoc generator */ }

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Point (1,2,3) used in tests
     */
    private static final Point POINT1 = new Point(1, 2, 3);
    /**
     * Point (2,4,6) used in tests
     */
    private static final Point POINT2 = new Point(2, 4, 6);
    /**
     * Point (2,4,5) used in tests
     */
    private static final Point POINT3 = new Point(2, 4, 5);

    /**
     * Vector (1,2,3) used in tests
     */
    private static final Vector VECTOR1 = new Vector(1, 2, 3);
    /**
     * Vector (-1,-2,-3) used in tests
     */
    private static final Vector VECTOR2 = new Vector(-1, -2, -3);

    /**
     * Error message for wrong subtract result
     */
    private static final String ERROR_SUBTRACT = "ERROR: subtract() wrong result";
    /**
     * Error message for wrong add result
     */
    private static final String ERROR_ADD = "ERROR: add() wrong result";
    /**
     * Error message for wrong squared distance result
     */
    private static final String ERROR_DISTANCE_SQUARED = "ERROR: distanceSquared() wrong result";
    /**
     * Error message for wrong distance result
     */
    private static final String ERROR_DISTANCE = "ERROR: distance() wrong result";
    /**
     * Error message for expected exception
     */
    private static final String ERROR_EXCEPTION = "ERROR: expected exception was not thrown";

    /**
     * Test method for {@link Point#subtract(Point)}.
     * Verifies correct subtraction of points and zero-vector boundary case.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Subtracting two different points returns the correct vector
        assertDoesNotThrow(() -> POINT2.subtract(POINT1), ERROR_EXCEPTION);
        assertEquals(VECTOR1, POINT2.subtract(POINT1), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================

        // BV01: Subtracting a point from itself should throw exception (zero vector)
        assertThrows(IllegalArgumentException.class, () -> POINT1.subtract(POINT1), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Point#add(Vector)}.
     * Verifies correct translation of a point by a vector.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Adding a regular vector to a point
        assertDoesNotThrow(() -> POINT1.add(VECTOR1), ERROR_EXCEPTION);
        assertEquals(POINT2, POINT1.add(VECTOR1), ERROR_ADD);

        // EP02: Adding a negative vector to a point
        assertDoesNotThrow(() -> POINT1.add(VECTOR2), ERROR_EXCEPTION);
        assertEquals(Point.ZERO, POINT1.add(VECTOR2), ERROR_ADD);
    }

    /**
     * Test method for {@link Point#distanceSquared(Point)}.
     * Verifies squared distance calculation and self-distance boundary case.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Squared distance between two different points
        assertEquals(9d, POINT1.distanceSquared(POINT3), DELTA, ERROR_DISTANCE_SQUARED);

        // EP02: Symmetry of squared distance
        assertEquals(9d, POINT3.distanceSquared(POINT1), DELTA, ERROR_DISTANCE_SQUARED);

        // =============== Boundary Values Tests ==================

        // BV01: Squared distance from a point to itself is zero
        assertEquals(0d, POINT1.distanceSquared(POINT1), DELTA, ERROR_DISTANCE_SQUARED);
    }

    /**
     * Test method for {@link Point#distance(Point)}.
     * Verifies distance calculation and self-distance boundary case.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Distance between two different points
        assertEquals(3d, POINT1.distance(POINT3), DELTA, ERROR_DISTANCE);

        // EP02: Symmetry of distance
        assertEquals(3d, POINT3.distance(POINT1), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================

        // BV01: Distance from a point to itself is zero
        assertEquals(0d, POINT1.distance(POINT1), DELTA, ERROR_DISTANCE);
    }
}