package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a light source with constant intensity coming from a single direction.
 */
public class DirectionalLight extends Light implements LightSource {

    /**
     * The direction of the directional light.
     */
    private final Vector _direction;

    /**
     * Constructs a DirectionalLight with the specified intensity and direction.
     *
     * @param intensity the color representing the intensity of the light
     * @param direction the vector representing the direction of the light
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        _direction = direction.normalize();
    }

    @Override
    public Vector getL(Point p) {
        return _direction;
    }

    @Override
    public Color getIntensity(Point p) {
        return _intensity;
    }

    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY; // because there are no attenuation factors for directional light
    }
}
