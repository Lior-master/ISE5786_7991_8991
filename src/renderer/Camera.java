package renderer;

import java.util.MissingResourceException;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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

    private Point _p0;

    private Vector _vTo;
    private Vector _vUp;
    private Vector _vRight;

    private double _width;
    private double _height;
    private double _distance;

    private int _nX = 1;
    private int _nY = 1;

    private Point _vpCenter;
    private double _pixelWidth;
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
         * Validates all required camera data and returns a built instance.
         *
         * @return a configured {@link Camera}
         * @throws MissingResourceException  if location or direction data is missing
         * @throws IllegalArgumentException if size, distance, resolution, or orientation is invalid
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

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
        }

        /**
         * Validates location and direction inputs and computes camera basis vectors.
         *
         * @throws MissingResourceException  if required location/direction input is missing
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
}