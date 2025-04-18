package mod.bespectacled.modernbetaforge.compat.buildcraft;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverAddSingleBiome;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BuildCraftOilDesertResolver extends BiomeResolverAddSingleBiome {
    public static final ResourceLocation BIOME_ID = new ResourceLocation(CompatBuildCraftEnergy.MOD_ID, "oil_desert");
    private static final List<Type> REQUIRED_TYPES = Arrays.asList(BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);

    private final boolean isReleaseBiomeSource;
    private final boolean useCompat;
    
    public BuildCraftOilDesertResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(BIOME_ID, chunkSource.getSeed(), 1847L, 74531L, settings.getFloatProperty(CompatBuildCraftEnergy.KEY_OIL_DESERT_CHANCE));

        this.isReleaseBiomeSource = settings.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.getRegistryKey());
        this.useCompat = settings.getBooleanProperty(CompatBuildCraftEnergy.KEY_USE_COMPAT);
    }
    
    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        return context -> !this.isReleaseBiomeSource && this.useCompat && BiomeDictionary.getTypes(context.biome).containsAll(REQUIRED_TYPES);
    }
}
