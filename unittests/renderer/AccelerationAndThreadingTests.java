package renderer;

import geometries.impl.RegularGrid;
import org.junit.jupiter.api.Test;

import static parser.XmlImageLoader.loadImage;

/**
 * Final benchmark test for MP2.
 * <p>
 * Compares the same final scene in four modes:
 * no acceleration, Regular Grid only, multithreading only,
 * and both Regular Grid + multithreading.
 * </p>
 * <p>
 * Also contains tuning tests for:
 * <ul>
 *     <li>Regular Grid density</li>
 *     <li>Regular Grid minResolution</li>
 *     <li>Regular Grid maxResolution</li>
 *     <li>Small combined search around density = 5.0</li>
 * </ul>
 */
class AccelerationAndThreadingTests {
    /**
     * XML file with Regular Grid disabled.
     */
    private final String XML_FILE = "xml/MP2_RichScene_ForRegularGrid.xml";

    /**
     * Number of measured runs for the final comparison.
     */
    private static final int RUNS = 2;

    /**
     * Number of threads used when multithreading is enabled.
     */
    private static final int THREADS = 4;


    /**
     * Current best known density.
     */
    private static final double DEFAULT_DENSITY = 5.0;

    /**
     * Current default minimum grid resolution.
     */
    private static final int DEFAULT_MIN_RESOLUTION = 4;

    /**
     * Current default maximum grid resolution.
     */
    private static final int DEFAULT_MAX_RESOLUTION = 96;

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
     */
    @Test
    void finalTestComparatifWithAccelerationAndThreading() {
        double baselineTime = averageRenderTime(
                XML_FILE,
                0,
                "Baseline: no threads + no Regular Grid"
        );

        writeImage(
                XML_FILE,
                0,
                "mp2-final-01-baseline-no-threads-no-grid"
        );

        double multithreadingOnlyTime = averageRenderTime(
                XML_FILE,
                THREADS,
                "Multithreading only: 4 threads + no Regular Grid"
        );

        writeImage(
                XML_FILE,
                THREADS,
                "mp2-final-02-four-threads-no-grid"
        );

        double regularGridOnlyTime = averageRenderTimeWithGrid(
                DEFAULT_DENSITY,
                DEFAULT_MIN_RESOLUTION,
                DEFAULT_MAX_RESOLUTION,
                0,
                RUNS,
                "Regular Grid only: no threads + Regular Grid"
        );

        writeImageWithGrid(
                DEFAULT_DENSITY,
                DEFAULT_MIN_RESOLUTION,
                DEFAULT_MAX_RESOLUTION,
                0,
                "mp2-final-03-no-threads-with-regular-grid"
        );

        double fullAccelerationTime = averageRenderTimeWithGrid(
                DEFAULT_DENSITY,
                DEFAULT_MIN_RESOLUTION,
                DEFAULT_MAX_RESOLUTION,
                THREADS,
                RUNS,
                "Full acceleration: 4 threads + Regular Grid"
        );

        writeImageWithGrid(
                DEFAULT_DENSITY,
                DEFAULT_MIN_RESOLUTION,
                DEFAULT_MAX_RESOLUTION,
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
     * Renders only one final image with the current chosen Regular Grid configuration.
     */
    @Test
    void justImage() {
        loadImage(XML_FILE, true)
                .setMultithreading(THREADS)
                .setDebugPrint(1)
                .setRegularGrid(new RegularGrid.Config(DEFAULT_DENSITY, DEFAULT_MIN_RESOLUTION, DEFAULT_MAX_RESOLUTION))
                .build()
                .renderImage()
                .writeToImage("mp2-final-just-image");
    }

    /**
     * Measures the average rendering time for a scene without forcing Regular Grid configuration.
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

        return toSeconds(endTime - startTime);
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

    /**
     * Converts nanoseconds to seconds.
     *
     * @param nanoTime time in nanoseconds
     * @return time in seconds
     */
    private double toSeconds(long nanoTime) {
        return nanoTime / 1_000_000_000.0;
    }

    /**
     * Measures the average rendering time with a specific Regular Grid configuration.
     *
     * @param density       Regular Grid density
     * @param minResolution Regular Grid minimum resolution
     * @param maxResolution Regular Grid maximum resolution
     * @param threads       number of rendering threads
     * @param runs          number of measured runs
     * @param label         readable mode name
     * @return average rendering time in seconds
     */
    private double averageRenderTimeWithGrid(
            double density,
            int minResolution,
            int maxResolution,
            int threads,
            int runs,
            String label
    ) {
        double totalTime = 0;

        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println(label);
        System.out.println("XML: " + XML_FILE);
        System.out.println("Threads: " + threads);
        System.out.println("Density: " + density);
        System.out.println("Min resolution: " + minResolution);
        System.out.println("Max resolution: " + maxResolution);
        System.out.println("------------------------------------------------------------");

        for (int i = 1; i <= runs; i++) {
            double currentTime = renderOnceWithGrid(
                    density,
                    minResolution,
                    maxResolution,
                    threads
            );

            totalTime += currentTime;

            System.out.printf(
                    "Run %d/%d: %.3f seconds%n",
                    i,
                    runs,
                    currentTime
            );
        }

        double averageTime = totalTime / runs;

        System.out.printf(
                "Average: %.3f seconds%n",
                averageTime
        );

        return averageTime;
    }

    /**
     * Renders one scene once with a specific Regular Grid configuration.
     *
     * @param density       Regular Grid density
     * @param minResolution Regular Grid minimum resolution
     * @param maxResolution Regular Grid maximum resolution
     * @param threads       number of rendering threads
     * @return rendering time in seconds
     */
    private double renderOnceWithGrid(
            double density,
            int minResolution,
            int maxResolution,
            int threads
    ) {
        Camera camera = loadImage(XML_FILE, true)
                .setRegularGrid(new RegularGrid.Config(
                        density,
                        minResolution,
                        maxResolution
                ))
                .setMultithreading(threads)
                .setDebugPrint(0)
                .build();

        long startTime = System.nanoTime();

        camera.renderImage();

        long endTime = System.nanoTime();

        return toSeconds(endTime - startTime);
    }


    /**
     * Renders and writes one final image with a specific Regular Grid configuration.
     * <p>
     * This method is not used for time measurement.
     * </p>
     *
     * @param density       Regular Grid density
     * @param minResolution Regular Grid minimum resolution
     * @param maxResolution Regular Grid maximum resolution
     * @param threads       number of rendering threads
     * @param imageName     output image name
     */
    private void writeImageWithGrid(
            double density,
            int minResolution,
            int maxResolution,
            int threads,
            String imageName
    ) {
        Camera camera = loadImage(XML_FILE, true)
                .setRegularGrid(new RegularGrid.Config(
                        density,
                        minResolution,
                        maxResolution
                ))
                .setMultithreading(threads)
                .setDebugPrint(0)
                .build();

        camera.renderImage();
        camera.writeToImage(imageName);

        System.out.println("Image written: " + imageName);
    }
}