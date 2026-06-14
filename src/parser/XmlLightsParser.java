package parser;

import java.util.Locale;

import lighting.DirectionalLight;
import lighting.LightSource;
import lighting.PointLight;
import lighting.SpotLight;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.sampling.Blackboard;
import renderer.sampling.SamplingPattern;
import renderer.sampling.SamplingShape;
import scene.Scene;

/**
 * Utility class for parsing light elements from an XML scene description
 * and adding the resulting {@link LightSource} objects to a {@link Scene}.
 */
final class XmlLightsParser {
    /**
     * Private constructor to prevent instantiation.
     */
    private XmlLightsParser() {
    }

    /**
     * Parses the lights section and adds all parsed lights to the scene.
     * <p>
     * Supported light tags are:
     * </p>
     * <ul>
     *     <li>{@code directional-light}</li>
     *     <li>{@code point-light}</li>
     *     <li>{@code spot-light}</li>
     * </ul>
     *
     * @param root  root scene element
     * @param scene scene to update
     */
    static void parseLights(Element root, Scene scene) {
        NodeList lightNode = root.getElementsByTagName("lights");

        if (lightNode.getLength() == 0) {
            return;
        }

        Element lightsElement = (Element) lightNode.item(0);
        NodeList lightNodes = lightsElement.getChildNodes();

        for (int i = 0; i < lightNodes.getLength(); i++) {
            Node node = lightNodes.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            LightSource light = parseLight((Element) node);
            scene.lights.add(light);
        }
    }

    /**
     * Parses a single light element and dispatches to the specific parser
     * according to the element tag name.
     *
     * @param element XML element representing a light source
     * @return parsed {@link LightSource} instance
     * @throws IllegalArgumentException if the element tag is not a known light type
     */
    private static LightSource parseLight(Element element) {
        return switch (element.getTagName()) {
            case "directional-light" -> parseDirectionalLight(element);
            case "point-light" -> parsePointLight(element);
            case "spot-light" -> parseSpotLight(element);
            default -> throw new IllegalArgumentException("Unknown light type: " + element.getTagName());
        };
    }

    /**
     * Parses optional blackboard sampling parameters from a light element.
     * <p>
     * If no blackboard-related attributes are present, the returned blackboard
     * keeps its default configuration, which produces a single central sample.
     * </p>
     * <p>
     * Supported attributes:
     * </p>
     * <ul>
     *     <li>{@code size} - sets both width and height</li>
     *     <li>{@code width} - sampling area width</li>
     *     <li>{@code height} - sampling area height</li>
     *     <li>{@code gridSize} - number of samples per axis</li>
     *     <li>{@code shape} - sampling area shape, for example RECTANGLE or CIRCLE</li>
     *     <li>{@code pattern} - sampling pattern, for example REGULAR</li>
     * </ul>
     *
     * @param element XML element containing optional blackboard attributes
     * @return parsed {@link Blackboard} configuration
     */
    private static Blackboard parseBlackboard(Element element) {
        Blackboard blackboard = new Blackboard();

        if (element.hasAttribute("size")) {
            blackboard.setSize(Double.parseDouble(element.getAttribute("size")));
        }

        if (element.hasAttribute("width") || element.hasAttribute("height")) {
            if (!element.hasAttribute("width") || !element.hasAttribute("height")) {
                throw new IllegalArgumentException(
                        "Both width and height must be provided together for blackboard sampling"
                );
            }

            double width = Double.parseDouble(element.getAttribute("width"));
            double height = Double.parseDouble(element.getAttribute("height"));

            blackboard.setSize(width, height);
        }

        if (element.hasAttribute("gridSize")) {
            blackboard.setGridSize(Integer.parseInt(element.getAttribute("gridSize")));
        }

        if (element.hasAttribute("shape")) {
            blackboard.setShape(SamplingShape.valueOf(
                    element.getAttribute("shape").toUpperCase(Locale.ROOT)
            ));
        }

        if (element.hasAttribute("pattern")) {
            blackboard.setPattern(SamplingPattern.valueOf(
                    element.getAttribute("pattern").toUpperCase(Locale.ROOT)
            ));
        }

        return blackboard;
    }

    /**
     * Reads an optional double attribute from an XML element.
     *
     * @param element      XML element
     * @param attribute    attribute name
     * @param defaultValue value to return when the attribute is missing
     * @return parsed double value or the default value
     */
    private static double getOptionalDouble(Element element, String attribute, double defaultValue) {
        return element.hasAttribute(attribute)
                ? Double.parseDouble(element.getAttribute(attribute))
                : defaultValue;
    }

    /**
     * Parses a directional light element.
     *
     * @param element element with attributes: intensity, direction
     * @return a new {@link DirectionalLight} built from the element attributes
     */
    private static LightSource parseDirectionalLight(Element element) {
        Color intensity = XmlPrimitivesParser.parseColor(
                XmlPrimitivesParser.getRequiredAttribute(element, "intensity")
        );

        Vector direction = XmlPrimitivesParser.parseVector(
                XmlPrimitivesParser.getRequiredAttribute(element, "direction")
        );

        return new DirectionalLight(intensity, direction);
    }

    /**
     * Parses a point light element.
     * <p>
     * Supported attributes:
     * </p>
     * <ul>
     *     <li>{@code intensity} - required</li>
     *     <li>{@code position} - required</li>
     *     <li>{@code kC} - optional, default 1</li>
     *     <li>{@code kL} - optional, default 0</li>
     *     <li>{@code kQ} - optional, default 0</li>
     *     <li>blackboard attributes such as {@code size}, {@code gridSize}, {@code shape}</li>
     * </ul>
     *
     * @param element element representing the point light
     * @return a new {@link PointLight} built from the element attributes
     */
    private static LightSource parsePointLight(Element element) {
        Color intensity = XmlPrimitivesParser.parseColor(
                XmlPrimitivesParser.getRequiredAttribute(element, "intensity")
        );

        Point position = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "position")
        );

        double kC = getOptionalDouble(element, "kC", 1.0);
        double kL = getOptionalDouble(element, "kL", 0.0);
        double kQ = getOptionalDouble(element, "kQ", 0.0);

        Blackboard blackboard = parseBlackboard(element);

        return new PointLight(intensity, position)
                .setKc(kC)
                .setKl(kL)
                .setKq(kQ)
                .setBlackboard(blackboard);
    }

    /**
     * Parses a spotlight element.
     * <p>
     * Supported attributes:
     * </p>
     * <ul>
     *     <li>{@code intensity} - required</li>
     *     <li>{@code position} - required</li>
     *     <li>{@code direction} - required</li>
     *     <li>{@code kC} - optional, default 1</li>
     *     <li>{@code kL} - optional, default 0</li>
     *     <li>{@code kQ} - optional, default 0</li>
     *     <li>blackboard attributes such as {@code size}, {@code gridSize}, {@code shape}</li>
     * </ul>
     *
     * @param element element representing the spotlight
     * @return a new {@link SpotLight} built from the element attributes
     */
    private static LightSource parseSpotLight(Element element) {
        Color intensity = XmlPrimitivesParser.parseColor(
                XmlPrimitivesParser.getRequiredAttribute(element, "intensity")
        );

        Point position = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "position")
        );

        Vector direction = XmlPrimitivesParser.parseVector(
                XmlPrimitivesParser.getRequiredAttribute(element, "direction")
        );

        double kC = getOptionalDouble(element, "kC", 1.0);
        double kL = getOptionalDouble(element, "kL", 0.0);
        double kQ = getOptionalDouble(element, "kQ", 0.0);

        Blackboard blackboard = parseBlackboard(element);

        return new SpotLight(intensity, position, direction)
                .setKc(kC)
                .setKl(kL)
                .setKq(kQ)
                .setBlackboard(blackboard);
    }
}