package lighting;

import primitives.Color;

/**
 * Represents the ambient light in the lighting system.
 * <p>
 * Ambient light provides a global, uniform illumination that affects all
 * geometries in the scene equally. Instances of this class are immutable.
 *
 * @author Halimi Lior and Nakache Ben
 */
public final class AmbientLight {
    /**
     * The intensity (color) of the ambient light.
     */
    private final Color _intensity;

    /**
     * A constant representing no ambient light (black / zero intensity).
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Creates a new ambient light with the given intensity.
     *
     * @param intensity the color/intensity of the ambient light (must not be null)
     */
    public AmbientLight(Color intensity) {
        _intensity = intensity;
    }

    /**
     * Returns the intensity of this ambient light.
     *
     * @return the color representing the ambient light intensity
     */
    public Color intensity() {
        return _intensity;
    }
}
