package primitives;

public class Vector extends Point {

    public Vector(double x, double y, double z) {
        if (x == 0 && y == 0 && z == 0)
            throw new IllegalArgumentException("It impossible to create a vector with zero values");

        super(x, y, z);
    }

    public Vector(Double3 xyz) {
        if (xyz.equals(new Double3(0.0, 0.0, 0.0)))
            throw new IllegalArgumentException("It impossible to create a vector with zero values");
        super(xyz);
    }
}
