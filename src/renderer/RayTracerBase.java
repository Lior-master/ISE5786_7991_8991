package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Base abstraction for ray tracing strategies.
 */
abstract class RayTracerBase {

    /**
     * Scene used by the ray tracer.
     */
    protected final Scene _scene;

    /**
     * Creates a ray tracer for the given scene.
     *
     * @param scene scene to trace
     */
    RayTracerBase(Scene scene) {
        _scene = scene;
    }

    /**
     * Traces a single ray and returns its resulting color.
     *
     * @param ray ray to trace
     * @return traced color
     */
    abstract Color traceRay(Ray ray);
}