package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

public interface NoiseSampler {
    /**
     * Sample density at a particular point.
     * This will affect terrain generation if registered.
     * 
     * @param density The terrain density from the previous step.
     * @param noiseX x-coordinate in noise coordinates.
     * @param noiseY y-coordinate in noise coordinates.
     * @param noiseZ z-coordinate in noise coordinates.
     * @param noiseSizeX Number of subchunks in the x-axis of a chunk.
     * @param noiseSizeY Number of subchunks in world height.
     * @param noiseSizeZ Number of subchunks in the z-axis of a chunk.
     * @return The modified terrain density. The density parameter should be returned if no work has been done.
     */
    double sample(
        double density,
        int noiseX,
        int noiseY,
        int noiseZ,
        int noiseSizeX,
        int noiseSizeY,
        int noiseSizeZ
    );
}
