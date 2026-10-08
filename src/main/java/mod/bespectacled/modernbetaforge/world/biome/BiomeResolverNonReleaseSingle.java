package mod.bespectacled.modernbetaforge.world.biome;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverAddSingleBiome;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;

public abstract class BiomeResolverNonReleaseSingle extends BiomeResolverAddSingleBiome {
    protected final Set<BiomeDictionary.Type> replacementTypes;
    protected final boolean enabled;
    protected final boolean isReleaseBiomeSource;

    public BiomeResolverNonReleaseSingle(
        ChunkSource chunkSource,
        ModernBetaGeneratorSettings settings,
        ResourceLocation biomeKey,
        long climateSeed,
        long detailSeed,
        float chance,
        float noiseScale,
        boolean enabled,
        BiomeDictionary.Type ...replacementTypes
    ) {
        super(biomeKey, chunkSource.getSeed(), climateSeed, detailSeed, chance, noiseScale);

        this.replacementTypes = ImmutableSet.copyOf(Arrays.asList(replacementTypes));
        this.enabled = enabled;
        this.isReleaseBiomeSource = settings.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.getRegistryKey());
    }
    
    @Override
    public boolean useCustomResolver() {
        return this.enabled && !this.isReleaseBiomeSource;
    }

    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        return context -> this.replacementTypes.stream().anyMatch(type -> BiomeDictionary.hasType(context.getBiome(), type));
    }

}
