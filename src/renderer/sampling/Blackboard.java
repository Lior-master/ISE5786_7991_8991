package renderer.sampling;

import java.util.LinkedList;
import java.util.List;

import primitives.Point;
import primitives.Vector;

import static primitives.Util.isZero;
import static primitives.Util.random;

/**
 * Generates 2D sampling points on a target area.
 * This class is used as a generic multi-sampling engine.
 *
 * @author Halimi Lior
 */
public class Blackboard {

    /**
     * Default constructor for Javadoc tools.
     */
    public Blackboard() {/* to satisfy javadoc generator */}

    /**
     * Width of the sampling area. Must be non-negative.
     */
    private double width = 0;

    /**
     * Height of the sampling area. Must be non-negative.
     */
    private double height = 0;

    /**
     * Number of cells per side in the sampling grid. Minimum is 1.
     */
    private int gridSize = 1;

    /**
     * Sampling distribution pattern to use (regular, random, jittered).
     */
    private SamplingPattern pattern = SamplingPattern.REGULAR;

    /**
     * Geometric shape of the sampling area (rectangle or circle).
     */
    private SamplingShape shape = SamplingShape.RECTANGLE;

    /**
     * Set the size of the sampling area. Both width and height are set to the same value.
     *
     * @param size the size of the sampling area (width and height). Must be non-negative.
     * @return this Blackboard instance for method chaining
     * @throws IllegalArgumentException if size is negative
     */
    public Blackboard setSize(double size) {
        return setSize(size, size);
    }

    /**
     * Set the size of the sampling area with specified width and height.
     *
     * @param width  the width of the sampling area. Must be non-negative.
     * @param height the height of the sampling area. Must be non-negative.
     * @return this Blackboard instance for method chaining
     * @throws IllegalArgumentException if width or height is negative
     */
    public Blackboard setSize(double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height must be non-negative");
        }
        this.width = width;
        this.height = height;
        return this;
    }


    /**
     * Set the grid size for sampling. The grid size determines how many samples are generated along each axis.
     * Must be at least 1.
     *
     * @param gridSize the number of samples along each axis. Must be at least 1.
     * @return this Blackboard instance for method chaining
     * @throws IllegalArgumentException if gridSize is less than 1
     */
    public Blackboard setGridSize(int gridSize) {
        if (gridSize < 1) {
            throw new IllegalArgumentException("Grid size must be at least 1");
        }
        this.gridSize = gridSize;
        return this;
    }

    /**
     * Set the sampling shape (rectangle or circle).
     *
     * @param shape the sampling shape to use. Cannot be null.
     * @return this Blackboard instance for method chaining
     * @throws IllegalArgumentException if shape is null
     */
    public Blackboard setShape(SamplingShape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Sampling shape cannot be null");
        }
        this.shape = shape;
        return this;
    }

    /**
     * Set the sampling pattern (regular, random, jittered).
     *
     * @param pattern the sampling pattern to use. Cannot be null.
     * @return this Blackboard instance for method chaining
     * @throws IllegalArgumentException if pattern is null
     */
    public Blackboard setPattern(SamplingPattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Sampling pattern cannot be null");
        }
        this.pattern = pattern;
        return this;
    }

    /**
     * Generate 2D sample offsets according to the currently set sampling pattern and shape.
     *
     * @return a list of 2D sample offsets (x, y) in the local sampling plane
     */
    public List<Sample2D> generateSamples() {
        return switch (pattern) {
            case REGULAR -> generateRegularSamples();
            case RANDOM -> generateRandomSamples();
            case JITTERED -> generateJitteredSamples();
        };
    }

    /**
     * Generate samples arranged in a regular grid covering the sampling area.
     * The grid is centered at (0,0). If the sampling shape is CIRCLE, samples outside
     * the circle are discarded.
     *
     * @return list of regular grid Sample2D offsets
     */
    private List<Sample2D> generateRegularSamples() {

        if (isZero(width) || isZero(height) || gridSize == 1) {
            return List.of(new Sample2D(0, 0));
        }

        List<Sample2D> samples = new LinkedList<>();

        double stepX = width / gridSize;
        double stepY = height / gridSize;

        double startX = -width / 2 + stepX / 2;
        double startY = -height / 2 + stepY / 2;

        double radius = Math.min(width, height) / 2;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double x = startX + j * stepX;
                double y = startY + i * stepY;

                if (shape == SamplingShape.CIRCLE && x * x + y * y > radius * radius) {
                    continue;
                }

                samples.add(new Sample2D(x, y));
            }
        }

        return samples;
    }

    /**
     * Convert the generated 2D sample offsets into world points.
     * Each sample (x,y) is transformed to center + axisX * x + axisY * y.
     *
     * @param center the center point of the sampling area
     * @param axisX  the direction corresponding to the sample x axis
     * @param axisY  the direction corresponding to the sample y axis
     * @return list of transformed points in world coordinates
     */
    public List<Point> generatePoints(Point center, Vector axisX, Vector axisY) {
        List<Point> points = new LinkedList<>();

        for (Sample2D sample : generateSamples()) {
            Point point = center;

            if (!isZero(sample.x())) {
                point = point.add(axisX.scale(sample.x()));
            }

            if (!isZero(sample.y())) {
                point = point.add(axisY.scale(sample.y()));
            }

            points.add(point);
        }

        return points;
    }

    /**
     * Generate samples distributed randomly within the sampling area.
     * The number of samples generated is gridSize * gridSize.
     * If the sampling shape is CIRCLE, only samples inside the circle are kept.
     *
     * @return list of randomly distributed Sample2D offsets
     */
    private List<Sample2D> generateRandomSamples() {

        if (isZero(width) || isZero(height) || gridSize == 1) {
            return List.of(new Sample2D(0, 0));
        }

        List<Sample2D> samples = new LinkedList<>();

        int samplesAmount = gridSize * gridSize;

        double minX = -width / 2;
        double maxX = width / 2;
        double minY = -height / 2;
        double maxY = height / 2;

        double radius = Math.min(width, height) / 2;

        for (int i = 0; i < samplesAmount; i++) {
            double x = random(minX, maxX);
            double y = random(minY, maxY);

            if (shape == SamplingShape.CIRCLE && x * x + y * y > radius * radius) {
                continue;
            }

            samples.add(new Sample2D(x, y));
        }

        return samples;
    }

    /**
     * Generate samples with jittered distribution within the sampling area.
     * Each grid cell contains one sample randomly placed inside that cell.
     * The grid is centered around (0,0). If the sampling shape is CIRCLE,
     * samples outside the circle are discarded.
     *
     * @return list of jittered Sample2D offsets
     */
    private List<Sample2D> generateJitteredSamples() {

        if (isZero(width) || isZero(height) || gridSize == 1) {
            return List.of(new Sample2D(0, 0));
        }

        List<Sample2D> samples = new LinkedList<>();

        double stepX = width / gridSize;
        double stepY = height / gridSize;

        double startX = -width / 2;
        double startY = -height / 2;

        double radius = Math.min(width, height) / 2;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {

                double cellMinX = startX + j * stepX;
                double cellMinY = startY + i * stepY;

                double x = random(cellMinX, cellMinX + stepX);
                double y = random(cellMinY, cellMinY + stepY);

                if (shape == SamplingShape.CIRCLE && x * x + y * y > radius * radius) {
                    continue;
                }

                samples.add(new Sample2D(x, y));
            }
        }

        return samples;
    }
}