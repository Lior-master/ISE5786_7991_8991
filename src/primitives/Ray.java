package primitives;

import java.util.List;
import java.util.Objects;

import static geometries.api.Intersectable.Intersection;
import static primitives.Util.isZero;

/**
 * Represents an immutable geometric ray in 3D space.
 * <p>
 * A ray is defined by an origin point and a direction vector. The direction is
 * normalized in the constructor so each {@code Ray} keeps a unit direction.
 * </p>
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public class Ray {

    /**
     * Starting point of the ray.
     */
    private final Point _origin;

    /**
     * Unit direction vector of the ray.
     */
    private final Vector _direction;

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
     * Constructs a ray from an origin point and a direction vector.
     * <p>
     * The given direction is normalized and stored as a unit vector.
     * </p>
     *
     * @param _origin    starting point of the ray
     * @param _direction direction of the ray (will be normalized)
     */
    public Ray(Point _origin, Vector _direction) {
        this._origin = _origin;
        this._direction = _direction.normalize();
    }

    public Ray(Point origin, Vector direction, Vector normal) {
        Vector delta = normal.scale(isZero(normal.dotProduct(direction)) ? DELTA : DELTA * Math.signum(normal.dotProduct(direction)));
        this._origin = origin.add(delta);
        this._direction = direction.normalize();
    }

    /**
     * Returns the unit direction vector of the ray.
     *
     * @return the normalized direction vector
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Returns the origin point of the ray.
     *
     * @return the starting point of the ray
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Returns a point on the ray line at distance t from the origin.
     *
     * @param t the signed distance from the ray origin
     * @return the computed point
     */
    public Point getPoint(double t) {
        return isZero(t) ? _origin : _origin.add(_direction.scale(t));
    }

    /**
     * Finds the closest intersection to the ray origin.
     *
     * @param intersections list of intersections
     * @return closest intersection, or {@code null} if the list is {@code null}
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null) {
            return null;
        }

        Intersection closestIntersection = null;
        double closestDistanceSquared = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            double distanceSquared = _origin.distanceSquared(intersection.point);

            if (distanceSquared < closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                closestIntersection = intersection;
            }
        }

        return closestIntersection;
    }

    /**
     * Finds the point closest to the ray origin from a given list of points.
     *
     * @param points list of points
     * @return the closest point to the ray origin, or null if the list is null
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(
                points.stream()
                        .map(point -> new Intersection(null, point))
                        .toList()
        ).point;
    }

    /**
     * Compares this ray with another object.
     *
     * @param obj object to compare
     * @return {@code true} if both rays have equal origin and direction
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ray other = (Ray) obj;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    /**
     * Returns a string representation of the ray.
     *
     * @return ray as text
     */
    @Override
    public String toString() {
        return "Ray:" + _origin + _direction;
    }

    /**
     * Returns a hash code based on origin and direction.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }
}