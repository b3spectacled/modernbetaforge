package mod.bespectacled.modernbetaforge.registry;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers;
import mod.bespectacled.modernbetaforge.world.biome.source.BetaBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.PEBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.SingleBiomeSource;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.chunk.source.AlphaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.BetaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev415ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.PEChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.SkylandsChunkSource;

public class ModernBetaBuiltInRegistry {
    
    public static void registerChunkSources() {
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.BETA.id, BetaChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.ALPHA.id, AlphaChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, SkylandsChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, Infdev415ChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.PE.id, PEChunkSource::new);
        ModernBetaRegistries.CHUNK.register(ModernBetaBuiltInTypes.Chunk.RELEASE.id, ReleaseChunkSource::new);
    }
    
    public static void registerBiomeSources() {
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.BETA.id, BetaBiomeSource::new);
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.SINGLE.id, SingleBiomeSource::new);
        ModernBetaRegistries.BIOME.register(ModernBetaBuiltInTypes.Biome.PE.id, PEBiomeSource::new);
    }
    
    public static void registerNoiseSettings() {
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.BETA.id, ModernBetaNoiseSettings.BETA);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.ALPHA.id, ModernBetaNoiseSettings.ALPHA);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, ModernBetaNoiseSettings.SKYLANDS);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, ModernBetaNoiseSettings.INFDEV_415);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.PE.id, ModernBetaNoiseSettings.PE);
        ModernBetaRegistries.NOISE.register(ModernBetaBuiltInTypes.Chunk.RELEASE.id, ModernBetaNoiseSettings.RELEASE);
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
    }
}
