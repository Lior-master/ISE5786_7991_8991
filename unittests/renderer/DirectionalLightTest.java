package renderer;

import lighting.DirectionalLight;
import lighting.LightSource;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for class {@link DirectionalLight}.
 * <p>
 * These tests verify the implementation of the {@link LightSource}
 * interface methods:
 * {@link LightSource#getL(Point)} and {@link LightSource#getIntensity(Point)}.
 * </p>
 */
class DirectionalLightTests {

    /**
     * Default constructor to satisfy documentation tools.
     */
    DirectionalLightTests() {
        // Default constructor
    }

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Original light intensity used in the tests.
     */
    private static final Color INTENSITY = new Color(100, 150, 200);

    /**
     * Non-normalized direction used to verify normalization.
     */
    private static final Vector DIRECTION = new Vector(1, 2, 2);

    /**
     * First point used to verify that directional light is point-independent.
     */
    private static final Point POINT_1 = new Point(1, 2, 3);

    /**
     * Second point used to verify that directional light is point-independent.
     */
    private static final Point POINT_2 = new Point(-4, 5, -6);

    /**
     * Error message for wrong light direction.
     */
    private static final String ERROR_DIRECTION = "Wrong directional light direction";

    /**
     * Error message for wrong light intensity.
     */
    private static final String ERROR_INTENSITY = "Wrong directional light intensity";

    /**
     * Test method for {@link DirectionalLight#getL(Point)}.
     */
    @Test
    void testGetL() {
        LightSource light = new DirectionalLight(INTENSITY, DIRECTION);
        Vector expectedDirection = DIRECTION.normalize();

        // ============ Equivalence Partitions Tests ==============

        // EP01: Directional light direction is constant and normalized
        Vector result1 = light.getL(POINT_1);
        assertEquals(expectedDirection, result1, ERROR_DIRECTION);
        assertEquals(1, result1.length(), DELTA, "Directional light direction is not normalized");

        // EP02: Directional light direction does not depend on the point
        Vector result2 = light.getL(POINT_2);
        assertEquals(expectedDirection, result2, ERROR_DIRECTION);
    }

    /**
     * Test method for {@link DirectionalLight#getIntensity(Point)}.
     */
    @Test
    void testGetIntensity() {
        LightSource light = new DirectionalLight(INTENSITY, DIRECTION);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Directional light intensity is constant and does not depend on the point
        assertEquals(INTENSITY.getColor(), light.getIntensity(POINT_1).getColor(), ERROR_INTENSITY);

        // EP02: Another point receives the same intensity
        assertEquals(INTENSITY.getColor(), light.getIntensity(POINT_2).getColor(), ERROR_INTENSITY);
    }
}