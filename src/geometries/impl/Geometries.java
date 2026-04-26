package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

import static java.util.Collections.addAll;

/**
 * Represents a composite geometry that groups multiple {@link Intersectable}
 * objects and allows treating them as a single intersectable entity.
 *
 * @author Halimi Lior
 * @author Nakache Ben
 */
public class Geometries extends Intersectable {

    /**
     * Internal list of all intersectable geometries in the collection.
     */
    private final List<Intersectable> geometries = new ArrayList<>();

    /**
     * Constructs a geometry collection and optionally initializes it
     * with the given intersectable objects.
     *
     * @param geometries one or more intersectable geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more intersectable geometries to this collection.
     *
     * @param geometries one or more intersectable geometries to add
     */
    public void add(Intersectable... geometries) {
        addAll(this.geometries, geometries);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}
