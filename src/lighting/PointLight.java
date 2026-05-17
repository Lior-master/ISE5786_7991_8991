package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a point light source in the scene.
 * <p>
 * A point light emits light from a specific position in all directions.
 * Its intensity decreases with distance according to three attenuation
 * factors: constant, linear, and quadratic.
 * </p>
 * <p>
 * This class extends {@link Light}, which stores the original light intensity.
 * </p>
 */
public class PointLight extends Light implements LightSource {

    /**
     * The position of the point light in the scene.
     */
    protected final Point _position;

    /**
     * Constant attenuation factor.
     */
    private double _kC = 1;

    /**
     * Linear attenuation factor.
     */
    private double _kL = 0;

    /**
     * Quadratic attenuation factor.
     */
    private double _kQ = 0;

    /**
     * Constructs a point light with the given intensity, position,
     * and attenuation factors.
     *
     * @param intensity the original light intensity
     * @param position  the position of the light source in the scene
     * @param kC        the constant attenuation factor
     * @param kL        the linear attenuation factor
     * @param kQ        the quadratic attenuation factor
     */
    public PointLight(Color intensity, Point position, double kC, double kL, double kQ) {
        super(intensity);
        _position = position;
        setkC(kC).setkL(kL).setkQ(kQ);
    }

    public PointLight setkC(double kC) {
        _kC = kC;
        return this;
    }

    public PointLight setkL(double kL) {
        _kL = kL;
        return this;
    }

    public PointLight setkQ(double kQ) {
        _kQ = kQ;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double distance = _position.distance(p);
        double attenuation = _kC + _kL * distance + _kQ * distance * distance;
        return _intensity.scale(1.0 / attenuation);
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }
}