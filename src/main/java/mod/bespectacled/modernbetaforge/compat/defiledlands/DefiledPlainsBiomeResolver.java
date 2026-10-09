package mod.bespectacled.modernbetaforge.compat.defiledlands;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.BiomeResolverNonReleaseSingle;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;

public class DefiledPlainsBiomeResolver extends BiomeResolverNonReleaseSingle {
    private static final ResourceLocation BIOME_ID = new ResourceLocation(CompatDefiledLands.MOD_ID, "plains_defiled");
    
    public DefiledPlainsBiomeResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BIOME_ID,
            1511L,
            22549L,
            settings.getFloatProperty(CompatDefiledLands.KEY_DEFILED_PLAINS_CHANCE),
            settings.getFloatProperty(CompatDefiledLands.KEY_DEFILED_PLAINS_NOISE_SCALE),
            settings.getBooleanProperty(CompatDefiledLands.KEY_USE_COMPAT),
            Type.PLAINS
        );
    }

}
