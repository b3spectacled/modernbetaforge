package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

@FunctionalInterface
public interface NoiseColumnSampler {
    /**
     * Samples noise in a column at a particular x/z-coordinate.
     * Has no effect on terrain generation if registered.
     * 
     * @param buffer The array to store initial densities in for the column.
     * @param startNoiseX x-coordinate in noise coordinates, at the beginning of the chunk.
     * @param startNoiseZ z-coordinate in noise coordinates, at the beginning of the chunk.
     * @param localNoiseX x-coordinate in noise coordinates, the current position in the chunk.
     * @param localNoiseZ z-coordinate in noise coordinates, the current position in the chunk.
     * @param noiseSizeX Number of subchunks in the x-axis of a chunk.
     * @param noiseSizeY Number of subchunks in world height.
     * @param noiseSizeZ Number of subchunks in the z-axis of a chunk.
     */
    public void sampleNoiseColumn(
        double[] buffer,
        int startNoiseX,
        int startNoiseZ,
        int localNoiseX,
        int localNoiseZ,
        int noiseSizeX,
        int noiseSizeY,
        int noiseSizeZ
    );
}