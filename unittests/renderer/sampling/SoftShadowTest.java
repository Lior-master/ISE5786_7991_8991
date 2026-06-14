package renderer.sampling;

import org.junit.jupiter.api.Test;
import parser.XmlImageLoader;

public class SoftShadowTest {
    @Test
    void softShadowTestOff() {
        XmlImageLoader.loadImage("xml/SoftShadowOff.xml");
    }

    @Test
    void softShadowTestOn() {
        XmlImageLoader.loadImage("xml/SoftShadowOn.xml");
    }

}
