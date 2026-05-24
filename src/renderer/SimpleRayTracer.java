package renderer;

import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static geometries.api.Intersectable.Intersection;
import static primitives.Util.alignZero;

/**
 * A simple ray tracer implementation.
 * <p>
 * This class computes the color of rays by finding their closest intersection
 * with the scene geometries and evaluating the local lighting effects,
 * shadows, and global effects such as reflection and transparency.
 * </p>
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Small offset used to move the beginning of secondary rays away from the
     * surface.
     * <p>
     * This prevents numerical precision problems such as self-shadowing or
     * immediately intersecting the same geometry again.
     * </p>
     */
    private static final double DELTA = 0.1;

    /**
     * Maximum recursion depth for recursive color calculations.
     * <p>
     * This prevents infinite recursion when rays repeatedly reflect or pass
     * through transparent objects.
     * </p>
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /**
     * Minimum contribution factor for recursive color calculations.
     * <p>
     * If the accumulated contribution becomes smaller than this value, the
     * recursive calculation stops because its effect on the final color is
     * negligible.
     * </p>
     */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /**
     * Initial contribution factor for recursive color calculations.
     * <p>
     * At the beginning, the ray has full contribution.
     * </p>
     */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructs a simple ray tracer for the given scene.
     *
     * @param scene the scene to render
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray through the scene and returns the computed color.
     * <p>
     * If the ray does not intersect any geometry, the scene background color is
     * returned. Otherwise, the color is calculated at the closest intersection.
     * </p>
     *
     * @param ray the ray to trace
     * @return the color seen along the given ray
     */
    @Override
    public Color traceRay(Ray ray) {
        Intersection intersection = findClosestIntersection(ray);

        return intersection == null
                ? _scene.background
                : calcColor(intersection, ray.direction());
    }

    /**
     * Computes the color at a given intersection.
     * <p>
     * This method prepares the geometric data needed for lighting calculations,
     * adds the ambient light contribution, and then delegates the rest of the
     * calculation to the recursive color method.
     * </p>
     *
     * @param intersection the intersection point with a geometry
     * @param v            the direction of the ray that caused the intersection
     * @return the computed color at the intersection
     */
    private Color calcColor(Intersection intersection, Vector v) {
        return !preprocessIntersection(intersection, v)
                ? Color.BLACK
                : _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
    }

    /**
     * Recursively computes the color at an intersection.
     * <p>
     * The color is composed of local effects and, if the recursion level allows
     * it, global effects such as transparency and reflection.
     * </p>
     *
     * @param intersection the intersection point with a geometry
     * @param level        the remaining recursion depth
     * @param k            the accumulated contribution factor
     * @return the computed color at the intersection
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcColorLocalEffect(intersection);

        return level == 1
                ? color
                : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Computes the local lighting effects at an intersection.
     * <p>
     * Local effects include the geometry emission color and the contribution of
     * each light source using the diffuse and specular components of the Phong
     * reflection model. A light contributes only if the point is not shadowed
     * with respect to that light source.
     * </p>
     *
     * @param intersection the intersection point with a geometry
     * @return the color contribution of local lighting effects
     */
    private Color calcColorLocalEffect(Intersection intersection) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource) && unshaded(intersection)) {
                color = color.add(
                        lightSource.getIntensity(intersection.point)
                                .scale(calcDiffuse(intersection).add(calcSpecular(intersection)))
                );
            }
        }

        return color;
    }

    /**
     * Computes the diffuse reflection component at an intersection.
     * <p>
     * The diffuse component depends on the material diffuse coefficient and on
     * the angle between the light direction and the surface normal.
     * </p>
     *
     * @param intersection the intersection point with a geometry
     * @return the diffuse reflection factor
     */
    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    /**
     * Computes the specular reflection component at an intersection.
     * <p>
     * The specular component represents the shiny highlight created by the Phong
     * reflection model.
     * </p>
     *
     * @param intersection the intersection point with a geometry
     * @return the specular reflection factor
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(
                intersection.normal.scale(2 * intersection.lNormal)
        );

        double minusVR = alignZero(-intersection.v.dotProduct(r));

        return minusVR <= 0
                ? Double3.ZERO
                : intersection.material.kS.scale(
                Math.pow(minusVR, intersection.material.nShininess)
        );
    }

    /**
     * Checks whether a point is unshaded with respect to the current light
     * source stored in the intersection.
     * <p>
     * A shadow ray is sent from the intersection point toward the light source.
     * If another geometry intersects this ray before the light source, the point
     * is considered shadowed.
     * </p>
     *
     * @param intersection the intersection point being tested
     * @return {@code true} if the light reaches the point, {@code false}
     * otherwise
     */
    private boolean unshaded(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);

        Vector delta = intersection.normal.scale(
                intersection.lNormal < 0 ? DELTA : -DELTA
        );

        Ray shadowRay = new Ray(intersection.point.add(delta), pointToLight);

        var shadowIntersections = _scene.geometries.findIntersections(shadowRay);

        if (shadowIntersections == null) {
            return true;
        }

        double lightDistance = intersection.light.getDistance(intersection.point);

        for (var shadowIntersection : shadowIntersections) {
            if (alignZero(shadowIntersection.distance(intersection.point) - lightDistance) <= 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Constructs a transparency ray from the current intersection.
     * <p>
     * In this project, transparency is modeled as a ray that continues in the
     * same direction as the incoming ray, without physical refraction.
     * </p>
     *
     * @param intersection the intersection point where the transparency ray
     *                     starts
     * @return the constructed transparency ray
     */
    private Ray constructTransparencyRay(Intersection intersection) {
        Vector delta = intersection.normal.scale(
                intersection.vNormal > 0 ? DELTA : -DELTA
        );

        return new Ray(intersection.point.add(delta), intersection.v);
    }

    /**
     * Constructs a reflection ray from the current intersection.
     * <p>
     * The reflection direction is calculated from the incoming ray direction and
     * the surface normal.
     * </p>
     *
     * @param intersection the intersection point where the reflection ray starts
     * @return the constructed reflection ray
     */
    private Ray constructReflectionRay(Intersection intersection) {
        Vector r = intersection.v.subtract(
                intersection.normal.scale(2 * intersection.vNormal)
        );

        Vector delta = intersection.normal.scale(
                r.dotProduct(intersection.normal) > 0 ? DELTA : -DELTA
        );

        return new Ray(intersection.point.add(delta), r);
    }

    /**
     * Computes the color contribution of a single global effect.
     * <p>
     * A global effect can be either reflection or transparency. The method traces
     * the secondary ray, finds its closest intersection, and recursively computes
     * the color contribution.
     * </p>
     *
     * @param secondaryRay the reflection or transparency ray
     * @param level        the remaining recursion depth
     * @param k            the accumulated contribution factor
     * @param coefficient  the material coefficient of the current global effect
     * @return the color contribution of the global effect
     */
    private Color calcGlobalEffect(Ray secondaryRay, int level, Double3 k, Double3 coefficient) {
        Double3 kkx = k.product(coefficient);

        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }

        Intersection intersection = findClosestIntersection(secondaryRay);

        if (intersection == null) {
            return _scene.background.scale(coefficient);
        }


        return preprocessIntersection(intersection, secondaryRay.direction())
                ? calcColor(intersection, level - 1, kkx).scale(coefficient)
                : Color.BLACK;
    }

    /**
     * Computes all global effects at an intersection.
     * <p>
     * The method computes the transparency contribution and the reflection
     * contribution, then returns their sum.
     * </p>
     *
     * @param intersection the intersection point with a geometry
     * @param level        the remaining recursion depth
     * @param k            the accumulated contribution factor
     * @return the total color contribution of global effects
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        return calcGlobalEffect(
                constructTransparencyRay(intersection),
                level,
                k,
                intersection.material.kT
        ).add(
                calcGlobalEffect(
                        constructReflectionRay(intersection),
                        level,
                        k,
                        intersection.material.kR
                )
        );
    }

    private Intersection findClosestIntersection(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);

        return ray.findClosestIntersection(intersections);
    }
}