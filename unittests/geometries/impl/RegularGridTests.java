package geometries.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import geometries.api.Intersectable;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit and integration tests for {@link RegularGrid}.
 * <p>
 * Tests are organized according to EP and BV methodology.
 * </p>
 */
class RegularGridTests {

    /**
     * Accuracy for point comparison.
     */
    private static final double DELTA = 1e-6;

    /**
     * Default constructor for Javadoc.
     */
    RegularGridTests() { /* to satisfy Javadoc generator */ }

    /**
     * Compare two intersection lists by point coordinates after sorting by distance.
     */
    private static void assertSameIntersectionPoints(
            Ray ray,
            List<Intersectable.Intersection> expected,
            List<Intersectable.Intersection> actual,
            String message
    ) {
        if (expected == null) {
            assertNull(actual, message + " expected no intersections");
            return;
        }

        assertNotNull(actual, message + " actual intersections should not be null");
        assertEquals(expected.size(), actual.size(), message + " wrong intersection count");

        List<Intersectable.Intersection> sortedExpected = new ArrayList<>(expected);
        List<Intersectable.Intersection> sortedActual = new ArrayList<>(actual);

        Comparator<Intersectable.Intersection> byDistance =
                Comparator.comparingDouble(i -> ray.origin().distanceSquared(i.point));

        sortedExpected.sort(byDistance);
        sortedActual.sort(byDistance);

        for (int i = 0; i < sortedExpected.size(); i++) {
            Point p1 = sortedExpected.get(i).point;
            Point p2 = sortedActual.get(i).point;

            assertEquals(p1.x(), p2.x(), DELTA, message + " wrong X at index " + i);
            assertEquals(p1.y(), p2.y(), DELTA, message + " wrong Y at index " + i);
            assertEquals(p1.z(), p2.z(), DELTA, message + " wrong Z at index " + i);
        }
    }

    /**
     * Create a simple baseline Geometries object.
     */
    private static Geometries createBaselineGeometries() {
        return new Geometries(
                new Sphere(new Point(0, 0, 0), 2.0),
                new Sphere(new Point(6, 0, 0), 1.0),
                new Triangle(
                        new Point(-3, -3, 5),
                        new Point(3, -3, 5),
                        new Point(0, 3, 5)
                )
        );
    }

    /**
     * Create the same Geometries object with grid enabled.
     */
    private static Geometries createGridGeometries() {
        Geometries geometries = createBaselineGeometries();
        geometries.enableRegularGrid(new RegularGrid.Config(2.0, 2, 50));
        return geometries;
    }

    // ============ Equivalence Partitions Tests ==============

    /**
     * EP01: Grid and baseline return the same intersections for a ray hitting a sphere.
     */
    @Test
    void testGridMatchesBaselineSphereHit() {
        Geometries baseline = createBaselineGeometries();
        Geometries grid = createGridGeometries();

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Sphere hit"
        );
    }

    /**
     * EP02: Grid and baseline return the same intersections for a ray hitting a triangle.
     */
    @Test
    void testGridMatchesBaselineTriangleHit() {
        Geometries baseline = createBaselineGeometries();
        Geometries grid = createGridGeometries();

        Ray ray = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Triangle hit"
        );
    }

    /**
     * EP03: Grid and baseline return null for a ray missing all finite geometries.
     */
    @Test
    void testGridMatchesBaselineMiss() {
        Geometries baseline = createBaselineGeometries();
        Geometries grid = createGridGeometries();

        Ray ray = new Ray(new Point(-10, 20, 0), new Vector(1, 0, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Ray miss"
        );
    }

    /**
     * EP04: Grid works with mixed finite and infinite geometries.
     */
    @Test
    void testGridMatchesBaselineWithInfiniteGeometry() {
        Geometries baseline = new Geometries(
                new Sphere(new Point(0, 0, 0), 1.0),
                new Plane(new Point(5, 0, 0), new Vector(1, 0, 0))
        );

        Geometries grid = new Geometries(
                new Sphere(new Point(0, 0, 0), 1.0),
                new Plane(new Point(5, 0, 0), new Vector(1, 0, 0))
        );
        grid.enableRegularGrid(new RegularGrid.Config(2.0, 2, 50));

        Ray ray = new Ray(new Point(-5, 0, 0), new Vector(1, 0, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Finite and infinite geometries"
        );
    }

    /**
     * EP05: Large sphere crossing several voxels is not counted multiple times.
     */
    @Test
    void testNoDuplicateIntersectionsForLargeSphere() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 5.0);
        Geometries grid = new Geometries(sphere);
        grid.enableRegularGrid(new RegularGrid.Config(10.0, 10, 100));

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
        List<Intersectable.Intersection> intersections = grid.calcIntersections(ray);

        assertNotNull(intersections, "Ray should hit the sphere");
        assertEquals(2, intersections.size(),
                "Sphere crossing several voxels must still produce exactly 2 intersections");
    }

    /**
     * EP06: Empty geometry collection returns null.
     */
    @Test
    void testEmptyGeometriesReturnNull() {
        Geometries geometries = new Geometries();
        geometries.enableRegularGrid(new RegularGrid.Config(1.0, 1, 50));

        Ray ray = new Ray(new Point(0, 0, -10), new Vector(0, 0, 1));

        assertNull(geometries.calcIntersections(ray), "Empty grid should return null");
    }

    // =============== Boundary Values Tests ==================

    /**
     * BV01: Ray starts inside a finite geometry.
     */
    @Test
    void testRayStartsInsideSphere() {
        Geometries baseline = new Geometries(new Sphere(new Point(0, 0, 0), 2.0));

        Geometries grid = new Geometries(new Sphere(new Point(0, 0, 0), 2.0));
        grid.enableRegularGrid(new RegularGrid.Config(2.0, 2, 50));

        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Ray starts inside sphere"
        );
    }

    /**
     * BV02: Ray starts exactly on the scene AABB boundary.
     */
    @Test
    void testRayStartsOnSceneBoundary() {
        Geometries baseline = new Geometries(new Sphere(new Point(0, 0, 0), 2.0));

        Geometries grid = new Geometries(new Sphere(new Point(0, 0, 0), 2.0));
        grid.enableRegularGrid(new RegularGrid.Config(2.0, 2, 50));

        Ray ray = new Ray(new Point(-2, 0, 0), new Vector(1, 0, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Ray starts on scene boundary"
        );
    }

    /**
     * BV03: Ray is parallel to X axis.
     */
    @Test
    void testRayParallelToXAxis() {
        Geometries baseline = createBaselineGeometries();
        Geometries grid = createGridGeometries();

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Ray parallel to X axis"
        );
    }

    /**
     * BV04: Ray is parallel to Y axis.
     */
    @Test
    void testRayParallelToYAxis() {
        Geometries baseline = createBaselineGeometries();
        Geometries grid = createGridGeometries();

        Ray ray = new Ray(new Point(0, -10, 0), new Vector(0, 1, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Ray parallel to Y axis"
        );
    }

    /**
     * BV05: Ray is parallel to Z axis.
     */
    @Test
    void testRayParallelToZAxis() {
        Geometries baseline = createBaselineGeometries();
        Geometries grid = createGridGeometries();

        Ray ray = new Ray(new Point(0, 0, -10), new Vector(0, 0, 1));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Ray parallel to Z axis"
        );
    }

    /**
     * BV06: Scene contains only flat triangles lying on the same plane.
     * This catches zero-size AABB dimensions.
     */
    @Test
    void testFlatSceneWithOnlyTriangles() {
        Geometries baseline = new Geometries(
                new Triangle(
                        new Point(-5, -5, 0),
                        new Point(5, -5, 0),
                        new Point(0, 5, 0)
                ),
                new Triangle(
                        new Point(-5, -5, 2),
                        new Point(5, -5, 2),
                        new Point(0, 5, 2)
                )
        );

        Geometries grid = new Geometries(
                new Triangle(
                        new Point(-5, -5, 0),
                        new Point(5, -5, 0),
                        new Point(0, 5, 0)
                ),
                new Triangle(
                        new Point(-5, -5, 2),
                        new Point(5, -5, 2),
                        new Point(0, 5, 2)
                )
        );
        grid.enableRegularGrid(new RegularGrid.Config(2.0, 2, 50));

        Ray ray = new Ray(new Point(0, 0, -10), new Vector(0, 0, 1));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Flat triangle scene"
        );
    }

    /**
     * BV07: Grid can be enabled and disabled without changing results.
     */
    @Test
    void testEnableDisableGrid() {
        Geometries geometries = createBaselineGeometries();
        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        List<Intersectable.Intersection> baseline = geometries.calcIntersections(ray);

        geometries.enableRegularGrid(new RegularGrid.Config(2.0, 2, 50));
        List<Intersectable.Intersection> withGrid = geometries.calcIntersections(ray);

        geometries.disableRegularGrid();
        List<Intersectable.Intersection> withoutGridAgain = geometries.calcIntersections(ray);

        assertSameIntersectionPoints(ray, baseline, withGrid, "Enable grid");
        assertSameIntersectionPoints(ray, baseline, withoutGridAgain, "Disable grid");
    }

    /**
     * BV08: Ray crosses several grid cells and must not duplicate intersections.
     */
    @Test
    void testRayCrossesManyCellsWithoutDuplicateIntersections() {
        Geometries grid = new Geometries(
                new Sphere(new Point(0, 0, 0), 5.0)
        );
        grid.enableRegularGrid(new RegularGrid.Config(20.0, 20, 200));

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        var intersections = grid.calcIntersections(ray);

        assertNotNull(intersections, "Ray should intersect the sphere");
        assertEquals(2, intersections.size(),
                "A sphere spread across many voxels must still produce exactly 2 intersections");
    }

    /**
     * BV09: Very low density still produces correct results.
     */
    @Test
    void testVeryLowDensityGrid() {
        Geometries baseline = createBaselineGeometries();
        Geometries grid = createBaselineGeometries();

        grid.enableRegularGrid(new RegularGrid.Config(0.1, 1, 10));

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "Low density grid"
        );
    }

    /**
     * BV10: Very high density still produces correct results.
     */
    @Test
    void testVeryHighDensityGrid() {
        Geometries baseline = createBaselineGeometries();
        Geometries grid = createBaselineGeometries();

        grid.enableRegularGrid(new RegularGrid.Config(20.0, 20, 200));

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        assertSameIntersectionPoints(
                ray,
                baseline.calcIntersections(ray),
                grid.calcIntersections(ray),
                "High density grid"
        );
    }

    /**
     * BV11: Invalid density should throw.
     */
    @Test
    void testInvalidConfigDensity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularGrid.Config(0.0, 1, 50),
                "Density must be positive"
        );
    }

    /**
     * BV12: Invalid resolution bounds should throw.
     */
    @Test
    void testInvalidConfigResolutionBounds() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularGrid.Config(1.0, 0, 50),
                "Min resolution must be at least 1"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new RegularGrid.Config(1.0, 10, 5),
                "Max resolution must be greater than or equal to min resolution"
        );
    }

    /**
     * BV: Scene AABB is flat on Z axis.
     * RegularGrid must handle zero-size scene dimensions without division by zero.
     */
    @Test
    void testCompletelyFlatSceneOnZAxis() {
        Geometries baseline = new Geometries(
                new Triangle(
                        new Point(-5, -5, 0),
                        new Point(5, -5, 0),
                        new Point(0, 5, 0)
                ),
                new Triangle(
                        new Point(-4, -4, 0),
                        new Point(4, -4, 0),
                        new Point(0, 4, 0)
                )
        );

        Geometries grid = new Geometries(
                new Triangle(
                        new Point(-5, -5, 0),
                        new Point(5, -5, 0),
                        new Point(0, 5, 0)
                ),
                new Triangle(
                        new Point(-4, -4, 0),
                        new Point(4, -4, 0),
                        new Point(0, 4, 0)
                )
        );

        assertDoesNotThrow(
                () -> grid.enableRegularGrid(new RegularGrid.Config(2.0, 2, 50)),
                "RegularGrid construction should handle flat scene AABB"
        );

        Ray ray = new Ray(new Point(0, 0, -10), new Vector(0, 0, 1));

        var baselineIntersections = baseline.calcIntersections(ray);
        var gridIntersections = grid.calcIntersections(ray);

        assertNotNull(baselineIntersections, "Baseline should intersect flat triangles");
        assertNotNull(gridIntersections, "Grid should intersect flat triangles");
        assertEquals(baselineIntersections.size(), gridIntersections.size(),
                "Grid should find same number of intersections as baseline in flat scene");
    }
}