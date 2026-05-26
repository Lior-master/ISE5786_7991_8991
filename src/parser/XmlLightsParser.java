package parser;

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
import scene.Scene;

/**
 * Utility class for parsing light elements from an XML scene description
 * and adding the resulting LightSource objects to a Scene.
 */
final class XmlLightsParser {
    /**
     * Private constructor to prevent instantiation.
     */
    private XmlLightsParser() {
    }

    /**
     * Parses the lights section and adds all the lights to the scene.
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
     * based on the element tag name (directionalLight, spotLight, pointLight).
     *
     * @param element XML element representing a light
     * @return parsed LightSource instance
     * @throws IllegalArgumentException if the element tag is not a known light type
     */
    private static LightSource parseLight(Element element) {

        return switch (element.getTagName()) {
            case "directional-light" -> parseDirectionalLight(element);
            case "spot-light" -> parseSpotLight(element);
            case "point-light" -> parsePointLight(element);
            default -> throw new IllegalArgumentException("Unknown light type: " + element.getTagName());
        };
    }

    /**
     * Parse a directional light element.
     *
     * @param element element with attributes: intensity, direction
     * @return a new DirectionalLight built from the element attributes
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
     * Parse a point light element.
     *
     * @param element element with attributes: intensity, point, optional kC, kL, kQ
     * @return a new PointLight built from the element attributes
     */
    private static LightSource parsePointLight(Element element) {
        Color intensity = XmlPrimitivesParser.parseColor(
                XmlPrimitivesParser.getRequiredAttribute(element, "intensity")
        );
        Point position = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(element, "position")
        );
        double kC = element.hasAttribute("kC")
                ? Double.parseDouble(element.getAttribute("kC"))
                : 1.0;

        double kL = element.hasAttribute("kL")
                ? Double.parseDouble(element.getAttribute("kL"))
                : 0.0;

        double kQ = element.hasAttribute("kQ")
                ? Double.parseDouble(element.getAttribute("kQ"))
                : 0.0;
        return new PointLight(intensity, position).setKc(kC).setKl(kL).setKq(kQ);
    }

    /**
     * Parse a spotlight element.
     *
     * @param element element with attributes: intensity, point, direction, optional kC, kL, kQ
     * @return a new SpotLight built from the element attributes
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

        double kC = element.hasAttribute("kC")
                ? Double.parseDouble(element.getAttribute("kC"))
                : 1.0;

        double kL = element.hasAttribute("kL")
                ? Double.parseDouble(element.getAttribute("kL"))
                : 0.0;

        double kQ = element.hasAttribute("kQ")
                ? Double.parseDouble(element.getAttribute("kQ"))
                : 0.0;

        return new SpotLight(intensity, position, direction).setKl(kL).setKc(kC).setKq(kQ);
    }

}