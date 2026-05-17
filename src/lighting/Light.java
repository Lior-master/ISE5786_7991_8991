package lighting;

import primitives.Color;

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
    protected Light(Color intensity) {
        _intensity = intensity;
    }
}
