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
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeTags;
import net.minecraft.init.Biomes;
import net.minecraft.util.JsonUtils;

public class ModernBetaChunkGeneratorSettings {
    public final String chunkSource;
    public final String biomeSource;
    
    public final String fixedBiome;
    
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
    
    public final float tempNoiseScale;
    public final float rainNoiseScale;
    public final float detailNoiseScale;
    
    public final float biomeDepthWeight;
    public final float biomeDepthOffset;
    public final float biomeScaleWeight;
    public final float biomeScaleOffset;
    public final boolean useBiomeDepthScale;
    
    public final boolean useCaves;
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
    
    public final boolean useWaterLakes;
    public final int waterLakeChance;
    public final boolean useLavaLakes;
    public final int lavaLakeChance;
    public final boolean useLavaOceans;
    
    public final boolean useSandstone;
    
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

    public final boolean useTallGrass;
    public final boolean useNewFlowers;
    public final boolean useLilyPads;
    public final boolean useMelons;
    public final boolean useDesertWells;
    public final boolean useFossils;
    
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
    
    public final String desertBiomeBase;
    public final String desertBiomeOcean;
    public final String desertBiomeBeach;
    
    public final String forestBiomeBase;
    public final String forestBiomeOcean;
    public final String forestBiomeBeach;
    
    public final String iceDesertBiomeBase;
    public final String iceDesertBiomeOcean;
    public final String iceDesertBiomeBeach;
    
    public final String plainsBiomeBase;
    public final String plainsBiomeOcean;
    public final String plainsBiomeBeach;
    
    public final String rainforestBiomeBase;
    public final String rainforestBiomeOcean;
    public final String rainforestBiomeBeach;
    
    public final String savannaBiomeBase;
    public final String savannaBiomeOcean;
    public final String savannaBiomeBeach;
    
    public final String shrublandBiomeBase;
    public final String shrublandBiomeOcean;
    public final String shrublandBiomeBeach;
    
    public final String seasonalForestBiomeBase;
    public final String seasonalForestBiomeOcean;
    public final String seasonalForestBiomeBeach;
    
    public final String swamplandBiomeBase;
    public final String swamplandBiomeOcean;
    public final String swamplandBiomeBeach;
    
    public final String taigaBiomeBase;
    public final String taigaBiomeOcean;
    public final String taigaBiomeBeach;
    
    public final String tundraBiomeBase;
    public final String tundraBiomeOcean;
    public final String tundraBiomeBeach;
    
    private ModernBetaChunkGeneratorSettings(Factory factory) {
        this.chunkSource = factory.chunkSource;
        this.biomeSource = factory.biomeSource;
        
        this.fixedBiome = factory.fixedBiome;
        
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
        
        this.tempNoiseScale = factory.tempNoiseScale;
        this.rainNoiseScale = factory.rainNoiseScale;
        this.detailNoiseScale = factory.detailNoiseScale;
        
        this.biomeDepthWeight = factory.biomeDepthWeight;
        this.biomeDepthOffset = factory.biomeDepthOffset;
        this.biomeScaleWeight = factory.biomeScaleWeight;
        this.biomeScaleOffset = factory.biomeScaleOffset;
        this.useBiomeDepthScale = factory.useBiomeDepthScale;
        
        this.useCaves = factory.useCaves;
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
        
        this.useBirchTrees = factory.useBirchTrees;
        this.usePineTrees = factory.usePineTrees;
        this.useSwampTrees = factory.useSwampTrees;
        this.useJungleTrees = factory.useJungleTrees;
        this.useAcaciaTrees = factory.useAcaciaTrees;
        
        this.useWaterLakes = factory.useWaterLakes;
        this.waterLakeChance = factory.waterLakeChance;
        this.useLavaLakes = factory.useLavaLakes;
        this.lavaLakeChance = factory.lavaLakeChance;
        this.useLavaOceans = factory.useLavaOceans;
        
        this.useSandstone = factory.useSandstone;
        
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

        this.useTallGrass = factory.useTallGrass;
        this.useNewFlowers = factory.useNewFlowers;
        this.useLilyPads = factory.useLilyPads;
        this.useMelons = factory.useMelons;
        this.useDesertWells = factory.useDesertWells;
        this.useFossils = factory.useFossils;
        
        this.spawnNewCreatureMobs = factory.spawnNewCreatureMobs;
        this.spawnNewMonsterMobs = factory.spawnNewMonsterMobs;
        this.spawnWaterMobs = factory.spawnWaterMobs;
        this.spawnAmbientMobs = factory.spawnAmbientMobs;
        this.spawnWolves = factory.spawnWolves;
        
        this.desertBiomeBase = factory.desertBiomeBase;
        this.desertBiomeOcean = factory.desertBiomeOcean;
        this.desertBiomeBeach = factory.desertBiomeBeach;
        
        this.forestBiomeBase = factory.forestBiomeBase;
        this.forestBiomeOcean = factory.forestBiomeOcean;
        this.forestBiomeBeach = factory.forestBiomeBeach;
        
        this.iceDesertBiomeBase = factory.iceDesertBiomeBase;
        this.iceDesertBiomeOcean = factory.iceDesertBiomeOcean;
        this.iceDesertBiomeBeach = factory.iceDesertBiomeBeach;
        
        this.plainsBiomeBase = factory.plainsBiomeBase;
        this.plainsBiomeOcean = factory.plainsBiomeOcean;
        this.plainsBiomeBeach = factory.plainsBiomeBeach;
        
        this.rainforestBiomeBase = factory.rainforestBiomeBase;
        this.rainforestBiomeOcean = factory.rainforestBiomeOcean;
        this.rainforestBiomeBeach = factory.rainforestBiomeBeach;
        
        this.savannaBiomeBase = factory.savannaBiomeBase;
        this.savannaBiomeOcean = factory.savannaBiomeOcean;
        this.savannaBiomeBeach = factory.savannaBiomeBeach;
        
        this.shrublandBiomeBase = factory.shrublandBiomeBase;
        this.shrublandBiomeOcean = factory.shrublandBiomeOcean;
        this.shrublandBiomeBeach = factory.shrublandBiomeBeach;
        
        this.seasonalForestBiomeBase = factory.seasonalForestBiomeBase;
        this.seasonalForestBiomeOcean = factory.seasonalForestBiomeOcean;
        this.seasonalForestBiomeBeach = factory.seasonalForestBiomeBeach;
        
        this.swamplandBiomeBase = factory.swamplandBiomeBase;
        this.swamplandBiomeOcean = factory.swamplandBiomeOcean;
        this.swamplandBiomeBeach = factory.swamplandBiomeBeach;
        
        this.taigaBiomeBase = factory.taigaBiomeBase;
        this.taigaBiomeOcean = factory.taigaBiomeOcean;
        this.taigaBiomeBeach = factory.taigaBiomeBeach;
        
        this.tundraBiomeBase = factory.tundraBiomeBase;
        this.tundraBiomeOcean = factory.tundraBiomeOcean;
        this.tundraBiomeBeach = factory.tundraBiomeBeach;
    }
    
    public static class Factory {
        static final Gson JSON_ADAPTER;
        
        public String chunkSource;
        public String biomeSource;
        
        public String fixedBiome;

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
        
        public float tempNoiseScale;
        public float rainNoiseScale;
        public float detailNoiseScale;
        
        public float biomeDepthWeight;
        public float biomeDepthOffset;
        public float biomeScaleWeight;
        public float biomeScaleOffset;
        public boolean useBiomeDepthScale;
        
        public boolean useCaves;
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
        
        public boolean useWaterLakes;
        public int waterLakeChance;
        public boolean useLavaLakes;
        public int lavaLakeChance;
        public boolean useLavaOceans;
        
        public boolean useSandstone;
        
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
        
        public boolean useTallGrass;
        public boolean useNewFlowers;
        public boolean useLilyPads;
        public boolean useMelons;
        public boolean useDesertWells;
        public boolean useFossils;
        
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
        
        @Override
        public String toString() {
            return Factory.JSON_ADAPTER.toJson(this);
        }
        
        public Factory() {
            this.chunkSource = ModernBetaBuiltInTypes.Chunk.BETA.id;
            this.biomeSource = ModernBetaBuiltInTypes.Biome.BETA.id;
            
            this.fixedBiome = Biomes.PLAINS.getRegistryName().toString();
            
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
            
            this.tempNoiseScale = 1.0f;
            this.rainNoiseScale = 1.0f;
            this.detailNoiseScale = 1.0f;
            
            this.biomeDepthWeight = 1.0f;
            this.biomeDepthOffset = 0.0f;
            this.biomeScaleWeight = 1.0f;
            this.biomeScaleOffset = 0.0f;
            this.useBiomeDepthScale = false;
            
            this.useCaves = true;
            this.useDungeons = true;
            this.dungeonChance = 8;
            
            this.useStrongholds = true;
            this.useVillages = true;
            this.useVillageVariants = false;
            this.useMineShafts = true;
            this.useTemples = true;
            this.useMonuments = true;
            this.useMansions = true;
            this.useRavines = true;
            
            this.useWaterLakes = true;
            this.waterLakeChance = 4;
            this.useLavaLakes = true;
            this.lavaLakeChance = 80;
            
            this.useSandstone = true;
            
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
            
            this.useTallGrass = true;
            this.useNewFlowers = true;
            this.useLilyPads = false;
            this.useMelons = true;
            this.useDesertWells = true;
            this.useFossils = true;
            
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
            
            this.desertBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_DESERT).toString();
            this.desertBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.desertBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_DESERT).toString();
            
            this.forestBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_FOREST).toString();
            this.forestBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.forestBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.iceDesertBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_TUNDRA).toString();
            this.iceDesertBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.iceDesertBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();
            
            this.plainsBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_PLAINS).toString();
            this.plainsBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.plainsBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.rainforestBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_RAINFOREST).toString();
            this.rainforestBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.rainforestBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.savannaBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_SAVANNA).toString();
            this.savannaBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.savannaBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.shrublandBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_SHRUBLAND).toString();
            this.shrublandBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.shrublandBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.seasonalForestBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_SEASONAL_FOREST).toString();
            this.seasonalForestBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.seasonalForestBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.swamplandBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_SWAMPLAND).toString();
            this.swamplandBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.swamplandBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.taigaBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_TAIGA).toString();
            this.taigaBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.taigaBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();
            
            this.tundraBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_TUNDRA).toString();
            this.tundraBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.tundraBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();

            this.setDefaults();
        }
        
        public void setDefaults() {
            this.chunkSource = ModernBetaBuiltInTypes.Chunk.BETA.id;
            this.biomeSource = ModernBetaBuiltInTypes.Biome.BETA.id;
            
            this.fixedBiome = Biomes.PLAINS.getRegistryName().toString();
            
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
            
            this.tempNoiseScale = 1.0f;
            this.rainNoiseScale = 1.0f;
            this.detailNoiseScale = 1.0f;
            
            this.biomeDepthWeight = 1.0f;
            this.biomeDepthOffset = 0.0f;
            this.biomeScaleWeight = 1.0f;
            this.biomeScaleOffset = 0.0f;
            this.useBiomeDepthScale = false;
            
            this.useCaves = true;
            this.useDungeons = true;
            this.dungeonChance = 8;
            
            this.useStrongholds = true;
            this.useVillages = true;
            this.useVillageVariants = false;
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
            
            this.useSandstone = true;
            
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
            
            this.useTallGrass = true;
            this.useNewFlowers = true;
            this.useLilyPads = false;
            this.useMelons = true;
            this.useDesertWells = true;
            this.useFossils = true;
            
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

            this.desertBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_DESERT).toString();
            this.desertBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.desertBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_DESERT).toString();
            
            this.forestBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_FOREST).toString();
            this.forestBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.forestBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.iceDesertBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_TUNDRA).toString();
            this.iceDesertBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.iceDesertBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();
            
            this.plainsBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_PLAINS).toString();
            this.plainsBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.plainsBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.rainforestBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_RAINFOREST).toString();
            this.rainforestBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.rainforestBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.savannaBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_SAVANNA).toString();
            this.savannaBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.savannaBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.shrublandBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_SHRUBLAND).toString();
            this.shrublandBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.shrublandBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.seasonalForestBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_SEASONAL_FOREST).toString();
            this.seasonalForestBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.seasonalForestBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.swamplandBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_SWAMPLAND).toString();
            this.swamplandBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_OCEAN).toString();
            this.swamplandBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_BEACH).toString();
            
            this.taigaBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_TAIGA).toString();
            this.taigaBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.taigaBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();
            
            this.tundraBiomeBase = ModernBeta.createId(ModernBetaBiomeTags.BETA_TUNDRA).toString();
            this.tundraBiomeOcean = ModernBeta.createId(ModernBetaBiomeTags.BETA_FROZEN_OCEAN).toString();
            this.tundraBiomeBeach = ModernBeta.createId(ModernBetaBiomeTags.BETA_SNOWY_BEACH).toString();
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
                
                this.fixedBiome.equals(factory.fixedBiome) &&
                
                this.replaceOceanBiomes == factory.replaceOceanBiomes &&
                this.replaceBeachBiomes == factory.replaceBeachBiomes &&
                        
                Float.compare(factory.coordinateScale, this.coordinateScale) == 0 &&        
                Float.compare(factory.heightScale, this.heightScale) == 0 &&
                Float.compare(factory.upperLimitScale, this.upperLimitScale) == 0 &&
                Float.compare(factory.lowerLimitScale, this.lowerLimitScale) == 0 &&
                Float.compare(factory.depthNoiseScaleX, this.depthNoiseScaleX) == 0 &&
                Float.compare(factory.depthNoiseScaleZ, this.depthNoiseScaleZ) == 0 &&
                Float.compare(factory.depthNoiseScaleExponent, this.depthNoiseScaleExponent) == 0 &&
                Float.compare(factory.mainNoiseScaleX, this.mainNoiseScaleX) == 0 &&
                Float.compare(factory.mainNoiseScaleY, this.mainNoiseScaleY) == 0 &&
                Float.compare(factory.mainNoiseScaleZ, this.mainNoiseScaleZ) == 0 &&
                Float.compare(factory.baseSize, this.baseSize) == 0 &&
                Float.compare(factory.stretchY, this.stretchY) == 0 &&
                
                this.seaLevel == factory.seaLevel &&
                this.height == factory.height &&
                        
                this.tempNoiseScale == factory.tempNoiseScale &&
                this.rainNoiseScale == factory.rainNoiseScale &&
                this.detailNoiseScale == factory.detailNoiseScale &&
                
                Float.compare(factory.biomeDepthWeight, this.biomeDepthWeight) == 0 &&
                Float.compare(factory.biomeDepthOffset, this.biomeDepthOffset) == 0 &&
                Float.compare(factory.biomeScaleWeight, this.biomeScaleWeight) == 0 &&
                Float.compare(factory.biomeScaleOffset, this.biomeScaleOffset) == 0 &&
                this.useBiomeDepthScale == factory.useBiomeDepthScale &&
                
                this.useCaves == factory.useCaves &&
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
                
                this.useWaterLakes == factory.useWaterLakes &&
                this.waterLakeChance == factory.waterLakeChance &&
                this.useLavaLakes == factory.useLavaLakes &&
                this.lavaLakeChance == factory.lavaLakeChance &&
                this.useLavaOceans == factory.useLavaOceans &&
                
                this.useSandstone == factory.useSandstone &&
                
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
                
                this.useTallGrass == factory.useTallGrass &&
                this.useNewFlowers == factory.useNewFlowers &&
                this.useLilyPads == factory.useLilyPads &&
                this.useMelons == factory.useMelons &&
                this.useDesertWells == factory.useDesertWells &&
                this.useFossils == factory.useFossils &&
                        
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
                this.tundraBiomeBeach.equals(factory.tundraBiomeBeach)
                
                ;
        }
        
        @Override
        public int hashCode() {
            int hashCode = this.chunkSource.hashCode();
            hashCode = 31 * hashCode + this.biomeSource.hashCode();
            
            hashCode = 31 * hashCode + this.fixedBiome.hashCode();
            
            hashCode = 31 * hashCode + (this.replaceOceanBiomes ? 1 : 0);
            hashCode = 31 * hashCode + (this.replaceBeachBiomes ? 1 : 0);
            
            hashCode = 31 * hashCode + ((this.coordinateScale == 0.0f) ? 0 : Float.floatToIntBits(this.coordinateScale));
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
            
            hashCode = 31 * hashCode + ((this.tempNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.tempNoiseScale));
            hashCode = 31 * hashCode + ((this.rainNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.rainNoiseScale));
            hashCode = 31 * hashCode + ((this.detailNoiseScale == 0.0f) ? 0 : Float.floatToIntBits(this.detailNoiseScale));
            
            hashCode = 31 * hashCode + ((this.biomeDepthWeight == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthWeight));
            hashCode = 31 * hashCode + ((this.biomeDepthOffset == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthOffset));
            hashCode = 31 * hashCode + ((this.biomeScaleWeight == 0.0f) ? 0 : Float.floatToIntBits(this.biomeScaleWeight));
            hashCode = 31 * hashCode + ((this.biomeDepthOffset == 0.0f) ? 0 : Float.floatToIntBits(this.biomeDepthOffset));
            hashCode = 31 * hashCode + (this.useBiomeDepthScale ? 1 : 0);
            
            hashCode = 31 * hashCode + (this.useCaves ? 1 : 0);
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
            
            hashCode = 31 * hashCode + (this.useWaterLakes ? 1 : 0);
            hashCode = 31 * hashCode + this.waterLakeChance;
            hashCode = 31 * hashCode + (this.useLavaLakes ? 1 : 0);
            hashCode = 31 * hashCode + this.lavaLakeChance;
            hashCode = 31 * hashCode + (this.useLavaOceans ? 1 : 0);
            
            hashCode = 31 * hashCode + (this.useSandstone ? 1 : 0);
            
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

            hashCode = 31 * hashCode + (this.useTallGrass ? 1 : 0);
            hashCode = 31 * hashCode + (this.useNewFlowers ? 1 : 0);
            hashCode = 31 * hashCode + (this.useLilyPads ? 1 : 0);
            hashCode = 31 * hashCode + (this.useMelons ? 1 : 0);
            hashCode = 31 * hashCode + (this.useDesertWells ? 1 : 0);
            hashCode = 31 * hashCode + (this.useFossils ? 1 : 0);

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
                
                factory.fixedBiome = JsonUtils.getString(jsonObject, "fixedBiome", factory.fixedBiome);
                
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

                factory.tempNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.TEMP_NOISE_SCALE, factory.tempNoiseScale);
                factory.rainNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.RAIN_NOISE_SCALE, factory.rainNoiseScale);
                factory.detailNoiseScale = JsonUtils.getFloat(jsonObject, NbtTags.DETAIL_NOISE_SCALE, factory.detailNoiseScale);
                
                factory.biomeDepthWeight = JsonUtils.getFloat(jsonObject, "biomeDepthWeight", factory.biomeDepthWeight);
                factory.biomeDepthOffset = JsonUtils.getFloat(jsonObject, "biomeDepthOffset", factory.biomeDepthOffset);
                factory.biomeScaleWeight = JsonUtils.getFloat(jsonObject, "biomeScaleWeight", factory.biomeScaleWeight);
                factory.biomeScaleOffset = JsonUtils.getFloat(jsonObject, "biomeScaleOffset", factory.biomeScaleOffset);
                factory.useBiomeDepthScale = JsonUtils.getBoolean(jsonObject, NbtTags.USE_BIOME_DEPTH_SCALE, factory.useBiomeDepthScale);
                
                factory.useCaves = JsonUtils.getBoolean(jsonObject, "useCaves", factory.useCaves);
                factory.useDungeons = JsonUtils.getBoolean(jsonObject, "useDungeons", factory.useDungeons);
                factory.dungeonChance = JsonUtils.getInt(jsonObject, "dungeonChance", factory.dungeonChance);
                
                factory.useStrongholds = JsonUtils.getBoolean(jsonObject, "useStrongholds", factory.useStrongholds);
                factory.useVillages = JsonUtils.getBoolean(jsonObject, "useVillages", factory.useVillages);
                factory.useVillageVariants = JsonUtils.getBoolean(jsonObject, NbtTags.USE_VILLAGE_VARIANTS, factory.useVillageVariants);
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
                
                factory.useSandstone = JsonUtils.getBoolean(jsonObject, NbtTags.USE_SANDSTONE, factory.useSandstone);
                
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

                factory.useTallGrass = JsonUtils.getBoolean(jsonObject, NbtTags.USE_TALL_GRASS, factory.useTallGrass);
                factory.useNewFlowers = JsonUtils.getBoolean(jsonObject, NbtTags.USE_NEW_FLOWERS, factory.useNewFlowers);
                factory.useLilyPads = JsonUtils.getBoolean(jsonObject, NbtTags.USE_LILY_PADS, factory.useLilyPads);
                factory.useMelons = JsonUtils.getBoolean(jsonObject, NbtTags.USE_MELONS, factory.useMelons);
                factory.useDesertWells = JsonUtils.getBoolean(jsonObject, NbtTags.USE_DESERT_WELLS, factory.useDesertWells);
                factory.useFossils = JsonUtils.getBoolean(jsonObject, NbtTags.USE_FOSSILS, factory.useFossils);
                
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
                
            } catch (Exception e) {}
            
            return factory;
        }
        
        @Override
        public JsonElement serialize(Factory factory, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            
            jsonObject.addProperty(NbtTags.CHUNK_SOURCE, factory.chunkSource);
            jsonObject.addProperty(NbtTags.BIOME_SOURCE, factory.biomeSource);
            
            jsonObject.addProperty("fixedBiome", factory.fixedBiome);

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
            
            jsonObject.addProperty(NbtTags.TEMP_NOISE_SCALE, factory.tempNoiseScale);
            jsonObject.addProperty(NbtTags.RAIN_NOISE_SCALE, factory.rainNoiseScale);
            jsonObject.addProperty(NbtTags.DETAIL_NOISE_SCALE, factory.detailNoiseScale);

            jsonObject.addProperty("biomeDepthWeight", factory.biomeDepthWeight);
            jsonObject.addProperty("biomeDepthOffset", factory.biomeDepthOffset);
            jsonObject.addProperty("biomeScaleWeight", factory.biomeScaleWeight);
            jsonObject.addProperty("biomeScaleOffset", factory.biomeScaleOffset);
            jsonObject.addProperty(NbtTags.USE_BIOME_DEPTH_SCALE, factory.useBiomeDepthScale);
            
            jsonObject.addProperty("useCaves", factory.useCaves);
            jsonObject.addProperty("useDungeons", factory.useDungeons);
            jsonObject.addProperty("dungeonChance", factory.dungeonChance);
            
            jsonObject.addProperty("useStrongholds", factory.useStrongholds);
            jsonObject.addProperty("useVillages", factory.useVillages);
            jsonObject.addProperty(NbtTags.USE_VILLAGE_VARIANTS, factory.useVillageVariants);
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
            
            jsonObject.addProperty(NbtTags.USE_SANDSTONE, factory.useSandstone);
            
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

            jsonObject.addProperty(NbtTags.USE_TALL_GRASS, factory.useTallGrass);
            jsonObject.addProperty(NbtTags.USE_NEW_FLOWERS, factory.useNewFlowers);
            jsonObject.addProperty(NbtTags.USE_LILY_PADS, factory.useLilyPads);
            jsonObject.addProperty(NbtTags.USE_MELONS, factory.useMelons);
            jsonObject.addProperty(NbtTags.USE_DESERT_WELLS, factory.useDesertWells);
            jsonObject.addProperty(NbtTags.USE_FOSSILS, factory.useFossils);

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
            
            return jsonObject;
        }
    }
    
    public static ModernBetaChunkGeneratorSettings buildSettings(String generatorSettings) {
        return ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(generatorSettings).build();
    }
}
