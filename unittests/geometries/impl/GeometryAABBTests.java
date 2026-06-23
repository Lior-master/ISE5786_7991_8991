package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.AABB;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for geometry AABB computation.
 * <p>
 * Tests are organized according to EP and BV methodology.
 * </p>
 */
class GeometryAABBTests {

    /**
     * Accuracy for double comparisons.
     */
    private static final double DELTA = 1e-6;

    /**
     * Default constructor for Javadoc.
     */
    GeometryAABBTests() { /* to satisfy Javadoc generator */ }

    /**
     * Assert AABB coordinates.
     */
    private static void assertAABB(AABB box, Point min, Point max, String message) {
        assertNotNull(box, message + " AABB should not be null");
        assertEquals(min.x(), box.min.x(), DELTA, message + " wrong min X");
        assertEquals(min.y(), box.min.y(), DELTA, message + " wrong min Y");
        assertEquals(min.z(), box.min.z(), DELTA, message + " wrong min Z");
        assertEquals(max.x(), box.max.x(), DELTA, message + " wrong max X");
        assertEquals(max.y(), box.max.y(), DELTA, message + " wrong max Y");
        assertEquals(max.z(), box.max.z(), DELTA, message + " wrong max Z");
        assertTrue(box.isValid(), message + " AABB should be valid");
    }

    // ============ Equivalence Partitions Tests ==============

    /**
     * EP01: Sphere centered at origin.
     */
    @Test
    void testSphereAABBAtOrigin() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 2.0);

        assertAABB(
                sphere.getAABB(),
                new Point(-2, -2, -2),
                new Point(2, 2, 2),
                "Sphere at origin"
        );
    }

    /**
     * EP02: Sphere with arbitrary center.
     */
    @Test
    void testSphereAABBArbitraryCenter() {
        Sphere sphere = new Sphere(new Point(1, -2, 3), 1.5);

        assertAABB(
                sphere.getAABB(),
                new Point(-0.5, -3.5, 1.5),
                new Point(2.5, -0.5, 4.5),
                "Arbitrary sphere"
        );
    }

    /**
     * EP03: Triangle with arbitrary non-axis-aligned vertices.
     */
    @Test
    void testTriangleAABBArbitraryVertices() {
        Triangle triangle = new Triangle(
                new Point(-1, 2, 3),
                new Point(4, -2, 1),
                new Point(2, 3, -4)
        );

        assertAABB(
                triangle.getAABB(),
                new Point(-1, -2, -4),
                new Point(4, 3, 3),
                "Arbitrary triangle"
        );
    }

    /**
     * EP04: Polygon with arbitrary vertices.
     */
    @Test
    void testPolygonAABBArbitraryVertices() {
        Polygon polygon = new Polygon(
                new Point(1, 1, 0),
                new Point(4, 1, 0),
                new Point(5, 3, 0),
                new Point(2, 4, 0)
        );

        assertAABB(
                polygon.getAABB(),
                new Point(1, 1, 0),
                new Point(5, 4, 0),
                "Arbitrary polygon"
        );
    }

    /**
     * EP05: Plane is infinite, therefore it has no finite AABB.
     */
    @Test
    void testPlaneAABBIsNull() {
        Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 0, 1));

        assertNull(plane.getAABB(), "Plane is infinite and should not have finite AABB");
    }

    /**
     * EP06: Tube is infinite, therefore it has no finite AABB.
     */
    @Test
    void testTubeAABBIsNull() {
        Tube tube = new Tube(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));

        assertNull(tube.getAABB(), "Tube is infinite and should not have finite AABB");
    }

    // =============== Boundary Values Tests ==================

    /**
     * BV01: Triangle lies entirely on a flat Z plane.
     */
    @Test
    void testFlatTriangleAABB() {
        Triangle triangle = new Triangle(
                new Point(0, 0, 5),
                new Point(2, 0, 5),
                new Point(0, 3, 5)
        );

        assertAABB(
                triangle.getAABB(),
                new Point(0, 0, 5),
                new Point(2, 3, 5),
                "Flat triangle"
        );
    }

    /**
     * BV02: Polygon lies entirely on a flat Z plane.
     */
    @Test
    void testFlatPolygonAABB() {
        Polygon polygon = new Polygon(
                new Point(-2, -1, 7),
                new Point(2, -1, 7),
                new Point(2, 1, 7),
                new Point(-2, 1, 7)
        );

        assertAABB(
                polygon.getAABB(),
                new Point(-2, -1, 7),
                new Point(2, 1, 7),
                "Flat polygon"
        );
    }

    /**
     * BV03: Geometry touches the coordinate origin.
     */
    @Test
    void testSphereTouchingOriginAABB() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1.0);

        assertAABB(
                sphere.getAABB(),
                new Point(0, -1, -1),
                new Point(2, 1, 1),
                "Sphere touching origin"
        );
    }

    /**
     * BV04: Polygon with negative and positive coordinates.
     */
    @Test
    void testPolygonAABBMixedCoordinates() {
        Polygon polygon = new Polygon(
                new Point(-3, -2, 1),
                new Point(2, -2, 1),
                new Point(2, 4, 1),
                new Point(-3, 4, 1)
        );

        assertAABB(
                polygon.getAABB(),
                new Point(-3, -2, 1),
                new Point(2, 4, 1),
                "Polygon with mixed coordinates"
        );
    }
}