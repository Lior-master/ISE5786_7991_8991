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
     * Diffuse reflection coefficient (Lambert term).
     */
    public Double3 kD = Double3.ZERO;

    /**
     * Specular reflection coefficient (highlight term).
     */
    public Double3 kS = Double3.ZERO;

    /**
     * Transparency coefficient
     */
    public Double3 kT = Double3.ZERO;

    /**
     * Reflection coefficient.
     */
    public Double3 kR = Double3.ZERO;

    /**
     * Shininess exponent used in the specular term.
     */
    public int nShininess = 0;

    /**
     * Sets diffuse reflection coefficient per channel.
     *
     * @param kD diffuse coefficient values
     * @return this material
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets a uniform diffuse reflection coefficient.
     *
     * @param kD diffuse coefficient for all channels
     * @return this material
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Sets specular reflection coefficient per channel.
     *
     * @param kS specular coefficient values
     * @return this material
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets a uniform specular reflection coefficient.
     *
     * @param kS specular coefficient for all channels
     * @return this material
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

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

    /**
     * Sets a uniform transparency coefficient.
     *
     * @param kT transparency coefficient for all channels
     * @return this material
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Sets a uniform transparency coefficient.
     *
     * @param kT transparency coefficient for all channels
     * @return this material
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets reflection coefficient per channel.
     *
     * @param kR reflection coefficient values
     * @return this material
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * Sets a uniform reflection coefficient.
     *
     * @param kR reflection coefficient for all channels
     * @return this material
     */
    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }


    /**
     * Sets the shininess exponent.
     *
     * @param nShininess exponent value
     * @return this material
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}