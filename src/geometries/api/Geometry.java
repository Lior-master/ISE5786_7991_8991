package geometries.api;

import primitives.Color;
import primitives.Material;
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

    /**
     * Creates a new geometry instance.
     */
    public Geometry() {/* to satisfy the Javadoc generator */}

    /**
     * Emission color of the geometry.
     */
    private Color _emission = Color.BLACK; // Default emission color is black (no emission)

    /**
     * Returns the emission color.
     *
     * @return emission color
     */
    public Color getEmission() {
        return _emission;
    }

    /**
     * Material coefficients of the geometry.
     */
    private Material _material = new Material(); // Default material with default coefficients

    /**
     * Sets the emission color.
     *
     * @param _emission new emission color
     * @return this geometry
     */
    public Geometry setEmission(Color _emission) {
        this._emission = _emission;
        return this;
    }

    /**
     * Returns the material.
     *
     * @return material settings
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * Sets the material.
     *
     * @param _material new material settings
     * @return this geometry
     */
    public Geometry setMaterial(Material _material) {
        this._material = _material;
        return this;
    }

    /**
     * Computes the normal vector to the geometry at a given point on its surface.
     *
     * @param point the point on the surface of the geometry
     * @return the normalized normal {@link Vector} at the given point
     */
    public abstract Vector getNormal(Point point);

    /**
     * Returns the axis-aligned bounding box for this geometry, or {@code null}
     * if the geometry is infinite or has no meaningful AABB.
     * <p>
     * Default implementation returns {@code null}. Concrete finite geometries
     * should override this method and provide their AABB.
     *
     * @return the AABB of the geometry, or {@code null} if not applicable
     */
    public primitives.AABB getAABB() {
        return null;
    }

}