package renderer;

import java.util.MissingResourceException;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.isZero;

/**
 * Represents a pinhole camera in 3D space.
 * <p>
 * A camera is defined by its position and orthonormal basis vectors:
 * forward ({@code vTo}), up ({@code vUp}) and right ({@code vRight}).
 * It also stores view-plane size, distance, and resolution required to
 * construct rays through pixels.
 * </p>
 */
public class Camera implements Cloneable {

    /** Camera origin point. */
    private Point _p0;

    /** Forward camera axis. */
    private Vector _vTo;
    /** Up camera axis. */
    private Vector _vUp;
    /** Right camera axis. */
    private Vector _vRight;

    /** View-plane width. */
    private double _width;
    /** View-plane height. */
    private double _height;
    /** Distance from camera to view plane. */
    private double _distance;

    /** Horizontal resolution (columns). */
    private int _nX = 1;
    /** Vertical resolution (rows). */
    private int _nY = 1;

    /** Image buffer writer. */
    private ImageWriter _imageWriter;
    /** Active ray tracer implementation. */
    private RayTracerBase _rayTracer;

    /** Cached view-plane center point. */
    private Point _vpCenter;
    /** Cached pixel width. */
    private double _pixelWidth;
    /** Cached pixel height. */
    private double _pixelHeight;

    /**
     * Private constructor. Use {@link #getBuilder()} to create instances.
     */
    private Camera() {
    }

    /**
     * Creates a new builder for configuring and constructing a camera.
     *
     * @return a fresh {@link Builder} instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Builder for {@link Camera}.
     * <p>
     * Supports direction setup using either explicit vectors or a target point.
     * The camera is validated during {@link #build()}.
     * </p>
     */
    public static class Builder {

        /**
         * Private constructor. Use {@link Camera#getBuilder()}.
         */
        private Builder() {/* to satisfy Javadoc generator */ }

        private final Camera _camera = new Camera();

        private Vector _to;
        private Point _target;
        private Vector _up = Vector.AXIS_Y;

        /**
         * Sets the camera location.
         *
         * @param location camera origin point
         * @return this builder
         */
        public Builder setLocation(Point location) {
            _camera._p0 = location;
            return this;
        }

        /**
         * Sets camera direction using explicit forward and up vectors.
         *
         * @param to forward direction
         * @param up up direction
         * @return this builder
         */
        public Builder setDirection(Vector to, Vector up) {
            _to = to;
            _target = null;
            _up = up;
            return this;
        }

        /**
         * Sets camera direction using a target point and explicit up vector.
         *
         * @param target point the camera should face
         * @param up     up direction
         * @return this builder
         */
        public Builder setDirection(Point target, Vector up) {
            _to = null;
            _target = target;
            _up = up;
            return this;
        }

        /**
         * Sets camera direction using only a target point.
         * The up vector defaults to {@link Vector#AXIS_Y}.
         *
         * @param target point the camera should face
         * @return this builder
         */
        public Builder setDirection(Point target) {
            _to = null;
            _target = target;
            _up = Vector.AXIS_Y;
            return this;
        }

        /**
         * Sets view-plane physical size.
         *
         * @param width  view-plane width
         * @param height view-plane height
         * @return this builder
         */
        public Builder setVpSize(double width, double height) {
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets distance from camera origin to view plane.
         *
         * @param distance view-plane distance
         * @return this builder
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * Sets pixel resolution of the view plane.
         *
         * @param nX number of columns
         * @param nY number of rows
         * @return this builder
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer strategy for the camera.
         *
         * @param scene scene to trace
         * @param type  ray tracer implementation type
         * @return this builder
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                _camera._rayTracer = new SimpleRayTracer(scene);
                return this;
            }

            throw new IllegalArgumentException("Unsupported ray tracer type: " + type);
        }

        /**
         * Validates all required camera data and returns a built instance.
         *
         * @return a configured {@link Camera}
         * @throws MissingResourceException if location or direction data is missing
         * @throws IllegalArgumentException if size, distance, resolution, or orientation is invalid
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if (_camera._rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }

            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        /**
         * Validates resolution values.
         *
         * @throws IllegalArgumentException if any resolution component is non-positive
         */
        private void checkResolution() {
            if (_camera._nX <= 0 || _camera._nY <= 0) {
                throw new IllegalArgumentException("Resolution values must be positive");
            }

            _camera._imageWriter = new ImageWriter(_camera._nX, _camera._nY);
        }

        /**
         * Validates location and direction inputs and computes camera basis vectors.
         *
         * @throws MissingResourceException if required location/direction input is missing
         * @throws IllegalArgumentException if forward and up vectors are parallel
         */
        private void checkLocationAndDirection() {
            if (_camera._p0 == null || _up == null || (_to == null && _target == null)) {
                throw new MissingResourceException(
                        "Missing camera location or direction",
                        Camera.class.getName(),
                        ""
                );
            }

            Vector vTo = _to != null ? _to : _target.subtract(_camera._p0);

            _camera._vTo = vTo.normalize();

            try {
                _camera._vRight = _camera._vTo.crossProduct(_up).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Camera direction and up vector cannot be parallel");
            }

            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
        }

        /**
         * Validates view-plane size/distance and computes cached pixel geometry.
         *
         * @throws IllegalArgumentException if size or distance is non-positive
         */
        private void checkViewPlane() {
            if (_camera._width <= 0 || _camera._height <= 0 || _camera._distance <= 0) {
                throw new IllegalArgumentException("View plane size and distance must be positive");
            }

            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
        }
    }

    /**
     * Creates a shallow clone of this camera.
     *
     * @return cloned camera object
     * @throws CloneNotSupportedException if cloning is not supported
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Constructs a ray from the camera origin through a pixel center.
     *
     * @param xIndex pixel column index (x / j)
     * @param yIndex pixel row index (y / i)
     * @return ray from camera origin through the requested pixel
     */
    public Ray constructRay(int xIndex, int yIndex) {
        Point pIJ = _vpCenter;

        double xJ = (xIndex - (_nX - 1) / 2d) * _pixelWidth;
        double yI = -(yIndex - (_nY - 1) / 2d) * _pixelHeight;

        if (!isZero(xJ)) {
            pIJ = pIJ.add(_vRight.scale(xJ));
        }

        if (!isZero(yI)) {
            pIJ = pIJ.add(_vUp.scale(yI));
        }

        return new Ray(_p0, pIJ.subtract(_p0));
    }

    /**
     * Renders the image by tracing one ray per pixel.
     *
     * @return this camera
     */
    public Camera renderImage() {
        if (_imageWriter == null || _rayTracer == null) {
            throw new MissingResourceException("Missing render resources", Camera.class.getName(), "");
        }

        for (int yIndex = 0; yIndex < _nY; yIndex++) {
            for (int xIndex = 0; xIndex < _nX; xIndex++) {
                castRay(xIndex, yIndex);
            }
        }

        return this;
    }

    /**
     * Casts a ray through the given pixel and writes its color.
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row index
     */
    private void castRay(int xIndex, int yIndex) {
        Ray ray = constructRay(xIndex, yIndex);
        Color color = _rayTracer.traceRay(ray);
        _imageWriter.writePixel(xIndex, yIndex, color);
    }

    /**
     * Draws a grid over the rendered image.
     *
     * @param interval grid spacing in pixels
     * @param color    grid line color
     * @return this camera
     */
    public Camera printGrid(int interval, Color color) {
        if (interval <= 0) {
            throw new IllegalArgumentException("Grid interval must be positive");
        }

        if (_imageWriter == null) {
            throw new MissingResourceException("Missing image writer", Camera.class.getName(), "");
        }

        for (int yIndex = 0; yIndex < _nY; yIndex++) {
            for (int xIndex = 0; xIndex < _nX; xIndex++) {
                if (xIndex % interval == 0 || yIndex % interval == 0) {
                    _imageWriter.writePixel(xIndex, yIndex, color);
                }
            }
        }

        return this;
    }

    /**
     * Writes the current image buffer to a PNG file.
     *
     * @param fileName output file name without extension
     * @return this camera
     */
    public Camera writeToImage(String fileName) {
        if (_imageWriter == null) {
            throw new MissingResourceException("Missing image writer", Camera.class.getName(), "");
        }

        _imageWriter.writeToImage(fileName);
        return this;
    }
}