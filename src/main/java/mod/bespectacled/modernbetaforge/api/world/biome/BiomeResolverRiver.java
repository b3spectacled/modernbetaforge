package mod.bespectacled.modernbetaforge.api.world.biome;

import net.minecraft.world.biome.Biome;

public interface BiomeResolverRiver {
    
    /**
     * Gets a beach biome to overwrite the original biome at given coordinates.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return A biome at given coordinates. May return null, in which case original biome is not replaced.
     */
    Biome getRiverBiome(int x, int z);
}
