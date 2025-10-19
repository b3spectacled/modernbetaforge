package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.util.mersenne.MTRandom;
import mod.bespectacled.modernbetaforge.world.biome.source.PEBiomeSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class PEChunkSource extends BetaChunkSource {
    public PEChunkSource(long seed, ModernBetaGeneratorSettings settings, BiomeSource biomeSource) {
        super(seed, settings, biomeSource);
    }
    
    @Override
    protected Random createRandom(long seed) {
        // Use Mersenne Twister random instead of Java random
        return new MTRandom(seed);
    }

    @Override
    protected ClimateSampler createClimateSampler(long seed, ModernBetaGeneratorSettings settings) {
        return new PEBiomeSource(seed, settings);
    }
}
