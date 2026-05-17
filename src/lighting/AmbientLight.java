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
public final class AmbientLight extends Light {


    /**
     * A constant representing no ambient light (black / zero intensity).
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);


    public AmbientLight(Color intensity) {
        super(intensity);
    }
}
