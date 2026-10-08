package mod.bespectacled.modernbetaforge.compat.thaumcraft;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.BiomeResolverNonReleaseSingle;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ThaumcraftMagicalForestResolver extends BiomeResolverNonReleaseSingle {
    public static final ResourceLocation BIOME_ID = new ResourceLocation(CompatThaumcraft.MOD_ID, "magical_forest");
    
    public ThaumcraftMagicalForestResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BIOME_ID,
            8363L,
            21061L,
            settings.getFloatProperty(CompatThaumcraft.KEY_MAGICAL_FOREST_CHANCE),
            settings.getFloatProperty(CompatThaumcraft.KEY_MAGICAL_FOREST_NOISE_SCALE),
            settings.getBooleanProperty(CompatThaumcraft.KEY_USE_COMPAT),
            Type.FOREST
        );
    }
}
