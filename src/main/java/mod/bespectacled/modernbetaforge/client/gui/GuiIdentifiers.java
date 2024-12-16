package mod.bespectacled.modernbetaforge.client.gui;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings.Factory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIdentifiers {
    public static final Map<String, List<Integer>> CHUNK_SETTINGS = new LinkedHashMap<>();
    public static final Map<Integer, BiConsumer<String, ModernBetaGeneratorSettings.Factory>> GUI_BIOMES = new HashMap<>();
    public static final Map<Integer, BiPredicate<ModernBetaGeneratorSettings.Factory, Integer>> GUI_IDS = new HashMap<>();
    
    private static final MapGenStronghold STRONGHOLD = new MapGenStronghold();
    
    /* Function Buttons */
    
    public static final int FUNC_DONE = 60;
    public static final int FUNC_RAND = 61;
    public static final int FUNC_PREV = 62;
    public static final int FUNC_NEXT = 63;
    public static final int FUNC_DFLT = 64;
    public static final int FUNC_PRST = 65;
    public static final int FUNC_CONF = 66;
    public static final int FUNC_CNCL = 67;
    public static final int FUNC_FRST = 70;
    public static final int FUNC_LAST = 71;
    
    /* Page 1 */
    
    // Entries
    public static final int PG0_S_CHUNK = 100;
    public static final int PG0_S_BIOME = 101;
    public static final int PG0_S_SURFACE = 102;
    public static final int PG0_S_CARVER = 103;
    
    public static final int PG0_B_FIXED = 104;
    
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
    public static final int PG0_S_LEVEL_HOUSE = 134;
    public static final int PG0_B_USE_INDEV_CAVES = 135;
    
    public static final int PG0_B_USE_INFDEV_WALLS = 136;
    public static final int PG0_B_USE_INFDEV_PYRAMIDS = 137;
    
    public static final int PG0_S_CAVE_HEIGHT = 138;
    public static final int PG0_S_CAVE_COUNT = 139;
    public static final int PG0_S_CAVE_CHANCE = 140;
    
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
    public static final int PG3_S_BIOME_SZ = 418;

    public static final int PG3_S_TEMP_SCL = 419;
    public static final int PG3_S_RAIN_SCL = 420;
    public static final int PG3_S_DETL_SCL = 421;

    public static final int PG3_B_USE_BDS = 450;
    
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
    public static final int PG4_F_RIVER_SZ = 517;
    public static final int PG4_F_BIOME_SZ = 518;

    public static final int PG4_F_TEMP_SCL = 519;
    public static final int PG4_F_RAIN_SCL = 520;
    public static final int PG4_F_DETL_SCL = 521;
    
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
    
    /* Page 7 - Custom Properties */
    
    public static final int CUSTOM_INITIAL_ID = 5000;
    
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
    
    public static Set<Integer> getBiomeGuiIds() {
        return GUI_BIOMES.keySet();
    }
    
    public static void updateBiomeSetting(int id, String registryName, Factory factory) {
        GUI_BIOMES.get(id).accept(registryName, factory);
    }
    
    public static Set<Integer> getGuiIds() {
        return GUI_IDS.keySet();
    }
    
    public static boolean testGuiEnabled(Factory factory, int id) {
        if (GUI_IDS.containsKey(id)) {
            return GUI_IDS.get(id).test(factory, id);
        }
        
        return false;
    }
    
    private static void assertOffset(int sliderId, int fieldId) {
        if (sliderId != offsetBackward(fieldId)) {
            String errorStr = String.format("[Modern Beta] GUI slider id %d not correctly offset with field id %d!", sliderId, fieldId);
            
            throw new IllegalStateException(errorStr);
        }
    }
    
    private static void add(int id) {
        add(id, (factory, guiId) -> true);
    }
    
    private static void add(int id, BiPredicate<ModernBetaGeneratorSettings.Factory, Integer> predicate) {
        if (GUI_IDS.containsKey(id)) {
            String errorStr = String.format("[Modern Beta] GUI id %d has already been registered!", id);
            
            throw new IllegalArgumentException(errorStr);
        }
        
        GUI_IDS.put(id, predicate);
    }
    
    static {
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.BETA.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_DPTH_NS_X,
                GuiIdentifiers.PG3_S_DPTH_NS_Z,
                GuiIdentifiers.PG3_S_BASE_SIZE,
                GuiIdentifiers.PG3_S_COORD_SCL,
                GuiIdentifiers.PG3_S_HEIGH_SCL,
                GuiIdentifiers.PG3_S_STRETCH_Y,
                GuiIdentifiers.PG3_S_UPPER_LIM,
                GuiIdentifiers.PG3_S_LOWER_LIM,
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.ALPHA.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_DPTH_NS_X,
                GuiIdentifiers.PG3_S_DPTH_NS_Z,
                GuiIdentifiers.PG3_S_BASE_SIZE,
                GuiIdentifiers.PG3_S_COORD_SCL,
                GuiIdentifiers.PG3_S_HEIGH_SCL,
                GuiIdentifiers.PG3_S_STRETCH_Y,
                GuiIdentifiers.PG3_S_UPPER_LIM,
                GuiIdentifiers.PG3_S_LOWER_LIM,
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_611.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_DPTH_NS_X,
                GuiIdentifiers.PG3_S_DPTH_NS_Z,
                GuiIdentifiers.PG3_S_BASE_SIZE,
                GuiIdentifiers.PG3_S_COORD_SCL,
                GuiIdentifiers.PG3_S_HEIGH_SCL,
                GuiIdentifiers.PG3_S_STRETCH_Y,
                GuiIdentifiers.PG3_S_UPPER_LIM,
                GuiIdentifiers.PG3_S_LOWER_LIM,
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_420.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_BASE_SIZE,
                GuiIdentifiers.PG3_S_COORD_SCL,
                GuiIdentifiers.PG3_S_HEIGH_SCL,
                GuiIdentifiers.PG3_S_STRETCH_Y,
                GuiIdentifiers.PG3_S_UPPER_LIM,
                GuiIdentifiers.PG3_S_LOWER_LIM,
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_415.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_COORD_SCL,
                GuiIdentifiers.PG3_S_HEIGH_SCL,
                GuiIdentifiers.PG3_S_UPPER_LIM,
                GuiIdentifiers.PG3_S_LOWER_LIM,
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_227.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.SKYLANDS.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_DPTH_NS_X,
                GuiIdentifiers.PG3_S_DPTH_NS_Z,
                GuiIdentifiers.PG3_S_COORD_SCL,
                GuiIdentifiers.PG3_S_HEIGH_SCL,
                GuiIdentifiers.PG3_S_UPPER_LIM,
                GuiIdentifiers.PG3_S_LOWER_LIM,
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );

        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.PE.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_DPTH_NS_X,
                GuiIdentifiers.PG3_S_DPTH_NS_Z,
                GuiIdentifiers.PG3_S_BASE_SIZE,
                GuiIdentifiers.PG3_S_COORD_SCL,
                GuiIdentifiers.PG3_S_HEIGH_SCL,
                GuiIdentifiers.PG3_S_STRETCH_Y,
                GuiIdentifiers.PG3_S_UPPER_LIM,
                GuiIdentifiers.PG3_S_LOWER_LIM,
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.RELEASE.id,
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_DPTH_NS_X,
                GuiIdentifiers.PG3_S_DPTH_NS_Z,
                GuiIdentifiers.PG3_S_BASE_SIZE,
                GuiIdentifiers.PG3_S_COORD_SCL,
                GuiIdentifiers.PG3_S_HEIGH_SCL,
                GuiIdentifiers.PG3_S_STRETCH_Y,
                GuiIdentifiers.PG3_S_UPPER_LIM,
                GuiIdentifiers.PG3_S_LOWER_LIM,
                GuiIdentifiers.PG3_S_HEIGH_LIM,
                
                GuiIdentifiers.PG3_S_B_DPTH_WT,
                GuiIdentifiers.PG3_S_B_DPTH_OF,
                GuiIdentifiers.PG3_S_B_SCL_WT,
                GuiIdentifiers.PG3_S_B_SCL_OF,
                GuiIdentifiers.PG3_S_BIOME_SZ,
                GuiIdentifiers.PG3_S_RIVER_SZ,
                
                GuiIdentifiers.PG3_B_USE_BDS
            )
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INDEV.id,
            ImmutableList.of()
        );
        
        CHUNK_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.id,
            ImmutableList.of()
        );
        
        GUI_BIOMES.put(GuiIdentifiers.PG0_B_FIXED, (str, factory) -> factory.singleBiome = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_DSRT_LAND, (str, factory) -> factory.desertBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_DSRT_OCEAN, (str, factory) -> factory.desertBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_DSRT_BEACH, (str, factory) -> factory.desertBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_FRST_LAND, (str, factory) -> factory.forestBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_FRST_OCEAN, (str, factory) -> factory.forestBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_FRST_BEACH, (str, factory) -> factory.forestBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_ICED_LAND, (str, factory) -> factory.iceDesertBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_ICED_OCEAN, (str, factory) -> factory.iceDesertBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_ICED_BEACH, (str, factory) -> factory.iceDesertBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_PLNS_LAND, (str, factory) -> factory.plainsBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_PLNS_OCEAN, (str, factory) -> factory.plainsBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_PLNS_BEACH, (str, factory) -> factory.plainsBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_RAIN_LAND, (str, factory) -> factory.rainforestBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_RAIN_OCEAN, (str, factory) -> factory.rainforestBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_RAIN_BEACH, (str, factory) -> factory.rainforestBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_SAVA_LAND, (str, factory) -> factory.savannaBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_SAVA_OCEAN, (str, factory) -> factory.savannaBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_SAVA_BEACH, (str, factory) -> factory.savannaBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_SHRB_LAND, (str, factory) -> factory.shrublandBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_SHRB_OCEAN, (str, factory) -> factory.shrublandBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_SHRB_BEACH, (str, factory) -> factory.shrublandBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_SEAS_LAND, (str, factory) -> factory.seasonalForestBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_SEAS_OCEAN, (str, factory) -> factory.seasonalForestBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_SEAS_BEACH, (str, factory) -> factory.seasonalForestBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_SWMP_LAND, (str, factory) -> factory.swamplandBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_SWMP_OCEAN, (str, factory) -> factory.swamplandBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_SWMP_BEACH, (str, factory) -> factory.swamplandBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_TAIG_LAND, (str, factory) -> factory.taigaBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_TAIG_OCEAN, (str, factory) -> factory.taigaBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_TAIG_BEACH, (str, factory) -> factory.taigaBiomeBeach = str);
        
        GUI_BIOMES.put(GuiIdentifiers.PG5_TUND_LAND, (str, factory) -> factory.tundraBiomeBase = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_TUND_OCEAN, (str, factory) -> factory.tundraBiomeOcean = str);
        GUI_BIOMES.put(GuiIdentifiers.PG5_TUND_BEACH, (str, factory) -> factory.tundraBiomeBeach = str);
        
        BiPredicate<Factory, Integer> testSurface = (factory, id) -> {
            String chunkSource = factory.chunkSource;
            
            boolean isSkylands = chunkSource.equals(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id);
            boolean isIndev = chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.id);
            boolean isClassic = factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.id);
            
            return !(isIndev || isClassic) && !isSkylands;
        };
        BiPredicate<Factory, Integer> testCarver = (factory, id) -> factory.useCaves;
        BiPredicate<Factory, Integer> testFixed = (factory, id) -> factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
        BiPredicate<Factory, Integer> testBiomeReplacement = (factory, id) -> {
            boolean isFixedBiomeSource = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean isSkylands = factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.SKYLANDS.id);
            
            return !isFixedBiomeSource && !isSkylands;
        };
        BiPredicate<Factory, Integer> testClassicNetherBoP = (factory, id) -> !ModCompat.isBoPLoaded();
        BiPredicate<Factory, Integer> testClassicNether = (factory, id) -> factory.useOldNether && !ModCompat.isBoPLoaded();
        BiPredicate<Factory, Integer> testDungeons = (factory, id) -> factory.useDungeons;
        BiPredicate<Factory, Integer> testWaterLakes = (factory, id) -> factory.useWaterLakes;
        BiPredicate<Factory, Integer> testLavaLakes = (factory, id) -> factory.useLavaLakes;
        BiPredicate<Factory, Integer> testVillageVariants = (factory, id) -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean hasVillages = isFixed ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
            
            return hasVillages && factory.useVillages;
        };
        BiPredicate<Factory, Integer> testSandstone = (factory, id) -> {
            boolean isReleaseSurface = factory.surfaceBuilder.equals(ModernBetaBuiltInTypes.Surface.RELEASE.id);
            boolean isIndev = factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.id);
            boolean isClassic = factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.id);
            
            return !isReleaseSurface && !(isIndev || isClassic);
        };
        BiPredicate<Factory, Integer> testIndev = (factory, id) -> factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.id);
        BiPredicate<Factory, Integer> testFinite = (factory, id) -> {
            boolean isIndev = factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.id);
            boolean isClassic = factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.id);
            
            return isIndev || isClassic;
        };
        BiPredicate<Factory, Integer> testInfdev227 = (factory, id) -> factory.chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INFDEV_227.id);
        BiPredicate<Factory, Integer> testReleaseBiomeSource = (factory, id) -> factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.id);
        BiPredicate<Factory, Integer> testBetaBiomeFeature = (factory, id) -> {
            String biomeSource = factory.biomeSource;
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            
            boolean isBetaOrPEBiomeSource = 
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
            boolean isFixedBiomeSource = biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean isBetaBiome = biome instanceof BiomeBeta;
            
            return isBetaOrPEBiomeSource || (isFixedBiomeSource && isBetaBiome);
        };
        BiPredicate<Factory, Integer> testModernBetaBiomeFeature = (factory, id) -> {
            String biomeSource = factory.biomeSource;
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            
            boolean isBetaOrPEBiomeSource = 
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
            boolean isFixedBiomeSource = biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean isModernBetaBiome = biome instanceof ModernBetaBiome;
            
            return isBetaOrPEBiomeSource || (isFixedBiomeSource && isModernBetaBiome);
        };
        BiPredicate<Factory, Integer> testModernBetaOre = (factory, id) -> {
            String biomeSource = factory.biomeSource;
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));

            boolean isReleaseBiomeSource = biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.id);
            boolean isFixedBiomeSource = biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            boolean isModernBetaBiome = biome instanceof ModernBetaBiome;
            
            return (isFixedBiomeSource && isModernBetaBiome) ||  (!isReleaseBiomeSource && !isFixedBiomeSource);
        };
        BiPredicate<Factory, Integer> testStrongholds = (factory, id) -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? STRONGHOLD.allowedBiomes.contains(biome) : true;
        };
        BiPredicate<Factory, Integer> testVillages = (factory, id) -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
        };
        BiPredicate<Factory, Integer> testTemples = (factory, id) -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? MapGenScatteredFeature.BIOMELIST.contains(biome) : true;
        };
        BiPredicate<Factory, Integer> testMonuments = (factory, id) -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? StructureOceanMonument.SPAWN_BIOMES.contains(biome) : true;
        };
        BiPredicate<Factory, Integer> testMansions = (factory, id) -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean isFixed = factory.biomeSource.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id);
            
            return isFixed ? WoodlandMansion.ALLOWED_BIOMES.contains(biome) : true;
        };
        BiPredicate<Factory, Integer> testCustomBetaBiomeBase = (factory, id) -> {
            String biomeSource = factory.biomeSource;
            boolean isBetaOrPEBiomeSource = 
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
            
            return isBetaOrPEBiomeSource;
        };
        BiPredicate<Factory, Integer> testCustomBetaBiomeOcean = (factory, id) -> {
            String biomeSource = factory.biomeSource;
            boolean isBetaOrPEBiomeSource = 
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
            
            return isBetaOrPEBiomeSource && factory.replaceOceanBiomes;
        };
        BiPredicate<Factory, Integer> testCustomBetaBiomeBeach = (factory, id) -> {
            String biomeSource = factory.biomeSource;
            boolean isBetaOrPEBiomeSource = 
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
            
            return isBetaOrPEBiomeSource && factory.replaceBeachBiomes;
        };
        BiPredicate<Factory, Integer> testBiomeSize = (factory, id) -> {
            String chunkSource = factory.chunkSource;
            String biomeSource = factory.biomeSource;
            
            return chunkSource.equals(ModernBetaBuiltInTypes.Chunk.RELEASE.id) || biomeSource.equals(ModernBetaBuiltInTypes.Biome.RELEASE.id);
        };
        BiPredicate<Factory, Integer> testClimateScales = (factory, id) -> {
            String chunkSource = factory.chunkSource;
            String biomeSource = factory.biomeSource;

            return 
                chunkSource.equals(ModernBetaBuiltInTypes.Chunk.BETA.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.BETA.id) ||
                chunkSource.equals(ModernBetaBuiltInTypes.Chunk.PE.id) ||
                biomeSource.equals(ModernBetaBuiltInTypes.Biome.PE.id);
        };
        BiPredicate<Factory, Integer> testChunkSettings = (factory, id) -> {
            boolean enabled = false;
            List<Integer> settings = CHUNK_SETTINGS.getOrDefault(factory.chunkSource, ImmutableList.of());
            
            if (id >= GuiIdentifiers.PG3_S_MAIN_NS_X && id <= GuiIdentifiers.PG3_B_USE_BDS) {
                enabled = settings.contains(id);
            } else if (id >= offsetForward(GuiIdentifiers.PG3_S_MAIN_NS_X) && id <= offsetForward(GuiIdentifiers.PG3_B_USE_BDS)) {
                enabled = settings.contains(offsetBackward(id));
            }
            
            return enabled;
        };
        BiPredicate<Factory, Integer> testCarverSettings = (factory, id) -> {
            String caveCarver = factory.caveCarver;
            
            return !caveCarver.equals(ModernBetaBuiltInTypes.Carver.RELEASE.id) && factory.useCaves;
        };
        
        add(PG0_S_CHUNK);
        add(PG0_S_BIOME);
        add(PG0_S_SURFACE, testSurface);
        add(PG0_S_CARVER, testCarver);
        
        add(PG0_B_FIXED, testFixed);
        
        add(PG0_B_USE_OCEAN, testBiomeReplacement);
        add(PG0_B_USE_BEACH, testBiomeReplacement);
        
        add(PG0_S_SEA_LEVEL, testSurface);
        add(PG0_B_USE_CAVES);
        add(PG0_S_CAVE_HEIGHT, testCarverSettings);
        add(PG0_S_CAVE_COUNT, testCarverSettings);
        add(PG0_S_CAVE_CHANCE, testCarverSettings);
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
        add(PG0_S_LEVEL_WIDTH, testFinite);
        add(PG0_S_LEVEL_LENGTH, testFinite);
        add(PG0_S_LEVEL_HEIGHT, testFinite);
        add(PG0_S_LEVEL_HOUSE, testFinite);
        add(PG0_B_USE_INDEV_CAVES, testFinite);
        
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
        
        add(PG3_S_MAIN_NS_X, testChunkSettings);
        add(PG3_S_MAIN_NS_Y, testChunkSettings);
        add(PG3_S_MAIN_NS_Z, testChunkSettings);
        add(PG3_S_DPTH_NS_X, testChunkSettings);
        add(PG3_S_DPTH_NS_Z, testChunkSettings);
        add(PG3_S_BASE_SIZE, testChunkSettings);
        add(PG3_S_COORD_SCL, testChunkSettings);
        add(PG3_S_HEIGH_SCL, testChunkSettings);
        add(PG3_S_STRETCH_Y, testChunkSettings);
        add(PG3_S_UPPER_LIM, testChunkSettings);
        add(PG3_S_LOWER_LIM, testChunkSettings);
        add(PG3_S_HEIGH_LIM, testChunkSettings);
        
        add(PG3_S_TEMP_SCL, testClimateScales);
        add(PG3_S_RAIN_SCL, testClimateScales);
        add(PG3_S_DETL_SCL, testClimateScales);
        
        add(PG3_S_B_DPTH_WT, testChunkSettings);
        add(PG3_S_B_DPTH_OF, testChunkSettings);
        add(PG3_S_B_SCL_WT, testChunkSettings);
        add(PG3_S_B_SCL_OF, testChunkSettings);
        add(PG3_S_BIOME_SZ, testBiomeSize);
        add(PG3_S_RIVER_SZ, testChunkSettings);
        
        add(PG3_B_USE_BDS, testChunkSettings);
        
        add(PG4_F_MAIN_NS_X, testChunkSettings);
        add(PG4_F_MAIN_NS_Y, testChunkSettings);
        add(PG4_F_MAIN_NS_Z, testChunkSettings);
        add(PG4_F_DPTH_NS_X, testChunkSettings);
        add(PG4_F_DPTH_NS_Z, testChunkSettings);
        add(PG4_F_BASE_SIZE, testChunkSettings);
        add(PG4_F_COORD_SCL, testChunkSettings);
        add(PG4_F_HEIGH_SCL, testChunkSettings);
        add(PG4_F_STRETCH_Y, testChunkSettings);
        add(PG4_F_UPPER_LIM, testChunkSettings);
        add(PG4_F_LOWER_LIM, testChunkSettings);
        add(PG4_F_HEIGH_LIM, testChunkSettings);
        
        add(PG4_F_TEMP_SCL, testClimateScales);
        add(PG4_F_RAIN_SCL, testClimateScales);
        add(PG4_F_DETL_SCL, testClimateScales);
        
        add(PG4_F_B_DPTH_WT, testChunkSettings);
        add(PG4_F_B_DPTH_OF, testChunkSettings);
        add(PG4_F_B_SCL_WT, testChunkSettings);
        add(PG4_F_B_SCL_OF, testChunkSettings);
        add(PG4_F_BIOME_SZ, testBiomeSize);
        add(PG4_F_RIVER_SZ, testChunkSettings);
        
        add(PG5_DSRT_LAND, testCustomBetaBiomeBase);
        add(PG5_DSRT_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_DSRT_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_FRST_LAND, testCustomBetaBiomeBase);
        add(PG5_FRST_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_FRST_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_ICED_LAND, testCustomBetaBiomeBase);
        add(PG5_ICED_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_ICED_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_PLNS_LAND, testCustomBetaBiomeBase);
        add(PG5_PLNS_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_PLNS_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_RAIN_LAND, testCustomBetaBiomeBase);
        add(PG5_RAIN_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_RAIN_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_SAVA_LAND, testCustomBetaBiomeBase);
        add(PG5_SAVA_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_SAVA_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_SHRB_LAND, testCustomBetaBiomeBase);
        add(PG5_SHRB_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_SHRB_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_SEAS_LAND, testCustomBetaBiomeBase);
        add(PG5_SEAS_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_SEAS_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_SWMP_LAND, testCustomBetaBiomeBase);
        add(PG5_SWMP_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_SWMP_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_TAIG_LAND, testCustomBetaBiomeBase);
        add(PG5_TAIG_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_TAIG_BEACH, testCustomBetaBiomeBeach);
        
        add(PG5_TUND_LAND, testCustomBetaBiomeBase);
        add(PG5_TUND_OCEAN, testCustomBetaBiomeOcean);
        add(PG5_TUND_BEACH, testCustomBetaBiomeBeach);
    }
}
