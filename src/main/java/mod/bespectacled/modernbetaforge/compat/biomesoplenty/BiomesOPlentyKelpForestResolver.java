package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import java.util.function.Predicate;

import biomesoplenty.api.biome.BOPBiomes;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverAddSingleBiome;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomesOPlentyKelpForestResolver extends BiomeResolverAddSingleBiome {
    private final boolean isReleaseBiomeSource;
    private final boolean useCompat;

    public BiomesOPlentyKelpForestResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(BOPBiomes.kelp_forest.get(), chunkSource.getSeed(), 3469L, 55579L, 0.25f);
        
        this.isReleaseBiomeSource = settings.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.getRegistryKey());
        this.useCompat = settings.getBooleanProperty(CompatBiomesOPlenty.KEY_USE_COMPAT);
    }
    
    @Override
    public boolean useCustomResolver() {
        return this.useCompat && this.isReleaseBiomeSource;
    }

    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        return context ->
            BiomeDictionary.hasType(context.getBiome(), Type.OCEAN) &&
            !BiomeDictionary.hasType(context.getBiome(), Type.COLD);
    }
}
