package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import geometries.api.Intersectable;
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
        if (geometries != null) {
            addAll(this.geometries, geometries);
        }

    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersection> intersections = null;

        for (Intersectable geometry : geometries) {
            var geoIntersections = geometry.calcIntersections(ray);

            if (geoIntersections != null && !geoIntersections.isEmpty()) {
                if (intersections == null) {
                    // Create only when we actually have first intersection points
                    intersections = new ArrayList<>(geoIntersections);
                } else {
                    intersections.addAll(geoIntersections);
                }
            }
        }

        return intersections; // null if nothing found
    }
}
