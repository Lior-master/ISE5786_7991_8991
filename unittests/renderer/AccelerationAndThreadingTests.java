package renderer;

import java.util.LinkedHashMap;
import java.util.Map;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import renderer.sampling.Blackboard;
import renderer.sampling.SamplingPattern;
import renderer.sampling.SamplingShape;
import scene.Scene;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static parser.XmlImageLoader.loadImage;

/**
 * Benchmark tests for Camera multithreading.
 */
@SuppressWarnings("java:S109")
class AccelerationAndThreadingTests {

    /**
     * Number of measured runs for each mode
     */
    private static final int RUNS = 5;

    /**
     * Number of warm-up runs, not included in average
     */
    private static final int WARMUP_RUNS = 1;

    /**
     * Resolution for quick correctness test
     */
    private static final int QUICK_RESOLUTION = 250;

    /**
     * Resolution for real benchmark
     */
    private static final int BENCHMARK_RESOLUTION = 600;

    /**
     * Default constructor to satisfy JavaDoc generator.
     */
    AccelerationAndThreadingTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Quick test: verifies that all multithreading modes render without crashing.
     * This one does not write images.
     */
    @Test
    @Disabled
    void testMultithreadingModesDoNotCrash() {
        int[] threadModes = {0, 4, 8, 16, 32, -1};

        for (int threads : threadModes) {
            Camera camera = buildCamera(threads, QUICK_RESOLUTION, false);

            assertDoesNotThrow(
                    camera::renderImage,
                    "Rendering crashed with threads = " + threadLabel(threads)
            );
        }
    }

    /**
     * Manual benchmark.
     */
    @Test
    @Disabled
    void benchmarkAverageRenderTime() {
        int[] threadModes = {0, 4, 8, 16, 32, -1};

        Map<Integer, Double> averages = new LinkedHashMap<>();

        System.out.println();
        System.out.println("==========================================");
        System.out.println("       MULTITHREADING BENCHMARK");
        System.out.println("==========================================");
        System.out.println();

        for (int threads : threadModes) {
            double average = measureAverageRenderTime(threads);
            averages.put(threads, average);

            System.out.printf(
                    "FINAL | threads = %-6s | average = %.3f seconds%n",
                    threadLabel(threads),
                    average
            );

            System.out.println();
        }

        printSpeedups(averages);
    }

    /**
     * Manual image generation with the best mode.
     * Remove @Disabled to generate a final image.
     */
    @Test
    @Disabled
    void renderFinalImageWithThreads8() {
        Camera camera = buildCamera(8, BENCHMARK_RESOLUTION, true).renderImage();

        camera.printGrid(60, new Color(RED));
        camera.writeToImage("multithreading-final-threads-8");
    }

    /**
     * Manual image generation with stream mode.
     * Remove @Disabled to generate a final image.
     */
    @Test
    @Disabled
    void renderFinalImageWithStream() {
        Camera camera = buildCamera(-1, BENCHMARK_RESOLUTION, true).renderImage();

        camera.printGrid(60, new Color(RED));
        camera.writeToImage("multithreading-final-stream");
    }

    /**
     * Measures the average render time for one multithreading mode.
     *
     * @param threads multithreading mode
     * @return average render time in seconds
     */
    private double measureAverageRenderTime(int threads) {
        runWarmup(threads);

        long totalTime = 0;

        for (int run = 1; run <= RUNS; run++) {
            Camera camera = buildCamera(threads, BENCHMARK_RESOLUTION, true);

            long start = System.nanoTime();

            assertDoesNotThrow(camera::renderImage);

            long end = System.nanoTime();

            long elapsed = end - start;
            totalTime += elapsed;

            System.out.printf(
                    "threads = %-6s | run %d/%d | time = %.3f seconds%n",
                    threadLabel(threads),
                    run,
                    RUNS,
                    elapsed / 1_000_000_000.0
            );
        }

        return totalTime / 1_000_000_000.0 / RUNS;
    }

    /**
     * Runs warm-up renderings before measuring.
     *
     * @param threads multithreading mode
     */
    private void runWarmup(int threads) {
        for (int run = 1; run <= WARMUP_RUNS; run++) {
            Camera camera = buildCamera(threads, BENCHMARK_RESOLUTION, true);
            assertDoesNotThrow(camera::renderImage);
        }
    }

    /**
     * Prints the speedup compared to no multithreading.
     *
     * @param averages average render times
     */
    private void printSpeedups(Map<Integer, Double> averages) {
        double baseTime = averages.get(0);

        System.out.println("==========================================");
        System.out.println("       SPEEDUP COMPARED TO threads = 0");
        System.out.println("==========================================");

        for (var entry : averages.entrySet()) {
            int threads = entry.getKey();
            double average = entry.getValue();

            System.out.printf(
                    "threads = %-6s | average = %.3f seconds | speedup = x%.2f%n",
                    threadLabel(threads),
                    average,
                    baseTime / average
            );
        }
    }

    /**
     * Converts a thread mode to a readable label.
     *
     * @param threads thread mode
     * @return readable label
     */
    private String threadLabel(int threads) {
        return threads == -1 ? "stream" : String.valueOf(threads);
    }

    /**
     * Builds a camera for the benchmark.
     *
     * @param threads    multithreading mode
     * @param resolution image resolution
     * @param softShadow true for soft shadows, false for regular shadows
     * @return configured camera
     */
    private Camera buildCamera(int threads, int resolution, boolean softShadow) {
        Scene scene = createBenchmarkScene(softShadow);

        return Camera.getBuilder()
                .setLocation(new Point(0, 0, 1000))
                .setVpDistance(1000)
                .setVpSize(240, 240)
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setResolution(resolution, resolution)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setMultithreading(threads)
                .setDebugPrint(0)
                .build();
    }

    /**
     * Creates a benchmark scene.
     *
     * @param softShadow true for soft shadows, false for regular shadows
     * @return scene
     */
    private Scene createBenchmarkScene(boolean softShadow) {
        Scene scene = new Scene("Multithreading benchmark scene")
                .setBackground(new Color(8, 8, 18))
                .setAmbientLight(new AmbientLight(new Color(28, 28, 28)));

        Material sphereMaterial = new Material()
                .setKD(0.5)
                .setKS(0.4)
                .setShininess(120);

        Material triangleMaterial = new Material()
                .setKD(0.45)
                .setKS(0.35)
                .setShininess(80);

        // Large background triangles
        scene.geometries.add(
                new Triangle(
                        new Point(-180, -180, -360),
                        new Point(180, -180, -360),
                        new Point(0, 180, -430)
                )
                        .setEmission(new Color(25, 25, 70))
                        .setMaterial(triangleMaterial),

                new Triangle(
                        new Point(-180, 170, -390),
                        new Point(180, 170, -390),
                        new Point(0, -190, -470)
                )
                        .setEmission(new Color(50, 25, 60))
                        .setMaterial(triangleMaterial)
        );

        // Central sphere
        scene.geometries.add(
                new Sphere(new Point(0, 0, -190), 55D)
                        .setEmission(new Color(BLUE))
                        .setMaterial(sphereMaterial)
        );

        // Many small spheres to make the rendering heavier
        for (int ix = -3; ix <= 3; ix++) {
            for (int iy = -3; iy <= 3; iy++) {
                if (ix == 0 && iy == 0) continue;

                int red = 60 + (ix + 3) * 25;
                int green = 70 + (iy + 3) * 23;
                int blue = 120 + ((ix + iy + 6) % 5) * 20;

                double x = ix * 42;
                double y = iy * 34;
                double z = -240 - ((ix + iy + 6) % 4) * 25;

                scene.geometries.add(
                        new Sphere(new Point(x, y, z), 16D)
                                .setEmission(new Color(red, green, blue))
                                .setMaterial(sphereMaterial)
                );
            }
        }

        // A small triangle in front of the main sphere to create shadow
        scene.geometries.add(
                new Triangle(
                        new Point(-65, -35, -80),
                        new Point(-25, -75, -85),
                        new Point(-62, -72, -92)
                )
                        .setEmission(new Color(80, 80, 160))
                        .setMaterial(triangleMaterial)
        );

        SpotLight spotLight = new SpotLight(
                new Color(700, 420, 300),
                new Point(-90, -90, 180),
                new Vector(1, 1, -3)
        )
                .setKl(1E-5)
                .setKq(1.5E-7);

        if (softShadow) {
            spotLight.setBlackboard(
                    new Blackboard()
                            .setSize(28)
                            .setGridSize(7)
                            .setShape(SamplingShape.CIRCLE)
                            .setPattern(SamplingPattern.REGULAR)
            );
        } else {
            spotLight.setBlackboard(
                    new Blackboard()
                            .setSize(0)
                            .setGridSize(1)
                            .setShape(SamplingShape.RECTANGLE)
                            .setPattern(SamplingPattern.REGULAR)
            );
        }

        scene.lights.add(spotLight);

        return scene;
    }

    @Test
    public void multithreadingTest() {
        Camera.Builder cameraBuilder = loadImage("xml/SoftShadowOn.xml", true);
        long start = System.nanoTime();
        cameraBuilder.setResolution(200, 200).build().renderImage();
        long end = System.nanoTime();

        System.out.println("Without multithreading time: " + ((end - start) / 1_000_000_000.0) + " seconds");

        start = System.nanoTime();
        cameraBuilder.setMultithreading(4).build().renderImage();
        end = System.nanoTime();
        System.out.println("With 4 threads time: " + ((end - start) / 1_000_000_000.0) + " seconds");
    }
}