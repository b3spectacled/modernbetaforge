package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.compat.biomesoplenty.CompatBiomesOPlenty;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.climate.BetaClimateMap;
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
    
    public static JsonElement fixBiome(JsonObject jsonObject, String mapTag, String deprTag, String defaultTag) {
        Map<String, String> biomeMap = deserializeBiomeMap(jsonObject, mapTag);
        String biome = biomeMap.getOrDefault(deprTag, defaultTag);
        
        return new JsonPrimitive(biome);
    }
    
    public static JsonElement fixDesertBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.DESERT_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.desertBiomeBase.toString());
    }
    
    public static JsonElement fixDesertBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.DESERT_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.desertBiomeOcean.toString());
    }
    
    public static JsonElement fixDesertBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.DESERT_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.desertBiomeBeach.toString());
    }
    
    public static JsonElement fixForestBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.FOREST_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.forestBiomeBase.toString());
    }
    
    public static JsonElement fixForestBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.FOREST_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.forestBiomeOcean.toString());
    }
    
    public static JsonElement fixForestBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.FOREST_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.forestBiomeBeach.toString());
    }
    
    public static JsonElement fixIceDesertBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.ICE_DESERT_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.iceDesertBiomeBase.toString());
    }
    
    public static JsonElement fixIceDesertBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.ICE_DESERT_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.iceDesertBiomeOcean.toString());
    }
    
    public static JsonElement fixIceDesertBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.ICE_DESERT_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.iceDesertBiomeBeach.toString());
    }
    
    public static JsonElement fixPlainsBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.PLAINS_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.plainsBiomeBase.toString());
    }
    
    public static JsonElement fixPlainsBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.PLAINS_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.plainsBiomeOcean.toString());
    }
    
    public static JsonElement fixPlainsBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.PLAINS_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.plainsBiomeBeach.toString());
    }
    
    public static JsonElement fixRainforestBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.RAINFOREST_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.rainforestBiomeBase.toString());
    }
    
    public static JsonElement fixRainforestBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.RAINFOREST_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.rainforestBiomeOcean.toString());
    }
    
    public static JsonElement fixRainforestBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.RAINFOREST_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.rainforestBiomeBeach.toString());
    }
    
    public static JsonElement fixSavannaBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SAVANNA_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.savannaBiomeBase.toString());
    }
    
    public static JsonElement fixSavannaBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SAVANNA_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.savannaBiomeOcean.toString());
    }
    
    public static JsonElement fixSavannaBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SAVANNA_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.savannaBiomeBeach.toString());
    }
    
    public static JsonElement fixShrublandBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SHRUBLAND_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.shrublandBiomeBase.toString());
    }
    
    public static JsonElement fixShrublandBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SHRUBLAND_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.shrublandBiomeOcean.toString());
    }
    
    public static JsonElement fixShrublandBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SHRUBLAND_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.shrublandBiomeBeach.toString());
    }
    
    public static JsonElement fixSeasonalForestBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SEASONAL_FOREST_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.seasonalForestBiomeBase.toString());
    }
    
    public static JsonElement fixSeasonalForestBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SEASONAL_FOREST_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.seasonalForestBiomeOcean.toString());
    }
    
    public static JsonElement fixSeasonalForestBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SEASONAL_FOREST_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.seasonalForestBiomeBeach.toString());
    }
    
    public static JsonElement fixSwamplandBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SWAMPLAND_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.swamplandBiomeBase.toString());
    }
    
    public static JsonElement fixSwamplandBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SWAMPLAND_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.swamplandBiomeOcean.toString());
    }
    
    public static JsonElement fixSwamplandBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.SWAMPLAND_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.swamplandBiomeBeach.toString());
    }
    
    public static JsonElement fixTaigaBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.TAIGA_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.taigaBiomeBase.toString());
    }
    
    public static JsonElement fixTaigaBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.TAIGA_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.taigaBiomeOcean.toString());
    }
    
    public static JsonElement fixTaigaBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.TAIGA_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.taigaBiomeBeach.toString());
    }
    
    public static JsonElement fixTundraBiomeBase(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.TUNDRA_BIOMES, NbtTags.DEPR_BASE_BIOME, DEFAULTS.tundraBiomeBase.toString());
    }
    
    public static JsonElement fixTundraBiomeOcean(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.TUNDRA_BIOMES, NbtTags.DEPR_OCEAN_BIOME, DEFAULTS.tundraBiomeOcean.toString());
    }
    
    public static JsonElement fixTundraBiomeBeach(JsonObject jsonObject) {
        return fixBiome(jsonObject, NbtTags.TUNDRA_BIOMES, NbtTags.DEPR_BEACH_BIOME, DEFAULTS.tundraBiomeBeach.toString());
    }
    
    public static JsonElement fixSandstone(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());

        return new JsonPrimitive(SHOULD_GEN_SANDSTONE.getOrDefault(registryString, DEFAULTS.useSandstone));
    }
    
    public static JsonElement fixWolves(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());

        return new JsonPrimitive(SHOULD_SPAWN_WOLVES.getOrDefault(registryString, DEFAULTS.spawnWolves));
    }
    
    public static JsonElement fixSurfaces(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        return new JsonPrimitive(SURFACE_BUILDERS.getOrDefault(registryString, DEFAULTS.surfaceBuilder.getPath()));
    }
    
    public static JsonElement fixBiomeDepthScale(JsonObject jsonObject) {
        return new JsonPrimitive(false);
    }
    
    public static JsonElement fixSkylandsSurface(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        if (registryString.equals(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getId()))
            return new JsonPrimitive(ModernBetaBuiltInTypes.Surface.SKYLANDS.getId());
        
        return null;
    }
    
    public static JsonElement fixSingleBiome(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.DEPR_FIXED_BIOME, DEFAULTS.singleBiome.toString());

        return new JsonPrimitive(registryString);
    }
    
    public static JsonElement fixIndevHouse(JsonObject jsonObject) {
        boolean useIndevHouse = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_INDEV_HOUSE, true);
        
        return new JsonPrimitive(useIndevHouse ? IndevHouse.OAK.id : IndevHouse.NONE.id);
    }
    
    public static JsonElement fixResourceLocationChunk(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, ModernBetaBuiltInTypes.Chunk.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            return new JsonPrimitive(ModernBeta.createRegistryKey(registryString).toString());
        }
        
        return null;
    }
    
    public static JsonElement fixResourceLocationBiome(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.BIOME_SOURCE, ModernBetaBuiltInTypes.Biome.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            return new JsonPrimitive(ModernBeta.createRegistryKey(registryString).toString());
        }
        
        return null;
    }
    
    public static JsonElement fixResourceLocationSurface(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.SURFACE_BUILDER, ModernBetaBuiltInTypes.Surface.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            return new JsonPrimitive( ModernBeta.createRegistryKey(registryString).toString());
        }
        
        return null;
    }
    
    public static JsonElement fixResourceLocationCarver(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CAVE_CARVER, ModernBetaBuiltInTypes.Carver.BETA.getId());
        
        if (!isResourceFormat(registryString)) {
            return new JsonPrimitive(ModernBeta.createRegistryKey(registryString).toString());
        }
        
        return null;
    }
    
    public static JsonElement fixScaleNoiseScaleX(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, DEFAULTS.chunkSource.toString());
        boolean isAlpha = registryString.equals(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryString());
        boolean isInfdev611 = registryString.equals(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryString());
        
        if (isAlpha || isInfdev611) {
            return new JsonPrimitive(1.0f);
        }
        
        return null;
    }
    
    public static JsonElement fixScaleNoiseScaleZ(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, DEFAULTS.chunkSource.toString());
        boolean isAlpha = registryString.equals(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryString());
        boolean isInfdev611 = registryString.equals(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryString());
        
        if (isAlpha || isInfdev611) {
            return new JsonPrimitive(1.0f);
        }
        
        return null;
    }
    
    public static JsonElement fixLayerSize(JsonObject jsonObject) {
        int biomeSize = JsonUtils.getInt(jsonObject, NbtTags.BIOME_SIZE, DEFAULTS.layerSize);

        return new JsonPrimitive(biomeSize);
    }
    
    public static JsonElement fixCaveCarverNone(JsonObject jsonObject) {
        boolean useCaves = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_CAVES, true);
        
        if (!useCaves) {
            return new JsonPrimitive(ModernBetaBuiltInTypes.Carver.NONE.getRegistryString());
        }
        
        return null;
    }
    
    public static JsonElement fixWorldSpawner(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, DEFAULTS.chunkSource.toString());
        String defaultSpawner = ModernBetaBuiltInTypes.WorldSpawner.DEFAULT.getRegistryString();
        
        return new JsonPrimitive(WORLD_SPAWNERS.getOrDefault(registryString, defaultSpawner));
    }
    
    public static JsonElement fixDefaultFluid(JsonObject jsonObject) {
        boolean useLavaOceans = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_LAVA_OCEANS, false);
        
        if (useLavaOceans) {
            return new JsonPrimitive(Blocks.LAVA.getRegistryName().toString());
        }
        
        return null;
    }
    
    public static JsonElement fixSandDisks(JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(jsonObject);
        
        if (hasVanillaBiome) {
            return new JsonPrimitive(true);
        }
        
        return null;
    }
    
    public static JsonElement fixGravelDisks(JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(jsonObject);
        
        if (hasVanillaBiome) {
            return new JsonPrimitive(true);
        }
        
        return null;
    }
    
    public static JsonElement fixClayDisks(JsonObject jsonObject) {
        boolean hasVanillaBiome = hasVanillaBiome(jsonObject);
        
        if (hasVanillaBiome) {
            return new JsonPrimitive(true);
        }
        
        return null;
    }
    
    public static JsonElement fixDoublePlants(JsonObject jsonObject) {
        boolean useNewFlowers = JsonUtils.getBoolean(jsonObject, NbtTags.USE_NEW_FLOWERS, DEFAULTS.useNewFlowers);
        
        return new JsonPrimitive(useNewFlowers);
    }
    
    public static JsonElement fixSnowyBiomeChance(JsonObject jsonObject) {
        return new JsonPrimitive(6);
    }
    
    public static JsonElement fixLayerVersion1600(JsonObject jsonObject) {
        return new JsonPrimitive(GenLayerVersion.LAYER_VERSION_V1_6_0_0);
    }
    
    public static JsonElement fixBoPCompat(JsonObject jsonObject) {
        if (ModCompat.isModLoaded(CompatBiomesOPlenty.MOD_ID) && DEFAULTS.containsProperty(CompatBiomesOPlenty.KEY_USE_COMPAT)) {
            boolean useModdedBiomes = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_MODDED_BIOMES, true);
            
            return new JsonPrimitive(useModdedBiomes);
        }
        
        return null;
    }
    
    public static JsonElement fixReplaceRiverBiomes(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, DEFAULTS.chunkSource.toString());
        
        if (!registryString.equals(ModernBetaBuiltInTypes.Chunk.RELEASE.getRegistryString())) {
            return new JsonPrimitive(false);
        }
        
        return null;
    }
    
    public static JsonElement fixReleaseWorldSpawner(JsonObject jsonObject) {
        String registryString = JsonUtils.getString(jsonObject, NbtTags.WORLD_SPAWNER, DEFAULTS.worldSpawner.toString());
        
        if (registryString.equals(ModernBeta.createRegistryKey("none").toString())) {
            return new JsonPrimitive(ModernBetaBuiltInTypes.WorldSpawner.RELEASE.getRegistryString());
        }
        
        return null;
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
            hasVanillaBiome = new BetaClimateMap(factory.build()).containsNonModernBetaBiomes();
            
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
