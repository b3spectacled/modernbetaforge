package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.climate.ClimateMap;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.init.Blocks;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DataFixers {
    private static final Gson GSON = new Gson();
    
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
    
    public static void fixDesertBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
         Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.DESERT_BIOMES);
         
         factory.desertBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.desertBiomeBase);
         factory.desertBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.desertBiomeOcean);
         factory.desertBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.desertBiomeBeach);
    }
    
    public static void fixForestBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.FOREST_BIOMES);
        
        factory.forestBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.forestBiomeBase);
        factory.forestBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.forestBiomeOcean);
        factory.forestBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.forestBiomeBeach);
    }
    
    public static void fixIceDesertBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.ICE_DESERT_BIOMES);
        
        factory.iceDesertBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.iceDesertBiomeBase);
        factory.iceDesertBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.iceDesertBiomeOcean);
        factory.iceDesertBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.iceDesertBiomeBeach);
    }
    
    public static void fixPlainsBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.PLAINS_BIOMES);
        
        factory.plainsBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.plainsBiomeBase);
        factory.plainsBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.plainsBiomeOcean);
        factory.plainsBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.plainsBiomeBeach);
    }
    
    public static void fixRainforestBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.RAINFOREST_BIOMES);
        
        factory.rainforestBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.rainforestBiomeBase);
        factory.rainforestBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.rainforestBiomeOcean);
        factory.rainforestBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.rainforestBiomeBeach);
    }
    
    public static void fixSavannaBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SAVANNA_BIOMES);
        
        factory.savannaBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.savannaBiomeBase);
        factory.savannaBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.savannaBiomeOcean);
        factory.savannaBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.savannaBiomeBeach);
    }
    
    public static void fixShrublandBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SHRUBLAND_BIOMES);
        
        factory.shrublandBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.shrublandBiomeBase);
        factory.shrublandBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.shrublandBiomeOcean);
        factory.shrublandBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.shrublandBiomeBeach);
    }
    
    public static void fixSeasonalForestBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SEASONAL_FOREST_BIOMES);
        
        factory.seasonalForestBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.seasonalForestBiomeBase);
        factory.seasonalForestBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.seasonalForestBiomeOcean);
        factory.seasonalForestBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.seasonalForestBiomeBeach);
    }
    
    public static void fixSwamplandBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.SWAMPLAND_BIOMES);
        
        factory.swamplandBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.swamplandBiomeBase);
        factory.swamplandBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.swamplandBiomeOcean);
        factory.swamplandBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.swamplandBiomeBeach);
    }
    
    public static void fixTaigaBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.TAIGA_BIOMES);
        
        factory.taigaBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.taigaBiomeBase);
        factory.taigaBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.taigaBiomeOcean);
        factory.taigaBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.taigaBiomeBeach);
    }

    public static void fixTundraBiomes(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, NbtTags.TUNDRA_BIOMES);
        
        factory.tundraBiomeBase = biomeMap.getOrDefault(NbtTags.DEPR_LAND_BIOME, factory.tundraBiomeBase);
        factory.tundraBiomeOcean = biomeMap.getOrDefault(NbtTags.DEPR_OCEAN_BIOME, factory.tundraBiomeOcean);
        factory.tundraBiomeBeach = biomeMap.getOrDefault(NbtTags.DEPR_BEACH_BIOME, factory.tundraBiomeBeach);
    }
    
    public static void fixSandstone(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        factory.useSandstone = SHOULD_GEN_SANDSTONE.getOrDefault(factory.chunkSource, factory.useSandstone);
    }
    
    public static void fixWolves(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        factory.spawnWolves = SHOULD_SPAWN_WOLVES.getOrDefault(factory.chunkSource, factory.spawnWolves);
    }
    
    public static void fixSurfaces(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        factory.surfaceBuilder = SURFACE_BUILDERS.getOrDefault(factory.chunkSource, factory.surfaceBuilder);
    }
    
    public static void fixBiomeDepthScale(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        factory.useBiomeDepthScale = false;
    }
    
    public static void fixSkylandsSurface(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        if (factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getId()))
            factory.surfaceBuilder = ModernBetaBuiltInTypes.Surface.SKYLANDS.getId();
    }
    
    public static void fixSingleBiome(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        factory.singleBiome = JsonUtils.getString(jsonObject, NbtTags.DEPR_FIXED_BIOME, factory.singleBiome);
    }
    
    public static void fixIndevHouse(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        boolean useIndevHouse = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_INDEV_HOUSE, true);
        
        factory.levelHouse = useIndevHouse ? IndevHouse.OAK.id : IndevHouse.NONE.id;
    }
    
    public static void fixResourceLocationChunk(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            factory.chunkSource = ModernBeta.createRegistryKey(registryString).toString();
        }
    }
    
    public static void fixResourceLocationBiome(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.BIOME_SOURCE, ModernBetaBuiltInTypes.Biome.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            factory.biomeSource = ModernBeta.createRegistryKey(registryString).toString();
        }
    }
    
    public static void fixResourceLocationSurface(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.SURFACE_BUILDER, ModernBetaBuiltInTypes.Surface.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            factory.surfaceBuilder = ModernBeta.createRegistryKey(registryString).toString();
        }
    }
    
    public static void fixResourceLocationCarver(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CAVE_CARVER, ModernBetaBuiltInTypes.Carver.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            factory.caveCarver = ModernBeta.createRegistryKey(registryString).toString();
        }
    }
    
    public static void fixScaleNoiseScaleX(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, factory.chunkSource);
        boolean isAlpha = registryString.equals(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryString());
        boolean isInfdev611 = registryString.equals(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryString());
        
        if (isAlpha || isInfdev611) {
            factory.scaleNoiseScaleX = 1.0f;
        }
    }
    
    public static void fixScaleNoiseScaleZ(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, factory.chunkSource);
        boolean isAlpha = registryString.equals(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryString());
        boolean isInfdev611 = registryString.equals(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryString());
        
        if (isAlpha || isInfdev611) {
            factory.scaleNoiseScaleZ = 1.0f;
        }
    }
    
    public static void fixLayerSize(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        int biomeSize = JsonUtils.getInt(jsonObject, NbtTags.BIOME_SIZE, factory.layerSize);
        
        factory.layerSize = biomeSize;
    }
    
    public static void fixCaveCarverNone(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        boolean useCaves = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_CAVES, true);
        
        if (!useCaves) {
            factory.caveCarver = ModernBetaBuiltInTypes.Carver.NONE.getRegistryString();
        }
    }
    
    public static void fixWorldSpawner(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, factory.chunkSource);
        
        factory.worldSpawner = WORLD_SPAWNERS.getOrDefault(registryString, ModernBetaBuiltInTypes.WorldSpawner.DEFAULT.getRegistryString());
    }
    
    public static void fixDefaultFluid(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        boolean useLavaOceans = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_LAVA_OCEANS, false);
        
        if (useLavaOceans) {
            factory.defaultFluid = Blocks.LAVA.getRegistryName().toString();
        }
    }
    
    public static void fixSandDisks(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(factory, JsonUtils.getString(jsonObject, NbtTags.BIOME_SOURCE, factory.biomeSource));
        
        if (hasVanillaBiome) {
            factory.useSandDisks = true;
        }
    }
    
    public static void fixGravelDisks(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(factory, JsonUtils.getString(jsonObject, NbtTags.BIOME_SOURCE, factory.biomeSource));
        
        if (hasVanillaBiome) {
            factory.useGravelDisks = true;
        }
    }
    
    public static void fixClayDisks(ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(factory, JsonUtils.getString(jsonObject, NbtTags.BIOME_SOURCE, factory.biomeSource));
        
        if (hasVanillaBiome) {
            factory.useClayDisks = true;
        }
    }
    
    private static boolean isResourceFormat(String resourceString) {
        return resourceString.split(":").length == 2;
    }
    
    private static boolean hasVanillaBiome(ModernBetaGeneratorSettings.Factory factory, String biomeSource) {
        ModernBetaGeneratorSettings settings = factory.build();
        boolean hasVanillaBiome = true;
        
        if (biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.getRegistryString()) || biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.getRegistryString())) {
            hasVanillaBiome = new ClimateMap(settings).containsNonModernBetaBiomes();
            
        } else if (biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.getRegistryString())) {
            hasVanillaBiome = !(ForgeRegistryUtil.get(settings.singleBiome, ForgeRegistries.BIOMES) instanceof ModernBetaBiome);
            
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
    
    public static class DataFix {
        private final String tag;
        private final BiConsumer<ModernBetaGeneratorSettings.Factory, JsonObject> dataFixConsumer;
        
        public DataFix(String tag, BiConsumer<ModernBetaGeneratorSettings.Factory, JsonObject> dataFixConsumer) {
            this.tag = tag;
            this.dataFixConsumer = dataFixConsumer;
        }
        
        public String getTag() {
            return this.tag;
        }
        
        public BiConsumer<ModernBetaGeneratorSettings.Factory, JsonObject> getDataFixConsumer() {
            return this.dataFixConsumer;
        }
    }
}
