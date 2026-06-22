package renderer;

import java.util.List;

import lighting.LightSample;
import lighting.LightSource;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.sampling.Blackboard;
import renderer.sampling.SamplingPattern;
import renderer.sampling.SamplingShape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link SpotLight}.
 * <p>
 * These tests verify the implementation of the {@link LightSource}
 * interface methods:
 * {@link LightSource#getL(Point)} and {@link LightSource#getIntensity(Point)}.
 * </p>
 */
class SpotLightTest {

    /**
     * Default constructor to satisfy documentation tools.
     */
    SpotLightTest() {
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
     * Position of the spotlight in the scene.
     */
    private static final Point LIGHT_POSITION = new Point(1, 1, 1);

    /**
     * Spotlight direction along the positive X axis.
     */
    private static final Vector SPOT_DIRECTION = new Vector(1, 0, 0);

    /**
     * Point located directly in front of the spotlight.
     */
    private static final Point POINT_IN_FRONT = new Point(3, 1, 1);

    /**
     * Point located behind the spotlight.
     */
    private static final Point POINT_BEHIND = new Point(-1, 1, 1);

    /**
     * Point located at 90 degrees from the spotlight direction.
     */
    private static final Point POINT_90_DEGREES = new Point(1, 3, 1);

    /**
     * Error message for wrong spotlight direction.
     */
    private static final String ERROR_DIRECTION = "Wrong spotlight direction";

    /**
     * Error message for wrong spotlight intensity.
     */
    private static final String ERROR_INTENSITY = "Wrong spotlight intensity";

    /**
     * Test method for {@link SpotLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        SpotLight light = new SpotLight(INTENSITY, LIGHT_POSITION, SPOT_DIRECTION).setKl(1);

        // ============ Equivalence Partitions Tests ==============

        // EP01: The direction vector is from the light position to the given point
        Vector expectedDirection = POINT_IN_FRONT.subtract(LIGHT_POSITION).normalize();
        Vector result = light.getL(POINT_IN_FRONT);

        assertEquals(expectedDirection, result, ERROR_DIRECTION);
        assertEquals(1, result.length(), DELTA, "Spotlight direction vector is not normalized");

        // =============== Boundary Values Tests ==================

        // BV01: The point coincides with the light position, so creating direction vector fails
        assertThrows(IllegalArgumentException.class,
                () -> light.getL(LIGHT_POSITION),
                "getL() should throw an exception when the point equals the light position");
    }

    /**
     * Test method for {@link SpotLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        SpotLight light = new SpotLight(INTENSITY, LIGHT_POSITION, SPOT_DIRECTION).setKl(1);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Point is directly in front of the spotlight
        // Distance = 2
        // attenuation = kC + kL * d + kQ * d^2 = 1 + 1 * 2 + 0 = 3
        // beam factor = direction dot getL(point) = 1
        // expected intensity = (90,120,150) / 3 = (30,40,50)
        Color expectedFrontIntensity = new Color(30, 40, 50);

        assertEquals(expectedFrontIntensity.getColor(),
                light.getIntensity(POINT_IN_FRONT).getColor(),
                ERROR_INTENSITY);

        // EP02: Point is behind the spotlight, so it receives no light
        assertEquals(Color.BLACK.getColor(),
                light.getIntensity(POINT_BEHIND).getColor(),
                ERROR_INTENSITY);

        // =============== Boundary Values Tests ==================

        // BV01: Point is at 90 degrees from the spotlight direction, so beam factor is 0
        assertEquals(Color.BLACK.getColor(),
                light.getIntensity(POINT_90_DEGREES).getColor(),
                ERROR_INTENSITY);

        // BV02: Point coincides with the spotlight position
        // Distance = 0, so point-light attenuation is 1 and intensity stays unchanged
        assertEquals(INTENSITY.getColor(),
                light.getIntensity(LIGHT_POSITION).getColor(),
                ERROR_INTENSITY);
    }

    /**
     * Test method for {@link SpotLight#getSamples(Point)} with a regular rectangle sampling pattern.
     * <p>
     * This test verifies that the spotlight generates the correct number of samples and that each sample direction is normalized.
     * </p>
     */
    @Test
    void testGetSamplesRegularRectangle() {
        SpotLight light = new SpotLight(
                new Color(500, 500, 500),
                new Point(0, 0, 10),
                new Vector(0, 0, -1)
        ).setBlackboard(
                new Blackboard()
                        .setSize(20)
                        .setGridSize(3)
                        .setShape(SamplingShape.RECTANGLE)
                        .setPattern(SamplingPattern.REGULAR)
        );

        Point point = new Point(0, 0, 0);

        List<LightSample> samples = light.getSamples(point);

        assertEquals(9, samples.size(), "3x3 spotlight sampling should create 9 samples");

        for (LightSample sample : samples) {
            assertEquals(1, sample.l().length(), DELTA, "Sample direction should be normalized");
        }
    }
}