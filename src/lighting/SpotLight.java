package lighting;

import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Vector;
import renderer.sampling.Blackboard;

import static primitives.Util.alignZero;

/**
 * Represents a spotlight light source in the scene.
 * <p>
 * A spotlight is a point light with an additional direction.
 * The intensity depends on both distance attenuation and the angle
 * between the spotlight direction and the direction to the illuminated point.
 * </p>
 */
public class SpotLight extends PointLight {

    /**
     * The direction in which the spotlight emits light.
     */
    private final Vector _direction;

    /**
     * Constructs a spotlight with the given intensity, position, direction,
     * and attenuation factors.
     *
     * @param intensity the original light intensity
     * @param position  the position of the spotlight
     * @param direction the direction in which the spotlight emits light
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        _direction = direction.normalize();
    }

    @Override
    public Vector getL(Point p) {
        return super.getL(p);
    }

    @Override
    public Color getIntensity(Point p) {
        Color pointLightIntensity = super.getIntensity(p);

        // Boundary case: point coincides with the light position.
        // getL(p) would create a zero vector, so we return the point-light intensity.
        if (p.equals(_position)) {
            return pointLightIntensity;
        }

        double projection = alignZero(_direction.dotProduct(getL(p)));

        return projection <= 0
                ? Color.BLACK
                : pointLightIntensity.scale(new Double3(projection));
    }

    @Override
    public SpotLight setKc(double kC) {
        return (SpotLight) super.setKc(kC);
    }

    @Override
    public SpotLight setKl(double kL) {
        return (SpotLight) super.setKl(kL);
    }

    @Override
    public SpotLight setKq(double kQ) {
        return (SpotLight) super.setKq(kQ);
    }

    @Override
    protected Vector getSamplingNormal(Point p) {
        return _direction;
    }

    @Override
    protected Color getIntensityFrom(Point point, Point samplePoint) {
        Vector l = point.subtract(samplePoint).normalize();

        double factor = alignZero(_direction.dotProduct(l));

        if (factor <= 0) {
            return Color.BLACK;
        }

        return super.getIntensityFrom(point, samplePoint).scale(factor);
    }

    @Override
    public SpotLight setBlackboard(Blackboard blackboard) {
        super.setBlackboard(blackboard);
        return this;
    }
}