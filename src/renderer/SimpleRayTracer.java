package renderer;

import java.util.List;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

import static geometries.api.Intersectable.Intersection;

/**
 * A minimal ray tracer implementation.
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Creates a simple ray tracer for the given scene.
     *
     * @param scene scene to trace
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Computes the color at a geometry intersection point.
     *
     * @param intersection hit point on a geometry
     * @return color at the intersection
     */
    private Color calcColor(Intersection intersection) {
        return _scene.ambientLight.intensity().add(intersection.geometry.getEmission());
    }

    @Override
    Color traceRay(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return _scene.background;
        }

        var closestIntersection = ray.findClosestIntersection(intersections);

        return calcColor(closestIntersection);
    }
}