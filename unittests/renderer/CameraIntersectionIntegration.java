package renderer;

import java.util.List;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for camera ray construction and geometry intersections.
 * <p>
 * The tests verify that rays constructed through a 3x3 view plane intersect
 * spheres, planes, and triangles with the expected total number of intersections.
 * </p>
 */
public class CameraIntersectionIntegration {
    /**
     * Default constructor to satisfy documentation tools.
     */
    CameraIntersectionIntegration() {
        /* Default constructor to satisfy documentation tools */
    }

    /**
     * Basic camera located at the origin and looking toward the negative Z axis.
     */
    private final Camera CAMERA_000 = Camera.getBuilder()
            .setLocation(new Point(0, 0, 0))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setResolution(3, 3)
            .setVpSize(3, 3)
            .setVpDistance(1)
            .build();

    /**
     * Camera located at (0,0,0.5) and looking toward the negative Z axis.
     */
    private final Camera CAMERA_000_5 = Camera.getBuilder()
            .setLocation(new Point(0, 0, 0.5))
            .setDirection(new Point(0, 0, -2.5))
            .setResolution(3, 3)
            .setVpSize(3, 3)
            .setVpDistance(1)
            .build();

    /**
     * Sphere for the first sphere integration test, expected to produce 2 intersections.
     */
    private final Sphere SPHERE_2 = new Sphere(new Point(0, 0, -3), 1);

    /**
     * Sphere for the second sphere integration test, expected to produce 18 intersections.
     */
    private final Sphere SPHERE_18 = new Sphere(new Point(0, 0, -2.5), 2.5);

    /**
     * Sphere for the third sphere integration test, expected to produce 10 intersections.
     */
    private final Sphere SPHERE_10 = new Sphere(new Point(0, 0, -2), 2);

    /**
     * Sphere for the fourth sphere integration test, expected to produce 9 intersections.
     */
    private final Sphere SPHERE_9 = new Sphere(new Point(0, 0, 0), 4);

    /**
     * Sphere for the fifth sphere integration test, expected to produce no intersections.
     */
    private final Sphere SPHERE_0 = new Sphere(new Point(0, 0, 1), 0.5);

    /**
     * Plane parallel to the view plane, expected to produce 9 intersections.
     */
    private final Plane PLANE_9_PARALLEL = new Plane(
            new Point(0, 0, -3),
            new Vector(0, 0, 1)
    );

    /**
     * Slightly inclined plane, expected to produce 9 intersections.
     */
    private final Plane PLANE_9_INCLINED = new Plane(
            new Point(0, 0, -3),
            new Vector(0, 1, 2)
    );

    /**
     * More inclined plane, expected to produce 6 intersections.
     */
    private final Plane PLANE_6 = new Plane(
            new Point(0, 0, -3),
            new Vector(0, 1, 1)
    );

    /**
     * Small triangle, expected to produce 1 intersection.
     */
    private final Triangle TRIANGLE_1 = new Triangle(
            new Point(0, 1, -2),
            new Point(1, -1, -2),
            new Point(-1, -1, -2)
    );

    /**
     * Larger triangle, expected to produce 2 intersections.
     */
    private final Triangle TRIANGLE_2 = new Triangle(
            new Point(0, 10, -2),
            new Point(1, -1, -2),
            new Point(-1, -1, -2)
    );

    /**
     * Counts all intersections between rays constructed through the camera view plane
     * and a given geometry, then compares the count with the expected value.
     *
     * @param camera        the camera used to construct rays
     * @param geometry      the geometry tested for intersections
     * @param expectedCount the expected total number of intersections
     * @param message       the assertion failure message
     */
    private void assertIntersectionsCount(Camera camera, Intersectable geometry, int expectedCount, String message) {
        int count = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Ray ray = camera.constructRay(j, i);
                List<Point> intersections = geometry.findIntersections(ray);

                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }

        assertEquals(expectedCount, count, message);
    }

    /**
     * Tests camera ray integration with spheres.
     */
    @Test
    void testCameraRaySphereIntegration() {
        // TC01: Small sphere in front of the camera, only the center ray intersects.
        assertIntersectionsCount(CAMERA_000, SPHERE_2, 2, "TC01: Expected 2 intersections with SPHERE_2");

        // TC02: Large sphere containing all rays, each ray intersects twice.
        assertIntersectionsCount(CAMERA_000_5, SPHERE_18, 18, "TC02: Expected 18 intersections with SPHERE_18");

        // TC03: Medium sphere producing 10 total intersections.
        assertIntersectionsCount(CAMERA_000_5, SPHERE_10, 10, "TC03: Expected 10 intersections with SPHERE_10");

        // TC04: Camera starts inside the sphere, each ray intersects once.
        assertIntersectionsCount(CAMERA_000, SPHERE_9, 9, "TC04: Expected 9 intersections with SPHERE_9");

        // TC05: Sphere is behind the camera, no ray intersects it.
        assertIntersectionsCount(CAMERA_000, SPHERE_0, 0, "TC05: Expected 0 intersections with SPHERE_0");
    }

    /**
     * Tests camera ray integration with planes.
     */
    @Test
    void testCameraRayPlaneIntegration() {
        // TC01: Plane parallel to the view plane, all 9 rays intersect.
        assertIntersectionsCount(CAMERA_000, PLANE_9_PARALLEL, 9,
                "TC01: Expected 9 intersections with PLANE_9_PARALLEL");

        // TC02: Slightly inclined plane, all 9 rays intersect.
        assertIntersectionsCount(CAMERA_000, PLANE_9_INCLINED, 9,
                "TC02: Expected 9 intersections with PLANE_9_INCLINED");

        // TC03: More inclined plane, only 6 rays intersect.
        assertIntersectionsCount(CAMERA_000, PLANE_6, 6,
                "TC03: Expected 6 intersections with PLANE_6");
    }

    /**
     * Tests camera ray integration with triangles.
     */
    @Test
    void testCameraRayTriangleIntegration() {
        // TC01: Small triangle, only the center ray intersects.
        assertIntersectionsCount(CAMERA_000, TRIANGLE_1, 1,
                "TC01: Expected 1 intersection with TRIANGLE_1");

        // TC02: Larger triangle, two rays intersect.
        assertIntersectionsCount(CAMERA_000, TRIANGLE_2, 2,
                "TC02: Expected 2 intersections with TRIANGLE_2");
    }
}