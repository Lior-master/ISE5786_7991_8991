package parser;

import java.io.File;

import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import primitives.Color;
import renderer.Camera;
import scene.Scene;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Loads a full image from an XML file.
 * <p>
 * This class is the main entry point for XML rendering.
 * It parses the scene, geometries, lights and camera, then renders the image.
 * </p>
 */
public final class XmlImageLoader {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlImageLoader() {
    }

    /**
     * Loads an XML file, builds the scene and camera, renders the image,
     * and writes the result to the image's folder.
     *
     * @param xmlPath path to the XML file
     */
    public static void loadImage(String xmlPath) {
        Document document = parseDocument(xmlPath);
        Element root = document.getDocumentElement();

        Scene scene = parseScene(root, xmlPath);

        XmlGeometryParser.parseGeometries(root, scene);
        XmlLightsParser.parseLights(root, scene);

        Camera camera = XmlCameraParser.parseCamera(root, scene);

        String imageName = getImageName(root, xmlPath);

        camera.renderImage()
                .writeToImage(imageName);
    }

    /**
     * Parses an XML document from a file path.
     *
     * @param xmlPath XML file path
     * @return parsed XML document
     */
    private static Document parseDocument(String xmlPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(xmlPath));
            document.getDocumentElement().normalize();

            return document;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to load XML file: " + xmlPath, exception);
        }
    }

    /**
     * Parses the scene root element.
     * <p>
     * Supported root attributes:
     * <ul>
     *     <li>{@code name="..."}</li>
     *     <li>{@code background-color="r g b"}</li>
     * </ul>
     *
     * @param root    root XML element
     * @param xmlPath XML file path
     * @return parsed scene
     */
    private static Scene parseScene(Element root, String xmlPath) {
        if (!"scene".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root element must be <scene>");
        }

        String sceneName = XmlPrimitivesParser.hasAttribute(root, "name")
                ? root.getAttribute("name")
                : getFileNameWithoutExtension(xmlPath);

        Scene scene = new Scene(sceneName);

        if (XmlPrimitivesParser.hasAttribute(root, "background-color")) {
            scene.setBackground(
                    XmlPrimitivesParser.parseColor(root.getAttribute("background-color"))
            );
        }

        parseAmbientLight(root, scene);

        return scene;
    }

    /**
     * Parses the optional ambient light element.
     * <p>
     * Supported format:
     * <pre>
     * {@code <ambient-light color="38 38 38"/>}
     * </pre>
     *
     * @param root  root XML element
     * @param scene scene to update
     */
    private static void parseAmbientLight(Element root, Scene scene) {
        var ambientNodes = root.getElementsByTagName("ambient-light");

        if (ambientNodes.getLength() == 0) {
            return;
        }

        if (ambientNodes.getLength() > 1) {
            throw new IllegalArgumentException("Only one <ambient-light> element is allowed");
        }

        Element ambientElement = (Element) ambientNodes.item(0);

        Color color = XmlPrimitivesParser.parseColor(
                XmlPrimitivesParser.getRequiredAttribute(ambientElement, "color")
        );

        scene.setAmbientLight(new AmbientLight(color));
    }

    /**
     * Gets the output image name.
     * <p>
     * If the XML contains {@code image-name}, it is used.
     * Otherwise, the XML file name is used.
     * </p>
     *
     * @param root    root XML element
     * @param xmlPath XML file path
     * @return image name without extension
     */
    /**
     * Gets the output image name.
     * <p>
     * Priority:
     * <ol>
     *     <li>{@code <image name="..."/>}</li>
     *     <li>{@code <scene image-name="...">}</li>
     *     <li>XML file name without extension</li>
     * </ol>
     *
     * @param root    root XML element
     * @param xmlPath XML file path
     * @return output image name without extension
     */
    private static String getImageName(Element root, String xmlPath) {
        var imageNodes = root.getElementsByTagName("image");

        if (imageNodes.getLength() > 0) {
            Element imageElement = (Element) imageNodes.item(0);

            if (XmlPrimitivesParser.hasAttribute(imageElement, "name")) {
                return imageElement.getAttribute("name");
            }
        }

        if (XmlPrimitivesParser.hasAttribute(root, "image-name")) {
            return root.getAttribute("image-name");
        }

        return getFileNameWithoutExtension(xmlPath);
    }

    /**
     * Extracts the file name without extension from a path.
     *
     * @param path file path
     * @return file name without extension
     */
    private static String getFileNameWithoutExtension(String path) {
        String fileName = new File(path).getName();
        int dotIndex = fileName.lastIndexOf('.');

        return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
    }
}