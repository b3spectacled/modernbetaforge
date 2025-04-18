package mod.bespectacled.modernbetaforge.api.world.biome;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import net.minecraft.world.biome.Biome;

public interface BiomeResolverCustom {
    /**
     * Gets the biome injection step to replace biome.
     * Should be one of {@link BiomeInjectionStep#PRE_SURFACE} or {@link BiomeInjectionStep#POST_SURFACE}.
     * If {@link BiomeInjectionStep#ALL} is specified, it is treated as {@link BiomeInjectionStep#POST_SURFACE}.
     * 
     * @return The biome injection step to replace biome.
     */
    BiomeInjectionStep getInjectionStep();
    
    /**
     * Gets the predicate with which to check whether to use {@link #getCustomBiome(int, int) getCustomBiome}.
     * 
     * @return The predicate to check whether to resolve the custom biome.
     */
    Predicate<BiomeInjectionContext> getCustomPredicate();
    
    /**
     * Gets a custom biome to overwrite the original biome at given coordinates.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return A biome at given coordinates. May return null, in which case original biome is not replaced.
     */
    Biome getCustomBiome(int x, int z);
}
