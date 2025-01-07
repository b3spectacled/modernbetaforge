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
    
    /**
     * Gets the base height from the biome at given coordinates.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return The base height at given coordinates.
     */
    float getBaseHeight(int x, int z);
    
    /**
     * Gets the height variation from the biome at given coordinates.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return The height variation at given coordinates.
     */
    float getHeightVariation(int x, int z);
}
