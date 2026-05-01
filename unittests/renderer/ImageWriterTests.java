package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Unit tests for {@link ImageWriter}.
 * This test generates a simple grid image in order to verify
 * basic pixel writing and image creation.
 */
class ImageWriterTests {
    /**
     * Image width in pixels.
     */
    private static final int IMAGE_WIDTH = 800;

    /**
     * Image height in pixels.
     */
    private static final int IMAGE_HEIGHT = 500;

    /**
     * Size of one grid square in pixels.
     */
    private static final int GRID_INTERVAL = 50;

    /**
     * Background color of the image.
     */
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 0);

    /**
     * Grid line color.
     */
    private static final Color GRID_COLOR = new Color(255, 0, 0);

    /**
     * Default constructor to satisfy documentation tools.
     */
    ImageWriterTests() { /* to satisfy documentation tools */ }

    /**
     * Test method for {@link ImageWriter}.
     * Creates a 800x500 image with a 50x50 grid.
     */
    @Test
    void testImageWriter() {
        ImageWriter imageWriter = new ImageWriter(IMAGE_WIDTH, IMAGE_HEIGHT);

        for (int yIndex = 0; yIndex < IMAGE_HEIGHT; yIndex++) {
            for (int xIndex = 0; xIndex < IMAGE_WIDTH; xIndex++) {
                imageWriter.writePixel(
                        xIndex,
                        yIndex,
                        xIndex % GRID_INTERVAL == 0 || yIndex % GRID_INTERVAL == 0
                                ? GRID_COLOR
                                : BACKGROUND_COLOR
                );
            }
        }

        imageWriter.writeToImage("image-writer-test");
    }
}