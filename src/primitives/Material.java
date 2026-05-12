package primitives;

/**
 * Represents material coefficients used by the renderer.
 */
public class Material {
    /**
     * Creates a material with default coefficients.
     */
    public Material() { /* for the Javadoc generator */ }

    /**
     * Ambient reflection coefficient.
     */
    public Double3 kA = Double3.ONE;

    /**
     * Sets ambient reflection coefficient per channel.
     *
     * @param kA ambient coefficient values
     * @return this material
     */
    public Material setkA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets a uniform ambient reflection coefficient.
     *
     * @param kA ambient coefficient for all channels
     * @return this material
     */
    public Material setkA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}