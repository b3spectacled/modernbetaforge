package mod.bespectacled.modernbetaforge.api.world.biome;

import net.minecraft.world.biome.Biome;

public interface NoiseBiomeSource {
    /**
     * Gets a biome to use for Release Chunk Source noise generation.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return A biome at given coordinates.
     */
    Biome getBiome(int x, int z);
}
