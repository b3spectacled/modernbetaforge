package mod.bespectacled.modernbetaforge.api.world.biome.source;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.biome.Biome;

public abstract class BiomeSource {
    protected final long seed;
    protected final ModernBetaGeneratorSettings settings;
    
    /**
     * Constructs a Modern Beta biome source, given the world's seed and generator settings.
     * 
     * @param seed The world seed.
     * @param settings The generator settings.
     */
    public BiomeSource(long seed, ModernBetaGeneratorSettings settings) {
        this.seed = seed;
        this.settings = settings;
    }
    
    /**
     * Gets a biome for biome source at given coordinates.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return A biome at given biome coordinates.
     */
    public abstract Biome getBiome(int x, int z);
}
