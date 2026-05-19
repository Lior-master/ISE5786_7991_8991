package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;

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

    protected boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
        intersection.light = light;
        intersection.l = light.getL(intersection.point);
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }
}