package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.util.JsonUtils;

public class DataFixers {
    private static final Gson GSON = new Gson();
    
    private static final Map<String, Boolean> SHOULD_GEN_SANDSTONE = ImmutableMap.<String, Boolean>builder()
        .put(ModernBetaBuiltInTypes.Chunk.BETA.id, true)
        .put(ModernBetaBuiltInTypes.Chunk.ALPHA.id, false)
        .put(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, true)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_611.id, false)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_420.id, false)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, false)
        .put(ModernBetaBuiltInTypes.Chunk.PE.id, true)
        .put(ModernBetaBuiltInTypes.Chunk.RELEASE.id, true)
        .build();
    
    private static final Map<String, Boolean> SHOULD_SPAWN_WOLVES = ImmutableMap.<String, Boolean>builder()
        .put(ModernBetaBuiltInTypes.Chunk.BETA.id, true)
        .put(ModernBetaBuiltInTypes.Chunk.ALPHA.id, false)
        .put(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, true)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_611.id, false)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_420.id, false)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, false)
        .put(ModernBetaBuiltInTypes.Chunk.PE.id, true)
        .put(ModernBetaBuiltInTypes.Chunk.RELEASE.id, true)
        .build();
    
    private static final Map<String, String> SURFACE_BUILDERS = ImmutableMap.<String, String>builder()
        .put(ModernBetaBuiltInTypes.Chunk.BETA.id, ModernBetaBuiltInTypes.Surface.BETA.id)
        .put(ModernBetaBuiltInTypes.Chunk.ALPHA.id, ModernBetaBuiltInTypes.Surface.ALPHA.id)
        .put(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id, ModernBetaBuiltInTypes.Surface.SKYLANDS.id)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_611.id, ModernBetaBuiltInTypes.Surface.INFDEV.id)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_420.id, ModernBetaBuiltInTypes.Surface.INFDEV.id)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_415.id, ModernBetaBuiltInTypes.Surface.INFDEV.id)
        .put(ModernBetaBuiltInTypes.Chunk.PE.id, ModernBetaBuiltInTypes.Surface.PE.id)
        .put(ModernBetaBuiltInTypes.Chunk.RELEASE.id, ModernBetaBuiltInTypes.Surface.INFDEV.id)
        .build();
    
    public static void fixDesertBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
         Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.DESERT_BIOMES);
         
         factory.desertBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.desertBiomeBase);
         factory.desertBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.desertBiomeOcean);
         factory.desertBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.desertBiomeBeach);
    }
    
    public static void fixForestBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.FOREST_BIOMES);
        
        factory.forestBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.forestBiomeBase);
        factory.forestBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.forestBiomeOcean);
        factory.forestBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.forestBiomeBeach);
   }
    
    public static void fixIceDesertBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.ICE_DESERT_BIOMES);
        
        factory.iceDesertBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.iceDesertBiomeBase);
        factory.iceDesertBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.iceDesertBiomeOcean);
        factory.iceDesertBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.iceDesertBiomeBeach);
   }
    
    public static void fixPlainsBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.PLAINS_BIOMES);
        
        factory.plainsBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.plainsBiomeBase);
        factory.plainsBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.plainsBiomeOcean);
        factory.plainsBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.plainsBiomeBeach);
   }
    
    public static void fixRainforestBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.RAINFOREST_BIOMES);
        
        factory.rainforestBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.rainforestBiomeBase);
        factory.rainforestBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.rainforestBiomeOcean);
        factory.rainforestBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.rainforestBiomeBeach);
   }
    
    public static void fixSavannaBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SAVANNA_BIOMES);
        
        factory.savannaBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.savannaBiomeBase);
        factory.savannaBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.savannaBiomeOcean);
        factory.savannaBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.savannaBiomeBeach);
   }
    
    public static void fixShrublandBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SHRUBLAND_BIOMES);
        
        factory.shrublandBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.shrublandBiomeBase);
        factory.shrublandBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.shrublandBiomeOcean);
        factory.shrublandBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.shrublandBiomeBeach);
   }
    
    public static void fixSeasonalForestBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SEASONAL_FOREST_BIOMES);
        
        factory.seasonalForestBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.seasonalForestBiomeBase);
        factory.seasonalForestBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.seasonalForestBiomeOcean);
        factory.seasonalForestBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.seasonalForestBiomeBeach);
   }
    
    public static void fixSwamplandBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SWAMPLAND_BIOMES);
        
        factory.swamplandBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.swamplandBiomeBase);
        factory.swamplandBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.swamplandBiomeOcean);
        factory.swamplandBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.swamplandBiomeBeach);
   }
    
    public static void fixTaigaBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.TAIGA_BIOMES);
        
        factory.taigaBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.taigaBiomeBase);
        factory.taigaBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.taigaBiomeOcean);
        factory.taigaBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.taigaBiomeBeach);
   }
    
    public static void fixTundraBiomes(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.TUNDRA_BIOMES);
        
        factory.tundraBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.tundraBiomeBase);
        factory.tundraBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.tundraBiomeOcean);
        factory.tundraBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.tundraBiomeBeach);
   }
    
    public static void fixSandstone(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        factory.useSandstone = SHOULD_GEN_SANDSTONE.getOrDefault(factory.chunkSource, factory.useSandstone);
    }
    
    public static void fixWolves(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        factory.spawnWolves = SHOULD_SPAWN_WOLVES.getOrDefault(factory.chunkSource, factory.spawnWolves);
    }
    
    public static void fixSurfaces(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        factory.surfaceBuilder = SURFACE_BUILDERS.getOrDefault(factory.chunkSource, factory.surfaceBuilder);
    }
    
    @SuppressWarnings("unchecked")
    private static Map<String, String> deserializeBiomeMap(JsonObject jsonObject, String tag) {
        try {
            return GSON.fromJson(JsonUtils.getString(jsonObject, tag), LinkedHashMap.class);
        } catch (Exception e) {
            ModernBeta.log(Level.INFO, String.format("Couldn't deserialize tag '%s'.", tag));
        }
        
        return new LinkedHashMap<>();
    }
}
