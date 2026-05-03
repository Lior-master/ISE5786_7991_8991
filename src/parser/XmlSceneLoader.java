package parser;

import java.io.File;

import geometries.impl.Cylinder;
import geometries.impl.Plane;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import geometries.impl.Tube;
import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Utility class for loading a {@link Scene} from an XML file.
 * <p>
 * This class is responsible only for parsing XML scene files and converting
 * them into scene objects used by the rendering system.
 * </p>
 * <p>
 * The parsing logic is intentionally separated from test classes and renderer
 * classes in order to keep a clear separation between data loading and
 * rendering logic.
 * </p>
 */
public final class XmlSceneLoader {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private XmlSceneLoader() {
    }

    /**
     * Loads a {@link Scene} from an XML file.
     * <p>
     * The XML file may define the scene background color, ambient light and
     * geometries such as spheres, triangles, polygons, planes, tubes and
     * cylinders.
     * </p>
     *
     * @param filePath path to the XML scene file
     * @return scene created from the XML file
     * @throws IllegalArgumentException if the XML file cannot be loaded or parsed
     */
    public static Scene loadScene(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(filePath));
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            Scene scene = new Scene("XML scene");

            String backgroundColor = root.getAttribute("background-color");
            if (!backgroundColor.isBlank()) {
                scene.setBackground(parseColor(backgroundColor));
            }

            loadAmbientLight(root, scene);
            loadGeometries(root, scene);

            return scene;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to load XML scene: " + filePath, exception);
        }
    }

    /**
     * Loads the ambient light from the XML root element and updates the given
     * scene.
     * <p>
     * Expected XML format:
     * </p>
     *
     * <pre>
     * {@code
     * <ambient-light color="255 191 191"/>
     * }
     * </pre>
     *
     * @param root  root XML element of the scene
     * @param scene scene to update
     */
    private static void loadAmbientLight(Element root, Scene scene) {
        NodeList ambientLightNodes = root.getElementsByTagName("ambient-light");

        if (ambientLightNodes.getLength() == 0) {
            return;
        }

        Element ambientLightElement = (Element) ambientLightNodes.item(0);
        String color = ambientLightElement.getAttribute("color");

        if (!color.isBlank()) {
            scene.setAmbientLight(new AmbientLight(parseColor(color)));
        }
    }

    /**
     * Loads all geometries from the XML root element and adds them to the given
     * scene.
     * <p>
     * Expected XML format:
     * </p>
     *
     * <pre>
     * {@code
     * <geometries>
     *     <sphere center="0 0 -100" radius="50"/>
     *     <triangle p0="0 0 -100" p1="100 0 -100" p2="0 100 -100"/>
     * </geometries>
     * }
     * </pre>
     *
     * @param root  root XML element of the scene
     * @param scene scene to update
     * @throws IllegalArgumentException if an unknown geometry type is found
     */
    private static void loadGeometries(Element root, Scene scene) {
        NodeList geometriesNodes = root.getElementsByTagName("geometries");

        if (geometriesNodes.getLength() == 0) {
            return;
        }

        Element geometriesElement = (Element) geometriesNodes.item(0);
        NodeList geometryNodes = geometriesElement.getChildNodes();

        for (int i = 0; i < geometryNodes.getLength(); i++) {
            Node node = geometryNodes.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element geometryElement = (Element) node;
            String tagName = geometryElement.getTagName();

            switch (tagName) {
                case "sphere" -> scene.geometries.add(parseSphere(geometryElement));
                case "triangle" -> scene.geometries.add(parseTriangle(geometryElement));
                case "cylinder" -> scene.geometries.add(parseCylinder(geometryElement));
                case "tube" -> scene.geometries.add(parseTube(geometryElement));
                case "polygon" -> scene.geometries.add(parsePolygon(geometryElement));
                case "plane" -> scene.geometries.add(parsePlane(geometryElement));
                default -> throw new IllegalArgumentException("Unknown geometry type: " + tagName);
            }
        }
    }

    /**
     * Parses a plane from an XML element.
     * <p>
     * Expected XML format:
     * </p>
     *
     * <pre>
     * {@code
     * <plane point="0 0 -100" vector="0 0 1"/>
     * }
     * </pre>
     *
     * @param element plane XML element
     * @return plane object created from the XML data
     */
    private static Plane parsePlane(Element element) {
        Point point = parsePoint(element.getAttribute("point"));
        Vector vector = parseVector(element.getAttribute("vector"));

        return new Plane(point, vector);
    }

    /**
     * Parses a polygon from an XML element.
     * <p>
     * Since a polygon can contain any number of vertices, the XML element uses
     * child {@code point} elements instead of fixed attributes such as
     * {@code p0}, {@code p1}, {@code p2}.
     * </p>
     * <p>
     * Expected XML format:
     * </p>
     *
     * <pre>
     * {@code
     * <polygon>
     *     <point value="-100 0 -100"/>
     *     <point value="0 100 -100"/>
     *     <point value="100 0 -100"/>
     *     <point value="0 -100 -100"/>
     * </polygon>
     * }
     * </pre>
     *
     * @param element polygon XML element
     * @return polygon object created from the XML data
     * @throws IllegalArgumentException if the polygon has fewer than three points
     */
    private static Polygon parsePolygon(Element element) {
        NodeList pointNodes = element.getElementsByTagName("point");

        if (pointNodes.getLength() < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 points");
        }

        Point[] points = new Point[pointNodes.getLength()];

        for (int i = 0; i < pointNodes.getLength(); i++) {
            Element pointElement = (Element) pointNodes.item(i);
            String value = pointElement.getAttribute("value");

            points[i] = parsePoint(value);
        }

        return new Polygon(points);
    }

    /**
     * Parses a tube from an XML element.
     * <p>
     * Expected XML format:
     * </p>
     *
     * <pre>
     * {@code
     * <tube radius="50" rayOrigin="0 0 -100" rayVector="0 1 0"/>
     * }
     * </pre>
     *
     * @param element tube XML element
     * @return tube object created from the XML data
     */
    private static Tube parseTube(Element element) {
        double radius = Double.parseDouble(element.getAttribute("radius"));
        Point rayOrigin = parsePoint(element.getAttribute("rayOrigin"));
        Vector rayVector = parseVector(element.getAttribute("rayVector"));

        return new Tube(radius, new Ray(rayOrigin, rayVector));
    }

    /**
     * Parses a cylinder from an XML element.
     * <p>
     * Expected XML format:
     * </p>
     *
     * <pre>
     * {@code
     * <cylinder radius="50" height="100" rayOrigin="0 0 -100" rayVector="0 1 0"/>
     * }
     * </pre>
     *
     * @param element cylinder XML element
     * @return cylinder object created from the XML data
     */
    private static Cylinder parseCylinder(Element element) {
        double radius = Double.parseDouble(element.getAttribute("radius"));
        double height = Double.parseDouble(element.getAttribute("height"));
        Point rayOrigin = parsePoint(element.getAttribute("rayOrigin"));
        Vector rayVector = parseVector(element.getAttribute("rayVector"));

        return new Cylinder(radius, new Ray(rayOrigin, rayVector), height);
    }

    /**
     * Parses a sphere from an XML element.
     * <p>
     * Expected XML format:
     * </p>
     *
     * <pre>
     * {@code
     * <sphere center="0 0 -100" radius="50"/>
     * }
     * </pre>
     *
     * @param element sphere XML element
     * @return sphere object created from the XML data
     */
    private static Sphere parseSphere(Element element) {
        Point center = parsePoint(element.getAttribute("center"));
        double radius = Double.parseDouble(element.getAttribute("radius"));

        return new Sphere(center, radius);
    }

    /**
     * Parses a triangle from an XML element.
     * <p>
     * Expected XML format:
     * </p>
     *
     * <pre>
     * {@code
     * <triangle p0="0 0 -100" p1="100 0 -100" p2="0 100 -100"/>
     * }
     * </pre>
     *
     * @param element triangle XML element
     * @return triangle object created from the XML data
     */
    private static Triangle parseTriangle(Element element) {
        Point p0 = parsePoint(element.getAttribute("p0"));
        Point p1 = parsePoint(element.getAttribute("p1"));
        Point p2 = parsePoint(element.getAttribute("p2"));

        return new Triangle(p0, p1, p2);
    }

    /**
     * Parses a color from a string formatted as {@code "r g b"}.
     * <p>
     * For example, the string {@code "75 127 190"} is converted into
     * {@code new Color(75, 127, 190)}.
     * </p>
     *
     * @param value color string
     * @return color object
     */
    private static Color parseColor(String value) {
        double[] numbers = parseThreeNumbers(value);
        return new Color(numbers[0], numbers[1], numbers[2]);
    }

    /**
     * Parses a point from a string formatted as {@code "x y z"}.
     * <p>
     * For example, the string {@code "0 0 -100"} is converted into
     * {@code new Point(0, 0, -100)}.
     * </p>
     *
     * @param value point string
     * @return point object
     */
    private static Point parsePoint(String value) {
        double[] numbers = parseThreeNumbers(value);
        return new Point(numbers[0], numbers[1], numbers[2]);
    }

    /**
     * Parses a vector from a string formatted as {@code "x y z"}.
     * <p>
     * For example, the string {@code "0 1 0"} is converted into
     * {@code new Vector(0, 1, 0)}.
     * </p>
     *
     * @param value vector string
     * @return vector object
     */
    private static Vector parseVector(String value) {
        double[] numbers = parseThreeNumbers(value);
        return new Vector(numbers[0], numbers[1], numbers[2]);
    }

    /**
     * Parses three double values from a string.
     * <p>
     * The values must be separated by one or more whitespace characters.
     * </p>
     * <p>
     * For example, {@code "75 127 190"} becomes an array containing
     * {@code 75.0}, {@code 127.0} and {@code 190.0}.
     * </p>
     *
     * @param value string containing three numbers
     * @return array of three double values
     * @throws IllegalArgumentException if the string does not contain exactly three
     *                                  numbers
     */
    private static double[] parseThreeNumbers(String value) {
        String[] parts = value.trim().split("\\s+");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected three numbers, got: " + value);
        }

        return new double[]{
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        };
    }
}