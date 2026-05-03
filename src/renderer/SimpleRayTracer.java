package renderer;

import java.util.List;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

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
    private Color calcColor(Point intersection) {
        return _scene.ambientLight.intensity();
    }

    @Override
    Color traceRay(Ray ray) {
        List<Point> intersections = _scene.geometries.findIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return _scene.background;
        }

        Point closestPoint = intersections.get(0);
        double minDistance = closestPoint.distanceSquared(ray.origin());

        for (int i = 1; i < intersections.size(); i++) {
            Point point = intersections.get(i);
            double distance = point.distanceSquared(ray.origin());
            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = point;
            }
        }

        return calcColor(closestPoint);
    }
}