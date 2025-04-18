package mod.bespectacled.modernbetaforge.compat.buildcraft;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverAddSingleBiome;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BuildCraftOilOceanResolver extends BiomeResolverAddSingleBiome {
    public static final ResourceLocation BIOME_ID = new ResourceLocation(CompatBuildCraftEnergy.MOD_ID, "oil_ocean");

    private final boolean isReleaseBiomeSource;
    private final boolean useCompat;
    
    public BuildCraftOilOceanResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(BIOME_ID, chunkSource.getSeed(), 5521L, 73379L, settings.getFloatProperty(CompatBuildCraftEnergy.KEY_OIL_OCEAN_CHANCE));

        this.isReleaseBiomeSource = settings.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.getRegistryKey());
        this.useCompat = settings.getBooleanProperty(CompatBuildCraftEnergy.KEY_USE_COMPAT);
    }
    
    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        return context -> !this.isReleaseBiomeSource && this.useCompat && BiomeDictionary.hasType(context.biome, Type.OCEAN);
    }
}
