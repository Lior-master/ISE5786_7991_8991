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
     * Convert the generated 2D sample offsets into world points.
     * Each sample (x,y) is transformed to center + axisX * x + axisY * y.
     *
     * @param center the center point of the sampling area
     * @param axisX  the direction corresponding to the sample x axis
     * @param axisY  the direction corresponding to the sample y axis
     * @return list of transformed points in world coordinates
     */
    public List<Point> generatePoints(Point center, Vector axisX, Vector axisY) {
        // Generate points directly according to the current pattern for better efficiency
        return switch (pattern) {
            case REGULAR -> generateRegularPoints(center, axisX, axisY);
            case RANDOM -> generateRandomPoints(center, axisX, axisY);
            case JITTERED -> generateJitteredPoints(center, axisX, axisY);
        };
    }

    /**
     * Generate a regular grid of points within the sampling area.
     *
     * @param center the center point of the sampling area
     * @param axisX  the direction corresponding to the sample x-axis
     * @param axisY  the direction corresponding to the sample y-axis
     * @return list of transformed points in world coordinates
     */
    private List<Point> generateRegularPoints(Point center, Vector axisX, Vector axisY) {
        if (isZero(width) || isZero(height) || gridSize == 1) {
            return List.of(center);
        }

        List<Point> points = new LinkedList<>();

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

                Point p = center;
                if (!isZero(x)) p = p.add(axisX.scale(x));
                if (!isZero(y)) p = p.add(axisY.scale(y));

                points.add(p);
            }
        }

        return points;
    }

    /**
     * Generate random points within the sampling area.
     *
     * @param center the center point of the sampling area
     * @param axisX  the direction corresponding to the sample x-axis
     * @param axisY  the direction corresponding to the sample y-axis
     * @return list of transformed points in world coordinates
     */
    private List<Point> generateRandomPoints(Point center, Vector axisX, Vector axisY) {
        if (isZero(width) || isZero(height) || gridSize == 1) {
            return List.of(center);
        }

        List<Point> points = new LinkedList<>();

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

            Point p = center;
            if (!isZero(x)) p = p.add(axisX.scale(x));
            if (!isZero(y)) p = p.add(axisY.scale(y));

            points.add(p);
        }

        return points;
    }

    /**
     * Generate jittered points within the sampling area.
     *
     * @param center the center point of the sampling area
     * @param axisX  the direction corresponding to the sample x-axis
     * @param axisY  the direction corresponding to the sample y-axis
     * @return list of transformed points in world coordinates
     */
    private List<Point> generateJitteredPoints(Point center, Vector axisX, Vector axisY) {
        if (isZero(width) || isZero(height) || gridSize == 1) {
            return List.of(center);
        }

        List<Point> points = new LinkedList<>();

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

                Point p = center;
                if (!isZero(x)) p = p.add(axisX.scale(x));
                if (!isZero(y)) p = p.add(axisY.scale(y));

                points.add(p);
            }
        }

        return points;
    }

    // ...existing code...

    // ...existing code...
}