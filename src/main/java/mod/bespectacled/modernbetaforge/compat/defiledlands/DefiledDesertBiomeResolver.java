package mod.bespectacled.modernbetaforge.compat.defiledlands;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.BiomeResolverNonReleaseSingle;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;

public class DefiledDesertBiomeResolver extends BiomeResolverNonReleaseSingle {
    private static final ResourceLocation BIOME_ID = new ResourceLocation(CompatDefiledLands.MOD_ID, "desert_defiled");
    
    public DefiledDesertBiomeResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BIOME_ID,
            3947L,
            99571L,
            settings.getFloatProperty(CompatDefiledLands.KEY_DEFILED_DESERT_CHANCE),
            settings.getFloatProperty(CompatDefiledLands.KEY_DEFILED_DESERT_NOISE_SCALE),
            settings.getBooleanProperty(CompatDefiledLands.KEY_USE_COMPAT),
            Type.HOT,
            Type.DRY,
            Type.SANDY
        );
    }

}
