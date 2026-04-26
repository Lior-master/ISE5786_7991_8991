package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class Camera implements Cloneable {

    // ===== Fields =====
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

    // ===== Constructor =====
    private Camera() {
    }

    // ===== Builder access =====
    public static Builder getBuilder() {
        return new Builder();
    }

    // ===== Builder =====
    public static class Builder {

        private final Camera _camera = new Camera();

        // ===== SETTERS =====

        public Builder setLocation(Point location) {
            _camera._p0 = location;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            _camera._vTo = vTo;
            _camera._vUp = vUp;
            return this;
        }

        public Builder setDirection(Point target, Vector up) {
            _camera._vTo = target.subtract(_camera._p0);
            _camera._vUp = up;
            return this;
        }

        public Builder setDirection(Point target) {
            _camera._vTo = target.subtract(_camera._p0);
            _camera._vUp = Vector.AXIS_Y;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        // ===== BUILD =====
        public Camera build() {

            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            calcVectors();
            calcVpCenter();
            calcPixelSize();

            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        // ===== PRIVATE METHODS =====

        private void checkResolution() {
            if (_camera._nX <= 0 || _camera._nY <= 0)
                throw new IllegalArgumentException("Invalid resolution");
        }

        private void checkLocationAndDirection() {
            if (_camera._p0 == null || _camera._vTo == null || _camera._vUp == null)
                throw new IllegalArgumentException("Missing camera orientation");

            _camera._vTo = _camera._vTo.normalize();
            _camera._vUp = _camera._vUp.normalize();

            if (_camera._vTo.dotProduct(_camera._vUp) != 0)
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");

            _camera._vRight = _camera._vTo.crossProduct(_camera._vUp).normalize();
        }

        private void checkViewPlane() {
            if (_camera._width <= 0 || _camera._height <= 0 || _camera._distance <= 0)
                throw new IllegalArgumentException("Invalid view plane");
        }

        private void calcVectors() {
            // déjà fait dans checkOrientation (ok)
        }

        private void calcVpCenter() {
            _camera._vpCenter = _camera._p0.add(
                    _camera._vTo.scale(_camera._distance));
        }

        private void calcPixelSize() {
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
        }
    }

    // ===== CLONE =====
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // ===== CONSTRUCT RAY =====
    public Ray constructRay(int column, int row) {
        return null; // tu feras après
    }
}