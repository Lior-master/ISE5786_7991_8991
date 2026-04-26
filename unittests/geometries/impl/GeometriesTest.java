package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for class {@link Geometries}.
 * The tests verify:
 * <ul>
 * <li>{@link Geometries#findIntersections(Ray)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class GeometriesTest {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    GeometriesTest() { /* to satisfy JavaDoc generator */ }

    /**
     * Composite collection used in all intersection tests.
     */
    private static final Geometries GEOMETRIES = new Geometries(
            new Sphere(new Point(0, 0, 3), 1),
            new Plane(new Point(0, 0, 5), new Vector(0, 0, 1)),
            new Triangle(new Point(-1, -1, 1), new Point(1, -1, 1), new Point(0, 1, 1))
    );

    /**
     * Error message for wrong intersections result.
     */
    private static final String ERROR_GEOMETRIES_INTERSECTION = "Wrong geometries intersection result";

    /**
     * Test method for {@link Geometries#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Some geometries are intersected (sphere + plane => 2 points)
        List<Point> resultSome = GEOMETRIES.findIntersections(
                new Ray(new Point(0, 0, 2.5), new Vector(0, 0, 1))
        );
        assertNotNull(resultSome, ERROR_GEOMETRIES_INTERSECTION);
        assertEquals(2, resultSome.size(), ERROR_GEOMETRIES_INTERSECTION);

        // =============== Boundary Values Tests ==================

        // BV01: No geometry is intersected (must return null, not empty list)
        assertNull(
                GEOMETRIES.findIntersections(new Ray(new Point(2, 2, 0), new Vector(1, 0, 0))),
                ERROR_GEOMETRIES_INTERSECTION
        );

        // BV02: Only one geometry is intersected (plane => 1 point)
        List<Point> resultOne = GEOMETRIES.findIntersections(
                new Ray(new Point(2, 2, 0), new Vector(0, 0, 1))
        );
        assertNotNull(resultOne, ERROR_GEOMETRIES_INTERSECTION);
        assertEquals(1, resultOne.size(), ERROR_GEOMETRIES_INTERSECTION);

        // BV03: All geometries are intersected (triangle + sphere(2) + plane => 4 points)
        List<Point> resultAll = GEOMETRIES.findIntersections(
                new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))
        );
        assertNotNull(resultAll, ERROR_GEOMETRIES_INTERSECTION);
        assertEquals(4, resultAll.size(), ERROR_GEOMETRIES_INTERSECTION);
    }
}