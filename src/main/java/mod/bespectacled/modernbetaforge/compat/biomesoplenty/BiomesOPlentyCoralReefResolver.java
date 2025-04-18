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

public class BiomesOPlentyCoralReefResolver extends BiomeResolverAddSingleBiome {
    private final boolean isReleaseBiomeSource;
    private final boolean useCompat;

    public BiomesOPlentyCoralReefResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(BOPBiomes.coral_reef.get(), chunkSource.getSeed(), 2027L, 26183L, 0.1f);
        
        this.isReleaseBiomeSource = settings.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.getRegistryKey());
        this.useCompat = settings.getBooleanProperty(CompatBiomesOPlenty.KEY_USE_COMPAT);
    }

    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        return context ->
            this.isReleaseBiomeSource &&
            this.useCompat &&
            BiomeDictionary.hasType(context.getBiome(), Type.OCEAN) &&
            !BiomeDictionary.hasType(context.getBiome(), Type.COLD);
    }
}
