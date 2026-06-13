package renderer.sampling;

/**
 * Sampling pattern for 2D sampling.
 */
public enum SamplingPattern {
    /**
     * Regular sampling pattern, where samples are evenly spaced in a grid.
     */
    REGULAR,

    /**
     * Random sampling pattern, where samples are randomly distributed within the sampling area.
     */
    RANDOM,

    /**
     * Jittered sampling pattern, where samples are placed in a regular grid but with random offsets (jitter) applied to each sample position.
     */
    JITTERED
}
