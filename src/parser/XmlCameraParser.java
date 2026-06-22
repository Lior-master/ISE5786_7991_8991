package parser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

import static parser.XmlPrimitivesParser.parseDouble;

/**
 * Parser responsible for XML camera elements.
 */
final class XmlCameraParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlCameraParser() {
    }

    /**
     * Parses the camera element and builds a {@link Camera}.
     * <p>
     * Supported XML format:
     * <pre>
     * {@code
     * <camera
     *      location="0 0 1000"
     *      target="0 0 0"
     *      up="0 1 0"
     *      vp-width="200"
     *      vp-height="200"
     *      vp-distance="1000"
     *      nX="500"
     *      nY="500"/>
     * }
     * </pre>
     *
     * @param root  root scene element
     * @param scene parsed scene
     * @return built camera
     */
    static Camera parseCamera(Element root, Scene scene) {
        NodeList cameraNodes = root.getElementsByTagName("camera");

        if (cameraNodes.getLength() == 0) {
            throw new IllegalArgumentException("Missing <camera> element");
        }

        if (cameraNodes.getLength() > 1) {
            throw new IllegalArgumentException("Only one <camera> element is allowed");
        }

        Element cameraElement = (Element) cameraNodes.item(0);

        Point location = XmlPrimitivesParser.parsePoint(
                XmlPrimitivesParser.getRequiredAttribute(cameraElement, "location")
        );

        Vector up = XmlPrimitivesParser.parseVector(
                XmlPrimitivesParser.getRequiredAttribute(cameraElement, "up")
        );

        double vpWidth = parseDouble(
                XmlPrimitivesParser.getRequiredAttribute(cameraElement, "vp-width")
        );

        double vpHeight = parseDouble(
                XmlPrimitivesParser.getRequiredAttribute(cameraElement, "vp-height")
        );

        double vpDistance = parseDouble(
                XmlPrimitivesParser.getRequiredAttribute(cameraElement, "vp-distance")
        );

        int nX = parseInt(
                XmlPrimitivesParser.getRequiredAttribute(cameraElement, "nX")
        );

        int nY = parseInt(
                XmlPrimitivesParser.getRequiredAttribute(cameraElement, "nY")
        );

        int threads = XmlPrimitivesParser.hasAttribute(cameraElement, "threads") ?
                parseInt(XmlPrimitivesParser.getRequiredAttribute(cameraElement, "threads"))
                : 0;

        double debugPrint = XmlPrimitivesParser.hasAttribute(cameraElement, "debug-print") ?
                parseDouble(XmlPrimitivesParser.getRequiredAttribute(cameraElement, "debug-print"))
                : 0.0;

        Camera.Builder builder = Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(location)
                .setVpSize(vpWidth, vpHeight)
                .setVpDistance(vpDistance)
                .setResolution(nX, nY)
                .setMultithreading(threads)
                .setDebugPrint(debugPrint);


        applyDirection(builder, cameraElement, up);

        return builder.build();
    }

    /**
     * Applies the camera direction.
     * <p>
     * The XML can define either:
     * </p>
     * <ul>
     *     <li>{@code target="x y z"} for {@code setDirection(Point, Vector)}</li>
     *     <li>{@code to="x y z"} for {@code setDirection(Vector, Vector)}</li>
     * </ul>
     *
     * @param builder       camera builder
     * @param cameraElement camera XML element
     * @param up            up vector
     */
    private static void applyDirection(Camera.Builder builder, Element cameraElement, Vector up) {
        boolean hasTarget = XmlPrimitivesParser.hasAttribute(cameraElement, "target");
        boolean hasTo = XmlPrimitivesParser.hasAttribute(cameraElement, "to");

        if (hasTarget && hasTo) {
            throw new IllegalArgumentException("Camera cannot have both target and to attributes");
        }

        if (hasTarget) {
            Point target = XmlPrimitivesParser.parsePoint(
                    cameraElement.getAttribute("target")
            );
            builder.setDirection(target, up);
            return;
        }

        if (hasTo) {
            Vector to = XmlPrimitivesParser.parseVector(
                    cameraElement.getAttribute("to")
            );
            builder.setDirection(to, up);
            return;
        }

        throw new IllegalArgumentException("Camera must have either target or to attribute");
    }

    /**
     * Parses an integer value.
     *
     * @param value string value
     * @return parsed integer
     */
    private static int parseInt(String value) {
        return Integer.parseInt(value.trim());
    }
}