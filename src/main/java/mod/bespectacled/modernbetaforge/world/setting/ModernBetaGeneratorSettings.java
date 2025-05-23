package mod.bespectacled.modernbetaforge.world.setting;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.EntityEntryProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.property.Property;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.property.StringProperty;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.property.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeTags;
import mod.bespectacled.modernbetaforge.world.biome.layer.GenLayerType;
import mod.bespectacled.modernbetaforge.world.biome.layer.GenLayerVersion;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevTheme;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevType;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModernBetaGeneratorSettings {
    public static final int[] LEVEL_WIDTHS = { 64, 128, 256, 512, 768, 1024, 1536, 2048, 2560 };
    public static final int[] LEVEL_HEIGHTS = { 64, 96, 128, 160, 192, 224, 256 };
    
    public static final int MIN_HEIGHT = 1;
    public static final int MAX_HEIGHT = 255;
    
    public static final int MIN_SEA_LEVEL = 0;
    public static final int MAX_SEA_LEVEL = MAX_HEIGHT;
    public static final int MIN_DUNGEON_CHANCE = 1;
    public static final int MAX_DUNGEON_CHANCE = 100;
    public static final int MIN_WATER_LAKE_CHANCE = 1;
    public static final int MAX_WATER_LAKE_CHANCE = 100;
    public static final int MIN_LAVA_LAKE_CHANCE = 10;
    public static final int MAX_LAVA_LAKE_CHANCE = 100;
    
    public static final float MIN_LEVEL_CAVE_WIDTH = 1.0f;
    public static final float MAX_LEVEL_CAVE_WIDTH = 5.0f;
    
    public static final int MIN_SNOWY_BIOME_CHANCE = 1;
    public static final int MAX_SNOWY_BIOME_CHANCE = 12;
    
    public static final int MIN_ORE_SIZE = 1;
    public static final int MAX_ORE_SIZE = 50;
    public static final int MIN_ORE_COUNT = 0;
    public static final int MAX_ORE_COUNT = 40;
    public static final int MIN_ORE_HEIGHT = 0;
    public static final int MAX_ORE_HEIGHT = MAX_HEIGHT;
    public static final int MIN_ORE_CENTER = 0;
    public static final int MAX_ORE_CENTER = MAX_HEIGHT;
    public static final int MIN_ORE_SPREAD = 1;
    public static final int MAX_ORE_SPREAD = MAX_HEIGHT;
    
    public static final float MIN_CAVE_WIDTH = 1.0f;
    public static final float MAX_CAVE_WIDTH = 7.5f;
    public static final int MIN_CAVE_HEIGHT = 9;
    public static final int MAX_CAVE_HEIGHT = MAX_HEIGHT;
    public static final int MIN_CAVE_COUNT = 1;
    public static final int MAX_CAVE_COUNT = 100;
    public static final int MIN_CAVE_CHANCE = 1;
    public static final int MAX_CAVE_CHANCE = 100;
    
    public static final int MIN_BIOME_SIZE = 1;
    public static final int MAX_BIOME_SIZE = 8;
    public static final int MIN_RIVER_SIZE = 1;
    public static final int MAX_RIVER_SIZE = 5;
    
    public static final float MIN_MAIN_NOISE = 1.0f;
    public static final float MAX_MAIN_NOISE = 5000.0f;
    public static final float MIN_SCALE_NOISE = 1.0f;
    public static final float MAX_SCALE_NOISE = 20.0f;
    public static final float MIN_DEPTH_NOISE = 1.0f;
    public static final float MAX_DEPTH_NOISE = 2000.0f;
    public static final float MIN_BASE_SIZE = 1.0f;
    public static final float MAX_BASE_SIZE = 25.0f;
    public static final float MIN_COORD_SCALE = 1.0f;
    public static final float MAX_COORD_SCALE = 6000.0f;
    public static final float MIN_HEIGHT_SCALE = 1.0f;
    public static final float MAX_HEIGHT_SCALE = 6000.0f;
    public static final float MIN_STRETCH_Y = 0.01f;
    public static final float MAX_STRETCH_Y = 50.0f;
    public static final float MIN_LIMIT_SCALE = 1.0f;
    public static final float MAX_LIMIT_SCALE = 5000.0f;
    public static final float MIN_BIOME_SCALE = 0.1f;
    public static final float MAX_BIOME_SCALE = 8.0f;
    public static final float MIN_BIOME_WEIGHT = 1.0f;
    public static final float MAX_BIOME_WEIGHT = 20.0f;
    public static final float MIN_BIOME_OFFSET = 0.0f;
    public static final float MAX_BIOME_OFFSET = 20.0f;
    public static final float MIN_END_OFFSET = 0.0f;
    public static final float MAX_END_OFFSET = 2000.0f;
    public static final float MIN_END_WEIGHT = 1.0f;
    public static final float MAX_END_WEIGHT = 20.0f;
    public static final int MIN_END_DIST = 0;
    public static final int MAX_END_DIST = 256;
    
    public final ResourceLocation chunkSource;
    public final ResourceLocation biomeSource;
    public final ResourceLocation surfaceBuilder;
    public final ResourceLocation caveCarver;
    public final ResourceLocation worldSpawner;
    
    public final ResourceLocation singleBiome;
    public final ResourceLocation defaultBlock;
    public final ResourceLocation defaultFluid;
    
    public final boolean replaceOceanBiomes;
    public final boolean replaceBeachBiomes;
    public final boolean replaceRiverBiomes;
    
    public final float coordinateScale;
    public final float heightScale;
    public final float upperLimitScale;
    public final float lowerLimitScale;
    public final float scaleNoiseScaleX;
    public final float scaleNoiseScaleZ;
    public final float depthNoiseScaleX;
    public final float depthNoiseScaleZ;
    public final float mainNoiseScaleX;
    public final float mainNoiseScaleY;
    public final float mainNoiseScaleZ;
    public final float baseSize;
    public final float stretchY;
    public final int seaLevel;
    public final int height;
    
    public final float tempNoiseScale;
    public final float rainNoiseScale;
    public final float detailNoiseScale;
    
    public final float biomeDepthWeight;
    public final float biomeDepthOffset;
    public final float biomeScaleWeight;
    public final float biomeScaleOffset;
    public final boolean useBiomeDepthScale;
    public final int biomeSize;
    public final int riverSize;
    public final String layerType;
    public final int layerSize;
    public final int layerVersion;
    public final int snowyBiomeChance;
    
    public final float endIslandOffset;
    public final float endIslandWeight;
    public final float endOuterIslandOffset;
    public final int endOuterIslandDistance;
    public final boolean useEndOuterIslands;
    
    public final float caveWidth;
    public final int caveHeight;
    public final int caveCount;
    public final int caveChance;
    public final boolean useDungeons;
    public final int dungeonChance;
    
    public final boolean useStrongholds;
    public final boolean useVillages;
    public final boolean useVillageVariants;
    public final boolean useMineShafts;
    public final boolean useTemples;
    public final boolean useMonuments;
    public final boolean useMansions;
    public final boolean useRavines;
    public final boolean useUnderwaterCaves;
    
    public final boolean useWaterLakes;
    public final int waterLakeChance;
    public final boolean useLavaLakes;
    public final int lavaLakeChance;
    
    public final boolean useSandstone;
    
    public final boolean useOldNether;
    public final boolean useNetherCaves;
    public final boolean useFortresses;
    public final boolean useLavaPockets;
    
    public final boolean useInfdevWalls;
    public final boolean useInfdevPyramids;
    
    public final String levelTheme;
    public final String levelType;
    public final int levelWidth;
    public final int levelLength;
    public final int levelHeight;
    public final String levelHouse;
    public final boolean useIndevCaves;
    public final float levelCaveWidth;
    
    public final int claySize;
    public final int clayCount;
    public final int clayMinHeight;
    public final int clayMaxHeight;
    
    public final int dirtSize;
    public final int dirtCount;
    public final int dirtMinHeight;
    public final int dirtMaxHeight;
    
    public final int gravelSize;
    public final int gravelCount;
    public final int gravelMinHeight;
    public final int gravelMaxHeight;
    
    public final int graniteSize;
    public final int graniteCount;
    public final int graniteMinHeight;
    public final int graniteMaxHeight;
    
    public final int dioriteSize;
    public final int dioriteCount;
    public final int dioriteMinHeight;
    public final int dioriteMaxHeight;
    
    public final int andesiteSize;
    public final int andesiteCount;
    public final int andesiteMinHeight;
    public final int andesiteMaxHeight;
    
    public final int coalSize;
    public final int coalCount;
    public final int coalMinHeight;
    public final int coalMaxHeight;
    
    public final int ironSize;
    public final int ironCount;
    public final int ironMinHeight;
    public final int ironMaxHeight;
    
    public final int goldSize;
    public final int goldCount;
    public final int goldMinHeight;
    public final int goldMaxHeight;
    
    public final int redstoneSize;
    public final int redstoneCount;
    public final int redstoneMinHeight;
    public final int redstoneMaxHeight;
    
    public final int diamondSize;
    public final int diamondCount;
    public final int diamondMinHeight;
    public final int diamondMaxHeight;
    
    public final int lapisSize;
    public final int lapisCount;
    public final int lapisCenterHeight;
    public final int lapisSpread;
    
    public final int emeraldSize;
    public final int emeraldCount;
    public final int emeraldMinHeight;
    public final int emeraldMaxHeight;
    
    public final int quartzSize;
    public final int quartzCount;
    
    public final int magmaSize;
    public final int magmaCount;

    public final boolean useTallGrass;
    public final boolean useNewFlowers;
    public final boolean useDoublePlants;
    public final boolean useLilyPads;
    public final boolean useMelons;
    public final boolean useDesertWells;
    public final boolean useFossils;
    public final boolean useSandDisks;
    public final boolean useGravelDisks;
    public final boolean useClayDisks;
    
    public final boolean useBirchTrees;
    public final boolean usePineTrees;
    public final boolean useSwampTrees;
    public final boolean useJungleTrees;
    public final boolean useAcaciaTrees;

    public final boolean spawnNewCreatureMobs;
    public final boolean spawnNewMonsterMobs;
    public final boolean spawnWaterMobs;
    public final boolean spawnAmbientMobs;
    public final boolean spawnWolves;
    
    public final ResourceLocation desertBiomeBase;
    public final ResourceLocation desertBiomeOcean;
    public final ResourceLocation desertBiomeBeach;
    
    public final ResourceLocation forestBiomeBase;
    public final ResourceLocation forestBiomeOcean;
    public final ResourceLocation forestBiomeBeach;
    
    public final ResourceLocation iceDesertBiomeBase;
    public final ResourceLocation iceDesertBiomeOcean;
    public final ResourceLocation iceDesertBiomeBeach;
    
    public final ResourceLocation plainsBiomeBase;
    public final ResourceLocation plainsBiomeOcean;
    public final ResourceLocation plainsBiomeBeach;
    
    public final ResourceLocation rainforestBiomeBase;
    public final ResourceLocation rainforestBiomeOcean;
    public final ResourceLocation rainforestBiomeBeach;
    
    public final ResourceLocation savannaBiomeBase;
    public final ResourceLocation savannaBiomeOcean;
    public final ResourceLocation savannaBiomeBeach;
    
    public final ResourceLocation shrublandBiomeBase;
    public final ResourceLocation shrublandBiomeOcean;
    public final ResourceLocation shrublandBiomeBeach;
    
    public final ResourceLocation seasonalForestBiomeBase;
    public final ResourceLocation seasonalForestBiomeOcean;
    public final ResourceLocation seasonalForestBiomeBeach;
    
    public final ResourceLocation swamplandBiomeBase;
    public final ResourceLocation swamplandBiomeOcean;
    public final ResourceLocation swamplandBiomeBeach;
    
    public final ResourceLocation taigaBiomeBase;
    public final ResourceLocation taigaBiomeOcean;
    public final ResourceLocation taigaBiomeBeach;
    
    public final ResourceLocation tundraBiomeBase;
    public final ResourceLocation tundraBiomeOcean;
    public final ResourceLocation tundraBiomeBeach;
    
    private final Map<ResourceLocation, Property<?>> customProperties;
    
    private ModernBetaGeneratorSettings(Factory factory) {
        this.chunkSource = new ResourceLocation(factory.chunkSource);
        this.biomeSource = new ResourceLocation(factory.biomeSource);
        this.surfaceBuilder = new ResourceLocation(factory.surfaceBuilder);
        this.caveCarver = new ResourceLocation(factory.caveCarver);
        this.worldSpawner = new ResourceLocation(factory.worldSpawner);
        
        this.singleBiome = new ResourceLocation(factory.singleBiome);
        this.defaultBlock = new ResourceLocation(factory.defaultBlock);
        this.defaultFluid = new ResourceLocation(factory.defaultFluid);
        
        this.replaceOceanBiomes = factory.replaceOceanBiomes;
        this.replaceBeachBiomes = factory.replaceBeachBiomes;
        this.replaceRiverBiomes = factory.replaceRiverBiomes;
        
        this.coordinateScale = factory.coordinateScale;
        this.heightScale = factory.heightScale;
        this.upperLimitScale = factory.upperLimitScale;
        this.lowerLimitScale = factory.lowerLimitScale;
        this.scaleNoiseScaleX = factory.scaleNoiseScaleX;
        this.scaleNoiseScaleZ = factory.scaleNoiseScaleZ;
        this.depthNoiseScaleX = factory.depthNoiseScaleX;
        this.depthNoiseScaleZ = factory.depthNoiseScaleZ;
        this.mainNoiseScaleX = factory.mainNoiseScaleX;
        this.mainNoiseScaleY = factory.mainNoiseScaleY;
        this.mainNoiseScaleZ = factory.mainNoiseScaleZ;
        this.baseSize = factory.baseSize;
        this.stretchY = factory.stretchY;
        this.seaLevel = factory.seaLevel;
        this.height = factory.height;
        
        this.tempNoiseScale = factory.tempNoiseScale;
        this.rainNoiseScale = factory.rainNoiseScale;
        this.detailNoiseScale = factory.detailNoiseScale;
        
        this.biomeDepthWeight = factory.biomeDepthWeight;
        this.biomeDepthOffset = factory.biomeDepthOffset;
        this.biomeScaleWeight = factory.biomeScaleWeight;
        this.biomeScaleOffset = factory.biomeScaleOffset;
        this.useBiomeDepthScale = factory.useBiomeDepthScale;
        this.biomeSize = factory.biomeSize;
        this.riverSize = factory.riverSize;
        this.layerType = factory.layerType;
        this.layerSize = factory.layerSize;
        this.layerVersion = factory.layerVersion;
        this.snowyBiomeChance = factory.snowyBiomeChance;
        
        this.endIslandOffset = factory.endIslandOffset;
        this.endIslandWeight = factory.endIslandWeight;
        this.endOuterIslandOffset = factory.endOuterIslandOffset;
        this.endOuterIslandDistance = factory.endOuterIslandDistance;
        this.useEndOuterIslands = factory.useEndOuterIslands;
        
        this.caveWidth = factory.caveWidth;
        this.caveHeight = factory.caveHeight;
        this.caveCount = factory.caveCount;
        this.caveChance = factory.caveChance;
        this.useDungeons = factory.useDungeons;
        this.dungeonChance = factory.dungeonChance;
        
        this.useStrongholds = factory.useStrongholds;
        this.useVillages = factory.useVillages;
        this.useVillageVariants = factory.useVillageVariants;
        this.useMineShafts = factory.useMineShafts;
        this.useTemples = factory.useTemples;
        this.useMonuments = factory.useMonuments;
        this.useMansions = factory.useMansions;
        this.useRavines = factory.useRavines;
        this.useUnderwaterCaves = factory.useUnderwaterCaves;
        
        this.useBirchTrees = factory.useBirchTrees;
        this.usePineTrees = factory.usePineTrees;
        this.useSwampTrees = factory.useSwampTrees;
        this.useJungleTrees = factory.useJungleTrees;
        this.useAcaciaTrees = factory.useAcaciaTrees;
        
        this.useWaterLakes = factory.useWaterLakes;
        this.waterLakeChance = factory.waterLakeChance;
        this.useLavaLakes = factory.useLavaLakes;
        this.lavaLakeChance = factory.lavaLakeChance;
        
        this.useSandstone = factory.useSandstone;
        
        this.useOldNether = factory.useOldNether;
        this.useNetherCaves = factory.useNetherCaves;
        this.useFortresses = factory.useFortresses;
        this.useLavaPockets = factory.useLavaPockets;
        
        this.useInfdevWalls = factory.useInfdevWalls;
        this.useInfdevPyramids = factory.useInfdevPyramids;
        
        this.levelTheme = factory.levelTheme;
        this.levelType = factory.levelType;
        this.levelLength = factory.levelLength;
        this.levelWidth = factory.levelWidth;
        this.levelHeight = factory.levelHeight;
        this.levelHouse = factory.levelHouse;
        this.useIndevCaves = factory.useIndevCaves;
        this.levelCaveWidth = factory.levelCaveWidth;
        
        this.claySize = factory.claySize;
        this.clayCount = factory.clayCount;
        this.clayMinHeight = factory.clayMinHeight;
        this.clayMaxHeight = factory.clayMaxHeight;
        
        this.dirtSize = factory.dirtSize;
        this.dirtCount = factory.dirtCount;
        this.dirtMinHeight = factory.dirtMinHeight;
        this.dirtMaxHeight = factory.dirtMaxHeight;
        
        this.gravelSize = factory.gravelSize;
        this.gravelCount = factory.gravelCount;
        this.gravelMinHeight = factory.gravelMinHeight;
        this.gravelMaxHeight = factory.gravelMaxHeight;
        
        this.graniteSize = factory.graniteSize;
        this.graniteCount = factory.graniteCount;
        this.graniteMinHeight = factory.graniteMinHeight;
        this.graniteMaxHeight = factory.graniteMaxHeight;
        
        this.dioriteSize = factory.dioriteSize;
        this.dioriteCount = factory.dioriteCount;
        this.dioriteMinHeight = factory.dioriteMinHeight;
        this.dioriteMaxHeight = factory.dioriteMaxHeight;
        
        this.andesiteSize = factory.andesiteSize;
        this.andesiteCount = factory.andesiteCount;
        this.andesiteMinHeight = factory.andesiteMinHeight;
        this.andesiteMaxHeight = factory.andesiteMaxHeight;
        
        this.coalSize = factory.coalSize;
        this.coalCount = factory.coalCount;
        this.coalMinHeight = factory.coalMinHeight;
        this.coalMaxHeight = factory.coalMaxHeight;
        
        this.ironSize = factory.ironSize;
        this.ironCount = factory.ironCount;
        this.ironMinHeight = factory.ironMinHeight;
        this.ironMaxHeight = factory.ironMaxHeight;
        
        this.goldSize = factory.goldSize;
        this.goldCount = factory.goldCount;
        this.goldMinHeight = factory.goldMinHeight;
        this.goldMaxHeight = factory.goldMaxHeight;
        
        this.redstoneSize = factory.redstoneSize;
        this.redstoneCount = factory.redstoneCount;
        this.redstoneMinHeight = factory.redstoneMinHeight;
        this.redstoneMaxHeight = factory.redstoneMaxHeight;
        
        this.diamondSize = factory.diamondSize;
        this.diamondCount = factory.diamondCount;
        this.diamondMinHeight = factory.diamondMinHeight;
        this.diamondMaxHeight = factory.diamondMaxHeight;
        
        this.lapisSize = factory.lapisSize;
        this.lapisCount = factory.lapisCount;
        this.lapisCenterHeight = factory.lapisCenterHeight;
        this.lapisSpread = factory.lapisSpread;
        
        this.emeraldSize = factory.emeraldSize;
        this.emeraldCount = factory.emeraldCount;
        this.emeraldMinHeight = factory.emeraldMinHeight;
        this.emeraldMaxHeight = factory.emeraldMaxHeight;
        
        this.quartzSize = factory.quartzSize;
        this.quartzCount = factory.quartzCount;
        
        this.magmaSize = factory.magmaSize;
        this.magmaCount = factory.magmaCount;

        this.useTallGrass = factory.useTallGrass;
        this.useNewFlowers = factory.useNewFlowers;
        this.useDoublePlants = factory.useDoublePlants;
        this.useLilyPads = factory.useLilyPads;
        this.useMelons = factory.useMelons;
        this.useDesertWells = factory.useDesertWells;
        this.useFossils = factory.useFossils;
        this.useSandDisks = factory.useSandDisks;
        this.useGravelDisks = factory.useGravelDisks;
        this.useClayDisks = factory.useClayDisks;
        
        this.spawnNewCreatureMobs = factory.spawnNewCreatureMobs;
        this.spawnNewMonsterMobs = factory.spawnNewMonsterMobs;
        this.spawnWaterMobs = factory.spawnWaterMobs;
        this.spawnAmbientMobs = factory.spawnAmbientMobs;
        this.spawnWolves = factory.spawnWolves;
        
        this.desertBiomeBase = new ResourceLocation(factory.desertBiomeBase);
        this.desertBiomeOcean = new ResourceLocation(factory.desertBiomeOcean);
        this.desertBiomeBeach = new ResourceLocation(factory.desertBiomeBeach);
        
        this.forestBiomeBase = new ResourceLocation(factory.forestBiomeBase);
        this.forestBiomeOcean = new ResourceLocation(factory.forestBiomeOcean);
        this.forestBiomeBeach = new ResourceLocation(factory.forestBiomeBeach);
        
        this.iceDesertBiomeBase = new ResourceLocation(factory.iceDesertBiomeBase);
        this.iceDesertBiomeOcean = new ResourceLocation(factory.iceDesertBiomeOcean);
        this.iceDesertBiomeBeach = new ResourceLocation(factory.iceDesertBiomeBeach);
        
        this.plainsBiomeBase = new ResourceLocation(factory.plainsBiomeBase);
        this.plainsBiomeOcean = new ResourceLocation(factory.plainsBiomeOcean);
        this.plainsBiomeBeach = new ResourceLocation(factory.plainsBiomeBeach);
        
        this.rainforestBiomeBase = new ResourceLocation(factory.rainforestBiomeBase);
        this.rainforestBiomeOcean = new ResourceLocation(factory.rainforestBiomeOcean);
        this.rainforestBiomeBeach = new ResourceLocation(factory.rainforestBiomeBeach);
        
        this.savannaBiomeBase = new ResourceLocation(factory.savannaBiomeBase);
        this.savannaBiomeOcean = new ResourceLocation(factory.savannaBiomeOcean);
        this.savannaBiomeBeach = new ResourceLocation(factory.savannaBiomeBeach);
        
        this.shrublandBiomeBase = new ResourceLocation(factory.shrublandBiomeBase);
        this.shrublandBiomeOcean = new ResourceLocation(factory.shrublandBiomeOcean);
        this.shrublandBiomeBeach = new ResourceLocation(factory.shrublandBiomeBeach);
        
        this.seasonalForestBiomeBase = new ResourceLocation(factory.seasonalForestBiomeBase);
        this.seasonalForestBiomeOcean = new ResourceLocation(factory.seasonalForestBiomeOcean);
        this.seasonalForestBiomeBeach = new ResourceLocation(factory.seasonalForestBiomeBeach);
        
        this.swamplandBiomeBase = new ResourceLocation(factory.swamplandBiomeBase);
        this.swamplandBiomeOcean = new ResourceLocation(factory.swamplandBiomeOcean);
        this.swamplandBiomeBeach = new ResourceLocation(factory.swamplandBiomeBeach);
        
        this.taigaBiomeBase = new ResourceLocation(factory.taigaBiomeBase);
        this.taigaBiomeOcean = new ResourceLocation(factory.taigaBiomeOcean);
        this.taigaBiomeBeach = new ResourceLocation(factory.taigaBiomeBeach);
        
        this.tundraBiomeBase = new ResourceLocation(factory.tundraBiomeBase);
        this.tundraBiomeOcean = new ResourceLocation(factory.tundraBiomeOcean);
        this.tundraBiomeBeach = new ResourceLocation(factory.tundraBiomeBeach);
        
        this.customProperties = ImmutableMap.copyOf(factory.customProperties);
    }
    
    public boolean containsProperty(ResourceLocation registryKey) {
        return this.customProperties.containsKey(registryKey);
    }
    
    public boolean getBooleanProperty(ResourceLocation registryKey) {
        Property<?> property = this.customProperties.get(registryKey);
        
        if (property != null && property instanceof BooleanProperty) {
            return ((BooleanProperty)property).getValue();
        }
        
        throw new IllegalArgumentException(String.format("[Modern Beta] Boolean Property '%s' was not found!", registryKey));
    }
    
    public float getFloatProperty(ResourceLocation registryKey) {
        Property<?> property = this.customProperties.get(registryKey);
        
        if (property != null && property instanceof FloatProperty) {
            return ((FloatProperty)property).getValue();
        }
        
        throw new IllegalArgumentException(String.format("[Modern Beta] Float Property '%s' was not found!", registryKey));
    }
    
    public int getIntProperty(ResourceLocation registryKey) {
        Property<?> property = this.customProperties.get(registryKey);
        
        if (property != null && property instanceof IntProperty) {
            return ((IntProperty)property).getValue();
        }
        
        throw new IllegalArgumentException(String.format("[Modern Beta] Int Property '%s' was not found!", registryKey));
    }
    
    public String getStringProperty(ResourceLocation registryKey) {
        Property<?> property = this.customProperties.get(registryKey);
        
        if (property != null && property instanceof StringProperty) {
            return ((StringProperty)property).getValue();
        }
        
        throw new IllegalArgumentException(String.format("[Modern Beta] String Property '%s' was not found!", registryKey));
    }
    
    public String getListProperty(ResourceLocation registryKey) {
        return this.getStringProperty(registryKey);
    }
    
    public Biome getBiomeProperty(ResourceLocation registryKey) {
        return ForgeRegistryUtil.get(new ResourceLocation(this.getStringProperty(registryKey)), ForgeRegistries.BIOMES);
    }
    
    public Block getBlockProperty(ResourceLocation registryKey) {
        return ForgeRegistryUtil.get(new ResourceLocation(this.getStringProperty(registryKey)), ForgeRegistries.BLOCKS);
    }
    
    public EntityEntry getEntityEntryProperty(ResourceLocation registryKey) {
        return ForgeRegistryUtil.get(new ResourceLocation(this.getStringProperty(registryKey)), ForgeRegistries.ENTITIES);
    }
    
    public static class Factory {
        static final Gson JSON_ADAPTER;
        
        public String chunkSource;
        public String biomeSource;
        public String surfaceBuilder;
        public String caveCarver;
        public String worldSpawner;
        
        public String singleBiome;
        public String defaultBlock;
        public String defaultFluid;

        public boolean replaceOceanBiomes;
        public boolean replaceBeachBiomes;
        public boolean replaceRiverBiomes;
        
        public float coordinateScale;
        public float heightScale;
        public float upperLimitScale;
        public float lowerLimitScale;
        public float scaleNoiseScaleX;
        public float scaleNoiseScaleZ;
        public float depthNoiseScaleX;
        public float depthNoiseScaleZ;
        public float mainNoiseScaleX;
        public float mainNoiseScaleY;
        public float mainNoiseScaleZ;
        public float baseSize;
        public float stretchY;
        public int seaLevel;
        public int height;
        
        public float tempNoiseScale;
        public float rainNoiseScale;
        public float detailNoiseScale;
        
        public float biomeDepthWeight;
        public float biomeDepthOffset;
        public float biomeScaleWeight;
        public float biomeScaleOffset;
        public boolean useBiomeDepthScale;
        public int biomeSize;
        public int riverSize;
        public String layerType;
        public int layerSize;
        public int layerVersion;
        public int snowyBiomeChance;
        
        public float endIslandOffset;
        public float endIslandWeight;
        public float endOuterIslandOffset;
        public int endOuterIslandDistance;
        public boolean useEndOuterIslands;
        
        public float caveWidth;
        public int caveHeight;
        public int caveCount;
        public int caveChance;
        public boolean useDungeons;
        public int dungeonChance;
        
        public boolean useStrongholds;
        public boolean useVillages;
        public boolean useVillageVariants;
        public boolean useMineShafts;
        public boolean useTemples;
        public boolean useMonuments;
        public boolean useMansions;
        public boolean useRavines;
        public boolean useUnderwaterCaves;
        
        public boolean useWaterLakes;
        public int waterLakeChance;
        public boolean useLavaLakes;
        public int lavaLakeChance;
        
        public boolean useSandstone;
        
        public boolean useOldNether;
        public boolean useNetherCaves;
        public boolean useFortresses;
        public boolean useLavaPockets;
        
        public boolean useInfdevWalls;
        public boolean useInfdevPyramids;
        
        public String levelTheme;
        public String levelType;
        public int levelWidth;
        public int levelLength;
        public int levelHeight;
        public String levelHouse;
        public boolean useIndevCaves;
        public float levelCaveWidth;
        
        public int claySize;
        public int clayCount;
        public int clayMinHeight;
        public int clayMaxHeight;
        
        public int dirtSize;
        public int dirtCount;
        public int dirtMinHeight;
        public int dirtMaxHeight;
        
        public int gravelSize;
        public int gravelCount;
        public int gravelMinHeight;
        public int gravelMaxHeight;
        
        public int graniteSize;
        public int graniteCount;
        public int graniteMinHeight;
        public int graniteMaxHeight;
        
        public int dioriteSize;
        public int dioriteCount;
        public int dioriteMinHeight;
        public int dioriteMaxHeight;
        
        public int andesiteSize;
        public int andesiteCount;
        public int andesiteMinHeight;
        public int andesiteMaxHeight;
        
        public int coalSize;
        public int coalCount;
        public int coalMinHeight;
        public int coalMaxHeight;
        
        public int ironSize;
        public int ironCount;
        public int ironMinHeight;
        public int ironMaxHeight;
        
        public int goldSize;
        public int goldCount;
        public int goldMinHeight;
        public int goldMaxHeight;
        
        public int redstoneSize;
        public int redstoneCount;
        public int redstoneMinHeight;
        public int redstoneMaxHeight;
        
        public int diamondSize;
        public int diamondCount;
        public int diamondMinHeight;
        public int diamondMaxHeight;
        
        public int lapisSize;
        public int lapisCount;
        public int lapisCenterHeight;
        public int lapisSpread;
        
        public int emeraldSize;
        public int emeraldCount;
        public int emeraldMinHeight;
        public int emeraldMaxHeight;
        
        public int quartzSize;
        public int quartzCount;
        
        public int magmaSize;
        public int magmaCount;
        
        public boolean useTallGrass;
        public boolean useNewFlowers;
        public boolean useDoublePlants;
        public boolean useLilyPads;
        public boolean useMelons;
        public boolean useDesertWells;
        public boolean useFossils;
        public boolean useSandDisks;
        public boolean useGravelDisks;
        public boolean useClayDisks;
        
        public boolean useBirchTrees;
        public boolean usePineTrees;
        public boolean useSwampTrees;
        public boolean useJungleTrees;
        public boolean useAcaciaTrees;

        public boolean spawnNewCreatureMobs;
        public boolean spawnNewMonsterMobs;
        public boolean spawnWaterMobs;
        public boolean spawnAmbientMobs;
        public boolean spawnWolves;
        
        public String desertBiomeBase;
        public String desertBiomeOcean;
        public String desertBiomeBeach;
        
        public String forestBiomeBase;
        public String forestBiomeOcean;
        public String forestBiomeBeach;
        
        public String iceDesertBiomeBase;
        public String iceDesertBiomeOcean;
        public String iceDesertBiomeBeach;
        
        public String plainsBiomeBase;
        public String plainsBiomeOcean;
        public String plainsBiomeBeach;
        
        public String rainforestBiomeBase;
        public String rainforestBiomeOcean;
        public String rainforestBiomeBeach;
        
        public String savannaBiomeBase;
        public String savannaBiomeOcean;
        public String savannaBiomeBeach;
        
        public String shrublandBiomeBase;
        public String shrublandBiomeOcean;
        public String shrublandBiomeBeach;
        
        public String seasonalForestBiomeBase;
        public String seasonalForestBiomeOcean;
        public String seasonalForestBiomeBeach;
        
        public String swamplandBiomeBase;
        public String swamplandBiomeOcean;
        public String swamplandBiomeBeach;
        
        public String taigaBiomeBase;
        public String taigaBiomeOcean;
        public String taigaBiomeBeach;
        
        public String tundraBiomeBase;
        public String tundraBiomeOcean;
        public String tundraBiomeBeach;
        
        public Map<ResourceLocation, Property<?>> customProperties;
        
        public Factory() {
            this.chunkSource = ModernBetaBuiltInTypes.Chunk.BETA.getRegistryString();
            this.biomeSource = ModernBetaBuiltInTypes.Biome.BETA.getRegistryString();
            this.surfaceBuilder = ModernBetaBuiltInTypes.Surface.BETA.getRegistryString();
            this.caveCarver = ModernBetaBuiltInTypes.Carver.BETA.getRegistryString();
            this.worldSpawner = ModernBetaBuiltInTypes.WorldSpawner.BETA.getRegistryString();
            
            this.singleBiome = Biomes.PLAINS.getRegistryName().toString();
            this.defaultBlock = Blocks.STONE.getRegistryName().toString();
            this.defaultFluid = Blocks.WATER.getRegistryName().toString();
            
            this.replaceOceanBiomes = true;
            this.replaceBeachBiomes = true;
            this.replaceRiverBiomes = true;
            
            this.coordinateScale = 684.412f;
            this.heightScale = 684.412f;
            this.upperLimitScale = 512.0f;
            this.lowerLimitScale = 512.0f;
            this.scaleNoiseScaleX = 1.121f;
            this.scaleNoiseScaleZ = 1.121f;
            this.depthNoiseScaleX = 200.0f;
            this.depthNoiseScaleZ = 200.0f;
            this.mainNoiseScaleX = 80.0f;
            this.mainNoiseScaleY = 160.0f;
            this.mainNoiseScaleZ = 80.0f;
            this.baseSize = 8.5f;
            this.stretchY = 12.0f;
            this.seaLevel = 64;
            this.height = 128;
            
            this.tempNoiseScale = 1.0f;
            this.rainNoiseScale = 1.0f;
            this.detailNoiseScale = 1.0f;
            
            this.biomeDepthWeight = 1.0f;
            this.biomeDepthOffset = 0.0f;
            this.biomeScaleWeight = 1.0f;
            this.biomeScaleOffset = 0.0f;
            this.useBiomeDepthScale = true;
            this.biomeSize = 4;
            this.riverSize = 4;
            this.layerType = GenLayerType.VANILLA.id;
            this.layerSize = 4;
            this.layerVersion = GenLayerVersion.getVersion();
            this.snowyBiomeChance = 8;
            
            this.endIslandOffset = 100.0f;
            this.endIslandWeight = 8.0f;
            this.endOuterIslandOffset = 100.0f;
            this.endOuterIslandDistance = 64;
            this.useEndOuterIslands = true;
            
            this.caveWidth = 1.0f;
            this.caveHeight = 128;
            this.caveCount = 40;
            this.caveChance = 15;
            this.useDungeons = true;
            this.dungeonChance = 8;
            
            this.useStrongholds = true;
            this.useVillages = true;
            this.useVillageVariants = true;
            this.useMineShafts = true;
            this.useTemples = true;
            this.useMonuments = true;
            this.useMansions = true;
            this.useRavines = true;
            this.useUnderwaterCaves = false;
            
            this.useWaterLakes = true;
            this.waterLakeChance = 4;
            this.useLavaLakes = true;
            this.lavaLakeChance = 80;
            
            this.useSandstone = true;
            
            this.useOldNether = false;
            this.useNetherCaves = true;
            this.useFortresses = true;
            this.useLavaPockets = true;
            
            this.useInfdevWalls = true;
            this.useInfdevPyramids = true;
            
            this.levelTheme = IndevTheme.NORMAL.id;
            this.levelType = IndevType.ISLAND.id;
            this.levelWidth = 256;
            this.levelLength = 256;
            this.levelHeight = 64;
            this.levelHouse = IndevHouse.OAK.id;
            this.useIndevCaves = true;
            this.levelCaveWidth = 1.0f;
            
            this.claySize = 33;
            this.clayCount = 10;
            this.clayMinHeight = 0;
            this.clayMaxHeight = 128;
            
            this.dirtSize = 33;
            this.dirtCount = 20;
            this.dirtMinHeight = 0;
            this.dirtMaxHeight = 128;
            
            this.gravelSize = 33;
            this.gravelCount = 10;
            this.gravelMinHeight = 0;
            this.gravelMaxHeight = 128;
            
            this.graniteSize = 33;
            this.graniteCount = 10;
            this.graniteMinHeight = 0;
            this.graniteMaxHeight = 80;
            
            this.dioriteSize = 33;
            this.dioriteCount = 10;
            this.dioriteMinHeight = 0;
            this.dioriteMaxHeight = 80;
            
            this.andesiteSize = 33;
            this.andesiteCount = 10;
            this.andesiteMinHeight = 0;
            this.andesiteMaxHeight = 80;
            
            this.coalSize = 17;
            this.coalCount = 20;
            this.coalMinHeight = 0;
            this.coalMaxHeight = 128;
            
            this.ironSize = 9;
            this.ironCount = 20;
            this.ironMinHeight = 0;
            this.ironMaxHeight = 64;
            
            this.goldSize = 9;
            this.goldCount = 2;
            this.goldMinHeight = 0;
            this.goldMaxHeight = 32;
            
            this.redstoneSize = 8;
            this.redstoneCount = 8;
            this.redstoneMinHeight = 0;
            this.redstoneMaxHeight = 16;
            
            this.diamondSize = 8;
            this.diamondCount = 1;
            this.diamondMinHeight = 0;
            this.diamondMaxHeight = 16;
            
            this.lapisSize = 7;
            this.lapisCount = 1;
            this.lapisCenterHeight = 16;
            this.lapisSpread = 16;
            
            this.emeraldSize = 3;
            this.emeraldCount = 1;
            this.emeraldMinHeight = 95;
            this.emeraldMaxHeight = 128;
            
            this.quartzSize = 14;
            this.quartzCount = 16;
            
            this.magmaSize = 33;
            this.magmaCount = 4;
            
            this.useTallGrass = true;
            this.useNewFlowers = true;
            this.useDoublePlants = true;
            this.useLilyPads = false;
            this.useMelons = true;
            this.useDesertWells = true;
            this.useFossils = true;
            this.useSandDisks = false;
            this.useGravelDisks = false;
            this.useClayDisks = false;
            
            this.useBirchTrees = true;
            this.usePineTrees = true;
            this.useSwampTrees = false;
            this.useJungleTrees = false;
            this.useAcaciaTrees = false;
            
            this.spawnNewCreatureMobs = true;
            this.spawnNewMonsterMobs = true;
            this.spawnWaterMobs = true;
            this.spawnAmbientMobs = true;
            this.spawnWolves = true;
            
            this.desertBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_DESERT).toString();
            this.desertBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.desertBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_DESERT).toString();
            
            this.forestBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_FOREST).toString();
            this.forestBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.forestBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.iceDesertBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_TUNDRA).toString();
            this.iceDesertBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.iceDesertBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();
            
            this.plainsBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_PLAINS).toString();
            this.plainsBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.plainsBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.rainforestBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_RAINFOREST).toString();
            this.rainforestBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.rainforestBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.savannaBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_SAVANNA).toString();
            this.savannaBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.savannaBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.shrublandBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_SHRUBLAND).toString();
            this.shrublandBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.shrublandBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.seasonalForestBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_SEASONAL_FOREST).toString();
            this.seasonalForestBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.seasonalForestBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.swamplandBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_SWAMPLAND).toString();
            this.swamplandBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.swamplandBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.taigaBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_TAIGA).toString();
            this.taigaBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.taigaBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();
            
            this.tundraBiomeBase = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_TUNDRA).toString();
            this.tundraBiomeOcean = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.tundraBiomeBeach = ModernBeta.createRegistryKey(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();
            
            this.customProperties = new LinkedHashMap<>();
            ModernBetaRegistries.PROPERTY.getKeys().forEach(registryKey -> {
                Property<?> property = ModernBetaRegistries.PROPERTY.get(registryKey);
                property.visitFactory(new NewFactoryPropertyVisitor(), this, registryKey, null);
            });
        }

        @Override
        public String toString() {
            return Factory.JSON_ADAPTER.toJson(this);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            
            Factory factory = (Factory)object;
            
            return
                this.chunkSource.equals(factory.chunkSource) &&
                this.biomeSource.equals(factory.biomeSource) &&
                this.surfaceBuilder.equals(factory.surfaceBuilder) &&
                this.caveCarver.equals(factory.caveCarver) &&
                this.worldSpawner.equals(factory.worldSpawner) &&
                
                this.singleBiome.equals(factory.singleBiome) &&
                this.defaultBlock.equals(factory.defaultBlock) &&
                this.defaultFluid.equals(factory.defaultFluid) &&
                
                this.replaceOceanBiomes == factory.replaceOceanBiomes &&
                this.replaceBeachBiomes == factory.replaceBeachBiomes &&
                this.replaceRiverBiomes == factory.replaceRiverBiomes &&
                        
                Float.compare(factory.coordinateScale, this.coordinateScale) == 0 &&        
                Float.compare(factory.heightScale, this.heightScale) == 0 &&
                Float.compare(factory.upperLimitScale, this.upperLimitScale) == 0 &&
                Float.compare(factory.lowerLimitScale, this.lowerLimitScale) == 0 &&
                Float.compare(factory.scaleNoiseScaleX, this.scaleNoiseScaleX) == 0 &&
                Float.compare(factory.scaleNoiseScaleZ, this.scaleNoiseScaleZ) == 0 &&
                Float.compare(factory.depthNoiseScaleX, this.depthNoiseScaleX) == 0 &&
                Float.compare(factory.depthNoiseScaleZ, this.depthNoiseScaleZ) == 0 &&
                Float.compare(factory.mainNoiseScaleX, this.mainNoiseScaleX) == 0 &&
                Float.compare(factory.mainNoiseScaleY, this.mainNoiseScaleY) == 0 &&
                Float.compare(factory.mainNoiseScaleZ, this.mainNoiseScaleZ) == 0 &&
                Float.compare(factory.baseSize, this.baseSize) == 0 &&
                Float.compare(factory.stretchY, this.stretchY) == 0 &&
                
                this.seaLevel == factory.seaLevel &&
                this.height == factory.height &&
                        
                Float.compare(factory.tempNoiseScale, this.tempNoiseScale) == 0 &&
                Float.compare(factory.rainNoiseScale, this.rainNoiseScale) == 0 &&
                Float.compare(factory.detailNoiseScale, this.detailNoiseScale) == 0 &&
                
                Float.compare(factory.biomeDepthWeight, this.biomeDepthWeight) == 0 &&
                Float.compare(factory.biomeDepthOffset, this.biomeDepthOffset) == 0 &&
                Float.compare(factory.biomeScaleWeight, this.biomeScaleWeight) == 0 &&
                Float.compare(factory.biomeScaleOffset, this.biomeScaleOffset) == 0 &&
                this.useBiomeDepthScale == factory.useBiomeDepthScale &&
                this.biomeSize == factory.biomeSize &&
                this.riverSize == factory.riverSize &&
                this.layerType.equals(factory.layerType) &&
                this.layerSize == factory.layerSize &&
                this.layerVersion == factory.layerVersion &&
                this.snowyBiomeChance == factory.snowyBiomeChance &&
                        
                Float.compare(factory.endIslandOffset, this.endIslandOffset) == 0 &&
                Float.compare(factory.endIslandWeight, this.endIslandWeight) == 0 &&
                Float.compare(factory.endOuterIslandOffset, this.endOuterIslandOffset) == 0 &&
                this.endOuterIslandDistance == factory.endOuterIslandDistance &&
                this.useEndOuterIslands == factory.useEndOuterIslands &&

                Float.compare(factory.caveWidth, this.caveWidth) == 0 &&
                this.caveHeight == factory.caveHeight &&
                this.caveCount == factory.caveCount &&
                this.caveChance == factory.caveChance &&
                this.useDungeons == factory.useDungeons &&
                this.dungeonChance == factory.dungeonChance &&
                
                this.useStrongholds == factory.useStrongholds &&
                this.useVillages == factory.useVillages &&
                this.useVillageVariants == factory.useVillageVariants &&
                this.useMineShafts == factory.useMineShafts &&
                this.useTemples == factory.useTemples &&
                this.useMonuments == factory.useMonuments &&
                this.useMansions == factory.useMansions &&
                this.useRavines == factory.useRavines &&
                this.useUnderwaterCaves == factory.useUnderwaterCaves &&
                
                this.useWaterLakes == factory.useWaterLakes &&
                this.waterLakeChance == factory.waterLakeChance &&
                this.useLavaLakes == factory.useLavaLakes &&
                this.lavaLakeChance == factory.lavaLakeChance &&
                
                this.useSandstone == factory.useSandstone &&
                
                this.useOldNether == factory.useOldNether &&
                this.useNetherCaves == factory.useNetherCaves &&
                this.useFortresses == factory.useFortresses &&
                this.useLavaPockets == factory.useLavaPockets &&
                
                this.useInfdevWalls == factory.useInfdevWalls &&
                this.useInfdevPyramids == factory.useInfdevPyramids &&
                
                this.levelTheme.equals(factory.levelTheme) &&
                this.levelType.equals(factory.levelType) &&
                this.levelWidth == factory.levelWidth &&
                this.levelLength == factory.levelLength &&
                this.levelHeight == factory.levelHeight &&
                this.levelHouse.equals(factory.levelHouse) &&
                this.useIndevCaves == factory.useIndevCaves &&
                Float.compare(factory.levelCaveWidth, this.levelCaveWidth) == 0 &&
                
                this.claySize == factory.claySize &&
                this.clayCount == factory.clayCount &&
                this.clayMinHeight == factory.clayMinHeight &&
                this.clayMaxHeight == factory.clayMaxHeight &&

                this.dirtSize == factory.dirtSize &&
                this.dirtCount == factory.dirtCount &&
                this.dirtMinHeight == factory.dirtMinHeight &&
                this.dirtMaxHeight == factory.dirtMaxHeight &&

                this.gravelSize == factory.gravelSize &&
                this.gravelCount == factory.gravelCount &&
                this.gravelMinHeight == factory.gravelMinHeight &&
                this.gravelMaxHeight == factory.gravelMaxHeight &&

                this.graniteSize == factory.graniteSize &&
                this.graniteCount == factory.graniteCount &&
                this.graniteMinHeight == factory.graniteMinHeight &&
                this.graniteMaxHeight == factory.graniteMaxHeight &&

                this.dioriteSize == factory.dioriteSize &&
                this.dioriteCount == factory.dioriteCount &&
                this.dioriteMinHeight == factory.dioriteMinHeight &&
                this.dioriteMaxHeight == factory.dioriteMaxHeight &&

                this.andesiteSize == factory.andesiteSize &&
                this.andesiteCount == factory.andesiteCount &&
                this.andesiteMinHeight == factory.andesiteMinHeight &&
                this.andesiteMaxHeight == factory.andesiteMaxHeight &&

                this.coalSize == factory.coalSize &&
                this.coalCount == factory.coalCount &&
                this.coalMinHeight == factory.coalMinHeight &&
                this.coalMaxHeight == factory.coalMaxHeight &&

                this.ironSize == factory.ironSize &&
                this.ironCount == factory.ironCount &&
                this.ironMinHeight == factory.ironMinHeight &&
                this.ironMaxHeight == factory.ironMaxHeight &&

                this.goldSize == factory.goldSize &&
                this.goldCount == factory.goldCount &&
                this.goldMinHeight == factory.goldMinHeight &&
                this.goldMaxHeight == factory.goldMaxHeight &&

                this.redstoneSize == factory.redstoneSize &&
                this.redstoneCount == factory.redstoneCount &&
                this.redstoneMinHeight == factory.redstoneMinHeight &&
                this.redstoneMaxHeight == factory.redstoneMaxHeight &&

                this.diamondSize == factory.diamondSize &&
                this.diamondCount == factory.diamondCount &&
                this.diamondMinHeight == factory.diamondMinHeight &&
                this.diamondMaxHeight == factory.diamondMaxHeight &&

                this.lapisSize == factory.lapisSize &&
                this.lapisCount == factory.lapisCount &&
                this.lapisCenterHeight == factory.lapisCenterHeight &&
                this.lapisSpread == factory.lapisSpread &&

                this.emeraldSize == factory.emeraldSize &&
                this.emeraldCount == factory.emeraldCount &&
                this.emeraldMinHeight == factory.emeraldMinHeight &&
                this.emeraldMaxHeight == factory.emeraldMaxHeight &&
        
                this.quartzSize == factory.quartzSize &&
                this.quartzCount == factory.quartzCount &&

                this.magmaSize == factory.magmaSize &&
                this.magmaCount == factory.magmaCount &&
                
                this.useTallGrass == factory.useTallGrass &&
                this.useNewFlowers == factory.useNewFlowers &&
                this.useDoublePlants == factory.useDoublePlants &&
                this.useLilyPads == factory.useLilyPads &&
                this.useMelons == factory.useMelons &&
                this.useDesertWells == factory.useDesertWells &&
                this.useFossils == factory.useFossils &&
                this.useSandDisks == factory.useSandDisks &&
                this.useGravelDisks == factory.useGravelDisks &&
                this.useClayDisks == factory.useClayDisks &&
                        
                this.useBirchTrees == factory.useBirchTrees &&
                this.usePineTrees == factory.usePineTrees &&
                this.useSwampTrees == factory.useSwampTrees &&
                this.useJungleTrees == factory.useJungleTrees &&
                this.useAcaciaTrees == factory.useAcaciaTrees &&
                
                this.spawnNewCreatureMobs == factory.spawnNewCreatureMobs &&
                this.spawnNewMonsterMobs == factory.spawnNewMonsterMobs &&
                this.spawnWaterMobs == factory.spawnWaterMobs &&
                this.spawnAmbientMobs == factory.spawnAmbientMobs &&
                this.spawnWolves == factory.spawnWolves &&
                
                this.desertBiomeBase.equals(factory.desertBiomeBase) &&
                this.desertBiomeOcean.equals(factory.desertBiomeOcean) &&
                this.desertBiomeBeach.equals(factory.desertBiomeBeach) &&
                
                this.forestBiomeBase.equals(factory.forestBiomeBase) &&
                this.forestBiomeOcean.equals(factory.forestBiomeOcean) &&
                this.forestBiomeBeach.equals(factory.forestBiomeBeach) &&
                
                this.iceDesertBiomeBase.equals(factory.iceDesertBiomeBase) &&
                this.iceDesertBiomeOcean.equals(factory.iceDesertBiomeOcean) &&
                this.iceDesertBiomeBeach.equals(factory.iceDesertBiomeBeach) &&
                
                this.plainsBiomeBase.equals(factory.plainsBiomeBase) &&
                this.plainsBiomeOcean.equals(factory.plainsBiomeOcean) &&
                this.plainsBiomeBeach.equals(factory.plainsBiomeBeach) &&
                
                this.rainforestBiomeBase.equals(factory.rainforestBiomeBase) &&
                this.rainforestBiomeOcean.equals(factory.rainforestBiomeOcean) &&
                this.rainforestBiomeBeach.equals(factory.rainforestBiomeBeach) &&
                
                this.savannaBiomeBase.equals(factory.savannaBiomeBase) &&
                this.savannaBiomeOcean.equals(factory.savannaBiomeOcean) &&
                this.savannaBiomeBeach.equals(factory.savannaBiomeBeach) &&
                
                this.shrublandBiomeBase.equals(factory.shrublandBiomeBase) &&
                this.shrublandBiomeOcean.equals(factory.shrublandBiomeOcean) &&
                this.shrublandBiomeBeach.equals(factory.shrublandBiomeBeach) &&
                
                this.seasonalForestBiomeBase.equals(factory.seasonalForestBiomeBase) &&
                this.seasonalForestBiomeOcean.equals(factory.seasonalForestBiomeOcean) &&
                this.seasonalForestBiomeBeach.equals(factory.seasonalForestBiomeBeach) &&
                
                this.swamplandBiomeBase.equals(factory.swamplandBiomeBase) &&
                this.swamplandBiomeOcean.equals(factory.swamplandBiomeOcean) &&
                this.swamplandBiomeBeach.equals(factory.swamplandBiomeBeach) &&
                
                this.taigaBiomeBase.equals(factory.taigaBiomeBase) &&
                this.taigaBiomeOcean.equals(factory.taigaBiomeOcean) &&
                this.taigaBiomeBeach.equals(factory.taigaBiomeBeach) &&
                
                this.tundraBiomeBase.equals(factory.tundraBiomeBase) &&
                this.tundraBiomeOcean.equals(factory.tundraBiomeOcean) &&
                this.tundraBiomeBeach.equals(factory.tundraBiomeBeach) &&
                
                this.customProperties.equals(factory.customProperties)
                
                ;
        }
        
        @Override
        public int hashCode() {
            int hashCode = this.chunkSource.hashCode();
            hashCode = 31 * hashCode + this.biomeSource.hashCode();
            hashCode = 31 * hashCode + this.surfaceBuilder.hashCode();
            hashCode = 31 * hashCode + this.caveCarver.hashCode();
            hashCode = 31 * hashCode + this.worldSpawner.hashCode();
            
            hashCode = 31 * hashCode + this.singleBiome.hashCode();
            hashCode = 31 * hashCode + this.defaultBlock.hashCode();
            hashCode = 31 * hashCode + this.defaultFluid.hashCode();
            
            hashCode = 31 * hashCode + (this.replaceOceanBiomes ? 1 : 0);
            hashCode = 31 * hashCode + (this.replaceBeachBiomes ? 1 : 0);
            hashCode = 31 * hashCode + (this.replaceRiverBiomes ? 1 : 0);
            
            hashCode = 31 * hashCode + ((this.coordinateScale == 0.0f) ? 0 : Float.floatToIntBits(this.coordinateScale));
            hashCode = 31 * hashCode + ((this.heightScale == 0.0f) ? 0 : Float.floatToIntBits(this.heightScale));
            hashCode = 31 * hashCode + ((this.upperLimitScale == 0.0f) ? 0 : Float.floatToIntBits(this.upperLimitScale));
            hashCode = 31 * hashCode + ((this.lowerLimitScale == 0.0f) ? 0 : Float.floatToIntBits(this.lowerLimitScale));
            hashCode = 31 * hashCode + ((this.scaleNoiseScaleX == 0.0f) ? 0 : Float.floatToIntBits(this.scaleNoiseScaleX));
            hashCode = 31 * hashCode + ((this.scaleNoiseScaleZ == 0.0f) ? 0 : Float.floatToIntBits(this.scaleNoiseScaleZ));
            hashCode = 31 * hashCode + ((this.depthNoiseScaleX == 0.0f) ? 0 : Float.floatToIntBits(this.depthNoiseScaleX));
            hashCode = 31 * hashCode + ((this.depthNoiseScaleZ == 0.0f) ? 0 : Float.floatToIntBits(this.depthNoiseScaleZ));
            hashCode = 31 * hashCode + ((this.mainNoiseScaleX == 0.0f) ? 0 : Float.floatToIntBits(this.mainNoiseScaleX));
            hashCode = 31 * hashCode + ((this.mainNoiseScaleY == 0.0f) ? 0 : Float.floatToIntBits(this.mainNoiseScaleY));
            hashCode = 31 * hashCode + ((this.mainNoiseScaleZ == 0.0f) ? 0 : Float.floatToIntBits(this.mainNoiseScaleZ));
            hashCode = 31 * hashCode + ((this.baseSize == 0.0f) ? 0 : Float.floatToIntBits(this.baseSize));
            hashCode = 31 * hashCode + ((this.stretchY == 0.0f) ? 0 : Float.floatToIntBits(this.stretchY));
            hashCode = 31 * hashCode + this.seaLevel;
            hashCode = 31 * hashCode + this.height;
            
            hashCode = 31 * hashCode + ((this.tempNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.tempNoiseScale));
            hashCode = 31 * hashCode + ((this.rainNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.rainNoiseScale));
            hashCode = 31 * hashCode + ((this.detailNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.detailNoiseScale));
            
            hashCode = 31 * hashCode + ((this.biomeDepthWeight == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthWeight));
            hashCode = 31 * hashCode + ((this.biomeDepthOffset == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthOffset));
            hashCode = 31 * hashCode + ((this.biomeScaleWeight == 0.0f) ? 0 : Float.floatToIntBits(this.biomeScaleWeight));
            hashCode = 31 * hashCode + ((this.biomeDepthOffset == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthOffset));
            hashCode = 31 * hashCode + (this.useBiomeDepthScale ? 1 : 0);
            hashCode = 31 * hashCode + this.biomeSize;
            hashCode = 31 * hashCode + this.riverSize;
            hashCode = 31 * hashCode + this.layerType.hashCode();
            hashCode = 31 * hashCode + this.layerSize;
            hashCode = 31 * hashCode + this.layerVersion;
            hashCode = 31 * hashCode + this.snowyBiomeChance;
            
            hashCode = 31 * hashCode + ((this.endIslandOffset == 0.0f) ? 0 : Float.floatToIntBits(this.endIslandOffset));
            hashCode = 31 * hashCode + ((this.endIslandWeight == 0.0f) ? 0 : Float.floatToIntBits(this.endIslandWeight));
            hashCode = 31 * hashCode + ((this.endOuterIslandOffset == 0.0f) ? 0 : Float.floatToIntBits(this.endOuterIslandOffset));
            hashCode = 31 * hashCode + this.endOuterIslandDistance;
            hashCode = 31 * hashCode + (this.useEndOuterIslands ? 1 : 0);

            hashCode = 31 * hashCode + ((this.caveWidth == 0.0f) ? 0 : Float.floatToIntBits(this.caveWidth));
            hashCode = 31 * hashCode + this.caveHeight;
            hashCode = 31 * hashCode + this.caveCount;
            hashCode = 31 * hashCode + this.caveChance;
            hashCode = 31 * hashCode + (this.useDungeons ? 1 : 0);
            hashCode = 31 * hashCode + this.dungeonChance;
            
            hashCode = 31 * hashCode + (this.useStrongholds ? 1 : 0);
            hashCode = 31 * hashCode + (this.useVillages ? 1 : 0);
            hashCode = 31 * hashCode + (this.useVillageVariants ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMineShafts ? 1 : 0);
            hashCode = 31 * hashCode + (this.useTemples ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMonuments ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMansions ? 1 : 0);
            hashCode = 31 * hashCode + (this.useRavines ? 1 : 0);
            hashCode = 31 * hashCode + (this.useUnderwaterCaves ? 1 : 0);
            
            hashCode = 31 * hashCode + (this.useWaterLakes ? 1 : 0);
            hashCode = 31 * hashCode + this.waterLakeChance;
            hashCode = 31 * hashCode + (this.useLavaLakes ? 1 : 0);
            hashCode = 31 * hashCode + this.lavaLakeChance;
            
            hashCode = 31 * hashCode + (this.useSandstone ? 1 : 0);
            
            hashCode = 31 * hashCode + (this.useOldNether ? 1 : 0);
            hashCode = 31 * hashCode + (this.useNetherCaves ? 1 : 0);
            hashCode = 31 * hashCode + (this.useFortresses ? 1 : 0);
            hashCode = 31 * hashCode + (this.useLavaPockets ? 1 : 0);

            hashCode = 31 * hashCode + (this.useInfdevWalls ? 1 : 0);
            hashCode = 31 * hashCode + (this.useInfdevPyramids ? 1 : 0);
            
            hashCode = 31 * hashCode + this.levelTheme.hashCode();
            hashCode = 31 * hashCode + this.levelType.hashCode();
            hashCode = 31 * hashCode + this.levelWidth;
            hashCode = 31 * hashCode + this.levelLength;
            hashCode = 31 * hashCode + this.levelHeight;
            hashCode = 31 * hashCode + this.levelHouse.hashCode();
            hashCode = 31 * hashCode + (this.useIndevCaves ? 1 : 0);
            hashCode = 31 * hashCode + ((this.levelCaveWidth == 0.0f) ? 0 : Float.floatToIntBits(this.levelCaveWidth));
            
            hashCode = 31 * hashCode + this.claySize;
            hashCode = 31 * hashCode + this.clayCount;
            hashCode = 31 * hashCode + this.clayMinHeight;
            hashCode = 31 * hashCode + this.clayMaxHeight;
            
            hashCode = 31 * hashCode + this.dirtSize;
            hashCode = 31 * hashCode + this.dirtCount;
            hashCode = 31 * hashCode + this.dirtMinHeight;
            hashCode = 31 * hashCode + this.dirtMaxHeight;
            
            hashCode = 31 * hashCode + this.gravelSize;
            hashCode = 31 * hashCode + this.gravelCount;
            hashCode = 31 * hashCode + this.gravelMinHeight;
            hashCode = 31 * hashCode + this.gravelMaxHeight;
            
            hashCode = 31 * hashCode + this.graniteSize;
            hashCode = 31 * hashCode + this.graniteCount;
            hashCode = 31 * hashCode + this.graniteMinHeight;
            hashCode = 31 * hashCode + this.graniteMaxHeight;
            
            hashCode = 31 * hashCode + this.dioriteSize;
            hashCode = 31 * hashCode + this.dioriteCount;
            hashCode = 31 * hashCode + this.dioriteMinHeight;
            hashCode = 31 * hashCode + this.dioriteMaxHeight;
            
            hashCode = 31 * hashCode + this.andesiteSize;
            hashCode = 31 * hashCode + this.andesiteCount;
            hashCode = 31 * hashCode + this.andesiteMinHeight;
            hashCode = 31 * hashCode + this.andesiteMaxHeight;
            
            hashCode = 31 * hashCode + this.coalSize;
            hashCode = 31 * hashCode + this.coalCount;
            hashCode = 31 * hashCode + this.coalMinHeight;
            hashCode = 31 * hashCode + this.coalMaxHeight;
            
            hashCode = 31 * hashCode + this.ironSize;
            hashCode = 31 * hashCode + this.ironCount;
            hashCode = 31 * hashCode + this.ironMinHeight;
            hashCode = 31 * hashCode + this.ironMaxHeight;
            
            hashCode = 31 * hashCode + this.goldSize;
            hashCode = 31 * hashCode + this.goldCount;
            hashCode = 31 * hashCode + this.goldMinHeight;
            hashCode = 31 * hashCode + this.goldMaxHeight;
            
            hashCode = 31 * hashCode + this.redstoneSize;
            hashCode = 31 * hashCode + this.redstoneCount;
            hashCode = 31 * hashCode + this.redstoneMinHeight;
            hashCode = 31 * hashCode + this.redstoneMaxHeight;
            
            hashCode = 31 * hashCode + this.diamondSize;
            hashCode = 31 * hashCode + this.diamondCount;
            hashCode = 31 * hashCode + this.diamondMinHeight;
            hashCode = 31 * hashCode + this.diamondMaxHeight;
            
            hashCode = 31 * hashCode + this.lapisSize;
            hashCode = 31 * hashCode + this.lapisCount;
            hashCode = 31 * hashCode + this.lapisCenterHeight;
            hashCode = 31 * hashCode + this.lapisSpread;
            
            hashCode = 31 * hashCode + this.emeraldSize;
            hashCode = 31 * hashCode + this.emeraldCount;
            hashCode = 31 * hashCode + this.emeraldMinHeight;
            hashCode = 31 * hashCode + this.emeraldMaxHeight;
            
            hashCode = 31 * hashCode + this.quartzSize;
            hashCode = 31 * hashCode + this.quartzCount;
            
            hashCode = 31 * hashCode + this.magmaSize;
            hashCode = 31 * hashCode + this.magmaCount;

            hashCode = 31 * hashCode + (this.useTallGrass ? 1 : 0);
            hashCode = 31 * hashCode + (this.useNewFlowers ? 1 : 0);
            hashCode = 31 * hashCode + (this.useDoublePlants ? 1 : 0);
            hashCode = 31 * hashCode + (this.useLilyPads ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMelons ? 1 : 0);
            hashCode = 31 * hashCode + (this.useDesertWells ? 1 : 0);
            hashCode = 31 * hashCode + (this.useFossils ? 1 : 0);
            hashCode = 31 * hashCode + (this.useSandDisks ? 1 : 0);
            hashCode = 31 * hashCode + (this.useGravelDisks ? 1 : 0);
            hashCode = 31 * hashCode + (this.useClayDisks ? 1 : 0);

            hashCode = 31 * hashCode + (this.useBirchTrees ? 1 : 0);
            hashCode = 31 * hashCode + (this.usePineTrees ? 1 : 0);
            hashCode = 31 * hashCode + (this.useSwampTrees ? 1 : 0);
            hashCode = 31 * hashCode + (this.useJungleTrees ? 1 : 0);
            hashCode = 31 * hashCode + (this.useAcaciaTrees ? 1 : 0);

            hashCode = 31 * hashCode + (this.spawnNewCreatureMobs ? 1 : 0);
            hashCode = 31 * hashCode + (this.spawnNewMonsterMobs ? 1 : 0);
            hashCode = 31 * hashCode + (this.spawnWaterMobs ? 1 : 0);
            hashCode = 31 * hashCode + (this.spawnAmbientMobs ? 1 : 0);
            hashCode = 31 * hashCode + (this.spawnWolves ? 1 : 0);
            
            hashCode = 31 * hashCode + this.desertBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.desertBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.desertBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.forestBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.forestBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.forestBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.iceDesertBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.iceDesertBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.iceDesertBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.plainsBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.plainsBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.plainsBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.rainforestBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.rainforestBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.rainforestBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.savannaBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.savannaBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.savannaBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.shrublandBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.shrublandBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.shrublandBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.seasonalForestBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.seasonalForestBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.seasonalForestBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.swamplandBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.swamplandBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.swamplandBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.taigaBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.taigaBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.taigaBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.tundraBiomeBase.hashCode();
            hashCode = 31 * hashCode + this.tundraBiomeOcean.hashCode();
            hashCode = 31 * hashCode + this.tundraBiomeBeach.hashCode();
            
            hashCode = 31 * hashCode + this.customProperties.hashCode();
            
            return hashCode;
        }
        
        public ModernBetaGeneratorSettings build() {
            return new ModernBetaGeneratorSettings(this);
        }

        public static Factory jsonToFactory(String string) {
            if (string.isEmpty()) {
                return new Factory();
            }
            
            try {
                return JsonUtils.<Factory>gsonDeserialize(Factory.JSON_ADAPTER, string, Factory.class);
            } catch (Exception e) {
                return new Factory();
            }
        }

        static {
            JSON_ADAPTER = new GsonBuilder().registerTypeAdapter(Factory.class, new Serializer()).create();
        }
    }
    
    public static class Serializer implements JsonDeserializer<Factory>, JsonSerializer<Factory> {
        @Override
        public Factory deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Factory factory = new Factory();
            
            try {
                factory.chunkSource = JsonUtils.getString(jsonObject, NbtTags.CHUNK_SOURCE, factory.chunkSource);
                factory.biomeSource = JsonUtils.getString(jsonObject, NbtTags.BIOME_SOURCE, factory.biomeSource);
                factory.surfaceBuilder = JsonUtils.getString(jsonObject, NbtTags.SURFACE_BUILDER, factory.surfaceBuilder);
                factory.caveCarver = JsonUtils.getString(jsonObject, NbtTags.CAVE_CARVER, factory.caveCarver);
                factory.worldSpawner = JsonUtils.getString(jsonObject, NbtTags.WORLD_SPAWNER, factory.worldSpawner);
                
                factory.singleBiome = JsonUtils.getString(jsonObject, NbtTags.SINGLE_BIOME, factory.singleBiome);
                factory.defaultBlock = JsonUtils.getString(jsonObject, NbtTags.DEFAULT_BLOCK, factory.defaultBlock);
                factory.defaultFluid = JsonUtils.getString(jsonObject, NbtTags.DEFAULT_FLUID, factory.defaultFluid);
                
                factory.replaceOceanBiomes = JsonUtils.getBoolean(jsonObject, NbtTags.REPLACE_OCEAN_BIOMES, factory.replaceOceanBiomes);
                factory.replaceBeachBiomes = JsonUtils.getBoolean(jsonObject, NbtTags.REPLACE_BEACH_BIOMES, factory.replaceBeachBiomes);
                factory.replaceRiverBiomes = JsonUtils.getBoolean(jsonObject, NbtTags.REPLACE_RIVER_BIOMES, factory.replaceRiverBiomes);
                
                factory.coordinateScale = JsonUtils.getFloat(jsonObject, NbtTags.COORDINATE_SCALE, factory.coordinateScale);
                factory.heightScale = JsonUtils.getFloat(jsonObject, NbtTags.HEIGHT_SCALE, factory.heightScale);
                factory.lowerLimitScale = JsonUtils.getFloat(jsonObject, NbtTags.LOWER_LIMIT_SCALE, factory.lowerLimitScale);
                factory.upperLimitScale = JsonUtils.getFloat(jsonObject, NbtTags.UPPER_LIMIT_SCALE, factory.upperLimitScale);
                factory.scaleNoiseScaleX = JsonUtils.getFloat(jsonObject, NbtTags.SCALE_NOISE_SCALE_X, factory.scaleNoiseScaleX);
                factory.scaleNoiseScaleZ = JsonUtils.getFloat(jsonObject, NbtTags.SCALE_NOISE_SCALE_Z, factory.scaleNoiseScaleZ);
                factory.depthNoiseScaleX = JsonUtils.getFloat(jsonObject, NbtTags.DEPTH_NOISE_SCALE_X, factory.depthNoiseScaleX);
                factory.depthNoiseScaleZ = JsonUtils.getFloat(jsonObject, NbtTags.DEPTH_NOISE_SCALE_Z, factory.depthNoiseScaleZ);
                factory.mainNoiseScaleX = JsonUtils.getFloat(jsonObject, NbtTags.MAIN_NOISE_SCALE_X, factory.mainNoiseScaleX);
                factory.mainNoiseScaleY = JsonUtils.getFloat(jsonObject, NbtTags.MAIN_NOISE_SCALE_Y, factory.mainNoiseScaleY);
                factory.mainNoiseScaleZ = JsonUtils.getFloat(jsonObject, NbtTags.MAIN_NOISE_SCALE_Z, factory.mainNoiseScaleZ);
                factory.baseSize = JsonUtils.getFloat(jsonObject, NbtTags.BASE_SIZE, factory.baseSize);
                factory.stretchY = JsonUtils.getFloat(jsonObject, NbtTags.STRETCH_Y, factory.stretchY);
                factory.seaLevel = JsonUtils.getInt(jsonObject, NbtTags.SEA_LEVEL, factory.seaLevel);
                factory.height = JsonUtils.getInt(jsonObject, NbtTags.HEIGHT, factory.height);

                factory.tempNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.TEMP_NOISE_SCALE, factory.tempNoiseScale);
                factory.rainNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.RAIN_NOISE_SCALE, factory.rainNoiseScale);
                factory.detailNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.DETAIL_NOISE_SCALE, factory.detailNoiseScale);
                
                factory.biomeDepthWeight = JsonUtils.getFloat(jsonObject, NbtTags.BIOME_DEPTH_WEIGHT, factory.biomeDepthWeight);
                factory.biomeDepthOffset = JsonUtils.getFloat(jsonObject, NbtTags.BIOME_DEPTH_OFFSET, factory.biomeDepthOffset);
                factory.biomeScaleWeight = JsonUtils.getFloat(jsonObject, NbtTags.BIOME_SCALE_WEIGHT, factory.biomeScaleWeight);
                factory.biomeScaleOffset = JsonUtils.getFloat(jsonObject, NbtTags.BIOME_SCALE_OFFSET, factory.biomeScaleOffset);
                factory.useBiomeDepthScale = JsonUtils.getBoolean(jsonObject, NbtTags.USE_BIOME_DEPTH_SCALE, factory.useBiomeDepthScale);
                factory.biomeSize = JsonUtils.getInt(jsonObject, NbtTags.BIOME_SIZE, factory.biomeSize);
                factory.riverSize = JsonUtils.getInt(jsonObject, NbtTags.RIVER_SIZE, factory.riverSize);
                factory.layerType = JsonUtils.getString(jsonObject, NbtTags.LAYER_TYPE, factory.layerType);
                factory.layerSize = JsonUtils.getInt(jsonObject, NbtTags.LAYER_SIZE, factory.layerSize);
                factory.layerVersion = JsonUtils.getInt(jsonObject, NbtTags.LAYER_VERSION, factory.layerVersion);
                factory.snowyBiomeChance = JsonUtils.getInt(jsonObject, NbtTags.SNOWY_BIOME_CHANCE, factory.snowyBiomeChance);
                
                factory.endIslandOffset = JsonUtils.getFloat(jsonObject, NbtTags.END_ISLAND_OFFSET, factory.endIslandOffset);
                factory.endIslandWeight = JsonUtils.getFloat(jsonObject, NbtTags.END_ISLAND_WEIGHT, factory.endIslandWeight);
                factory.endOuterIslandOffset = JsonUtils.getFloat(jsonObject, NbtTags.END_OUTER_ISLAND_OFFSET, factory.endOuterIslandOffset);
                factory.endOuterIslandDistance = JsonUtils.getInt(jsonObject, NbtTags.END_OUTER_ISLAND_DISTANCE, factory.endOuterIslandDistance);
                factory.useEndOuterIslands = JsonUtils.getBoolean(jsonObject, NbtTags.USE_END_OUTER_ISLANDS, factory.useEndOuterIslands);
                
                factory.caveWidth = JsonUtils.getFloat(jsonObject, NbtTags.CAVE_WIDTH, factory.caveWidth);
                factory.caveHeight = JsonUtils.getInt(jsonObject, NbtTags.CAVE_HEIGHT, factory.caveHeight);
                factory.caveCount = JsonUtils.getInt(jsonObject, NbtTags.CAVE_COUNT, factory.caveCount);
                factory.caveChance = JsonUtils.getInt(jsonObject, NbtTags.CAVE_CHANCE, factory.caveChance);
                factory.useDungeons = JsonUtils.getBoolean(jsonObject, NbtTags.USE_DUNGEONS, factory.useDungeons);
                factory.dungeonChance = JsonUtils.getInt(jsonObject, NbtTags.DUNGEON_CHANCE, factory.dungeonChance);
                
                factory.useStrongholds = JsonUtils.getBoolean(jsonObject, NbtTags.USE_STRONGHOLDS, factory.useStrongholds);
                factory.useVillages = JsonUtils.getBoolean(jsonObject, NbtTags.USE_VILLAGES, factory.useVillages);
                factory.useVillageVariants = JsonUtils.getBoolean(jsonObject, NbtTags.USE_VILLAGE_VARIANTS, factory.useVillageVariants);
                factory.useMineShafts = JsonUtils.getBoolean(jsonObject, NbtTags.USE_MINESHAFTS, factory.useMineShafts);
                factory.useTemples = JsonUtils.getBoolean(jsonObject, NbtTags.USE_TEMPLES, factory.useTemples);
                factory.useMonuments = JsonUtils.getBoolean(jsonObject, NbtTags.USE_MONUMENTS, factory.useMonuments);
                factory.useMansions = JsonUtils.getBoolean(jsonObject, NbtTags.USE_MANSIONS, factory.useMansions);
                factory.useRavines = JsonUtils.getBoolean(jsonObject, NbtTags.USE_RAVINES, factory.useRavines);
                factory.useUnderwaterCaves = JsonUtils.getBoolean(jsonObject, NbtTags.USE_UNDERWATER_CAVES, factory.useUnderwaterCaves);
                
                factory.useWaterLakes = JsonUtils.getBoolean(jsonObject, NbtTags.USE_WATER_LAKES, factory.useWaterLakes);
                factory.waterLakeChance = JsonUtils.getInt(jsonObject, NbtTags.WATER_LAKE_CHANCE, factory.waterLakeChance);
                factory.useLavaLakes = JsonUtils.getBoolean(jsonObject, NbtTags.USE_LAVA_LAKES, factory.useLavaLakes);
                factory.lavaLakeChance = JsonUtils.getInt(jsonObject, NbtTags.LAVA_LAKE_CHANCE, factory.lavaLakeChance);
                
                factory.useSandstone = JsonUtils.getBoolean(jsonObject, NbtTags.USE_SANDSTONE, factory.useSandstone);
                
                factory.useOldNether = JsonUtils.getBoolean(jsonObject, NbtTags.USE_OLD_NETHER, factory.useOldNether);
                factory.useNetherCaves = JsonUtils.getBoolean(jsonObject, NbtTags.USE_NETHER_CAVES, factory.useNetherCaves);
                factory.useFortresses = JsonUtils.getBoolean(jsonObject, NbtTags.USE_FORTRESSES, factory.useFortresses);
                factory.useLavaPockets = JsonUtils.getBoolean(jsonObject, NbtTags.USE_LAVA_POCKETS, factory.useLavaPockets);
                
                factory.useInfdevWalls = JsonUtils.getBoolean(jsonObject, NbtTags.USE_INFDEV_WALLS, factory.useInfdevWalls);
                factory.useInfdevPyramids = JsonUtils.getBoolean(jsonObject, NbtTags.USE_INFDEV_PYRAMIDS, factory.useInfdevPyramids);
                
                factory.levelTheme = JsonUtils.getString(jsonObject, NbtTags.LEVEL_THEME, factory.levelTheme);
                factory.levelType = JsonUtils.getString(jsonObject, NbtTags.LEVEL_TYPE, factory.levelType);
                factory.levelWidth = JsonUtils.getInt(jsonObject, NbtTags.LEVEL_WIDTH, factory.levelWidth);
                factory.levelLength = JsonUtils.getInt(jsonObject, NbtTags.LEVEL_LENGTH, factory.levelLength);
                factory.levelHeight = JsonUtils.getInt(jsonObject, NbtTags.LEVEL_HEIGHT, factory.levelHeight);
                factory.levelHouse = JsonUtils.getString(jsonObject, NbtTags.LEVEL_HOUSE, factory.levelHouse);
                factory.useIndevCaves = JsonUtils.getBoolean(jsonObject, NbtTags.USE_INDEV_CAVES, factory.useIndevCaves);
                factory.levelCaveWidth = JsonUtils.getFloat(jsonObject, NbtTags.LEVEL_CAVE_WIDTH, factory.levelCaveWidth);
                
                factory.claySize = JsonUtils.getInt(jsonObject, NbtTags.CLAY_SIZE, factory.claySize);
                factory.clayCount = JsonUtils.getInt(jsonObject, NbtTags.CLAY_COUNT, factory.clayCount);
                factory.clayMinHeight = JsonUtils.getInt(jsonObject, NbtTags.CLAY_MIN_HEIGHT, factory.clayMinHeight);
                factory.clayMaxHeight = JsonUtils.getInt(jsonObject, NbtTags.CLAY_MAX_HEIGHT, factory.clayMaxHeight);
                
                factory.dirtSize = JsonUtils.getInt(jsonObject, "dirtSize", factory.dirtSize);
                factory.dirtCount = JsonUtils.getInt(jsonObject, "dirtCount", factory.dirtCount);
                factory.dirtMinHeight = JsonUtils.getInt(jsonObject, "dirtMinHeight", factory.dirtMinHeight);
                factory.dirtMaxHeight = JsonUtils.getInt(jsonObject, "dirtMaxHeight", factory.dirtMaxHeight);
                
                factory.gravelSize = JsonUtils.getInt(jsonObject, "gravelSize", factory.gravelSize);
                factory.gravelCount = JsonUtils.getInt(jsonObject, "gravelCount", factory.gravelCount);
                factory.gravelMinHeight = JsonUtils.getInt(jsonObject, "gravelMinHeight", factory.gravelMinHeight);
                factory.gravelMaxHeight = JsonUtils.getInt(jsonObject, "gravelMaxHeight", factory.gravelMaxHeight);
                
                factory.graniteSize = JsonUtils.getInt(jsonObject, "graniteSize", factory.graniteSize);
                factory.graniteCount = JsonUtils.getInt(jsonObject, "graniteCount", factory.graniteCount);
                factory.graniteMinHeight = JsonUtils.getInt(jsonObject, "graniteMinHeight", factory.graniteMinHeight);
                factory.graniteMaxHeight = JsonUtils.getInt(jsonObject, "graniteMaxHeight", factory.graniteMaxHeight);
                
                factory.dioriteSize = JsonUtils.getInt(jsonObject, "dioriteSize", factory.dioriteSize);
                factory.dioriteCount = JsonUtils.getInt(jsonObject, "dioriteCount", factory.dioriteCount);
                factory.dioriteMinHeight = JsonUtils.getInt(jsonObject, "dioriteMinHeight", factory.dioriteMinHeight);
                factory.dioriteMaxHeight = JsonUtils.getInt(jsonObject, "dioriteMaxHeight", factory.dioriteMaxHeight);
                
                factory.andesiteSize = JsonUtils.getInt(jsonObject, "andesiteSize", factory.andesiteSize);
                factory.andesiteCount = JsonUtils.getInt(jsonObject, "andesiteCount", factory.andesiteCount);
                factory.andesiteMinHeight = JsonUtils.getInt(jsonObject, "andesiteMinHeight", factory.andesiteMinHeight);
                factory.andesiteMaxHeight = JsonUtils.getInt(jsonObject, "andesiteMaxHeight", factory.andesiteMaxHeight);
                
                factory.coalSize = JsonUtils.getInt(jsonObject, "coalSize", factory.coalSize);
                factory.coalCount = JsonUtils.getInt(jsonObject, "coalCount", factory.coalCount);
                factory.coalMinHeight = JsonUtils.getInt(jsonObject, "coalMinHeight", factory.coalMinHeight);
                factory.coalMaxHeight = JsonUtils.getInt(jsonObject, "coalMaxHeight", factory.coalMaxHeight);
                
                factory.ironSize = JsonUtils.getInt(jsonObject, "ironSize", factory.ironSize);
                factory.ironCount = JsonUtils.getInt(jsonObject, "ironCount", factory.ironCount);
                factory.ironMinHeight = JsonUtils.getInt(jsonObject, "ironMinHeight", factory.ironMinHeight);
                factory.ironMaxHeight = JsonUtils.getInt(jsonObject, "ironMaxHeight", factory.ironMaxHeight);
                
                factory.goldSize = JsonUtils.getInt(jsonObject, "goldSize", factory.goldSize);
                factory.goldCount = JsonUtils.getInt(jsonObject, "goldCount", factory.goldCount);
                factory.goldMinHeight = JsonUtils.getInt(jsonObject, "goldMinHeight", factory.goldMinHeight);
                factory.goldMaxHeight = JsonUtils.getInt(jsonObject, "goldMaxHeight", factory.goldMaxHeight);
                
                factory.redstoneSize = JsonUtils.getInt(jsonObject, "redstoneSize", factory.redstoneSize);
                factory.redstoneCount = JsonUtils.getInt(jsonObject, "redstoneCount", factory.redstoneCount);
                factory.redstoneMinHeight = JsonUtils.getInt(jsonObject, "redstoneMinHeight", factory.redstoneMinHeight);
                factory.redstoneMaxHeight = JsonUtils.getInt(jsonObject, "redstoneMaxHeight", factory.redstoneMaxHeight);
                
                factory.diamondSize = JsonUtils.getInt(jsonObject, "diamondSize", factory.diamondSize);
                factory.diamondCount = JsonUtils.getInt(jsonObject, "diamondCount", factory.diamondCount);
                factory.diamondMinHeight = JsonUtils.getInt(jsonObject, "diamondMinHeight", factory.diamondMinHeight);
                factory.diamondMaxHeight = JsonUtils.getInt(jsonObject, "diamondMaxHeight", factory.diamondMaxHeight);
                
                factory.lapisSize = JsonUtils.getInt(jsonObject, "lapisSize", factory.lapisSize);
                factory.lapisCount = JsonUtils.getInt(jsonObject, "lapisCount", factory.lapisCount);
                factory.lapisCenterHeight = JsonUtils.getInt(jsonObject, "lapisCenterHeight", factory.lapisCenterHeight);
                factory.lapisSpread = JsonUtils.getInt(jsonObject, "lapisSpread", factory.lapisSpread);
                
                factory.emeraldSize = JsonUtils.getInt(jsonObject, NbtTags.EMERALD_SIZE, factory.emeraldSize);
                factory.emeraldCount = JsonUtils.getInt(jsonObject, NbtTags.EMERALD_COUNT, factory.emeraldCount);
                factory.emeraldMinHeight = JsonUtils.getInt(jsonObject, NbtTags.EMERALD_MIN_HEIGHT, factory.emeraldMinHeight);
                factory.emeraldMaxHeight = JsonUtils.getInt(jsonObject, NbtTags.EMERALD_MAX_HEIGHT, factory.emeraldMaxHeight);
                
                factory.quartzSize = JsonUtils.getInt(jsonObject, NbtTags.QUARTZ_SIZE, factory.quartzSize);
                factory.quartzCount = JsonUtils.getInt(jsonObject, NbtTags.QUARTZ_COUNT, factory.quartzCount);
                
                factory.magmaSize = JsonUtils.getInt(jsonObject, NbtTags.MAGMA_SIZE, factory.magmaSize);
                factory.magmaCount = JsonUtils.getInt(jsonObject, NbtTags.MAGMA_COUNT, factory.magmaCount);

                factory.useTallGrass = JsonUtils.getBoolean(jsonObject, NbtTags.USE_TALL_GRASS, factory.useTallGrass);
                factory.useNewFlowers = JsonUtils.getBoolean(jsonObject, NbtTags.USE_NEW_FLOWERS, factory.useNewFlowers);
                factory.useDoublePlants = JsonUtils.getBoolean(jsonObject, NbtTags.USE_DOUBLE_PLANTS, factory.useDoublePlants);
                factory.useLilyPads = JsonUtils.getBoolean(jsonObject, NbtTags.USE_LILY_PADS, factory.useLilyPads);
                factory.useMelons = JsonUtils.getBoolean(jsonObject, NbtTags.USE_MELONS, factory.useMelons);
                factory.useDesertWells = JsonUtils.getBoolean(jsonObject, NbtTags.USE_DESERT_WELLS, factory.useDesertWells);
                factory.useFossils = JsonUtils.getBoolean(jsonObject, NbtTags.USE_FOSSILS, factory.useFossils);
                factory.useSandDisks = JsonUtils.getBoolean(jsonObject, NbtTags.USE_SAND_DISKS, factory.useSandDisks);
                factory.useGravelDisks = JsonUtils.getBoolean(jsonObject, NbtTags.USE_GRAVEL_DISKS, factory.useGravelDisks);
                factory.useClayDisks = JsonUtils.getBoolean(jsonObject, NbtTags.USE_CLAY_DISKS, factory.useClayDisks);
                
                factory.useBirchTrees = JsonUtils.getBoolean(jsonObject, NbtTags.USE_BIRCH_TREES, factory.useBirchTrees);
                factory.usePineTrees = JsonUtils.getBoolean(jsonObject, NbtTags.USE_PINE_TREES, factory.usePineTrees);
                factory.useSwampTrees = JsonUtils.getBoolean(jsonObject, NbtTags.USE_SWAMP_TREES, factory.useSwampTrees);
                factory.useJungleTrees = JsonUtils.getBoolean(jsonObject, NbtTags.USE_JUNGLE_TREES, factory.useJungleTrees);
                factory.useAcaciaTrees = JsonUtils.getBoolean(jsonObject, NbtTags.USE_ACACIA_TREES, factory.useAcaciaTrees);
                
                factory.spawnNewCreatureMobs = JsonUtils.getBoolean(jsonObject, NbtTags.SPAWN_NEW_CREATURE_MOBS, factory.spawnNewCreatureMobs);
                factory.spawnNewMonsterMobs = JsonUtils.getBoolean(jsonObject, NbtTags.SPAWN_NEW_MONSTER_MOBS, factory.spawnNewMonsterMobs);
                factory.spawnWaterMobs = JsonUtils.getBoolean(jsonObject, NbtTags.SPAWN_WATER_MOBS, factory.spawnWaterMobs);
                factory.spawnAmbientMobs = JsonUtils.getBoolean(jsonObject, NbtTags.SPAWN_AMBIENT_MOBS, factory.spawnAmbientMobs);
                factory.spawnWolves = JsonUtils.getBoolean(jsonObject, NbtTags.SPAWN_WOLVES, factory.spawnWolves);

                factory.desertBiomeBase = JsonUtils.getString(jsonObject, NbtTags.DESERT_BIOME_BASE, factory.desertBiomeBase);
                factory.desertBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.DESERT_BIOME_OCEAN, factory.desertBiomeOcean);
                factory.desertBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.DESERT_BIOME_BEACH, factory.desertBiomeBeach);

                factory.forestBiomeBase = JsonUtils.getString(jsonObject, NbtTags.FOREST_BIOME_BASE, factory.forestBiomeBase);
                factory.forestBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.FOREST_BIOME_OCEAN, factory.forestBiomeOcean);
                factory.forestBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.FOREST_BIOME_BEACH, factory.forestBiomeBeach);

                factory.iceDesertBiomeBase = JsonUtils.getString(jsonObject, NbtTags.ICE_DESERT_BIOME_BASE, factory.iceDesertBiomeBase);
                factory.iceDesertBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.ICE_DESERT_BIOME_OCEAN, factory.iceDesertBiomeOcean);
                factory.iceDesertBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.ICE_DESERT_BIOME_BEACH, factory.iceDesertBiomeBeach);

                factory.plainsBiomeBase = JsonUtils.getString(jsonObject, NbtTags.PLAINS_BIOME_BASE, factory.plainsBiomeBase);
                factory.plainsBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.PLAINS_BIOME_OCEAN, factory.plainsBiomeOcean);
                factory.plainsBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.PLAINS_BIOME_BEACH, factory.plainsBiomeBeach);

                factory.rainforestBiomeBase = JsonUtils.getString(jsonObject, NbtTags.RAINFOREST_BIOME_BASE, factory.rainforestBiomeBase);
                factory.rainforestBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.RAINFOREST_BIOME_OCEAN, factory.rainforestBiomeOcean);
                factory.rainforestBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.RAINFOREST_BIOME_BEACH, factory.rainforestBiomeBeach);

                factory.savannaBiomeBase = JsonUtils.getString(jsonObject, NbtTags.SAVANNA_BIOME_BASE, factory.savannaBiomeBase);
                factory.savannaBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.SAVANNA_BIOME_OCEAN, factory.savannaBiomeOcean);
                factory.savannaBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.SAVANNA_BIOME_BEACH, factory.savannaBiomeBeach);

                factory.shrublandBiomeBase = JsonUtils.getString(jsonObject, NbtTags.SHRUBLAND_BIOME_BASE, factory.shrublandBiomeBase);
                factory.shrublandBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.SHRUBLAND_BIOME_OCEAN, factory.shrublandBiomeOcean);
                factory.shrublandBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.SHRUBLAND_BIOME_BEACH, factory.shrublandBiomeBeach);

                factory.seasonalForestBiomeBase = JsonUtils.getString(jsonObject, NbtTags.SEASONAL_FOREST_BIOME_BASE, factory.seasonalForestBiomeBase);
                factory.seasonalForestBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.SEASONAL_FOREST_BIOME_OCEAN, factory.seasonalForestBiomeOcean);
                factory.seasonalForestBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.SEASONAL_FOREST_BIOME_BEACH, factory.seasonalForestBiomeBeach);

                factory.swamplandBiomeBase = JsonUtils.getString(jsonObject, NbtTags.SWAMPLAND_BIOME_BASE, factory.swamplandBiomeBase);
                factory.swamplandBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.SWAMPLAND_BIOME_OCEAN, factory.swamplandBiomeOcean);
                factory.swamplandBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.SWAMPLAND_BIOME_BEACH, factory.swamplandBiomeBeach);

                factory.taigaBiomeBase = JsonUtils.getString(jsonObject, NbtTags.TAIGA_BIOME_BASE, factory.taigaBiomeBase);
                factory.taigaBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.TAIGA_BIOME_OCEAN, factory.taigaBiomeOcean);
                factory.taigaBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.TAIGA_BIOME_BEACH, factory.taigaBiomeBeach);

                factory.tundraBiomeBase = JsonUtils.getString(jsonObject, NbtTags.TUNDRA_BIOME_BASE, factory.tundraBiomeBase);
                factory.tundraBiomeOcean = JsonUtils.getString(jsonObject, NbtTags.TUNDRA_BIOME_OCEAN, factory.tundraBiomeOcean);
                factory.tundraBiomeBeach = JsonUtils.getString(jsonObject, NbtTags.TUNDRA_BIOME_BEACH, factory.tundraBiomeBeach);
                
                /* Compatibility with Vanilla settings */
                factory.caveCarver = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_CAVES, true) ? factory.caveCarver : ModernBetaBuiltInTypes.Carver.NONE.getRegistryString();
                factory.defaultFluid = JsonUtils.getBoolean(jsonObject, NbtTags.DEPR_USE_LAVA_OCEANS, false) ? Blocks.LAVA.getRegistryName().toString() : factory.defaultFluid;
                
                /* Clamp values */
                
                factory.coordinateScale = MathHelper.clamp(factory.coordinateScale, MIN_COORD_SCALE, MAX_COORD_SCALE);
                factory.heightScale = MathHelper.clamp(factory.heightScale, MIN_HEIGHT_SCALE, MAX_HEIGHT_SCALE);
                factory.lowerLimitScale = MathHelper.clamp(factory.lowerLimitScale, MIN_LIMIT_SCALE, MAX_LIMIT_SCALE);
                factory.upperLimitScale = MathHelper.clamp(factory.upperLimitScale, MIN_LIMIT_SCALE, MAX_LIMIT_SCALE);
                factory.scaleNoiseScaleX = MathHelper.clamp(factory.scaleNoiseScaleX, MIN_SCALE_NOISE, MAX_SCALE_NOISE);
                factory.scaleNoiseScaleZ = MathHelper.clamp(factory.scaleNoiseScaleZ, MIN_SCALE_NOISE, MAX_SCALE_NOISE);
                factory.depthNoiseScaleX = MathHelper.clamp(factory.depthNoiseScaleX, MIN_DEPTH_NOISE, MAX_DEPTH_NOISE);
                factory.depthNoiseScaleZ = MathHelper.clamp(factory.depthNoiseScaleZ, MIN_DEPTH_NOISE, MAX_DEPTH_NOISE);
                factory.mainNoiseScaleX = MathHelper.clamp(factory.mainNoiseScaleX, MIN_MAIN_NOISE, MAX_MAIN_NOISE);
                factory.mainNoiseScaleY = MathHelper.clamp(factory.mainNoiseScaleY, MIN_MAIN_NOISE, MAX_MAIN_NOISE);
                factory.mainNoiseScaleZ = MathHelper.clamp(factory.mainNoiseScaleZ, MIN_MAIN_NOISE, MAX_MAIN_NOISE);
                factory.baseSize = MathHelper.clamp(factory.baseSize, MIN_BASE_SIZE, MAX_BASE_SIZE);
                factory.stretchY = MathHelper.clamp(factory.stretchY, MIN_STRETCH_Y, MAX_STRETCH_Y);
                factory.seaLevel = MathHelper.clamp(factory.seaLevel, MIN_SEA_LEVEL, MAX_SEA_LEVEL);
                factory.height = MathHelper.clamp(factory.height, MIN_HEIGHT, MAX_HEIGHT);

                factory.tempNoiseScale = MathHelper.clamp(factory.tempNoiseScale, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                factory.rainNoiseScale = MathHelper.clamp(factory.rainNoiseScale, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                factory.detailNoiseScale = MathHelper.clamp(factory.detailNoiseScale, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                
                factory.biomeDepthWeight = MathHelper.clamp(factory.biomeDepthWeight, MIN_BIOME_WEIGHT, MAX_BIOME_WEIGHT);
                factory.biomeDepthOffset = MathHelper.clamp(factory.biomeDepthOffset, MIN_BIOME_OFFSET, MAX_BIOME_OFFSET);
                factory.biomeScaleWeight = MathHelper.clamp(factory.biomeScaleWeight, MIN_BIOME_WEIGHT, MAX_BIOME_WEIGHT);
                factory.biomeScaleOffset = MathHelper.clamp(factory.biomeScaleOffset, MIN_BIOME_OFFSET, MAX_BIOME_OFFSET);

                factory.biomeSize = MathHelper.clamp(factory.biomeSize, MIN_BIOME_SIZE, MAX_BIOME_SIZE);
                factory.riverSize = MathHelper.clamp(factory.riverSize, MIN_RIVER_SIZE, MAX_RIVER_SIZE);
                factory.layerSize = MathHelper.clamp(factory.layerSize, MIN_BIOME_SIZE, MAX_BIOME_SIZE);
                factory.snowyBiomeChance = MathHelper.clamp(factory.snowyBiomeChance, MIN_SNOWY_BIOME_CHANCE, MAX_SNOWY_BIOME_CHANCE);
                
                factory.endIslandOffset = MathHelper.clamp(factory.endIslandOffset, MIN_END_OFFSET, MAX_END_OFFSET);
                factory.endIslandWeight = MathHelper.clamp(factory.endIslandWeight, MIN_END_WEIGHT, MAX_END_WEIGHT);
                factory.endOuterIslandOffset = MathHelper.clamp(factory.endOuterIslandOffset, MIN_END_OFFSET, MAX_END_OFFSET);
                factory.endOuterIslandDistance = MathHelper.clamp(factory.endOuterIslandDistance, MIN_END_DIST, MAX_END_DIST);
                
                factory.caveWidth = MathHelper.clamp(factory.caveWidth, MIN_CAVE_WIDTH, MAX_CAVE_WIDTH);
                factory.caveHeight = MathHelper.clamp(factory.caveHeight, MIN_CAVE_HEIGHT, MAX_CAVE_HEIGHT);
                factory.caveCount = MathHelper.clamp(factory.caveCount, MIN_CAVE_COUNT, MAX_CAVE_COUNT);
                factory.caveChance = MathHelper.clamp(factory.caveChance, MIN_CAVE_CHANCE, MAX_CAVE_CHANCE);
                
                factory.dungeonChance = MathHelper.clamp(factory.dungeonChance, MIN_DUNGEON_CHANCE, MAX_DUNGEON_CHANCE);
                factory.waterLakeChance = MathHelper.clamp(factory.waterLakeChance, MIN_WATER_LAKE_CHANCE, MAX_WATER_LAKE_CHANCE);
                factory.lavaLakeChance = MathHelper.clamp(factory.lavaLakeChance, MIN_LAVA_LAKE_CHANCE, MAX_LAVA_LAKE_CHANCE);

                factory.levelWidth = MathHelper.clamp(factory.levelWidth, LEVEL_WIDTHS[0], LEVEL_WIDTHS[LEVEL_WIDTHS.length - 1]);
                factory.levelLength = MathHelper.clamp(factory.levelLength, LEVEL_WIDTHS[0], LEVEL_WIDTHS[LEVEL_WIDTHS.length - 1]);
                factory.levelHeight = MathHelper.clamp(factory.levelHeight, LEVEL_HEIGHTS[0], LEVEL_HEIGHTS[LEVEL_HEIGHTS.length - 1]);
                factory.levelCaveWidth = MathHelper.clamp(factory.levelCaveWidth, MIN_LEVEL_CAVE_WIDTH, MAX_LEVEL_CAVE_WIDTH);
                
                factory.claySize = MathHelper.clamp(factory.claySize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.clayCount = MathHelper.clamp(factory.clayCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.clayMinHeight = MathHelper.clamp(factory.clayMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.clayMaxHeight = MathHelper.clamp(factory.clayMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.dirtSize = MathHelper.clamp(factory.dirtSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.dirtCount = MathHelper.clamp(factory.dirtCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.dirtMinHeight = MathHelper.clamp(factory.dirtMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.dirtMaxHeight = MathHelper.clamp(factory.dirtMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.gravelSize = MathHelper.clamp(factory.gravelSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.gravelCount = MathHelper.clamp(factory.gravelCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.gravelMinHeight = MathHelper.clamp(factory.gravelMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.gravelMaxHeight = MathHelper.clamp(factory.gravelMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.graniteSize = MathHelper.clamp(factory.graniteSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.graniteCount = MathHelper.clamp(factory.graniteCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.graniteMinHeight = MathHelper.clamp(factory.graniteMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.graniteMaxHeight = MathHelper.clamp(factory.graniteMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.dioriteSize = MathHelper.clamp(factory.dioriteSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.dioriteCount = MathHelper.clamp(factory.dioriteCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.dioriteMinHeight = MathHelper.clamp(factory.dioriteMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.dioriteMaxHeight = MathHelper.clamp(factory.dioriteMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.andesiteSize = MathHelper.clamp(factory.andesiteSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.andesiteCount = MathHelper.clamp(factory.andesiteCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.andesiteMinHeight = MathHelper.clamp(factory.andesiteMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.andesiteMaxHeight = MathHelper.clamp(factory.andesiteMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.coalSize = MathHelper.clamp(factory.coalSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.coalCount = MathHelper.clamp(factory.coalCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.coalMinHeight = MathHelper.clamp(factory.coalMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.coalMaxHeight = MathHelper.clamp(factory.coalMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.ironSize = MathHelper.clamp(factory.ironSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.ironCount = MathHelper.clamp(factory.ironCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.ironMinHeight = MathHelper.clamp(factory.ironMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.ironMaxHeight = MathHelper.clamp(factory.ironMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.goldSize = MathHelper.clamp(factory.goldSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.goldCount = MathHelper.clamp(factory.goldCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.goldMinHeight = MathHelper.clamp(factory.goldMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.goldMaxHeight = MathHelper.clamp(factory.goldMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.redstoneSize = MathHelper.clamp(factory.redstoneSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.redstoneCount = MathHelper.clamp(factory.redstoneCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.redstoneMinHeight = MathHelper.clamp(factory.redstoneMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.redstoneMaxHeight = MathHelper.clamp(factory.redstoneMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.diamondSize = MathHelper.clamp(factory.diamondSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.diamondCount = MathHelper.clamp(factory.diamondCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.diamondMinHeight = MathHelper.clamp(factory.diamondMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.diamondMaxHeight = MathHelper.clamp(factory.diamondMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.lapisSize = MathHelper.clamp(factory.lapisSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.lapisCount = MathHelper.clamp(factory.lapisCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.lapisCenterHeight = MathHelper.clamp(factory.lapisCenterHeight, MIN_ORE_CENTER, MAX_ORE_CENTER);
                factory.lapisSpread = MathHelper.clamp(factory.lapisSpread, MIN_ORE_SPREAD, MAX_ORE_SPREAD);
                
                factory.emeraldSize = MathHelper.clamp(factory.emeraldSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.emeraldCount = MathHelper.clamp(factory.emeraldCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                factory.emeraldMinHeight = MathHelper.clamp(factory.emeraldMinHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                factory.emeraldMaxHeight = MathHelper.clamp(factory.emeraldMaxHeight, MIN_ORE_HEIGHT, MAX_ORE_HEIGHT);
                
                factory.quartzSize = MathHelper.clamp(factory.quartzSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.quartzCount = MathHelper.clamp(factory.quartzCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                
                factory.magmaSize = MathHelper.clamp(factory.magmaSize, MIN_ORE_SIZE, MAX_ORE_SIZE);
                factory.magmaCount = MathHelper.clamp(factory.magmaCount, MIN_ORE_COUNT, MAX_ORE_COUNT);
                
                ModernBetaRegistries.PROPERTY.getKeys().forEach(key -> {
                    Property<?> property = ModernBetaRegistries.PROPERTY.get(key);
                    property.visitFactory(new ReadFactoryPropertyVisitor(), factory, key, jsonObject);
                });
                
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, "[Modern Beta] Failed to deserialize generator settings!");
                ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
            }
            
            return factory;
        }
        
        @Override
        public JsonElement serialize(Factory factory, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            
            jsonObject.addProperty(NbtTags.CHUNK_SOURCE, factory.chunkSource);
            jsonObject.addProperty(NbtTags.BIOME_SOURCE, factory.biomeSource);
            jsonObject.addProperty(NbtTags.SURFACE_BUILDER, factory.surfaceBuilder);
            jsonObject.addProperty(NbtTags.CAVE_CARVER, factory.caveCarver);
            jsonObject.addProperty(NbtTags.WORLD_SPAWNER, factory.worldSpawner);
            
            jsonObject.addProperty(NbtTags.SINGLE_BIOME, factory.singleBiome);
            jsonObject.addProperty(NbtTags.DEFAULT_BLOCK, factory.defaultBlock);
            jsonObject.addProperty(NbtTags.DEFAULT_FLUID, factory.defaultFluid);

            jsonObject.addProperty(NbtTags.REPLACE_OCEAN_BIOMES, factory.replaceOceanBiomes);
            jsonObject.addProperty(NbtTags.REPLACE_BEACH_BIOMES, factory.replaceBeachBiomes);
            jsonObject.addProperty(NbtTags.REPLACE_RIVER_BIOMES, factory.replaceRiverBiomes);
            
            jsonObject.addProperty(NbtTags.COORDINATE_SCALE, factory.coordinateScale);
            jsonObject.addProperty(NbtTags.HEIGHT_SCALE, factory.heightScale);
            jsonObject.addProperty(NbtTags.LOWER_LIMIT_SCALE, factory.lowerLimitScale);
            jsonObject.addProperty(NbtTags.UPPER_LIMIT_SCALE, factory.upperLimitScale);
            jsonObject.addProperty(NbtTags.SCALE_NOISE_SCALE_X, factory.scaleNoiseScaleX);
            jsonObject.addProperty(NbtTags.SCALE_NOISE_SCALE_Z, factory.scaleNoiseScaleZ);
            jsonObject.addProperty(NbtTags.DEPTH_NOISE_SCALE_X, factory.depthNoiseScaleX);
            jsonObject.addProperty(NbtTags.DEPTH_NOISE_SCALE_Z, factory.depthNoiseScaleZ);
            jsonObject.addProperty(NbtTags.MAIN_NOISE_SCALE_X, factory.mainNoiseScaleX);
            jsonObject.addProperty(NbtTags.MAIN_NOISE_SCALE_Y, factory.mainNoiseScaleY);
            jsonObject.addProperty(NbtTags.MAIN_NOISE_SCALE_Z, factory.mainNoiseScaleZ);
            jsonObject.addProperty(NbtTags.BASE_SIZE, factory.baseSize);
            jsonObject.addProperty(NbtTags.STRETCH_Y, factory.stretchY);
            jsonObject.addProperty(NbtTags.SEA_LEVEL, factory.seaLevel);
            jsonObject.addProperty(NbtTags.HEIGHT, factory.height);
            
            jsonObject.addProperty(NbtTags.TEMP_NOISE_SCALE, factory.tempNoiseScale);
            jsonObject.addProperty(NbtTags.RAIN_NOISE_SCALE, factory.rainNoiseScale);
            jsonObject.addProperty(NbtTags.DETAIL_NOISE_SCALE, factory.detailNoiseScale);

            jsonObject.addProperty(NbtTags.BIOME_DEPTH_WEIGHT, factory.biomeDepthWeight);
            jsonObject.addProperty(NbtTags.BIOME_DEPTH_OFFSET, factory.biomeDepthOffset);
            jsonObject.addProperty(NbtTags.BIOME_SCALE_WEIGHT, factory.biomeScaleWeight);
            jsonObject.addProperty(NbtTags.BIOME_SCALE_OFFSET, factory.biomeScaleOffset);
            jsonObject.addProperty(NbtTags.USE_BIOME_DEPTH_SCALE, factory.useBiomeDepthScale);
            jsonObject.addProperty(NbtTags.BIOME_SIZE, factory.biomeSize);
            jsonObject.addProperty(NbtTags.RIVER_SIZE, factory.riverSize);
            jsonObject.addProperty(NbtTags.LAYER_TYPE, factory.layerType);
            jsonObject.addProperty(NbtTags.LAYER_SIZE, factory.layerSize);
            jsonObject.addProperty(NbtTags.LAYER_VERSION, factory.layerVersion);
            jsonObject.addProperty(NbtTags.SNOWY_BIOME_CHANCE, factory.snowyBiomeChance);

            jsonObject.addProperty(NbtTags.END_ISLAND_OFFSET, factory.endIslandOffset);
            jsonObject.addProperty(NbtTags.END_ISLAND_WEIGHT, factory.endIslandWeight);
            jsonObject.addProperty(NbtTags.END_OUTER_ISLAND_OFFSET, factory.endOuterIslandOffset);
            jsonObject.addProperty(NbtTags.END_OUTER_ISLAND_DISTANCE, factory.endOuterIslandDistance);
            jsonObject.addProperty(NbtTags.USE_END_OUTER_ISLANDS, factory.useEndOuterIslands);

            jsonObject.addProperty(NbtTags.CAVE_WIDTH, factory.caveWidth);
            jsonObject.addProperty(NbtTags.CAVE_HEIGHT, factory.caveHeight);
            jsonObject.addProperty(NbtTags.CAVE_COUNT, factory.caveCount);
            jsonObject.addProperty(NbtTags.CAVE_CHANCE, factory.caveChance);
            jsonObject.addProperty(NbtTags.USE_DUNGEONS, factory.useDungeons);
            jsonObject.addProperty(NbtTags.DUNGEON_CHANCE, factory.dungeonChance);
            
            jsonObject.addProperty(NbtTags.USE_STRONGHOLDS, factory.useStrongholds);
            jsonObject.addProperty(NbtTags.USE_VILLAGES, factory.useVillages);
            jsonObject.addProperty(NbtTags.USE_VILLAGE_VARIANTS, factory.useVillageVariants);
            jsonObject.addProperty(NbtTags.USE_MINESHAFTS, factory.useMineShafts);
            jsonObject.addProperty(NbtTags.USE_TEMPLES, factory.useTemples);
            jsonObject.addProperty(NbtTags.USE_MONUMENTS, factory.useMonuments);
            jsonObject.addProperty(NbtTags.USE_MANSIONS, factory.useMansions);
            jsonObject.addProperty(NbtTags.USE_RAVINES, factory.useRavines);
            jsonObject.addProperty(NbtTags.USE_UNDERWATER_CAVES, factory.useUnderwaterCaves);
            
            jsonObject.addProperty(NbtTags.USE_WATER_LAKES, factory.useWaterLakes);
            jsonObject.addProperty(NbtTags.WATER_LAKE_CHANCE, factory.waterLakeChance);
            jsonObject.addProperty(NbtTags.USE_LAVA_LAKES, factory.useLavaLakes);
            jsonObject.addProperty(NbtTags.LAVA_LAKE_CHANCE, factory.lavaLakeChance);
            
            jsonObject.addProperty(NbtTags.USE_SANDSTONE, factory.useSandstone);
            
            jsonObject.addProperty(NbtTags.USE_OLD_NETHER, factory.useOldNether);
            jsonObject.addProperty(NbtTags.USE_NETHER_CAVES, factory.useNetherCaves);
            jsonObject.addProperty(NbtTags.USE_FORTRESSES, factory.useFortresses);
            jsonObject.addProperty(NbtTags.USE_LAVA_POCKETS, factory.useLavaPockets);
            
            jsonObject.addProperty(NbtTags.USE_INFDEV_WALLS, factory.useInfdevWalls);
            jsonObject.addProperty(NbtTags.USE_INFDEV_PYRAMIDS, factory.useInfdevPyramids);
            
            jsonObject.addProperty(NbtTags.LEVEL_THEME, factory.levelTheme);
            jsonObject.addProperty(NbtTags.LEVEL_TYPE, factory.levelType);
            jsonObject.addProperty(NbtTags.LEVEL_WIDTH, factory.levelWidth);
            jsonObject.addProperty(NbtTags.LEVEL_LENGTH, factory.levelLength);
            jsonObject.addProperty(NbtTags.LEVEL_HEIGHT, factory.levelHeight);
            jsonObject.addProperty(NbtTags.LEVEL_HOUSE, factory.levelHouse);
            jsonObject.addProperty(NbtTags.USE_INDEV_CAVES, factory.useIndevCaves);
            jsonObject.addProperty(NbtTags.LEVEL_CAVE_WIDTH, factory.levelCaveWidth);
            
            jsonObject.addProperty(NbtTags.CLAY_SIZE, factory.claySize);
            jsonObject.addProperty(NbtTags.CLAY_COUNT, factory.clayCount);
            jsonObject.addProperty(NbtTags.CLAY_MIN_HEIGHT, factory.clayMinHeight);
            jsonObject.addProperty(NbtTags.CLAY_MAX_HEIGHT, factory.clayMaxHeight);
            
            jsonObject.addProperty("dirtSize", factory.dirtSize);
            jsonObject.addProperty("dirtCount", factory.dirtCount);
            jsonObject.addProperty("dirtMinHeight", factory.dirtMinHeight);
            jsonObject.addProperty("dirtMaxHeight", factory.dirtMaxHeight);
            
            jsonObject.addProperty("gravelSize", factory.gravelSize);
            jsonObject.addProperty("gravelCount", factory.gravelCount);
            jsonObject.addProperty("gravelMinHeight", factory.gravelMinHeight);
            jsonObject.addProperty("gravelMaxHeight", factory.gravelMaxHeight);
            
            jsonObject.addProperty("graniteSize", factory.graniteSize);
            jsonObject.addProperty("graniteCount", factory.graniteCount);
            jsonObject.addProperty("graniteMinHeight", factory.graniteMinHeight);
            jsonObject.addProperty("graniteMaxHeight", factory.graniteMaxHeight);
            
            jsonObject.addProperty("dioriteSize", factory.dioriteSize);
            jsonObject.addProperty("dioriteCount", factory.dioriteCount);
            jsonObject.addProperty("dioriteMinHeight", factory.dioriteMinHeight);
            jsonObject.addProperty("dioriteMaxHeight", factory.dioriteMaxHeight);
            
            jsonObject.addProperty("andesiteSize", factory.andesiteSize);
            jsonObject.addProperty("andesiteCount", factory.andesiteCount);
            jsonObject.addProperty("andesiteMinHeight", factory.andesiteMinHeight);
            jsonObject.addProperty("andesiteMaxHeight", factory.andesiteMaxHeight);
            
            jsonObject.addProperty("coalSize", factory.coalSize);
            jsonObject.addProperty("coalCount", factory.coalCount);
            jsonObject.addProperty("coalMinHeight", factory.coalMinHeight);
            jsonObject.addProperty("coalMaxHeight", factory.coalMaxHeight);
            
            jsonObject.addProperty("ironSize", factory.ironSize);
            jsonObject.addProperty("ironCount", factory.ironCount);
            jsonObject.addProperty("ironMinHeight", factory.ironMinHeight);
            jsonObject.addProperty("ironMaxHeight", factory.ironMaxHeight);
            
            jsonObject.addProperty("goldSize", factory.goldSize);
            jsonObject.addProperty("goldCount", factory.goldCount);
            jsonObject.addProperty("goldMinHeight", factory.goldMinHeight);
            jsonObject.addProperty("goldMaxHeight", factory.goldMaxHeight);
            
            jsonObject.addProperty("redstoneSize", factory.redstoneSize);
            jsonObject.addProperty("redstoneCount", factory.redstoneCount);
            jsonObject.addProperty("redstoneMinHeight", factory.redstoneMinHeight);
            jsonObject.addProperty("redstoneMaxHeight", factory.redstoneMaxHeight);
            
            jsonObject.addProperty("diamondSize", factory.diamondSize);
            jsonObject.addProperty("diamondCount", factory.diamondCount);
            jsonObject.addProperty("diamondMinHeight", factory.diamondMinHeight);
            jsonObject.addProperty("diamondMaxHeight", factory.diamondMaxHeight);
            
            jsonObject.addProperty("lapisSize", factory.lapisSize);
            jsonObject.addProperty("lapisCount", factory.lapisCount);
            jsonObject.addProperty("lapisCenterHeight", factory.lapisCenterHeight);
            jsonObject.addProperty("lapisSpread", factory.lapisSpread);
            
            jsonObject.addProperty(NbtTags.EMERALD_SIZE, factory.emeraldSize);
            jsonObject.addProperty(NbtTags.EMERALD_COUNT, factory.emeraldCount);
            jsonObject.addProperty(NbtTags.EMERALD_MIN_HEIGHT, factory.emeraldMinHeight);
            jsonObject.addProperty(NbtTags.EMERALD_MAX_HEIGHT, factory.emeraldMaxHeight);
            
            jsonObject.addProperty(NbtTags.QUARTZ_SIZE, factory.quartzSize);
            jsonObject.addProperty(NbtTags.QUARTZ_COUNT, factory.quartzCount);
            
            jsonObject.addProperty(NbtTags.MAGMA_SIZE, factory.magmaSize);
            jsonObject.addProperty(NbtTags.MAGMA_COUNT, factory.magmaCount);

            jsonObject.addProperty(NbtTags.USE_TALL_GRASS, factory.useTallGrass);
            jsonObject.addProperty(NbtTags.USE_NEW_FLOWERS, factory.useNewFlowers);
            jsonObject.addProperty(NbtTags.USE_DOUBLE_PLANTS, factory.useDoublePlants);
            jsonObject.addProperty(NbtTags.USE_LILY_PADS, factory.useLilyPads);
            jsonObject.addProperty(NbtTags.USE_MELONS, factory.useMelons);
            jsonObject.addProperty(NbtTags.USE_DESERT_WELLS, factory.useDesertWells);
            jsonObject.addProperty(NbtTags.USE_FOSSILS, factory.useFossils);
            jsonObject.addProperty(NbtTags.USE_SAND_DISKS, factory.useSandDisks);
            jsonObject.addProperty(NbtTags.USE_GRAVEL_DISKS, factory.useGravelDisks);
            jsonObject.addProperty(NbtTags.USE_CLAY_DISKS, factory.useClayDisks);

            jsonObject.addProperty(NbtTags.USE_BIRCH_TREES, factory.useBirchTrees);
            jsonObject.addProperty(NbtTags.USE_PINE_TREES, factory.usePineTrees);
            jsonObject.addProperty(NbtTags.USE_SWAMP_TREES, factory.useSwampTrees);
            jsonObject.addProperty(NbtTags.USE_JUNGLE_TREES, factory.useJungleTrees);
            jsonObject.addProperty(NbtTags.USE_ACACIA_TREES, factory.useAcaciaTrees);

            jsonObject.addProperty(NbtTags.SPAWN_NEW_CREATURE_MOBS, factory.spawnNewCreatureMobs);
            jsonObject.addProperty(NbtTags.SPAWN_NEW_MONSTER_MOBS, factory.spawnNewMonsterMobs);
            jsonObject.addProperty(NbtTags.SPAWN_WATER_MOBS, factory.spawnWaterMobs);
            jsonObject.addProperty(NbtTags.SPAWN_AMBIENT_MOBS, factory.spawnAmbientMobs);
            jsonObject.addProperty(NbtTags.SPAWN_WOLVES, factory.spawnWolves);

            jsonObject.addProperty(NbtTags.DESERT_BIOME_BASE, factory.desertBiomeBase);
            jsonObject.addProperty(NbtTags.DESERT_BIOME_OCEAN, factory.desertBiomeOcean);
            jsonObject.addProperty(NbtTags.DESERT_BIOME_BEACH, factory.desertBiomeBeach);
            
            jsonObject.addProperty(NbtTags.FOREST_BIOME_BASE, factory.forestBiomeBase);
            jsonObject.addProperty(NbtTags.FOREST_BIOME_OCEAN, factory.forestBiomeOcean);
            jsonObject.addProperty(NbtTags.FOREST_BIOME_BEACH, factory.forestBiomeBeach);
            
            jsonObject.addProperty(NbtTags.ICE_DESERT_BIOME_BASE, factory.iceDesertBiomeBase);
            jsonObject.addProperty(NbtTags.ICE_DESERT_BIOME_OCEAN, factory.iceDesertBiomeOcean);
            jsonObject.addProperty(NbtTags.ICE_DESERT_BIOME_BEACH, factory.iceDesertBiomeBeach);
            
            jsonObject.addProperty(NbtTags.PLAINS_BIOME_BASE, factory.plainsBiomeBase);
            jsonObject.addProperty(NbtTags.PLAINS_BIOME_OCEAN, factory.plainsBiomeOcean);
            jsonObject.addProperty(NbtTags.PLAINS_BIOME_BEACH, factory.plainsBiomeBeach);
            
            jsonObject.addProperty(NbtTags.RAINFOREST_BIOME_BASE, factory.rainforestBiomeBase);
            jsonObject.addProperty(NbtTags.RAINFOREST_BIOME_OCEAN, factory.rainforestBiomeOcean);
            jsonObject.addProperty(NbtTags.RAINFOREST_BIOME_BEACH, factory.rainforestBiomeBeach);
            
            jsonObject.addProperty(NbtTags.SAVANNA_BIOME_BASE, factory.savannaBiomeBase);
            jsonObject.addProperty(NbtTags.SAVANNA_BIOME_OCEAN, factory.savannaBiomeOcean);
            jsonObject.addProperty(NbtTags.SAVANNA_BIOME_BEACH, factory.savannaBiomeBeach);
            
            jsonObject.addProperty(NbtTags.SHRUBLAND_BIOME_BASE, factory.shrublandBiomeBase);
            jsonObject.addProperty(NbtTags.SHRUBLAND_BIOME_OCEAN, factory.shrublandBiomeOcean);
            jsonObject.addProperty(NbtTags.SHRUBLAND_BIOME_BEACH, factory.shrublandBiomeBeach);
            
            jsonObject.addProperty(NbtTags.SEASONAL_FOREST_BIOME_BASE, factory.seasonalForestBiomeBase);
            jsonObject.addProperty(NbtTags.SEASONAL_FOREST_BIOME_OCEAN, factory.seasonalForestBiomeOcean);
            jsonObject.addProperty(NbtTags.SEASONAL_FOREST_BIOME_BEACH, factory.seasonalForestBiomeBeach);
            
            jsonObject.addProperty(NbtTags.SWAMPLAND_BIOME_BASE, factory.swamplandBiomeBase);
            jsonObject.addProperty(NbtTags.SWAMPLAND_BIOME_OCEAN, factory.swamplandBiomeOcean);
            jsonObject.addProperty(NbtTags.SWAMPLAND_BIOME_BEACH, factory.swamplandBiomeBeach);
            
            jsonObject.addProperty(NbtTags.TAIGA_BIOME_BASE, factory.taigaBiomeBase);
            jsonObject.addProperty(NbtTags.TAIGA_BIOME_OCEAN, factory.taigaBiomeOcean);
            jsonObject.addProperty(NbtTags.TAIGA_BIOME_BEACH, factory.taigaBiomeBeach);
            
            jsonObject.addProperty(NbtTags.TUNDRA_BIOME_BASE, factory.tundraBiomeBase);
            jsonObject.addProperty(NbtTags.TUNDRA_BIOME_OCEAN, factory.tundraBiomeOcean);
            jsonObject.addProperty(NbtTags.TUNDRA_BIOME_BEACH, factory.tundraBiomeBeach);
            
            factory.customProperties.keySet().forEach(key -> {
                Property<?> property = factory.customProperties.get(key);
                property.visitFactory(new WriteFactoryPropertyVisitor(), factory, key, jsonObject);
            });
            
            return jsonObject;
        }
    }
    
    public static ModernBetaGeneratorSettings build() {
        return new ModernBetaGeneratorSettings.Factory().build();
    }
    
    public static ModernBetaGeneratorSettings build(String generatorSettings) {
        return ModernBetaGeneratorSettings.Factory.jsonToFactory(generatorSettings).build();
    }
    
    public static ModernBetaGeneratorSettings buildOrGet(World world) {
        if (world instanceof WorldServer && ((WorldServer)world).getChunkProvider().chunkGenerator instanceof ModernBetaChunkGenerator) {
            ModernBetaChunkGenerator chunkGenerator = (ModernBetaChunkGenerator)((WorldServer)world).getChunkProvider().chunkGenerator;

            return chunkGenerator.getGeneratorSettings();
        }
        
        return build(world.getWorldInfo().getGeneratorOptions());
    }
    
    private static class NewFactoryPropertyVisitor implements FactoryPropertyVisitor {
        @Override
        public void visit(BooleanProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            factory.customProperties.put(registryKey, new BooleanProperty(property.getValue()));
        }

        @Override
        public void visit(FloatProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            float value = property.getValue();
            float minValue = property.getMinValue();
            float maxValue = property.getMaxValue();
            PropertyGuiType type = property.getGuiType();
            
            factory.customProperties.put(registryKey, new FloatProperty(value, minValue, maxValue, type));
        }

        @Override
        public void visit(IntProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            int value = property.getValue();
            int minValue = property.getMinValue();
            int maxValue = property.getMaxValue();
            PropertyGuiType type = property.getGuiType();
            
            factory.customProperties.put(registryKey, new IntProperty(value, minValue, maxValue, type));
        }

        @Override
        public void visit(StringProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = property.getValue();
            
            factory.customProperties.put(registryKey, new StringProperty(value));
        }

        @Override
        public void visit(ListProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = property.getValue();
            String[] values = property.getValues();
            
            factory.customProperties.put(registryKey, new ListProperty(property.indexOf(value), values));
        }

        @Override
        public void visit(BiomeProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = property.getValue();
            Predicate<ResourceLocation> predicate = property.getFilter();
            
            factory.customProperties.put(registryKey, new BiomeProperty(new ResourceLocation(value), predicate));
        }

        @Override
        public void visit(BlockProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = property.getValue();
            Predicate<ResourceLocation> predicate = property.getFilter();
            
            factory.customProperties.put(registryKey, new BlockProperty(new ResourceLocation(value), predicate));
        }

        @Override
        public void visit(EntityEntryProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = property.getValue();
            Predicate<ResourceLocation> predicate = property.getFilter();
            
            factory.customProperties.put(registryKey, new EntityEntryProperty(new ResourceLocation(value), predicate));
        }

    }
    
    private static class ReadFactoryPropertyVisitor implements FactoryPropertyVisitor {
        @Override
        public void visit(BooleanProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            boolean value = JsonUtils.getBoolean(jsonObject, registryKey.toString(), property.getValue());
            
            factory.customProperties.put(registryKey, new BooleanProperty(value));
        }

        @Override
        public void visit(FloatProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            float value = JsonUtils.getFloat(jsonObject, registryKey.toString(), property.getValue());
            float minValue = property.getMinValue();
            float maxValue = property.getMaxValue();
            PropertyGuiType guiType = property.getGuiType();
            
            factory.customProperties.put(registryKey, new FloatProperty(value, minValue, maxValue, guiType));
        }

        @Override
        public void visit(IntProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            int value = JsonUtils.getInt(jsonObject, registryKey.toString(), property.getValue());
            int minValue = property.getMinValue();
            int maxValue = property.getMaxValue();
            PropertyGuiType guiType = property.getGuiType();

            factory.customProperties.put(registryKey, new IntProperty(value, minValue, maxValue, guiType));
        }

        @Override
        public void visit(StringProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = JsonUtils.getString(jsonObject, registryKey.toString(), property.getValue());
            
            factory.customProperties.put(registryKey, new StringProperty(value));
        }

        @Override
        public void visit(ListProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = JsonUtils.getString(jsonObject, registryKey.toString(), property.getValue());
            
            factory.customProperties.put(registryKey, new ListProperty(property.indexOf(value), property.getValues()));
        }

        @Override
        public void visit(BiomeProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = JsonUtils.getString(jsonObject, registryKey.toString(), property.getValue());
            Predicate<ResourceLocation> predicate = property.getFilter();
            
            factory.customProperties.put(registryKey, new BiomeProperty(new ResourceLocation(value), predicate));
        }

        @Override
        public void visit(BlockProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = JsonUtils.getString(jsonObject, registryKey.toString(), property.getValue());
            Predicate<ResourceLocation> predicate = property.getFilter();
            
            factory.customProperties.put(registryKey, new BlockProperty(new ResourceLocation(value), predicate));
        }

        @Override
        public void visit(EntityEntryProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            String value = JsonUtils.getString(jsonObject, registryKey.toString(), property.getValue());
            Predicate<ResourceLocation> predicate = property.getFilter();
            
            factory.customProperties.put(registryKey, new EntityEntryProperty(new ResourceLocation(value), predicate));
        }

    }
    
    private static class WriteFactoryPropertyVisitor implements FactoryPropertyVisitor {
        @Override
        public void visit(BooleanProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            jsonObject.addProperty(registryKey.toString(), property.getValue());
        }

        @Override
        public void visit(FloatProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            jsonObject.addProperty(registryKey.toString(), property.getValue());
        }

        @Override
        public void visit(IntProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            jsonObject.addProperty(registryKey.toString(), property.getValue());
        }

        @Override
        public void visit(StringProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            jsonObject.addProperty(registryKey.toString(), property.getValue());
        }

        @Override
        public void visit(ListProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            jsonObject.addProperty(registryKey.toString(), property.getValue());
        }

        @Override
        public void visit(BiomeProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            jsonObject.addProperty(registryKey.toString(), property.getValue());
        }

        @Override
        public void visit(BlockProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            jsonObject.addProperty(registryKey.toString(), property.getValue());
        }

        @Override
        public void visit(EntityEntryProperty property, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
            jsonObject.addProperty(registryKey.toString(), property.getValue());
        }
    }
}
