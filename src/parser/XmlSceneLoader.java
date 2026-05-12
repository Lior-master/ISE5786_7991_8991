package parser;

import org.w3c.dom.Document;
import scene.Scene;

/**
 * Entry point for loading a {@link Scene} from an XML file.
 */
public final class XmlSceneLoader {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlSceneLoader() {
    }

    /**
     * Loads a scene from an XML file.
     *
     * @param filePath path to the XML file
     * @return scene created from the XML file
     */
    public static Scene loadScene(String filePath) {
        Document document = XmlDocumentReader.read(filePath);
        return XmlSceneParser.parse(document.getDocumentElement());
    }
}