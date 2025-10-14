package mod.bespectacled.modernbetaforge.world.chunk.source;

import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseHeight;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class Infdev420ChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    public Infdev420ChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        new PerlinOctaveNoise(this.random, 5, true); // Unused in original source
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 5, true);

        this.setBeachOctaveNoise(this.beachOctaveNoise);
        this.setSurfaceOctaveNoise(this.surfaceOctaveNoise);
        this.setForestOctaveNoise(this.forestOctaveNoise);
    }
    
    @Override
    public long getPopulationSeed(int chunkX, int chunkZ) {
        return (long)chunkX * 318279123L + (long)chunkZ * 919871212L;
    }
    
    @Override
    protected NoiseHeight sampleNoiseHeight(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        return NoiseHeight.ZERO;
    }

    @Override
    protected double sampleNoiseOffset(int noiseY, double scale, double depth) {
        double offset = ((double)noiseY - this.settings.baseSize) * this.settings.stretchY;
        
        if (offset < 0.0)
            offset *= 2.0;
        
        return offset;
    }
}
