package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import java.util.function.Predicate;

import biomesoplenty.api.biome.BOPBiomes;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.BiomeResolverNonReleaseSingle;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomesOPlentyKelpForestResolver extends BiomeResolverNonReleaseSingle {
    public BiomesOPlentyKelpForestResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BOPBiomes.kelp_forest.get().getRegistryName(),
            3469L,
            55579L,
            settings.getFloatProperty(CompatBiomesOPlenty.KEY_KELP_FOREST_CHANCE),
            settings.getFloatProperty(CompatBiomesOPlenty.KEY_KELP_FOREST_NOISE_SCALE),
            settings.getBooleanProperty(CompatBiomesOPlenty.KEY_USE_COMPAT),
            Type.OCEAN
        );
    }

    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        Predicate<BiomeInjectionContext> predicate = super.getCustomPredicate();
        
        return context -> predicate.test(context) && !BiomeDictionary.hasType(context.getBiome(), Type.COLD);
    }
}
