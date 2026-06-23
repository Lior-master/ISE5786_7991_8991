package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link Vector}.
 * The tests verify:
 * <ul>
 * <li>{@link Vector#add(Vector)}</li>
 * <li>{@link Vector#subtract(Point)}</li>
 * <li>{@link Vector#scale(double)}</li>
 * <li>{@link Vector#dotProduct(Vector)}</li>
 * <li>{@link Vector#crossProduct(Vector)}</li>
 * <li>{@link Vector#lengthSquared()}</li>
 * <li>{@link Vector#length()}</li>
 * <li>{@link Vector#normalize()}</li>
 * <li>{@link Vector#distanceSquared(Point)}</li>
 * <li>{@link Vector#distance(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class VectorTest {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    VectorTest() { /* to satisfy JavaDoc generator */ }

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Vector (1,2,3) used in tests
     */
    private static final Vector V1 = new Vector(1, 2, 3);
    /**
     * Vector (-1,-2,-3) used in tests
     */
    private static final Vector V1_OPPOSITE = new Vector(-1, -2, -3);
    /**
     * Vector (-2,-4,-6) used in tests
     */
    private static final Vector V2 = new Vector(-2, -4, -6);
    /**
     * Vector (0,3,-2) used in tests
     */
    private static final Vector V3 = new Vector(0, 3, -2);
    /**
     * Vector (1,2,2) used in tests
     */
    private static final Vector V4 = new Vector(1, 2, 2);
    /**
     * Vector (3,6,9) used in tests
     */
    private static final Vector V5 = new Vector(3, 6, 9);

    /**
     * Error message for wrong add result
     */
    private static final String ERROR_ADD = "ERROR: add() wrong result";
    /**
     * Error message for wrong subtract result
     */
    private static final String ERROR_SUBTRACT = "ERROR: subtract() wrong result";
    /**
     * Error message for wrong scale result
     */
    private static final String ERROR_SCALE = "ERROR: scale() wrong result";
    /**
     * Error message for wrong dot product result
     */
    private static final String ERROR_DOT_PRODUCT = "ERROR: dotProduct() wrong result";
    /**
     * Error message for wrong cross product result
     */
    private static final String ERROR_CROSS_PRODUCT = "ERROR: crossProduct() wrong result";
    /**
     * Error message for wrong vector lengthSquared
     */
    private static final String ERROR_LENGTH_SQUARED = "ERROR: lengthSquared() wrong result";
    /**
     * Error message for wrong vector length
     */
    private static final String ERROR_LENGTH = "ERROR: length() wrong result";
    /**
     * Error message for wrong normalize result
     */
    private static final String ERROR_NORMALIZE = "ERROR: normalize() wrong result";
    /**
     * Error message for wrong distanceSquared result
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
     * test method for {@link Vector#Vector(double, double, double)} and for {@link Vector#Vector(Double3)}.
     * Verifies that a vector is constructed correctly and that constructing a zero vector throws an exception.
     */
    @Test
    void testConstructors() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Constructing a valid vector should not throw
        assertDoesNotThrow(() -> new Vector(1, 2, 3), ERROR_EXCEPTION);
        assertDoesNotThrow(() -> new Vector(new Double3(1, 2, 3)), ERROR_EXCEPTION);

        // =============== Boundary Values Tests ==================

        // BV01: Constructing a zero vector should throw exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0), ERROR_EXCEPTION);
        assertThrows(IllegalArgumentException.class, () -> new Vector(new Double3(0, 0, 0)), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#add(Vector)}.
     * Verifies vector addition and zero-vector boundary case.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Adding two different vectors returns the correct vector
        assertDoesNotThrow(() -> V1.add(V2), ERROR_EXCEPTION);
        assertEquals(V1_OPPOSITE, V1.add(V2), ERROR_ADD);

        // =============== Boundary Values Tests ==================

        // BV01: Adding opposite vectors should throw exception (zero vector)
        assertThrows(IllegalArgumentException.class, () -> V1.add(V1_OPPOSITE), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#subtract(Point)}.
     * Verifies vector subtraction and zero-vector boundary case.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Subtracting two different vectors returns the correct vector
        assertDoesNotThrow(() -> V1.subtract(V2), ERROR_EXCEPTION);
        assertEquals(V5, V1.subtract(V2), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================

        // BV01: Subtracting a vector from itself should throw exception (zero vector)
        assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#scale(double)}.
     * Verifies correct scaling and zero-scalar boundary case.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Scaling by a positive scalar
        assertEquals(new Vector(2, 4, 6), V1.scale(2), ERROR_SCALE);

        // EP02: Scaling by a negative scalar
        assertEquals(V1_OPPOSITE, V1.scale(-1), ERROR_SCALE);

        // =============== Boundary Values Tests ==================

        // BV01: Scaling by zero should throw exception (zero vector)
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#dotProduct(Vector)}.
     * Verifies orthogonality and correct scalar product.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Dot product of orthogonal vectors is zero
        assertEquals(0d, V1.dotProduct(V3), DELTA, ERROR_DOT_PRODUCT);

        // EP02: Dot product of non-orthogonal vectors
        assertEquals(-28d, V1.dotProduct(V2), DELTA, ERROR_DOT_PRODUCT);

        // =============== Boundary Values Tests ==================

        // BV01: Dot product of a vector with itself equals length squared
        assertEquals(V1.lengthSquared(), V1.dotProduct(V1), DELTA, ERROR_DOT_PRODUCT);
    }

    /**
     * Test method for {@link Vector#crossProduct(Vector)}.
     * Verifies orthogonality, length relation and parallel-vector boundary case.
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Cross product of non-parallel vectors produces orthogonal result
        Vector result = V1.crossProduct(V3);
        assertEquals(V1.length() * V3.length(), result.length(), DELTA, ERROR_CROSS_PRODUCT);
        assertEquals(0d, result.dotProduct(V1), DELTA, ERROR_CROSS_PRODUCT);
        assertEquals(0d, result.dotProduct(V3), DELTA, ERROR_CROSS_PRODUCT);

        // EP02: Anti-commutativity: a x b = -(b x a)
        assertEquals(V1.crossProduct(V3), V3.crossProduct(V1).scale(-1), ERROR_CROSS_PRODUCT);

        // =============== Boundary Values Tests ==================

        // BV01: Cross product of parallel vectors should throw exception (zero vector)
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V2), ERROR_EXCEPTION);
    }

    /**
     * Test method for {@link Vector#lengthSquared()}.
     * Verifies squared length computation.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: lengthSquared of a regular vector
        assertEquals(9d, V4.lengthSquared(), DELTA, ERROR_LENGTH_SQUARED);
    }

    /**
     * Test method for {@link Vector#length()}.
     * Verifies vector length computation.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: length of a regular vector
        assertEquals(3d, V4.length(), DELTA, ERROR_LENGTH);
    }

    /**
     * Test method for {@link Vector#normalize()}.
     * Verifies that normalized vector has unit length and same direction.
     */
    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Normalized vector has length 1
        Vector result = V1.normalize();
        assertEquals(1d, result.length(), DELTA, ERROR_NORMALIZE);

        // EP02: Normalized vector is parallel to the original vector
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(result), ERROR_NORMALIZE);

        // EP03: Normalized vector has the same direction as the original vector
        assertEquals(true, V1.dotProduct(result) > 0, ERROR_NORMALIZE);
    }

    /**
     * Test method for {@link Vector#distanceSquared(Point)}.
     * Verifies squared distance computation inherited from {@link Point}.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Squared distance between two different vectors
        assertEquals(9d, V1.distanceSquared(new Vector(2, 4, 5)), DELTA, ERROR_DISTANCE_SQUARED);

        // =============== Boundary Values Tests ==================

        // BV01: Squared distance from a vector to itself is zero
        assertEquals(0d, V1.distanceSquared(V1), DELTA, ERROR_DISTANCE_SQUARED);
    }

    /**
     * Test method for {@link Vector#distance(Point)}.
     * Verifies distance computation inherited from {@link Point}.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Distance between two different vectors
        assertEquals(3d, V1.distance(new Vector(2, 4, 5)), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================

        // BV01: Distance from a vector to itself is zero
        assertEquals(0d, V1.distance(V1), DELTA, ERROR_DISTANCE);
    }
}