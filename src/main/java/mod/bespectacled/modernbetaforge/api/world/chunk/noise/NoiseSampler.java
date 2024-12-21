package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

public interface NoiseSampler {
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
