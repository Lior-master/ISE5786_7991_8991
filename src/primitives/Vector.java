package primitives;

/**
 * Represents a 3D vector in space.
 * <p>
 * This class extends {@link Point} to reuse coordinate storage.
 * A zero vector (0,0,0) is not allowed.
 * </p>
 */
public class Vector extends Point {

    /**
     * Constructs a vector from its three components.
     *
     * @param x the X component
     * @param y the Y component
     * @param z the Z component
     * @throws IllegalArgumentException if all components are zero
     */
    public Vector(double x, double y, double z) {
        if (x == 0 && y == 0 && z == 0)
            throw new IllegalArgumentException("It impossible to create a vector with zero values");

        super(x, y, z);
    }

    /**
     * Constructs a vector from a {@link Double3} tuple.
     *
     * @param xyz the tuple containing vector components
     * @throws IllegalArgumentException if the tuple represents a zero vector
     */
    public Vector(Double3 xyz) {
        if (xyz.equals(new Double3(0.0, 0.0, 0.0)))
            throw new IllegalArgumentException("It impossible to create a vector with zero values");
        super(xyz);
    }

    /**
     * Adds this vector to another vector.
     *
     * @param v the vector to add
     * @return a new vector equal to the vector sum
     */
    @Override
    public Vector add(Vector v) {
        return new Vector(this._xyz.add(v._xyz));
    }

    /**
     * Multiplies this vector by a scalar.
     *
     * @param scalar the scalar multiplier
     * @return a new scaled vector
     */
    public Vector scale(double scalar) {
        return new Vector(this._xyz.scale(scalar));
    }

    /**
     * Computes the dot product with another vector.
     *
     * @param v the other vector
     * @return the dot product value
     */
    public double dotProduct(Vector v) {
        Double3 result = this._xyz.product(v._xyz);
        return result._d1() + result._d2() + result._d3();
    }

    /**
     * Computes the cross product with another vector.
     *
     * @param v the other vector
     * @return a new vector orthogonal to both vectors
     */
    public Vector crossProduct(Vector v) {
        return new Vector(
                this._xyz._d2() * v._xyz._d3() - this._xyz._d3() * v._xyz._d2(),
                this._xyz._d3() * v._xyz._d1() - this._xyz._d1() * v._xyz._d3(),
                this._xyz._d1() * v._xyz._d2() - this._xyz._d2() * v._xyz._d1()
        );
    }

    /**
     * Computes the squared length of this vector.
     *
     * @return the squared length
     */
    public double lengthSquared() {
        Double3 result = this._xyz.product(this._xyz);
        return result._d1() + result._d2() + result._d3();
    }

    /**
     * Computes the length (magnitude) of this vector.
     *
     * @return the vector length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Returns the normalized (unit-length) vector in the same direction.
     *
     * @return a new normalized vector
     * @throws ArithmeticException if the vector length is zero
     */
    public Vector normalize() {
        double len = length();
        if (Util.isZero(len))
            throw new ArithmeticException("Cannot normalize a zero-length vector");
        return new Vector(this._xyz.divide(len));
    }
}