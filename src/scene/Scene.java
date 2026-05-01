package scene;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import primitives.Color;

/**
 * Represents a scene containing geometries and lighting settings.
 * <p>
 * A Scene groups the scene name, background color, ambient light and the
 * collection of geometries present in the scene. The class provides fluent
 * setters to simplify scene construction.
 * </p>
 * Note: fields are public for convenience in the original project design (PDS Class);
 * they can be changed directly or via the provided setter methods.
 *
 * @author Halimi Lior & Nakache Ben
 */
public class Scene {
    /**
     * The scene name (identifier).
     */
    public String name;

    /**
     * Background color used when a ray misses all geometries.
     */
    public Color background = Color.BLACK;

    /**
     * Ambient light that uniformly illuminates the scene.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * Collection of geometries that belong to the scene.
     */
    public Geometries geometries = new Geometries();

    /**
     * Constructs a scene with the given name.
     *
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the scene background color.
     *
     * @param background the background color to set
     * @return this scene (for chaining)
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light for the scene.
     *
     * @param ambientLight the ambient light to set
     * @return this scene (for chaining)
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries collection for the scene.
     *
     * @param geometries the geometries to assign to the scene
     * @return this scene (for chaining)
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}
