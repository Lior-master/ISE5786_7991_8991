package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.AABB;
import primitives.Point;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AABB implementations in geometry classes.
 * Tests verify that each finite geometry correctly computes its bounding box.
 */
class GeometryAABBTests {

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    GeometryAABBTests() { /* to satisfy JavaDoc generator */ }

    // ============ Tests for Sphere AABB ============

    /**
     * Test sphere AABB at origin.
     */
    @Test
    void testSphereAABBAtOrigin() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 2.0);
        AABB aabb = sphere.getAABB();

        assertNotNull(aabb, "Sphere should have a bounding box");
        assertEquals(-2.0, aabb.min.x(), 0.001, "Min x should be -2.0");
        assertEquals(-2.0, aabb.min.y(), 0.001, "Min y should be -2.0");
        assertEquals(-2.0, aabb.min.z(), 0.001, "Min z should be -2.0");
        assertEquals(2.0, aabb.max.x(), 0.001, "Max x should be 2.0");
        assertEquals(2.0, aabb.max.y(), 0.001, "Max y should be 2.0");
        assertEquals(2.0, aabb.max.z(), 0.001, "Max z should be 2.0");
    }

    /**
     * Test sphere AABB with arbitrary center.
     */
    @Test
    void testSphereAABBArbitraryCenter() {
        Sphere sphere = new Sphere(new Point(1, 2, 3), 1.5);
        AABB aabb = sphere.getAABB();

        assertNotNull(aabb, "Sphere should have a bounding box");
        assertEquals(-0.5, aabb.min.x(), 0.001, "Min x should be 1-1.5");
        assertEquals(0.5, aabb.min.y(), 0.001, "Min y should be 2-1.5");
        assertEquals(1.5, aabb.min.z(), 0.001, "Min z should be 3-1.5");
        assertEquals(2.5, aabb.max.x(), 0.001, "Max x should be 1+1.5");
        assertEquals(3.5, aabb.max.y(), 0.001, "Max y should be 2+1.5");
        assertEquals(4.5, aabb.max.z(), 0.001, "Max z should be 3+1.5");
    }

    /**
     * Test sphere AABB is valid.
     */
    @Test
    void testSphereAABBIsValid() {
        Sphere sphere = new Sphere(new Point(1, 1, 1), 1.0);
        AABB aabb = sphere.getAABB();

        assertTrue(aabb.isValid(), "Sphere AABB should always be valid");
    }

    // ============ Tests for Triangle AABB ============

    /**
     * Test triangle AABB with axis-aligned vertices.
     */
    @Test
    void testTriangleAABBAxisAligned() {
        Triangle triangle = new Triangle(
            new Point(0, 0, 0),
            new Point(1, 0, 0),
            new Point(0, 1, 0)
        );
        AABB aabb = triangle.getAABB();

        assertNotNull(aabb, "Triangle should have a bounding box");
        assertEquals(0, aabb.min.x(), 0.001, "Min x should be 0");
        assertEquals(0, aabb.min.y(), 0.001, "Min y should be 0");
        assertEquals(0, aabb.min.z(), 0.001, "Min z should be 0");
        assertEquals(1, aabb.max.x(), 0.001, "Max x should be 1");
        assertEquals(1, aabb.max.y(), 0.001, "Max y should be 1");
        assertEquals(0, aabb.max.z(), 0.001, "Max z should be 0");
    }

    /**
     * Test triangle AABB with arbitrary vertices.
     */
    @Test
    void testTriangleAABBArbitrary() {
        Triangle triangle = new Triangle(
            new Point(-1, 2, 3),
            new Point(4, -2, 1),
            new Point(2, 3, -4)
        );
        AABB aabb = triangle.getAABB();

        assertNotNull(aabb, "Triangle should have a bounding box");
        assertEquals(-1, aabb.min.x(), 0.001, "Min x should be -1");
        assertEquals(-2, aabb.min.y(), 0.001, "Min y should be -2");
        assertEquals(-4, aabb.min.z(), 0.001, "Min z should be -4");
        assertEquals(4, aabb.max.x(), 0.001, "Max x should be 4");
        assertEquals(3, aabb.max.y(), 0.001, "Max y should be 3");
        assertEquals(3, aabb.max.z(), 0.001, "Max z should be 3");
    }

    /**
     * Test triangle AABB is valid.
     */
    @Test
    void testTriangleAABBIsValid() {
        Triangle triangle = new Triangle(
            new Point(0, 0, 0),
            new Point(1, 0, 0),
            new Point(0, 1, 0)
        );
        AABB aabb = triangle.getAABB();

        assertTrue(aabb.isValid(), "Triangle AABB should always be valid");
    }

    // ============ Tests for Polygon AABB ============

    /**
     * Test square polygon AABB.
     */
    @Test
    void testPolygonAABBSquare() {
        Polygon polygon = new Polygon(
            new Point(0, 0, 0),
            new Point(2, 0, 0),
            new Point(2, 2, 0),
            new Point(0, 2, 0)
        );
        AABB aabb = polygon.getAABB();

        assertNotNull(aabb, "Polygon should have a bounding box");
        assertEquals(0, aabb.min.x(), 0.001, "Min x should be 0");
        assertEquals(0, aabb.min.y(), 0.001, "Min y should be 0");
        assertEquals(0, aabb.min.z(), 0.001, "Min z should be 0");
        assertEquals(2, aabb.max.x(), 0.001, "Max x should be 2");
        assertEquals(2, aabb.max.y(), 0.001, "Max y should be 2");
        assertEquals(0, aabb.max.z(), 0.001, "Max z should be 0");
    }

    /**
     * Test polygon AABB with arbitrary vertices.
     */
    @Test
    void testPolygonAABBArbitrary() {
        Polygon polygon = new Polygon(
            new Point(1, 1, 0),
            new Point(3, 1, 0),
            new Point(4, 3, 0),
            new Point(2, 4, 0)
        );
        AABB aabb = polygon.getAABB();

        assertNotNull(aabb, "Polygon should have a bounding box");
        assertEquals(1, aabb.min.x(), 0.001, "Min x should be 1");
        assertEquals(1, aabb.min.y(), 0.001, "Min y should be 1");
        assertEquals(0, aabb.min.z(), 0.001, "Min z should be 0");
        assertEquals(4, aabb.max.x(), 0.001, "Max x should be 4");
        assertEquals(4, aabb.max.y(), 0.001, "Max y should be 4");
        assertEquals(0, aabb.max.z(), 0.001, "Max z should be 0");
    }

    /**
     * Test polygon AABB is valid.
     */
    @Test
    void testPolygonAABBIsValid() {
        Polygon polygon = new Polygon(
            new Point(0, 0, 0),
            new Point(1, 0, 0),
            new Point(1, 1, 0)
        );
        AABB aabb = polygon.getAABB();

        assertTrue(aabb.isValid(), "Polygon AABB should always be valid");
    }

    // ============ Tests for Plane (infinite geometry) ============

    /**
     * Test that Plane returns null AABB (infinite geometry).
     */
    @Test
    void testPlaneAABBIsNull() {
        Plane plane = new Plane(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));
        AABB aabb = plane.getAABB();

        assertNull(aabb, "Plane is infinite and should return null AABB");
    }

    // ============ Tests for Tube (infinite geometry) ============

    /**
     * Test that Tube returns null AABB (infinite geometry).
     */
    @Test
    void testTubeAABBIsNull() {
        Tube tube = new Tube(1.0, new primitives.Ray(new Point(0, 0, 0), new primitives.Vector(0, 0, 1)));
        AABB aabb = tube.getAABB();

        assertNull(aabb, "Tube is infinite and should return null AABB");
    }
}
