package geometries.api;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class representing a geometric shape in 3D space.
 * All concrete geometry classes must implement this interface.
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public abstract class Geometry extends Intersectable {

    private Color _emission = Color.BLACK; // Default emission color is black (no emission)

    public Color getEmission() {
        return _emission;
    }

    public Geometry setEmission(Color _emission) {
        this._emission = _emission;
        return this;
    }

    /**
     * Computes the normal vector to the geometry at a given point on its surface.
     *
     * @param point the point on the surface of the geometry
     * @return the normalized normal {@link Vector} at the given point
     */
    public abstract Vector getNormal(Point point);
}