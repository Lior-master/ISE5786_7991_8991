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
     *     <li>{@code emission="r g b"}</li>
     *     <li>{@code kA="value"} or {@code kA="r g b"}</li>
     *     <li>{@code kD="value"} or {@code kD="r g b"}</li>
     *     <li>{@code kS="value"} or {@code kS="r g b"}</li>
     *     <li>{@code shininess="value"}</li>
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

        Material material = new Material();
        boolean hasMaterial = false;

        if (XmlPrimitivesParser.hasAttribute(element, "kA")) {
            material.setkA(
                    XmlPrimitivesParser.parseDouble3OrSingle(element.getAttribute("kA"))
            );
            hasMaterial = true;
        }

        if (XmlPrimitivesParser.hasAttribute(element, "kD")) {
            material.setKD(
                    XmlPrimitivesParser.parseDouble3OrSingle(element.getAttribute("kD"))
            );
            hasMaterial = true;
        }

        if (XmlPrimitivesParser.hasAttribute(element, "kS")) {
            material.setKS(
                    XmlPrimitivesParser.parseDouble3OrSingle(element.getAttribute("kS"))
            );
            hasMaterial = true;
        }

        if (XmlPrimitivesParser.hasAttribute(element, "shininess")) {
            material.setShininess(
                    (int) XmlPrimitivesParser.parseDouble(element.getAttribute("shininess"))
            );
            hasMaterial = true;
        }

        if (hasMaterial) {
            geometry.setMaterial(material);
        }

        return geometry;
    }
}