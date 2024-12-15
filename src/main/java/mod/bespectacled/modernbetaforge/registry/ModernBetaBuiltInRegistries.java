package mod.bespectacled.modernbetaforge.registry;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiCustomizePresets;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixTags;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers.DataFix;
import mod.bespectacled.modernbetaforge.world.biome.source.BetaBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.PEBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.ReleaseBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.SingleBiomeSource;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBetaCave;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.chunk.source.AlphaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.BetaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Classic23aChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.IndevChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev227ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev415ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev420ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev611ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.PEChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.SkylandsChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.surface.Alpha12SurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.AlphaSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.BetaSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.Infdev227SurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.InfdevSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.PESurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.ReleaseSurfaceBuilder;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModernBetaBuiltInRegistries {
    
    public static void registerChunkSources() {
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.BETA.id, BetaChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.ALPHA.id, AlphaChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, SkylandsChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INFDEV_611.id, Infdev611ChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INFDEV_420.id, Infdev420ChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, Infdev415ChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INFDEV_227.id, Infdev227ChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INDEV.id, IndevChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.id, Classic23aChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.PE.id, PEChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.RELEASE.id, ReleaseChunkSource::new);
    }
    
    public static void registerBiomeSources() {
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.BETA.id, BetaBiomeSource::new);
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.SINGLE.id, SingleBiomeSource::new);
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.PE.id, PEBiomeSource::new);
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.RELEASE.id, ReleaseBiomeSource::new);
    }
    
    public static void registerNoiseSources() { }
    
    public static void registerNoiseSettings() {
        ModernBetaRegistries.NOISE_SETTING.register(ModernBetaBuiltInTypes.Chunk.BETA.id, ModernBetaNoiseSettings.BETA);
        ModernBetaRegistries.NOISE_SETTING.register(ModernBetaBuiltInTypes.Chunk.ALPHA.id, ModernBetaNoiseSettings.ALPHA);
        ModernBetaRegistries.NOISE_SETTING.register(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, ModernBetaNoiseSettings.SKYLANDS);
        ModernBetaRegistries.NOISE_SETTING.register(ModernBetaBuiltInTypes.Chunk.INFDEV_611.id, ModernBetaNoiseSettings.INFDEV_611);
        ModernBetaRegistries.NOISE_SETTING.register(ModernBetaBuiltInTypes.Chunk.INFDEV_420.id, ModernBetaNoiseSettings.INFDEV_420);
        ModernBetaRegistries.NOISE_SETTING.register(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, ModernBetaNoiseSettings.INFDEV_415);
        ModernBetaRegistries.NOISE_SETTING.register(ModernBetaBuiltInTypes.Chunk.PE.id, ModernBetaNoiseSettings.PE);
        ModernBetaRegistries.NOISE_SETTING.register(ModernBetaBuiltInTypes.Chunk.RELEASE.id, ModernBetaNoiseSettings.RELEASE);
    }
    
    public static void registerSurfaceBuilders() {
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.BETA.id, BetaSurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.ALPHA.id, AlphaSurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.ALPHA_1_2.id, Alpha12SurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.INFDEV_227.id, Infdev227SurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.INFDEV.id, InfdevSurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.PE.id, PESurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.RELEASE.id, ReleaseSurfaceBuilder::new);
    }
    
    public static void registerCaveCarvers() {
        ModernBetaRegistries.CARVER.register(ModernBetaBuiltInTypes.Carver.BETA.id, (world, chunkSource, settings) -> new MapGenBetaCave(settings));
        ModernBetaRegistries.CARVER.register(ModernBetaBuiltInTypes.Carver.RELEASE.id, (world, chunkSource, settings) -> new MapGenCaves());
    }
    
    public static void registerBlockSources() { }
    
    public static void registerDataFixes() {
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.DESERT_BIOMES, new DataFix(NbtTags.DESERT_BIOMES, DataFixers::fixDesertBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.FOREST_BIOMES, new DataFix(NbtTags.FOREST_BIOMES, DataFixers::fixForestBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.ICE_DESERT_BIOMES, new DataFix(NbtTags.ICE_DESERT_BIOMES, DataFixers::fixIceDesertBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.PLAINS_BIOMES, new DataFix(NbtTags.PLAINS_BIOMES, DataFixers::fixPlainsBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.RAINFOREST_BIOMES, new DataFix(NbtTags.RAINFOREST_BIOMES, DataFixers::fixRainforestBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.SAVANNA_BIOMES, new DataFix(NbtTags.SAVANNA_BIOMES, DataFixers::fixSavannaBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.SHRUBLAND_BIOMES, new DataFix(NbtTags.SHRUBLAND_BIOMES, DataFixers::fixShrublandBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.SEASONAL_FOREST_BIOMES, new DataFix(NbtTags.SEASONAL_FOREST_BIOMES, DataFixers::fixSeasonalForestBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.SWAMPLAND_BIOMES, new DataFix(NbtTags.SWAMPLAND_BIOMES, DataFixers::fixSwamplandBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.TAIGA_BIOMES, new DataFix(NbtTags.TAIGA_BIOMES, DataFixers::fixTaigaBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.TUNDRA_BIOMES, new DataFix(NbtTags.TUNDRA_BIOMES, DataFixers::fixTundraBiomes));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.USE_SANDSTONE, new DataFix(NbtTags.USE_SANDSTONE, DataFixers::fixSandstone));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.SPAWN_WOLVES, new DataFix(NbtTags.SPAWN_WOLVES, DataFixers::fixWolves));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.SURFACE_BUILDER, new DataFix(NbtTags.SURFACE_BUILDER, DataFixers::fixSurfaces));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.SURFACE_SKYLANDS, new DataFix(NbtTags.SURFACE_BUILDER, DataFixers::fixSkylandsSurface));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.FIX_SINGLE_BIOME, new DataFix(NbtTags.DEPR_FIXED_BIOME, DataFixers::fixSingleBiome));
        ModernBetaRegistries.DATA_FIX.register(DataFixTags.FIX_USE_INDEV_HOUSE, new DataFix(NbtTags.DEPR_USE_INDEV_HOUSE, DataFixers::fixIndevHouse));
    }

    @SideOnly(Side.CLIENT)
    public static void registerPresets() {
        ModernBetaClientRegistries.PRESET.register("classic_beta", GuiCustomizePresets.PRESET_CLASSIC_BETA);
        ModernBetaClientRegistries.PRESET.register("classic_alpha_1_2", GuiCustomizePresets.PRESET_CLASSIC_ALPHA_1_2);
        ModernBetaClientRegistries.PRESET.register("classic_alpha", GuiCustomizePresets.PRESET_CLASSIC_ALPHA);
        ModernBetaClientRegistries.PRESET.register("classic_alpha_winter", GuiCustomizePresets.PRESET_CLASSIC_ALPHA_WINTER);
        ModernBetaClientRegistries.PRESET.register("classic_infdev_611", GuiCustomizePresets.PRESET_CLASSIC_INFDEV_611);
        ModernBetaClientRegistries.PRESET.register("classic_infdev_420", GuiCustomizePresets.PRESET_CLASSIC_INFDEV_420);
        ModernBetaClientRegistries.PRESET.register("classic_infdev_415", GuiCustomizePresets.PRESET_CLASSIC_INFDEV_415);
        ModernBetaClientRegistries.PRESET.register("classic_infdev_227", GuiCustomizePresets.PRESET_CLASSIC_INFDEV_227);
        ModernBetaClientRegistries.PRESET.register("classic_indev_island", GuiCustomizePresets.PRESET_CLASSIC_INDEV_ISLAND);
        ModernBetaClientRegistries.PRESET.register("classic_0_0_23a", GuiCustomizePresets.PRESET_CLASSIC_0_0_23A);
        ModernBetaClientRegistries.PRESET.register("classic_skylands", GuiCustomizePresets.PRESET_CLASSIC_SKYLANDS);
        ModernBetaClientRegistries.PRESET.register("beta_skylands", GuiCustomizePresets.PRESET_BETA_SKYLANDS);
        ModernBetaClientRegistries.PRESET.register("beta_pe", GuiCustomizePresets.PRESET_BETA_PE);
        ModernBetaClientRegistries.PRESET.register("beta_realistic", GuiCustomizePresets.PRESET_BETA_REALISTIC);
        ModernBetaClientRegistries.PRESET.register("beta_plus", GuiCustomizePresets.PRESET_BETA_PLUS);
        ModernBetaClientRegistries.PRESET.register("beta_release", GuiCustomizePresets.PRESET_BETA_REALISTIC);
    }
}
