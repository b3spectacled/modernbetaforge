package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import javax.annotation.Nullable;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.mersenne.MTRandom;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class PESurfaceBuilder extends BetaSurfaceBuilder {
    public PESurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings);
    }
    
    /*
     * MCPE uses different values to seed random surface generation.
     */
    @Override
    public Random createSurfaceRandom(int chunkX, int chunkZ) {
        long seed = (long)chunkX * 0x14609048 + (long)chunkZ * 0x7ebe2d5;
        
        return new MTRandom(seed);
    }
    
    /*
     * MCPE uses nextFloat() instead of nextDouble()
     */
    @Override
    protected double getSurfaceVariation(@Nullable Random random) {
        return random != null ? random.nextFloat() : 0.0;
    }
}
