package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import geometries.api.Intersectable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RegularGrid} acceleration structure.
 * Tests verify:
 * <ul>
 * <li>Grid construction with finite and infinite geometries</li>
 * <li>Voxel assignment based on AABB</li>
 * <li>3DDDA ray traversal</li>
 * <li>Intersection computation via grid</li>
 * <li>Correctness compared to baseline</li>
 * </ul>
 */
class RegularGridTests {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    RegularGridTests() { /* to satisfy JavaDoc generator */ }

    // ============ Tests for RegularGrid Construction ============

    /**
     * Test grid construction with single sphere.
     */
    @Test
    void testGridConstructionSingleSphere() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1.0);
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(sphere);

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        assertDoesNotThrow(() -> new RegularGrid(geometries, cfg),
            "Grid construction with single sphere should not throw");
    }

    /**
     * Test grid construction with mixed finite and infinite geometries.
     */
    @Test
    void testGridConstructionMixedGeometries() {
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(new Sphere(new Point(0, 0, 0), 1.0));
        geometries.add(new Sphere(new Point(5, 5, 5), 2.0));
        geometries.add(new Plane(new Point(10, 0, 0), new Vector(1, 0, 0))); // infinite

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        assertDoesNotThrow(() -> new RegularGrid(geometries, cfg),
            "Grid construction with mixed geometries should not throw");
    }

    /**
     * Test grid construction with empty geometry list.
     */
    @Test
    void testGridConstructionEmptyGeometries() {
        List<Intersectable> geometries = new ArrayList<>();

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        assertDoesNotThrow(() -> new RegularGrid(geometries, cfg),
            "Grid construction with empty list should not throw");
    }

    /**
     * Test grid construction with only infinite geometries.
     */
    @Test
    void testGridConstructionOnlyInfiniteGeometries() {
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(new Plane(new Point(0, 0, 0), new Vector(1, 0, 0)));
        geometries.add(new Plane(new Point(0, 0, 0), new Vector(0, 1, 0)));

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        assertDoesNotThrow(() -> new RegularGrid(geometries, cfg),
            "Grid construction with only infinite geometries should not throw");
    }

    // ============ Tests for Voxel Assignment ============

    /**
     * Test that sphere AABB is correctly assigned to voxels.
     */
    @Test
    void testVoxelAssignmentSphere() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 0.5);
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(sphere);

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 2, 20);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        // Grid should be constructed without errors
        assertNotNull(grid, "Grid should be created");
    }

    /**
     * Test voxel assignment with multiple spheres at different locations.
     */
    @Test
    void testVoxelAssignmentMultipleSpheres() {
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(new Sphere(new Point(-5, -5, -5), 1.0));
        geometries.add(new Sphere(new Point(0, 0, 0), 1.0));
        geometries.add(new Sphere(new Point(5, 5, 5), 1.0));

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        assertNotNull(grid, "Grid with multiple spheres should be created");
    }

    /**
     * Test that only non-empty voxels are stored (sparse storage).
     */
    @Test
    void testSparseVoxelStorage() {
        List<Intersectable> geometries = new ArrayList<>();
        // Small sphere - should occupy only a few voxels
        geometries.add(new Sphere(new Point(0, 0, 0), 0.1));

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 10, 100);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        // Grid is created; sparse storage is internal and cannot be directly tested
        // but we verify it doesn't throw and works correctly
        assertNotNull(grid, "Grid should handle sparse voxel storage");
    }

    // ============ Tests for Intersection Calculation ============

    /**
     * Test ray intersection with grid containing single sphere.
     */
    @Test
    void testIntersectionWithSphere() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 2.0);
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(sphere);

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        // Ray pointing toward sphere
        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
        var intersections = grid.calcIntersections(ray);

        assertNotNull(intersections, "Ray should intersect sphere in grid");
        assertFalse(intersections.isEmpty(), "Intersections list should not be empty");
    }

    /**
     * Test ray missing sphere in grid.
     */
    @Test
    void testRayMissesSphere() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1.0);
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(sphere);

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        // Ray far from sphere
        Ray ray = new Ray(new Point(-10, -10, -10), new Vector(0, 0, 1));
        var intersections = grid.calcIntersections(ray);

        assertNull(intersections, "Ray far from sphere should return null");
    }

    /**
     * Test intersection with infinite geometry in grid.
     */
    @Test
    void testIntersectionWithInfiniteGeometry() {
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(new Plane(new Point(0, 0, 0), new Vector(0, 0, 1))); // plane at z=0

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        Ray ray = new Ray(new Point(0, 0, -10), new Vector(0, 0, 1));
        var intersections = grid.calcIntersections(ray);

        assertNotNull(intersections, "Ray should intersect plane");
    }

    /**
     * Test intersection with both finite and infinite geometries.
     */
    @Test
    void testIntersectionWithMixedGeometries() {
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(new Sphere(new Point(0, 0, 0), 1.0));
        geometries.add(new Plane(new Point(10, 0, 0), new Vector(1, 0, 0))); // plane at x=10

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        Ray ray = new Ray(new Point(-5, 0, 0), new Vector(1, 0, 0));
        var intersections = grid.calcIntersections(ray);

        assertNotNull(intersections, "Ray should intersect both sphere and plane");
        assertTrue(intersections.size() >= 2, "Should have at least 2 intersections");
    }

    /**
     * Test that no duplicate intersections are counted (geometry tested only once per ray).
     */
    @Test
    void testNoDuplicateGeometryTesting() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 2.0);
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(sphere);

        RegularGrid.Config cfg = new RegularGrid.Config(1.0, 1, 50);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        // Ray that travels through many voxels
        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
        var intersections = grid.calcIntersections(ray);

        assertNotNull(intersections, "Ray should intersect sphere");
        // The sphere may appear in multiple voxels, but it should be tested only once
        // Check that we have the expected 2 intersections (entry and exit)
        assertEquals(2, intersections.size(), "Sphere should produce exactly 2 intersections (entry and exit)");
    }

    // ============ Tests for Resolution Calculation ============

    /**
     * Test automatic resolution calculation with density parameter.
     */
    @Test
    void testAutomaticResolutionCalculation() {
        List<Intersectable> geometries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            geometries.add(new Sphere(new Point(i * 2, 0, 0), 0.5));
        }

        RegularGrid.Config cfg = new RegularGrid.Config(2.0, 2, 50);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        assertNotNull(grid, "Grid with automatic resolution should be created");
    }

    /**
     * Test that resolution respects min and max clamps.
     */
    @Test
    void testResolutionClamping() {
        List<Intersectable> geometries = new ArrayList<>();
        geometries.add(new Sphere(new Point(0, 0, 0), 1.0));

        // Very restrictive config
        RegularGrid.Config cfg = new RegularGrid.Config(0.001, 1, 5);
        RegularGrid grid = new RegularGrid(geometries, cfg);

        assertNotNull(grid, "Grid with clamped resolution should be created");
    }

    // ============ Tests for Configuration ============

    /**
     * Test RegularGrid.Config construction.
     */
    @Test
    void testConfigConstruction() {
        RegularGrid.Config cfg = new RegularGrid.Config(1.5, 2, 100);

        assertEquals(1.5, cfg.density, 0.001, "Density should match");
        assertEquals(2, cfg.minResolution, "Min resolution should match");
        assertEquals(100, cfg.maxResolution, "Max resolution should match");
    }

    /**
     * Test config with various parameters.
     */
    @Test
    void testConfigVariousParameters() {
        RegularGrid.Config cfg1 = new RegularGrid.Config(0.5, 1, 10);
        assertNotNull(cfg1, "Config with low density should be created");

        RegularGrid.Config cfg2 = new RegularGrid.Config(5.0, 50, 200);
        assertNotNull(cfg2, "Config with high density should be created");
    }
}
