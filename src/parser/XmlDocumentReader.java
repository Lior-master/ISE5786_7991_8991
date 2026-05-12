package parser;

import java.io.File;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Utility class responsible only for reading XML files into DOM documents.
 */
final class XmlDocumentReader {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlDocumentReader() {
    }

    /**
     * Reads an XML file and returns its DOM document.
     *
     * @param filePath path to the XML file
     * @return parsed XML document
     */
    static Document read(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(filePath));
            document.getDocumentElement().normalize();

            return document;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to read XML file: " + filePath, exception);
        }
    }
}