package lighting;

import java.util.List;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.sampling.Blackboard;

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
     * The blackboard used to generate soft shadow samples.
     */
    private Blackboard _blackboard = new Blackboard();

    /**
     * Constructs a point light with the given intensity, position,
     * and attenuation factors.
     *
     * @param intensity the original light intensity
     * @param position  the position of the light source in the scene
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        _position = position;
    }

    /**
     * setter for kC
     *
     * @param kC value
     * @return this
     */
    public PointLight setKc(double kC) {
        _kC = kC;
        return this;
    }

    /**
     * setter for KL
     *
     * @param kL value
     * @return this
     */
    public PointLight setKl(double kL) {
        _kL = kL;
        return this;
    }

    /**
     * setter for kQ
     *
     * @param kQ value
     * @return this
     */
    public PointLight setKq(double kQ) {
        _kQ = kQ;
        return this;
    }

    /**
     * Sets the blackboard used to generate soft shadow samples.
     *
     * @param blackboard the blackboard configuration to use
     * @return this point light instance
     * @throws IllegalArgumentException if {@code blackboard} is {@code null}
     */
    public PointLight setBlackboard(Blackboard blackboard) {
        if (blackboard == null) {
            throw new IllegalArgumentException("Blackboard cannot be null");
        }
        _blackboard = blackboard;
        return this;
    }

    /**
     * Returns the direction used as the sampling normal for this light.
     *
     * @param point the point being illuminated
     * @return the sampling normal vector
     */
    protected Vector getSamplingNormal(Point point) {
        return getL(point);
    }

    /**
     * Creates a light sample from the light to a sampled point.
     *
     * @param point       the shaded point
     * @param samplePoint the sampled light position
     * @return a light sample containing direction, distance, and intensity
     */
    protected LightSample createLightSample(Point point, Point samplePoint) {
        Vector l = point.subtract(samplePoint).normalize();
        double distance = point.distance(samplePoint);
        Color intensity = getIntensityFrom(point, samplePoint);

        return new LightSample(l, distance, intensity);
    }

    /**
     * Computes the attenuated intensity from this light toward a sample point.
     *
     * @param point       the shaded point
     * @param samplePoint the sampled light position
     * @return the attenuated light intensity
     */
    protected Color getIntensityFrom(Point point, Point samplePoint) {
        double d = point.distance(samplePoint);
        double attenuation = _kC + _kL * d + _kQ * d * d;

        return getIntensity().scale(1.0 / attenuation);
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

    @Override
    public double getDistance(Point point) {
        return _position.distance(point);
    }

    @Override
    public List<LightSample> getSamples(Point point) {
        Vector normal = getSamplingNormal(point);

        Vector axisX = normal.createOrthogonal();
        Vector axisY = normal.crossProduct(axisX).normalize();

        List<Point> samplePoints = _blackboard.generatePoints(_position, axisX, axisY);

        return samplePoints.stream()
                .map(samplePoint -> createLightSample(point, samplePoint))
                .toList();
    }
}