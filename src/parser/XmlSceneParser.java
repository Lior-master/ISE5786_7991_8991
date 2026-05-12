package parser;

import lighting.AmbientLight;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import scene.Scene;

/**
 * Parser responsible for converting the root XML element into a {@link Scene}.
 */
final class XmlSceneParser {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlSceneParser() {
    }

    /**
     * Parses a scene from the root XML element.
     *
     * @param root root scene element
     * @return parsed scene
     */
    static Scene parse(Element root) {
        String sceneName = XmlPrimitivesParser.getOptionalAttribute(root, "name", "XML scene");
        Scene scene = new Scene(sceneName);

        if (XmlPrimitivesParser.hasAttribute(root, "background-color")) {
            scene.setBackground(XmlPrimitivesParser.parseColor(root.getAttribute("background-color")));
        }

        parseAmbientLight(root, scene);
        XmlGeometryParser.parseGeometries(root, scene);

        return scene;
    }

    /**
     * Parses ambient light and applies it to the scene.
     *
     * @param root  root scene element
     * @param scene scene to update
     */
    private static void parseAmbientLight(Element root, Scene scene) {
        NodeList ambientLightNodes = root.getElementsByTagName("ambient-light");

        if (ambientLightNodes.getLength() == 0) {
            return;
        }

        Element ambientLightElement = (Element) ambientLightNodes.item(0);

        if (XmlPrimitivesParser.hasAttribute(ambientLightElement, "color")) {
            scene.setAmbientLight(
                    new AmbientLight(XmlPrimitivesParser.parseColor(ambientLightElement.getAttribute("color")))
            );
        }
    }
}