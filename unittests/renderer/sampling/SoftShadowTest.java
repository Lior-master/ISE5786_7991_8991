package renderer.sampling;

import org.junit.jupiter.api.Test;
import parser.XmlImageLoader;

/**
 * Test for soft shadow effect
 */
public class SoftShadowTest {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    SoftShadowTest() {
    }

    /**
     * Test for soft shadow effect disabled
     */
    @Test
    void softShadowTestOff() {
        XmlImageLoader.loadImage("xml/SoftShadowOff.xml");
    }

    /**
     * Test for soft shadow effect enabled
     */
    @Test
    void softShadowTestOn() {
        XmlImageLoader.loadImage("xml/SoftShadowOn.xml");
    }
}
