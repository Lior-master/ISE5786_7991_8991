package lighting;

import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Vector;

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
     * @param kC        the constant attenuation factor
     * @param kL        the linear attenuation factor
     * @param kQ        the quadratic attenuation factor
     */
    public SpotLight(Color intensity, Point position, Vector direction, double kC, double kL, double kQ) {
        super(intensity, position, kC, kL, kQ);
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
    public SpotLight setkC(double kC) {
        return (SpotLight) super.setkC(kC);
    }

    @Override
    public SpotLight setkL(double kL) {
        return (SpotLight) super.setkL(kL);
    }

    @Override
    public SpotLight setkQ(double kQ) {
        return (SpotLight) super.setkQ(kQ);
    }
}