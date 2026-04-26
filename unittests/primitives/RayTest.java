package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link Ray}.
 * The tests verify:
 * <ul>
 * <li>Ray constructor validity</li>
 * <li>{@link Ray#direction()}</li>
 * <li>{@link Ray#origin()}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class RayTest {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    RayTest() { /* to satisfy JavaDoc generator */ }

    // ─────────────────────────── shared test data ────────────────────────────

    /**
     * A simple origin point used across tests.
     */
    private static final Point ORIGIN = new Point(1, 2, 3);

    /**
     * A non-normalized direction vector (length ≠ 1).
     */
    private static final Vector DIR_NON_UNIT = new Vector(0, 3, 4);   // length = 5

    /**
     * Expected unit direction after normalization of {@link #DIR_NON_UNIT}.
     */
    private static final Vector DIR_UNIT = new Vector(0, 0.6, 0.8);

    /**
     * An already-normalized direction vector (length = 1).
     */
    private static final Vector DIR_AXIS_X = Vector.AXIS_X;           // (1,0,0)

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-10;

    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Test method for {@link Ray#Ray(Point, Vector)}.
     * <p>
     * Verifies that a ray is built without exception and that its direction
     * is automatically normalized regardless of the supplied vector's length.
     * </p>
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct ray with a non-unit direction – should not throw
        assertDoesNotThrow(() -> new Ray(ORIGIN, DIR_NON_UNIT),
                "Failed constructing a valid ray with a non-unit direction");

        // TC02: Direction must be normalized by the constructor
        Ray ray = new Ray(ORIGIN, DIR_NON_UNIT);
        assertEquals(1d, ray.direction().length(), DELTA,
                "Ray constructor did not normalize the direction vector");

        // =============== Boundary Values Tests ==================

        // TC11: Direction already unit-length – should not throw
        assertDoesNotThrow(() -> new Ray(ORIGIN, DIR_AXIS_X),
                "Failed constructing a valid ray with a unit direction");

        // TC12: Zero vector as direction – must throw
        assertThrows(IllegalArgumentException.class,
                () -> new Ray(ORIGIN, new Vector(0, 0, 0)),
                "Constructed a ray with a zero direction vector");
    }

    /**
     * Test method for {@link Ray#direction()}.
     * <p>
     * Verifies that the returned direction is the normalized version of the
     * vector supplied to the constructor.
     * </p>
     */
    @Test
    void direction() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: direction() returns the correctly normalized vector
        Ray ray = new Ray(ORIGIN, DIR_NON_UNIT);
        assertEquals(DIR_UNIT, ray.direction(),
                "direction() did not return the expected normalized vector");

        // TC02: Returned direction must be a unit vector
        assertEquals(1d, ray.direction().length(), DELTA,
                "direction() returned a non-unit vector");

        // =============== Boundary Values Tests ==================

        // TC11: direction() with an already-unit direction
        Ray rayUnit = new Ray(ORIGIN, DIR_AXIS_X);
        assertEquals(DIR_AXIS_X, rayUnit.direction(),
                "direction() altered an already-unit direction");
    }

    /**
     * Test method for {@link Ray#origin()}.
     * <p>
     * Verifies that the returned origin equals the point supplied to the
     * constructor and is never affected by direction normalization.
     * </p>
     */
    @Test
    void origin() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: origin() returns the exact point used during construction
        Ray ray = new Ray(ORIGIN, DIR_NON_UNIT);
        assertEquals(ORIGIN, ray.origin(),
                "origin() did not return the expected origin point");

        // =============== Boundary Values Tests ==================

        // TC11: Origin at (0,0,0)
        Point zero = new Point(0, 0, 0);
        Ray rayAtZero = new Ray(zero, DIR_AXIS_X);
        assertEquals(zero, rayAtZero.origin(),
                "origin() did not return the expected origin when origin is (0,0,0)");
    }
    
    @Test
    void getPointTest()
    {
        // ============ Equivalence Partitions Tests ==============

        Ray ray = new Ray(ORIGIN, DIR_AXIS_X);

        // EP01: t < 0
        assertEquals(new Point(-1, 2, 3), ray.getPoint(-2),
                "getPoint() returned wrong point for t < 0");

        // EP02: t > 0
        assertEquals(new Point(4, 2, 3), ray.getPoint(3),
                "getPoint() returned wrong point for t > 0");

        // =============== Boundary Values Tests ==================

        // BV01: t = 0
        assertEquals(ORIGIN, ray.getPoint(0),
                "getPoint() should return origin for t = 0");
    }
}
