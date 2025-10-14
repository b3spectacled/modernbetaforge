package mod.bespectacled.modernbetaforge.client.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIdentifiers {
    public static final Map<Integer, BiConsumer<String, ModernBetaGeneratorSettings.Factory>> BIOME_SETTINGS = new HashMap<>();
    public static final Map<Integer, BiConsumer<String, ModernBetaGeneratorSettings.Factory>> BASE_SETTINGS = new HashMap<>();
    
    /* Function Buttons */
    public static final int FUNC_INITIAL_TAB = 30;
    
    public static final int FUNC_DONE = 60;
    public static final int FUNC_RAND = 61;
    public static final int FUNC_DFLT = 62;
    public static final int FUNC_PRST = 63;
    public static final int FUNC_CONF = 64;
    public static final int FUNC_CNCL = 65;
    public static final int FUNC_PRVW = 66;
    
    /* Page 1 */
    
    // Entries
    
    public static final int PG0_S_CHUNK = 100;
    public static final int PG0_S_BIOME = 101;
    public static final int PG0_S_SURFACE = 102;
    public static final int PG0_S_CARVER = 103;
    public static final int PG0_S_SPAWN = 104;
    
    public static final int PG0_B_FIXED = 105;
    
    public static final int PG0_B_USE_OCEAN = 106;
    public static final int PG0_B_USE_BEACH = 107;
    public static final int PG0_B_USE_RIVER = 108;
    
    public static final int PG0_S_SEA_LEVEL = 109;
    public static final int PG0_B_USE_HOLDS = 110;
    public static final int PG0_B_USE_VILLAGES = 111;
    public static final int PG0_B_USE_VILLAGE_VARIANTS = 112;
    public static final int PG0_B_USE_SHAFTS = 113;
    public static final int PG0_B_USE_TEMPLES = 114;
    public static final int PG0_B_USE_MONUMENTS = 115;
    public static final int PG0_B_USE_MANSIONS = 116;
    public static final int PG0_B_USE_RAVINES = 117;
    public static final int PG0_B_USE_DUNGEONS = 118;
    public static final int PG0_S_DUNGEON_CHANCE = 119;
    public static final int PG0_B_USE_WATER_LAKES = 120;
    public static final int PG0_S_WATER_LAKE_CHANCE = 121;
    public static final int PG0_B_USE_LAVA_LAKES = 122;
    public static final int PG0_S_LAVA_LAKE_CHANCE = 123;
    public static final int PG0_B_USE_LAVA_OCEANS = 124;
    public static final int PG0_B_USE_SANDSTONE = 125;
    
    public static final int PG0_B_USE_OLD_NETHER = 126;
    public static final int PG0_B_USE_NETHER_CAVES = 127;
    public static final int PG0_B_USE_FORTRESSES = 128;
    public static final int PG0_B_USE_LAVA_POCKETS = 129;
    
    public static final int PG0_S_CAVE_WIDTH = 130;
    public static final int PG0_S_CAVE_HEIGHT = 131;
    public static final int PG0_S_CAVE_COUNT = 132;
    public static final int PG0_S_CAVE_CHANCE = 133;
    
    public static final int PG0_S_BLOCK = 134;
    public static final int PG0_S_FLUID = 135;
    
    public static final int PG0_B_USE_UNDERWATER_CAVES = 136;
    
    public static final int PG0_B_CHUNK = 190;
    public static final int PG0_B_BIOME = 191;
    public static final int PG0_B_SURFACE = 192;
    public static final int PG0_B_CARVER = 193;
    public static final int PG0_B_SPAWN = 194;
    public static final int PG0_B_BLOCK = 195;
    public static final int PG0_B_FLUID = 196;
    
    public static final int PG0_L_INDEV_SEA_LEVEL = 150;
    
    // Labels
    public static final int PG0_L_SURFACE_BUILDER = 1000;
    public static final int PG0_L_FIXED_BIOME = 1001;
    public static final int PG0_L_BIOME_REPLACEMENT = 1002;
    public static final int PG0_L_BASIC_FEATURES = 1003;
    public static final int PG0_L_NETHER_FEATURES = 1004;
    public static final int PG0_L_NETHER_BOP = 1005;
    
    /* Page 2 */
    public static final int PG1_S_LEVEL_THEME = 200;
    public static final int PG1_S_LEVEL_TYPE = 201;
    public static final int PG1_S_LEVEL_WIDTH = 202;
    public static final int PG1_S_LEVEL_LENGTH = 203;
    public static final int PG1_S_LEVEL_HEIGHT = 204;
    public static final int PG1_S_LEVEL_HOUSE = 205;
    public static final int PG1_B_USE_INDEV_CAVES = 206;
    public static final int PG1_S_LEVEL_CAVE_WIDTH = 207;
    
    public static final int PG1_B_USE_INFDEV_WALLS = 208;
    public static final int PG1_B_USE_INFDEV_PYRAMIDS = 209;
    
    public static final int PG1_S_RIVER_SZ = 210;
    public static final int PG1_S_LAYER_SZ = 211;
    public static final int PG1_S_LAYER_TYPE = 212;

    public static final int PG1_L_INDEV_FEATURES = 2000;
    public static final int PG1_L_INFDEV_227_FEATURES = 2001;
    public static final int PG1_L_RELEASE_FEATURES = 2002;
    
    /* Page 3 */
    
    public static final int PG2_B_USE_GRASS = 300;
    public static final int PG2_B_USE_FLOWERS = 301;
    public static final int PG2_B_USE_PADS = 302;
    public static final int PG2_B_USE_MELONS = 303;
    
    public static final int PG2_B_USE_WELLS = 304;
    public static final int PG2_B_USE_FOSSILS = 305;
    
    public static final int PG2_B_USE_BIRCH = 306;
    public static final int PG2_B_USE_PINE = 307;
    public static final int PG2_B_USE_SWAMP = 308;
    public static final int PG2_B_USE_JUNGLE = 309;
    public static final int PG2_B_USE_ACACIA = 310;
    public static final int PG2_B_USE_FANCY_OAK = 311;
    public static final int PG2_B_USE_SAND_DISKS = 312;
    public static final int PG2_B_USE_GRAV_DISKS = 313;
    public static final int PG2_B_USE_CLAY_DISKS = 314;
    public static final int PG2_B_USE_DOUBLE = 315;
    
    public static final int PG2_B_SPAWN_CREATURE = 320;
    public static final int PG2_B_SPAWN_MONSTER = 321;
    public static final int PG2_B_SPAWN_WATER = 322;
    public static final int PG2_B_SPAWN_AMBIENT = 323;
    public static final int PG2_B_SPAWN_WOLVES = 324;
    
    public static final int PG2_S_BIOME_SZ = 330;
    public static final int PG2_S_SNOWY_CHANCE = 331;
    
    // Labels
    public static final int PG2_L_BETA = 1300;
    public static final int PG2_L_MOBS = 1301;
    public static final int PG2_L_RELEASE = 1302;
    public static final int PG2_L_MODS = 1303;
    public static final int PG0_L_TREES = 1304;
    
    /* Page 4 */
    
    // Entries
    public static final int PG3_S_CLAY_SIZE = 400;
    public static final int PG3_S_CLAY_CNT = 401;
    public static final int PG3_S_CLAY_MIN = 402;
    public static final int PG3_S_CLAY_MAX = 403;

    public static final int PG3_S_DIRT_SIZE = 404;
    public static final int PG3_S_DIRT_CNT = 405;
    public static final int PG3_S_DIRT_MIN = 406;
    public static final int PG3_S_DIRT_MAX = 407;

    public static final int PG3_S_GRAV_SIZE = 408;
    public static final int PG3_S_GRAV_CNT = 409;
    public static final int PG3_S_GRAV_MIN = 410;
    public static final int PG3_S_GRAV_MAX = 411;

    public static final int PG3_S_GRAN_SIZE = 412;
    public static final int PG3_S_GRAN_CNT = 413;
    public static final int PG3_S_GRAN_MIN = 414;
    public static final int PG3_S_GRAN_MAX = 415;

    public static final int PG3_S_DIOR_SIZE = 416;
    public static final int PG3_S_DIOR_CNT = 417;
    public static final int PG3_S_DIOR_MIN = 418;
    public static final int PG3_S_DIOR_MAX = 419;

    public static final int PG3_S_ANDE_SIZE = 420;
    public static final int PG3_S_ANDE_CNT = 421;
    public static final int PG3_S_ANDE_MIN = 422;
    public static final int PG3_S_ANDE_MAX = 423;

    public static final int PG3_S_COAL_SIZE = 424;
    public static final int PG3_S_COAL_CNT = 425;
    public static final int PG3_S_COAL_MIN = 426;
    public static final int PG3_S_COAL_MAX = 427;
    
    public static final int PG3_S_IRON_SIZE = 428;
    public static final int PG3_S_IRON_CNT = 429;
    public static final int PG3_S_IRON_MIN = 430;
    public static final int PG3_S_IRON_MAX = 431;

    public static final int PG3_S_GOLD_SIZE = 432;
    public static final int PG3_S_GOLD_CNT = 433;
    public static final int PG3_S_GOLD_MIN = 434;
    public static final int PG3_S_GOLD_MAX = 435;

    public static final int PG3_S_REDS_SIZE = 436;
    public static final int PG3_S_REDS_CNT = 437;
    public static final int PG3_S_REDS_MIN = 438;
    public static final int PG3_S_REDS_MAX = 439;

    public static final int PG3_S_DIAM_SIZE = 440;
    public static final int PG3_S_DIAM_CNT = 441;
    public static final int PG3_S_DIAM_MIN = 442;
    public static final int PG3_S_DIAM_MAX = 443;

    public static final int PG3_S_LAPS_SIZE = 444;
    public static final int PG3_S_LAPS_CNT = 445;
    public static final int PG3_S_LAPS_CTR = 446;
    public static final int PG3_S_LAPS_SPR = 447;
    
    public static final int PG3_S_EMER_SIZE = 448;
    public static final int PG3_S_EMER_CNT = 449;
    public static final int PG3_S_EMER_MIN = 450;
    public static final int PG3_S_EMER_MAX = 451;
    
    public static final int PG3_S_QRTZ_SIZE = 452;
    public static final int PG3_S_QRTZ_CNT = 453;
    
    public static final int PG3_S_MGMA_SIZE = 454;
    public static final int PG3_S_MGMA_CNT = 455;
    
    public static final int PG3_B_USE_OLD_ORES = 460;
    
    // Labels
    public static final int PG3_L_CLAY_NAME = 1400;
    public static final int PG3_L_DIRT_NAME = 1401;
    public static final int PG3_L_GRAV_NAME = 1402;
    public static final int PG3_L_GRAN_NAME = 1403;
    public static final int PG3_L_DIOR_NAME = 1404;
    public static final int PG3_L_ANDE_NAME = 1405;
    public static final int PG3_L_COAL_NAME = 1406;
    public static final int PG3_L_IRON_NAME = 1407;
    public static final int PG3_L_GOLD_NAME = 1408;
    public static final int PG3_L_REDS_NAME = 1409;
    public static final int PG3_L_DIAM_NAME = 1410;
    public static final int PG3_L_LAPS_NAME = 1411;
    public static final int PG3_L_EMER_NAME = 1412;
    public static final int PG3_L_QRTZ_NAME = 1413;
    public static final int PG3_L_MGMA_NAME = 1414;
    
    /* Page 5 */
    
    // Entries
    public static final int PG4_S_MAIN_NS_X = 500;
    public static final int PG4_S_MAIN_NS_Y = 501;
    public static final int PG4_S_MAIN_NS_Z = 502;
    public static final int PG4_S_DPTH_NS_X = 503;
    public static final int PG4_S_DPTH_NS_Z = 504;
    public static final int PG4_S_BASE_SIZE = 506;
    public static final int PG4_S_COORD_SCL = 507;
    public static final int PG4_S_HEIGH_SCL = 508;
    public static final int PG4_S_STRETCH_Y = 509;
    public static final int PG4_S_UPPER_LIM = 510;
    public static final int PG4_S_LOWER_LIM = 511;
    public static final int PG4_S_HEIGH_LIM = 512;
    
    public static final int PG4_S_B_DPTH_WT = 513;
    public static final int PG4_S_B_DPTH_OF = 514;
    public static final int PG4_S_B_SCLE_WT = 515;
    public static final int PG4_S_B_SCLE_OF = 516;

    public static final int PG4_S_TEMP_SCL = 520;
    public static final int PG4_S_RAIN_SCL = 521;
    public static final int PG4_S_DETL_SCL = 522;
    
    public static final int PG4_S_SCLE_NS_X = 524;
    public static final int PG4_S_SCLE_NS_Z = 525;

    public static final int PG4_S_END_OF = 526;
    public static final int PG4_S_END_WT = 527;
    public static final int PG4_S_END_OUT_OF = 528;
    public static final int PG4_S_END_OUT_DT = 529;

    public static final int PG4_SLIDER_END = 549;
    public static final int PG4_B_USE_BDS = 550;
    public static final int PG4_B_USE_END_OUT = 551;
    public static final int PG4_B_USE_AMP = 552;
    
    // Labels
    public static final int PG4_L_BETA_LABL = 1500;
    public static final int PG4_L_RELE_LABL = 1501;
    public static final int PG4_L_END_LABL = 1502;
    
    /* Page 6 */
    
    // Entries
    public static final int PG5_F_MAIN_NS_X = 600;
    public static final int PG5_F_MAIN_NS_Y = 601;
    public static final int PG5_F_MAIN_NS_Z = 602;
    public static final int PG5_F_DPTH_NS_X = 603;
    public static final int PG5_F_DPTH_NS_Z = 604;
    public static final int PG5_F_BASE_SIZE = 606;
    public static final int PG5_F_COORD_SCL = 607;
    public static final int PG5_F_HEIGH_SCL = 608;
    public static final int PG5_F_STRETCH_Y = 609;
    public static final int PG5_F_UPPER_LIM = 610;
    public static final int PG5_F_LOWER_LIM = 611;
    public static final int PG5_F_HEIGH_LIM = 612;
    
    public static final int PG5_F_B_DPTH_WT = 613;
    public static final int PG5_F_B_DPTH_OF = 614;
    public static final int PG5_F_B_SCLE_WT = 615;
    public static final int PG5_F_B_SCLE_OF = 616;

    public static final int PG5_F_TEMP_SCL = 620;
    public static final int PG5_F_RAIN_SCL = 621;
    public static final int PG5_F_DETL_SCL = 622;
    
    public static final int PG5_F_SCLE_NS_X = 624;
    public static final int PG5_F_SCLE_NS_Z = 625;
    
    public static final int PG5_F_END_OF = 626;
    public static final int PG5_F_END_WT = 627;
    public static final int PG5_F_END_OUT_OF = 628;
    public static final int PG5_F_END_OUT_DT = 629;
    
    public static final int PG5_FIELD_END = 649;
    
    // Labels
    public static final int PG5_L_MAIN_NS_X = 1600;
    public static final int PG5_L_MAIN_NS_Y = 1601;
    public static final int PG5_L_MAIN_NS_Z = 1602;
    public static final int PG5_L_DPTH_NS_X = 1603;
    public static final int PG5_L_DPTH_NS_Z = 1604;
    public static final int PG5_L_BASE_SIZE = 1606;
    public static final int PG5_L_COORD_SCL = 1607;
    public static final int PG5_L_HEIGH_SCL = 1608;
    public static final int PG5_L_STRETCH_Y = 1609;
    public static final int PG5_L_UPPER_LIM = 1610;
    public static final int PG5_L_LOWER_LIM = 1611;
    public static final int PG5_L_HEIGH_LIM = 1612;
    
    public static final int PG5_L_TEMP_SCL = 1613;
    public static final int PG5_L_RAIN_SCL = 1614;
    public static final int PG5_L_DETL_SCL = 1615;
    
    public static final int PG5_L_B_DPTH_WT = 1617;
    public static final int PG5_L_B_DPTH_OF = 1618;
    public static final int PG5_L_B_SCLE_WT = 1619;
    public static final int PG5_L_B_SCLE_OF = 1620;
    public static final int PG5_L_RIVER_SZ = 1621;
    public static final int PG5_L_LAYER_SZ = 1622;
    
    public static final int PG5_L_SCLE_NS_X = 1623;
    public static final int PG5_L_SCLE_NS_Z = 1624;
    
    public static final int PG5_L_END_OF = 1625;
    public static final int PG5_L_END_WT = 1626;
    public static final int PG5_L_END_OUT_OF = 1627;
    public static final int PG5_L_END_OUT_DT = 1628;
    
    /* Page 7 */
    
    // Entries
    public static final int PG6_DSRT_LAND = 700;
    public static final int PG6_DSRT_OCEAN = 701;
    public static final int PG6_DSRT_BEACH = 702;
    
    public static final int PG6_FRST_LAND = 703;
    public static final int PG6_FRST_OCEAN = 704;
    public static final int PG6_FRST_BEACH = 705;
    
    public static final int PG6_ICED_LAND = 706;
    public static final int PG6_ICED_OCEAN = 707;
    public static final int PG6_ICED_BEACH = 708;
    
    public static final int PG6_PLNS_LAND = 709;
    public static final int PG6_PLNS_OCEAN = 710;
    public static final int PG6_PLNS_BEACH = 711;
    
    public static final int PG6_RAIN_LAND = 712;
    public static final int PG6_RAIN_OCEAN = 713;
    public static final int PG6_RAIN_BEACH = 714;
    
    public static final int PG6_SAVA_LAND = 715;
    public static final int PG6_SAVA_OCEAN = 716;
    public static final int PG6_SAVA_BEACH = 717;
    
    public static final int PG6_SHRB_LAND = 718;
    public static final int PG6_SHRB_OCEAN = 719;
    public static final int PG6_SHRB_BEACH = 720;
    
    public static final int PG6_SEAS_LAND = 721;
    public static final int PG6_SEAS_OCEAN = 722;
    public static final int PG6_SEAS_BEACH = 723;
    
    public static final int PG6_SWMP_LAND = 724;
    public static final int PG6_SWMP_OCEAN = 725;
    public static final int PG6_SWMP_BEACH = 726;
    
    public static final int PG6_TAIG_LAND = 727;
    public static final int PG6_TAIG_OCEAN = 728;
    public static final int PG6_TAIG_BEACH = 729;
    
    public static final int PG6_TUND_LAND = 730;
    public static final int PG6_TUND_OCEAN = 731;
    public static final int PG6_TUND_BEACH = 732;
    
    public static final int PG6_S_SNOW_OFFSET = 750;
    public static final int PG6_B_CLIMATE_FEAT = 751;
    
    // Labels
    public static final int PG6_LAND_LABL = 1700;
    public static final int PG6_OCEAN_LABL = 1701;
    public static final int PG6_BEACH_LABL = 1702;
    
    public static final int PG6_DSRT_LABL = 1703;
    public static final int PG6_FRST_LABL = 1704;
    public static final int PG6_ICED_LABL = 1705;
    public static final int PG6_PLNS_LABL = 1706;
    public static final int PG6_RAIN_LABL = 1707;
    public static final int PG6_SAVA_LABL = 1708;
    public static final int PG6_SHRB_LABL = 1709;
    public static final int PG6_SEAS_LABL = 1710;
    public static final int PG6_SWMP_LABL = 1711;
    public static final int PG6_TAIG_LABL = 1712;
    public static final int PG6_TUND_LABL = 1713;
    
    /* Page 8 - Custom Properties */
    
    public static final int CUSTOM_INITIAL_ID = 5000;
    
    public static int offsetForward(int entry) {
        return entry + 100;
    }
    
    public static int offsetBackward(int entry) {
        return entry - 100;
    }
    
    public static void assertOffsets() {
        assertOffset(PG4_S_MAIN_NS_X, PG5_F_MAIN_NS_X);
        assertOffset(PG4_S_MAIN_NS_Y, PG5_F_MAIN_NS_Y);
        assertOffset(PG4_S_MAIN_NS_Z, PG5_F_MAIN_NS_Z);
        assertOffset(PG4_S_DPTH_NS_X, PG5_F_DPTH_NS_X);
        assertOffset(PG4_S_DPTH_NS_Z, PG5_F_DPTH_NS_Z);
        assertOffset(PG4_S_BASE_SIZE, PG5_F_BASE_SIZE);
        assertOffset(PG4_S_COORD_SCL, PG5_F_COORD_SCL);
        assertOffset(PG4_S_HEIGH_SCL, PG5_F_HEIGH_SCL);
        assertOffset(PG4_S_STRETCH_Y, PG5_F_STRETCH_Y);
        assertOffset(PG4_S_UPPER_LIM, PG5_F_UPPER_LIM);
        assertOffset(PG4_S_LOWER_LIM, PG5_F_LOWER_LIM);
        assertOffset(PG4_S_HEIGH_LIM, PG5_F_HEIGH_LIM);
        
        assertOffset(PG4_S_TEMP_SCL, PG5_F_TEMP_SCL);
        assertOffset(PG4_S_RAIN_SCL, PG5_F_RAIN_SCL);
        assertOffset(PG4_S_DETL_SCL, PG5_F_DETL_SCL);
        
        assertOffset(PG4_S_B_DPTH_WT, PG5_F_B_DPTH_WT);
        assertOffset(PG4_S_B_DPTH_OF, PG5_F_B_DPTH_OF);
        assertOffset(PG4_S_B_SCLE_WT, PG5_F_B_SCLE_WT);
        assertOffset(PG4_S_B_SCLE_OF, PG5_F_B_SCLE_OF);
        
        assertOffset(PG4_S_SCLE_NS_X, PG5_F_SCLE_NS_X);
        assertOffset(PG4_S_SCLE_NS_Z, PG5_F_SCLE_NS_Z);
        
        assertOffset(PG4_S_END_OF, PG5_F_END_OF);
        assertOffset(PG4_S_END_WT, PG5_F_END_WT);
        assertOffset(PG4_S_END_OUT_OF, PG5_F_END_OUT_OF);
        assertOffset(PG4_S_END_OUT_DT, PG5_F_END_OUT_DT);
    }

    private static void assertOffset(int sliderId, int fieldId) {
        if (sliderId != offsetBackward(fieldId)) {
            String errorStr = String.format("[Modern Beta] GUI slider id %d not correctly offset with field id %d!", sliderId, fieldId);
            
            throw new IllegalStateException(errorStr);
        }
    }
    
    static {
        BIOME_SETTINGS.put(GuiIdentifiers.PG0_B_FIXED, (str, factory) -> factory.singleBiome = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_DSRT_LAND, (str, factory) -> factory.desertBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_DSRT_OCEAN, (str, factory) -> factory.desertBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_DSRT_BEACH, (str, factory) -> factory.desertBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_FRST_LAND, (str, factory) -> factory.forestBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_FRST_OCEAN, (str, factory) -> factory.forestBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_FRST_BEACH, (str, factory) -> factory.forestBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_ICED_LAND, (str, factory) -> factory.iceDesertBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_ICED_OCEAN, (str, factory) -> factory.iceDesertBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_ICED_BEACH, (str, factory) -> factory.iceDesertBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_PLNS_LAND, (str, factory) -> factory.plainsBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_PLNS_OCEAN, (str, factory) -> factory.plainsBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_PLNS_BEACH, (str, factory) -> factory.plainsBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_RAIN_LAND, (str, factory) -> factory.rainforestBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_RAIN_OCEAN, (str, factory) -> factory.rainforestBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_RAIN_BEACH, (str, factory) -> factory.rainforestBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SAVA_LAND, (str, factory) -> factory.savannaBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SAVA_OCEAN, (str, factory) -> factory.savannaBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SAVA_BEACH, (str, factory) -> factory.savannaBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SHRB_LAND, (str, factory) -> factory.shrublandBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SHRB_OCEAN, (str, factory) -> factory.shrublandBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SHRB_BEACH, (str, factory) -> factory.shrublandBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SEAS_LAND, (str, factory) -> factory.seasonalForestBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SEAS_OCEAN, (str, factory) -> factory.seasonalForestBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SEAS_BEACH, (str, factory) -> factory.seasonalForestBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SWMP_LAND, (str, factory) -> factory.swamplandBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SWMP_OCEAN, (str, factory) -> factory.swamplandBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_SWMP_BEACH, (str, factory) -> factory.swamplandBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_TAIG_LAND, (str, factory) -> factory.taigaBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_TAIG_OCEAN, (str, factory) -> factory.taigaBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_TAIG_BEACH, (str, factory) -> factory.taigaBiomeBeach = str);
        
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_TUND_LAND, (str, factory) -> factory.tundraBiomeBase = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_TUND_OCEAN, (str, factory) -> factory.tundraBiomeOcean = str);
        BIOME_SETTINGS.put(GuiIdentifiers.PG6_TUND_BEACH, (str, factory) -> factory.tundraBiomeBeach = str);
        
        BASE_SETTINGS.put(GuiIdentifiers.PG0_B_CHUNK, (str, factory) -> factory.chunkSource = str);
        BASE_SETTINGS.put(GuiIdentifiers.PG0_B_BIOME, (str, factory) -> factory.biomeSource = str);
        BASE_SETTINGS.put(GuiIdentifiers.PG0_B_SURFACE, (str, factory) -> factory.surfaceBuilder = str);
        BASE_SETTINGS.put(GuiIdentifiers.PG0_B_CARVER, (str, factory) -> factory.caveCarver = str);
        BASE_SETTINGS.put(GuiIdentifiers.PG0_B_SPAWN, (str, factory) -> factory.worldSpawner = str);
        BASE_SETTINGS.put(GuiIdentifiers.PG0_B_BLOCK, (str, factory) -> factory.defaultBlock = str);
        BASE_SETTINGS.put(GuiIdentifiers.PG0_B_FLUID, (str, factory) -> factory.defaultFluid = str);
    }
}
