package mod.bespectacled.modernbetaforge.world.chunk.source;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class Infdev611ChunkSource extends AlphaChunkSource {
    public Infdev611ChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings, true);
    }
    
    @Override
    public long getPopulationSeed(int chunkX, int chunkZ) {
        return (long)chunkX * 318279123L + (long)chunkZ * 919871212L;
    }
}
