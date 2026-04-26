package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents an infinite tube in 3D space.
 * A tube is defined by a central axis ray and a constant radius.
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis ray of the tube.
     */
    protected final Ray _axis;

    /**
     * Constructs a tube with the given radius and axis ray.
     *
     * @param _radius the radius of the tube
     * @param _axis   the axis ray of the tube
     */
    public Tube(double _radius, Ray _axis) {
        super(_radius);
        this._axis = _axis;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector dir = _axis.direction();

        double t = dir.dotProduct(point.subtract(p0));

        Point o = primitives.Util.isZero(t) ? p0 : p0.add(dir.scale(t));

        return point.subtract(o).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Vector va = _axis.direction();
        Vector v = ray.direction();

        double vVa = v.dotProduct(va);
        double a = alignZero(1 - vVa * vVa); // |v - (v·va)va|^2, with |v|=|va|=1

        // Ray parallel to tube axis -> no crossing with an infinite tube surface
        if (isZero(a)) return null;

        double b;
        double c;

        if (ray.origin().equals(_axis.origin())) {
            // DeltaP is zero at axis head, so b and c collapse to this simple form
            b = 0;
            c = -_radiusSquared;
        } else {
            Vector deltaP = ray.origin().subtract(_axis.origin());
            double dpVa = deltaP.dotProduct(va);

            b = alignZero(2 * (v.dotProduct(deltaP) - vVa * dpVa));
            c = alignZero(deltaP.lengthSquared() - dpVa * dpVa - _radiusSquared);
        }

        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant <= 0) return null;

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double denominator = 2 * a;
        double t1 = alignZero((-b - sqrtDiscriminant) / denominator);
        double t2 = alignZero((-b + sqrtDiscriminant) / denominator);

        if (t1 > 0 && t2 > 0) return List.of(ray.getPoint(t1), ray.getPoint(t2));
        if (t1 > 0) return List.of(ray.getPoint(t1));
        return t2 > 0 ? List.of(ray.getPoint(t2)) : null;
    }
}
