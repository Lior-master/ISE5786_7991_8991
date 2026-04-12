package primitives;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ray other = (Ray) obj;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    @Override
    public String toString() {
        return "Ray:" + _origin + _direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }
}