package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.NoiseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class InfdevSurfaceBuilder extends NoiseSurfaceBuilder {
    public InfdevSurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings, true, true, true);
    }
    
    @Override
    public boolean isBeach(int x, int z, Random random) {
        double noise = this.getBeachOctaveNoise().sample(x * 0.03125, z * 0.03125, 0.0);
        
        return noise + this.getSurfaceVariation(random) * 0.2 > 0.0;
    }
    
    @Override
    public boolean isGravelBeach(int x, int z, Random random) {
        double noise = this.getBeachOctaveNoise().sample(z * 0.03125, 109.0134, x * 0.03125);
        
        return noise + this.getSurfaceVariation(random) * 0.2 > 3.0;
    }
    
    @Override
    public int sampleSurfaceDepth(int x, int z, Random random) {
        double noise = this.getSurfaceOctaveNoise().sample(x * 0.03125 * 2.0, z * 0.03125 * 2.0);
        
        return (int)(noise / 3.0 + 3.0 + this.getSurfaceVariation(random) * 0.25);
    }
    
    @Override
    public boolean isBasin(int surfaceDepth) {
        return surfaceDepth <= 0;
    }
}
