package lighting;

import primitives.Color;

/**
 * Base class for all light types in the lighting system.
 */
abstract class Light {
    /**
     * The intensity (color) of the ambient light.
     */
    protected final Color _intensity;

    /**
     * Returns the intensity of this ambient light.
     *
     * @return the color representing the ambient light intensity
     */
    public Color getIntensity() {
        return _intensity;
    }

    /**
     * Basic constructor for light sources, initializing the intensity.
     *
     * @param intensity to set intensity
     */
    /**
     * Constructs a light with the given intensity.
     *
     * @param intensity the light intensity color
     */
    protected Light(Color intensity) {
        _intensity = intensity;
    }
}
