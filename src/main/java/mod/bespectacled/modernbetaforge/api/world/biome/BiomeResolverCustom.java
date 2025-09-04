package mod.bespectacled.modernbetaforge.api.world.biome;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import net.minecraft.world.biome.Biome;

public interface BiomeResolverCustom {
    /**
     * Check if the predicate should be added to the list of injection rules.
     * 
     * @return Whether the predicate should be added to the list of injection rules.
     */
    default boolean useCustomResolver() {
        return true;
    }
    
    /**
     * Gets the predicate with which to check whether to use {@link #getCustomBiome(int, int) getCustomBiome}.
     * 
     * @return The predicate to check whether to resolve the custom biome.
     */
    Predicate<BiomeInjectionContext> getCustomPredicate();
    
    /**
     * @deprecated
     * Gets a custom biome to overwrite the original biome at given coordinates.
     
     * @param x x-coordinate in block coordinates
     * @param z z-coordinate in block coordinates
     * @return A biome at given coordinates. May return null, in which case original biome is not replaced.
     */
    Biome getCustomBiome(int x, int z);
    
    /**
     * Gets a custom biome to overwrite the original biome at given coordinates.
     
     * @param x x-coordinate in block coordinates
     * @param z z-coordinate in block coordinates
     * @param biomeSource The primary biome source
     * @return A biome at given coordinates. May return null, in which case original biome is not replaced.
     */
    default Biome getCustomBiome(int x, int z, BiomeSource biomeSource) {
        return this.getCustomBiome(x, z);
    }
}
