package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorGuiLabel;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevTheme;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizeWorld extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder {
    private static final int[] LEVEL_WIDTHS = { 64, 128, 256, 512, 768, 1024, 1536, 2048, 2560 };
    private static final int[] LEVEL_HEIGHTS = { 64, 96, 128, 160, 192, 224, 256 };
    private static final String PREFIX = "createWorld.customize.custom.";
    private static final int PAGELIST_ADDITIONAL_WIDTH = 96;
    
    private static final int MIN_CAVE_HEIGHT = 9;
    private static final int MAX_CAVE_HEIGHT = 255;
    private static final int MIN_CAVE_COUNT = 1;
    private static final int MAX_CAVE_COUNT = 100;
    private static final int MIN_CAVE_CHANCE = 1;
    private static final int MAX_CAVE_CHANCE = 100;
    private static final float MAX_HEIGHT = 255.0f;
    private static final float MIN_BIOME_SCALE = 0.1f;
    private static final float MAX_BIOME_SCALE = 8.0f;
    private static final float MIN_BIOME_WEIGHT = 1.0f;
    private static final float MAX_BIOME_WEIGHT = 20.0f;
    private static final float MIN_BIOME_OFFSET = 0.0f;
    private static final float MAX_BIOME_OFFSET = 20.0f;
    private static final int MIN_BIOME_SIZE = 1;
    private static final int MAX_BIOME_SIZE = 8;
    private static final int MIN_RIVER_SIZE = 1;
    private static final int MAX_RIVER_SIZE = 5;
    
    private final GuiCreateWorld parent;
    
    private final Predicate<String> floatFilter;
    private final Predicate<String> intFilter;
    private final Predicate<String> intBiomeSizeFilter;
    private final Predicate<String> intRiverSizeFilter;
    
    private final ModernBetaChunkGeneratorSettings.Factory defaultSettings;
    private ModernBetaChunkGeneratorSettings.Factory settings;
    
    private final Random random;
    
    protected String title;
    protected String subtitle;
    protected String pageTitle;
    protected String[] pageNames;
    
    private GuiPageButtonList pageList;
    private GuiButton done;
    private GuiButton randomize;
    private GuiButton defaults;
    private GuiButton previousPage;
    private GuiButton nextPage;
    private GuiButton confirm;
    private GuiButton cancel;
    private GuiButton presets;
    
    private boolean settingsModified;
    private int confirmMode;
    private boolean confirmDismissed;
    
    private boolean clicked;
    private boolean randomClicked;

    public GuiScreenCustomizeWorld(GuiScreen guiScreen, String string) {
        this.title = "Customize World Settings";
        this.subtitle = "Page 1 of 6";
        this.pageTitle = "Basic Settings";
        this.pageNames = new String[]{
            I18n.format(PREFIX + "page0"),
            I18n.format(PREFIX + "page5"),
            I18n.format(PREFIX + "page1"),
            I18n.format(PREFIX + "page2"),
            I18n.format(PREFIX + "page3"),
            I18n.format(PREFIX + "page4")
        };
        
        this.floatFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                Float entryValue = Floats.tryParse(entryString);
                
                return entryString.isEmpty() || (entryValue != null && Floats.isFinite(entryValue) && entryValue >= 0.0f);
            }
        };
        
        this.intFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                Integer entryValue = Ints.tryParse(entryString);
                
                return entryString.isEmpty() || (entryValue != null && entryValue >= 0 && entryValue <= (int)MAX_HEIGHT);
            }
        };
        
        this.intBiomeSizeFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                Integer entryValue = Ints.tryParse(entryString);
                
                return entryString.isEmpty() || (entryValue != null && entryValue >= (int)MIN_BIOME_SIZE && entryValue <= (int)MAX_BIOME_SIZE);
            }
        };
        
        this.intRiverSizeFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                Integer entryValue = Ints.tryParse(entryString);
                
                return entryString.isEmpty() || (entryValue != null && entryValue >= (int)MIN_RIVER_SIZE && entryValue <= (int)MAX_RIVER_SIZE);
            }
        };
        
        String defaultPreset = ModernBetaConfig.guiOptions.defaultPreset;
        this.defaultSettings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(defaultPreset);
        this.random = new Random();
        this.parent = (GuiCreateWorld)guiScreen;
        
        this.loadValues(string);
    }
    
    private void createPagedList() {
        int chunkSourceId = ModernBetaRegistries.CHUNK.getKeys().indexOf(this.settings.chunkSource);
        int biomeSourceId = ModernBetaRegistries.BIOME.getKeys().indexOf(this.settings.biomeSource);
        int surfaceBuilderId = ModernBetaRegistries.SURFACE.getKeys().indexOf(this.settings.surfaceBuilder);
        int caveCarverId = ModernBetaRegistries.CARVER.getKeys().indexOf(this.settings.caveCarver);
        
        int levelThemeId = IndevTheme.fromId(this.settings.levelTheme).ordinal();
        int levelTypeId = IndevType.fromId(this.settings.levelType).ordinal();
        int levelWidth = getNdx(LEVEL_WIDTHS, this.settings.levelWidth);
        int levelLength = getNdx(LEVEL_WIDTHS, this.settings.levelLength);
        int levelHeight = getNdx(LEVEL_HEIGHTS, this.settings.levelHeight);
        int levelHouseId = IndevHouse.fromId(this.settings.levelHouse).ordinal();
        int levelSeaLevel = this.getLevelSeaLevel();
        String levelSeaLevelStr = levelSeaLevel == -1 ? "" : Integer.toString(levelSeaLevel);
        
        List<String> loadedMods = new ArrayList<>(ModCompat.LOADED_MODS.keySet());
        StringBuilder loadedModsList = new StringBuilder();
        
        if (loadedMods.isEmpty()) { 
            loadedModsList.append("n/a");
        } else if (loadedMods.size() > 0) {
            loadedModsList.append(loadedMods.get(0));
        }
        
        GuiPageButtonList.GuiListEntry[] pageList0 = {
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_CHUNK, I18n.format(PREFIX + NbtTags.CHUNK_SOURCE), true, this, 0f, ModernBetaRegistries.CHUNK.getKeys().size() - 1, chunkSourceId),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_BIOME, I18n.format(PREFIX + NbtTags.BIOME_SOURCE), true, this, 0f, ModernBetaRegistries.BIOME.getKeys().size() - 1, biomeSourceId),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_SURFACE, I18n.format(PREFIX + NbtTags.SURFACE_BUILDER), true, this, 0f, ModernBetaRegistries.SURFACE.getKeys().size() - 1, surfaceBuilderId),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_FIXED, I18n.format(PREFIX + "fixedBiome"), true, true),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_CARVER, I18n.format(PREFIX + NbtTags.CAVE_CARVER), true, this, 0f, ModernBetaRegistries.CARVER.getKeys().size() - 1, caveCarverId),
            null,

            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG0_L_BIOME_REPLACEMENT, I18n.format(PREFIX + "biomeReplacementLabel"), true),
            null,
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_OCEAN, I18n.format(PREFIX + NbtTags.REPLACE_OCEAN_BIOMES), true, this.settings.replaceOceanBiomes),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_BEACH, I18n.format(PREFIX + NbtTags.REPLACE_BEACH_BIOMES), true, this.settings.replaceBeachBiomes),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG0_L_BASIC_FEATURES, I18n.format(PREFIX + "overworldFeaturesLabel"), true),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_SEA_LEVEL, I18n.format(PREFIX + "seaLevel"), true, this, 0.0f, MAX_HEIGHT, (float)this.settings.seaLevel),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_SANDSTONE, I18n.format(PREFIX + NbtTags.USE_SANDSTONE), true, this.settings.useSandstone),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_CAVES, I18n.format(PREFIX + "useCaves"), true, this.settings.useCaves),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_CAVE_HEIGHT, I18n.format(PREFIX + NbtTags.CAVE_HEIGHT), true, this, MIN_CAVE_HEIGHT, MAX_CAVE_HEIGHT, (float)this.settings.caveHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_CAVE_COUNT, I18n.format(PREFIX + NbtTags.CAVE_COUNT), true, this, MIN_CAVE_COUNT, MAX_CAVE_COUNT, (float)this.settings.caveCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_CAVE_CHANCE, I18n.format(PREFIX + NbtTags.CAVE_CHANCE), true, this, MIN_CAVE_CHANCE, MAX_CAVE_CHANCE, (float)this.settings.caveChance),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_RAVINES, I18n.format(PREFIX + "useRavines"), true, this.settings.useRavines),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_SHAFTS, I18n.format(PREFIX + "useMineShafts"), true, this.settings.useMineShafts),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_VILLAGES, I18n.format(PREFIX + "useVillages"), true, this.settings.useVillages),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_VILLAGE_VARIANTS, I18n.format(PREFIX + NbtTags.USE_VILLAGE_VARIANTS), true, this.settings.useVillageVariants),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_HOLDS, I18n.format(PREFIX + "useStrongholds"), true, this.settings.useStrongholds),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_TEMPLES, I18n.format(PREFIX + "useTemples"), true, this.settings.useTemples),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_MONUMENTS, I18n.format(PREFIX + "useMonuments"), true, this.settings.useMonuments),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_MANSIONS, I18n.format(PREFIX + "useMansions"), true, this.settings.useMansions),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_DUNGEONS, I18n.format(PREFIX + "useDungeons"), true, this.settings.useDungeons),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_DUNGEON_CHANCE, I18n.format(PREFIX + "dungeonChance"), true, this, 1.0f, 100.0f, (float)this.settings.dungeonChance),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_WATER_LAKES, I18n.format(PREFIX + "useWaterLakes"), true, this.settings.useWaterLakes),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_WATER_LAKE_CHANCE, I18n.format(PREFIX + "waterLakeChance"), true, this, 1.0f, 100.0f, (float)this.settings.waterLakeChance),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_LAVA_LAKES, I18n.format(PREFIX + "useLavaLakes"), true, this.settings.useLavaLakes),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_LAVA_LAKE_CHANCE, I18n.format(PREFIX + "lavaLakeChance"), true, this, 10.0f, 100.0f, (float)this.settings.lavaLakeChance),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_LAVA_OCEANS, I18n.format(PREFIX + "useLavaOceans"), true, this.settings.useLavaOceans),
            null,
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG0_L_INFDEV_227_FEATURES, I18n.format(PREFIX + "infdev227FeaturesLabel"), true),
            null,
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_INFDEV_WALLS, I18n.format(PREFIX + NbtTags.USE_INFDEV_WALLS), true, this.settings.useInfdevWalls),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_INFDEV_PYRAMIDS, I18n.format(PREFIX + NbtTags.USE_INFDEV_PYRAMIDS), true, this.settings.useInfdevPyramids),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG0_L_INDEV_FEATURES, I18n.format(PREFIX + "indevFeaturesLabel"), true),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_LEVEL_THEME, I18n.format(PREFIX + NbtTags.LEVEL_THEME), true, this, 0f, IndevTheme.values().length - 1, levelThemeId),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_LEVEL_TYPE, I18n.format(PREFIX + NbtTags.LEVEL_TYPE), true, this, 0f, IndevType.values().length - 1, levelTypeId),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_LEVEL_WIDTH, I18n.format(PREFIX + NbtTags.LEVEL_WIDTH), true, this, 0f, LEVEL_WIDTHS.length - 1, levelWidth),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_LEVEL_LENGTH, I18n.format(PREFIX + NbtTags.LEVEL_LENGTH), true, this, 0f, LEVEL_WIDTHS.length - 1, levelLength),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_LEVEL_HEIGHT, I18n.format(PREFIX + NbtTags.LEVEL_HEIGHT), true, this, 0f, LEVEL_HEIGHTS.length - 1, levelHeight),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG0_L_INDEV_SEA_LEVEL, String.format("%s: %s", I18n.format(PREFIX + "seaLevel"), levelSeaLevelStr), true),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG0_S_LEVEL_HOUSE, I18n.format(PREFIX + NbtTags.LEVEL_HOUSE), true, this, 0f, IndevHouse.values().length - 1, levelHouseId),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_INDEV_CAVES, I18n.format(PREFIX + NbtTags.USE_INDEV_CAVES), true, this.settings.useIndevCaves),
        
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG0_L_NETHER_FEATURES, I18n.format(PREFIX + "netherFeaturesLabel"), true),
            null,
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_OLD_NETHER, I18n.format(PREFIX + NbtTags.USE_OLD_NETHER), true, this.settings.useOldNether),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_NETHER_CAVES, I18n.format(PREFIX + NbtTags.USE_NETHER_CAVES), true, this.settings.useNetherCaves),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_FORTRESSES, I18n.format(PREFIX + NbtTags.USE_FORTRESSES), true, this.settings.useFortresses),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG0_B_USE_LAVA_POCKETS, I18n.format(PREFIX + NbtTags.USE_LAVA_POCKETS), true, this.settings.useLavaPockets)
        };
        
        GuiPageButtonList.GuiListEntry[] pageList1 = {
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG1_L_BETA, I18n.format(PREFIX + "betaFeaturesLabel"), true),
            null,
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_GRASS, I18n.format(PREFIX + NbtTags.USE_TALL_GRASS), true, this.settings.useTallGrass),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_FLOWERS, I18n.format(PREFIX + NbtTags.USE_NEW_FLOWERS), true, this.settings.useNewFlowers),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_PADS, I18n.format(PREFIX + NbtTags.USE_LILY_PADS), true, this.settings.useLilyPads),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_MELONS, I18n.format(PREFIX + NbtTags.USE_MELONS), true, this.settings.useMelons),

            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_WELLS, I18n.format(PREFIX + NbtTags.USE_DESERT_WELLS), true, this.settings.useDesertWells),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_FOSSILS, I18n.format(PREFIX + NbtTags.USE_FOSSILS), true, this.settings.useFossils),

            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_BIRCH, I18n.format(PREFIX + NbtTags.USE_BIRCH_TREES), true, this.settings.useBirchTrees),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_PINE, I18n.format(PREFIX + NbtTags.USE_PINE_TREES), true, this.settings.usePineTrees),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_SWAMP, I18n.format(PREFIX + NbtTags.USE_SWAMP_TREES), true, this.settings.useSwampTrees),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_JUNGLE, I18n.format(PREFIX + NbtTags.USE_JUNGLE_TREES), true, this.settings.useJungleTrees),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_ACACIA, I18n.format(PREFIX + NbtTags.USE_ACACIA_TREES), true, this.settings.useAcaciaTrees),
            null,
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG1_L_RELEASE, I18n.format(PREFIX + "releaseFeaturesLabel"), true),
            null,
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_USE_MODDED_BIOMES, I18n.format(PREFIX + NbtTags.USE_MODDED_BIOMES), true, this.settings.useModdedBiomes),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG1_L_MODS, String.format("%s: %s", I18n.format(PREFIX + "modsLabel"), loadedModsList), true),
        
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG1_L_MOBS, I18n.format(PREFIX + "mobSpawnLabel"), true),
            null,
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_SPAWN_CREATURE, I18n.format(PREFIX + NbtTags.SPAWN_NEW_CREATURE_MOBS), true, this.settings.spawnNewCreatureMobs),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_SPAWN_MONSTER, I18n.format(PREFIX + NbtTags.SPAWN_NEW_MONSTER_MOBS), true, this.settings.spawnNewMonsterMobs),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_SPAWN_WATER, I18n.format(PREFIX + NbtTags.SPAWN_WATER_MOBS), true, this.settings.spawnWaterMobs),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_SPAWN_AMBIENT, I18n.format(PREFIX + NbtTags.SPAWN_AMBIENT_MOBS), true, this.settings.spawnAmbientMobs),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG1_B_SPAWN_WOLVES, I18n.format(PREFIX + NbtTags.SPAWN_WOLVES), true, this.settings.spawnWolves),
            null
        };
        
        GuiPageButtonList.GuiListEntry[] pageList2 = {
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_CLAY_NAME, I18n.format("tile.clay.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_CLAY_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.claySize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_CLAY_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.clayCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_CLAY_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.clayMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_CLAY_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.clayMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_DIRT_NAME, I18n.format("tile.dirt.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIRT_SIZE, I18n.format(PREFIX + "size"),false, this, 1.0f, 50.0f, (float)this.settings.dirtSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIRT_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.dirtCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIRT_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.dirtMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIRT_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.dirtMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_GRAV_NAME, I18n.format("tile.gravel.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GRAV_SIZE, I18n.format(PREFIX + "size"),false, this, 1.0f, 50.0f, (float)this.settings.gravelSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GRAV_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.gravelCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GRAV_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.gravelMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GRAV_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.gravelMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_GRAN_NAME, I18n.format("tile.stone.granite.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GRAN_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.graniteSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GRAN_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.graniteCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GRAN_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.graniteMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GRAN_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.graniteMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_DIOR_NAME, I18n.format("tile.stone.diorite.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIOR_SIZE, I18n.format(PREFIX + "size"),false, this, 1.0f, 50.0f, (float)this.settings.dioriteSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIOR_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.dioriteCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIOR_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.dioriteMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIOR_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.dioriteMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_ANDE_NAME, I18n.format("tile.stone.andesite.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_ANDE_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.andesiteSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_ANDE_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.andesiteCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_ANDE_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.andesiteMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_ANDE_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.andesiteMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_COAL_NAME, I18n.format("tile.oreCoal.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_COAL_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.coalSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_COAL_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.coalCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_COAL_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.coalMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_COAL_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.coalMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_IRON_NAME, I18n.format("tile.oreIron.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_IRON_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.ironSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_IRON_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.ironCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_IRON_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.ironMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_IRON_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.ironMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_GOLD_NAME, I18n.format("tile.oreGold.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GOLD_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.goldSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GOLD_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.goldCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GOLD_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.goldMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_GOLD_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.goldMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_REDS_NAME, I18n.format("tile.oreRedstone.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_REDS_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.redstoneSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_REDS_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.redstoneCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_REDS_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.redstoneMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_REDS_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.redstoneMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_DIAM_NAME, I18n.format("tile.oreDiamond.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIAM_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.diamondSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIAM_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.diamondCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIAM_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.diamondMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_DIAM_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.diamondMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_LAPS_NAME, I18n.format("tile.oreLapis.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_LAPS_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.lapisSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_LAPS_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.lapisCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_LAPS_CTR, I18n.format(PREFIX + "center"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.lapisCenterHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_LAPS_SPR, I18n.format(PREFIX + "spread"), false, this, 1.0f, MAX_HEIGHT, (float)this.settings.lapisSpread),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_EMER_NAME, I18n.format("tile.oreEmerald.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_EMER_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.emeraldSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_EMER_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.emeraldCount),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_EMER_MIN, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.emeraldMinHeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_EMER_MAX, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.emeraldMaxHeight),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_QRTZ_NAME, String.format("%s (%s)", I18n.format("tile.netherquartz.name"), I18n.format(PREFIX + "useOldNether")), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_QRTZ_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.quartzSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_QRTZ_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.quartzCount),

            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG2_L_MGMA_NAME, String.format("%s (%s)", I18n.format("tile.magma.name"), I18n.format(PREFIX + "useOldNether")), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_MGMA_SIZE, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.magmaSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG2_S_MGMA_CNT, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.magmaCount),
        };
        
        GuiPageButtonList.GuiListEntry[] pageList3 = {
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_MAIN_NS_X, I18n.format(PREFIX + "mainNoiseScaleX"), false, this, 1.0f, 5000.0f, this.settings.mainNoiseScaleX),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_MAIN_NS_Y, I18n.format(PREFIX + "mainNoiseScaleY"), false, this, 1.0f, 5000.0f, this.settings.mainNoiseScaleY),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_MAIN_NS_Z, I18n.format(PREFIX + "mainNoiseScaleZ"), false, this, 1.0f, 5000.0f, this.settings.mainNoiseScaleZ),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_DPTH_NS_X, I18n.format(PREFIX + "depthNoiseScaleX"), false, this, 1.0f, 2000.0f, this.settings.depthNoiseScaleX),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_DPTH_NS_Z, I18n.format(PREFIX + "depthNoiseScaleZ"), false, this, 1.0f, 2000.0f, this.settings.depthNoiseScaleZ),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_BASE_SIZE, I18n.format(PREFIX + "baseSize"), false, this, 1.0f, 25.0f, this.settings.baseSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_COORD_SCL, I18n.format(PREFIX + "coordinateScale"), false, this, 1.0f, 6000.0f, this.settings.coordinateScale),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_HEIGH_SCL, I18n.format(PREFIX + "heightScale"), false, this, 1.0f, 6000.0f, this.settings.heightScale),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_STRETCH_Y, I18n.format(PREFIX + "stretchY"), false, this, 0.01f, 50.0f, this.settings.stretchY),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_UPPER_LIM, I18n.format(PREFIX + "upperLimitScale"), false, this, 1.0f, 5000.0f, this.settings.upperLimitScale),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_LOWER_LIM, I18n.format(PREFIX + "lowerLimitScale"), false, this, 1.0f, 5000.0f, this.settings.lowerLimitScale),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_HEIGH_LIM, I18n.format(PREFIX + NbtTags.HEIGHT), false, this, 1.0f, MAX_HEIGHT, this.settings.height),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG3_L_BETA_LABL, I18n.format(PREFIX + "betaNoiseLabel"), true),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_TEMP_SCL, I18n.format(PREFIX + NbtTags.TEMP_NOISE_SCALE), false, this, MIN_BIOME_SCALE, MAX_BIOME_SCALE, this.settings.tempNoiseScale),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_RAIN_SCL, I18n.format(PREFIX + NbtTags.RAIN_NOISE_SCALE), false, this, MIN_BIOME_SCALE, MAX_BIOME_SCALE, this.settings.rainNoiseScale),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_DETL_SCL, I18n.format(PREFIX + NbtTags.DETAIL_NOISE_SCALE), false, this, MIN_BIOME_SCALE, MAX_BIOME_SCALE, this.settings.detailNoiseScale),
            null,
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG3_L_RELE_LABL, I18n.format(PREFIX + "releaseNoiseLabel"), true),
            null,
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_B_DPTH_WT, I18n.format(PREFIX + "biomeDepthWeight"), false, this, MIN_BIOME_WEIGHT, MAX_BIOME_WEIGHT, this.settings.biomeDepthWeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_B_DPTH_OF, I18n.format(PREFIX + "biomeDepthOffset"), false, this, MIN_BIOME_OFFSET, MAX_BIOME_OFFSET, this.settings.biomeDepthOffset),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_B_SCL_WT, I18n.format(PREFIX + "biomeScaleWeight"), false, this, MIN_BIOME_WEIGHT, MAX_BIOME_WEIGHT, this.settings.biomeScaleWeight),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_B_SCL_OF, I18n.format(PREFIX + "biomeScaleOffset"), false, this, MIN_BIOME_OFFSET, MAX_BIOME_OFFSET, this.settings.biomeScaleOffset),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_BIOME_SZ, I18n.format(PREFIX + NbtTags.BIOME_SIZE), false, this, MIN_BIOME_SIZE, MAX_BIOME_SIZE, this.settings.biomeSize),
            new GuiPageButtonList.GuiSlideEntry(GuiIds.PG3_S_RIVER_SZ, I18n.format(PREFIX + "riverRarity"), false, this, MIN_RIVER_SIZE, MAX_RIVER_SIZE, this.settings.riverSize),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG3_B_USE_BDS, I18n.format(PREFIX + NbtTags.USE_BIOME_DEPTH_SCALE), true, this.settings.useBiomeDepthScale)
        };
        
        GuiPageButtonList.GuiListEntry[] pageList4 = {
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_MAIN_NS_X, I18n.format(PREFIX + "mainNoiseScaleX") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_MAIN_NS_X, String.format("%5.3f", this.settings.mainNoiseScaleX), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_MAIN_NS_Y, I18n.format(PREFIX + "mainNoiseScaleY") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_MAIN_NS_Y, String.format("%5.3f", this.settings.mainNoiseScaleY), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_MAIN_NS_Z, I18n.format(PREFIX + "mainNoiseScaleZ") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_MAIN_NS_Z, String.format("%5.3f", this.settings.mainNoiseScaleZ), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_DPTH_NS_X, I18n.format(PREFIX + "depthNoiseScaleX") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_DPTH_NS_X, String.format("%5.3f", this.settings.depthNoiseScaleX), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_DPTH_NS_Z, I18n.format(PREFIX + "depthNoiseScaleZ") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_DPTH_NS_Z, String.format("%5.3f", this.settings.depthNoiseScaleZ), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_BASE_SIZE, I18n.format(PREFIX + "baseSize") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_BASE_SIZE, String.format("%2.3f", this.settings.baseSize), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_COORD_SCL, I18n.format(PREFIX + "coordinateScale") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_COORD_SCL, String.format("%5.3f", this.settings.coordinateScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_HEIGH_SCL, I18n.format(PREFIX + "heightScale") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_HEIGH_SCL, String.format("%5.3f", this.settings.heightScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_STRETCH_Y, I18n.format(PREFIX + "stretchY") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_STRETCH_Y, String.format("%2.3f", this.settings.stretchY), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_UPPER_LIM, I18n.format(PREFIX + "upperLimitScale") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_UPPER_LIM, String.format("%5.3f", this.settings.upperLimitScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_LOWER_LIM, I18n.format(PREFIX + "lowerLimitScale") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_LOWER_LIM, String.format("%5.3f", this.settings.lowerLimitScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_HEIGH_LIM, I18n.format(PREFIX + NbtTags.HEIGHT) + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_HEIGH_LIM, String.format("%d", this.settings.height), false, this.intFilter),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG3_L_BETA_LABL, I18n.format(PREFIX + "betaNoiseLabel"), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_TEMP_SCL, I18n.format(PREFIX + NbtTags.TEMP_NOISE_SCALE) + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_TEMP_SCL, String.format("%2.3f", this.settings.tempNoiseScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_RAIN_SCL, I18n.format(PREFIX + NbtTags.RAIN_NOISE_SCALE) + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_RAIN_SCL, String.format("%2.3f", this.settings.rainNoiseScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_DETL_SCL, I18n.format(PREFIX + NbtTags.DETAIL_NOISE_SCALE) + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_DETL_SCL, String.format("%2.3f", this.settings.detailNoiseScale), false, this.floatFilter),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG3_L_RELE_LABL, I18n.format(PREFIX + "releaseNoiseLabel"), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_B_DPTH_WT, I18n.format(PREFIX + "biomeDepthWeight") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_B_DPTH_WT, String.format("%2.3f", this.settings.biomeDepthWeight), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_B_DPTH_OF, I18n.format(PREFIX + "biomeDepthOffset") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_B_DPTH_OF, String.format("%2.3f", this.settings.biomeDepthOffset), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_B_SCL_WT, I18n.format(PREFIX + "biomeScaleWeight") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_B_SCL_WT, String.format("%2.3f", this.settings.biomeScaleWeight), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_B_SCL_OF, I18n.format(PREFIX + "biomeScaleOffset") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_B_SCL_OF, String.format("%2.3f", this.settings.biomeScaleOffset), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_BIOME_SZ, I18n.format(PREFIX + NbtTags.BIOME_SIZE) + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_BIOME_SZ, String.format("%d", this.settings.biomeSize), false, this.intBiomeSizeFilter),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG4_L_RIVER_SZ, I18n.format(PREFIX + "riverRarity") + ":", false),
            new GuiPageButtonList.EditBoxEntry(GuiIds.PG4_F_RIVER_SZ, String.format("%d", this.settings.riverSize), false, this.intRiverSizeFilter)
            
        };
        
        GuiPageButtonList.GuiListEntry[] pageList5 = {
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_DSRT_LABL, I18n.format(PREFIX + NbtTags.DESERT_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_DSRT_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_DSRT_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_DSRT_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_FRST_LABL, I18n.format(PREFIX + NbtTags.FOREST_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_FRST_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_FRST_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_FRST_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_ICED_LABL, I18n.format(PREFIX + NbtTags.ICE_DESERT_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_ICED_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_ICED_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_ICED_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_PLNS_LABL, I18n.format(PREFIX + NbtTags.PLAINS_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_PLNS_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_PLNS_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_PLNS_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_RAIN_LABL, I18n.format(PREFIX + NbtTags.RAINFOREST_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_RAIN_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_RAIN_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_RAIN_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_SAVA_LABL, I18n.format(PREFIX + NbtTags.SAVANNA_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SAVA_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SAVA_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SAVA_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_SHRB_LABL, I18n.format(PREFIX + NbtTags.SHRUBLAND_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SHRB_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SHRB_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SHRB_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_SEAS_LABL, I18n.format(PREFIX + NbtTags.SEASONAL_FOREST_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SEAS_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SEAS_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SEAS_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_SWMP_LABL, I18n.format(PREFIX + NbtTags.SWAMPLAND_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SWMP_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SWMP_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_SWMP_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_TAIG_LABL, I18n.format(PREFIX + NbtTags.TAIGA_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_TAIG_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_TAIG_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_TAIG_BEACH, "", true, true),
            
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_TUND_LABL, I18n.format(PREFIX + NbtTags.TUNDRA_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_TUND_LAND, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_TUND_OCEAN, "", true, true),
            new GuiPageButtonList.GuiLabelEntry(GuiIds.PG5_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.GuiButtonEntry(GuiIds.PG5_TUND_BEACH, "", true, true)
        };
        
        if (ModCompat.isBoPLoaded()) {
            GuiPageButtonList.GuiListEntry[] newPageList0 = new GuiPageButtonList.GuiListEntry[pageList0.length + 2];
            System.arraycopy(pageList0, 0, newPageList0, 0, pageList0.length);
            newPageList0[pageList0.length] = new GuiPageButtonList.GuiLabelEntry(GuiIds.PG0_L_NETHER_BOP, I18n.format(PREFIX + "netherBoPLabel"), true);
            newPageList0[pageList0.length + 1] = null;
            pageList0 = newPageList0;
        }
        
        this.pageList = new GuiPageButtonList(
            this.mc,
            this.width,
            this.height,
            32,
            this.height - 32,
            25,
            this,
            new GuiPageButtonList.GuiListEntry[][] {
                pageList0,
                pageList1,
                pageList2,
                pageList3,
                pageList4,
                pageList5
            }
        );
        
        this.pageList.width += PAGELIST_ADDITIONAL_WIDTH;
        
        // Set biome text for Single Biome button
        this.setInitialTextButton(this.pageList, GuiIds.PG0_B_FIXED, this.getFormattedBiomeName(this.settings.singleBiome, true, 18));
        
        // Set biome text for Beta Biome buttons
        this.setInitialTextButton(this.pageList, GuiIds.PG5_DSRT_LAND, this.getFormattedBiomeName(this.settings.desertBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_DSRT_OCEAN, this.getFormattedBiomeName(this.settings.desertBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_DSRT_BEACH, this.getFormattedBiomeName(this.settings.desertBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_FRST_LAND, this.getFormattedBiomeName(this.settings.forestBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_FRST_OCEAN, this.getFormattedBiomeName(this.settings.forestBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_FRST_BEACH, this.getFormattedBiomeName(this.settings.forestBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_ICED_LAND, this.getFormattedBiomeName(this.settings.iceDesertBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_ICED_OCEAN, this.getFormattedBiomeName(this.settings.iceDesertBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_ICED_BEACH, this.getFormattedBiomeName(this.settings.iceDesertBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_PLNS_LAND, this.getFormattedBiomeName(this.settings.plainsBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_PLNS_OCEAN, this.getFormattedBiomeName(this.settings.plainsBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_PLNS_BEACH, this.getFormattedBiomeName(this.settings.plainsBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_RAIN_LAND, this.getFormattedBiomeName(this.settings.rainforestBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_RAIN_OCEAN, this.getFormattedBiomeName(this.settings.rainforestBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_RAIN_BEACH, this.getFormattedBiomeName(this.settings.rainforestBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SAVA_LAND, this.getFormattedBiomeName(this.settings.savannaBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SAVA_OCEAN, this.getFormattedBiomeName(this.settings.savannaBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SAVA_BEACH, this.getFormattedBiomeName(this.settings.savannaBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SHRB_LAND, this.getFormattedBiomeName(this.settings.shrublandBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SHRB_OCEAN, this.getFormattedBiomeName(this.settings.shrublandBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SHRB_BEACH, this.getFormattedBiomeName(this.settings.shrublandBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SEAS_LAND, this.getFormattedBiomeName(this.settings.seasonalForestBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SEAS_OCEAN, this.getFormattedBiomeName(this.settings.seasonalForestBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SEAS_BEACH, this.getFormattedBiomeName(this.settings.seasonalForestBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SWMP_LAND, this.getFormattedBiomeName(this.settings.swamplandBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SWMP_OCEAN, this.getFormattedBiomeName(this.settings.swamplandBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_SWMP_BEACH, this.getFormattedBiomeName(this.settings.swamplandBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_TAIG_LAND, this.getFormattedBiomeName(this.settings.taigaBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_TAIG_OCEAN, this.getFormattedBiomeName(this.settings.taigaBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_TAIG_BEACH, this.getFormattedBiomeName(this.settings.taigaBiomeBeach, false, -1));
        
        this.setInitialTextButton(this.pageList, GuiIds.PG5_TUND_LAND, this.getFormattedBiomeName(this.settings.tundraBiomeBase, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_TUND_OCEAN, this.getFormattedBiomeName(this.settings.tundraBiomeOcean, false, -1));
        this.setInitialTextButton(this.pageList, GuiIds.PG5_TUND_BEACH, this.getFormattedBiomeName(this.settings.tundraBiomeBeach, false, -1));

        this.updatePageControls();
    }

    @Override
    public void initGui() {
        int curPage = 0;
        int curScroll = 0;
        
        if (this.pageList != null) {
            curPage = this.pageList.getPage();
            curScroll = this.pageList.getAmountScrolled();
        }
        
        this.title = I18n.format("options.customizeTitle");
        this.buttonList.clear();
        
        this.done = this.<GuiButton>addButton(new GuiButton(GuiIds.FUNC_DONE, this.width / 2 + 98, this.height - 27, 90, 20, I18n.format("gui.done")));
        this.randomize = this.<GuiButton>addButton(new GuiButton(GuiIds.FUNC_RAND, this.width / 2 - 92, this.height - 27, 90, 20, I18n.format(PREFIX + "randomize")));
        this.previousPage = this.<GuiButton>addButton(new GuiButton(GuiIds.FUNC_PREV, 20, 5, 80, 20, I18n.format(PREFIX + "prev")));
        this.nextPage = this.<GuiButton>addButton(new GuiButton(GuiIds.FUNC_NEXT, this.width - 100, 5, 80, 20, I18n.format(PREFIX + "next")));
        this.defaults = this.<GuiButton>addButton(new GuiButton(GuiIds.FUNC_DFLT, this.width / 2 - 187, this.height - 27, 90, 20, I18n.format(PREFIX + "defaults")));
        this.presets = this.<GuiButton>addButton(new GuiButton(GuiIds.FUNC_PRST, this.width / 2 + 3, this.height - 27, 90, 20, I18n.format(PREFIX + "presets")));
        
        this.defaults.enabled = this.settingsModified;
        
        this.confirm = new GuiButton(GuiIds.FUNC_CONF, this.width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"));
        this.confirm.visible = false;
        
        this.cancel = new GuiButton(GuiIds.FUNC_CNCL, this.width / 2 + 5, 160, 50, 20, I18n.format("gui.no"));
        this.cancel.visible = false;

        this.buttonList.add(this.confirm);
        this.buttonList.add(this.cancel);
        
        if (this.confirmMode != 0) {
            this.confirm.visible = true;
            this.cancel.visible = true;
        }
        
        GuiIds.assertOffsets();
        this.createPagedList();
        
        if (curPage != 0) {
            this.pageList.setPage(curPage);
            this.pageList.scrollBy(curScroll);
            this.updatePageControls();
        }
        
        // Set default enabled for certain options
        this.updateGuiButtons();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.pageList.handleMouseInput();
    }
    
    @Override
    public String getText(int entry, String entryString, float entryValue) {
        return entryString + ": " + this.getFormattedValue(entry, entryValue);
    }

    @Override
    public void setEntryValue(int entry, String string) {
        if (entry == GuiIds.PG4_F_BIOME_SZ || entry == GuiIds.PG4_F_RIVER_SZ) {
            int entryValue = 0;
            
            try {
                entryValue = Integer.parseInt(string);
            } catch (NumberFormatException ex) {}
            
            int newEntryValue = 0;
            switch(entry) {
                case GuiIds.PG4_F_BIOME_SZ:
                    this.settings.biomeSize = MathHelper.clamp(entryValue, MIN_BIOME_SIZE, MAX_BIOME_SIZE);
                    newEntryValue = this.settings.biomeSize;
                    break;
                case GuiIds.PG4_F_RIVER_SZ:
                    this.settings.riverSize = MathHelper.clamp(entryValue, MIN_RIVER_SIZE, MAX_RIVER_SIZE);
                    newEntryValue = this.settings.riverSize;
                    break;
            }
            
            if (newEntryValue != entryValue && entryValue != 0) {
                ((GuiTextField)this.pageList.getComponent(entry)).setText(this.getFormattedValue(entry, newEntryValue));
            }
            
            ((GuiSlider)this.pageList.getComponent(GuiIds.offsetBackward(entry))).setSliderValue(newEntryValue, false);
        
        } else {
            float entryValue = 0.0f;
            
            try {
                entryValue = Float.parseFloat(string);
                
            } catch (NumberFormatException ex) {}
            
            float newEntryValue = 0.0f;
            switch (entry) {
                case GuiIds.PG4_F_MAIN_NS_X:
                    this.settings.mainNoiseScaleX = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.mainNoiseScaleX;
                    break;
                case GuiIds.PG4_F_MAIN_NS_Y:
                    this.settings.mainNoiseScaleY = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.mainNoiseScaleY;
                    break;
                case GuiIds.PG4_F_MAIN_NS_Z:
                    this.settings.mainNoiseScaleZ = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.mainNoiseScaleZ;
                    break;
                case GuiIds.PG4_F_DPTH_NS_X:
                    this.settings.depthNoiseScaleX = MathHelper.clamp(entryValue, 1.0f, 2000.0f);
                    newEntryValue = this.settings.depthNoiseScaleX;
                    break;
                case GuiIds.PG4_F_DPTH_NS_Z:
                    this.settings.depthNoiseScaleZ = MathHelper.clamp(entryValue, 1.0f, 2000.0f);
                    newEntryValue = this.settings.depthNoiseScaleZ;
                    break;
                case GuiIds.PG4_F_BASE_SIZE:
                    this.settings.baseSize = MathHelper.clamp(entryValue, 1.0f, 25.0f);
                    newEntryValue = this.settings.baseSize;
                    break;
                case GuiIds.PG4_F_COORD_SCL:
                    this.settings.coordinateScale = MathHelper.clamp(entryValue, 1.0f, 6000.0f);
                    newEntryValue = this.settings.coordinateScale;
                    break;
                case GuiIds.PG4_F_HEIGH_SCL:
                    this.settings.heightScale = MathHelper.clamp(entryValue, 1.0f, 6000.0f);
                    newEntryValue = this.settings.heightScale;
                    break;
                case GuiIds.PG4_F_STRETCH_Y:
                    this.settings.stretchY = MathHelper.clamp(entryValue, 0.01f, 50.0f);
                    newEntryValue = this.settings.stretchY;
                    break;
                case GuiIds.PG4_F_UPPER_LIM:
                    this.settings.upperLimitScale = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.upperLimitScale;
                    break;
                case GuiIds.PG4_F_LOWER_LIM:
                    this.settings.lowerLimitScale = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.lowerLimitScale;
                    break;
                case GuiIds.PG4_F_HEIGH_LIM:
                    this.settings.height = (int)MathHelper.clamp(entryValue, 1.0f, MAX_HEIGHT);
                    newEntryValue = this.settings.height;
                    break;
                case GuiIds.PG4_F_TEMP_SCL:
                    this.settings.tempNoiseScale = MathHelper.clamp(entryValue, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                    newEntryValue = this.settings.tempNoiseScale;
                    break;
                case GuiIds.PG4_F_RAIN_SCL:
                    this.settings.rainNoiseScale = MathHelper.clamp(entryValue, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                    newEntryValue = this.settings.rainNoiseScale;
                    break;
                case GuiIds.PG4_F_DETL_SCL:
                    this.settings.detailNoiseScale = MathHelper.clamp(entryValue, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                    newEntryValue = this.settings.detailNoiseScale;
                    break;
                case GuiIds.PG4_F_B_DPTH_WT:
                    this.settings.biomeDepthWeight = MathHelper.clamp(entryValue, MIN_BIOME_WEIGHT, MAX_BIOME_WEIGHT);
                    newEntryValue = this.settings.biomeDepthWeight;
                    break;
                case GuiIds.PG4_F_B_DPTH_OF:
                    this.settings.biomeDepthOffset = MathHelper.clamp(entryValue, MIN_BIOME_OFFSET, MAX_BIOME_OFFSET);
                    newEntryValue = this.settings.biomeDepthOffset;
                    break;
                case GuiIds.PG4_F_B_SCL_WT:
                    this.settings.biomeScaleWeight = MathHelper.clamp(entryValue, MIN_BIOME_WEIGHT, MAX_BIOME_WEIGHT);
                    newEntryValue = this.settings.biomeScaleWeight;
                    break;
                case GuiIds.PG4_F_B_SCL_OF:
                    this.settings.biomeScaleOffset = MathHelper.clamp(entryValue, MIN_BIOME_OFFSET, MAX_BIOME_OFFSET);
                    newEntryValue = this.settings.biomeScaleOffset;
                    break;
            }
            
            if (newEntryValue != entryValue && entryValue != 0.0f) {
                ((GuiTextField)this.pageList.getComponent(entry)).setText(this.getFormattedValue(entry, newEntryValue));
            }
            
            ((GuiSlider)this.pageList.getComponent(GuiIds.offsetBackward(entry))).setSliderValue(newEntryValue, false);
        }
        
        this.setSettingsModified(!this.settings.equals(this.defaultSettings));
    }

    @Override
    public void setEntryValue(int entry, boolean entryValue) {
        switch (entry) {
            case GuiIds.PG0_B_FIXED:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.singleBiome = str, settings.singleBiome));
                break;
                
            case GuiIds.PG5_DSRT_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.desertBiomeBase = str, settings.desertBiomeBase));
                break;
            case GuiIds.PG5_DSRT_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.desertBiomeOcean = str, settings.desertBiomeOcean));
                break;
            case GuiIds.PG5_DSRT_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.desertBiomeBeach = str, settings.desertBiomeBeach));
                break;
                
            case GuiIds.PG5_FRST_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.forestBiomeBase = str, settings.forestBiomeBase));
                break;
            case GuiIds.PG5_FRST_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.forestBiomeOcean = str, settings.forestBiomeOcean));
                break;
            case GuiIds.PG5_FRST_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.forestBiomeBeach = str, settings.forestBiomeBeach));
                break;
                
            case GuiIds.PG5_ICED_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.iceDesertBiomeBase = str, settings.iceDesertBiomeBase));
                break;
            case GuiIds.PG5_ICED_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.iceDesertBiomeOcean = str, settings.iceDesertBiomeOcean));
                break;
            case GuiIds.PG5_ICED_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.iceDesertBiomeBeach = str, settings.iceDesertBiomeBeach));
                break;
                
            case GuiIds.PG5_PLNS_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.plainsBiomeBase = str, settings.plainsBiomeBase));
                break;
            case GuiIds.PG5_PLNS_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.plainsBiomeOcean = str, settings.plainsBiomeOcean));
                break;
            case GuiIds.PG5_PLNS_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.plainsBiomeBeach = str, settings.plainsBiomeBeach));
                break;
                
            case GuiIds.PG5_RAIN_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.rainforestBiomeBase = str, settings.rainforestBiomeBase));
                break;
            case GuiIds.PG5_RAIN_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.rainforestBiomeOcean = str, settings.rainforestBiomeOcean));
                break;
            case GuiIds.PG5_RAIN_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.rainforestBiomeBeach = str, settings.rainforestBiomeBeach));
                break;
                
            case GuiIds.PG5_SAVA_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.savannaBiomeBase = str, settings.savannaBiomeBase));
                break;
            case GuiIds.PG5_SAVA_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.savannaBiomeOcean = str, settings.savannaBiomeOcean));
                break;
            case GuiIds.PG5_SAVA_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.savannaBiomeBeach = str, settings.savannaBiomeBeach));
                break;
                
            case GuiIds.PG5_SHRB_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.shrublandBiomeBase = str, settings.shrublandBiomeBase));
                break;
            case GuiIds.PG5_SHRB_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.shrublandBiomeOcean = str, settings.shrublandBiomeOcean));
                break;
            case GuiIds.PG5_SHRB_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.shrublandBiomeBeach = str, settings.shrublandBiomeBeach));
                break;
                
            case GuiIds.PG5_SEAS_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.seasonalForestBiomeBase = str, settings.seasonalForestBiomeBase));
                break;
            case GuiIds.PG5_SEAS_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.seasonalForestBiomeOcean = str, settings.seasonalForestBiomeOcean));
                break;
            case GuiIds.PG5_SEAS_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.seasonalForestBiomeBeach = str, settings.seasonalForestBiomeBeach));
                break;
                
            case GuiIds.PG5_SWMP_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.swamplandBiomeBase = str, settings.swamplandBiomeBase));
                break;
            case GuiIds.PG5_SWMP_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.swamplandBiomeOcean = str, settings.swamplandBiomeOcean));
                break;
            case GuiIds.PG5_SWMP_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.swamplandBiomeBeach = str, settings.swamplandBiomeBeach));
                break;
                
            case GuiIds.PG5_TAIG_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.taigaBiomeBase = str, settings.taigaBiomeBase));
                break;
            case GuiIds.PG5_TAIG_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.taigaBiomeOcean = str, settings.taigaBiomeOcean));
                break;
            case GuiIds.PG5_TAIG_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.taigaBiomeBeach = str, settings.taigaBiomeBeach));
                break;
                
            case GuiIds.PG5_TUND_LAND:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.tundraBiomeBase = str, settings.tundraBiomeBase));
                break;
            case GuiIds.PG5_TUND_OCEAN:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.tundraBiomeOcean = str, settings.tundraBiomeOcean));
                break;
            case GuiIds.PG5_TUND_BEACH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this, (str, factory) -> factory.tundraBiomeBeach = str, settings.tundraBiomeBeach));
                break;
        
            case GuiIds.PG0_B_USE_OCEAN:
                this.settings.replaceOceanBiomes = entryValue;
                break;
            case GuiIds.PG0_B_USE_BEACH:
                this.settings.replaceBeachBiomes = entryValue;
                break;
            case GuiIds.PG1_B_USE_GRASS:
                this.settings.useTallGrass = entryValue;
                break;
            case GuiIds.PG1_B_USE_FLOWERS:
                this.settings.useNewFlowers = entryValue;
                break;
            case GuiIds.PG1_B_USE_PADS:
                this.settings.useLilyPads = entryValue;
                break;
            case GuiIds.PG1_B_USE_MELONS:
                this.settings.useMelons = entryValue;
                break;
            case GuiIds.PG1_B_USE_WELLS:
                this.settings.useDesertWells = entryValue;
                break;
            case GuiIds.PG1_B_USE_FOSSILS:
                this.settings.useFossils = entryValue;
                break;
            case GuiIds.PG1_B_USE_BIRCH:
                this.settings.useBirchTrees = entryValue;
                break;
            case GuiIds.PG1_B_USE_PINE:
                this.settings.usePineTrees = entryValue;
                break;
            case GuiIds.PG1_B_USE_SWAMP:
                this.settings.useSwampTrees = entryValue;
                break;
            case GuiIds.PG1_B_USE_JUNGLE:
                this.settings.useJungleTrees = entryValue;
                break;
            case GuiIds.PG1_B_USE_ACACIA:
                this.settings.useAcaciaTrees = entryValue;
                break;
                
            case GuiIds.PG1_B_USE_MODDED_BIOMES:
                this.settings.useModdedBiomes = entryValue;
                break;
                
            case GuiIds.PG1_B_SPAWN_CREATURE:
                this.settings.spawnNewCreatureMobs = entryValue;
                break;
            case GuiIds.PG1_B_SPAWN_MONSTER:
                this.settings.spawnNewMonsterMobs = entryValue;
                break;
            case GuiIds.PG1_B_SPAWN_WOLVES:
                this.settings.spawnWolves = entryValue;
                break;
            case GuiIds.PG1_B_SPAWN_WATER:
                this.settings.spawnWaterMobs = entryValue;
                break;
            case GuiIds.PG1_B_SPAWN_AMBIENT:
                this.settings.spawnAmbientMobs = entryValue;
                break;
                
            case GuiIds.PG0_B_USE_CAVES:
                this.settings.useCaves = entryValue;
                break;
            case GuiIds.PG0_B_USE_HOLDS:
                this.settings.useStrongholds = entryValue;
                break;
            case GuiIds.PG0_B_USE_VILLAGES:
                this.settings.useVillages = entryValue;
                break;
            case GuiIds.PG0_B_USE_VILLAGE_VARIANTS:
                this.settings.useVillageVariants = entryValue;
                break;
            case GuiIds.PG0_B_USE_SHAFTS:
                this.settings.useMineShafts = entryValue;
                break;
            case GuiIds.PG0_B_USE_TEMPLES:
                this.settings.useTemples = entryValue;
                break;
            case GuiIds.PG0_B_USE_MONUMENTS:
                this.settings.useMonuments = entryValue;
                break;
            case GuiIds.PG0_B_USE_MANSIONS:
                this.settings.useMansions = entryValue;
                break;
            case GuiIds.PG0_B_USE_RAVINES:
                this.settings.useRavines = entryValue;
                break;
            case GuiIds.PG0_B_USE_DUNGEONS:
                this.settings.useDungeons = entryValue;
                break;
            case GuiIds.PG0_B_USE_WATER_LAKES:
                this.settings.useWaterLakes = entryValue;
                break;
            case GuiIds.PG0_B_USE_LAVA_LAKES:
                this.settings.useLavaLakes = entryValue;
                break;
            case GuiIds.PG0_B_USE_LAVA_OCEANS:
                this.settings.useLavaOceans = entryValue;
                break;
            case GuiIds.PG0_B_USE_SANDSTONE:
                this.settings.useSandstone = entryValue;
                break;
                
            case GuiIds.PG0_B_USE_OLD_NETHER:
                this.settings.useOldNether = entryValue;
                break;
            case GuiIds.PG0_B_USE_NETHER_CAVES:
                this.settings.useNetherCaves = entryValue;
                break;
            case GuiIds.PG0_B_USE_FORTRESSES:
                this.settings.useFortresses = entryValue;
                break;
            case GuiIds.PG0_B_USE_LAVA_POCKETS:
                this.settings.useLavaPockets = entryValue;
                break;

            case GuiIds.PG0_B_USE_INDEV_CAVES:
                this.settings.useIndevCaves = entryValue;
                break;
                
            case GuiIds.PG0_B_USE_INFDEV_WALLS:
                this.settings.useInfdevWalls = entryValue;
                break;
            case GuiIds.PG0_B_USE_INFDEV_PYRAMIDS:
                this.settings.useInfdevPyramids = entryValue;
                break;
                
            case GuiIds.PG3_B_USE_BDS:
                this.settings.useBiomeDepthScale = entryValue;
                break;
        }
        
        this.updateGuiButtons();
        this.setSettingsModified(!this.settings.equals(this.defaultSettings));
        this.playSound();
    }

    @Override
    public void setEntryValue(int entry, float entryValue) {
        switch (entry) {
            case GuiIds.PG3_S_MAIN_NS_X:
                this.settings.mainNoiseScaleX = entryValue;
                break;
            case GuiIds.PG3_S_MAIN_NS_Y:
                this.settings.mainNoiseScaleY = entryValue;
                break;
            case GuiIds.PG3_S_MAIN_NS_Z:
                this.settings.mainNoiseScaleZ = entryValue;
                break;
            case GuiIds.PG3_S_DPTH_NS_X:
                this.settings.depthNoiseScaleX = entryValue;
                break;
            case GuiIds.PG3_S_DPTH_NS_Z:
                this.settings.depthNoiseScaleZ = entryValue;
                break;
            case GuiIds.PG3_S_BASE_SIZE:
                this.settings.baseSize = entryValue;
                break;    
            case GuiIds.PG3_S_COORD_SCL:
                this.settings.coordinateScale = entryValue;
                break;
            case GuiIds.PG3_S_HEIGH_SCL:
                this.settings.heightScale = entryValue;
                break;
            case GuiIds.PG3_S_STRETCH_Y:
                this.settings.stretchY = entryValue;
                break;
            case GuiIds.PG3_S_UPPER_LIM:
                this.settings.upperLimitScale = entryValue;
                break;
            case GuiIds.PG3_S_LOWER_LIM:
                this.settings.lowerLimitScale = entryValue;
                break;
            case GuiIds.PG3_S_HEIGH_LIM:
                this.settings.height = (int)entryValue;
                break;
            case GuiIds.PG3_S_TEMP_SCL:
                this.settings.tempNoiseScale = entryValue;
                break;
            case GuiIds.PG3_S_RAIN_SCL:
                this.settings.rainNoiseScale = entryValue;
                break;
            case GuiIds.PG3_S_DETL_SCL:
                this.settings.detailNoiseScale = entryValue;
                break;
            case GuiIds.PG3_S_B_DPTH_WT:
                this.settings.biomeDepthWeight = entryValue;
                break;
            case GuiIds.PG3_S_B_DPTH_OF:
                this.settings.biomeDepthOffset = entryValue;
                break;
            case GuiIds.PG3_S_B_SCL_WT:
                this.settings.biomeScaleWeight = entryValue;
                break;
            case GuiIds.PG3_S_B_SCL_OF:
                this.settings.biomeScaleOffset = entryValue;
                break;
            case GuiIds.PG3_S_BIOME_SZ:
                this.settings.biomeSize = (int)entryValue;
                break;
            case GuiIds.PG3_S_RIVER_SZ:
                this.settings.riverSize = (int)entryValue;
                break;
                
            case GuiIds.PG0_S_CHUNK:
                this.settings.chunkSource = ModernBetaRegistries.CHUNK.getKeys().get((int)entryValue);
                break;
            case GuiIds.PG0_S_BIOME:
                this.settings.biomeSource = ModernBetaRegistries.BIOME.getKeys().get((int)entryValue);
                break;
            case GuiIds.PG0_S_SURFACE:
                this.settings.surfaceBuilder = ModernBetaRegistries.SURFACE.getKeys().get((int)entryValue);
                break;
            case GuiIds.PG0_S_CARVER:
                this.settings.caveCarver = ModernBetaRegistries.CARVER.getKeys().get((int)entryValue);
                break;
            
            case GuiIds.PG0_S_SEA_LEVEL:
                this.settings.seaLevel = (int)entryValue;
                break;
            case GuiIds.PG0_S_CAVE_HEIGHT:
                this.settings.caveHeight = (int)entryValue;
                break;
            case GuiIds.PG0_S_CAVE_COUNT:
                this.settings.caveCount = (int)entryValue;
                break;
            case GuiIds.PG0_S_CAVE_CHANCE:
                this.settings.caveChance = (int)entryValue;
                break;
            case GuiIds.PG0_S_DUNGEON_CHANCE:
                this.settings.dungeonChance = (int)entryValue;
                break;
            case GuiIds.PG0_S_WATER_LAKE_CHANCE:
                this.settings.waterLakeChance = (int)entryValue;
                break;
            case GuiIds.PG0_S_LAVA_LAKE_CHANCE:
                this.settings.lavaLakeChance = (int)entryValue;
                break;
                
            case GuiIds.PG0_S_LEVEL_THEME:
                this.settings.levelTheme = IndevTheme.values()[(int)entryValue].id;
                break;
            case GuiIds.PG0_S_LEVEL_TYPE:
                this.settings.levelType = IndevType.values()[(int)entryValue].id;
                break;
            case GuiIds.PG0_S_LEVEL_WIDTH:
                this.settings.levelWidth = LEVEL_WIDTHS[(int)entryValue];
                break;
            case GuiIds.PG0_S_LEVEL_LENGTH:
                this.settings.levelLength = LEVEL_WIDTHS[(int)entryValue];
                break;
            case GuiIds.PG0_S_LEVEL_HEIGHT:
                this.settings.levelHeight = LEVEL_HEIGHTS[(int)entryValue];
                break;
            case GuiIds.PG0_S_LEVEL_HOUSE:
                this.settings.levelHouse = IndevHouse.values()[(int)entryValue].id;
                break;

            case GuiIds.PG2_S_CLAY_SIZE:
                this.settings.claySize = (int)entryValue;
                break;
            case GuiIds.PG2_S_CLAY_CNT:
                this.settings.clayCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_CLAY_MIN:
                this.settings.clayMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_CLAY_MAX:
                this.settings.clayMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIRT_SIZE:
                this.settings.dirtSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIRT_CNT:
                this.settings.dirtCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIRT_MIN:
                this.settings.dirtMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIRT_MAX:
                this.settings.dirtMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_GRAV_SIZE:
                this.settings.gravelSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_GRAV_CNT:
                this.settings.gravelCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_GRAV_MIN:
                this.settings.gravelMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_GRAV_MAX:
                this.settings.gravelMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_GRAN_SIZE:
                this.settings.graniteSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_GRAN_CNT:
                this.settings.graniteCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_GRAN_MIN:
                this.settings.graniteMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_GRAN_MAX:
                this.settings.graniteMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIOR_SIZE:
                this.settings.dioriteSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIOR_CNT:
                this.settings.dioriteCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIOR_MIN:
                this.settings.dioriteMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIOR_MAX:
                this.settings.dioriteMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_ANDE_SIZE:
                this.settings.andesiteSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_ANDE_CNT:
                this.settings.andesiteCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_ANDE_MIN:
                this.settings.andesiteMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_ANDE_MAX:
                this.settings.andesiteMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_COAL_SIZE:
                this.settings.coalSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_COAL_CNT:
                this.settings.coalCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_COAL_MIN:
                this.settings.coalMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_COAL_MAX:
                this.settings.coalMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_IRON_SIZE:
                this.settings.ironSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_IRON_CNT:
                this.settings.ironCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_IRON_MIN:
                this.settings.ironMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_IRON_MAX:
                this.settings.ironMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_GOLD_SIZE:
                this.settings.goldSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_GOLD_CNT:
                this.settings.goldCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_GOLD_MIN:
                this.settings.goldMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_GOLD_MAX:
                this.settings.goldMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_REDS_SIZE:
                this.settings.redstoneSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_REDS_CNT:
                this.settings.redstoneCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_REDS_MIN:
                this.settings.redstoneMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_REDS_MAX:
                this.settings.redstoneMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIAM_SIZE:
                this.settings.diamondSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIAM_CNT:
                this.settings.diamondCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIAM_MIN:
                this.settings.diamondMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_DIAM_MAX:
                this.settings.diamondMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_LAPS_SIZE:
                this.settings.lapisSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_LAPS_CNT:
                this.settings.lapisCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_LAPS_CTR:
                this.settings.lapisCenterHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_LAPS_SPR:
                this.settings.lapisSpread = (int)entryValue;
                break;
            case GuiIds.PG2_S_EMER_SIZE:
                this.settings.emeraldSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_EMER_CNT:
                this.settings.emeraldCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_EMER_MIN:
                this.settings.emeraldMinHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_EMER_MAX:
                this.settings.emeraldMaxHeight = (int)entryValue;
                break;
            case GuiIds.PG2_S_QRTZ_SIZE:
                this.settings.quartzSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_QRTZ_CNT:
                this.settings.quartzCount = (int)entryValue;
                break;
            case GuiIds.PG2_S_MGMA_SIZE:
                this.settings.magmaSize = (int)entryValue;
                break;
            case GuiIds.PG2_S_MGMA_CNT:
                this.settings.magmaCount = (int)entryValue;
                break;
        }
        
        if (entry >= GuiIds.PG3_S_MAIN_NS_X && entry <= GuiIds.PG3_S_BIOME_SZ) {
            Gui gui = this.pageList.getComponent(GuiIds.offsetForward(entry));
            if (gui != null) {
                ((GuiTextField)gui).setText(this.getFormattedValue(entry, entryValue));
            }
        }
        
        if (entry == GuiIds.PG0_S_LEVEL_HEIGHT || entry == GuiIds.PG0_S_LEVEL_TYPE || entry == GuiIds.PG0_S_CHUNK) {
            Gui gui = this.pageList.getComponent(GuiIds.PG0_L_INDEV_SEA_LEVEL);
            if (gui != null) {
                AccessorGuiLabel accessor = (AccessorGuiLabel)gui;
                int levelSeaLevel = this.getLevelSeaLevel();
                String levelSeaLevelStr = levelSeaLevel == -1 ? "" : Integer.toString(levelSeaLevel);
                
                accessor.getLabels().set(0, String.format("%s: %s", I18n.format(PREFIX + "seaLevel"), levelSeaLevelStr));
            }
        }
        
        this.updateGuiButtons();
        this.setSettingsModified(!this.settings.equals(this.defaultSettings));
        this.playSound();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.pageList.drawScreen(mouseX, mouseY, partialTicks);
        
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 2, 16777215);
        this.drawCenteredString(this.fontRenderer, this.subtitle, this.width / 2, 12, 16777215);
        this.drawCenteredString(this.fontRenderer, this.pageTitle, this.width / 2, 22, 16777215);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        if (this.confirmMode != 0) {
            Gui.drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
            
            this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 99, -2039584);
            this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 185, -6250336);
            this.drawVerticalLine(this.width / 2 - 91, 99, 185, -2039584);
            this.drawVerticalLine(this.width / 2 + 90, 99, 185, -6250336);
            
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            
            this.mc.getTextureManager().bindTexture(GuiScreenCustomizeWorld.OPTIONS_BACKGROUND);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(this.width / 2 - 90, 185.0, 0.0).tex(0.0, 2.65625).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(this.width / 2 + 90, 185.0, 0.0).tex(5.625, 2.65625).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(this.width / 2 + 90, 100.0, 0.0).tex(5.625, 0.0).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(this.width / 2 - 90, 100.0, 0.0).tex(0.0, 0.0).color(64, 64, 64, 64).endVertex();
            
            tessellator.draw();
            
            this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "confirmTitle"), this.width / 2, 105, 16777215);
            this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "confirm1"), this.width / 2, 125, 16777215);
            this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "confirm2"), this.width / 2, 135, 16777215);
            
            this.confirm.drawButton(this.mc, mouseX, mouseY, partialTicks);
            this.cancel.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }
    }
    
    public ModernBetaChunkGeneratorSettings.Factory getDefaultSettings() {
        return ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(this.defaultSettings.toString());
    }

    public String getSettingsString() {
        return this.settings.toString().replace("\n", "");
    }

    public void loadValues(String string) {
        if (string != null && !string.isEmpty()) {
            this.settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(string);
        } else {
            this.settings = new ModernBetaChunkGeneratorSettings.Factory();
        }
    }

    public void setSettingsModified(boolean settingsModified) {
        this.settingsModified = settingsModified;
        this.defaults.enabled = settingsModified;
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        if (!guiButton.enabled) {
            return;
        }
        
        switch (guiButton.id) {
            case GuiIds.FUNC_DONE:
                this.parent.chunkProviderSettingsJson = this.settings.toString();
                this.mc.displayGuiScreen(this.parent);
                break;
            case GuiIds.FUNC_RAND: // Randomize
                for (int page = 0; page < this.pageList.getSize(); ++page) {
                    GuiPageButtonList.GuiEntry guiEntry = this.pageList.getListEntry(page);
                    
                    Gui guiComponent = guiEntry.getComponent1();
                    Gui guiComponent2 = guiEntry.getComponent2();
                    Gui guiComponentFixed = this.pageList.getComponent(GuiIds.PG0_B_FIXED);
                    
                    if (guiComponent instanceof GuiButton && guiComponent != guiComponentFixed) {
                        GuiButton guiButtonComponent = (GuiButton)guiComponent;
                        
                        if (guiButtonComponent instanceof GuiSlider && ((GuiSlider)guiButtonComponent).enabled) {
                            float randomFloat = ((GuiSlider)guiButtonComponent).getSliderPosition() * (0.75f + this.random.nextFloat() * 0.5f) + (this.random.nextFloat() * 0.1f - 0.05f);
                            ((GuiSlider)guiButtonComponent).setSliderPosition(MathHelper.clamp(randomFloat, 0.0f, 1.0f));
                            
                        } else if (guiButtonComponent instanceof GuiListButton && ((GuiListButton)guiButtonComponent).enabled) {
                            this.randomClicked = true;
                            ((GuiListButton)guiButtonComponent).setValue(this.random.nextBoolean());
                            this.randomClicked = false;
                        }
                    }
                    
                    if (guiComponent2 instanceof GuiButton && guiComponent2 != guiComponentFixed) {
                        GuiButton guiButtonComponent = (GuiButton)guiComponent2;
                        
                        if (guiButtonComponent instanceof GuiSlider && ((GuiSlider)guiButtonComponent).enabled) {
                            float randomFloat = ((GuiSlider)guiButtonComponent).getSliderPosition() * (0.75f + this.random.nextFloat() * 0.5f) + (this.random.nextFloat() * 0.1f - 0.05f);
                            ((GuiSlider)guiButtonComponent).setSliderPosition(MathHelper.clamp(randomFloat, 0.0f, 1.0f));
                            
                        } else if (guiButtonComponent instanceof GuiListButton && ((GuiListButton)guiButtonComponent).enabled) {
                            this.randomClicked = true;
                            ((GuiListButton)guiButtonComponent).setValue(this.random.nextBoolean());
                            this.randomClicked = false;
                        }
                    }
                    
                    this.updateGuiButtons();
                }
                             
                break;
            case GuiIds.FUNC_PREV:
                this.pageList.previousPage();
                this.updatePageControls();
                break;
            case GuiIds.FUNC_NEXT:
                this.pageList.nextPage();
                this.updatePageControls();
                break;
            case GuiIds.FUNC_DFLT:
                if (!this.settingsModified) {
                    break;
                }
                this.enterConfirmation(GuiIds.FUNC_DFLT);
                break;
            case GuiIds.FUNC_PRST:
                this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this));
                break;
            case GuiIds.FUNC_CONF:
                this.exitConfirmation();
                break;
            case GuiIds.FUNC_CNCL:
                this.confirmMode = 0;
                this.exitConfirmation();
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        
        if (this.confirmMode != 0) {
            return;
        }
        
        switch (keyCode) {
            case 208:
                this.modifyFocusValue(-1.0f);
                break;
            case 200:
                this.modifyFocusValue(1.0f);
                break;
            default:
                this.pageList.onKeyPressed(typedChar, keyCode);
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.pageList.mouseClicked(mouseX, mouseY, mouseButton);
        this.clicked = true;
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.confirmMode != 0 || this.confirmDismissed) {
            return;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.pageList.mouseReleased(mouseX, mouseY, mouseButton);
        this.clicked = false;
        
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (this.confirmDismissed) {
            this.confirmDismissed = false;
            return;
        }
        if (this.confirmMode != 0) {
            return;
        }
    }

    private String getFormattedValue(int entry, float entryValue) {
        switch (entry) {
            case GuiIds.PG3_S_MAIN_NS_X:
            case GuiIds.PG3_S_MAIN_NS_Y:
            case GuiIds.PG3_S_MAIN_NS_Z:
            case GuiIds.PG3_S_DPTH_NS_X:
            case GuiIds.PG3_S_DPTH_NS_Z:
            case GuiIds.PG3_S_COORD_SCL:
            case GuiIds.PG3_S_HEIGH_SCL:
            case GuiIds.PG3_S_UPPER_LIM:
            case GuiIds.PG3_S_LOWER_LIM:
                
            case GuiIds.PG4_F_MAIN_NS_X:
            case GuiIds.PG4_F_MAIN_NS_Y:
            case GuiIds.PG4_F_MAIN_NS_Z:
            case GuiIds.PG4_F_DPTH_NS_X:
            case GuiIds.PG4_F_DPTH_NS_Z:
            case GuiIds.PG4_F_COORD_SCL:
            case GuiIds.PG4_F_HEIGH_SCL:
            case GuiIds.PG4_F_UPPER_LIM:
            case GuiIds.PG4_F_LOWER_LIM:
                return String.format("%5.3f", entryValue);
            
            case GuiIds.PG3_S_BASE_SIZE:
            case GuiIds.PG3_S_STRETCH_Y:
            case GuiIds.PG3_S_TEMP_SCL:
            case GuiIds.PG3_S_RAIN_SCL:
            case GuiIds.PG3_S_DETL_SCL:
            case GuiIds.PG3_S_B_DPTH_WT:
            case GuiIds.PG3_S_B_DPTH_OF:
            case GuiIds.PG3_S_B_SCL_WT:
            case GuiIds.PG3_S_B_SCL_OF:
            
            case GuiIds.PG4_F_BASE_SIZE:
            case GuiIds.PG4_F_STRETCH_Y:
            case GuiIds.PG4_F_TEMP_SCL:
            case GuiIds.PG4_F_RAIN_SCL:
            case GuiIds.PG4_F_DETL_SCL:
            case GuiIds.PG4_F_B_DPTH_WT:
            case GuiIds.PG4_F_B_DPTH_OF:
            case GuiIds.PG4_F_B_SCL_WT:
            case GuiIds.PG4_F_B_SCL_OF:
                return String.format("%2.3f", entryValue);
                
            case GuiIds.PG4_F_BIOME_SZ:
            case GuiIds.PG4_F_RIVER_SZ:
                return String.format("%d", entryValue);
                
            case GuiIds.PG0_S_LEVEL_WIDTH: return String.format("%d", LEVEL_WIDTHS[(int)entryValue]);
            case GuiIds.PG0_S_LEVEL_LENGTH: return String.format("%d", LEVEL_WIDTHS[(int)entryValue]);
            case GuiIds.PG0_S_LEVEL_HEIGHT: return String.format("%d", LEVEL_HEIGHTS[(int)entryValue]);
            
            case GuiIds.PG0_S_CHUNK: {
                String key = ModernBetaRegistries.CHUNK.getKeys().get((int)entryValue);
                boolean contains = Arrays.stream(ModernBetaBuiltInTypes.Chunk.values()).anyMatch(i -> i.id.equals(key));
                
                return contains ? ModernBetaBuiltInTypes.Chunk.valueOf(key.toUpperCase()).name : key;
            }
            case GuiIds.PG0_S_BIOME: {
                String key = ModernBetaRegistries.BIOME.getKeys().get((int)entryValue);
                boolean contains = Arrays.stream(ModernBetaBuiltInTypes.Biome.values()).anyMatch(i -> i.id.equals(key));
                
                return contains ? ModernBetaBuiltInTypes.Biome.valueOf(key.toUpperCase()).name : key;
            }
            case GuiIds.PG0_S_SURFACE: {
                String key = ModernBetaRegistries.SURFACE.getKeys().get((int)entryValue);
                boolean contains = Arrays.stream(ModernBetaBuiltInTypes.Surface.values()).anyMatch(i -> i.id.equals(key));
                
                return contains ? ModernBetaBuiltInTypes.Surface.valueOf(key.toUpperCase()).name : key;
            }
            case GuiIds.PG0_S_CARVER: {
                String key = ModernBetaRegistries.CARVER.getKeys().get((int)entryValue);
                boolean contains = Arrays.stream(ModernBetaBuiltInTypes.Carver.values()).anyMatch(i -> i.id.equals(key));
                
                return contains ? ModernBetaBuiltInTypes.Carver.valueOf(key.toUpperCase()).name : key;
            }
            case GuiIds.PG0_S_LEVEL_THEME: {
                String key = IndevTheme.values()[(int)entryValue].id;
                return key.substring(0, 1).toUpperCase() + key.substring(1);
            }
            case GuiIds.PG0_S_LEVEL_TYPE: {
                String key = IndevType.values()[(int)entryValue].id;
                return key.substring(0, 1).toUpperCase() + key.substring(1);
            }
            case GuiIds.PG0_S_LEVEL_HOUSE: {
                String key = IndevHouse.values()[(int)entryValue].id;
                return key.substring(0, 1).toUpperCase() + key.substring(1);
            }
            
            default: return String.format("%d", (int)entryValue);
        }
    }

    private void restoreDefaults() {
        String defaultPreset = ModernBetaConfig.guiOptions.defaultPreset;
        this.settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(defaultPreset);
        
        this.createPagedList();
        this.setSettingsModified(false);
    }
    
    private void enterConfirmation(int id) {
        this.confirmMode = id;
        this.setConfirmationControls(true);
    }
    
    private void exitConfirmation() throws IOException {
        switch (this.confirmMode) {
            case GuiIds.FUNC_DONE:
                this.actionPerformed((GuiButton)this.pageList.getComponent(GuiIds.FUNC_DONE));
                break;
            case GuiIds.FUNC_DFLT:
                this.restoreDefaults();
                this.mc.displayGuiScreen(new GuiScreenCustomizeWorld(this.parent, this.settings.toString()));
                break;
        }
        
        this.confirmMode = 0;
        this.confirmDismissed = true;
        this.setConfirmationControls(false);
    }
    
    private void setConfirmationControls(boolean setConfirm) {
        this.confirm.visible = setConfirm;
        this.cancel.visible = setConfirm;
        this.randomize.enabled = !setConfirm;
        this.done.enabled = !setConfirm;
        this.previousPage.enabled = !setConfirm;
        this.nextPage.enabled = !setConfirm;
        this.defaults.enabled = (this.settingsModified && !setConfirm);
        this.presets.enabled = !setConfirm;
        this.pageList.setActive(!setConfirm);
    }
    
    private void updatePageControls() {
        this.previousPage.enabled = (this.pageList.getPage() != 0);
        this.nextPage.enabled = (this.pageList.getPage() != this.pageList.getPageCount() - 1);
        this.subtitle = I18n.format("book.pageIndicator", this.pageList.getPage() + 1, this.pageList.getPageCount());
        this.pageTitle = this.pageNames[this.pageList.getPage()];
        this.randomize.enabled = (this.pageList.getPage() < this.pageList.getPageCount() - 2);
    }
    
    private void modifyFocusValue(float amount) {
        Gui guiComponent = this.pageList.getFocusedControl();
        if (!(guiComponent instanceof GuiTextField)) {
            return;
        }
        
        float increment = amount;
        
        if (GuiScreen.isShiftKeyDown()) {
            increment *= 0.1f;
            
            if (GuiScreen.isCtrlKeyDown()) {
                increment *= 0.1f;
            }
            
        } else if (GuiScreen.isCtrlKeyDown()) {
            increment *= 10.0f;
            
            if (GuiScreen.isAltKeyDown()) {
                increment *= 10.0f;
            }
            
        }
        
        GuiTextField guiText = (GuiTextField)guiComponent;
        Float guiTextValue = Floats.tryParse(guiText.getText());
        
        if (guiTextValue == null) {
            return;
        }
        
        guiTextValue += increment;
        int guiTextId = guiText.getId();
        String guiTextString = this.getFormattedValue(guiText.getId(), guiTextValue);
        guiText.setText(guiTextString);
        
        this.setEntryValue(guiTextId, guiTextString);
    }
    
    private void setInitialTextButton(GuiPageButtonList pageList, int id, String initial) {
        ((GuiButton)pageList.getComponent(id)).displayString = initial;
    }
    
    private void updateGuiButtons() {
        // Set default enabled for certain options
        if (this.pageList != null) {
            for (Integer key : GuiIds.getGuiIds()) {
                boolean enabled = GuiIds.test(settings, key);
                
                this.setButtonEnabled(key, enabled);
                this.setFieldEnabled(key, enabled);
            }
        }
    }
    
    private void setButtonEnabled(int entry, boolean enabled) {
        Gui gui = this.pageList.getComponent(entry);
        if (gui != null && gui instanceof GuiButton) {
            ((GuiButton)gui).enabled = enabled;
        }
    }
    
    private void setFieldEnabled(int entry, boolean enabled) {
        Gui gui = this.pageList.getComponent(entry);
        if (gui != null && gui instanceof GuiTextField) {
            ((GuiTextField)gui).setEnabled(enabled);
        }
    }
    
    private int getLevelSeaLevel() {
        String chunkSource = this.settings.chunkSource;
        String levelType = this.settings.levelType;
        int levelHeight = this.settings.levelHeight;

        int levelSeaLevel = -1;
        if (chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.id)) {
            levelSeaLevel = levelType.equals(IndevType.FLOATING.id) ? 0 : levelHeight - 32;
        } else if (chunkSource.equals(ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.id)) {
            levelSeaLevel = levelHeight / 2;
        } else {
            levelSeaLevel = -1;
        }
        
        return levelSeaLevel;
    }
    
    private String getFormattedBiomeName(String registryName, boolean prefix, int truncateLen) {
        Biome biome = BiomeUtil.getBiome(registryName, "generator");
        String biomeName = biome.getBiomeName();
        
        // Truncate if biome name is too long to fit in button
        if (truncateLen > 0 && biomeName.length() > truncateLen) {
            biomeName = biomeName.substring(0, truncateLen) + "...";
        }
        
        return prefix ? String.format("%s: %s", I18n.format(PREFIX + "fixedBiome"), biomeName) : biomeName;
    }
    
    private void playSound() {
        if (!this.clicked && !this.randomClicked) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    private static int getNdx(int[] arr, int val) {
        for (int i = 0; i < arr.length; ++i) {
            if (val == arr[i])
                return i;
        }
        
        return 0;
    }
}