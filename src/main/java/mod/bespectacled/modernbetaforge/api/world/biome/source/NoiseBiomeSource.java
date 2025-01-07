package mod.bespectacled.modernbetaforge.api.world.biome.source;

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
    default float getBaseHeight(int x, int z) {
        return this.getBiome(x, z).getBaseHeight();
    }
    
    /**
     * Gets the height variation from the biome at given coordinates.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return The height variation at given coordinates.
     */
    default float getHeightVariation(int x, int z) {
        return this.getBiome(x, z).getHeightVariation();
    }
}
