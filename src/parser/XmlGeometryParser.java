package parser;

import geometries.api.Geometry;
import geometries.impl.Cylinder;
import geometries.impl.Plane;
import geometries.impl.Polygon;
import geometries.impl.RegularGrid;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import geometries.impl.Tube;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * Parser responsible for XML geometry elements.
 */
final class XmlGeometryParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlGeometryParser() {
    }

    /**
     * Parses the geometries section and adds all geometries to the scene.
     *
     * @param root  root scene element
     * @param scene scene to update
     */
    static void parseGeometries(Element root, Scene scene) {
        NodeList geometriesNodes = root.getElementsByTagName("geometries");

        if (geometriesNodes.getLength() == 0) {
            return;
        }

        Element geometriesElement = (Element) geometriesNodes.item(0);
        NodeList geometryNodes = geometriesElement.getChildNodes();
        setRegularGrid(geometriesElement, scene);

        for (int i = 0; i < geometryNodes.getLength(); i++) {
            Node node = geometryNodes.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Geometry geometry = parseGeometry((Element) node);
            scene.geometries.add(geometry);
        }

        setRegularGrid(geometriesElement, scene);
    }

    private static void setRegularGrid(Element geometriesElement, Scene scene) {
        if (!geometriesElement.hasAttributes()) {
            scene.geometries.disableRegularGrid();
            return;
        }
        if (geometriesElement.hasAttribute("Default") && geometriesElement.getAttribute("Default").equals("true")) {
            scene.geometries.enableRegularGrid(RegularGrid.Config.DEFAULT);
            return;
        }
        double density = geometriesElement.hasAttribute("Density") ? Double.parseDouble(geometriesElement.getAttribute("Density")) : 3.0;
        int min = geometriesElement.hasAttribute("minResolution") ? Integer.parseInt(geometriesElement.getAttribute("minResolution")) : 1;
        int max = geometriesElement.hasAttribute("maxResolution") ? Integer.parseInt(geometriesElement.getAttribute("maxResolution")) : 128;

        scene.geometries.enableRegularGrid(new RegularGrid.Config(density, min, max));
    }

    /**
     * Parses one geometry element.
     *
     * @param element geometry XML element
     * @return parsed geometry
     */
    private static Geometry parseGeometry(Element element) {
        Geometry geometry = switch (element.getTagName()) {
            case "sphere" -> parseSphere(element);
            case "triangle" -> parseTriangle(element);
            case "plane" -> parsePlane(element);
            case "polygon" -> parsePolygon(element);
            case "tube" -> parseTube(element);
            case "cylinder" -> parseCylinder(element);
            default -> throw new IllegalArgumentException("Unknown geometry type: " + element.getTagName());
        };

        return XmlGeometryAttributes.applyCommonAttributes(geometry, element);
    }

    /**
     * Parses a {@code <sphere>} element.
     *
     * @param element sphere XML element
     * @return parsed sphere
     */
    private static Sphere parseSphere(Element element) {
        Point center = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "center")
        );

        double radius = XmlPrimitivesParser.parseDouble(
                XmlPrimitivesParser.getRequiredAttribute(element, "radius")
        );

        return new Sphere(center, radius);
    }

    /**
     * Parses a {@code <triangle>} element.
     *
     * @param element triangle XML element
     * @return parsed triangle
     */
    private static Triangle parseTriangle(Element element) {
        Point p0 = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "p0")
        );

        Point p1 = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "p1")
        );

        Point p2 = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "p2")
        );

        return new Triangle(p0, p1, p2);
    }

    /**
     * Parses a {@code <plane>} element.
     *
     * @param element plane XML element
     * @return parsed plane
     */
    private static Plane parsePlane(Element element) {
        Point point = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "point")
        );

        Vector vector = XmlPrimitivesParser.parseVector(
                XmlPrimitivesParser.getRequiredAttribute(element, "vector")
        );

        return new Plane(point, vector);
    }

    /**
     * Parses a {@code <polygon>} element from direct child {@code <point>} elements.
     *
     * @param element polygon XML element
     * @return parsed polygon
     */
    private static Polygon parsePolygon(Element element) {
        NodeList childNodes = element.getChildNodes();

        int pointCount = countDirectPointChildren(childNodes);

        if (pointCount < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 points");
        }

        Point[] points = new Point[pointCount];
        int pointIndex = 0;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element pointElement = (Element) node;

            if (!"point".equals(pointElement.getTagName())) {
                continue;
            }

            points[pointIndex++] = XmlPrimitivesParser.parsePoint(
                    XmlPrimitivesParser.getRequiredAttribute(pointElement, "value")
            );
        }

        return new Polygon(points);
    }

    /**
     * Parses a {@code <tube>} element.
     *
     * @param element tube XML element
     * @return parsed tube
     */
    private static Tube parseTube(Element element) {
        double radius = XmlPrimitivesParser.parseDouble(
                XmlPrimitivesParser.getRequiredAttribute(element, "radius")
        );

        Point rayOrigin = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "rayOrigin")
        );

        Vector rayVector = XmlPrimitivesParser.parseVector(
                XmlPrimitivesParser.getRequiredAttribute(element, "rayVector")
        );

        return new Tube(radius, new Ray(rayOrigin, rayVector));
    }

    /**
     * Parses a {@code <cylinder>} element.
     *
     * @param element cylinder XML element
     * @return parsed cylinder
     */
    private static Cylinder parseCylinder(Element element) {
        double radius = XmlPrimitivesParser.parseDouble(
                XmlPrimitivesParser.getRequiredAttribute(element, "radius")
        );

        double height = XmlPrimitivesParser.parseDouble(
                XmlPrimitivesParser.getRequiredAttribute(element, "height")
        );

        Point rayOrigin = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "rayOrigin")
        );

        Vector rayVector = XmlPrimitivesParser.parseVector(
                XmlPrimitivesParser.getRequiredAttribute(element, "rayVector")
        );

        return new Cylinder(radius, new Ray(rayOrigin, rayVector), height);
    }

    /**
     * Counts direct child nodes named {@code point}.
     *
     * @param childNodes child nodes of a polygon element
     * @return number of direct {@code point} children
     */
    private static int countDirectPointChildren(NodeList childNodes) {
        int count = 0;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE
                    && "point".equals(((Element) node).getTagName())) {
                count++;
            }
        }

        return count;
    }
}