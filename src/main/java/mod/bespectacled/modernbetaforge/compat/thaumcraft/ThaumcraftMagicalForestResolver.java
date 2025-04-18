package mod.bespectacled.modernbetaforge.compat.thaumcraft;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverAddSingleBiome;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ThaumcraftMagicalForestResolver extends BiomeResolverAddSingleBiome {
    public static final ResourceLocation BIOME_ID = new ResourceLocation(CompatThaumcraft.MOD_ID, "magical_forest");
    
    private final boolean isReleaseBiomeSource;
    private final boolean useCompat;
    
    public ThaumcraftMagicalForestResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(BIOME_ID, chunkSource.getSeed(), 8363L, 21061L, settings.getFloatProperty(CompatThaumcraft.KEY_MAGICAL_FOREST_CHANCE));

        this.isReleaseBiomeSource = settings.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.getRegistryKey());
        this.useCompat = settings.getBooleanProperty(CompatThaumcraft.KEY_USE_COMPAT);
    }

    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        return context -> !this.isReleaseBiomeSource && this.useCompat && BiomeDictionary.hasType(context.getBiome(), Type.FOREST);
    }
}
