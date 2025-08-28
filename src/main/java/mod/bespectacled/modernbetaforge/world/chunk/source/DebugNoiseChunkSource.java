package mod.bespectacled.modernbetaforge.world.chunk.source;

import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseHeight;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class DebugNoiseChunkSource extends BetaChunkSource {
    public DebugNoiseChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
    }
    
    @Override
    protected NoiseHeight sampleNoiseHeight(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        return new NoiseHeight(0.01, 8.5);
    }
}
