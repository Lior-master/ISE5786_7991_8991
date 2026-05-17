package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface for external light sources.
 * A light source can calculate the light direction and intensity
 * reaching a given point in the scene.
 */
public interface LightSource {

    /**
     * Calculates the normalized direction vector from the light source
     * to the given point.
     *
     * @param p the point being illuminated
     * @return normalized vector from the light source to the point
     */
    Vector getL(Point p);

    /**
     * Calculates the light intensity that reaches the given point.
     *
     * @param p the point being illuminated
     * @return light intensity at the given point
     */
    Color getIntensity(Point p);
}