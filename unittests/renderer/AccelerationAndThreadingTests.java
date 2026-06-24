package renderer;

import org.junit.jupiter.api.Test;

import static parser.XmlImageLoader.loadImage;

/**
 * Final benchmark test for MP2.
 * <p>
 * Compares the same final scene in four modes:
 * no acceleration, Regular Grid only, multithreading only,
 * and both Regular Grid + multithreading.
 * </p>
 */
class AccelerationAndThreadingTests {

    /**
     * XML file with Regular Grid enabled.
     */
    private final String REGULAR_GRID_ON = "xml/MP2_RichScene_RegularGridOn.xml";

    /**
     * XML file with Regular Grid disabled.
     */
    private final String REGULAR_GRID_OFF = "xml/MP2_RichScene_RegularGridOff.xml";

    /**
     * Number of measured runs for each mode.
     */
    private static final int RUNS = 1;

    /**
     * Number of threads used when multithreading is enabled.
     */
    private static final int THREADS = 4;

    /**
     * Default constructor to satisfy Javadoc generator.
     */
    AccelerationAndThreadingTests() {
        /* to satisfy Javadoc generator */
    }

    /**
     * Final comparison test for MP2 acceleration and multithreading.
     * <p>
     * This test renders the same scene in four configurations:
     * </p>
     * <ul>
     *     <li>No multithreading and no Regular Grid</li>
     *     <li>4 threads and no Regular Grid</li>
     *     <li>No multithreading and Regular Grid</li>
     *     <li>4 threads and Regular Grid</li>
     * </ul>
     * <p>
     * For each configuration, it prints the average render time in seconds
     * and writes one image with a descriptive name.
     * </p>
     */
    @Test
    void finalTestComparatifWithAccelerationAndThreading() {
        double baselineTime = averageRenderTime(
                REGULAR_GRID_OFF,
                0,
                "Baseline: no threads + no Regular Grid"
        );

        writeImage(
                REGULAR_GRID_OFF,
                0,
                "mp2-final-01-baseline-no-threads-no-grid"
        );

        double multithreadingOnlyTime = averageRenderTime(
                REGULAR_GRID_OFF,
                THREADS,
                "Multithreading only: 4 threads + no Regular Grid"
        );

        writeImage(
                REGULAR_GRID_OFF,
                THREADS,
                "mp2-final-02-four-threads-no-grid"
        );

        double regularGridOnlyTime = averageRenderTime(
                REGULAR_GRID_ON,
                0,
                "Regular Grid only: no threads + Regular Grid"
        );

        writeImage(
                REGULAR_GRID_ON,
                0,
                "mp2-final-03-no-threads-with-regular-grid"
        );

        double fullAccelerationTime = averageRenderTime(
                REGULAR_GRID_ON,
                THREADS,
                "Full acceleration: 4 threads + Regular Grid"
        );

        writeImage(
                REGULAR_GRID_ON,
                THREADS,
                "mp2-final-04-four-threads-with-regular-grid"
        );

        System.out.println();
        System.out.println("==========================================================================");
        System.out.println("                    MP2 FINAL SCENE - COMPARISON");
        System.out.println("==========================================================================");
        System.out.printf("%-48s | %-12s | %-10s%n", "Mode", "Average", "Speedup");
        System.out.println("--------------------------------------------------------------------------");

        printResultLine(
                "Baseline: no threads + no Regular Grid",
                baselineTime,
                baselineTime
        );

        printResultLine(
                "Multithreading only: 4 threads + no Regular Grid",
                multithreadingOnlyTime,
                baselineTime
        );

        printResultLine(
                "Regular Grid only: no threads + Regular Grid",
                regularGridOnlyTime,
                baselineTime
        );

        printResultLine(
                "Full acceleration: 4 threads + Regular Grid",
                fullAccelerationTime,
                baselineTime
        );

        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(
                "Speedup of multithreading only:       %.2fx%n",
                baselineTime / multithreadingOnlyTime
        );
        System.out.printf(
                "Speedup of Regular Grid only:         %.2fx%n",
                baselineTime / regularGridOnlyTime
        );
        System.out.printf(
                "Speedup of both combined:             %.2fx%n",
                baselineTime / fullAccelerationTime
        );
        System.out.println();
        System.out.printf(
                "Full acceleration compared to Regular Grid only: %.2fx%n",
                regularGridOnlyTime / fullAccelerationTime
        );
        System.out.printf(
                "Full acceleration compared to 4 threads only:    %.2fx%n",
                multithreadingOnlyTime / fullAccelerationTime
        );
        System.out.println("==========================================================================");
    }

    /**
     * Measures the average rendering time for a scene.
     *
     * @param xmlPath XML scene path
     * @param threads number of rendering threads
     * @param label   readable mode name
     * @return average rendering time in seconds
     */
    private double averageRenderTime(String xmlPath, int threads, String label) {
        double totalTime = 0;

        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println(label);
        System.out.println("XML: " + xmlPath);
        System.out.println("Threads: " + threads);
        System.out.println("------------------------------------------------------------");

        for (int i = 1; i <= RUNS; i++) {
            double currentTime = renderOnce(xmlPath, threads);
            totalTime += currentTime;

            System.out.printf(
                    "Run %d/%d: %.3f seconds%n",
                    i,
                    RUNS,
                    currentTime
            );
        }

        double averageTime = totalTime / RUNS;

        System.out.printf(
                "Average: %.3f seconds%n",
                averageTime
        );

        return averageTime;
    }

    /**
     * Renders one scene once and returns the rendering time.
     *
     * @param xmlPath XML scene path
     * @param threads number of rendering threads
     * @return rendering time in seconds
     */
    private double renderOnce(String xmlPath, int threads) {
        Camera camera = loadImage(xmlPath, true)
                .setMultithreading(threads)
                .setDebugPrint(0)
                .build();

        long startTime = System.nanoTime();

        camera.renderImage();

        long endTime = System.nanoTime();

        return (endTime - startTime) / 1_000_000_000.0;
    }

    /**
     * Renders and writes one final image.
     * <p>
     * This method is not used for time measurement.
     * It only creates a visible image file for presentation.
     * </p>
     *
     * @param xmlPath   XML scene path
     * @param threads   number of rendering threads
     * @param imageName output image name
     */
    private void writeImage(String xmlPath, int threads, String imageName) {
        Camera camera = loadImage(xmlPath, true)
                .setMultithreading(threads)
                .setDebugPrint(0)
                .build();

        camera.renderImage();
        camera.writeToImage(imageName);

        System.out.println("Image written: " + imageName);
    }

    /**
     * Prints one line in the final benchmark table.
     *
     * @param label        readable mode name
     * @param averageTime  average time in seconds
     * @param baselineTime baseline time in seconds
     */
    private void printResultLine(String label, double averageTime, double baselineTime) {
        System.out.printf(
                "%-48s | %8.3f sec | %8.2fx%n",
                label,
                averageTime,
                baselineTime / averageTime
        );
    }

    @Test
    void justImage() {
        writeImage(
                REGULAR_GRID_ON,
                THREADS,
                "mp2-final-just-image-four-threads-with-regular-grid"
        );
    }

}