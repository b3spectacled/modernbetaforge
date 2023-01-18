package mod.bespectacled.modernbetaforge.api.world.biome;

import net.minecraft.world.biome.Biome;

public interface BiomeResolverOcean {
    
    /**
     * Gets an ocean biome to overwrite the original biome at given coordinates and sufficient depth.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return A biome at given coordinates. May return null, in which case original biome is not replaced.
     */
    Biome getOceanBiome(int x, int z);
}
