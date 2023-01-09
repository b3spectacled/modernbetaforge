package mod.bespectacled.modernbetaforge.api.world.biome;

import net.minecraft.world.biome.Biome;

public interface BiomeResolverOcean {
    
    /**
     * Gets an ocean biome to overwrite the original biome at given coordinates and sufficient depth.
     * 
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param z z-coordinate.
     *
     * @return A biome at given coordinates. May return null, in which case original biome is not replaced.
     */
    Biome getOceanBiome(int x, int y, int z);
    
    /**
     * Gets an ocean biome to overwrite the original biome at given coordinates and sufficient depth.
     * Uses a faster method if available.
     * 
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param z z-coordinate.
     *
     * @return A biome at given coordinates. May return null, in which case original biome is not replaced.
     */
    default Biome getOceanBiomeFast(int x, int y, int z) {
        return this.getOceanBiome(x, y, z);
    }
}
