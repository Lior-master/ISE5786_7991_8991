package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;
import geometries.api.Intersectable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Regular Grid acceleration.
 * Tests verify that rendering with and without the grid produces identical results.
 */
class RegularGridIntegrationTests {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    RegularGridIntegrationTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Create a simple test scene with a few geometries.
     */
    private static Scene createTestScene() {
        Scene scene = new Scene("RegularGrid Integration Test");
        
        // Add some geometries
        Sphere sphere1 = new Sphere(new Point(0, 0, 0), 2.0);
        Sphere sphere2 = new Sphere(new Point(5, 5, 5), 1.5);
        Triangle triangle = new Triangle(
            new Point(-3, -3, 0),
            new Point(3, -3, 0),
            new Point(0, 3, 0)
        );
        
        Geometries geoms = new Geometries(sphere1, sphere2, triangle);
        scene.setGeometries(geoms);
        
        return scene;
    }

    // ============ Tests for Correctness ============

    /**
     * Test that ray intersections are identical with and without grid.
     */
    @Test
    void testIntersectionsIdenticalWithoutAndWithGrid() {
        Scene sceneBaseline = createTestScene();
        Scene sceneWithGrid = createTestScene();

        // Enable grid on second scene
        sceneWithGrid.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));

        // Test rays
        Ray[] testRays = {
            new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0)),
            new Ray(new Point(0, -10, 0), new Vector(0, 1, 0)),
            new Ray(new Point(0, 0, -10), new Vector(0, 0, 1)),
            new Ray(new Point(-5, -5, -5), new Vector(1, 1, 1).normalize()),
            new Ray(new Point(10, 10, 10), new Vector(-1, -1, -1).normalize())
        };

        for (Ray ray : testRays) {
            var intersectionsBaseline = sceneBaseline.geometries.calcIntersections(ray);
            var intersectionsWithGrid = sceneWithGrid.geometries.calcIntersections(ray);

            // Check that both have same number of intersections
            if (intersectionsBaseline == null) {
                assertNull(intersectionsWithGrid, "Baseline miss should equal grid miss for ray: " + ray);
            } else {
                assertNotNull(intersectionsWithGrid, "Grid should find intersections when baseline does for ray: " + ray);
                assertEquals(intersectionsBaseline.size(), intersectionsWithGrid.size(),
                    "Same number of intersections expected for ray: " + ray);
            }
        }
    }

    /**
     * Test that closest intersection is the same with and without grid.
     */
    @Test
    void testClosestIntersectionIdentical() {
        Scene sceneBaseline = createTestScene();
        Scene sceneWithGrid = createTestScene();

        sceneWithGrid.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        var intersectionsBaseline = sceneBaseline.geometries.calcIntersections(ray);
        var intersectionsWithGrid = sceneWithGrid.geometries.calcIntersections(ray);

        if (intersectionsBaseline != null) {
            assertNotNull(intersectionsWithGrid, "Grid should find intersections");
            
            var closestBaseline = ray.findClosestIntersection(intersectionsBaseline);
            var closestWithGrid = ray.findClosestIntersection(intersectionsWithGrid);

            assertNotNull(closestBaseline, "Baseline should find closest");
            assertNotNull(closestWithGrid, "Grid should find closest");
            
            assertEquals(closestBaseline.point.x(), closestWithGrid.point.x(), 0.001,
                "Closest intersection X should match");
            assertEquals(closestBaseline.point.y(), closestWithGrid.point.y(), 0.001,
                "Closest intersection Y should match");
            assertEquals(closestBaseline.point.z(), closestWithGrid.point.z(), 0.001,
                "Closest intersection Z should match");
        }
    }

    /**
     * Test that disabling grid reverts to baseline behavior.
     */
    @Test
    void testDisableGridReturnsToBaseline() {
        Scene scene = createTestScene();

        // Enable grid
        scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));
        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
        var intersectionsWithGrid = scene.geometries.calcIntersections(ray);

        // Disable grid
        scene.geometries.disableRegularGrid();
        var intersectionsWithoutGrid = scene.geometries.calcIntersections(ray);

        if (intersectionsWithGrid == null) {
            assertNull(intersectionsWithoutGrid, "After disabling grid, should match no-grid result");
        } else {
            assertNotNull(intersectionsWithoutGrid, "After disabling grid, intersections should remain");
            assertEquals(intersectionsWithGrid.size(), intersectionsWithoutGrid.size(),
                "Size should match after disabling grid");
        }
    }

    // ============ Tests for Edge Cases ============

    /**
     * Test grid with rays starting inside geometries.
     */
    @Test
    void testRaysStartingInsideGeometry() {
        Scene sceneBaseline = createTestScene();
        Scene sceneWithGrid = createTestScene();

        sceneWithGrid.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));

        // Ray starting inside sphere
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));

        var intersectionsBaseline = sceneBaseline.geometries.calcIntersections(ray);
        var intersectionsWithGrid = sceneWithGrid.geometries.calcIntersections(ray);

        if (intersectionsBaseline == null) {
            assertNull(intersectionsWithGrid, "Ray starting inside: baseline miss should match grid miss");
        } else {
            assertNotNull(intersectionsWithGrid, "Ray starting inside: grid should find intersections");
            assertEquals(intersectionsBaseline.size(), intersectionsWithGrid.size(),
                "Ray starting inside: same count expected");
        }
    }

    /**
     * Test grid with very small scene (all geometries in few voxels).
     */
    @Test
    void testSmallScene() {
        Scene scene = new Scene("Small Test Scene");
        
        Sphere sphere = new Sphere(new Point(0, 0, 0), 0.1);
        Geometries geoms = new Geometries(sphere);
        scene.setGeometries(geoms);

        scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 1, 50));

        Ray ray = new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0));
        var intersections = scene.geometries.calcIntersections(ray);

        assertNotNull(intersections, "Small scene should find intersections");
        assertEquals(2, intersections.size(), "Small scene sphere should have 2 intersections");
    }

    /**
     * Test grid with very large scene (many geometries spread apart).
     */
    @Test
    void testLargeSparsScene() {
        Scene scene = new Scene("Large Sparse Scene");
        
        List<Intersectable> geoms = new ArrayList<>();
        // Create spheres spread far apart
        for (int i = 0; i < 5; i++) {
            geoms.add(new Sphere(new Point(i * 100, 0, 0), 5.0));
        }

        Geometries geometries = new Geometries();
        for (Intersectable g : geoms) {
            geometries.add(g);
        }
        scene.setGeometries(geometries);

        scene.geometries.enableRegularGrid(new RegularGrid.Config(0.5, 1, 100));

        Ray ray = new Ray(new Point(0, 0, -10), new Vector(0, 0, 1));
        var intersections = scene.geometries.calcIntersections(ray);

        assertNotNull(intersections, "Large sparse scene should find intersections");
        assertEquals(2, intersections.size(), "Ray should hit one sphere twice");
    }

    /**
     * Test grid with rays parallel to axes (special case for 3DDDA).
     */
    @Test
    void testRaysParallelToAxes() {
        Scene scene = createTestScene();
        scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));

        // Ray parallel to X axis
        Ray rayX = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
        assertDoesNotThrow(() -> scene.geometries.calcIntersections(rayX),
            "Ray parallel to X should not throw");

        // Ray parallel to Y axis
        Ray rayY = new Ray(new Point(0, -10, 0), new Vector(0, 1, 0));
        assertDoesNotThrow(() -> scene.geometries.calcIntersections(rayY),
            "Ray parallel to Y should not throw");

        // Ray parallel to Z axis
        Ray rayZ = new Ray(new Point(0, 0, -10), new Vector(0, 0, 1));
        assertDoesNotThrow(() -> scene.geometries.calcIntersections(rayZ),
            "Ray parallel to Z should not throw");
    }

    /**
     * Test grid with rays perpendicular to axes.
     */
    @Test
    void testRaysDiagonal() {
        Scene scene = createTestScene();
        scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));

        // Diagonal ray
        Ray rayDiag = new Ray(new Point(-10, -10, -10), new Vector(1, 1, 1).normalize());
        assertDoesNotThrow(() -> scene.geometries.calcIntersections(rayDiag),
            "Diagonal ray should not throw");
    }

    // ============ Tests for Configuration Parameters ============

    /**
     * Test grid with very low density (coarse grid).
     */
    @Test
    void testLowDensityGrid() {
        Scene scene = createTestScene();

        // Very coarse grid
        scene.geometries.enableRegularGrid(new RegularGrid.Config(0.1, 1, 10));

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
        var intersections = scene.geometries.calcIntersections(ray);

        assertNotNull(intersections, "Low density grid should still find intersections");
    }

    /**
     * Test grid with very high density (fine grid).
     */
    @Test
    void testHighDensityGrid() {
        Scene scene = createTestScene();

        // Very fine grid
        scene.geometries.enableRegularGrid(new RegularGrid.Config(10.0, 50, 200));

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
        var intersections = scene.geometries.calcIntersections(ray);

        assertNotNull(intersections, "High density grid should find intersections");
    }

    /**
     * Test grid with tight resolution bounds.
     */
    @Test
    void testTightResolutionBounds() {
        Scene scene = createTestScene();

        // Force very specific resolution
        scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 5, 5));

        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));
        var intersections = scene.geometries.calcIntersections(ray);

        assertNotNull(intersections, "Tight bounds grid should find intersections");
    }

    // ============ Tests for Multiple Enable/Disable Cycles ============

    /**
     * Test toggling grid on and off multiple times.
     */
    @Test
    void testMultipleEnableDisableCycles() {
        Scene scene = createTestScene();
        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        var baseline = scene.geometries.calcIntersections(ray);

        // Enable, disable, enable again
        scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));
        var withGrid1 = scene.geometries.calcIntersections(ray);

        scene.geometries.disableRegularGrid();
        var without1 = scene.geometries.calcIntersections(ray);

        scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));
        var withGrid2 = scene.geometries.calcIntersections(ray);

        // All should be compatible
        if (baseline == null) {
            assertNull(withGrid1, "First enable should match baseline");
            assertNull(without1, "First disable should match baseline");
            assertNull(withGrid2, "Second enable should match baseline");
        } else {
            assertNotNull(withGrid1, "First enable should have intersections");
            assertNotNull(without1, "First disable should have intersections");
            assertNotNull(withGrid2, "Second enable should have intersections");
            assertEquals(baseline.size(), withGrid1.size(), "Sizes should match after enable");
            assertEquals(baseline.size(), without1.size(), "Sizes should match after disable");
            assertEquals(baseline.size(), withGrid2.size(), "Sizes should match after re-enable");
        }
    }

    /**
     * Test changing grid configuration.
     */
    @Test
    void testChangingGridConfiguration() {
        Scene scene = createTestScene();
        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        // Different configurations should all work
        var cfg1 = new RegularGrid.Config(0.5, 1, 30);
        scene.geometries.enableRegularGrid(cfg1);
        var result1 = scene.geometries.calcIntersections(ray);

        var cfg2 = new RegularGrid.Config(2.0, 10, 100);
        scene.geometries.enableRegularGrid(cfg2);
        var result2 = scene.geometries.calcIntersections(ray);

        // Both should have same intersections
        if (result1 == null) {
            assertNull(result2, "Different configs should have same result");
        } else {
            assertNotNull(result2, "Different configs should both find intersections");
            assertEquals(result1.size(), result2.size(), "Different configs should have same count");
        }
    }
}
