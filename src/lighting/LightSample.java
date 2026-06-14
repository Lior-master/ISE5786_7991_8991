package lighting;

import primitives.Color;
import primitives.Vector;

/**
 * Represents one sampled light contribution.
 *
 * @param l         direction from the sampled light point to the shaded point
 * @param distance  distance from the shaded point to the sampled light point
 * @param intensity light intensity coming from this sampled point
 */
public record LightSample(Vector l, double distance, Color intensity) {
}