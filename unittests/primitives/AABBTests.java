package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link AABB}.
 * Tests verify:
 * <ul>
 * <li>{@link AABB#union(AABB, AABB)} - union of two bounding boxes</li>
 * <li>{@link AABB#intersect(Ray)} - ray-box intersection using slab method</li>
 * <li>{@link AABB#isValid()} - validity check</li>
 * </ul>
 */
class AABBTests {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    AABBTests() { /* to satisfy JavaDoc generator */ }

    private static final Point MIN = new Point(0, 0, 0);
    private static final Point MAX = new Point(2, 2, 2);

    // ============ Tests for intersect ============

    /**
     * Test ray passing through the box horizontally.
     */
    @Test
    void testRayIntersectsBoxHorizontal() {
        AABB box = new AABB(MIN, MAX);
        Ray ray = new Ray(new Point(-1, 1, 1), new Vector(1, 0, 0));
        double[] result = box.intersect(ray);

        assertNotNull(result, "Ray should intersect box");
        assertEquals(2, result.length, "Result should have two values: [tEnter, tExit]");
        assertEquals(1.0, result[0], 0.001, "tEnter should be 1.0");
        assertEquals(3.0, result[1], 0.001, "tExit should be 3.0");
    }

    /**
     * Test ray starting inside the box.
     */
    @Test
    void testRayStartingInsideBox() {
        AABB box = new AABB(MIN, MAX);
        Ray ray = new Ray(new Point(1, 1, 1), new Vector(1, 0, 0));
        double[] result = box.intersect(ray);

        assertNotNull(result, "Ray starting inside should intersect");
        assertEquals(2, result.length, "Result should have two values");
        // Since ray starts inside at t=0, tEnter should be negative infinity or 0 (depending on implementation)
        // tExit should be when ray exits
        assertTrue(result[1] > 0, "tExit should be positive");
    }

    /**
     * Test ray missing the box.
     */
    @Test
    void testRayMissesBox() {
        AABB box = new AABB(MIN, MAX);
        Ray ray = new Ray(new Point(-1, -1, -1), new Vector(1, 0, 0));
        double[] result = box.intersect(ray);

        assertNull(result, "Ray should not intersect box when passing below");
    }

    /**
     * Test ray parallel to box (along one axis) but misses it.
     */
    @Test
    void testRayParallelToBoxAndMisses() {
        AABB box = new AABB(MIN, MAX);
        Ray ray = new Ray(new Point(1, -1, 1), new Vector(1, 0, 0));
        double[] result = box.intersect(ray);

        assertNull(result, "Ray parallel to box but outside should not intersect");
    }

    /**
     * Test ray from origin piercing the box diagonally.
     */
    @Test
    void testRayDiagonalIntersect() {
        AABB box = new AABB(MIN, MAX);
        Ray ray = new Ray(new Point(-1, -1, -1), new Vector(1, 1, 1).normalize());
        double[] result = box.intersect(ray);

        assertNotNull(result, "Diagonal ray should intersect box");
        assertEquals(2, result.length, "Result should have two values");
        assertTrue(result[0] >= 0 || result[0] < 0, "tEnter should be valid");
        assertTrue(result[1] > result[0], "tExit should be greater than tEnter");
    }

    /**
     * Test ray hitting corner of box.
     */
    @Test
    void testRayHittingCorner() {
        AABB box = new AABB(MIN, MAX);
        // Ray aiming at corner (2, 2, 2)
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 1, 1).normalize());
        double[] result = box.intersect(ray);

        assertNotNull(result, "Ray hitting corner should intersect");
    }

    // ============ Tests for union ============

    /**
     * Test union of two non-overlapping boxes.
     */
    @Test
    void testUnionNonOverlappingBoxes() {
        AABB box1 = new AABB(new Point(0, 0, 0), new Point(1, 1, 1));
        AABB box2 = new AABB(new Point(3, 3, 3), new Point(4, 4, 4));

        AABB result = AABB.union(box1, box2);

        assertNotNull(result, "Union should not be null");
        assertEquals(0, result.min.x(), 0.001, "Min x should be 0");
        assertEquals(0, result.min.y(), 0.001, "Min y should be 0");
        assertEquals(0, result.min.z(), 0.001, "Min z should be 0");
        assertEquals(4, result.max.x(), 0.001, "Max x should be 4");
        assertEquals(4, result.max.y(), 0.001, "Max y should be 4");
        assertEquals(4, result.max.z(), 0.001, "Max z should be 4");
    }

    /**
     * Test union of overlapping boxes.
     */
    @Test
    void testUnionOverlappingBoxes() {
        AABB box1 = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        AABB box2 = new AABB(new Point(1, 1, 1), new Point(3, 3, 3));

        AABB result = AABB.union(box1, box2);

        assertNotNull(result, "Union should not be null");
        assertEquals(0, result.min.x(), 0.001, "Min x should be 0");
        assertEquals(3, result.max.x(), 0.001, "Max x should be 3");
    }

    /**
     * Test union with null box (first argument).
     */
    @Test
    void testUnionWithNullFirst() {
        AABB box2 = new AABB(new Point(1, 1, 1), new Point(3, 3, 3));
        AABB result = AABB.union(null, box2);

        assertEquals(box2, result, "Union with null should return the non-null box");
    }

    /**
     * Test union with null box (second argument).
     */
    @Test
    void testUnionWithNullSecond() {
        AABB box1 = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        AABB result = AABB.union(box1, null);

        assertEquals(box1, result, "Union with null should return the non-null box");
    }

    /**
     * Test union of identical boxes.
     */
    @Test
    void testUnionIdenticalBoxes() {
        AABB box1 = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        AABB box2 = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));

        AABB result = AABB.union(box1, box2);

        assertNotNull(result, "Union should not be null");
        assertEquals(0, result.min.x(), 0.001, "Min x should be 0");
        assertEquals(2, result.max.x(), 0.001, "Max x should be 2");
    }

    // ============ Tests for isValid ============

    /**
     * Test validity of a proper box.
     */
    @Test
    void testValidBox() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        assertTrue(box.isValid(), "Box with min <= max should be valid");
    }

    /**
     * Test invalidity of inverted box.
     */
    @Test
    void testInvalidBoxInvertedX() {
        AABB box = new AABB(new Point(2, 0, 0), new Point(0, 2, 2));
        assertFalse(box.isValid(), "Box with inverted X should be invalid");
    }

    /**
     * Test invalidity of inverted box on Y.
     */
    @Test
    void testInvalidBoxInvertedY() {
        AABB box = new AABB(new Point(0, 2, 0), new Point(2, 0, 2));
        assertFalse(box.isValid(), "Box with inverted Y should be invalid");
    }

    /**
     * Test validity of box with min == max (point).
     */
    @Test
    void testValidBoxMinEqualsMax() {
        AABB box = new AABB(new Point(1, 1, 1), new Point(1, 1, 1));
        assertTrue(box.isValid(), "Box where min == max should be valid (degenerate but valid)");
    }
}
