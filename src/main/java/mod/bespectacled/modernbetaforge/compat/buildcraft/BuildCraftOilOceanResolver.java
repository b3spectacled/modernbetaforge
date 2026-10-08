package mod.bespectacled.modernbetaforge.compat.buildcraft;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.BiomeResolverNonReleaseSingle;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BuildCraftOilOceanResolver extends BiomeResolverNonReleaseSingle {
    public static final ResourceLocation BIOME_ID = new ResourceLocation(CompatBuildCraftEnergy.MOD_ID, "oil_ocean");
    
    public BuildCraftOilOceanResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BIOME_ID,
            5521L,
            73379L,
            settings.getFloatProperty(CompatBuildCraftEnergy.KEY_OIL_OCEAN_CHANCE),
            settings.getFloatProperty(CompatBuildCraftEnergy.KEY_OIL_OCEAN_NOISE_SCALE),
            settings.getBooleanProperty(CompatBuildCraftEnergy.KEY_USE_COMPAT),
            Type.OCEAN
        );
    }
}
