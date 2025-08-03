package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

public interface NoiseHeightSampler {
    /**
     * Samples the scale and depth values at startNoiseX + localNoiseX, startNoiseZ + localNoiseZ.
     * The startNoise and localNoise values should be added to produce the actual noise coordinate; they are kept separate for calculating accurate Beta/PE generation.
     * 
     * @param noiseHeight The noise height from the previous step.
     * @param startNoiseX x-coordinate start of chunk in noise coordinates.
     * @param startNoiseZ z-coordinate start of chunk in noise coordinates.
     * @param localNoiseX Current subchunk index along x-axis.
     * @param localNoiseZ Current subchunk index along z-axis.
     * @param noiseSizeX Number of subchunks in the x-axis of a chunk.
     * @param noiseSizeZ Number of subchunks in the z-axis of a chunk.
     * @return A {@link NoiseHeight} containing the sampled scale and depth values.
     */
    NoiseHeight sampleNoiseHeight(
        NoiseHeight noiseHeight,
        int startNoiseX,
        int startNoiseZ,
        int localNoiseX,
        int localNoiseZ,
        int noiseSizeX,
        int noiseSizeZ
    );
}
