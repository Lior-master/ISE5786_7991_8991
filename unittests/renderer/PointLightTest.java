package renderer;

import java.util.List;

import lighting.LightSample;
import lighting.LightSource;
import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.sampling.Blackboard;
import renderer.sampling.SamplingPattern;
import renderer.sampling.SamplingShape;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for class {@link PointLight}.
 * <p>
 * These tests verify the implementation of the {@link LightSource}
 * interface methods:
 * {@link LightSource#getL(Point)}, {@link LightSource#getIntensity(Point)},
 * {@link LightSource#getDistance(Point)} and
 * {@link LightSource#getSamples(Point)}.
 * </p>
 */
class PointLightTests {

    /**
     * Default constructor to satisfy documentation tools.
     */
    PointLightTests() {
        // Default constructor
    }

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Original light intensity used in the tests.
     */
    private static final Color INTENSITY = new Color(90, 120, 150);

    /**
     * Position of the point light in the scene.
     */
    private static final Point LIGHT_POSITION = new Point(1, 1, 1);

    /**
     * Point used for testing the light direction.
     */
    private static final Point POINT_DIRECTION = new Point(1, 4, 5);

    /**
     * Point used for testing attenuation. Distance from light is 2.
     */
    private static final Point POINT_ATTENUATION = new Point(3, 1, 1);

    /**
     * Error message for wrong light direction.
     */
    private static final String ERROR_DIRECTION = "Wrong point light direction";

    /**
     * Error message for wrong light intensity.
     */
    private static final String ERROR_INTENSITY = "Wrong point light intensity";

    /**
     * Test method for {@link PointLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        PointLight light = new PointLight(INTENSITY, LIGHT_POSITION).setKl(1);

        // ============ Equivalence Partitions Tests ==============

        // EP01: The direction vector is from the light position to the given point
        Vector expectedDirection = POINT_DIRECTION.subtract(LIGHT_POSITION).normalize();
        Vector result = light.getL(POINT_DIRECTION);

        assertEquals(expectedDirection, result, ERROR_DIRECTION);
        assertEquals(1, result.length(), DELTA, "Point light direction is not normalized");

        // =============== Boundary Values Tests ==================

        // BV01: The point coincides with the light position, so creating direction vector fails
        assertThrows(IllegalArgumentException.class,
                () -> light.getL(LIGHT_POSITION),
                "getL() should throw an exception when the point equals the light position");
    }

    /**
     * Test method for {@link PointLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        PointLight light = new PointLight(INTENSITY, LIGHT_POSITION).setKl(1);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Intensity decreases according to distance attenuation
        // Distance = 2, attenuation = kC + kL * d + kQ * d^2 = 1 + 1 * 2 + 0 = 3
        Color expectedIntensity = new Color(30, 40, 50);

        assertEquals(expectedIntensity.getColor(),
                light.getIntensity(POINT_ATTENUATION).getColor(),
                ERROR_INTENSITY);

        // =============== Boundary Values Tests ==================

        // BV01: The point coincides with the light position, distance is 0
        // attenuation = kC = 1, so intensity stays unchanged
        assertEquals(INTENSITY.getColor(),
                light.getIntensity(LIGHT_POSITION).getColor(),
                ERROR_INTENSITY);
    }

    /**
     * Test method for {@link PointLight#getDistance(Point)}.
     */
    @Test
    void testGetDistance() {
        PointLight light = new PointLight(INTENSITY, LIGHT_POSITION);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Distance from light position to point is calculated correctly
        assertEquals(2, light.getDistance(POINT_ATTENUATION), DELTA,
                "Wrong distance from point light");

        // =============== Boundary Values Tests ==================

        // BV01: Distance from light position to itself is zero
        assertEquals(0, light.getDistance(LIGHT_POSITION), DELTA,
                "Distance from light to itself should be zero");
    }

    /**
     * Test method for {@link PointLight#getSamples(Point)}.
     * Verifies that a point light with the default blackboard creates exactly
     * one sample, equivalent to the original point light behavior.
     */
    @Test
    void testGetSamplesDefaultSingleSample() {
        PointLight light = new PointLight(
                new Color(500, 500, 500),
                new Point(0, 0, 10)
        );

        Point point = new Point(0, 0, 0);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Default point light should create exactly one sample
        List<LightSample> samples = light.getSamples(point);

        assertEquals(1, samples.size(), "Default point light should create one sample");

        LightSample sample = samples.get(0);

        assertEquals(light.getL(point), sample.l(), "Wrong sample direction");
        assertEquals(light.getDistance(point), sample.distance(), DELTA, "Wrong sample distance");
        assertNotNull(sample.intensity(), "Sample intensity should not be null");
        assertEquals(light.getIntensity(point).getColor(), sample.intensity().getColor(),
                "Default sample intensity should match point light intensity");
    }

    /**
     * Test method for {@link PointLight#getSamples(Point)}.
     * Verifies that regular rectangular sampling creates the expected number
     * of light samples.
     */
    @Test
    void testGetSamplesRegularRectangle() {
        PointLight light = new PointLight(
                new Color(500, 500, 500),
                new Point(0, 0, 10)
        ).setBlackboard(
                new Blackboard()
                        .setSize(20)
                        .setGridSize(3)
                        .setShape(SamplingShape.RECTANGLE)
                        .setPattern(SamplingPattern.REGULAR)
        );

        Point point = new Point(0, 0, 0);

        // ============ Equivalence Partitions Tests ==============

        // EP02: A 3x3 regular rectangular grid should create 9 samples
        List<LightSample> samples = light.getSamples(point);

        assertEquals(9, samples.size(), "3x3 rectangular grid should create 9 light samples");

        for (LightSample sample : samples) {
            assertEquals(1, sample.l().length(), DELTA,
                    "Sample direction should be normalized");
            assertTrue(sample.distance() > 0,
                    "Sample distance should be positive");
            assertNotNull(sample.intensity(),
                    "Sample intensity should not be null");
        }

        // At least one sample should be farther than the central light distance
        double centralDistance = light.getDistance(point);
        boolean foundFartherSample = samples.stream()
                .anyMatch(sample -> sample.distance() > centralDistance);

        assertTrue(foundFartherSample,
                "At least one sampled light point should be farther than the central light position");
    }

    /**
     * Test method for {@link PointLight#getSamples(Point)}.
     * Verifies that zero-size sampling keeps the original single-sample behavior.
     */
    @Test
    void testGetSamplesZeroSize() {
        PointLight light = new PointLight(
                new Color(500, 500, 500),
                new Point(0, 0, 10)
        ).setBlackboard(
                new Blackboard()
                        .setSize(0)
                        .setGridSize(9)
                        .setShape(SamplingShape.RECTANGLE)
                        .setPattern(SamplingPattern.REGULAR)
        );

        Point point = new Point(0, 0, 0);

        // =============== Boundary Values Tests ==================

        // BV01: Zero size should create only one sample
        List<LightSample> samples = light.getSamples(point);

        assertEquals(1, samples.size(), "Zero size should create only one light sample");

        LightSample sample = samples.get(0);

        assertEquals(light.getL(point), sample.l(), "Wrong sample direction for zero-size sampling");
        assertEquals(light.getDistance(point), sample.distance(), DELTA,
                "Wrong sample distance for zero-size sampling");
    }

    /**
     * Test method for {@link PointLight#getSamples(Point)}.
     * Verifies that grid size 1 keeps the original single-sample behavior.
     */
    @Test
    void testGetSamplesGridSizeOne() {
        PointLight light = new PointLight(
                new Color(500, 500, 500),
                new Point(0, 0, 10)
        ).setBlackboard(
                new Blackboard()
                        .setSize(50)
                        .setGridSize(1)
                        .setShape(SamplingShape.RECTANGLE)
                        .setPattern(SamplingPattern.REGULAR)
        );

        Point point = new Point(0, 0, 0);

        // =============== Boundary Values Tests ==================

        // BV02: Grid size 1 should create only one sample
        List<LightSample> samples = light.getSamples(point);

        assertEquals(1, samples.size(), "Grid size 1 should create only one light sample");

        LightSample sample = samples.get(0);

        assertEquals(light.getL(point), sample.l(), "Wrong sample direction for grid size 1");
        assertEquals(light.getDistance(point), sample.distance(), DELTA,
                "Wrong sample distance for grid size 1");
    }

    /**
     * Test method for {@link PointLight#getSamples(Point)}.
     * Verifies that circular sampling filters out samples outside the circle.
     */
    @Test
    void testGetSamplesCircle() {
        PointLight light = new PointLight(
                new Color(500, 500, 500),
                new Point(0, 0, 10)
        ).setBlackboard(
                new Blackboard()
                        .setSize(10)
                        .setGridSize(5)
                        .setShape(SamplingShape.CIRCLE)
                        .setPattern(SamplingPattern.REGULAR)
        );

        Point point = new Point(0, 0, 0);

        // ============ Equivalence Partitions Tests ==============

        // EP03: A 5x5 circular grid should create 21 samples
        List<LightSample> samples = light.getSamples(point);

        assertEquals(21, samples.size(), "5x5 circular sampling should create 21 light samples");

        for (LightSample sample : samples) {
            assertEquals(1, sample.l().length(), DELTA,
                    "Sample direction should be normalized");
            assertTrue(sample.distance() > 0,
                    "Sample distance should be positive");
            assertNotNull(sample.intensity(),
                    "Sample intensity should not be null");
        }
    }

    /**
     * Test method for {@link PointLight#setBlackboard(Blackboard)}.
     * Verifies that setting a null blackboard is not allowed.
     */
    @Test
    void testSetBlackboardNull() {
        PointLight light = new PointLight(
                new Color(500, 500, 500),
                new Point(0, 0, 10)
        );

        // =============== Boundary Values Tests ==================

        // BV03: Null blackboard should throw an exception
        assertThrows(IllegalArgumentException.class,
                () -> light.setBlackboard(null),
                "Setting a null blackboard should throw an exception");
    }

    /**
     * Test method for {@link PointLight#getSamples(Point)}.
     * Verifies that sampling does not throw when the light direction is aligned
     * with a main axis.
     */
    @Test
    void testGetSamplesAxisAlignedDirectionDoesNotThrow() {
        PointLight light = new PointLight(
                new Color(500, 500, 500),
                new Point(0, 0, 10)
        ).setBlackboard(
                new Blackboard()
                        .setSize(20)
                        .setGridSize(3)
                        .setShape(SamplingShape.RECTANGLE)
                        .setPattern(SamplingPattern.REGULAR)
        );

        Point point = new Point(0, 0, 0);

        // =============== Boundary Values Tests ==================

        // BV04: Light direction aligned with Z axis should not throw
        assertDoesNotThrow(
                () -> light.getSamples(point),
                "Axis-aligned light direction should not throw"
        );
    }
}