package renderer;

import java.util.List;

import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static geometries.api.Intersectable.Intersection;
import static primitives.Util.alignZero;

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
    private Color calcColor(Intersection intersection, Vector v) {
        return !preprocessIntersection(intersection, v) ? Color.BLACK
                : _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(calcColorLocalEffect(intersection));
    }

    @Override
    Color traceRay(Ray ray) {
        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return _scene.background;
        }

        return calcColor(ray.findClosestIntersection(intersections), ray.direction());
    }


    /**
     * Computes the total color contribution from all local lighting effects at the intersection point.
     * This includes the sum of all light sources' contributions (diffuse and specular components).
     *
     * @param intersection the intersection point on the geometry
     * @return the cumulative color from all local lighting effects
     */
    private Color calcColorLocalEffect(Intersection intersection) {
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                color = color.add(
                        lightSource.getIntensity(intersection.point)
                                .scale(calcDiffuse(intersection).add(calcSpecular(intersection)))
                );
            }
        }
        return color;
    }

    /**
     * Computes the diffuse reflection component of the material for the light source effects.
     * This represents the contribution of diffuse reflection (Lambert's law) at the intersection.
     *
     * @param intersection the intersection point on the geometry
     * @return the diffuse reflection contribution
     */
    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    /**
     * Computes the specular reflection component of the material for the light source effects.
     * This represents the contribution of specular reflection (highlight term) at the intersection.
     *
     * @param intersection the intersection point on the geometry
     * @return the specular reflection contribution
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(
                intersection.normal.scale(2 * intersection.lNormal)
        );

        double minusVR = alignZero(-intersection.v.dotProduct(r));

        return minusVR <= 0
                ? Double3.ZERO
                : intersection.material.kS.scale(Math.pow(minusVR, intersection.material.nShininess));
    }
}