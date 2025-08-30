package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.compat.biomesoplenty.CompatBiomesOPlenty;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.climate.ClimateMap;
import mod.bespectacled.modernbetaforge.world.biome.layer.GenLayerVersion;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.init.Blocks;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DataFixers {
    private static final Gson GSON = new Gson();
    private static final ModernBetaGeneratorSettings DEFAULTS = ModernBetaGeneratorSettings.build();
    
    private static final Map<String, Boolean> SHOULD_GEN_SANDSTONE = ImmutableMap.<String, Boolean>builder()
        .put(ModernBetaBuiltInTypes.Chunk.BETA.getId(), true)
        .put(ModernBetaBuiltInTypes.Chunk.ALPHA.getId(), false)
        .put(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getId(), true)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getId(), false)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_420.getId(), false)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_415.getId(), false)
        .put(ModernBetaBuiltInTypes.Chunk.PE.getId(), true)
        .put(ModernBetaBuiltInTypes.Chunk.RELEASE.getId(), true)
        .build();
    
    private static final Map<String, Boolean> SHOULD_SPAWN_WOLVES = ImmutableMap.<String, Boolean>builder()
        .put(ModernBetaBuiltInTypes.Chunk.BETA.getId(), true)
        .put(ModernBetaBuiltInTypes.Chunk.ALPHA.getId(), false)
        .put(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getId(), true)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getId(), false)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_420.getId(), false)
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_415.getId(), false)
        .put(ModernBetaBuiltInTypes.Chunk.PE.getId(), true)
        .put(ModernBetaBuiltInTypes.Chunk.RELEASE.getId(), true)
        .build();
    
    private static final Map<String, String> SURFACE_BUILDERS = ImmutableMap.<String, String>builder()
        .put(ModernBetaBuiltInTypes.Chunk.BETA.getId(), ModernBetaBuiltInTypes.Surface.BETA.getId())
        .put(ModernBetaBuiltInTypes.Chunk.ALPHA.getId(), ModernBetaBuiltInTypes.Surface.ALPHA.getId())
        .put(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getId(), ModernBetaBuiltInTypes.Surface.SKYLANDS.getId())
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getId(), ModernBetaBuiltInTypes.Surface.INFDEV.getId())
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_420.getId(), ModernBetaBuiltInTypes.Surface.INFDEV.getId())
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_415.getId(), ModernBetaBuiltInTypes.Surface.INFDEV.getId())
        .put(ModernBetaBuiltInTypes.Chunk.PE.getId(), ModernBetaBuiltInTypes.Surface.PE.getId())
        .put(ModernBetaBuiltInTypes.Chunk.RELEASE.getId(), ModernBetaBuiltInTypes.Surface.INFDEV.getId())
        .build();
    
    private static final Map<String, String> WORLD_SPAWNERS = ImmutableMap.<String, String>builder()
        .put(ModernBetaBuiltInTypes.Chunk.BETA.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.BETA.getRegistryString())
        .put(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.BETA.getRegistryString())
        .put(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.DEFAULT.getRegistryString())
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.INFDEV.getRegistryString())
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_420.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.INFDEV.getRegistryString())
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_415.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.INFDEV.getRegistryString())
        .put(ModernBetaBuiltInTypes.Chunk.INFDEV_227.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.INFDEV.getRegistryString())
        .put(ModernBetaBuiltInTypes.Chunk.PE.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.PE.getRegistryString())
        .put(ModernBetaBuiltInTypes.Chunk.RELEASE.getRegistryString(), ModernBetaBuiltInTypes.WorldSpawner.DEFAULT.getRegistryString())
        .build();
    
    public static void fixDesertBiomes(JsonObject jsonObject) {
         Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.DESERT_BIOMES);
         
         String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.desertBiomeBase.toString());
         String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.desertBiomeOcean.toString());
         String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.desertBiomeBeach.toString());
         
         jsonObject.addProperty(NbtTags.DESERT_BIOME_BASE, biomeBase);
         jsonObject.addProperty(NbtTags.DESERT_BIOME_OCEAN, biomeOcean);
         jsonObject.addProperty(NbtTags.DESERT_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixForestBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.FOREST_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.forestBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.forestBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.forestBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.FOREST_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.FOREST_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.FOREST_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixIceDesertBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.ICE_DESERT_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.iceDesertBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.iceDesertBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.iceDesertBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.ICE_DESERT_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.ICE_DESERT_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.ICE_DESERT_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixPlainsBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.PLAINS_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.plainsBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.plainsBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.plainsBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.PLAINS_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.PLAINS_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.PLAINS_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixRainforestBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.RAINFOREST_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.rainforestBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.rainforestBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.rainforestBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.RAINFOREST_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.RAINFOREST_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.RAINFOREST_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixSavannaBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SAVANNA_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.savannaBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.savannaBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.savannaBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.SAVANNA_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.SAVANNA_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.SAVANNA_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixShrublandBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SHRUBLAND_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.shrublandBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.shrublandBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.shrublandBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.SHRUBLAND_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.SHRUBLAND_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.SHRUBLAND_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixSeasonalForestBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SEASONAL_FOREST_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.seasonalForestBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.seasonalForestBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.seasonalForestBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.SEASONAL_FOREST_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.SEASONAL_FOREST_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.SEASONAL_FOREST_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixSwamplandBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SWAMPLAND_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.swamplandBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.swamplandBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.swamplandBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.SWAMPLAND_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.SWAMPLAND_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.SWAMPLAND_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixTaigaBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.TAIGA_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.taigaBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.taigaBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.taigaBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.TAIGA_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.TAIGA_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.TAIGA_BIOME_BEACH, biomeBeach);
    }

    public static void fixTundraBiomes(JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.TUNDRA_BIOMES);
        
        String biomeBase = biomeMap.getOrDefault(NbtTags.DEPR_BASE_BIOME, DEFAULTS.tundraBiomeBase.toString());
        String biomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.tundraBiomeOcean.toString());
        String biomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, DEFAULTS.tundraBiomeBeach.toString());
        
        jsonObject.addProperty(NbtTags.TUNDRA_BIOME_BASE, biomeBase);
        jsonObject.addProperty(NbtTags.TUNDRA_BIOME_OCEAN, biomeOcean);
        jsonObject.addProperty(NbtTags.TUNDRA_BIOME_BEACH, biomeBeach);
    }
    
    public static void fixSandstone(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        jsonObject.addProperty(NbtTags.USE_SANDSTONE, SHOULD_GEN_SANDSTONE.getOrDefault(registryString, DEFAULTS.useSandstone));
    }
    
    public static void fixWolves(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        jsonObject.addProperty(NbtTags.SPAWN_WOLVES, SHOULD_SPAWN_WOLVES.getOrDefault(registryString, DEFAULTS.spawnWolves));
    }
    
    public static void fixSurfaces(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        jsonObject.addProperty(NbtTags.SURFACE_BUILDER, SURFACE_BUILDERS.getOrDefault(registryString, DEFAULTS.surfaceBuilder.getPath()));
    }
    
    public static void fixBiomeDepthScale(JsonObject jsonObject) {
        jsonObject.addProperty(NbtTags.USE_BIOME_DEPTH_SCALE, false);
    }
    
    public static void fixSkylandsSurface(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        if (registryString.equals(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getId()))
            jsonObject.addProperty(NbtTags.SURFACE_BUILDER, ModernBetaBuiltInTypes.Surface.SKYLANDS.getId());
    }
    
    public static void fixSingleBiome(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.DEPR_FIXED_BIOME, DEFAULTS.singleBiome.toString());
        
        jsonObject.addProperty(NbtTags.SINGLE_BIOME, registryString);
    }
    
    public static void fixIndevHouse(JsonObject jsonObject) {
        boolean useIndevHouse = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_INDEV_HOUSE, true);
        
        jsonObject.addProperty(NbtTags.LEVEL_HOUSE, useIndevHouse ? IndevHouse.OAK.id : IndevHouse.NONE.id);
    }
    
    public static void fixResourceLocationChunk(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            jsonObject.addProperty(NbtTags.CHUNK_SOURCE, ModernBeta.createRegistryKey(registryString).toString());
        }
    }
    
    public static void fixResourceLocationBiome(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.BIOME_SOURCE, ModernBetaBuiltInTypes.Biome.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            jsonObject.addProperty(NbtTags.BIOME_SOURCE, ModernBeta.createRegistryKey(registryString).toString());
        }
    }
    
    public static void fixResourceLocationSurface(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.SURFACE_BUILDER, ModernBetaBuiltInTypes.Surface.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            jsonObject.addProperty(NbtTags.SURFACE_BUILDER, ModernBeta.createRegistryKey(registryString).toString());
        }
    }
    
    public static void fixResourceLocationCarver(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CAVE_CARVER, ModernBetaBuiltInTypes.Carver.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            jsonObject.addProperty(NbtTags.CAVE_CARVER, ModernBeta.createRegistryKey(registryString).toString());
        }
    }
    
    public static void fixScaleNoiseScaleX(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, DEFAULTS.chunkSource.toString());
        boolean isAlpha = registryString.equals(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryString());
        boolean isInfdev611 = registryString.equals(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryString());
        
        if (isAlpha || isInfdev611) {
            jsonObject.addProperty(NbtTags.SCALE_NOISE_SCALE_X, 1.0f);
        }
    }
    
    public static void fixScaleNoiseScaleZ(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, DEFAULTS.chunkSource.toString());
        boolean isAlpha = registryString.equals(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryString());
        boolean isInfdev611 = registryString.equals(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryString());
        
        if (isAlpha || isInfdev611) {
            jsonObject.addProperty(NbtTags.SCALE_NOISE_SCALE_Z, 1.0f);
        }
    }
    
    public static void fixLayerSize(JsonObject jsonObject) {
        int biomeSize = JsonUtils.getInt(jsonObject, NbtTags.BIOME_SIZE, DEFAULTS.layerSize);

        jsonObject.addProperty(NbtTags.LAYER_SIZE, biomeSize);
    }
    
    public static void fixCaveCarverNone(JsonObject jsonObject) {
        boolean useCaves = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_CAVES, true);
        
        if (!useCaves) {
            jsonObject.addProperty(NbtTags.CAVE_CARVER, ModernBetaBuiltInTypes.Carver.NONE.getRegistryString());
        }
    }
    
    public static void fixWorldSpawner(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, DEFAULTS.chunkSource.toString());
        String defaultSpawner = ModernBetaBuiltInTypes.WorldSpawner.DEFAULT.getRegistryString();
        
        jsonObject.addProperty(NbtTags.WORLD_SPAWNER, WORLD_SPAWNERS.getOrDefault(registryString, defaultSpawner));
    }
    
    public static void fixDefaultFluid(JsonObject jsonObject) {
        boolean useLavaOceans = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_LAVA_OCEANS, false);
        
        if (useLavaOceans) {
            jsonObject.addProperty(NbtTags.DEFAULT_FLUID, Blocks.LAVA.getRegistryName().toString());
        }
    }
    
    public static void fixSandDisks(JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(jsonObject);
        
        if (hasVanillaBiome) {
            jsonObject.addProperty(NbtTags.USE_SAND_DISKS, true);
        }
    }
    
    public static void fixGravelDisks(JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(jsonObject);
        
        if (hasVanillaBiome) {
            jsonObject.addProperty(NbtTags.USE_GRAVEL_DISKS, true);
        }
    }
    
    public static void fixClayDisks(JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(jsonObject);
        
        if (hasVanillaBiome) {
            jsonObject.addProperty(NbtTags.USE_CLAY_DISKS, true);
        }
    }
    
    public static void fixDoublePlants(JsonObject jsonObject) {
        boolean useNewFlowers = JsonUtils.getBoolean(jsonObject, NbtTags.USE_NEW_FLOWERS, DEFAULTS.useNewFlowers);
        
        jsonObject.addProperty(NbtTags.USE_DOUBLE_PLANTS, useNewFlowers);
    }
    
    public static void fixSnowyBiomeChance(JsonObject jsonObject) {
        jsonObject.addProperty(NbtTags.SNOWY_BIOME_CHANCE, 6);
    }
    
    public static void fixLayerVersion1600(JsonObject jsonObject) {
        jsonObject.addProperty(NbtTags.LAYER_VERSION, GenLayerVersion.LAYER_VERSION_V1_6_0_0);
    }
    
    public static void fixBoPCompat(JsonObject jsonObject) {
        if (ModCompat.isModLoaded(CompatBiomesOPlenty.MOD_ID) && DEFAULTS.containsProperty(CompatBiomesOPlenty.KEY_USE_COMPAT)) {
            boolean useModdedBiomes = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_MODDED_BIOMES, true);
            
            jsonObject.addProperty(CompatBiomesOPlenty.KEY_USE_COMPAT.toString(), useModdedBiomes);
        }
    }
    
    public static void fixReplaceRiverBiomes(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, DEFAULTS.chunkSource.toString());
        
        if (!registryString.equals(ModernBetaBuiltInTypes.Chunk.RELEASE.getRegistryString())) {
            jsonObject.addProperty(NbtTags.REPLACE_RIVER_BIOMES, false);
        }
    }
    
    public static void fixReleaseWorldSpawner(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.WORLD_SPAWNER, DEFAULTS.worldSpawner.toString());
        
        if (registryString.equals(ModernBeta.createRegistryKey("none").toString())) {
            jsonObject.addProperty(NbtTags.WORLD_SPAWNER, ModernBetaBuiltInTypes.WorldSpawner.RELEASE.getRegistryString());
        }
    }
    
    private static boolean isResourceFormat(String resourceString) {
        return resourceString.split(":").length == 2;
    }
    
    private static boolean hasVanillaBiome(JsonObject jsonObject) {
        String biomeSourceStr = JsonUtils.getString(jsonObject, NbtTags.BIOME_SOURCE, ModernBetaBuiltInTypes.Biome.BETA.getId());
        ResourceLocation biomeSource = new ResourceLocation(biomeSourceStr);
        
        boolean hasVanillaBiome = true;
        
        if (biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.getRegistryKey()) ||
            biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.getRegistryKey())
        ) {
            ModernBetaGeneratorSettings.Factory factory = ModernBetaGeneratorSettings.Factory.jsonToFactory(jsonObject.toString());
            hasVanillaBiome = new ClimateMap(factory.build()).containsNonModernBetaBiomes();
            
        } else if (biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.getRegistryKey())) {
            String singleBiomeStr = JsonUtils.getString(jsonObject, NbtTags.SINGLE_BIOME, DEFAULTS.singleBiome.toString());
            ResourceLocation singleBiome = new ResourceLocation(singleBiomeStr);
            
            hasVanillaBiome = !(ForgeRegistryUtil.get(singleBiome, ForgeRegistries.BIOMES) instanceof ModernBetaBiome);
            
        }
        
        return hasVanillaBiome;
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
    
    @Deprecated
    public static class DataFix {
        private final String tag;
        private final Consumer<JsonObject> dataFixConsumer;
        
        public DataFix(String tag, BiConsumer<ModernBetaGeneratorSettings.Factory, JsonObject> dataFixConsumer) {
            this.tag = tag;
            this.dataFixConsumer = (jsonObject) -> dataFixConsumer.accept(new ModernBetaGeneratorSettings.Factory(), jsonObject);
        }
        
        public String getTag() {
            return this.tag;
        }
        
        public Consumer<JsonObject> getDataFixConsumer() {
            return this.dataFixConsumer;
        }
    }
}
