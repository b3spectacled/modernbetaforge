package mod.bespectacled.modernbetaforge.world.chunk;

import java.lang.reflect.Type;

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
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSourceType;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSourceType;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeTags;
import net.minecraft.init.Biomes;
import net.minecraft.util.JsonUtils;

public class ModernBetaChunkGeneratorSettings {
    private static final Gson GSON = new Gson();
    
    public final String chunkSource;
    public final String biomeSource;
    
    public final boolean replaceOceanBiomes;
    public final boolean replaceBeachBiomes;
    
    public final float coordinateScale;
    public final float heightScale;
    public final float upperLimitScale;
    public final float lowerLimitScale;
    public final float depthNoiseScaleX;
    public final float depthNoiseScaleZ;
    public final float depthNoiseScaleExponent;
    public final float mainNoiseScaleX;
    public final float mainNoiseScaleY;
    public final float mainNoiseScaleZ;
    public final float baseSize;
    public final float stretchY;
    public final int seaLevel;
    public final int height;
    
    public final float biomeDepthWeight;
    public final float biomeDepthOffset;
    public final float biomeScaleWeight;
    public final float biomeScaleOffset;
    
    public final boolean useCaves;
    public final boolean useDungeons;
    public final int dungeonChance;
    
    public final boolean useStrongholds;
    public final boolean useVillages;
    public final boolean useMineShafts;
    public final boolean useTemples;
    public final boolean useMonuments;
    public final boolean useMansions;
    public final boolean useRavines;
    
    public final boolean useWaterLakes;
    public final int waterLakeChance;
    public final boolean useLavaLakes;
    public final int lavaLakeChance;
    public final boolean useLavaOceans;
    
    public final String fixedBiome;
    
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

    public final boolean useTallGrass;
    public final boolean useNewFlowers;
    public final boolean useLilyPads;
    public final boolean useMelons;
    public final boolean useDesertWells;
    public final boolean useFossils;
    
    public final float tempNoiseScale;
    public final float rainNoiseScale;
    public final float detailNoiseScale;

    /*
    public final boolean useIslands;
    public final boolean useOuterIslands;
    
    public final float oceanSlideTarget;
    
    public final String centerIslandShape;
    public final int centerIslandRadius;
    public final int centerIslandFalloffDistance;
    
    public final int centerOceanRadius;
    public final int centerOceanFalloffDistance;
    
    public final float outerIslandNoiseScale;
    public final float outerIslandNoiseOffset;
    */
    
    public final ClimateMappingSettings desertBiomes;
    public final ClimateMappingSettings forestBiomes;
    public final ClimateMappingSettings iceDesertBiomes;
    public final ClimateMappingSettings plainsBiomes;
    public final ClimateMappingSettings rainforestBiomes;
    public final ClimateMappingSettings savannaBiomes;
    public final ClimateMappingSettings shrublandBiomes;
    public final ClimateMappingSettings seasonalForestBiomes;
    public final ClimateMappingSettings swamplandBiomes;
    public final ClimateMappingSettings taigaBiomes;
    public final ClimateMappingSettings tundraBiomes;
    
    private ModernBetaChunkGeneratorSettings(Factory factory) {
        this.chunkSource = factory.chunkSource;
        this.biomeSource = factory.biomeSource;
        
        this.replaceOceanBiomes = factory.replaceOceanBiomes;
        this.replaceBeachBiomes = factory.replaceBeachBiomes;
        
        this.coordinateScale = factory.coordinateScale;
        this.heightScale = factory.heightScale;
        this.upperLimitScale = factory.upperLimitScale;
        this.lowerLimitScale = factory.lowerLimitScale;
        this.depthNoiseScaleX = factory.depthNoiseScaleX;
        this.depthNoiseScaleZ = factory.depthNoiseScaleZ;
        this.depthNoiseScaleExponent = factory.depthNoiseScaleExponent;
        this.mainNoiseScaleX = factory.mainNoiseScaleX;
        this.mainNoiseScaleY = factory.mainNoiseScaleY;
        this.mainNoiseScaleZ = factory.mainNoiseScaleZ;
        this.baseSize = factory.baseSize;
        this.stretchY = factory.stretchY;
        this.seaLevel = factory.seaLevel;
        this.height = factory.height;
        
        this.biomeDepthWeight = factory.biomeDepthWeight;
        this.biomeDepthOffset = factory.biomeDepthOffset;
        this.biomeScaleWeight = factory.biomeScaleWeight;
        this.biomeScaleOffset = factory.biomeScaleOffset;
        
        this.useCaves = factory.useCaves;
        this.useDungeons = factory.useDungeons;
        this.dungeonChance = factory.dungeonChance;
        
        this.useStrongholds = factory.useStrongholds;
        this.useVillages = factory.useVillages;
        this.useMineShafts = factory.useMineShafts;
        this.useTemples = factory.useTemples;
        this.useMonuments = factory.useMonuments;
        this.useMansions = factory.useMansions;
        this.useRavines = factory.useRavines;
        
        this.useWaterLakes = factory.useWaterLakes;
        this.waterLakeChance = factory.waterLakeChance;
        this.useLavaLakes = factory.useLavaLakes;
        this.lavaLakeChance = factory.lavaLakeChance;
        this.useLavaOceans = factory.useLavaOceans;
        
        this.fixedBiome = factory.fixedBiome;
        
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

        this.useTallGrass = factory.useTallGrass;
        this.useNewFlowers = factory.useNewFlowers;
        this.useLilyPads = factory.useLilyPads;
        this.useMelons = factory.useMelons;
        this.useDesertWells = factory.useDesertWells;
        this.useFossils = factory.useFossils;
        
        this.tempNoiseScale = factory.tempNoiseScale;
        this.rainNoiseScale = factory.rainNoiseScale;
        this.detailNoiseScale = factory.detailNoiseScale;
        
        /*
        this.useIslands = factory.useIslands;
        this.useOuterIslands = factory.useOuterIslands;
        
        this.oceanSlideTarget = factory.oceanSlideTarget;
        
        this.centerIslandShape = factory.centerIslandShape;
        this.centerIslandRadius = factory.centerIslandRadius;
        this.centerIslandFalloffDistance = factory.centerIslandFalloffDistance;
        
        this.centerOceanRadius = factory.centerOceanRadius;
        this.centerOceanFalloffDistance = factory.centerOceanFalloffDistance;
        
        this.outerIslandNoiseScale = factory.outerIslandNoiseScale;
        this.outerIslandNoiseOffset = factory.outerIslandNoiseOffset;
        */
        
        this.desertBiomes = factory.desertBiomes;
        this.forestBiomes = factory.forestBiomes;
        this.iceDesertBiomes = factory.iceDesertBiomes;
        this.plainsBiomes = factory.plainsBiomes;
        this.rainforestBiomes = factory.rainforestBiomes;
        this.savannaBiomes = factory.savannaBiomes;
        this.shrublandBiomes = factory.shrublandBiomes;
        this.seasonalForestBiomes = factory.seasonalForestBiomes;
        this.swamplandBiomes = factory.swamplandBiomes;
        this.taigaBiomes = factory.taigaBiomes;
        this.tundraBiomes = factory.tundraBiomes;
    }
    
    public static class Factory {
        static final Gson JSON_ADAPTER;
        
        public String chunkSource;
        public String biomeSource;

        public boolean replaceOceanBiomes;
        public boolean replaceBeachBiomes;
        
        public float coordinateScale;
        public float heightScale;
        public float upperLimitScale;
        public float lowerLimitScale;
        public float depthNoiseScaleX;
        public float depthNoiseScaleZ;
        public float depthNoiseScaleExponent;
        public float mainNoiseScaleX;
        public float mainNoiseScaleY;
        public float mainNoiseScaleZ;
        public float baseSize;
        public float stretchY;
        public int seaLevel;
        public int height;
        
        public float biomeDepthWeight;
        public float biomeDepthOffset;
        public float biomeScaleWeight;
        public float biomeScaleOffset;
        
        public boolean useCaves;
        public boolean useDungeons;
        public int dungeonChance;
        
        public boolean useStrongholds;
        public boolean useVillages;
        public boolean useMineShafts;
        public boolean useTemples;
        public boolean useMonuments;
        public boolean useMansions;
        public boolean useRavines;
        
        public boolean useWaterLakes;
        public int waterLakeChance;
        public boolean useLavaLakes;
        public int lavaLakeChance;
        public boolean useLavaOceans;
        
        public String fixedBiome;
        
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
        
        public boolean useTallGrass;
        public boolean useNewFlowers;
        public boolean useLilyPads;
        public boolean useMelons;
        public boolean useDesertWells;
        public boolean useFossils;
        
        public float tempNoiseScale;
        public float rainNoiseScale;
        public float detailNoiseScale;
        
        /*
        public boolean useIslands;
        public boolean useOuterIslands;
        
        public float oceanSlideTarget;
        
        public String centerIslandShape;
        public int centerIslandRadius;
        public int centerIslandFalloffDistance;
        
        public int centerOceanRadius;
        public int centerOceanFalloffDistance;
        
        public float outerIslandNoiseScale;
        public float outerIslandNoiseOffset;
        */
        
        public ClimateMappingSettings desertBiomes;
        public ClimateMappingSettings forestBiomes;
        public ClimateMappingSettings iceDesertBiomes;
        public ClimateMappingSettings plainsBiomes;
        public ClimateMappingSettings rainforestBiomes;
        public ClimateMappingSettings savannaBiomes;
        public ClimateMappingSettings shrublandBiomes;
        public ClimateMappingSettings seasonalForestBiomes;
        public ClimateMappingSettings swamplandBiomes;
        public ClimateMappingSettings taigaBiomes;
        public ClimateMappingSettings tundraBiomes;
        
        public static Factory jsonToFactory(String string) {
            if (string.isEmpty()) {
                return new Factory();
            }
            try {
                return JsonUtils.<Factory>gsonDeserialize(Factory.JSON_ADAPTER, string, Factory.class);
            }
            catch (Exception lvt_1_1_) {
                return new Factory();
            }
        }
        
        @Override
        public String toString() {
            return Factory.JSON_ADAPTER.toJson(this);
        }
        
        public Factory() {
            this.chunkSource = ChunkSourceType.BETA.getId();
            this.biomeSource = BiomeSourceType.BETA.getId();
            
            this.replaceOceanBiomes = true;
            this.replaceBeachBiomes = true;
            
            this.coordinateScale = 684.412f;
            this.heightScale = 684.412f;
            this.upperLimitScale = 512.0f;
            this.lowerLimitScale = 512.0f;
            this.depthNoiseScaleX = 200.0f;
            this.depthNoiseScaleZ = 200.0f;
            this.depthNoiseScaleExponent = 0.5f;
            this.mainNoiseScaleX = 80.0f;
            this.mainNoiseScaleY = 160.0f;
            this.mainNoiseScaleZ = 80.0f;
            this.baseSize = 8.5f;
            this.stretchY = 12.0f;
            this.seaLevel = 64;
            this.height = 128;
            
            this.biomeDepthWeight = 1.0f;
            this.biomeDepthOffset = 0.0f;
            this.biomeScaleWeight = 1.0f;
            this.biomeScaleOffset = 0.0f;
            
            this.useCaves = true;
            this.useDungeons = true;
            this.dungeonChance = 8;
            
            this.useStrongholds = true;
            this.useVillages = true;
            this.useMineShafts = true;
            this.useTemples = true;
            this.useMonuments = true;
            this.useMansions = true;
            this.useRavines = true;
            
            this.useWaterLakes = true;
            this.waterLakeChance = 4;
            this.useLavaLakes = true;
            this.lavaLakeChance = 80;
            
            this.fixedBiome = Biomes.PLAINS.getRegistryName().toString();
            
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
            
            this.useTallGrass = true;
            this.useNewFlowers = true;
            this.useLilyPads = false;
            this.useMelons = true;
            this.useDesertWells = true;
            this.useFossils = true;
            
            this.tempNoiseScale = 1.0f;
            this.rainNoiseScale = 1.0f;
            this.detailNoiseScale = 1.0f;
            
            /*
            this.useIslands = false;
            this.useOuterIslands = false;
            
            this.oceanSlideTarget = -200.0f;
            
            this.centerIslandShape = IslandShape.CIRCLE.getId();
            this.centerIslandRadius = 16;
            this.centerIslandFalloffDistance = 8;
            
            this.centerOceanRadius = 64;
            this.centerOceanFalloffDistance = 16;
            
            this.outerIslandNoiseScale = 300.0f;
            this.outerIslandNoiseOffset = 0.25f;
            */
            
            this.desertBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_DESERT).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_DESERT).toString()
            );
            
            this.forestBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_FOREST).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.iceDesertBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_TUNDRA).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString()
            );
            
            this.plainsBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_PLAINS).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.rainforestBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_RAINFOREST).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.savannaBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SAVANNA).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.shrublandBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SHRUBLAND).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.seasonalForestBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SEASONAL_FOREST).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.swamplandBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SWAMPLAND).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.taigaBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_TAIGA).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString()
            );
            
            this.tundraBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_TUNDRA).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString()
            );
            
            this.setDefaults();
        }
        
        public void setDefaults() {
            this.chunkSource = ChunkSourceType.BETA.getId();
            this.biomeSource = BiomeSourceType.BETA.getId();
            
            this.replaceOceanBiomes = true;
            this.replaceBeachBiomes = true;
            
            this.coordinateScale = 684.412f;
            this.heightScale = 684.412f;
            this.upperLimitScale = 512.0f;
            this.lowerLimitScale = 512.0f;
            this.depthNoiseScaleX = 200.0f;
            this.depthNoiseScaleZ = 200.0f;
            this.depthNoiseScaleExponent = 0.5f;
            this.mainNoiseScaleX = 80.0f;
            this.mainNoiseScaleY = 160.0f;
            this.mainNoiseScaleZ = 80.0f;
            this.baseSize = 8.5f;
            this.stretchY = 12.0f;
            this.seaLevel = 64;
            this.height = 128;
            
            this.biomeDepthWeight = 1.0f;
            this.biomeDepthOffset = 0.0f;
            this.biomeScaleWeight = 1.0f;
            this.biomeScaleOffset = 0.0f;
            
            this.useCaves = true;
            this.useDungeons = true;
            this.dungeonChance = 8;
            
            this.useStrongholds = true;
            this.useVillages = true;
            this.useMineShafts = true;
            this.useTemples = true;
            this.useMonuments = true;
            this.useMansions = true;
            this.useRavines = true;
            
            this.useWaterLakes = true;
            this.waterLakeChance = 4;
            this.useLavaLakes = true;
            this.lavaLakeChance = 80;
            this.useLavaOceans = false;
            
            this.fixedBiome = Biomes.PLAINS.getRegistryName().toString();
            
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
            
            this.useTallGrass = true;
            this.useNewFlowers = true;
            this.useLilyPads = false;
            this.useMelons = true;
            this.useDesertWells = true;
            this.useFossils = true;
            
            this.tempNoiseScale = 1.0f;
            this.rainNoiseScale = 1.0f;
            this.detailNoiseScale = 1.0f;
            
            /*
            this.useIslands = false;
            this.useOuterIslands = false;
            
            this.oceanSlideTarget = -200.0f;
            
            this.centerIslandShape = IslandShape.CIRCLE.getId();
            this.centerIslandRadius = 16;
            this.centerIslandFalloffDistance = 8;
            
            this.centerOceanRadius = 64;
            this.centerOceanFalloffDistance = 16;
            
            this.outerIslandNoiseScale = 300.0f;
            this.outerIslandNoiseOffset = 0.25f;
            */
            
            this.desertBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_DESERT).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_DESERT).toString()
            );
            
            this.forestBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_FOREST).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.iceDesertBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_TUNDRA).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString()
            );
            
            this.plainsBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_PLAINS).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.rainforestBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_RAINFOREST).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.savannaBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SAVANNA).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.shrublandBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SHRUBLAND).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.seasonalForestBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SEASONAL_FOREST).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.swamplandBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SWAMPLAND).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString()
            );
            
            this.taigaBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_TAIGA).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString()
            );
            
            this.tundraBiomes = new ClimateMappingSettings(
                ModernBeta.createId(ModernBetaBiomeTags.BETA_TUNDRA).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString(),
                ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString()
            );
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
                this.andesiteCount == factory.andesiteCount &&
                this.andesiteMaxHeight == factory.andesiteMaxHeight &&
                this.andesiteMinHeight == factory.andesiteMinHeight &&
                this.andesiteSize == factory.andesiteSize &&
                
                Float.compare(factory.baseSize, this.baseSize) == 0 &&
                
                this.coalCount == factory.coalCount &&
                this.coalMaxHeight == factory.coalMaxHeight &&
                this.coalMinHeight == factory.coalMinHeight &&
                this.coalSize == factory.coalSize &&
                
                Float.compare(factory.coordinateScale, this.coordinateScale) == 0 &&
                Float.compare(factory.depthNoiseScaleExponent, this.depthNoiseScaleExponent) == 0 &&
                Float.compare(factory.depthNoiseScaleX, this.depthNoiseScaleX) == 0 &&
                Float.compare(factory.depthNoiseScaleZ, this.depthNoiseScaleZ) == 0 &&
                
                this.diamondCount == factory.diamondCount &&
                this.diamondMaxHeight == factory.diamondMaxHeight &&
                this.diamondMinHeight == factory.diamondMinHeight &&
                this.diamondSize == factory.diamondSize &&
                
                this.dioriteCount == factory.dioriteCount &&
                this.dioriteMaxHeight == factory.dioriteMaxHeight &&
                this.dioriteMinHeight == factory.dioriteMinHeight &&
                this.dioriteSize == factory.dioriteSize &&
                
                this.dirtCount == factory.dirtCount &&
                this.dirtMaxHeight == factory.dirtMaxHeight &&
                this.dirtMinHeight == factory.dirtMinHeight &&
                this.dirtSize == factory.dirtSize &&
                
                this.dungeonChance == factory.dungeonChance &&
                
                this.fixedBiome.equals(factory.fixedBiome) &&
                
                this.goldCount == factory.goldCount &&
                this.goldMaxHeight == factory.goldMaxHeight &&
                this.goldMinHeight == factory.goldMinHeight &&
                this.goldSize == factory.goldSize &&
                
                this.graniteCount == factory.graniteCount &&
                this.graniteMaxHeight == factory.graniteMaxHeight &&
                this.graniteMinHeight == factory.graniteMinHeight &&
                this.graniteSize == factory.graniteSize &&
                
                this.gravelCount == factory.gravelCount &&
                this.gravelMaxHeight == factory.gravelMaxHeight &&
                this.gravelMinHeight == factory.gravelMinHeight &&
                this.gravelSize == factory.gravelSize &&
                
                Float.compare(factory.heightScale, this.heightScale) == 0 &&
                
                this.ironCount == factory.ironCount &&
                this.ironMaxHeight == factory.ironMaxHeight &&
                this.ironMinHeight == factory.ironMinHeight &&
                this.ironSize == factory.ironSize &&
                
                this.lapisCenterHeight == factory.lapisCenterHeight &&
                this.lapisCount == factory.lapisCount &&
                this.lapisSize == factory.lapisSize &&
                this.lapisSpread == factory.lapisSpread &&
                
                this.lavaLakeChance == factory.lavaLakeChance &&
                
                Float.compare(factory.lowerLimitScale, this.lowerLimitScale) == 0 &&
                Float.compare(factory.mainNoiseScaleX, this.mainNoiseScaleX) == 0 &&
                Float.compare(factory.mainNoiseScaleY, this.mainNoiseScaleY) == 0 &&
                Float.compare(factory.mainNoiseScaleZ, this.mainNoiseScaleZ) == 0 &&
                
                this.redstoneCount == factory.redstoneCount &&
                this.redstoneMaxHeight == factory.redstoneMaxHeight &&
                this.redstoneMinHeight == factory.redstoneMinHeight &&
                this.redstoneSize == factory.redstoneSize &&
                
                this.seaLevel == factory.seaLevel &&
                this.height == factory.height &&
                
                Float.compare(factory.stretchY, this.stretchY) == 0 &&
                Float.compare(factory.upperLimitScale, this.upperLimitScale) == 0 &&
                
                Float.compare(factory.biomeDepthWeight, this.biomeDepthWeight) == 0 &&
                Float.compare(factory.biomeDepthOffset, this.biomeDepthOffset) == 0 &&
                Float.compare(factory.biomeScaleWeight, this.biomeScaleWeight) == 0 &&
                Float.compare(factory.biomeScaleOffset, this.biomeScaleOffset) == 0 &&
                
                this.useCaves == factory.useCaves &&
                this.useDungeons == factory.useDungeons &&
                this.useLavaLakes == factory.useLavaLakes &&
                this.useLavaOceans == factory.useLavaOceans &&
                this.useMineShafts == factory.useMineShafts &&
                this.useRavines == factory.useRavines &&
                this.useStrongholds == factory.useStrongholds &&
                this.useTemples == factory.useTemples &&
                this.useMonuments == factory.useMonuments &&
                this.useMansions == factory.useMansions &&
                this.useVillages == factory.useVillages &&
                
                this.useWaterLakes == factory.useWaterLakes &&
                this.waterLakeChance == factory.waterLakeChance &&
                
                this.chunkSource.equals(factory.chunkSource) &&
                this.biomeSource.equals(factory.biomeSource) &&
                
                this.replaceOceanBiomes == factory.replaceOceanBiomes &&
                this.replaceBeachBiomes == factory.replaceBeachBiomes &&
                
                this.claySize == factory.claySize &&
                this.clayMaxHeight == factory.clayMaxHeight &&
                this.clayMinHeight == factory.clayMinHeight &&
                this.clayCount == factory.clayCount &&
                
                this.useTallGrass == factory.useTallGrass &&
                this.useNewFlowers == factory.useNewFlowers &&
                this.useLilyPads == factory.useLilyPads &&
                this.useMelons == factory.useMelons &&
                this.useDesertWells == factory.useDesertWells &&
                this.useFossils == factory.useFossils &&
                
                this.tempNoiseScale == factory.tempNoiseScale &&
                this.rainNoiseScale == factory.rainNoiseScale &&
                this.detailNoiseScale == factory.detailNoiseScale &&
                
                /*
                this.useIslands == factory.useIslands &&
                this.useOuterIslands == factory.useOuterIslands &&
                
                this.oceanSlideTarget == factory.oceanSlideTarget &&
                
                this.centerIslandShape == factory.centerIslandShape &&
                this.centerIslandRadius == factory.centerIslandRadius &&
                this.centerIslandFalloffDistance == factory.centerIslandFalloffDistance &&
                
                this.centerOceanRadius == factory.centerOceanRadius &&
                this.centerOceanFalloffDistance == factory.centerOceanFalloffDistance &&
                
                this.outerIslandNoiseScale == factory.outerIslandNoiseScale &&
                this.outerIslandNoiseOffset == factory.outerIslandNoiseOffset &&
                */
                
                this.desertBiomes.equals(factory.desertBiomes) &&
                this.forestBiomes.equals(factory.forestBiomes) &&
                this.iceDesertBiomes.equals(factory.iceDesertBiomes) &&
                this.plainsBiomes.equals(factory.plainsBiomes) &&
                this.rainforestBiomes.equals(factory.rainforestBiomes) &&
                this.savannaBiomes.equals(factory.savannaBiomes) &&
                this.shrublandBiomes.equals(factory.shrublandBiomes) &&
                this.seasonalForestBiomes.equals(factory.seasonalForestBiomes) &&
                this.swamplandBiomes.equals(factory.swamplandBiomes) &&
                this.taigaBiomes.equals(factory.taigaBiomes) &&
                this.tundraBiomes.equals(factory.tundraBiomes)
                
                ;
        }
        
        @Override
        public int hashCode() {
            int hashCode = (this.coordinateScale == 0.0f) ? 0 : Float.floatToIntBits(this.coordinateScale);
            hashCode = 31 * hashCode + ((this.heightScale == 0.0f) ? 0 : Float.floatToIntBits(this.heightScale));
            hashCode = 31 * hashCode + ((this.upperLimitScale == 0.0f) ? 0 : Float.floatToIntBits(this.upperLimitScale));
            hashCode = 31 * hashCode + ((this.lowerLimitScale == 0.0f) ? 0 : Float.floatToIntBits(this.lowerLimitScale));
            hashCode = 31 * hashCode + ((this.depthNoiseScaleX == 0.0f) ? 0 : Float.floatToIntBits(this.depthNoiseScaleX));
            hashCode = 31 * hashCode + ((this.depthNoiseScaleZ == 0.0f) ? 0 : Float.floatToIntBits(this.depthNoiseScaleZ));
            hashCode = 31 * hashCode + ((this.depthNoiseScaleExponent == 0.0f) ? 0 : Float.floatToIntBits(this.depthNoiseScaleExponent));
            hashCode = 31 * hashCode + ((this.mainNoiseScaleX == 0.0f) ? 0 : Float.floatToIntBits(this.mainNoiseScaleX));
            hashCode = 31 * hashCode + ((this.mainNoiseScaleY == 0.0f) ? 0 : Float.floatToIntBits(this.mainNoiseScaleY));
            hashCode = 31 * hashCode + ((this.mainNoiseScaleZ == 0.0f) ? 0 : Float.floatToIntBits(this.mainNoiseScaleZ));
            hashCode = 31 * hashCode + ((this.baseSize == 0.0f) ? 0 : Float.floatToIntBits(this.baseSize));
            hashCode = 31 * hashCode + ((this.stretchY == 0.0f) ? 0 : Float.floatToIntBits(this.stretchY));
            hashCode = 31 * hashCode + this.seaLevel;
            hashCode = 31 * hashCode + this.height;
            
            hashCode = 31 * hashCode + ((this.biomeDepthWeight == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthWeight));
            hashCode = 31 * hashCode + ((this.biomeDepthOffset == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthOffset));
            hashCode = 31 * hashCode + ((this.biomeScaleWeight == 0.0f) ? 0 : Float.floatToIntBits(this.biomeScaleWeight));
            hashCode = 31 * hashCode + ((this.biomeDepthOffset == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthOffset));
            
            hashCode = 31 * hashCode + (this.useCaves ? 1 : 0);
            hashCode = 31 * hashCode + (this.useDungeons ? 1 : 0);
            hashCode = 31 * hashCode + this.dungeonChance;
            
            hashCode = 31 * hashCode + (this.useStrongholds ? 1 : 0);
            hashCode = 31 * hashCode + (this.useVillages ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMineShafts ? 1 : 0);
            hashCode = 31 * hashCode + (this.useTemples ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMonuments ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMansions ? 1 : 0);
            hashCode = 31 * hashCode + (this.useRavines ? 1 : 0);
            
            hashCode = 31 * hashCode + (this.useWaterLakes ? 1 : 0);
            hashCode = 31 * hashCode + this.waterLakeChance;
            hashCode = 31 * hashCode + (this.useLavaLakes ? 1 : 0);
            hashCode = 31 * hashCode + this.lavaLakeChance;
            hashCode = 31 * hashCode + (this.useLavaOceans ? 1 : 0);
            
            hashCode = 31 * hashCode + this.fixedBiome.hashCode();
            
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
            
            hashCode = 31 * hashCode + (this.replaceOceanBiomes ? 1 : 0);
            hashCode = 31 * hashCode + (this.replaceBeachBiomes ? 1 : 0);
            
            hashCode = 31 * hashCode + this.chunkSource.hashCode();
            hashCode = 31 * hashCode + this.biomeSource.hashCode();

            hashCode = 31 * hashCode + (this.useTallGrass ? 1 : 0);
            hashCode = 31 * hashCode + (this.useNewFlowers ? 1 : 0);
            hashCode = 31 * hashCode + (this.useLilyPads ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMelons ? 1 : 0);
            hashCode = 31 * hashCode + (this.useDesertWells ? 1 : 0);
            hashCode = 31 * hashCode + (this.useFossils ? 1 : 0);
            
            hashCode = 31 * hashCode + ((this.tempNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.tempNoiseScale));
            hashCode = 31 * hashCode + ((this.rainNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.rainNoiseScale));
            hashCode = 31 * hashCode + ((this.detailNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.detailNoiseScale));
            
            /*
            hashCode = 31 * hashCode + (this.useIslands ? 1 : 0);
            hashCode = 31 * hashCode + (this.useOuterIslands ? 1 : 0);

            hashCode = 31 * hashCode + ((this.oceanSlideTarget == 0.0f) ? 0 : Float.floatToIntBits(this.oceanSlideTarget));
            
            hashCode = 31 * hashCode + this.centerIslandShape.hashCode();
            hashCode = 31 * hashCode + this.centerIslandRadius;
            hashCode = 31 * hashCode + this.centerIslandFalloffDistance;
            
            hashCode = 31 * hashCode + this.centerOceanRadius;
            hashCode = 31 * hashCode + this.centerOceanFalloffDistance;

            hashCode = 31 * hashCode + ((this.outerIslandNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.outerIslandNoiseScale));
            hashCode = 31 * hashCode + ((this.outerIslandNoiseOffset == 0.0f) ? 0 : Float.floatToIntBits(this.outerIslandNoiseOffset));
            */
            
            hashCode = 31 * hashCode + this.desertBiomes.hashCode();
            hashCode = 31 * hashCode + this.forestBiomes.hashCode();
            hashCode = 31 * hashCode + this.iceDesertBiomes.hashCode();
            hashCode = 31 * hashCode + this.plainsBiomes.hashCode();
            hashCode = 31 * hashCode + this.rainforestBiomes.hashCode();
            hashCode = 31 * hashCode + this.savannaBiomes.hashCode();
            hashCode = 31 * hashCode + this.shrublandBiomes.hashCode();
            hashCode = 31 * hashCode + this.seasonalForestBiomes.hashCode();
            hashCode = 31 * hashCode + this.swamplandBiomes.hashCode();
            hashCode = 31 * hashCode + this.taigaBiomes.hashCode();
            hashCode = 31 * hashCode + this.tundraBiomes.hashCode();
            
            return hashCode;
        }
        
        public ModernBetaChunkGeneratorSettings build() {
            return new ModernBetaChunkGeneratorSettings(this);
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
                
                factory.replaceOceanBiomes = JsonUtils.getBoolean(jsonObject, NbtTags.REPLACE_OCEAN_BIOMES, factory.replaceOceanBiomes);
                factory.replaceBeachBiomes = JsonUtils.getBoolean(jsonObject, NbtTags.REPLACE_BEACH_BIOMES, factory.replaceBeachBiomes);
                
                factory.coordinateScale = JsonUtils.getFloat(jsonObject, "coordinateScale", factory.coordinateScale);
                factory.heightScale = JsonUtils.getFloat(jsonObject, "heightScale", factory.heightScale);
                factory.lowerLimitScale = JsonUtils.getFloat(jsonObject, "lowerLimitScale", factory.lowerLimitScale);
                factory.upperLimitScale = JsonUtils.getFloat(jsonObject, "upperLimitScale", factory.upperLimitScale);
                factory.depthNoiseScaleX = JsonUtils.getFloat(jsonObject, "depthNoiseScaleX", factory.depthNoiseScaleX);
                factory.depthNoiseScaleZ = JsonUtils.getFloat(jsonObject, "depthNoiseScaleZ", factory.depthNoiseScaleZ);
                factory.depthNoiseScaleExponent = JsonUtils.getFloat(jsonObject, "depthNoiseScaleExponent", factory.depthNoiseScaleExponent);
                factory.mainNoiseScaleX = JsonUtils.getFloat(jsonObject, "mainNoiseScaleX", factory.mainNoiseScaleX);
                factory.mainNoiseScaleY = JsonUtils.getFloat(jsonObject, "mainNoiseScaleY", factory.mainNoiseScaleY);
                factory.mainNoiseScaleZ = JsonUtils.getFloat(jsonObject, "mainNoiseScaleZ", factory.mainNoiseScaleZ);
                factory.baseSize = JsonUtils.getFloat(jsonObject, "baseSize", factory.baseSize);
                factory.stretchY = JsonUtils.getFloat(jsonObject, "stretchY", factory.stretchY);
                factory.seaLevel = JsonUtils.getInt(jsonObject, "seaLevel", factory.seaLevel);
                factory.height = JsonUtils.getInt(jsonObject, NbtTags.HEIGHT, factory.height);
                
                factory.biomeDepthWeight = JsonUtils.getFloat(jsonObject, "biomeDepthWeight", factory.biomeDepthWeight);
                factory.biomeDepthOffset = JsonUtils.getFloat(jsonObject, "biomeDepthOffset", factory.biomeDepthOffset);
                factory.biomeScaleWeight = JsonUtils.getFloat(jsonObject, "biomeScaleWeight", factory.biomeScaleWeight);
                factory.biomeScaleOffset = JsonUtils.getFloat(jsonObject, "biomeScaleOffset", factory.biomeScaleOffset);
                
                factory.useCaves = JsonUtils.getBoolean(jsonObject, "useCaves", factory.useCaves);
                factory.useDungeons = JsonUtils.getBoolean(jsonObject, "useDungeons", factory.useDungeons);
                factory.dungeonChance = JsonUtils.getInt(jsonObject, "dungeonChance", factory.dungeonChance);
                
                factory.useStrongholds = JsonUtils.getBoolean(jsonObject, "useStrongholds", factory.useStrongholds);
                factory.useVillages = JsonUtils.getBoolean(jsonObject, "useVillages", factory.useVillages);
                factory.useMineShafts = JsonUtils.getBoolean(jsonObject, "useMineShafts", factory.useMineShafts);
                factory.useTemples = JsonUtils.getBoolean(jsonObject, "useTemples", factory.useTemples);
                factory.useMonuments = JsonUtils.getBoolean(jsonObject, "useMonuments", factory.useMonuments);
                factory.useMansions = JsonUtils.getBoolean(jsonObject, "useMansions", factory.useMansions);
                factory.useRavines = JsonUtils.getBoolean(jsonObject, "useRavines", factory.useRavines);
                
                factory.useWaterLakes = JsonUtils.getBoolean(jsonObject, "useWaterLakes", factory.useWaterLakes);
                factory.waterLakeChance = JsonUtils.getInt(jsonObject, "waterLakeChance", factory.waterLakeChance);
                factory.useLavaLakes = JsonUtils.getBoolean(jsonObject, "useLavaLakes", factory.useLavaLakes);
                factory.lavaLakeChance = JsonUtils.getInt(jsonObject, "lavaLakeChance", factory.lavaLakeChance);
                factory.useLavaOceans = JsonUtils.getBoolean(jsonObject, "useLavaOceans", factory.useLavaOceans);
                
                factory.fixedBiome = JsonUtils.getString(jsonObject, "fixedBiome", factory.fixedBiome);
                
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

                factory.useTallGrass = JsonUtils.getBoolean(jsonObject, NbtTags.USE_TALL_GRASS, factory.useTallGrass);
                factory.useNewFlowers = JsonUtils.getBoolean(jsonObject, NbtTags.USE_NEW_FLOWERS, factory.useNewFlowers);
                factory.useLilyPads = JsonUtils.getBoolean(jsonObject, NbtTags.USE_LILY_PADS, factory.useLilyPads);
                factory.useMelons = JsonUtils.getBoolean(jsonObject, NbtTags.USE_MELONS, factory.useMelons);
                factory.useDesertWells = JsonUtils.getBoolean(jsonObject, NbtTags.USE_DESERT_WELLS, factory.useDesertWells);
                factory.useFossils = JsonUtils.getBoolean(jsonObject, NbtTags.USE_FOSSILS, factory.useFossils);

                factory.tempNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.TEMP_NOISE_SCALE, factory.tempNoiseScale);
                factory.rainNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.RAIN_NOISE_SCALE, factory.rainNoiseScale);
                factory.detailNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.DETAIL_NOISE_SCALE, factory.detailNoiseScale);
                
                /*
                factory.useIslands = JsonUtils.getBoolean(jsonObject, NbtTags.USE_ISLANDS, factory.useIslands);
                factory.useOuterIslands = JsonUtils.getBoolean(jsonObject, NbtTags.USE_OUTER_ISLANDS, factory.useOuterIslands);

                factory.oceanSlideTarget = JsonUtils.getFloat(jsonObject, NbtTags.OCEAN_SLIDE_TARGET, factory.oceanSlideTarget);
                
                factory.centerIslandShape = JsonUtils.getString(jsonObject, NbtTags.CENTER_ISLAND_SHAPE, factory.centerIslandShape);
                factory.centerIslandRadius = JsonUtils.getInt(jsonObject, NbtTags.CENTER_ISLAND_RADIUS, factory.centerIslandRadius);
                factory.centerIslandFalloffDistance = JsonUtils.getInt(jsonObject, NbtTags.CENTER_ISLAND_FALLOFF_DIST, factory.centerIslandFalloffDistance);

                factory.centerOceanRadius = JsonUtils.getInt(jsonObject, NbtTags.CENTER_OCEAN_RADIUS, factory.centerOceanRadius);
                factory.centerOceanFalloffDistance = JsonUtils.getInt(jsonObject, NbtTags.CENTER_OCEAN_FALLOFF_DIST, factory.centerOceanFalloffDistance);
                
                factory.outerIslandNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.OUTER_ISLAND_NOISE_SCALE, factory.outerIslandNoiseScale);
                factory.outerIslandNoiseOffset = JsonUtils.getFloat(jsonObject, NbtTags.OUTER_ISLAND_NOISE_OFFSET, factory.outerIslandNoiseOffset);  
                */

                factory.desertBiomes = this.deserializeBiomes(jsonObject, NbtTags.DESERT_BIOMES, factory.desertBiomes);
                factory.forestBiomes = this.deserializeBiomes(jsonObject, NbtTags.FOREST_BIOMES, factory.forestBiomes);
                factory.iceDesertBiomes = this.deserializeBiomes(jsonObject, NbtTags.ICE_DESERT_BIOMES, factory.iceDesertBiomes);
                factory.plainsBiomes = this.deserializeBiomes(jsonObject, NbtTags.PLAINS_BIOMES, factory.plainsBiomes);
                factory.rainforestBiomes = this.deserializeBiomes(jsonObject, NbtTags.RAINFOREST_BIOMES, factory.rainforestBiomes);
                factory.savannaBiomes = this.deserializeBiomes(jsonObject, NbtTags.SAVANNA_BIOMES, factory.savannaBiomes);
                factory.shrublandBiomes = this.deserializeBiomes(jsonObject, NbtTags.SHRUBLAND_BIOMES, factory.shrublandBiomes);
                factory.seasonalForestBiomes = this.deserializeBiomes(jsonObject, NbtTags.SEASONAL_FOREST_BIOMES, factory.seasonalForestBiomes);
                factory.swamplandBiomes = this.deserializeBiomes(jsonObject, NbtTags.SWAMPLAND_BIOMES, factory.swamplandBiomes);
                factory.taigaBiomes = this.deserializeBiomes(jsonObject, NbtTags.TAIGA_BIOMES, factory.taigaBiomes);
                factory.tundraBiomes = this.deserializeBiomes(jsonObject, NbtTags.TUNDRA_BIOMES, factory.tundraBiomes);
                
            } catch (Exception e) {}
            
            return factory;
        }
        
        @Override
        public JsonElement serialize(Factory factory, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            
            jsonObject.addProperty(NbtTags.CHUNK_SOURCE, factory.chunkSource);
            jsonObject.addProperty(NbtTags.BIOME_SOURCE, factory.biomeSource);

            jsonObject.addProperty(NbtTags.REPLACE_OCEAN_BIOMES, factory.replaceOceanBiomes);
            jsonObject.addProperty(NbtTags.REPLACE_BEACH_BIOMES, factory.replaceBeachBiomes);
            
            jsonObject.addProperty("coordinateScale", factory.coordinateScale);
            jsonObject.addProperty("heightScale", factory.heightScale);
            jsonObject.addProperty("lowerLimitScale", factory.lowerLimitScale);
            jsonObject.addProperty("upperLimitScale", factory.upperLimitScale);
            jsonObject.addProperty("depthNoiseScaleX", factory.depthNoiseScaleX);
            jsonObject.addProperty("depthNoiseScaleZ", factory.depthNoiseScaleZ);
            jsonObject.addProperty("depthNoiseScaleExponent", factory.depthNoiseScaleExponent);
            jsonObject.addProperty("mainNoiseScaleX", factory.mainNoiseScaleX);
            jsonObject.addProperty("mainNoiseScaleY", factory.mainNoiseScaleY);
            jsonObject.addProperty("mainNoiseScaleZ", factory.mainNoiseScaleZ);
            jsonObject.addProperty("baseSize", factory.baseSize);
            jsonObject.addProperty("stretchY", factory.stretchY);
            jsonObject.addProperty("seaLevel", factory.seaLevel);
            jsonObject.addProperty(NbtTags.HEIGHT, factory.height);

            jsonObject.addProperty("biomeDepthWeight", factory.biomeDepthWeight);
            jsonObject.addProperty("biomeDepthOffset", factory.biomeDepthOffset);
            jsonObject.addProperty("biomeScaleWeight", factory.biomeScaleWeight);
            jsonObject.addProperty("biomeScaleOffset", factory.biomeScaleOffset);
            
            jsonObject.addProperty("useCaves", factory.useCaves);
            jsonObject.addProperty("useDungeons", factory.useDungeons);
            jsonObject.addProperty("dungeonChance", factory.dungeonChance);
            
            jsonObject.addProperty("useStrongholds", factory.useStrongholds);
            jsonObject.addProperty("useVillages", factory.useVillages);
            jsonObject.addProperty("useMineShafts", factory.useMineShafts);
            jsonObject.addProperty("useTemples", factory.useTemples);
            jsonObject.addProperty("useMonuments", factory.useMonuments);
            jsonObject.addProperty("useMansions", factory.useMansions);
            jsonObject.addProperty("useRavines", factory.useRavines);
            
            jsonObject.addProperty("useWaterLakes", factory.useWaterLakes);
            jsonObject.addProperty("waterLakeChance", factory.waterLakeChance);
            jsonObject.addProperty("useLavaLakes", factory.useLavaLakes);
            jsonObject.addProperty("lavaLakeChance", factory.lavaLakeChance);
            jsonObject.addProperty("useLavaOceans", factory.useLavaOceans);
            
            jsonObject.addProperty("fixedBiome", factory.fixedBiome);
            
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

            jsonObject.addProperty(NbtTags.USE_TALL_GRASS, factory.useTallGrass);
            jsonObject.addProperty(NbtTags.USE_NEW_FLOWERS, factory.useNewFlowers);
            jsonObject.addProperty(NbtTags.USE_LILY_PADS, factory.useLilyPads);
            jsonObject.addProperty(NbtTags.USE_MELONS, factory.useMelons);
            jsonObject.addProperty(NbtTags.USE_DESERT_WELLS, factory.useDesertWells);
            jsonObject.addProperty(NbtTags.USE_FOSSILS, factory.useFossils);

            jsonObject.addProperty(NbtTags.TEMP_NOISE_SCALE, factory.tempNoiseScale);
            jsonObject.addProperty(NbtTags.RAIN_NOISE_SCALE, factory.rainNoiseScale);
            jsonObject.addProperty(NbtTags.DETAIL_NOISE_SCALE, factory.detailNoiseScale);

            /*
            jsonObject.addProperty(NbtTags.USE_ISLANDS, factory.useIslands);
            jsonObject.addProperty(NbtTags.USE_OUTER_ISLANDS, factory.useOuterIslands);
            
            jsonObject.addProperty(NbtTags.OCEAN_SLIDE_TARGET, factory.oceanSlideTarget);
            
            jsonObject.addProperty(NbtTags.CENTER_ISLAND_SHAPE, factory.centerIslandShape);
            jsonObject.addProperty(NbtTags.CENTER_ISLAND_RADIUS, factory.centerIslandRadius);
            jsonObject.addProperty(NbtTags.CENTER_ISLAND_FALLOFF_DIST, factory.centerIslandFalloffDistance);

            jsonObject.addProperty(NbtTags.CENTER_OCEAN_RADIUS, factory.centerOceanRadius);
            jsonObject.addProperty(NbtTags.CENTER_OCEAN_FALLOFF_DIST, factory.centerOceanFalloffDistance);

            jsonObject.addProperty(NbtTags.OUTER_ISLAND_NOISE_SCALE, factory.outerIslandNoiseScale);
            jsonObject.addProperty(NbtTags.OUTER_ISLAND_NOISE_OFFSET, factory.outerIslandNoiseOffset);
            */
            
            jsonObject.addProperty(NbtTags.DESERT_BIOMES, GSON.toJson(factory.desertBiomes));
            jsonObject.addProperty(NbtTags.FOREST_BIOMES, GSON.toJson(factory.forestBiomes));
            jsonObject.addProperty(NbtTags.ICE_DESERT_BIOMES, GSON.toJson(factory.iceDesertBiomes));
            jsonObject.addProperty(NbtTags.PLAINS_BIOMES, GSON.toJson(factory.plainsBiomes));
            jsonObject.addProperty(NbtTags.RAINFOREST_BIOMES, GSON.toJson(factory.rainforestBiomes));
            jsonObject.addProperty(NbtTags.SAVANNA_BIOMES, GSON.toJson(factory.savannaBiomes));
            jsonObject.addProperty(NbtTags.SHRUBLAND_BIOMES, GSON.toJson(factory.shrublandBiomes));
            jsonObject.addProperty(NbtTags.SEASONAL_FOREST_BIOMES, GSON.toJson(factory.seasonalForestBiomes));
            jsonObject.addProperty(NbtTags.SWAMPLAND_BIOMES, GSON.toJson(factory.swamplandBiomes));
            jsonObject.addProperty(NbtTags.TAIGA_BIOMES, GSON.toJson(factory.taigaBiomes));
            jsonObject.addProperty(NbtTags.TUNDRA_BIOMES, GSON.toJson(factory.tundraBiomes));
            
            return jsonObject;
        }
        
        private ClimateMappingSettings deserializeBiomes(JsonObject jsonObject, String tag, ClimateMappingSettings fallback) {
            return GSON.fromJson(JsonUtils.getString(jsonObject, tag, GSON.toJson(fallback)), ClimateMappingSettings.class);
        }
    }
    
    public static class ClimateMappingSettings {
        public String landBiome;
        public String oceanBiome;
        public String beachBiome;
        
        public ClimateMappingSettings(String landBiome, String oceanBiome, String beachBiome) {
            this.landBiome = landBiome;
            this.oceanBiome = oceanBiome;
            this.beachBiome = beachBiome;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            
            if (!(o instanceof ClimateMappingSettings)) {
                return false;
            }
            
            ClimateMappingSettings other = (ClimateMappingSettings) o;
            
            return
                this.landBiome.equals(other.landBiome) &&
                this.oceanBiome.equals(other.oceanBiome) &&
                this.beachBiome.equals(other.beachBiome);
        }
    }
}
