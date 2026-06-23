package primitives;

/**
 * Axis-Aligned Bounding Box (AABB) utility used for acceleration structures.
 */
public final class AABB {
    public final Point min;
    public final Point max;

    public AABB(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    public static AABB union(AABB a, AABB b) {
        if (a == null) return b;
        if (b == null) return a;
        Point min = new Point(
                Math.min(a.min.x(), b.min.x()),
                Math.min(a.min.y(), b.min.y()),
                Math.min(a.min.z(), b.min.z())
        );
        Point max = new Point(
                Math.max(a.max.x(), b.max.x()),
                Math.max(a.max.y(), b.max.y()),
                Math.max(a.max.z(), b.max.z())
        );
        return new AABB(min, max);
    }

    public boolean isValid() {
        return min.x() <= max.x() && min.y() <= max.y() && min.z() <= max.z();
    }

    public double[] intersect(Ray ray) {
        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        double[] ro = {ray.origin().x(), ray.origin().y(), ray.origin().z()};
        double[] rd = {ray.direction().x(), ray.direction().y(), ray.direction().z()};
        double[] bMin = {min.x(), min.y(), min.z()};
        double[] bMax = {max.x(), max.y(), max.z()};

        for (int i = 0; i < 3; ++i) {
            double origin = ro[i];
            double dir = rd[i];

            if (primitives.Util.isZero(dir)) {
                // Ray is parallel to this slab.
                // If origin is outside the slab, there is no intersection.
                if (origin < bMin[i] || origin > bMax[i]) {
                    return null;
                }
            } else {
                double inv = 1.0 / dir;

                double t1 = (bMin[i] - origin) * inv;
                double t2 = (bMax[i] - origin) * inv;

                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                if (t1 > tMin) {
                    tMin = t1;
                }

                if (t2 < tMax) {
                    tMax = t2;
                }

                if (tMin > tMax) {
                    return null;
                }
            }
        }

        // The whole box is behind the ray origin.
        if (tMax <= 0) {
            return null;
        }

        return new double[]{tMin, tMax};
    }
}
