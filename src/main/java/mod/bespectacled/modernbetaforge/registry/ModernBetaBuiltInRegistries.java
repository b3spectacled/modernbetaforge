package mod.bespectacled.modernbetaforge.registry;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers;
import mod.bespectacled.modernbetaforge.world.biome.source.BetaBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.PEBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.ReleaseBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.SingleBiomeSource;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBetaCave;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.chunk.source.AlphaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.BetaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev415ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev420ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev611ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.PEChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.SkylandsChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.surface.Alpha12SurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.AlphaSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.BetaSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.InfdevSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.PESurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.ReleaseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.SkylandsSurfaceBuilder;
import net.minecraft.world.gen.MapGenCaves;

public class ModernBetaBuiltInRegistries {
    
    public static void registerChunkSources() {
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.BETA.id, BetaChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.ALPHA.id, AlphaChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, SkylandsChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INFDEV_611.id, Infdev611ChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INFDEV_420.id, Infdev420ChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, Infdev415ChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.PE.id, PEChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.RELEASE.id, ReleaseChunkSource::new);
    }
    
    public static void registerBiomeSources() {
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.BETA.id, BetaBiomeSource::new);
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.SINGLE.id, SingleBiomeSource::new);
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.PE.id, PEBiomeSource::new);
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.RELEASE.id, ReleaseBiomeSource::new);
    }
    
    public static void registerNoiseSettings() {
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.BETA.id, ModernBetaNoiseSettings.BETA);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.ALPHA.id, ModernBetaNoiseSettings.ALPHA);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, ModernBetaNoiseSettings.SKYLANDS);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.INFDEV_611.id, ModernBetaNoiseSettings.INFDEV_611);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.INFDEV_420.id, ModernBetaNoiseSettings.INFDEV_420);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, ModernBetaNoiseSettings.INFDEV_415);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.PE.id, ModernBetaNoiseSettings.PE);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.RELEASE.id, ModernBetaNoiseSettings.RELEASE);
    }
    
    public static void registerSurfaceBuilders() {
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.BETA.id, BetaSurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.ALPHA.id, AlphaSurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.ALPHA_1_2.id, Alpha12SurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.SKYLANDS.id, SkylandsSurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.INFDEV.id, InfdevSurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.PE.id, PESurfaceBuilder::new);
        ModernBetaRegistries.SURFACE.register(ModernBetaBuiltInTypes.Surface.RELEASE.id, ReleaseSurfaceBuilder::new);
    }
    
    public static void registerCaveCarvers() {
        ModernBetaRegistries.CARVER.register(ModernBetaBuiltInTypes.Carver.BETA.id, new MapGenBetaCave());
        ModernBetaRegistries.CARVER.register(ModernBetaBuiltInTypes.Carver.RELEASE.id, new MapGenCaves());
    }
    
    public static void registerDataFixes() {
        ModernBetaRegistries.DATA_FIX.register(NbtTags.DESERT_BIOMES, DataFixers::fixDesertBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.FOREST_BIOMES, DataFixers::fixForestBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.ICE_DESERT_BIOMES, DataFixers::fixIceDesertBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.PLAINS_BIOMES, DataFixers::fixPlainsBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.RAINFOREST_BIOMES, DataFixers::fixRainforestBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.SAVANNA_BIOMES, DataFixers::fixSavannaBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.SHRUBLAND_BIOMES, DataFixers::fixShrublandBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.SEASONAL_FOREST_BIOMES, DataFixers::fixSeasonalForestBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.SWAMPLAND_BIOMES, DataFixers::fixSwamplandBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.TAIGA_BIOMES, DataFixers::fixTaigaBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.TUNDRA_BIOMES, DataFixers::fixTundraBiomes);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.USE_SANDSTONE, DataFixers::fixSandstone);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.SPAWN_WOLVES, DataFixers::fixWolves);
        ModernBetaRegistries.DATA_FIX.register(NbtTags.SURFACE_BUILDER, DataFixers::fixSurfaces);
    }
}
