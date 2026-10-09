package mod.bespectacled.modernbetaforge.compat.buildcraft;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.BiomeResolverNonReleaseSingle;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BuildCraftOilDesertResolver extends BiomeResolverNonReleaseSingle {
    public static final ResourceLocation BIOME_ID = new ResourceLocation(CompatBuildCraftEnergy.MOD_ID, "oil_desert");
    
    public BuildCraftOilDesertResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BIOME_ID,
            1847L,
            74531L,
            settings.getFloatProperty(CompatBuildCraftEnergy.KEY_OIL_DESERT_CHANCE),
            settings.getFloatProperty(CompatBuildCraftEnergy.KEY_OIL_DESERT_NOISE_SCALE),
            settings.getBooleanProperty(CompatBuildCraftEnergy.KEY_USE_COMPAT),
            Type.HOT,
            Type.DRY,
            Type.SANDY
        );
    }
}
