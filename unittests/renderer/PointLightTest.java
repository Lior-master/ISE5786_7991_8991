package renderer;

import lighting.LightSource;
import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link PointLight}.
 * <p>
 * These tests verify the implementation of the {@link LightSource}
 * interface methods:
 * {@link LightSource#getL(Point)} and {@link LightSource#getIntensity(Point)}.
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
        PointLight light = new PointLight(INTENSITY, LIGHT_POSITION, 1, 1, 0);

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
        PointLight light = new PointLight(INTENSITY, LIGHT_POSITION, 1, 1, 0);

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
}