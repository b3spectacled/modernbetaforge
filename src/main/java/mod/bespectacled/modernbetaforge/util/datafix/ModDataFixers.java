package mod.bespectacled.modernbetaforge.util.datafix;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.datafix.DataFix;
import mod.bespectacled.modernbetaforge.api.datafix.ModDataFix;
import mod.bespectacled.modernbetaforge.compat.biomesoplenty.CompatBiomesOPlenty;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import net.minecraft.util.ResourceLocation;

public class ModDataFixers {
    private static final int DATA_VERSION_V1_1_0_0 = 1100;
    private static final int DATA_VERSION_V1_2_0_0 = 1200;
    private static final int DATA_VERSION_V1_2_2_2 = 1222;
    private static final int DATA_VERSION_V1_3_0_0 = 1300;
    private static final int DATA_VERSION_V1_3_1_0 = 1310;
    private static final int DATA_VERSION_V1_4_0_0 = 1400;
    private static final int DATA_VERSION_V1_5_0_0 = 1500;
    private static final int DATA_VERSION_V1_5_2_0 = 1520;
    private static final int DATA_VERSION_V1_6_0_0 = 1600;
    private static final int DATA_VERSION_V1_6_1_0 = 1610;
    private static final int DATA_VERSION_V1_7_0_0 = 1700;
    private static final int DATA_VERSION_V1_7_1_0 = 1710;
    private static final int DATA_VERSION_V1_8_1_0 = 1810;
    
    public static final ResourceLocation BIOME_MAP_FIX_KEY = ModernBeta.createRegistryKey("BIOME_MAP_FIX");
    public static final ResourceLocation SANDSTONE_WOLVES_SURFACE_FIX_KEY = ModernBeta.createRegistryKey("SANDSTONE_WOLVES_SURFACE_FIX");
    public static final ResourceLocation SKYLANDS_SURFACE_FIX_KEY = ModernBeta.createRegistryKey("SKYLANDS_SURFACE_FIX");
    public static final ResourceLocation SINGLE_BIOME_FIX_KEY = ModernBeta.createRegistryKey("SINGLE_BIOME_FIX");
    public static final ResourceLocation INDEV_HOUSE_FIX_KEY = ModernBeta.createRegistryKey("INDEV_HOUSE_FIX");
    public static final ResourceLocation RESOURCE_LOCATION_FIX_KEY = ModernBeta.createRegistryKey("RESOURCE_LOCATION_FIX");
    public static final ResourceLocation SCALE_NOISE_FIX_KEY = ModernBeta.createRegistryKey("SCALE_NOISE_FIX");
    public static final ResourceLocation LAYER_SIZE_FIX_KEY = ModernBeta.createRegistryKey("LAYER_SIZE_FIX");
    public static final ResourceLocation CAVE_CARVER_NONE_FIX_KEY = ModernBeta.createRegistryKey("CAVE_CARVER_NONE_FIX");
    public static final ResourceLocation SPAWN_LOCATOR_FIX_KEY = ModernBeta.createRegistryKey("SPAWN_LOCATOR_FIX");
    public static final ResourceLocation DEFAULT_FLUID_FIX_KEY = ModernBeta.createRegistryKey("DEFAULT_FLUID_FIX");
    public static final ResourceLocation DISKS_FIX_KEY = ModernBeta.createRegistryKey("DISKS_FIX");
    public static final ResourceLocation DOUBLE_PLANT_FIX_KEY = ModernBeta.createRegistryKey("DOUBLE_PLANT_FIX");
    public static final ResourceLocation LAYER_VERSION_FIX_KEY = ModernBeta.createRegistryKey("LAYER_VERSION_FIX");
    public static final ResourceLocation RIVER_BIOMES_FIX_KEY = ModernBeta.createRegistryKey("RIVER_BIOMES_FIX");
    public static final ResourceLocation RELEASE_WORLD_SPAWNER_FIX_KEY = ModernBeta.createRegistryKey("RELEASE_WORLD_SPAWNER_FIX");
    
    /*
     * Reference: https://gist.github.com/JoshieGemFinder/982830b6d66fccec04c1d1912ca76246
     * 
     * ModDataFix version should target the mod's data version where data should be fixed.
     * Fixes will run on data processed by an earlier mod data version of the DataFixer,
     * but not by a later mod data version of the DataFixer. 
     * After being processed, the file will be updated to the mod's current data version.
     * 
     * Ex:
     * level.dat/Data/ForgeDataVersion/modernbetaforge value
     * will be set to what the mod data version provided to the DataFixer is. 
     * 
     */
    
    public static final ModDataFix BIOME_MAP_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_1_0_0,
        new DataFix(NbtTags.DESERT_BIOME_BASE, DataFixers::fixDesertBiomeBase),
        new DataFix(NbtTags.DESERT_BIOME_OCEAN, DataFixers::fixDesertBiomeOcean),
        new DataFix(NbtTags.DESERT_BIOME_BEACH, DataFixers::fixDesertBiomeBeach),

        new DataFix(NbtTags.FOREST_BIOME_BASE, DataFixers::fixForestBiomeBase),
        new DataFix(NbtTags.FOREST_BIOME_OCEAN, DataFixers::fixForestBiomeOcean),
        new DataFix(NbtTags.FOREST_BIOME_BEACH, DataFixers::fixForestBiomeBeach),

        new DataFix(NbtTags.ICE_DESERT_BIOME_BASE, DataFixers::fixIceDesertBiomeBase),
        new DataFix(NbtTags.ICE_DESERT_BIOME_OCEAN, DataFixers::fixIceDesertBiomeOcean),
        new DataFix(NbtTags.ICE_DESERT_BIOME_BEACH, DataFixers::fixIceDesertBiomeBeach),

        new DataFix(NbtTags.PLAINS_BIOME_BASE, DataFixers::fixPlainsBiomeBase),
        new DataFix(NbtTags.PLAINS_BIOME_OCEAN, DataFixers::fixPlainsBiomeOcean),
        new DataFix(NbtTags.PLAINS_BIOME_BEACH, DataFixers::fixPlainsBiomeBeach),

        new DataFix(NbtTags.RAINFOREST_BIOME_BASE, DataFixers::fixRainforestBiomeBase),
        new DataFix(NbtTags.RAINFOREST_BIOME_OCEAN, DataFixers::fixRainforestBiomeOcean),
        new DataFix(NbtTags.RAINFOREST_BIOME_BEACH, DataFixers::fixRainforestBiomeBeach),

        new DataFix(NbtTags.SAVANNA_BIOME_BASE, DataFixers::fixSavannaBiomeBase),
        new DataFix(NbtTags.SAVANNA_BIOME_OCEAN, DataFixers::fixSavannaBiomeOcean),
        new DataFix(NbtTags.SAVANNA_BIOME_BEACH, DataFixers::fixSavannaBiomeBeach),

        new DataFix(NbtTags.SHRUBLAND_BIOME_BASE, DataFixers::fixShrublandBiomeBase),
        new DataFix(NbtTags.SHRUBLAND_BIOME_OCEAN, DataFixers::fixShrublandBiomeOcean),
        new DataFix(NbtTags.SHRUBLAND_BIOME_BEACH, DataFixers::fixShrublandBiomeBeach),

        new DataFix(NbtTags.SEASONAL_FOREST_BIOME_BASE, DataFixers::fixSeasonalForestBiomeBase),
        new DataFix(NbtTags.SEASONAL_FOREST_BIOME_OCEAN, DataFixers::fixSeasonalForestBiomeOcean),
        new DataFix(NbtTags.SEASONAL_FOREST_BIOME_BEACH, DataFixers::fixSeasonalForestBiomeBeach),

        new DataFix(NbtTags.SWAMPLAND_BIOME_BASE, DataFixers::fixSwamplandBiomeBase),
        new DataFix(NbtTags.SWAMPLAND_BIOME_OCEAN, DataFixers::fixSwamplandBiomeOcean),
        new DataFix(NbtTags.SWAMPLAND_BIOME_BEACH, DataFixers::fixSwamplandBiomeBeach),

        new DataFix(NbtTags.TAIGA_BIOME_BASE, DataFixers::fixTaigaBiomeBase),
        new DataFix(NbtTags.TAIGA_BIOME_OCEAN, DataFixers::fixTaigaBiomeOcean),
        new DataFix(NbtTags.TAIGA_BIOME_BEACH, DataFixers::fixTaigaBiomeBeach),

        new DataFix(NbtTags.TUNDRA_BIOME_BASE, DataFixers::fixTundraBiomeBase),
        new DataFix(NbtTags.TUNDRA_BIOME_OCEAN, DataFixers::fixTundraBiomeOcean),
        new DataFix(NbtTags.TUNDRA_BIOME_BEACH, DataFixers::fixTundraBiomeBeach)
    );

    public static final ModDataFix SANDSTONE_WOLVES_SURFACE_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_2_0_0,
        new DataFix(NbtTags.USE_SANDSTONE, DataFixers::fixSandstone),
        new DataFix(NbtTags.SPAWN_WOLVES, DataFixers::fixWolves),
        new DataFix(NbtTags.SURFACE_BUILDER, DataFixers::fixSurfaces),
        new DataFix(NbtTags.USE_BIOME_DEPTH_SCALE, DataFixers::fixBiomeDepthScale)
    );
    
    public static final ModDataFix SKYLANDS_SURFACE_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_2_2_2,
        new DataFix(NbtTags.SURFACE_BUILDER, DataFixers::fixSkylandsSurface)
    );
    
    public static final ModDataFix SINGLE_BIOME_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_3_0_0,
        new DataFix(NbtTags.SINGLE_BIOME, DataFixers::fixSingleBiome)
    );
    
    public static final ModDataFix INDEV_HOUSE_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_3_1_0,
        new DataFix(NbtTags.LEVEL_HOUSE, DataFixers::fixIndevHouse)
    );
    
    public static final ModDataFix RESOURCE_LOCATION_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_4_0_0,
        new DataFix(NbtTags.CHUNK_SOURCE, DataFixers::fixResourceLocationChunk),
        new DataFix(NbtTags.BIOME_SOURCE, DataFixers::fixResourceLocationBiome),
        new DataFix(NbtTags.SURFACE_BUILDER, DataFixers::fixResourceLocationSurface),
        new DataFix(NbtTags.CAVE_CARVER, DataFixers::fixResourceLocationCarver)
    );
    
    public static final ModDataFix SCALE_NOISE_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_4_0_0,
        new DataFix(NbtTags.SCALE_NOISE_SCALE_X, DataFixers::fixScaleNoiseScaleX),
        new DataFix(NbtTags.SCALE_NOISE_SCALE_Z, DataFixers::fixScaleNoiseScaleZ)
    );
    
    public static final ModDataFix LAYER_SIZE_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_5_0_0,
        new DataFix(NbtTags.LAYER_SIZE, DataFixers::fixLayerSize)
    );

    public static final ModDataFix CAVE_CARVER_NONE_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_5_2_0,
        new DataFix(NbtTags.CAVE_CARVER, DataFixers::fixCaveCarverNone)
    );

    public static final ModDataFix SPAWN_LOCATOR_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_5_2_0,
        new DataFix(NbtTags.WORLD_SPAWNER, DataFixers::fixWorldSpawner)
    );

    public static final ModDataFix DEFAULT_FLUID_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_6_0_0,
        new DataFix(NbtTags.DEFAULT_FLUID, DataFixers::fixDefaultFluid)
    );
    
    public static final ModDataFix DISKS_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_6_1_0,
        new DataFix(NbtTags.USE_SAND_DISKS, DataFixers::fixSandDisks),
        new DataFix(NbtTags.USE_GRAVEL_DISKS, DataFixers::fixGravelDisks),
        new DataFix(NbtTags.USE_CLAY_DISKS, DataFixers::fixClayDisks)
    );
    
    public static final ModDataFix DOUBLE_PLANT_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_6_1_0,
        new DataFix(NbtTags.USE_DOUBLE_PLANTS, DataFixers::fixDoublePlants)
    );
    
    public static final ModDataFix LAYER_VERSION_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_7_0_0,
        new DataFix(NbtTags.SNOWY_BIOME_CHANCE, DataFixers::fixSnowyBiomeChance),
        new DataFix(NbtTags.LAYER_VERSION, DataFixers::fixLayerVersion1600),
        new DataFix(CompatBiomesOPlenty.KEY_USE_COMPAT.toString(), DataFixers::fixBoPCompat)
    );
    
    public static final ModDataFix RIVER_BIOMES_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_7_1_0,
        new DataFix(NbtTags.REPLACE_RIVER_BIOMES, DataFixers::fixReplaceRiverBiomes)
    );
    
    public static final ModDataFix RELEASE_WORLD_SPAWNER_FIX = ModDataFix.createModDataFix(
        DATA_VERSION_V1_8_1_0,
        new DataFix(NbtTags.WORLD_SPAWNER, DataFixers::fixReleaseWorldSpawner)
    );
}
