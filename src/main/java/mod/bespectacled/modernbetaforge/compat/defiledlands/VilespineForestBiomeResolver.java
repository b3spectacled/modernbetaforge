package mod.bespectacled.modernbetaforge.compat.defiledlands;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.BiomeResolverNonReleaseSingle;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;

public class VilespineForestBiomeResolver extends BiomeResolverNonReleaseSingle {
    private static final ResourceLocation BIOME_ID = new ResourceLocation(CompatDefiledLands.MOD_ID, "forest_vilespine");
    
    public VilespineForestBiomeResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BIOME_ID,
            6277L,
            66683L,
            settings.getFloatProperty(CompatDefiledLands.KEY_VILESPINE_FOREST_CHANCE),
            settings.getFloatProperty(CompatDefiledLands.KEY_VILESPINE_FOREST_NOISE_SCALE),
            settings.getBooleanProperty(CompatDefiledLands.KEY_USE_COMPAT),
            Type.FOREST
        );
    }

}
