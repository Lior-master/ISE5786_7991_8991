package parser;

import org.w3c.dom.Element;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Vector;

/**
 * Parser for primitive XML values such as points, vectors, colors and numbers.
 */
final class XmlPrimitivesParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlPrimitivesParser() {
    }

    /**
     * Checks if an XML element has a non-blank attribute.
     *
     * @param element       XML element
     * @param attributeName attribute name
     * @return true if the attribute exists and is not blank
     */
    static boolean hasAttribute(Element element, String attributeName) {
        return element.hasAttribute(attributeName)
                && !element.getAttribute(attributeName).isBlank();
    }

    /**
     * Gets a required XML attribute.
     *
     * @param element       XML element
     * @param attributeName attribute name
     * @return attribute value
     */
    static String getRequiredAttribute(Element element, String attributeName) {
        if (!hasAttribute(element, attributeName)) {
            throw new IllegalArgumentException(
                    "Missing required attribute '" + attributeName
                            + "' in <" + element.getTagName() + ">"
            );
        }

        return element.getAttribute(attributeName);
    }

    /**
     * Gets an optional XML attribute or a default value.
     *
     * @param element       XML element
     * @param attributeName attribute name
     * @param defaultValue  default value
     * @return attribute value or default value
     */
    static String getOptionalAttribute(Element element, String attributeName, String defaultValue) {
        return hasAttribute(element, attributeName)
                ? element.getAttribute(attributeName)
                : defaultValue;
    }

    /**
     * Parses a double value.
     *
     * @param value string value
     * @return parsed double
     */
    static double parseDouble(String value) {
        return Double.parseDouble(value.trim());
    }

    /**
     * Parses a color from "r g b".
     *
     * @param value color string
     * @return parsed color
     */
    static Color parseColor(String value) {
        double[] numbers = parseThreeNumbers(value);
        return new Color(numbers[0], numbers[1], numbers[2]);
    }

    /**
     * Parses a point from "x y z".
     *
     * @param value point string
     * @return parsed point
     */
    static Point parsePoint(String value) {
        double[] numbers = parseThreeNumbers(value);
        return new Point(numbers[0], numbers[1], numbers[2]);
    }

    /**
     * Parses a vector from "x y z".
     *
     * @param value vector string
     * @return parsed vector
     */
    static Vector parseVector(String value) {
        double[] numbers = parseThreeNumbers(value);
        return new Vector(numbers[0], numbers[1], numbers[2]);
    }

    /**
     * Parses either one number or three numbers as a {@link Double3}.
     * <p>
     * Examples:
     * </p>
     * <ul>
     * <li>{@code "0.4"} becomes {@code new Double3(0.4)}</li>
     * <li>{@code "0.8 0 0"} becomes {@code new Double3(0.8, 0, 0)}</li>
     * </ul>
     *
     * @param value string value
     * @return parsed Double3
     */
    static Double3 parseDouble3OrSingle(String value) {
        String[] parts = value.trim().split("\\s+");

        if (parts.length == 1) {
            return new Double3(Double.parseDouble(parts[0]));
        }

        if (parts.length == 3) {
            return new Double3(
                    Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2])
            );
        }

        throw new IllegalArgumentException("Expected one or three numbers, got: " + value);
    }

    /**
     * Parses exactly three double values.
     *
     * @param value string containing three numbers
     * @return array of three doubles
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