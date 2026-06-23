package renderer;

import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;
import geometries.impl.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance and rendering tests for Regular Grid acceleration.
 * Tests verify ray-geometry intersection correctness and performance.
 */
class RegularGridRenderingCorrectnessTests {

    RegularGridRenderingCorrectnessTests() { /* default */ }

    private static Scene createTestScene() {
        Scene scene = new Scene("Test Scene");
        scene.setBackground(new Color(100, 100, 100));
        Geometries geo = new Geometries();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                geo.add(new Sphere(new Point(i * 6, j * 6, 0), 2.0));
            }
        }
        geo.add(new Triangle(new Point(-20, -20, 5), new Point(20, -20, 5), new Point(20, 20, 5)));
        geo.add(new Triangle(new Point(-20, -20, 5), new Point(20, 20, 5), new Point(-20, 20, 5)));
        scene.setGeometries(geo);
        return scene;
    }

    @Test
    void testRayIntersectionsMatch() {
        Scene baseline = createTestScene();
        Scene withGrid = createTestScene();
        withGrid.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));

        Ray[] rays = {
            new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0)),
            new Ray(new Point(0, -10, 0), new Vector(0, 1, 0)),
            new Ray(new Point(0, 0, -10), new Vector(0, 0, 1))
        };

        for (Ray ray : rays) {
            var b = baseline.geometries.calcIntersections(ray);
            var g = withGrid.geometries.calcIntersections(ray);
            if (b == null) {
                assertNull(g, "Mismatch on ray: " + ray);
            } else {
                assertNotNull(g, "Grid missed intersections on ray: " + ray);
                assertEquals(b.size(), g.size(), "Intersection count mismatch");
            }
        }
    }

    @Test
    void testPerformance() {
        Scene baseline = createTestScene();
        Scene withGrid = createTestScene();
        withGrid.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 3, 50));

        Ray ray = new Ray(new Point(-15, -15, -15), new Vector(1, 1, 1).normalize());

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            baseline.geometries.calcIntersections(ray);
        }
        long timeBaseline = System.currentTimeMillis() - t1;

        long t2 = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            withGrid.geometries.calcIntersections(ray);
        }
        long timeGrid = System.currentTimeMillis() - t2;

        System.out.println("Baseline: " + timeBaseline + "ms, Grid: " + timeGrid + "ms");
        assertTrue(timeGrid < timeBaseline * 5, "Grid should not be extremely slow");
    }

    @Test
    void testToggleGridOnOff() {
        Scene scene = createTestScene();
        Ray ray = new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0));

        var before = scene.geometries.calcIntersections(ray);
        scene.geometries.enableRegularGrid(new RegularGrid.Config(1.0, 2, 50));
        var withGrid = scene.geometries.calcIntersections(ray);
        scene.geometries.disableRegularGrid();
        var after = scene.geometries.calcIntersections(ray);

        if (before == null) {
            assertNull(withGrid);
            assertNull(after);
        } else {
            assertNotNull(withGrid);
            assertNotNull(after);
            assertEquals(before.size(), withGrid.size());
            assertEquals(before.size(), after.size());
        }
    }
}
