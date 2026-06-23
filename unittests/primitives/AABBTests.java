package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AABB}.
 * <p>
 * Tests are organized according to EP and BV methodology.
 * </p>
 */
class AABBTests {

    /**
     * Accuracy for double comparisons.
     */
    private static final double DELTA = 1e-6;

    /**
     * Default constructor for Javadoc.
     */
    AABBTests() { /* to satisfy Javadoc generator */ }

    // ============ Equivalence Partitions Tests ==============

    /**
     * EP01: Ray crosses the box through the X axis.
     */
    @Test
    void testIntersectRayCrossesBoxXAxis() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        Ray ray = new Ray(new Point(-1, 1, 1), new Vector(1, 0, 0));

        double[] result = box.intersect(ray);

        assertNotNull(result, "Ray should intersect the box");
        assertEquals(1.0, result[0], DELTA, "Wrong tEnter");
        assertEquals(3.0, result[1], DELTA, "Wrong tExit");
    }

    /**
     * EP02: Ray crosses the box diagonally.
     */
    @Test
    void testIntersectRayCrossesBoxDiagonal() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        Ray ray = new Ray(new Point(-1, -1, -1), new Vector(1, 1, 1));

        double[] result = box.intersect(ray);

        assertNotNull(result, "Diagonal ray should intersect the box");
        assertTrue(result[0] > 0, "tEnter should be positive");
        assertTrue(result[1] > result[0], "tExit should be after tEnter");
        assertFalse(Double.isNaN(result[0]), "tEnter must not be NaN");
        assertFalse(Double.isNaN(result[1]), "tExit must not be NaN");
    }

    /**
     * EP03: Ray misses the box because it is outside the Y slab.
     */
    @Test
    void testIntersectRayMissesBoxOutsideSlab() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        Ray ray = new Ray(new Point(-1, 3, 1), new Vector(1, 0, 0));

        assertNull(box.intersect(ray), "Ray outside Y slab should miss the box");
    }

    /**
     * EP04: Ray starts inside the box and exits it.
     */
    @Test
    void testIntersectRayStartsInsideBox() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        Ray ray = new Ray(new Point(1, 1, 1), new Vector(1, 0, 0));

        double[] result = box.intersect(ray);

        assertNotNull(result, "Ray starting inside should intersect the box");
        assertTrue(result[0] <= 0, "tEnter should be negative or zero when starting inside");
        assertEquals(1.0, result[1], DELTA, "Wrong tExit");
    }

    /**
     * EP05: Union of two separated boxes.
     */
    @Test
    void testUnionSeparatedBoxes() {
        AABB box1 = new AABB(new Point(0, 0, 0), new Point(1, 1, 1));
        AABB box2 = new AABB(new Point(3, -2, 4), new Point(5, 2, 6));

        AABB result = AABB.union(box1, box2);

        assertNotNull(result, "Union should not be null");
        assertEquals(0, result.min.x(), DELTA, "Wrong min X");
        assertEquals(-2, result.min.y(), DELTA, "Wrong min Y");
        assertEquals(0, result.min.z(), DELTA, "Wrong min Z");
        assertEquals(5, result.max.x(), DELTA, "Wrong max X");
        assertEquals(2, result.max.y(), DELTA, "Wrong max Y");
        assertEquals(6, result.max.z(), DELTA, "Wrong max Z");
    }

    /**
     * EP06: Union of overlapping boxes.
     */
    @Test
    void testUnionOverlappingBoxes() {
        AABB box1 = new AABB(new Point(0, 0, 0), new Point(3, 3, 3));
        AABB box2 = new AABB(new Point(1, -1, 2), new Point(5, 2, 4));

        AABB result = AABB.union(box1, box2);

        assertEquals(0, result.min.x(), DELTA, "Wrong min X");
        assertEquals(-1, result.min.y(), DELTA, "Wrong min Y");
        assertEquals(0, result.min.z(), DELTA, "Wrong min Z");
        assertEquals(5, result.max.x(), DELTA, "Wrong max X");
        assertEquals(3, result.max.y(), DELTA, "Wrong max Y");
        assertEquals(4, result.max.z(), DELTA, "Wrong max Z");
    }

    // =============== Boundary Values Tests ==================

    /**
     * BV01: Ray is parallel to an axis and inside the corresponding slab.
     */
    @Test
    void testIntersectRayParallelInsideSlab() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        Ray ray = new Ray(new Point(-1, 1, 1), new Vector(1, 0, 0));

        double[] result = box.intersect(ray);

        assertNotNull(result, "Parallel ray inside Y/Z slabs should intersect");
        assertEquals(1.0, result[0], DELTA, "Wrong tEnter");
        assertEquals(3.0, result[1], DELTA, "Wrong tExit");
    }

    /**
     * BV02: Ray is parallel to an axis and outside the corresponding slab.
     */
    @Test
    void testIntersectRayParallelOutsideSlab() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        Ray ray = new Ray(new Point(-1, 3, 1), new Vector(1, 0, 0));

        assertNull(box.intersect(ray), "Parallel ray outside slab should miss");
    }

    /**
     * BV03: Ray starts exactly on the box boundary and goes inside.
     */
    @Test
    void testIntersectRayStartsOnBoundaryGoesInside() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        Ray ray = new Ray(new Point(0, 1, 1), new Vector(1, 0, 0));

        double[] result = box.intersect(ray);

        assertNotNull(result, "Ray starting on boundary should intersect");
        assertEquals(0.0, result[0], DELTA, "tEnter should be zero on boundary");
        assertEquals(2.0, result[1], DELTA, "Wrong tExit");
    }

    /**
     * BV04: Box is completely behind the ray origin.
     */
    @Test
    void testIntersectBoxBehindRay() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        Ray ray = new Ray(new Point(5, 1, 1), new Vector(1, 0, 0));

        assertNull(box.intersect(ray), "Box behind ray origin should not count as intersection");
    }

    /**
     * BV05: Flat AABB with zero size on Z axis is hit by a perpendicular ray.
     */
    @Test
    void testIntersectFlatBoxHit() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 0));
        Ray ray = new Ray(new Point(1, 1, -1), new Vector(0, 0, 1));

        double[] result = box.intersect(ray);

        assertNotNull(result, "Flat box should be intersectable");
        assertEquals(1.0, result[0], DELTA, "Wrong tEnter for flat box");
        assertEquals(1.0, result[1], DELTA, "Wrong tExit for flat box");
    }

    /**
     * BV06: Flat AABB is missed by a ray outside its X/Y bounds.
     */
    @Test
    void testIntersectFlatBoxMiss() {
        AABB box = new AABB(new Point(0, 0, 0), new Point(2, 2, 0));
        Ray ray = new Ray(new Point(3, 1, -1), new Vector(0, 0, 1));

        assertNull(box.intersect(ray), "Ray outside flat box bounds should miss");
    }

    /**
     * BV07: Union with null first argument.
     */
    @Test
    void testUnionNullFirstArgument() {
        AABB box = new AABB(new Point(1, 1, 1), new Point(2, 2, 2));

        assertEquals(box, AABB.union(null, box), "Union with null should return other box");
    }

    /**
     * BV08: Union with null second argument.
     */
    @Test
    void testUnionNullSecondArgument() {
        AABB box = new AABB(new Point(1, 1, 1), new Point(2, 2, 2));

        assertEquals(box, AABB.union(box, null), "Union with null should return other box");
    }

    /**
     * BV09: Degenerate AABB where min equals max.
     */
    @Test
    void testValidDegenerateBox() {
        AABB box = new AABB(new Point(1, 1, 1), new Point(1, 1, 1));

        assertTrue(box.isValid(), "A point-sized AABB should be valid");
    }

    /**
     * BV10: Invalid AABB with inverted X range.
     */
    @Test
    void testInvalidInvertedBox() {
        AABB box = new AABB(new Point(2, 0, 0), new Point(0, 2, 2));

        assertFalse(box.isValid(), "AABB with min greater than max should be invalid");
    }
}