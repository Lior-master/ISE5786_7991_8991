package primitives;

import java.util.Objects;

/**
 * Represents an immutable geometric ray in 3D space.
 * <p>
 * A ray is defined by an origin point and a direction vector. The direction is
 * normalized in the constructor so each {@code Ray} keeps a unit direction.
 * </p>
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
     * Compares this ray with another object for equality.
     *
     * @param obj object to compare with
     * @return {@code true} if the other object is a ray with equal origin and
     * equal direction; otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ray other = (Ray) obj;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    /**
     * Returns a string representation of this ray.
     *
     * @return textual representation containing origin and direction
     */
    @Override
    public String toString() {
        return "Ray:" + _origin + _direction;
    }

    /**
     * Returns the hash code of this ray.
     *
     * @return hash based on origin and direction
     */
    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }
}