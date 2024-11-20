package mod.bespectacled.modernbetaforge.client.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings.Factory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class GuiIds {
    private static final MapGenStronghold STRONGHOLD = new MapGenStronghold();
    
    public static final Map<Integer, Predicate<ModernBetaChunkGeneratorSettings.Factory>> GUI_IDS = new HashMap<>();
    
    /* Function Buttons */
    
    public static final int FUNC_DONE = 60;
    public static final int FUNC_RAND = 61;
    public static final int FUNC_PREV = 62;
    public static final int FUNC_NEXT = 63;
    public static final int FUNC_DFLT = 64;
    public static final int FUNC_PRST = 65;
    public static final int FUNC_CONF = 66;
    public static final int FUNC_CNCL = 67;
    
    /* Page 1 */
    
    // Entries
    public static final int PG0_S_CHUNK = 100;
    public static final int PG0_S_BIOME = 101;
    public static final int PG0_S_SURFACE = 102;
    public static final int PG0_S_CARVER = 103;
    
    public static final int PG0_S_FIXED = 104;
    
    public static final int PG0_B_USE_OCEAN = 105;
    public static final int PG0_B_USE_BEACH = 106;
    
    public static final int PG0_S_SEA_LEVEL = 107;
    public static final int PG0_B_USE_CAVES = 108;
    public static final int PG0_B_USE_HOLDS = 109;
    public static final int PG0_B_USE_VILLAGES = 110;
    public static final int PG0_B_USE_VILLAGE_VARIANTS = 111;
    public static final int PG0_B_USE_SHAFTS = 112;
    public static final int PG0_B_USE_TEMPLES = 113;
    public static final int PG0_B_USE_MONUMENTS = 114;
    public static final int PG0_B_USE_MANSIONS = 115;
    public static final int PG0_B_USE_RAVINES = 116;
    public static final int PG0_B_USE_DUNGEONS = 117;
    public static final int PG0_S_DUNGEON_CHANCE = 118;
    public static final int PG0_B_USE_WATER_LAKES = 119;
    public static final int PG0_S_WATER_LAKE_CHANCE = 120;
    public static final int PG0_B_USE_LAVA_LAKES = 121;
    public static final int PG0_S_LAVA_LAKE_CHANCE = 122;
    public static final int PG0_B_USE_LAVA_OCEANS = 123;
    public static final int PG0_B_USE_SANDSTONE = 124;
    
    public static final int PG0_B_USE_OLD_NETHER = 125;
    public static final int PG0_B_USE_NETHER_CAVES = 126;
    public static final int PG0_B_USE_FORTRESSES = 127;
    public static final int PG0_B_USE_LAVA_POCKETS = 128;
    
    public static final int PG0_S_LEVEL_THEME = 129;
    public static final int PG0_S_LEVEL_TYPE = 130;
    public static final int PG0_S_LEVEL_WIDTH = 131;
    public static final int PG0_S_LEVEL_LENGTH = 132;
    public static final int PG0_S_LEVEL_HEIGHT = 133;
    public static final int PG0_B_USE_INDEV_CAVES = 134;
    public static final int PG0_B_USE_INDEV_HOUSE = 135;
    
    public static final int PG0_B_USE_INFDEV_WALLS = 136;
    public static final int PG0_B_USE_INFDEV_PYRAMIDS = 137;
    
    public static final int PG0_L_INDEV_SEA_LEVEL = 150;
    
    // Labels
    public static final int PG0_L_SURFACE_BUILDER = 1000;
    public static final int PG0_L_FIXED_BIOME = 1001;
    public static final int PG0_L_BIOME_REPLACEMENT = 1002;
    public static final int PG0_L_BASIC_FEATURES = 1003;
    public static final int PG0_L_NETHER_FEATURES = 1004;
    public static final int PG0_L_NETHER_BOP = 1005;
    public static final int PG0_L_INDEV_FEATURES = 1006;
    public static final int PG0_L_INFDEV_227_FEATURES = 1007;
    
    /* Page 2 */
    
    public static final int PG1_B_USE_GRASS = 200;
    public static final int PG1_B_USE_FLOWERS = 201;
    public static final int PG1_B_USE_PADS = 202;
    public static final int PG1_B_USE_MELONS = 203;
    
    public static final int PG1_B_USE_WELLS = 204;
    public static final int PG1_B_USE_FOSSILS = 205;
    
    public static final int PG1_B_USE_BIRCH = 206;
    public static final int PG1_B_USE_PINE = 207;
    public static final int PG1_B_USE_SWAMP = 208;
    public static final int PG1_B_USE_JUNGLE = 209;
    public static final int PG1_B_USE_ACACIA = 210;
    
    public static final int PG1_B_SPAWN_CREATURE = 211;
    public static final int PG1_B_SPAWN_MONSTER = 212;
    public static final int PG1_B_SPAWN_WATER = 213;
    public static final int PG1_B_SPAWN_AMBIENT = 214;
    public static final int PG1_B_SPAWN_WOLVES = 215;
    
    public static final int PG1_B_USE_MODDED_BIOMES = 216;
    
    // Labels
    public static final int PG1_L_BETA = 1200;
    public static final int PG1_L_MOBS = 1201;
    public static final int PG1_L_RELEASE = 1202;
    public static final int PG1_L_MODS = 1203;
    
    /* Page 3 */
    
    // Entries
    public static final int PG2_S_CLAY_SIZE = 300;
    public static final int PG2_S_CLAY_CNT = 301;
    public static final int PG2_S_CLAY_MIN = 302;
    public static final int PG2_S_CLAY_MAX = 303;

    public static final int PG2_S_DIRT_SIZE = 304;
    public static final int PG2_S_DIRT_CNT = 305;
    public static final int PG2_S_DIRT_MIN = 306;
    public static final int PG2_S_DIRT_MAX = 307;

    public static final int PG2_S_GRAV_SIZE = 308;
    public static final int PG2_S_GRAV_CNT = 309;
    public static final int PG2_S_GRAV_MIN = 310;
    public static final int PG2_S_GRAV_MAX = 311;

    public static final int PG2_S_GRAN_SIZE = 312;
    public static final int PG2_S_GRAN_CNT = 313;
    public static final int PG2_S_GRAN_MIN = 314;
    public static final int PG2_S_GRAN_MAX = 315;

    public static final int PG2_S_DIOR_SIZE = 316;
    public static final int PG2_S_DIOR_CNT = 317;
    public static final int PG2_S_DIOR_MIN = 318;
    public static final int PG2_S_DIOR_MAX = 319;

    public static final int PG2_S_ANDE_SIZE = 320;
    public static final int PG2_S_ANDE_CNT = 321;
    public static final int PG2_S_ANDE_MIN = 322;
    public static final int PG2_S_ANDE_MAX = 323;

    public static final int PG2_S_COAL_SIZE = 324;
    public static final int PG2_S_COAL_CNT = 325;
    public static final int PG2_S_COAL_MIN = 326;
    public static final int PG2_S_COAL_MAX = 327;
    
    public static final int PG2_S_IRON_SIZE = 328;
    public static final int PG2_S_IRON_CNT = 329;
    public static final int PG2_S_IRON_MIN = 330;
    public static final int PG2_S_IRON_MAX = 331;

    public static final int PG2_S_GOLD_SIZE = 332;
    public static final int PG2_S_GOLD_CNT = 333;
    public static final int PG2_S_GOLD_MIN = 334;
    public static final int PG2_S_GOLD_MAX = 335;

    public static final int PG2_S_REDS_SIZE = 336;
    public static final int PG2_S_REDS_CNT = 337;
    public static final int PG2_S_REDS_MIN = 338;
    public static final int PG2_S_REDS_MAX = 339;

    public static final int PG2_S_DIAM_SIZE = 340;
    public static final int PG2_S_DIAM_CNT = 341;
    public static final int PG2_S_DIAM_MIN = 342;
    public static final int PG2_S_DIAM_MAX = 343;

    public static final int PG2_S_LAPS_SIZE = 344;
    public static final int PG2_S_LAPS_CNT = 345;
    public static final int PG2_S_LAPS_CTR = 346;
    public static final int PG2_S_LAPS_SPR = 347;
    
    public static final int PG2_S_EMER_SIZE = 348;
    public static final int PG2_S_EMER_CNT = 349;
    public static final int PG2_S_EMER_MIN = 350;
    public static final int PG2_S_EMER_MAX = 351;
    
    public static final int PG2_S_QRTZ_SIZE = 352;
    public static final int PG2_S_QRTZ_CNT = 353;
    
    public static final int PG2_S_MGMA_SIZE = 354;
    public static final int PG2_S_MGMA_CNT = 355;
    
    // Labels
    public static final int PG2_L_CLAY_NAME = 1300;
    public static final int PG2_L_DIRT_NAME = 1301;
    public static final int PG2_L_GRAV_NAME = 1302;
    public static final int PG2_L_GRAN_NAME = 1303;
    public static final int PG2_L_DIOR_NAME = 1304;
    public static final int PG2_L_ANDE_NAME = 1305;
    public static final int PG2_L_COAL_NAME = 1306;
    public static final int PG2_L_IRON_NAME = 1307;
    public static final int PG2_L_GOLD_NAME = 1308;
    public static final int PG2_L_REDS_NAME = 1309;
    public static final int PG2_L_DIAM_NAME = 1310;
    public static final int PG2_L_LAPS_NAME = 1311;
    public static final int PG2_L_EMER_NAME = 1312;
    public static final int PG2_L_QRTZ_NAME = 1313;
    public static final int PG2_L_MGMA_NAME = 1314;
    
    /* Page 4 */
    
    // Entries
    public static final int PG3_S_MAIN_NS_X = 400;
    public static final int PG3_S_MAIN_NS_Y = 401;
    public static final int PG3_S_MAIN_NS_Z = 402;
    public static final int PG3_S_DPTH_NS_X = 403;
    public static final int PG3_S_DPTH_NS_Z = 404;
    public static final int PG3_S_BASE_SIZE = 406;
    public static final int PG3_S_COORD_SCL = 407;
    public static final int PG3_S_HEIGH_SCL = 408;
    public static final int PG3_S_STRETCH_Y = 409;
    public static final int PG3_S_UPPER_LIM = 410;
    public static final int PG3_S_LOWER_LIM = 411;
    public static final int PG3_S_HEIGH_LIM = 412;
    
    public static final int PG3_S_B_DPTH_WT = 413;
    public static final int PG3_S_B_DPTH_OF = 414;
    public static final int PG3_S_B_SCL_WT = 415;
    public static final int PG3_S_B_SCL_OF = 416;
    public static final int PG3_S_RIVER_SZ = 417;
    public static final int PG3_B_USE_BDS = 418;

    public static final int PG3_S_BIOME_SZ = 450;
    public static final int PG3_S_TEMP_SCL = 451;
    public static final int PG3_S_RAIN_SCL = 452;
    public static final int PG3_S_DETL_SCL = 453;
    
    // Labels
    public static final int PG3_L_BETA_LABL = 1400;
    public static final int PG3_L_RELE_LABL = 1401;
    
    /* Page 5 */
    
    // Entries
    public static final int PG4_F_MAIN_NS_X = 500;
    public static final int PG4_F_MAIN_NS_Y = 501;
    public static final int PG4_F_MAIN_NS_Z = 502;
    public static final int PG4_F_DPTH_NS_X = 503;
    public static final int PG4_F_DPTH_NS_Z = 504;
    public static final int PG4_F_BASE_SIZE = 506;
    public static final int PG4_F_COORD_SCL = 507;
    public static final int PG4_F_HEIGH_SCL = 508;
    public static final int PG4_F_STRETCH_Y = 509;
    public static final int PG4_F_UPPER_LIM = 510;
    public static final int PG4_F_LOWER_LIM = 511;
    public static final int PG4_F_HEIGH_LIM = 512;
    
    public static final int PG4_F_B_DPTH_WT = 513;
    public static final int PG4_F_B_DPTH_OF = 514;
    public static final int PG4_F_B_SCL_WT = 515;
    public static final int PG4_F_B_SCL_OF = 516;
    public static final int PG4_F_RIVER_SZ = 527;

    public static final int PG4_F_BIOME_SZ = 550;
    public static final int PG4_F_TEMP_SCL = 551;
    public static final int PG4_F_RAIN_SCL = 552;
    public static final int PG4_F_DETL_SCL = 553;
    
    // Labels
    public static final int PG4_L_MAIN_NS_X = 1500;
    public static final int PG4_L_MAIN_NS_Y = 1501;
    public static final int PG4_L_MAIN_NS_Z = 1502;
    public static final int PG4_L_DPTH_NS_X = 1503;
    public static final int PG4_L_DPTH_NS_Z = 1504;
    public static final int PG4_L_BASE_SIZE = 1506;
    public static final int PG4_L_COORD_SCL = 1507;
    public static final int PG4_L_HEIGH_SCL = 1508;
    public static final int PG4_L_STRETCH_Y = 1509;
    public static final int PG4_L_UPPER_LIM = 1510;
    public static final int PG4_L_LOWER_LIM = 1511;
    public static final int PG4_L_HEIGH_LIM = 1512;
    
    public static final int PG4_L_TEMP_SCL = 1513;
    public static final int PG4_L_RAIN_SCL = 1514;
    public static final int PG4_L_DETL_SCL = 1515;
    
    public static final int PG4_L_B_DPTH_WT = 1516;
    public static final int PG4_L_B_DPTH_OF = 1517;
    public static final int PG4_L_B_SCL_WT = 1518;
    public static final int PG4_L_B_SCL_OF = 1519;
    public static final int PG4_L_BIOME_SZ = 1520;
    public static final int PG4_L_RIVER_SZ = 1521;
    
    /* Page 6 */
    
    // Entries
    public static final int PG5_DSRT_LAND = 600;
    public static final int PG5_DSRT_OCEAN = 601;
    public static final int PG5_DSRT_BEACH = 602;
    
    public static final int PG5_FRST_LAND = 603;
    public static final int PG5_FRST_OCEAN = 604;
    public static final int PG5_FRST_BEACH = 605;
    
    public static final int PG5_ICED_LAND = 606;
    public static final int PG5_ICED_OCEAN = 607;
    public static final int PG5_ICED_BEACH = 608;
    
    public static final int PG5_PLNS_LAND = 609;
    public static final int PG5_PLNS_OCEAN = 610;
    public static final int PG5_PLNS_BEACH = 611;
    
    public static final int PG5_RAIN_LAND = 612;
    public static final int PG5_RAIN_OCEAN = 613;
    public static final int PG5_RAIN_BEACH = 614;
    
    public static final int PG5_SAVA_LAND = 615;
    public static final int PG5_SAVA_OCEAN = 616;
    public static final int PG5_SAVA_BEACH = 617;
    
    public static final int PG5_SHRB_LAND = 618;
    public static final int PG5_SHRB_OCEAN = 619;
    public static final int PG5_SHRB_BEACH = 620;
    
    public static final int PG5_SEAS_LAND = 621;
    public static final int PG5_SEAS_OCEAN = 622;
    public static final int PG5_SEAS_BEACH = 623;
    
    public static final int PG5_SWMP_LAND = 624;
    public static final int PG5_SWMP_OCEAN = 625;
    public static final int PG5_SWMP_BEACH = 626;
    
    public static final int PG5_TAIG_LAND = 627;
    public static final int PG5_TAIG_OCEAN = 628;
    public static final int PG5_TAIG_BEACH = 629;
    
    public static final int PG5_TUND_LAND = 630;
    public static final int PG5_TUND_OCEAN = 631;
    public static final int PG5_TUND_BEACH = 632;
    
    // Labels
    public static final int PG5_LAND_LABL = 1600;
    public static final int PG5_OCEAN_LABL = 1601;
    public static final int PG5_BEACH_LABL = 1602;
    
    public static final int PG5_DSRT_LABL = 1603;
    public static final int PG5_FRST_LABL = 1604;
    public static final int PG5_ICED_LABL = 1605;
    public static final int PG5_PLNS_LABL = 1606;
    public static final int PG5_RAIN_LABL = 1607;
    public static final int PG5_SAVA_LABL = 1608;
    public static final int PG5_SHRB_LABL = 1609;
    public static final int PG5_SEAS_LABL = 1610;
    public static final int PG5_SWMP_LABL = 1611;
    public static final int PG5_TAIG_LABL = 1612;
    public static final int PG5_TUND_LABL = 1613;
    
    public static int offsetForward(int entry) {
        return entry + 100;
    }
    
    public static int offsetBackward(int entry) {
        return entry - 100;
    }
    
    public static void assertOffsets() {
        assertOffset(PG3_S_MAIN_NS_X, PG4_F_MAIN_NS_X);
        assertOffset(PG3_S_MAIN_NS_Y, PG4_F_MAIN_NS_Y);
        assertOffset(PG3_S_MAIN_NS_Z, PG4_F_MAIN_NS_Z);
        assertOffset(PG3_S_DPTH_NS_X, PG4_F_DPTH_NS_X);
        assertOffset(PG3_S_DPTH_NS_Z, PG4_F_DPTH_NS_Z);
        assertOffset(PG3_S_BASE_SIZE, PG4_F_BASE_SIZE);
        assertOffset(PG3_S_COORD_SCL, PG4_F_COORD_SCL);
        assertOffset(PG3_S_HEIGH_SCL, PG4_F_HEIGH_SCL);
        assertOffset(PG3_S_STRETCH_Y, PG4_F_STRETCH_Y);
        assertOffset(PG3_S_UPPER_LIM, PG4_F_UPPER_LIM);
        assertOffset(PG3_S_LOWER_LIM, PG4_F_LOWER_LIM);
        assertOffset(PG3_S_HEIGH_LIM, PG4_F_HEIGH_LIM);
        
        assertOffset(PG3_S_TEMP_SCL, PG4_F_TEMP_SCL);
        assertOffset(PG3_S_RAIN_SCL, PG4_F_RAIN_SCL);
        assertOffset(PG3_S_DETL_SCL, PG4_F_DETL_SCL);
        
        assertOffset(PG3_S_B_DPTH_WT, PG4_F_B_DPTH_WT);
        assertOffset(PG3_S_B_DPTH_OF, PG4_F_B_DPTH_OF);
        assertOffset(PG3_S_B_SCL_WT, PG4_F_B_SCL_WT);
        assertOffset(PG3_S_B_SCL_OF, PG4_F_B_SCL_OF);
    }
    
    private static void assertOffset(int sliderId, int fieldId) {
        if (sliderId != offsetBackward(fieldId)) {
            String errorStr = String.format("[Modern Beta] GUI slider id %d not correctly offset with field id %d!", sliderId, fieldId);
            
            throw new IllegalStateException(errorStr);
        }
    }
    
    private static void add(int id) {
        add(id, (factory) -> true);
    }
    
    private static void add(int id, Predicate<ModernBetaChunkGeneratorSettings.Factory> predicate) {
        if (GUI_IDS.containsKey(id)) {
            String errorStr = String.format("[Modern Beta] GUI id %d has already been registered!", id);
            
            throw new IllegalArgumentException(errorStr);
        }
        
        GUI_IDS.put(id, predicate);
    }
    
    static {
        Predicate<Factory> testSurface = factory -> {
            String chunkSource = factory.chunkSource;
            
            boolean isSkylands = chunkSource.equals(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id);
            boolean isIndev = chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.id);
            
            return !isIndev && !isSkylands;
        };
        Predicate<Factory> testCarver = factory -> factory.useCaves;
        Predicate<Factory> testFixed = factory -> factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
        Predicate<Factory> testBiomeReplacement = factory -> {
            boolean isFixedBiomeSource = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean isSkylands = factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id);
            
            return !isFixedBiomeSource && !isSkylands;
        };
        Predicate<Factory> testClassicNetherBoP = factory -> !ModCompat.isBoPLoaded();
        Predicate<Factory> testClassicNether = factory -> factory.useOldNether && !ModCompat.isBoPLoaded();
        Predicate<Factory> testDungeons = factory -> factory.useDungeons;
        Predicate<Factory> testWaterLakes = factory -> factory.useWaterLakes;
        Predicate<Factory> testLavaLakes = factory -> factory.useLavaLakes;
        Predicate<Factory> testVillageVariants = factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean hasVillages = isFixed ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
            
            return hasVillages && factory.useVillages;
        };
        Predicate<Factory> testSandstone = factory -> {
            boolean isReleaseSurface = factory.surfaceBuilder.equals(ModernBetaBuiltInTypes.Surface.RELEASE.id);
            boolean isIndev = factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.id);
            
            return !isReleaseSurface && !isIndev;
        };
        Predicate<Factory> testIndev = factory -> factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.id);
        Predicate<Factory> testInfdev227 = factory -> factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INFDEV_227.id);
        Predicate<Factory> testReleaseBiomeSource = factory -> factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.id);
        Predicate<Factory> testBetaBiomeFeature = factory -> {
            String biomeSource = factory.biomeSource;
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            
            boolean isBetaOrPEBiomeSource = 
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
            boolean isFixedBiomeSource = biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean isBetaBiome = biome instanceof BiomeBeta;
            
            return isBetaOrPEBiomeSource || (isFixedBiomeSource && isBetaBiome);
        };
        Predicate<Factory> testModernBetaBiomeFeature = factory -> {
            String biomeSource = factory.biomeSource;
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            
            boolean isBetaOrPEBiomeSource = 
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
            boolean isFixedBiomeSource = biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean isModernBetaBiome = biome instanceof ModernBetaBiome;
            
            return isBetaOrPEBiomeSource || (isFixedBiomeSource && isModernBetaBiome);
        };
        Predicate<Factory> testModernBetaOre = factory -> {
            String biomeSource = factory.biomeSource;
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));

            boolean isReleaseBiomeSource = biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.id);
            boolean isFixedBiomeSource = biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean isModernBetaBiome = biome instanceof ModernBetaBiome;
            
            
            return (isFixedBiomeSource && isModernBetaBiome) ||  (!isReleaseBiomeSource && !isFixedBiomeSource);
        };
        Predicate<Factory> testStrongholds = factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? STRONGHOLD.allowedBiomes.contains(biome) : true;
        };
        Predicate<Factory> testVillages = factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
        };
        Predicate<Factory> testTemples = factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? MapGenScatteredFeature.BIOMELIST.contains(biome) : true;
        };
        Predicate<Factory> testMonuments = factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? StructureOceanMonument.SPAWN_BIOMES.contains(biome) : true;
        };
        Predicate<Factory> testMansions = factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? WoodlandMansion.ALLOWED_BIOMES.contains(biome) : true;
        };
        Predicate<Factory> testCustomBetaBiomes = factory -> {
            String biomeSource = factory.biomeSource;
            boolean isBetaOrPEBiomeSource = 
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
            
            return isBetaOrPEBiomeSource;
        };
        Predicate<Factory> testBiomeSize = factory -> {
            String chunkSource = factory.chunkSource;
            String biomeSource = factory.biomeSource;
            
            return chunkSource.equals(ModernBetaBuiltInTypes.Chunk.RELEASE.id) || biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.id);
        };
        Predicate<Factory> testClimateScales = factory -> {
            String chunkSource = factory.chunkSource;
            String biomeSource = factory.biomeSource;

            return 
                chunkSource.equals(ModernBetaBuiltInTypes.Chunk.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                chunkSource.equals(ModernBetaBuiltInTypes.Chunk.PE.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
        };
        
        add(PG0_S_CHUNK);
        add(PG0_S_BIOME);
        add(PG0_S_SURFACE, testSurface);
        add(PG0_S_CARVER, testCarver);
        
        add(PG0_S_FIXED, testFixed);
        
        add(PG0_B_USE_OCEAN, testBiomeReplacement);
        add(PG0_B_USE_BEACH, testBiomeReplacement);
        
        add(PG0_S_SEA_LEVEL, testSurface);
        add(PG0_B_USE_CAVES);
        add(PG0_B_USE_HOLDS, testStrongholds);
        add(PG0_B_USE_VILLAGES, testVillages);
        add(PG0_B_USE_VILLAGE_VARIANTS, testVillageVariants);
        add(PG0_B_USE_SHAFTS);
        add(PG0_B_USE_TEMPLES, testTemples);
        add(PG0_B_USE_MONUMENTS, testMonuments);
        add(PG0_B_USE_MANSIONS, testMansions);
        add(PG0_B_USE_RAVINES);
        add(PG0_B_USE_DUNGEONS);
        add(PG0_S_DUNGEON_CHANCE, testDungeons);
        add(PG0_B_USE_WATER_LAKES);
        add(PG0_S_WATER_LAKE_CHANCE, testWaterLakes);
        add(PG0_B_USE_LAVA_LAKES);
        add(PG0_S_LAVA_LAKE_CHANCE, testLavaLakes);
        add(PG0_B_USE_LAVA_OCEANS);
        add(PG0_B_USE_SANDSTONE, testSandstone);
        
        add(PG0_B_USE_OLD_NETHER, testClassicNetherBoP);
        add(PG0_B_USE_NETHER_CAVES, testClassicNether);
        add(PG0_B_USE_FORTRESSES, testClassicNether);
        add(PG0_B_USE_LAVA_POCKETS, testClassicNether);
        
        add(PG0_S_LEVEL_THEME, testIndev);
        add(PG0_S_LEVEL_TYPE, testIndev);
        add(PG0_S_LEVEL_WIDTH, testIndev);
        add(PG0_S_LEVEL_LENGTH, testIndev);
        add(PG0_S_LEVEL_HEIGHT, testIndev);
        add(PG0_B_USE_INDEV_CAVES, testIndev);
        add(PG0_B_USE_INDEV_HOUSE, testIndev);
        
        add(PG0_B_USE_INFDEV_WALLS, testInfdev227);
        add(PG0_B_USE_INFDEV_PYRAMIDS, testInfdev227);
        
        add(PG1_B_USE_GRASS, testModernBetaBiomeFeature);
        add(PG1_B_USE_FLOWERS, testBetaBiomeFeature);
        add(PG1_B_USE_PADS, testBetaBiomeFeature);
        add(PG1_B_USE_MELONS, testBetaBiomeFeature);
        
        add(PG1_B_USE_WELLS, testBetaBiomeFeature);
        add(PG1_B_USE_FOSSILS, testBetaBiomeFeature);
        
        add(PG1_B_USE_BIRCH, testBetaBiomeFeature);
        add(PG1_B_USE_PINE, testBetaBiomeFeature);
        add(PG1_B_USE_SWAMP, testBetaBiomeFeature);
        add(PG1_B_USE_JUNGLE, testBetaBiomeFeature);
        add(PG1_B_USE_ACACIA, testBetaBiomeFeature);
        
        add(PG1_B_SPAWN_CREATURE, testModernBetaBiomeFeature);
        add(PG1_B_SPAWN_MONSTER, testModernBetaBiomeFeature);
        add(PG1_B_SPAWN_WATER, testModernBetaBiomeFeature);
        add(PG1_B_SPAWN_AMBIENT, testModernBetaBiomeFeature);
        add(PG1_B_SPAWN_WOLVES, testModernBetaBiomeFeature);
        
        add(PG1_B_USE_MODDED_BIOMES, testReleaseBiomeSource);
        
        add(PG2_S_CLAY_SIZE, testModernBetaOre);
        add(PG2_S_CLAY_CNT, testModernBetaOre);
        add(PG2_S_CLAY_MIN, testModernBetaOre);
        add(PG2_S_CLAY_MAX, testModernBetaOre);

        add(PG2_S_DIRT_SIZE);
        add(PG2_S_DIRT_CNT);
        add(PG2_S_DIRT_MIN);
        add(PG2_S_DIRT_MAX);

        add(PG2_S_GRAV_SIZE);
        add(PG2_S_GRAV_CNT);
        add(PG2_S_GRAV_MIN);
        add(PG2_S_GRAV_MAX);

        add(PG2_S_GRAN_SIZE);
        add(PG2_S_GRAN_CNT);
        add(PG2_S_GRAN_MIN);
        add(PG2_S_GRAN_MAX);

        add(PG2_S_DIOR_SIZE);
        add(PG2_S_DIOR_CNT);
        add(PG2_S_DIOR_MIN);
        add(PG2_S_DIOR_MAX);

        add(PG2_S_ANDE_SIZE);
        add(PG2_S_ANDE_CNT);
        add(PG2_S_ANDE_MIN);
        add(PG2_S_ANDE_MAX);

        add(PG2_S_COAL_SIZE);
        add(PG2_S_COAL_CNT);
        add(PG2_S_COAL_MIN);
        add(PG2_S_COAL_MAX);
        
        add(PG2_S_IRON_SIZE);
        add(PG2_S_IRON_CNT);
        add(PG2_S_IRON_MIN);
        add(PG2_S_IRON_MAX);

        add(PG2_S_GOLD_SIZE);
        add(PG2_S_GOLD_CNT);
        add(PG2_S_GOLD_MIN);
        add(PG2_S_GOLD_MAX);

        add(PG2_S_REDS_SIZE);
        add(PG2_S_REDS_CNT);
        add(PG2_S_REDS_MIN);
        add(PG2_S_REDS_MAX);

        add(PG2_S_DIAM_SIZE);
        add(PG2_S_DIAM_CNT);
        add(PG2_S_DIAM_MIN);
        add(PG2_S_DIAM_MAX);

        add(PG2_S_LAPS_SIZE);
        add(PG2_S_LAPS_CNT);
        add(PG2_S_LAPS_CTR);
        add(PG2_S_LAPS_SPR);
        
        add(PG2_S_EMER_SIZE, testModernBetaOre);
        add(PG2_S_EMER_CNT, testModernBetaOre);
        add(PG2_S_EMER_MIN, testModernBetaOre);
        add(PG2_S_EMER_MAX, testModernBetaOre);
        
        add(PG2_S_QRTZ_SIZE, testClassicNether);
        add(PG2_S_QRTZ_CNT, testClassicNether);
        
        add(PG2_S_MGMA_SIZE, testClassicNether);
        add(PG2_S_MGMA_CNT, testClassicNether);
        
        add(PG3_S_MAIN_NS_X);
        add(PG3_S_MAIN_NS_Y);
        add(PG3_S_MAIN_NS_Z);
        add(PG3_S_DPTH_NS_X);
        add(PG3_S_DPTH_NS_Z);
        add(PG3_S_BASE_SIZE);
        add(PG3_S_COORD_SCL);
        add(PG3_S_HEIGH_SCL);
        add(PG3_S_STRETCH_Y);
        add(PG3_S_UPPER_LIM);
        add(PG3_S_LOWER_LIM);
        add(PG3_S_HEIGH_LIM);
        
        add(PG3_S_TEMP_SCL, testClimateScales);
        add(PG3_S_RAIN_SCL, testClimateScales);
        add(PG3_S_DETL_SCL, testClimateScales);
        
        add(PG3_S_B_DPTH_WT);
        add(PG3_S_B_DPTH_OF);
        add(PG3_S_B_SCL_WT);
        add(PG3_S_B_SCL_OF);
        add(PG3_S_BIOME_SZ, testBiomeSize);
        add(PG3_S_RIVER_SZ);
        
        add(PG3_B_USE_BDS);
        
        add(PG4_F_MAIN_NS_X);
        add(PG4_F_MAIN_NS_Y);
        add(PG4_F_MAIN_NS_Z);
        add(PG4_F_DPTH_NS_X);
        add(PG4_F_DPTH_NS_Z);
        add(PG4_F_BASE_SIZE);
        add(PG4_F_COORD_SCL);
        add(PG4_F_HEIGH_SCL);
        add(PG4_F_STRETCH_Y);
        add(PG4_F_UPPER_LIM);
        add(PG4_F_LOWER_LIM);
        add(PG4_F_HEIGH_LIM);
        
        add(PG4_F_TEMP_SCL, testClimateScales);
        add(PG4_F_RAIN_SCL, testClimateScales);
        add(PG4_F_DETL_SCL, testClimateScales);
        
        add(PG4_F_B_DPTH_WT);
        add(PG4_F_B_DPTH_OF);
        add(PG4_F_B_SCL_WT);
        add(PG4_F_B_SCL_OF);
        add(PG4_F_BIOME_SZ, testBiomeSize);
        add(PG4_F_RIVER_SZ);
        
        add(PG5_DSRT_LAND, testCustomBetaBiomes);
        add(PG5_DSRT_OCEAN, testCustomBetaBiomes);
        add(PG5_DSRT_BEACH, testCustomBetaBiomes);
        
        add(PG5_FRST_LAND, testCustomBetaBiomes);
        add(PG5_FRST_OCEAN, testCustomBetaBiomes);
        add(PG5_FRST_BEACH, testCustomBetaBiomes);
        
        add(PG5_ICED_LAND, testCustomBetaBiomes);
        add(PG5_ICED_OCEAN, testCustomBetaBiomes);
        add(PG5_ICED_BEACH, testCustomBetaBiomes);
        
        add(PG5_PLNS_LAND, testCustomBetaBiomes);
        add(PG5_PLNS_OCEAN, testCustomBetaBiomes);
        add(PG5_PLNS_BEACH, testCustomBetaBiomes);
        
        add(PG5_RAIN_LAND, testCustomBetaBiomes);
        add(PG5_RAIN_OCEAN, testCustomBetaBiomes);
        add(PG5_RAIN_BEACH, testCustomBetaBiomes);
        
        add(PG5_SAVA_LAND, testCustomBetaBiomes);
        add(PG5_SAVA_OCEAN, testCustomBetaBiomes);
        add(PG5_SAVA_BEACH, testCustomBetaBiomes);
        
        add(PG5_SHRB_LAND, testCustomBetaBiomes);
        add(PG5_SHRB_OCEAN, testCustomBetaBiomes);
        add(PG5_SHRB_BEACH, testCustomBetaBiomes);
        
        add(PG5_SEAS_LAND, testCustomBetaBiomes);
        add(PG5_SEAS_OCEAN, testCustomBetaBiomes);
        add(PG5_SEAS_BEACH, testCustomBetaBiomes);
        
        add(PG5_SWMP_LAND, testCustomBetaBiomes);
        add(PG5_SWMP_OCEAN, testCustomBetaBiomes);
        add(PG5_SWMP_BEACH, testCustomBetaBiomes);
        
        add(PG5_TAIG_LAND, testCustomBetaBiomes);
        add(PG5_TAIG_OCEAN, testCustomBetaBiomes);
        add(PG5_TAIG_BEACH, testCustomBetaBiomes);
        
        add(PG5_TUND_LAND, testCustomBetaBiomes);
        add(PG5_TUND_OCEAN, testCustomBetaBiomes);
        add(PG5_TUND_BEACH, testCustomBetaBiomes);
    }
}
