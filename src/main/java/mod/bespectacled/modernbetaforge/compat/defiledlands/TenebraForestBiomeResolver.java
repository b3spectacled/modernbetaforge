package mod.bespectacled.modernbetaforge.compat.defiledlands;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.BiomeResolverNonReleaseSingle;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;

public class TenebraForestBiomeResolver extends BiomeResolverNonReleaseSingle {
    private static final ResourceLocation BIOME_ID = new ResourceLocation(CompatDefiledLands.MOD_ID, "forest_tenebra");
    
    public TenebraForestBiomeResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BIOME_ID,
            2953L,
            82759L,
            settings.getFloatProperty(CompatDefiledLands.KEY_TENEBRA_FOREST_CHANCE),
            settings.getFloatProperty(CompatDefiledLands.KEY_TENEBRA_FOREST_NOISE_SCALE),
            settings.getBooleanProperty(CompatDefiledLands.KEY_USE_COMPAT),
            Type.FOREST
        );
    }

}
