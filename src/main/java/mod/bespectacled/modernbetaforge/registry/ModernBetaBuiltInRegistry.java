package mod.bespectacled.modernbetaforge.registry;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.util.VersionUtil;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixer.DataFix;
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
    
    public static void registerDataFixers() {
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.DESERT_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixDesertBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.FOREST_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixForestBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.ICE_DESERT_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixIceDesertBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.PLAINS_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixPlainsBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.RAINFOREST_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixRainforestBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.SAVANNA_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixSavannaBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.SHRUBLAND_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixShrublandBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.SEASONAL_FOREST_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixSeasonalForestBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.SWAMPLAND_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixSwamplandBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.TAIGA_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixTaigaBiomes(factory, jsonObject))
        );
        
        ModernBetaRegistries.DATA_FIX.register(
            NbtTags.TUNDRA_BIOMES,
            new DataFix(version -> VersionUtil.isLowerVersionThan(version, DataFixers.v1_1_0_0), (factory, jsonObject) -> DataFixers.fixTundraBiomes(factory, jsonObject))
        );
    }
}
