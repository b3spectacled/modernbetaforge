package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverAddSingleBiome;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public abstract class BiomesOPlentyOceanResolver extends BiomeResolverAddSingleBiome {
    private final boolean isReleaseBiomeSource;
    private final boolean useCompat;

    public BiomesOPlentyOceanResolver(
        ChunkSource chunkSource,
        ModernBetaGeneratorSettings settings,
        Biome biome,
        long climateSeed,
        long detailSeed,
        float chance,
        float noiseScale
    ) {
        super(
            biome,
            chunkSource.getSeed(),
            climateSeed,
            detailSeed,
            chance,
            noiseScale
        );
        
        this.isReleaseBiomeSource = settings.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.getRegistryKey());
        this.useCompat = settings.getBooleanProperty(CompatBiomesOPlenty.KEY_USE_COMPAT);
    }
    
    @Override
    public boolean useCustomResolver() {
        return this.useCompat && this.isReleaseBiomeSource;
    }

    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        return context -> hasType(context.getBiome(), Type.OCEAN) && !hasType(context.getBiome(), Type.COLD);
    }
    
    private static boolean hasType(Biome biome, Type type) {
        return BiomeDictionary.hasType(biome, type);
    }
}
