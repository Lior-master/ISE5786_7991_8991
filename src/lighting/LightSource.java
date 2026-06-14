package lighting;

import java.util.List;

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

    /**
     * Calculates the distances with the given point.
     *
     * @param point the point being illuminated
     * @return the distance from the light source to the given point
     */
    double getDistance(Point point);

    /**
     * Generates a list of light samples for the given point.
     *
     * @param point the point being illuminated
     * @return list of light samples, by default containing a single sample with the light direction, distance, and intensity at the given point
     */
    default List<LightSample> getSamples(Point point) {
        return List.of(new LightSample(
                getL(point),
                getDistance(point),
                getIntensity(point)
        ));
    }
}