package renderer.sampling;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for class {@link Blackboard}.
 * <p>
 * The tests verify regular sampling generation in 2D and the conversion
 * of 2D samples into 3D points using a local coordinate system.
 * </p>
 */
class BlackboardTests {
    /**
     * Accuracy for comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    BlackboardTests() {
        /* to satisfy JavaDoc generator */
    }

    /**
     * Test method for {@link Blackboard#generatePoints(Point, Vector, Vector)}.
     * Verifies regular sampling mapped to 3D points in a rectangular area.
     */
    @Test
    void testGenerateRegularPointsRectangle() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Regular 2x2 sampling inside a rectangular area
        Blackboard board = new Blackboard()
                .setSize(20, 10)
                .setGridSize(2)
                .setShape(SamplingShape.RECTANGLE)
                .setPattern(SamplingPattern.REGULAR);
        // Map 2D samples into 3D points using world axes and center at origin
        List<Point> points = board.generatePoints(new Point(0, 0, 0), Vector.AXIS_X, Vector.AXIS_Y);

        assertEquals(4, points.size(), "Wrong number of 3D points");

        assertEquals(new Point(-5, -2.5, 0), points.get(0), "Wrong first point");
        assertEquals(new Point(5, -2.5, 0), points.get(1), "Wrong second point");
        assertEquals(new Point(-5, 2.5, 0), points.get(2), "Wrong third point");
        assertEquals(new Point(5, 2.5, 0), points.get(3), "Wrong fourth point");
    }

    /**
     * Test method for {@link Blackboard#generatePoints(Point, Vector, Vector)}.
     * Verifies that zero-size sampling returns only the center point.
     */
    @Test
    void testGenerateSamplesZeroSize() {
        // =============== Boundary Values Tests ==================

        // TC01: Zero size should produce only one sample at the center
        Blackboard board = new Blackboard()
                .setSize(0)
                .setGridSize(9)
                .setShape(SamplingShape.RECTANGLE)
                .setPattern(SamplingPattern.REGULAR);

        Point center = new Point(0, 0, 0);
        List<Point> points = board.generatePoints(center, Vector.AXIS_X, Vector.AXIS_Y);

        assertEquals(1, points.size(), "Zero size should create only one 3D point");
        assertEquals(center, points.get(0), "The only point should be the center");
    }

    /**
     * Test method for {@link Blackboard#generatePoints(Point, Vector, Vector)}.
     * Verifies that grid size 1 returns only the center point.
     */
    @Test
    void testGenerateSamplesGridSizeOne() {
        // =============== Boundary Values Tests ==================

        // TC02: Grid size 1 should produce only one sample at the center
        Blackboard board = new Blackboard()
                .setSize(50)
                .setGridSize(1)
                .setShape(SamplingShape.RECTANGLE)
                .setPattern(SamplingPattern.REGULAR);

        Point center = new Point(0, 0, 0);
        List<Point> points = board.generatePoints(center, Vector.AXIS_X, Vector.AXIS_Y);

        assertEquals(1, points.size(), "Grid size 1 should create only one 3D point");
        assertEquals(center, points.get(0), "The only point should be the center");
    }

    /**
     * Test method for {@link Blackboard#generatePoints(Point, Vector, Vector)}.
     * Verifies that circular sampling removes points outside the circle after mapping to 3D.
     */
    @Test
    void testGenerateRegularSamplesCircle() {
        // ============ Equivalence Partitions Tests ==============

        // TC02: Regular 5x5 sampling inside a circular area
        Blackboard board = new Blackboard()
                .setSize(10)
                .setGridSize(5)
                .setShape(SamplingShape.CIRCLE)
                .setPattern(SamplingPattern.REGULAR);
        // Map to 3D using center at origin and world axes so that x,y correspond to sample coords
        List<Point> points = board.generatePoints(new Point(0, 0, 0), Vector.AXIS_X, Vector.AXIS_Y);

        // In a 5x5 grid with size 10, only the four corners are outside the circle
        assertEquals(21, points.size(), "Wrong number of points inside the circle");

        double radius = 5;

        for (Point p : points) {
            double distanceSquared = p.distanceSquared(Point.ZERO);

            assertTrue(distanceSquared <= radius * radius + DELTA,
                    "Point should be inside the circle");
        }
    }

    /**
     * Test method for {@link Blackboard#generatePoints(Point, Vector, Vector)}.
     * Verifies conversion from 2D samples to 3D points.
     */
    @Test
    void testGeneratePointsRectangle() {
        // ============ Equivalence Partitions Tests ==============

        // TC03: Convert 2D samples into 3D points using axis X and axis Y
        Blackboard board = new Blackboard()
                .setSize(20, 10)
                .setGridSize(2)
                .setShape(SamplingShape.RECTANGLE)
                .setPattern(SamplingPattern.REGULAR);

        Point center = new Point(1, 2, 3);

        List<Point> points = board.generatePoints(
                center,
                Vector.AXIS_X,
                Vector.AXIS_Y
        );

        assertEquals(4, points.size(), "Wrong number of 3D points");

        assertEquals(new Point(-4, -0.5, 3), points.get(0), "Wrong first point");
        assertEquals(new Point(6, -0.5, 3), points.get(1), "Wrong second point");
        assertEquals(new Point(-4, 4.5, 3), points.get(2), "Wrong third point");
        assertEquals(new Point(6, 4.5, 3), points.get(3), "Wrong fourth point");
    }

    /**
     * Test method for {@link Blackboard#generatePoints(Point, Vector, Vector)}.
     * Verifies that zero-size 3D sampling returns only the center point.
     */
    @Test
    void testGeneratePointsZeroSize() {
        // =============== Boundary Values Tests ==================

        // TC03: Zero size should return only the original center point
        Blackboard board = new Blackboard()
                .setSize(0)
                .setGridSize(5)
                .setShape(SamplingShape.RECTANGLE)
                .setPattern(SamplingPattern.REGULAR);

        Point center = new Point(1, 2, 3);

        List<Point> points = board.generatePoints(
                center,
                Vector.AXIS_X,
                Vector.AXIS_Y
        );

        assertEquals(1, points.size(), "Zero size should create only one 3D point");
        assertEquals(center, points.get(0), "The only point should be the center");
    }

    /**
     * Test method for Blackboard setter validation.
     */
    @Test
    void testSettersValidation() {
        // =============== Boundary Values Tests ==================

        // TC04: Negative size values should throw an exception
        assertThrows(IllegalArgumentException.class,
                () -> new Blackboard().setSize(-1),
                "Negative square size should throw an exception");

        assertThrows(IllegalArgumentException.class,
                () -> new Blackboard().setSize(10, -1),
                "Negative height should throw an exception");

        assertThrows(IllegalArgumentException.class,
                () -> new Blackboard().setSize(-1, 10),
                "Negative width should throw an exception");

        // TC05: Invalid grid size should throw an exception
        assertThrows(IllegalArgumentException.class,
                () -> new Blackboard().setGridSize(0),
                "Grid size 0 should throw an exception");

        assertThrows(IllegalArgumentException.class,
                () -> new Blackboard().setGridSize(-3),
                "Negative grid size should throw an exception");

        // TC06: Null shape and pattern should throw an exception
        assertThrows(IllegalArgumentException.class,
                () -> new Blackboard().setShape(null),
                "Null shape should throw an exception");

        assertThrows(IllegalArgumentException.class,
                () -> new Blackboard().setPattern(null),
                "Null pattern should throw an exception");
    }

    /**
     * Test method for normal valid construction and generation.
     */
    @Test
    void testValidBoardDoesNotThrow() {
        // ============ Equivalence Partitions Tests ==============

        // TC04: Valid board configuration should not throw
        assertDoesNotThrow(() -> new Blackboard()
                        .setSize(10, 20)
                        .setGridSize(3)
                        .setShape(SamplingShape.RECTANGLE)
                        .setPattern(SamplingPattern.REGULAR)
                        .generatePoints(new Point(0, 0, 0), Vector.AXIS_X, Vector.AXIS_Y),
                "Valid board configuration should not throw");
    }
}