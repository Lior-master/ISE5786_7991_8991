package primitives;

/**
 * Represents an immutable point in 3D Cartesian coordinate space.
 * <p>
 * A {@code Point} is defined by three coordinates (x, y, z) stored internally
 * as a {@link Double3} value. This class serves as the base class for
 * {@link Vector}.
 * </p>
 *
 * @author Halimi Lior
 * @author Ben Nakache
 */
public class Point {
    /**
     * The three coordinate values (x, y, z) of this point.
     */
    protected final Double3 _xyz;

    /**
     * The origin point (0, 0, 0).
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a point from three coordinate values.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     */
    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a point from a {@link Double3} coordinate triple.
     *
     * @param _xyz the coordinate triple
     */
    public Point(Double3 _xyz) {
        this._xyz = _xyz;
    }

    /**
     * Subtracts a point from this point.
     * The result is the vector from {@code other} to {@code this}.
     *
     * @param other the point to subtract
     * @return a vector equal to {@code this - other}
     * @throws IllegalArgumentException when both points are equal (zero vector)
     */
    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }

    /**
     * Adds a vector to this point and returns the resulting point.
     *
     * @param vector the vector to add
     * @return a new {@code Point} equal to {@code this + vector}
     */
    public Point add(Vector vector) {
        return new Point(_xyz.add(vector._xyz));
    }

    /**
     * Computes the squared Euclidean distance between this point and another.
     * <p>
     * Use this method when only comparing distances, as it avoids the cost of
     * computing a square root.
     * </p>
     *
     * @param other the other point
     * @return the squared distance between {@code this} and {@code other}
     */
    public double distanceSquared(Point other) {
        double dx = _xyz._d1() - other._xyz._d1();
        double dy = _xyz._d2() - other._xyz._d2();
        double dz = _xyz._d3() - other._xyz._d3();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Computes the Euclidean distance between this point and another point.
     * <p>
     * This method delegates to {@link #distanceSquared(Point)} and applies a
     * square root to obtain the actual distance.
     * </p>
     *
     * @param other the other point
     * @return the distance between {@code this} and {@code other}
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public String toString() {
        return "" + _xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return _xyz.equals(((Point) obj)._xyz);
    }

    @Override
    public int hashCode() {
        return _xyz.hashCode();
    }
}