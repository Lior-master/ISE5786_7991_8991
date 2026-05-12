package parser;

import geometries.api.Geometry;
import org.w3c.dom.Element;
import primitives.Material;

/**
 * Applies common geometry attributes such as emission color and material.
 */
final class XmlGeometryAttributes {

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlGeometryAttributes() {
    }

    /**
     * Applies common attributes shared by all geometries.
     * <p>
     * Supported attributes:
     * </p>
     * <ul>
     * <li>{@code emission="r g b"}</li>
     * <li>{@code kA="value"} or {@code kA="r g b"}</li>
     * </ul>
     *
     * @param geometry geometry to update
     * @param element  XML element containing the attributes
     * @return updated geometry
     */
    static Geometry applyCommonAttributes(Geometry geometry, Element element) {
        if (XmlPrimitivesParser.hasAttribute(element, "emission")) {
            geometry.setEmission(
                    XmlPrimitivesParser.parseColor(element.getAttribute("emission"))
            );
        }

        if (XmlPrimitivesParser.hasAttribute(element, "kA")) {
            geometry.setMaterial(
                    new Material().setkA(
                            XmlPrimitivesParser.parseDouble3OrSingle(element.getAttribute("kA"))
                    )
            );
        }

        return geometry;
    }
}