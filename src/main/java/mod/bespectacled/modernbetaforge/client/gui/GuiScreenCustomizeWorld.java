package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.property.Property;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.property.StringProperty;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.util.SoundUtil;
import mod.bespectacled.modernbetaforge.world.biome.layer.GenLayerType;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevTheme;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevType;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizeWorld extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder {
    
    private static final String PREFIX = "createWorld.customize.custom.";
    private static final String PREFIX_TAB = "createWorld.customize.custom.tab.";
    private static final String PREFIX_LABEL = "createWorld.customize.custom.label.";
    private static final DecimalFormat DF_THREE = new DecimalFormat("#.###");
    private static final DecimalFormat DF_ONE = new DecimalFormat("#.#");
    
    private static final int PAGE_TITLE_HEIGHT = 6;
    
    private static final int PAGELIST_PADDING_TOP = 40;
    private static final int PAGELIST_PADDING_BOTTOM = 32;
    private static final int PAGELIST_SCROLLBAR_PADDING = 24;
    private static final int BIOME_TRUNCATE_LEN = 18;

    private static final int BUTTON_WIDTH = 70;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SLOT_HEIGHT = 25;
    
    private static final int TAB_SPACE = 2;
    private static final int TAB_HEIGHT = 18;
    private static final int TAB_BUTTON_WIDTH = 44;
    private static final int TAB_BUTTON_HEIGHT = 20;
    
    private final GuiCreateWorld parent;
    
    private final Predicate<String> floatFilter;
    private final Predicate<String> intFilter;
    
    private final ModernBetaGeneratorSettings.Factory defaultSettings;
    private ModernBetaGeneratorSettings.Factory settings;
    
    private final Random random;
    
    protected String title;
    protected String subtitle;
    protected String pageTitle;
    protected String[] pageNames;
    protected Map<Integer, GuiButton> pageTabMap;
    
    private GuiPageButtonList pageList;
    private GuiButton done;
    private GuiButton randomize;
    private GuiButton defaults;
    private GuiButton confirm;
    private GuiButton cancel;
   private GuiButton presets;
    private GuiButton preview;
    
    private boolean settingsModified;
    private int confirmMode;
    private boolean confirmDismissed;
    
    private boolean clicked;
    private boolean randomClicked;
    private boolean tabClicked;
    
    private int customId;
    private BiMap<Integer, ResourceLocation> customIds;
    
    public GuiScreenCustomizeWorld(GuiScreen parent, String string) {
        this.title = I18n.format("options.customizeTitle");
        this.subtitle = "Page 1 of 6";
        this.pageTitle = "Basic Settings";
        this.pageNames = new String[]{
            I18n.format(PREFIX_TAB + "page0"),
            I18n.format(PREFIX_TAB + "page1"),
            I18n.format(PREFIX_TAB + "page2"),
            I18n.format(PREFIX_TAB + "page3"),
            I18n.format(PREFIX_TAB + "page4"),
            I18n.format(PREFIX_TAB + "page5"),
            I18n.format(PREFIX_TAB + "page6"),
            I18n.format(PREFIX_TAB + "page7")
        };
        
        this.floatFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                Float entryValue = Floats.tryParse(entryString);
                
                return entryString.isEmpty() || (entryValue != null && Floats.isFinite(entryValue));
            }
        };
        
        this.intFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                Integer entryValue = Ints.tryParse(entryString);
                
                return entryString.isEmpty() || entryValue != null;
            }
        };
        
        String defaultPreset = ModernBetaConfig.guiOptions.defaultPreset;
        this.defaultSettings = ModernBetaGeneratorSettings.Factory.jsonToFactory(defaultPreset);
        this.random = new Random();
        this.parent = (GuiCreateWorld)parent;
        
        this.loadValues(string);
    }
    
    private void createPagedList() {
        this.customId = GuiIdentifiers.CUSTOM_INITIAL_ID;
        this.customIds = HashBiMap.create();
        
        int chunkSourceId = ModernBetaRegistries.CHUNK_SOURCE.getKeys().indexOf(new ResourceLocation(this.settings.chunkSource));
        int biomeSourceId = ModernBetaRegistries.BIOME_SOURCE.getKeys().indexOf(new ResourceLocation(this.settings.biomeSource));
        int surfaceBuilderId = ModernBetaRegistries.SURFACE_BUILDER.getKeys().indexOf(new ResourceLocation(this.settings.surfaceBuilder));
        int caveCarverId = ModernBetaRegistries.CAVE_CARVER.getKeys().indexOf(new ResourceLocation(this.settings.caveCarver));
        
        int levelThemeId = IndevTheme.fromId(this.settings.levelTheme).ordinal();
        int levelTypeId = IndevType.fromId(this.settings.levelType).ordinal();
        int levelWidth = getNdx(ModernBetaGeneratorSettings.LEVEL_WIDTHS, this.settings.levelWidth);
        int levelLength = getNdx(ModernBetaGeneratorSettings.LEVEL_WIDTHS, this.settings.levelLength);
        int levelHeight = getNdx(ModernBetaGeneratorSettings.LEVEL_HEIGHTS, this.settings.levelHeight);
        int levelHouseId = IndevHouse.fromId(this.settings.levelHouse).ordinal();
        int levelSeaLevel = this.getLevelSeaLevel();
        String levelSeaLevelStr = levelSeaLevel == -1 ? "" : Integer.toString(levelSeaLevel);
        
        int layerTypeId = GenLayerType.fromId(this.settings.layerType).ordinal();
        
        List<String> loadedMods = new ArrayList<>(ModCompat.LOADED_MODS.keySet());
        StringBuilder loadedModsList = new StringBuilder();
        
        if (loadedMods.isEmpty()) { 
            loadedModsList.append("n/a");
        } else if (loadedMods.size() > 0) {
            loadedModsList.append(loadedMods.get(0));
        }
        
        boolean useMenu = ModernBetaConfig.guiOptions.useMenusForBasicSettings;
        GuiPageButtonList.GuiListEntry chunkEntry = useMenu ? 
            createGuiButton(GuiIdentifiers.PG0_B_CHUNK, NbtTags.CHUNK_SOURCE, true) :
            createGuiSlider(GuiIdentifiers.PG0_S_CHUNK, NbtTags.CHUNK_SOURCE, 0f, ModernBetaRegistries.CHUNK_SOURCE.getKeys().size() - 1, chunkSourceId, this);
        GuiPageButtonList.GuiListEntry biomeEntry = useMenu ? 
            createGuiButton(GuiIdentifiers.PG0_B_BIOME, NbtTags.BIOME_SOURCE, true) :
            createGuiSlider(GuiIdentifiers.PG0_S_BIOME, NbtTags.BIOME_SOURCE, 0f, ModernBetaRegistries.BIOME_SOURCE.getKeys().size() - 1, biomeSourceId, this);
        GuiPageButtonList.GuiListEntry surfaceEntry = useMenu ? 
            createGuiButton(GuiIdentifiers.PG0_B_SURFACE, NbtTags.SURFACE_BUILDER, true) :
            createGuiSlider(GuiIdentifiers.PG0_S_SURFACE, NbtTags.SURFACE_BUILDER, 0f, ModernBetaRegistries.SURFACE_BUILDER.getKeys().size() - 1, surfaceBuilderId, this);
        GuiPageButtonList.GuiListEntry carverEntry = useMenu ? 
            createGuiButton(GuiIdentifiers.PG0_B_CARVER, NbtTags.CAVE_CARVER, true) :
            createGuiSlider(GuiIdentifiers.PG0_S_CARVER, NbtTags.CAVE_CARVER, 0f, ModernBetaRegistries.CAVE_CARVER.getKeys().size() - 1, caveCarverId, this);
        
        GuiPageButtonList.GuiListEntry[] pageBasic = {
            chunkEntry,
            biomeEntry,
            surfaceEntry,
            createGuiButton(GuiIdentifiers.PG0_B_FIXED, NbtTags.SINGLE_BIOME, true),
            carverEntry,
            null,

            createGuiLabel(GuiIdentifiers.PG0_L_BIOME_REPLACEMENT, "page0", "biomeReplacement"),
            null,
            createGuiButton(GuiIdentifiers.PG0_B_USE_OCEAN, NbtTags.REPLACE_OCEAN_BIOMES, this.settings.replaceOceanBiomes),
            createGuiButton(GuiIdentifiers.PG0_B_USE_BEACH, NbtTags.REPLACE_BEACH_BIOMES, this.settings.replaceBeachBiomes),
            
            createGuiLabel(GuiIdentifiers.PG0_L_BASIC_FEATURES, "page0", "overworld"),
            null,
            createGuiSlider(GuiIdentifiers.PG0_S_SEA_LEVEL, NbtTags.SEA_LEVEL, ModernBetaGeneratorSettings.MIN_SEA_LEVEL, ModernBetaGeneratorSettings.MAX_SEA_LEVEL, (float)this.settings.seaLevel, this),
            createGuiButton(GuiIdentifiers.PG0_B_USE_SANDSTONE, NbtTags.USE_SANDSTONE, this.settings.useSandstone),
            createGuiSlider(GuiIdentifiers.PG0_S_CAVE_WIDTH, NbtTags.CAVE_WIDTH, ModernBetaGeneratorSettings.MIN_CAVE_WIDTH, ModernBetaGeneratorSettings.MAX_CAVE_WIDTH, this.settings.caveWidth, this),
            createGuiSlider(GuiIdentifiers.PG0_S_CAVE_HEIGHT, NbtTags.CAVE_HEIGHT, ModernBetaGeneratorSettings.MIN_CAVE_HEIGHT, ModernBetaGeneratorSettings.MAX_CAVE_HEIGHT, (float)this.settings.caveHeight, this),
            createGuiSlider(GuiIdentifiers.PG0_S_CAVE_COUNT, NbtTags.CAVE_COUNT, ModernBetaGeneratorSettings.MIN_CAVE_COUNT, ModernBetaGeneratorSettings.MAX_CAVE_COUNT, (float)this.settings.caveCount, this),
            createGuiSlider(GuiIdentifiers.PG0_S_CAVE_CHANCE, NbtTags.CAVE_CHANCE, ModernBetaGeneratorSettings.MIN_CAVE_CHANCE, ModernBetaGeneratorSettings.MAX_CAVE_CHANCE, (float)this.settings.caveChance, this),
            createGuiButton(GuiIdentifiers.PG0_B_USE_RAVINES, NbtTags.USE_RAVINES, this.settings.useRavines),
            createGuiButton(GuiIdentifiers.PG0_B_USE_SHAFTS, NbtTags.USE_MINESHAFTS, this.settings.useMineShafts),
            createGuiButton(GuiIdentifiers.PG0_B_USE_VILLAGES, NbtTags.USE_VILLAGES, this.settings.useVillages),
            createGuiButton(GuiIdentifiers.PG0_B_USE_VILLAGE_VARIANTS, NbtTags.USE_VILLAGE_VARIANTS, this.settings.useVillageVariants),
            createGuiButton(GuiIdentifiers.PG0_B_USE_HOLDS, NbtTags.USE_STRONGHOLDS, this.settings.useStrongholds),
            createGuiButton(GuiIdentifiers.PG0_B_USE_TEMPLES, NbtTags.USE_TEMPLES, this.settings.useTemples),
            createGuiButton(GuiIdentifiers.PG0_B_USE_MONUMENTS, NbtTags.USE_MONUMENTS, this.settings.useMonuments),
            createGuiButton(GuiIdentifiers.PG0_B_USE_MANSIONS, NbtTags.USE_MANSIONS, this.settings.useMansions),
            createGuiButton(GuiIdentifiers.PG0_B_USE_DUNGEONS, NbtTags.USE_DUNGEONS, this.settings.useDungeons),
            createGuiSlider(GuiIdentifiers.PG0_S_DUNGEON_CHANCE, NbtTags.DUNGEON_CHANCE, ModernBetaGeneratorSettings.MIN_DUNGEON_CHANCE, ModernBetaGeneratorSettings.MAX_DUNGEON_CHANCE, (float)this.settings.dungeonChance, this),
            createGuiButton(GuiIdentifiers.PG0_B_USE_WATER_LAKES, NbtTags.USE_WATER_LAKES, this.settings.useWaterLakes),
            createGuiSlider(GuiIdentifiers.PG0_S_WATER_LAKE_CHANCE, NbtTags.WATER_LAKE_CHANCE, ModernBetaGeneratorSettings.MIN_WATER_LAKE_CHANCE, ModernBetaGeneratorSettings.MAX_WATER_LAKE_CHANCE, (float)this.settings.waterLakeChance, this),
            createGuiButton(GuiIdentifiers.PG0_B_USE_LAVA_LAKES, NbtTags.USE_LAVA_LAKES, this.settings.useLavaLakes),
            createGuiSlider(GuiIdentifiers.PG0_S_LAVA_LAKE_CHANCE, NbtTags.LAVA_LAKE_CHANCE, ModernBetaGeneratorSettings.MIN_LAVA_LAKE_CHANCE, ModernBetaGeneratorSettings.MAX_LAVA_LAKE_CHANCE, (float)this.settings.lavaLakeChance, this),
            createGuiButton(GuiIdentifiers.PG0_B_USE_LAVA_OCEANS, NbtTags.USE_LAVA_OCEANS, this.settings.useLavaOceans),
            null,
            createGuiLabel(GuiIdentifiers.PG0_L_NETHER_FEATURES, "page0", "nether"),
            null,
            createGuiButton(GuiIdentifiers.PG0_B_USE_OLD_NETHER, NbtTags.USE_OLD_NETHER, this.settings.useOldNether),
            createGuiButton(GuiIdentifiers.PG0_B_USE_NETHER_CAVES, NbtTags.USE_NETHER_CAVES, this.settings.useNetherCaves),
            createGuiButton(GuiIdentifiers.PG0_B_USE_FORTRESSES, NbtTags.USE_FORTRESSES, this.settings.useFortresses),
            createGuiButton(GuiIdentifiers.PG0_B_USE_LAVA_POCKETS, NbtTags.USE_LAVA_POCKETS, this.settings.useLavaPockets)
        };
        
        GuiPageButtonList.GuiListEntry[] pageChunk = {
            createGuiLabel(GuiIdentifiers.PG1_L_INFDEV_227_FEATURES, "page1", "infdev227"),
            null,
            createGuiButton(GuiIdentifiers.PG1_B_USE_INFDEV_WALLS, NbtTags.USE_INFDEV_WALLS, this.settings.useInfdevWalls),
            createGuiButton(GuiIdentifiers.PG1_B_USE_INFDEV_PYRAMIDS, NbtTags.USE_INFDEV_PYRAMIDS, this.settings.useInfdevPyramids),
            
            createGuiLabel(GuiIdentifiers.PG1_L_INDEV_FEATURES, "page1", "indev"),
            null,
            createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_THEME, NbtTags.LEVEL_THEME, 0f, IndevTheme.values().length - 1, levelThemeId, this),
            createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_TYPE, NbtTags.LEVEL_TYPE, 0f, IndevType.values().length - 1, levelTypeId, this),
            createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_WIDTH, NbtTags.LEVEL_WIDTH, 0f, ModernBetaGeneratorSettings.LEVEL_WIDTHS.length - 1, levelWidth, this),
            createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_LENGTH, NbtTags.LEVEL_LENGTH, 0f, ModernBetaGeneratorSettings.LEVEL_WIDTHS.length - 1, levelLength, this),
            createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_HEIGHT, NbtTags.LEVEL_HEIGHT, 0f, ModernBetaGeneratorSettings.LEVEL_HEIGHTS.length - 1, levelHeight, this),
            createGuiLabelNoPrefix(GuiIdentifiers.PG0_L_INDEV_SEA_LEVEL, String.format("%s: %s", I18n.format(PREFIX + "seaLevel"), levelSeaLevelStr)),                                                                                                                                     
            createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_HOUSE, NbtTags.LEVEL_HOUSE, 0f, IndevHouse.values().length - 1, levelHouseId, this),
            createGuiButton(GuiIdentifiers.PG1_B_USE_INDEV_CAVES, NbtTags.USE_INDEV_CAVES, this.settings.useIndevCaves),
            
            createGuiLabel(GuiIdentifiers.PG1_L_RELEASE_FEATURES, "page1", "release"),
            null,
            createGuiSlider(GuiIdentifiers.PG1_S_LAYER_SZ, NbtTags.LAYER_SIZE, ModernBetaGeneratorSettings.MIN_BIOME_SIZE, ModernBetaGeneratorSettings.MAX_BIOME_SIZE, this.settings.layerSize, this),
            createGuiSlider(GuiIdentifiers.PG1_S_RIVER_SZ, "riverRarity", ModernBetaGeneratorSettings.MIN_RIVER_SIZE, ModernBetaGeneratorSettings.MAX_RIVER_SIZE, this.settings.riverSize, this),
            createGuiSlider(GuiIdentifiers.PG1_S_LAYER_TYPE, NbtTags.LAYER_TYPE, 0f, GenLayerType.values().length - 1, layerTypeId, this)
        };
        
        GuiPageButtonList.GuiListEntry[] pageBiome = {
            createGuiLabel(GuiIdentifiers.PG2_L_BETA, "page2", "beta"),
            null,
            createGuiButton(GuiIdentifiers.PG2_B_USE_GRASS, NbtTags.USE_TALL_GRASS, this.settings.useTallGrass),
            createGuiButton(GuiIdentifiers.PG2_B_USE_FLOWERS, NbtTags.USE_NEW_FLOWERS, this.settings.useNewFlowers),
            createGuiButton(GuiIdentifiers.PG2_B_USE_PADS, NbtTags.USE_LILY_PADS, this.settings.useLilyPads),
            createGuiButton(GuiIdentifiers.PG2_B_USE_MELONS, NbtTags.USE_MELONS, this.settings.useMelons),

            createGuiButton(GuiIdentifiers.PG2_B_USE_WELLS, NbtTags.USE_DESERT_WELLS, this.settings.useDesertWells),
            createGuiButton(GuiIdentifiers.PG2_B_USE_FOSSILS, NbtTags.USE_FOSSILS, this.settings.useFossils),

            createGuiButton(GuiIdentifiers.PG2_B_USE_BIRCH, NbtTags.USE_BIRCH_TREES, this.settings.useBirchTrees),
            createGuiButton(GuiIdentifiers.PG2_B_USE_PINE, NbtTags.USE_PINE_TREES, this.settings.usePineTrees),
            createGuiButton(GuiIdentifiers.PG2_B_USE_SWAMP, NbtTags.USE_SWAMP_TREES, this.settings.useSwampTrees),
            createGuiButton(GuiIdentifiers.PG2_B_USE_JUNGLE, NbtTags.USE_JUNGLE_TREES, this.settings.useJungleTrees),
            createGuiButton(GuiIdentifiers.PG2_B_USE_ACACIA, NbtTags.USE_ACACIA_TREES, this.settings.useAcaciaTrees),
            null,
            
            createGuiLabel(GuiIdentifiers.PG2_L_RELEASE, "page2", "release"),
            null,
            createGuiButton(GuiIdentifiers.PG2_B_USE_MODDED_BIOMES, NbtTags.USE_MODDED_BIOMES, this.settings.useModdedBiomes),
            createGuiSlider(GuiIdentifiers.PG2_S_BIOME_SZ, NbtTags.BIOME_SIZE, ModernBetaGeneratorSettings.MIN_BIOME_SIZE, ModernBetaGeneratorSettings.MAX_BIOME_SIZE, this.settings.biomeSize, this),
        
            createGuiLabel(GuiIdentifiers.PG2_L_MOBS, "page2", "mobSpawn"),
            null,
            createGuiButton(GuiIdentifiers.PG2_B_SPAWN_CREATURE, NbtTags.SPAWN_NEW_CREATURE_MOBS, this.settings.spawnNewCreatureMobs),
            createGuiButton(GuiIdentifiers.PG2_B_SPAWN_MONSTER, NbtTags.SPAWN_NEW_MONSTER_MOBS, this.settings.spawnNewMonsterMobs),
            createGuiButton(GuiIdentifiers.PG2_B_SPAWN_WATER, NbtTags.SPAWN_WATER_MOBS, this.settings.spawnWaterMobs),
            createGuiButton(GuiIdentifiers.PG2_B_SPAWN_AMBIENT, NbtTags.SPAWN_AMBIENT_MOBS, this.settings.spawnAmbientMobs),
            createGuiButton(GuiIdentifiers.PG2_B_SPAWN_WOLVES, NbtTags.SPAWN_WOLVES, this.settings.spawnWolves),
            null
        };
        
        GuiPageButtonList.GuiListEntry[] pageOre = {
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_CLAY_NAME, String.format("%s (%s)", I18n.format("tile.clay.name"), I18n.format(PREFIX + "modernBeta"))),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_CLAY_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.claySize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_CLAY_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.clayCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_CLAY_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.clayMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_CLAY_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.clayMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_DIRT_NAME, I18n.format("tile.dirt.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_DIRT_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.dirtSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIRT_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.dirtCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIRT_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.dirtMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIRT_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.dirtMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_GRAV_NAME, I18n.format("tile.gravel.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_GRAV_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.gravelSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GRAV_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.gravelCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GRAV_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.gravelMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GRAV_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.gravelMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_GRAN_NAME, I18n.format("tile.stone.granite.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_GRAN_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.graniteSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GRAN_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.graniteCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GRAN_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.graniteMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GRAN_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.graniteMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_DIOR_NAME, I18n.format("tile.stone.diorite.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_DIOR_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.dioriteSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIOR_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.dioriteCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIOR_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.dioriteMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIOR_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.dioriteMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_ANDE_NAME, I18n.format("tile.stone.andesite.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_ANDE_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.andesiteSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_ANDE_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.andesiteCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_ANDE_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.andesiteMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_ANDE_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.andesiteMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_COAL_NAME, I18n.format("tile.oreCoal.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_COAL_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.coalSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_COAL_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.coalCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_COAL_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.coalMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_COAL_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.coalMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_IRON_NAME, I18n.format("tile.oreIron.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_IRON_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.ironSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_IRON_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.ironCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_IRON_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.ironMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_IRON_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.ironMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_GOLD_NAME, I18n.format("tile.oreGold.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_GOLD_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.goldSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GOLD_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.goldCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GOLD_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.goldMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_GOLD_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.goldMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_REDS_NAME, I18n.format("tile.oreRedstone.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_REDS_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.redstoneSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_REDS_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.redstoneCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_REDS_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.redstoneMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_REDS_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.redstoneMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_DIAM_NAME, I18n.format("tile.oreDiamond.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_DIAM_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.diamondSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIAM_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.diamondCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIAM_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.diamondMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_DIAM_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.diamondMaxHeight, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_LAPS_NAME, I18n.format("tile.oreLapis.name")),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_LAPS_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.lapisSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_LAPS_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.lapisCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_LAPS_CTR, "center", ModernBetaGeneratorSettings.MIN_ORE_CENTER, ModernBetaGeneratorSettings.MAX_ORE_CENTER, (float)this.settings.lapisCenterHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_LAPS_SPR, "spread", ModernBetaGeneratorSettings.MIN_ORE_SPREAD, ModernBetaGeneratorSettings.MAX_ORE_SPREAD, (float)this.settings.lapisSpread, this),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_EMER_NAME, String.format("%s (%s)", I18n.format("tile.oreEmerald.name"), I18n.format(PREFIX + "modernBeta"))),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_EMER_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.emeraldSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_EMER_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.emeraldCount, this),
            createGuiSlider(GuiIdentifiers.PG3_S_EMER_MIN, "minHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.emeraldMinHeight, this),
            createGuiSlider(GuiIdentifiers.PG3_S_EMER_MAX, "maxHeight", ModernBetaGeneratorSettings.MIN_ORE_HEIGHT, ModernBetaGeneratorSettings.MAX_ORE_HEIGHT, (float)this.settings.emeraldMaxHeight, this),

            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_QRTZ_NAME, String.format("%s (%s)", I18n.format("tile.netherquartz.name"), I18n.format(PREFIX + "useOldNether"))),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_QRTZ_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.quartzSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_QRTZ_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.quartzCount, this),

            createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_MGMA_NAME, String.format("%s (%s)", I18n.format("tile.magma.name"), I18n.format(PREFIX + "useOldNether"))),
            null,
            createGuiSlider(GuiIdentifiers.PG3_S_MGMA_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.magmaSize, this),
            createGuiSlider(GuiIdentifiers.PG3_S_MGMA_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.magmaCount, this),
        };
        
        GuiPageButtonList.GuiListEntry[] pageNoise0 = {
            createGuiSlider(GuiIdentifiers.PG4_S_MAIN_NS_X, NbtTags.MAIN_NOISE_SCALE_X, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE, this.settings.mainNoiseScaleX, this),
            createGuiSlider(GuiIdentifiers.PG4_S_MAIN_NS_Y, NbtTags.MAIN_NOISE_SCALE_Y, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE, this.settings.mainNoiseScaleY, this),
            createGuiSlider(GuiIdentifiers.PG4_S_MAIN_NS_Z, NbtTags.MAIN_NOISE_SCALE_Z, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE, this.settings.mainNoiseScaleZ, this),
            createGuiSlider(GuiIdentifiers.PG4_S_SCLE_NS_X, NbtTags.SCALE_NOISE_SCALE_X, ModernBetaGeneratorSettings.MIN_SCALE_NOISE, ModernBetaGeneratorSettings.MAX_SCALE_NOISE, this.settings.scaleNoiseScaleX, this),
            createGuiSlider(GuiIdentifiers.PG4_S_SCLE_NS_Z, NbtTags.SCALE_NOISE_SCALE_Z, ModernBetaGeneratorSettings.MIN_SCALE_NOISE, ModernBetaGeneratorSettings.MAX_SCALE_NOISE, this.settings.scaleNoiseScaleZ, this),
            createGuiSlider(GuiIdentifiers.PG4_S_DPTH_NS_X, NbtTags.DEPTH_NOISE_SCALE_X, ModernBetaGeneratorSettings.MIN_DEPTH_NOISE, ModernBetaGeneratorSettings.MAX_DEPTH_NOISE, this.settings.depthNoiseScaleX, this),
            createGuiSlider(GuiIdentifiers.PG4_S_DPTH_NS_Z, NbtTags.DEPTH_NOISE_SCALE_Z, ModernBetaGeneratorSettings.MIN_DEPTH_NOISE, ModernBetaGeneratorSettings.MAX_DEPTH_NOISE, this.settings.depthNoiseScaleZ, this),
            createGuiSlider(GuiIdentifiers.PG4_S_BASE_SIZE, NbtTags.BASE_SIZE, ModernBetaGeneratorSettings.MIN_BASE_SIZE, ModernBetaGeneratorSettings.MAX_BASE_SIZE, this.settings.baseSize, this),
            createGuiSlider(GuiIdentifiers.PG4_S_COORD_SCL, NbtTags.COORDINATE_SCALE, ModernBetaGeneratorSettings.MIN_COORD_SCALE, ModernBetaGeneratorSettings.MAX_COORD_SCALE, this.settings.coordinateScale, this),
            createGuiSlider(GuiIdentifiers.PG4_S_HEIGH_SCL, NbtTags.HEIGHT_SCALE, ModernBetaGeneratorSettings.MIN_HEIGHT_SCALE, ModernBetaGeneratorSettings.MAX_HEIGHT_SCALE, this.settings.heightScale, this),
            createGuiSlider(GuiIdentifiers.PG4_S_STRETCH_Y, NbtTags.STRETCH_Y, ModernBetaGeneratorSettings.MIN_STRETCH_Y, ModernBetaGeneratorSettings.MAX_STRETCH_Y, this.settings.stretchY, this),
            createGuiSlider(GuiIdentifiers.PG4_S_UPPER_LIM, NbtTags.UPPER_LIMIT_SCALE, ModernBetaGeneratorSettings.MIN_LIMIT_SCALE, ModernBetaGeneratorSettings.MAX_LIMIT_SCALE, this.settings.upperLimitScale, this),
            createGuiSlider(GuiIdentifiers.PG4_S_LOWER_LIM, NbtTags.LOWER_LIMIT_SCALE, ModernBetaGeneratorSettings.MIN_LIMIT_SCALE, ModernBetaGeneratorSettings.MAX_LIMIT_SCALE, this.settings.lowerLimitScale, this),
            createGuiSlider(GuiIdentifiers.PG4_S_HEIGH_LIM, NbtTags.HEIGHT, ModernBetaGeneratorSettings.MIN_HEIGHT, ModernBetaGeneratorSettings.MAX_HEIGHT, this.settings.height, this),
            
            createGuiLabel(GuiIdentifiers.PG4_L_BETA_LABL, "page4", "beta"),
            null,
            createGuiSlider(GuiIdentifiers.PG4_S_TEMP_SCL, NbtTags.TEMP_NOISE_SCALE, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE, this.settings.tempNoiseScale, this),
            createGuiSlider(GuiIdentifiers.PG4_S_RAIN_SCL, NbtTags.RAIN_NOISE_SCALE, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE, this.settings.rainNoiseScale, this),
            createGuiSlider(GuiIdentifiers.PG4_S_DETL_SCL, NbtTags.DETAIL_NOISE_SCALE, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE, this.settings.detailNoiseScale, this),
            null,
            
            createGuiLabel(GuiIdentifiers.PG4_L_RELE_LABL, "page4", "release"),
            null,
            createGuiSlider(GuiIdentifiers.PG4_S_B_DPTH_WT, NbtTags.BIOME_DEPTH_WEIGHT, ModernBetaGeneratorSettings.MIN_BIOME_WEIGHT, ModernBetaGeneratorSettings.MAX_BIOME_WEIGHT, this.settings.biomeDepthWeight, this),
            createGuiSlider(GuiIdentifiers.PG4_S_B_DPTH_OF, NbtTags.BIOME_DEPTH_OFFSET, ModernBetaGeneratorSettings.MIN_BIOME_OFFSET, ModernBetaGeneratorSettings.MAX_BIOME_OFFSET, this.settings.biomeDepthOffset, this),
            createGuiSlider(GuiIdentifiers.PG4_S_B_SCLE_WT, NbtTags.BIOME_SCALE_WEIGHT, ModernBetaGeneratorSettings.MIN_BIOME_WEIGHT, ModernBetaGeneratorSettings.MAX_BIOME_WEIGHT, this.settings.biomeScaleWeight, this),
            createGuiSlider(GuiIdentifiers.PG4_S_B_SCLE_OF, NbtTags.BIOME_SCALE_OFFSET, ModernBetaGeneratorSettings.MIN_BIOME_OFFSET, ModernBetaGeneratorSettings.MAX_BIOME_OFFSET, this.settings.biomeScaleOffset, this),
            createGuiButton(GuiIdentifiers.PG4_B_USE_BDS, NbtTags.USE_BIOME_DEPTH_SCALE, this.settings.useBiomeDepthScale)
        };
        
        GuiPageButtonList.GuiListEntry[] pageNoise1 = {
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_MAIN_NS_X, I18n.format(PREFIX + NbtTags.MAIN_NOISE_SCALE_X) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_MAIN_NS_X, String.format("%5.3f", this.settings.mainNoiseScaleX), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_MAIN_NS_Y, I18n.format(PREFIX + NbtTags.MAIN_NOISE_SCALE_Y) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_MAIN_NS_Y, String.format("%5.3f", this.settings.mainNoiseScaleY), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_MAIN_NS_Z, I18n.format(PREFIX + NbtTags.MAIN_NOISE_SCALE_Z) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_MAIN_NS_Z, String.format("%5.3f", this.settings.mainNoiseScaleZ), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_SCLE_NS_X, I18n.format(PREFIX + NbtTags.SCALE_NOISE_SCALE_X) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_SCLE_NS_X, String.format("%5.3f", this.settings.scaleNoiseScaleX), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_SCLE_NS_Z, I18n.format(PREFIX + NbtTags.SCALE_NOISE_SCALE_Z) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_SCLE_NS_Z, String.format("%5.3f", this.settings.scaleNoiseScaleZ), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_DPTH_NS_X, I18n.format(PREFIX + NbtTags.DEPTH_NOISE_SCALE_X) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_DPTH_NS_X, String.format("%5.3f", this.settings.depthNoiseScaleX), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_DPTH_NS_Z, I18n.format(PREFIX + NbtTags.DEPTH_NOISE_SCALE_Z) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_DPTH_NS_Z, String.format("%5.3f", this.settings.depthNoiseScaleZ), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_BASE_SIZE, I18n.format(PREFIX + NbtTags.BASE_SIZE) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_BASE_SIZE, String.format("%2.3f", this.settings.baseSize), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_COORD_SCL, I18n.format(PREFIX + NbtTags.COORDINATE_SCALE) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_COORD_SCL, String.format("%5.3f", this.settings.coordinateScale), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_HEIGH_SCL, I18n.format(PREFIX + NbtTags.HEIGHT_SCALE) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_HEIGH_SCL, String.format("%5.3f", this.settings.heightScale), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_STRETCH_Y, I18n.format(PREFIX + NbtTags.STRETCH_Y) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_STRETCH_Y, String.format("%2.3f", this.settings.stretchY), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_UPPER_LIM, I18n.format(PREFIX + NbtTags.UPPER_LIMIT_SCALE) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_UPPER_LIM, String.format("%5.3f", this.settings.upperLimitScale), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_LOWER_LIM, I18n.format(PREFIX + NbtTags.LOWER_LIMIT_SCALE) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_LOWER_LIM, String.format("%5.3f", this.settings.lowerLimitScale), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_HEIGH_LIM, I18n.format(PREFIX + NbtTags.HEIGHT) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_HEIGH_LIM, String.format("%d", this.settings.height), this.intFilter),

            createGuiLabel(GuiIdentifiers.PG4_L_BETA_LABL, "page5", "beta"),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_TEMP_SCL, I18n.format(PREFIX + NbtTags.TEMP_NOISE_SCALE) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_TEMP_SCL, String.format("%2.3f", this.settings.tempNoiseScale), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_RAIN_SCL, I18n.format(PREFIX + NbtTags.RAIN_NOISE_SCALE) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_RAIN_SCL, String.format("%2.3f", this.settings.rainNoiseScale), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_DETL_SCL, I18n.format(PREFIX + NbtTags.DETAIL_NOISE_SCALE) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_DETL_SCL, String.format("%2.3f", this.settings.detailNoiseScale), this.floatFilter),

            createGuiLabel(GuiIdentifiers.PG4_L_RELE_LABL, "page5", "release"),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_B_DPTH_WT, I18n.format(PREFIX + NbtTags.BIOME_DEPTH_WEIGHT) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_B_DPTH_WT, String.format("%2.3f", this.settings.biomeDepthWeight), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_B_DPTH_OF, I18n.format(PREFIX + NbtTags.BIOME_DEPTH_OFFSET) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_B_DPTH_OF, String.format("%2.3f", this.settings.biomeDepthOffset), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_B_SCLE_WT, I18n.format(PREFIX + NbtTags.BIOME_SCALE_WEIGHT) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_B_SCLE_WT, String.format("%2.3f", this.settings.biomeScaleWeight), this.floatFilter),
            createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_B_SCLE_OF, I18n.format(PREFIX + NbtTags.BIOME_SCALE_OFFSET) + ":"),
            createGuiField(GuiIdentifiers.PG5_F_B_SCLE_OF, String.format("%2.3f", this.settings.biomeScaleOffset), this.floatFilter)
        };
        
        GuiPageButtonList.GuiListEntry[] pageClimate = {
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_DSRT_LABL, I18n.format(PREFIX + NbtTags.DESERT_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_DSRT_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_DSRT_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_DSRT_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_FRST_LABL, I18n.format(PREFIX + NbtTags.FOREST_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_FRST_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_FRST_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_FRST_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_ICED_LABL, I18n.format(PREFIX + NbtTags.ICE_DESERT_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_ICED_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_ICED_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_ICED_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_PLNS_LABL, I18n.format(PREFIX + NbtTags.PLAINS_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_PLNS_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_PLNS_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_PLNS_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_RAIN_LABL, I18n.format(PREFIX + NbtTags.RAINFOREST_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_RAIN_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_RAIN_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_RAIN_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_SAVA_LABL, I18n.format(PREFIX + NbtTags.SAVANNA_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SAVA_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SAVA_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SAVA_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_SHRB_LABL, I18n.format(PREFIX + NbtTags.SHRUBLAND_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SHRB_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SHRB_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SHRB_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_SEAS_LABL, I18n.format(PREFIX + NbtTags.SEASONAL_FOREST_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SEAS_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SEAS_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SEAS_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_SWMP_LABL, I18n.format(PREFIX + NbtTags.SWAMPLAND_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SWMP_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SWMP_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_SWMP_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_TAIG_LABL, I18n.format(PREFIX + NbtTags.TAIGA_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_TAIG_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_TAIG_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_TAIG_BEACH, "", true),
            
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_TUND_LABL, I18n.format(PREFIX + NbtTags.TUNDRA_BIOMES)),
            null,
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, I18n.format(PREFIX + NbtTags.BASE_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_TUND_LAND, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_TUND_OCEAN, "", true),
            createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":"),
            createGuiButton(GuiIdentifiers.PG6_TUND_BEACH, "", true)
        };
        
        GuiPageButtonList.GuiListEntry[] pageCustom = this.createCustomPropertyPage();
        
        if (!ModCompat.isNetherCompatible()) {
            pageBasic = Arrays.copyOf(pageBasic, pageBasic.length + 2);
            pageBasic[pageBasic.length - 2] = createGuiLabel(GuiIdentifiers.PG0_L_NETHER_BOP, "page0", "netherIncompatible");
            pageBasic[pageBasic.length - 1] = null;
        }
        
        GuiPageButtonList.GuiListEntry[][] pages = new GuiPageButtonList.GuiListEntry[][] {
            pageBasic,
            pageChunk,
            pageBiome,
            pageOre,
            pageNoise0,
            pageNoise1,
            pageClimate,
            pageCustom
        };
        
        if (ModernBetaRegistries.PROPERTY.getValues().isEmpty()) {
            pages = Arrays.copyOf(pages, pages.length - 1);
        }
        
        this.pageList = new GuiPageButtonList(
            this.mc,
            this.width,
            this.height,
            PAGELIST_PADDING_TOP,
            this.height - PAGELIST_PADDING_BOTTOM,
            BUTTON_SLOT_HEIGHT,
            this,
            pages
        );
        
        this.pageList.width += PAGELIST_SCROLLBAR_PADDING;
        
        // Set text for primary options
        this.setTextButton(GuiIdentifiers.PG0_B_CHUNK, getFormattedRegistryName(this.settings.chunkSource, NbtTags.CHUNK_SOURCE, -1));
        this.setTextButton(GuiIdentifiers.PG0_B_BIOME, getFormattedRegistryName(this.settings.biomeSource, NbtTags.BIOME_SOURCE, -1));
        this.setTextButton(GuiIdentifiers.PG0_B_SURFACE, getFormattedRegistryName(this.settings.surfaceBuilder, NbtTags.SURFACE_BUILDER, -1));
        this.setTextButton(GuiIdentifiers.PG0_B_CARVER, getFormattedRegistryName(this.settings.caveCarver, NbtTags.CAVE_CARVER, -1));
        
        // Set biome text for Single Biome button
        this.setTextButton(GuiIdentifiers.PG0_B_FIXED, getFormattedBiomeName(this.settings.singleBiome, true, BIOME_TRUNCATE_LEN));
        
        // Set biome text for Beta Biome buttons
        this.setTextButton(GuiIdentifiers.PG6_DSRT_LAND, getFormattedBiomeName(this.settings.desertBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_DSRT_OCEAN, getFormattedBiomeName(this.settings.desertBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_DSRT_BEACH, getFormattedBiomeName(this.settings.desertBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_FRST_LAND, getFormattedBiomeName(this.settings.forestBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_FRST_OCEAN, getFormattedBiomeName(this.settings.forestBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_FRST_BEACH, getFormattedBiomeName(this.settings.forestBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_ICED_LAND, getFormattedBiomeName(this.settings.iceDesertBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_ICED_OCEAN, getFormattedBiomeName(this.settings.iceDesertBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_ICED_BEACH, getFormattedBiomeName(this.settings.iceDesertBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_PLNS_LAND, getFormattedBiomeName(this.settings.plainsBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_PLNS_OCEAN, getFormattedBiomeName(this.settings.plainsBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_PLNS_BEACH, getFormattedBiomeName(this.settings.plainsBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_RAIN_LAND, getFormattedBiomeName(this.settings.rainforestBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_RAIN_OCEAN, getFormattedBiomeName(this.settings.rainforestBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_RAIN_BEACH, getFormattedBiomeName(this.settings.rainforestBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_SAVA_LAND, getFormattedBiomeName(this.settings.savannaBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_SAVA_OCEAN, getFormattedBiomeName(this.settings.savannaBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_SAVA_BEACH, getFormattedBiomeName(this.settings.savannaBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_SHRB_LAND, getFormattedBiomeName(this.settings.shrublandBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_SHRB_OCEAN, getFormattedBiomeName(this.settings.shrublandBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_SHRB_BEACH, getFormattedBiomeName(this.settings.shrublandBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_SEAS_LAND, getFormattedBiomeName(this.settings.seasonalForestBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_SEAS_OCEAN, getFormattedBiomeName(this.settings.seasonalForestBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_SEAS_BEACH, getFormattedBiomeName(this.settings.seasonalForestBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_SWMP_LAND, getFormattedBiomeName(this.settings.swamplandBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_SWMP_OCEAN, getFormattedBiomeName(this.settings.swamplandBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_SWMP_BEACH, getFormattedBiomeName(this.settings.swamplandBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_TAIG_LAND, getFormattedBiomeName(this.settings.taigaBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_TAIG_OCEAN, getFormattedBiomeName(this.settings.taigaBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_TAIG_BEACH, getFormattedBiomeName(this.settings.taigaBiomeBeach, false, -1));
        
        this.setTextButton(GuiIdentifiers.PG6_TUND_LAND, getFormattedBiomeName(this.settings.tundraBiomeBase, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_TUND_OCEAN, getFormattedBiomeName(this.settings.tundraBiomeOcean, false, -1));
        this.setTextButton(GuiIdentifiers.PG6_TUND_BEACH, getFormattedBiomeName(this.settings.tundraBiomeBeach, false, -1));
        
        for (Entry<Integer, ResourceLocation> entry : this.customIds.entrySet()) {
            Property<?> property = this.settings.customProperties.get(entry.getValue());
            
            if (property instanceof BooleanProperty) {
                BooleanProperty booleanProperty = (BooleanProperty)property;
                
                this.setTextButton(entry.getKey(), I18n.format(booleanProperty.getValue() ? "gui.yes" : "gui.no"));
                
            } if (property instanceof BiomeProperty) {
                BiomeProperty biomeProperty = (BiomeProperty)property;
                
                this.setTextButton(entry.getKey(), getFormattedBiomeName(biomeProperty.getValue(), false, -1));
            }
        }
    }

    private void createPageTabs() {
        int x = this.width / 2 - (TAB_BUTTON_WIDTH * this.pageList.getPageCount() / 2) - (TAB_SPACE *  this.pageList.getPageCount() / 2);
        
        this.pageTabMap = new LinkedHashMap<>();
        for (int i = 0; i < this.pageList.getPageCount(); ++i) {
            GuiTabButton guiButton = new GuiTabButton(
                GuiIdentifiers.FUNC_INITIAL_TAB + i,
                x,
                TAB_HEIGHT,
                TAB_BUTTON_WIDTH,
                TAB_BUTTON_HEIGHT,
                I18n.format(this.pageNames[i])
            );
            
            this.pageTabMap.put(
                GuiIdentifiers.FUNC_INITIAL_TAB + i,
                this.<GuiButton>addButton(guiButton)
            );
            
            x += TAB_BUTTON_WIDTH + TAB_SPACE;
        }
    }
    
    @Override
    public void initGui() {
        int curPage = 0;
        int curScroll = 0;
        
        if (this.pageList != null) {
            curPage = this.pageList.getPage();
            curScroll = this.pageList.getAmountScrolled();
        }
        
        this.buttonList.clear();
        
        int buttonY = this.height - 27;
        int defaultsX = this.width / 2 - BUTTON_WIDTH * 2 - BUTTON_WIDTH / 2 - 6;
        int randomizeX = this.width / 2 - BUTTON_WIDTH - BUTTON_WIDTH / 2 - 3;
        int previewX = this.width / 2 - BUTTON_WIDTH / 2;
        int presetsX = this.width / 2 + BUTTON_WIDTH / 2 + 3;
        int doneX = this.width / 2 + BUTTON_WIDTH / 2 + BUTTON_WIDTH + 6;

        this.defaults = this.<GuiButton>addButton(new GuiButton(GuiIdentifiers.FUNC_DFLT, defaultsX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "defaults")));
        this.randomize = this.<GuiButton>addButton(new GuiButton(GuiIdentifiers.FUNC_RAND, randomizeX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "randomize")));
        this.preview = this.<GuiButton>addButton(new GuiButton(GuiIdentifiers.FUNC_PRVW, previewX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "preview")));
        this.presets = this.<GuiButton>addButton(new GuiButton(GuiIdentifiers.FUNC_PRST, presetsX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "presets")));
        this.done = this.<GuiButton>addButton(new GuiButton(GuiIdentifiers.FUNC_DONE, doneX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("gui.done")));
        
        this.defaults.enabled = this.settingsModified;
        
        this.confirm = new GuiButton(GuiIdentifiers.FUNC_CONF, this.width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"));
        this.confirm.visible = false;
        
        this.cancel = new GuiButton(GuiIdentifiers.FUNC_CNCL, this.width / 2 + 5, 160, 50, 20, I18n.format("gui.no"));
        this.cancel.visible = false;

        this.buttonList.add(this.confirm);
        this.buttonList.add(this.cancel);
        
        if (this.confirmMode != 0) {
            this.confirm.visible = true;
            this.cancel.visible = true;
        }
        
        GuiIdentifiers.assertOffsets();
        
        this.createPagedList();
        this.createPageTabs();
        
        if (curPage != 0) {
            this.pageList.setPage(curPage);
            this.pageList.scrollBy(curScroll);
        }
        
        // Set default enabled for certain options
        this.setGuiEnabled();
        this.updatePageControls();
        this.isSettingsModified();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        if (!this.tabClicked) {
            this.pageList.handleMouseInput();
        }
    }
    
    @Override
    public String getText(int entry, String entryString, float entryValue) {
        // Do not append colon for custom property entries
        if (this.customIds.containsKey(entry)) {
            return this.getFormattedValue(entry, entryValue);
        }
        
        return entryString + ": " + this.getFormattedValue(entry, entryValue);
    }

    @Override
    public void setEntryValue(int entry, String entryString) {
        if (this.customIds.containsKey(entry)) {
            ResourceLocation registryKey = this.customIds.get(entry);
            
            Property<?> property = this.settings.customProperties.get(registryKey);
            property.visitEntryValue(new SetEntryValuePropertyVisitor(), entry, entryString, registryKey);
            
        } else {
            float entryValue = 0.0f;
            
            try {
                entryValue = Float.parseFloat(entryString);
                
            } catch (NumberFormatException ex) {}
            
            float newEntryValue = 0.0f;
            switch (entry) {
                case GuiIdentifiers.PG5_F_MAIN_NS_X:
                    this.settings.mainNoiseScaleX = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE);
                    newEntryValue = this.settings.mainNoiseScaleX;
                    break;
                case GuiIdentifiers.PG5_F_MAIN_NS_Y:
                    this.settings.mainNoiseScaleY = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE);
                    newEntryValue = this.settings.mainNoiseScaleY;
                    break;
                case GuiIdentifiers.PG5_F_MAIN_NS_Z:
                    this.settings.mainNoiseScaleZ = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE);
                    newEntryValue = this.settings.mainNoiseScaleZ;
                    break;
                case GuiIdentifiers.PG5_F_SCLE_NS_X:
                    this.settings.scaleNoiseScaleX = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_SCALE_NOISE, ModernBetaGeneratorSettings.MAX_SCALE_NOISE);
                    newEntryValue = this.settings.scaleNoiseScaleX;
                    break;
                case GuiIdentifiers.PG5_F_SCLE_NS_Z:
                    this.settings.scaleNoiseScaleZ = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_SCALE_NOISE, ModernBetaGeneratorSettings.MAX_SCALE_NOISE);
                    newEntryValue = this.settings.scaleNoiseScaleZ;
                    break;
                case GuiIdentifiers.PG5_F_DPTH_NS_X:
                    this.settings.depthNoiseScaleX = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_DEPTH_NOISE, ModernBetaGeneratorSettings.MAX_DEPTH_NOISE);
                    newEntryValue = this.settings.depthNoiseScaleX;
                    break;
                case GuiIdentifiers.PG5_F_DPTH_NS_Z:
                    this.settings.depthNoiseScaleZ = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_DEPTH_NOISE, ModernBetaGeneratorSettings.MAX_DEPTH_NOISE);
                    newEntryValue = this.settings.depthNoiseScaleZ;
                    break;
                case GuiIdentifiers.PG5_F_BASE_SIZE:
                    this.settings.baseSize = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_BASE_SIZE, ModernBetaGeneratorSettings.MAX_BASE_SIZE);
                    newEntryValue = this.settings.baseSize;
                    break;
                case GuiIdentifiers.PG5_F_COORD_SCL:
                    this.settings.coordinateScale = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_COORD_SCALE, ModernBetaGeneratorSettings.MAX_COORD_SCALE);
                    newEntryValue = this.settings.coordinateScale;
                    break;
                case GuiIdentifiers.PG5_F_HEIGH_SCL:
                    this.settings.heightScale = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_HEIGHT_SCALE, ModernBetaGeneratorSettings.MAX_HEIGHT_SCALE);
                    newEntryValue = this.settings.heightScale;
                    break;
                case GuiIdentifiers.PG5_F_STRETCH_Y:
                    this.settings.stretchY = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_STRETCH_Y, ModernBetaGeneratorSettings.MAX_STRETCH_Y);
                    newEntryValue = this.settings.stretchY;
                    break;
                case GuiIdentifiers.PG5_F_UPPER_LIM:
                    this.settings.upperLimitScale = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_LIMIT_SCALE, ModernBetaGeneratorSettings.MAX_LIMIT_SCALE);
                    newEntryValue = this.settings.upperLimitScale;
                    break;
                case GuiIdentifiers.PG5_F_LOWER_LIM:
                    this.settings.lowerLimitScale = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_LIMIT_SCALE, ModernBetaGeneratorSettings.MAX_LIMIT_SCALE);
                    newEntryValue = this.settings.lowerLimitScale;
                    break;
                case GuiIdentifiers.PG5_F_HEIGH_LIM:
                    this.settings.height = (int)MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_HEIGHT, ModernBetaGeneratorSettings.MAX_HEIGHT);
                    newEntryValue = this.settings.height;
                    break;
                case GuiIdentifiers.PG5_F_TEMP_SCL:
                    this.settings.tempNoiseScale = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE);
                    newEntryValue = this.settings.tempNoiseScale;
                    break;
                case GuiIdentifiers.PG5_F_RAIN_SCL:
                    this.settings.rainNoiseScale = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE);
                    newEntryValue = this.settings.rainNoiseScale;
                    break;
                case GuiIdentifiers.PG5_F_DETL_SCL:
                    this.settings.detailNoiseScale = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE);
                    newEntryValue = this.settings.detailNoiseScale;
                    break;
                case GuiIdentifiers.PG5_F_B_DPTH_WT:
                    this.settings.biomeDepthWeight = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_BIOME_WEIGHT, ModernBetaGeneratorSettings.MAX_BIOME_WEIGHT);
                    newEntryValue = this.settings.biomeDepthWeight;
                    break;
                case GuiIdentifiers.PG5_F_B_DPTH_OF:
                    this.settings.biomeDepthOffset = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_BIOME_OFFSET, ModernBetaGeneratorSettings.MAX_BIOME_OFFSET);
                    newEntryValue = this.settings.biomeDepthOffset;
                    break;
                case GuiIdentifiers.PG5_F_B_SCLE_WT:
                    this.settings.biomeScaleWeight = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_BIOME_WEIGHT, ModernBetaGeneratorSettings.MAX_BIOME_WEIGHT);
                    newEntryValue = this.settings.biomeScaleWeight;
                    break;
                case GuiIdentifiers.PG5_F_B_SCLE_OF:
                    this.settings.biomeScaleOffset = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_BIOME_OFFSET, ModernBetaGeneratorSettings.MAX_BIOME_OFFSET);
                    newEntryValue = this.settings.biomeScaleOffset;
                    break;
            }

            if (newEntryValue != entryValue) {
                ((GuiTextField)this.pageList.getComponent(entry)).setText(this.getFormattedValue(entry, newEntryValue));
            }
            
            if (entry >= GuiIdentifiers.PG5_F_MAIN_NS_X && entry <= GuiIdentifiers.PG5_F_SCLE_NS_Z) {
                Gui gui = this.pageList.getComponent(GuiIdentifiers.offsetBackward(entry));
                if (gui != null) {
                    ((GuiSlider)gui).setSliderValue(newEntryValue, false);
                }
            }
        }
        
        this.setSettingsModified(!this.settings.equals(this.defaultSettings));
    }

    @Override
    public void setEntryValue(int entry, boolean entryValue) {
        if (this.customIds.containsKey(entry)) {
            ResourceLocation registryKey = this.customIds.get(entry);
            
            Property<?> property = this.settings.customProperties.get(registryKey);
            property.visitEntryValue(new SetEntryValuePropertyVisitor(), entry, entryValue, registryKey);
            
        } else {
            switch (entry) {
                case GuiIdentifiers.PG0_B_CHUNK:
                    this.openRegistryScreen((str, factory) -> factory.chunkSource = str, settings.chunkSource, NbtTags.CHUNK_SOURCE,  ModernBetaRegistries.CHUNK_SOURCE.getKeys());
                    break;
                case GuiIdentifiers.PG0_B_BIOME:
                    this.openRegistryScreen((str, factory) -> factory.biomeSource = str, settings.biomeSource, NbtTags.BIOME_SOURCE,  ModernBetaRegistries.BIOME_SOURCE.getKeys());
                    break;
                case GuiIdentifiers.PG0_B_SURFACE:
                    this.openRegistryScreen((str, factory) -> factory.surfaceBuilder = str, settings.surfaceBuilder, NbtTags.SURFACE_BUILDER,  ModernBetaRegistries.SURFACE_BUILDER.getKeys());
                    break;
                case GuiIdentifiers.PG0_B_CARVER:
                    this.openRegistryScreen((str, factory) -> factory.caveCarver = str, settings.caveCarver, NbtTags.CAVE_CARVER,  ModernBetaRegistries.CAVE_CARVER.getKeys());
                    break;
                case GuiIdentifiers.PG0_B_FIXED:
                    this.openBiomeScreen((str, factory) -> factory.singleBiome = str, settings.singleBiome);
                    break;
                    
                case GuiIdentifiers.PG6_DSRT_LAND:
                    this.openBiomeScreen((str, factory) -> factory.desertBiomeBase = str, settings.desertBiomeBase);
                    break;
                case GuiIdentifiers.PG6_DSRT_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.desertBiomeOcean = str, settings.desertBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_DSRT_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.desertBiomeBeach = str, settings.desertBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_FRST_LAND:
                    this.openBiomeScreen((str, factory) -> factory.forestBiomeBase = str, settings.forestBiomeBase);
                    break;
                case GuiIdentifiers.PG6_FRST_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.forestBiomeOcean = str, settings.forestBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_FRST_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.forestBiomeBeach = str, settings.forestBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_ICED_LAND:
                    this.openBiomeScreen((str, factory) -> factory.iceDesertBiomeBase = str, settings.iceDesertBiomeBase);
                    break;
                case GuiIdentifiers.PG6_ICED_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.iceDesertBiomeOcean = str, settings.iceDesertBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_ICED_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.iceDesertBiomeBeach = str, settings.iceDesertBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_PLNS_LAND:
                    this.openBiomeScreen((str, factory) -> factory.plainsBiomeBase = str, settings.plainsBiomeBase);
                    break;
                case GuiIdentifiers.PG6_PLNS_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.plainsBiomeOcean = str, settings.plainsBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_PLNS_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.plainsBiomeBeach = str, settings.plainsBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_RAIN_LAND:
                    this.openBiomeScreen((str, factory) -> factory.rainforestBiomeBase = str, settings.rainforestBiomeBase);
                    break;
                case GuiIdentifiers.PG6_RAIN_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.rainforestBiomeOcean = str, settings.rainforestBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_RAIN_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.rainforestBiomeBeach = str, settings.rainforestBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_SAVA_LAND:
                    this.openBiomeScreen((str, factory) -> factory.savannaBiomeBase = str, settings.savannaBiomeBase);
                    break;
                case GuiIdentifiers.PG6_SAVA_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.savannaBiomeOcean = str, settings.savannaBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_SAVA_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.savannaBiomeBeach = str, settings.savannaBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_SHRB_LAND:
                    this.openBiomeScreen((str, factory) -> factory.shrublandBiomeBase = str, settings.shrublandBiomeBase);
                    break;
                case GuiIdentifiers.PG6_SHRB_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.shrublandBiomeOcean = str, settings.shrublandBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_SHRB_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.shrublandBiomeBeach = str, settings.shrublandBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_SEAS_LAND:
                    this.openBiomeScreen((str, factory) -> factory.seasonalForestBiomeBase = str, settings.seasonalForestBiomeBase);
                    break;
                case GuiIdentifiers.PG6_SEAS_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.seasonalForestBiomeOcean = str, settings.seasonalForestBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_SEAS_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.seasonalForestBiomeBeach = str, settings.seasonalForestBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_SWMP_LAND:
                    this.openBiomeScreen((str, factory) -> factory.swamplandBiomeBase = str, settings.swamplandBiomeBase);
                    break;
                case GuiIdentifiers.PG6_SWMP_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.swamplandBiomeOcean = str, settings.swamplandBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_SWMP_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.swamplandBiomeBeach = str, settings.swamplandBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_TAIG_LAND:
                    this.openBiomeScreen((str, factory) -> factory.taigaBiomeBase = str, settings.taigaBiomeBase);
                    break;
                case GuiIdentifiers.PG6_TAIG_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.taigaBiomeOcean = str, settings.taigaBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_TAIG_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.taigaBiomeBeach = str, settings.taigaBiomeBeach);
                    break;
                    
                case GuiIdentifiers.PG6_TUND_LAND:
                    this.openBiomeScreen((str, factory) -> factory.tundraBiomeBase = str, settings.tundraBiomeBase);
                    break;
                case GuiIdentifiers.PG6_TUND_OCEAN:
                    this.openBiomeScreen((str, factory) -> factory.tundraBiomeOcean = str, settings.tundraBiomeOcean);
                    break;
                case GuiIdentifiers.PG6_TUND_BEACH:
                    this.openBiomeScreen((str, factory) -> factory.tundraBiomeBeach = str, settings.tundraBiomeBeach);
                    break;
            
                case GuiIdentifiers.PG0_B_USE_OCEAN:
                    this.settings.replaceOceanBiomes = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_BEACH:
                    this.settings.replaceBeachBiomes = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_GRASS:
                    this.settings.useTallGrass = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_FLOWERS:
                    this.settings.useNewFlowers = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_PADS:
                    this.settings.useLilyPads = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_MELONS:
                    this.settings.useMelons = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_WELLS:
                    this.settings.useDesertWells = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_FOSSILS:
                    this.settings.useFossils = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_BIRCH:
                    this.settings.useBirchTrees = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_PINE:
                    this.settings.usePineTrees = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_SWAMP:
                    this.settings.useSwampTrees = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_JUNGLE:
                    this.settings.useJungleTrees = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_ACACIA:
                    this.settings.useAcaciaTrees = entryValue;
                    break;
                    
                case GuiIdentifiers.PG2_B_USE_MODDED_BIOMES:
                    this.settings.useModdedBiomes = entryValue;
                    break;
                    
                case GuiIdentifiers.PG2_B_SPAWN_CREATURE:
                    this.settings.spawnNewCreatureMobs = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_SPAWN_MONSTER:
                    this.settings.spawnNewMonsterMobs = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_SPAWN_WOLVES:
                    this.settings.spawnWolves = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_SPAWN_WATER:
                    this.settings.spawnWaterMobs = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_SPAWN_AMBIENT:
                    this.settings.spawnAmbientMobs = entryValue;
                    break;
                    
                case GuiIdentifiers.PG0_B_USE_HOLDS:
                    this.settings.useStrongholds = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_VILLAGES:
                    this.settings.useVillages = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_VILLAGE_VARIANTS:
                    this.settings.useVillageVariants = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_SHAFTS:
                    this.settings.useMineShafts = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_TEMPLES:
                    this.settings.useTemples = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_MONUMENTS:
                    this.settings.useMonuments = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_MANSIONS:
                    this.settings.useMansions = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_RAVINES:
                    this.settings.useRavines = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_DUNGEONS:
                    this.settings.useDungeons = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_WATER_LAKES:
                    this.settings.useWaterLakes = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_LAVA_LAKES:
                    this.settings.useLavaLakes = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_LAVA_OCEANS:
                    this.settings.useLavaOceans = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_SANDSTONE:
                    this.settings.useSandstone = entryValue;
                    break;
                    
                case GuiIdentifiers.PG0_B_USE_OLD_NETHER:
                    this.settings.useOldNether = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_NETHER_CAVES:
                    this.settings.useNetherCaves = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_FORTRESSES:
                    this.settings.useFortresses = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_LAVA_POCKETS:
                    this.settings.useLavaPockets = entryValue;
                    break;
    
                case GuiIdentifiers.PG1_B_USE_INDEV_CAVES:
                    this.settings.useIndevCaves = entryValue;
                    break;
                    
                case GuiIdentifiers.PG1_B_USE_INFDEV_WALLS:
                    this.settings.useInfdevWalls = entryValue;
                    break;
                case GuiIdentifiers.PG1_B_USE_INFDEV_PYRAMIDS:
                    this.settings.useInfdevPyramids = entryValue;
                    break;
                    
                case GuiIdentifiers.PG4_B_USE_BDS:
                    this.settings.useBiomeDepthScale = entryValue;
                    break;
            }
        }

        this.setGuiEnabled();
        this.setSettingsModified(!this.settings.equals(this.defaultSettings));
        this.playSound();
    }

    @Override
    public void setEntryValue(int entry, float entryValue) {
        if (this.customIds.containsKey(entry)) {
            ResourceLocation registryKey = this.customIds.get(entry);
            
            Property<?> property = this.settings.customProperties.get(registryKey);
            property.visitEntryValue(new SetEntryValuePropertyVisitor(), entry, entryValue, registryKey);
            
        } else {
            switch (entry) {
                case GuiIdentifiers.PG4_S_MAIN_NS_X:
                    this.settings.mainNoiseScaleX = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_MAIN_NS_Y:
                    this.settings.mainNoiseScaleY = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_MAIN_NS_Z:
                    this.settings.mainNoiseScaleZ = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_SCLE_NS_X:
                    this.settings.scaleNoiseScaleX = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_SCLE_NS_Z:
                    this.settings.scaleNoiseScaleZ = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_DPTH_NS_X:
                    this.settings.depthNoiseScaleX = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_DPTH_NS_Z:
                    this.settings.depthNoiseScaleZ = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_BASE_SIZE:
                    this.settings.baseSize = roundToThreeDec(entryValue);
                    break;    
                case GuiIdentifiers.PG4_S_COORD_SCL:
                    this.settings.coordinateScale = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_HEIGH_SCL:
                    this.settings.heightScale = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_STRETCH_Y:
                    this.settings.stretchY = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_UPPER_LIM:
                    this.settings.upperLimitScale = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_LOWER_LIM:
                    this.settings.lowerLimitScale = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_HEIGH_LIM:
                    this.settings.height = (int)entryValue;
                    break;
                case GuiIdentifiers.PG4_S_TEMP_SCL:
                    this.settings.tempNoiseScale = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_RAIN_SCL:
                    this.settings.rainNoiseScale = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_DETL_SCL:
                    this.settings.detailNoiseScale = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_B_DPTH_WT:
                    this.settings.biomeDepthWeight = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_B_DPTH_OF:
                    this.settings.biomeDepthOffset = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_B_SCLE_WT:
                    this.settings.biomeScaleWeight = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_B_SCLE_OF:
                    this.settings.biomeScaleOffset = roundToThreeDec(entryValue);
                    break;
                    
                case GuiIdentifiers.PG0_S_CHUNK:
                    this.settings.chunkSource = ModernBetaRegistries.CHUNK_SOURCE.getKeys().get((int)entryValue).toString();
                    break;
                case GuiIdentifiers.PG0_S_BIOME:
                    this.settings.biomeSource = ModernBetaRegistries.BIOME_SOURCE.getKeys().get((int)entryValue).toString();
                    break;
                case GuiIdentifiers.PG0_S_SURFACE:
                    this.settings.surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER.getKeys().get((int)entryValue).toString();
                    break;
                case GuiIdentifiers.PG0_S_CARVER:
                    this.settings.caveCarver = ModernBetaRegistries.CAVE_CARVER.getKeys().get((int)entryValue).toString();
                    break;
                
                case GuiIdentifiers.PG0_S_SEA_LEVEL:
                    this.settings.seaLevel = (int)entryValue;
                    break;
                case GuiIdentifiers.PG0_S_CAVE_WIDTH:
                    this.settings.caveWidth = roundToTwoDec(entryValue);
                    break;
                case GuiIdentifiers.PG0_S_CAVE_HEIGHT:
                    this.settings.caveHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG0_S_CAVE_COUNT:
                    this.settings.caveCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG0_S_CAVE_CHANCE:
                    this.settings.caveChance = (int)entryValue;
                    break;
                case GuiIdentifiers.PG0_S_DUNGEON_CHANCE:
                    this.settings.dungeonChance = (int)entryValue;
                    break;
                case GuiIdentifiers.PG0_S_WATER_LAKE_CHANCE:
                    this.settings.waterLakeChance = (int)entryValue;
                    break;
                case GuiIdentifiers.PG0_S_LAVA_LAKE_CHANCE:
                    this.settings.lavaLakeChance = (int)entryValue;
                    break;
                    
                case GuiIdentifiers.PG1_S_LEVEL_THEME:
                    this.settings.levelTheme = IndevTheme.values()[(int)entryValue].id;
                    break;
                case GuiIdentifiers.PG1_S_LEVEL_TYPE:
                    this.settings.levelType = IndevType.values()[(int)entryValue].id;
                    break;
                case GuiIdentifiers.PG1_S_LEVEL_WIDTH:
                    this.settings.levelWidth = ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)entryValue];
                    break;
                case GuiIdentifiers.PG1_S_LEVEL_LENGTH:
                    this.settings.levelLength = ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)entryValue];
                    break;
                case GuiIdentifiers.PG1_S_LEVEL_HEIGHT:
                    this.settings.levelHeight = ModernBetaGeneratorSettings.LEVEL_HEIGHTS[(int)entryValue];
                    break;
                case GuiIdentifiers.PG1_S_LEVEL_HOUSE:
                    this.settings.levelHouse = IndevHouse.values()[(int)entryValue].id;
                    break;
                case GuiIdentifiers.PG1_S_RIVER_SZ:
                    this.settings.riverSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG1_S_LAYER_TYPE:
                    this.settings.layerType = GenLayerType.values()[(int)entryValue].id;
                    break;
                case GuiIdentifiers.PG1_S_LAYER_SZ:
                    this.settings.layerSize = (int)entryValue;
                    break;

                case GuiIdentifiers.PG2_S_BIOME_SZ:
                    this.settings.biomeSize = (int)entryValue;
                    break;
    
                case GuiIdentifiers.PG3_S_CLAY_SIZE:
                    this.settings.claySize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_CLAY_CNT:
                    this.settings.clayCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_CLAY_MIN:
                    this.settings.clayMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_CLAY_MAX:
                    this.settings.clayMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIRT_SIZE:
                    this.settings.dirtSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIRT_CNT:
                    this.settings.dirtCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIRT_MIN:
                    this.settings.dirtMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIRT_MAX:
                    this.settings.dirtMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GRAV_SIZE:
                    this.settings.gravelSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GRAV_CNT:
                    this.settings.gravelCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GRAV_MIN:
                    this.settings.gravelMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GRAV_MAX:
                    this.settings.gravelMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GRAN_SIZE:
                    this.settings.graniteSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GRAN_CNT:
                    this.settings.graniteCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GRAN_MIN:
                    this.settings.graniteMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GRAN_MAX:
                    this.settings.graniteMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIOR_SIZE:
                    this.settings.dioriteSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIOR_CNT:
                    this.settings.dioriteCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIOR_MIN:
                    this.settings.dioriteMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIOR_MAX:
                    this.settings.dioriteMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_ANDE_SIZE:
                    this.settings.andesiteSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_ANDE_CNT:
                    this.settings.andesiteCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_ANDE_MIN:
                    this.settings.andesiteMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_ANDE_MAX:
                    this.settings.andesiteMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_COAL_SIZE:
                    this.settings.coalSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_COAL_CNT:
                    this.settings.coalCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_COAL_MIN:
                    this.settings.coalMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_COAL_MAX:
                    this.settings.coalMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_IRON_SIZE:
                    this.settings.ironSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_IRON_CNT:
                    this.settings.ironCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_IRON_MIN:
                    this.settings.ironMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_IRON_MAX:
                    this.settings.ironMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GOLD_SIZE:
                    this.settings.goldSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GOLD_CNT:
                    this.settings.goldCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GOLD_MIN:
                    this.settings.goldMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_GOLD_MAX:
                    this.settings.goldMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_REDS_SIZE:
                    this.settings.redstoneSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_REDS_CNT:
                    this.settings.redstoneCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_REDS_MIN:
                    this.settings.redstoneMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_REDS_MAX:
                    this.settings.redstoneMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIAM_SIZE:
                    this.settings.diamondSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIAM_CNT:
                    this.settings.diamondCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIAM_MIN:
                    this.settings.diamondMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_DIAM_MAX:
                    this.settings.diamondMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_LAPS_SIZE:
                    this.settings.lapisSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_LAPS_CNT:
                    this.settings.lapisCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_LAPS_CTR:
                    this.settings.lapisCenterHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_LAPS_SPR:
                    this.settings.lapisSpread = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_EMER_SIZE:
                    this.settings.emeraldSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_EMER_CNT:
                    this.settings.emeraldCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_EMER_MIN:
                    this.settings.emeraldMinHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_EMER_MAX:
                    this.settings.emeraldMaxHeight = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_QRTZ_SIZE:
                    this.settings.quartzSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_QRTZ_CNT:
                    this.settings.quartzCount = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_MGMA_SIZE:
                    this.settings.magmaSize = (int)entryValue;
                    break;
                case GuiIdentifiers.PG3_S_MGMA_CNT:
                    this.settings.magmaCount = (int)entryValue;
                    break;
            }
            
            if (entry >= GuiIdentifiers.PG4_S_MAIN_NS_X && entry <= GuiIdentifiers.PG4_S_SCLE_NS_Z) {
                Gui gui = this.pageList.getComponent(GuiIdentifiers.offsetForward(entry));
                if (gui != null) {
                    ((GuiTextField)gui).setText(this.getFormattedValue(entry, entryValue));
                }
            }
            
            if (entry == GuiIdentifiers.PG1_S_LEVEL_HEIGHT || entry == GuiIdentifiers.PG1_S_LEVEL_TYPE || entry == GuiIdentifiers.PG0_S_CHUNK) {
                Gui gui = this.pageList.getComponent(GuiIdentifiers.PG0_L_INDEV_SEA_LEVEL);
                if (gui != null && gui instanceof GuiLabel) {
                    int levelSeaLevel = this.getLevelSeaLevel();
                    String levelSeaLevelStr = levelSeaLevel == -1 ? "" : Integer.toString(levelSeaLevel);
                    
                    ((GuiLabel)gui).labels.set(0, String.format("%s: %s", I18n.format(PREFIX + "seaLevel"), levelSeaLevelStr));
                }
            }
        }

        this.setGuiEnabled();
        this.setSettingsModified(!this.settings.equals(this.defaultSettings));
        this.playSound();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.pageList.drawScreen(mouseX, mouseY, partialTicks);
        
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, PAGE_TITLE_HEIGHT, 16777215);
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
    
    public ModernBetaGeneratorSettings.Factory getDefaultSettings() {
        return ModernBetaGeneratorSettings.Factory.jsonToFactory(this.defaultSettings.toString());
    }

    public String getSettingsString() {
        return this.settings.toString().replace("\n", "");
    }

    public void loadValues(String string) {
        if (string != null && !string.isEmpty()) {
            this.settings = ModernBetaGeneratorSettings.Factory.jsonToFactory(string);
        } else {
            this.settings = new ModernBetaGeneratorSettings.Factory();
        }
    }
    
    public void isSettingsModified() {
        this.setSettingsModified(!this.settings.equals(this.defaultSettings));
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
            case GuiIdentifiers.FUNC_DONE:
                this.parent.chunkProviderSettingsJson = this.settings.toString();
                this.mc.displayGuiScreen(this.parent);
                break;
            case GuiIdentifiers.FUNC_RAND: // Randomize
                Set<Gui> biomeButtonComponents = this.getBiomeButtonComponents();
                Set<Gui> baseSliderComponents = this.getBaseSliderComponents();
                Set<Gui> baseButtonComponents = this.getBaseButtonComponents();
                
                for (int page = 0; page < this.pageList.getSize(); ++page) {
                    this.randomClicked = true;

                    GuiPageButtonList.GuiEntry guiEntry = this.pageList.getListEntry(page);
                    this.randomizeGuiComponent(guiEntry.getComponent1(), biomeButtonComponents, baseSliderComponents, baseButtonComponents);
                    this.randomizeGuiComponent(guiEntry.getComponent2(), biomeButtonComponents, baseSliderComponents, baseButtonComponents);

                    this.randomClicked = false;
                    this.setGuiEnabled();
                    this.setSettingsModified(!this.settings.equals(this.defaultSettings));
                }
                             
                break;
            case GuiIdentifiers.FUNC_DFLT:
                if (!this.settingsModified) {
                    break;
                }
                this.enterConfirmation(GuiIdentifiers.FUNC_DFLT);
                break;
            case GuiIdentifiers.FUNC_PRST:
                this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this));
                break;
            case GuiIdentifiers.FUNC_CONF:
                this.exitConfirmation();
                break;
            case GuiIdentifiers.FUNC_CNCL:
                this.confirmMode = 0;
                this.exitConfirmation();
                break;
            case GuiIdentifiers.FUNC_PRVW:
                this.mc.displayGuiScreen(new GuiScreenCustomizePreview(this, this.parent.worldSeed, this.settings.build()));
                break;
        }
        
        if (this.pageTabMap.containsKey(guiButton.id)) {
            this.pageList.setPage(guiButton.id - GuiIdentifiers.FUNC_INITIAL_TAB);
            this.updatePageControls();
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
        
        if (this.isMouseOverPageTab(mouseX, mouseY)) {
            this.tabClicked = true;
        }
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.confirmMode != 0 || this.confirmDismissed) {
            return;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.pageList.mouseReleased(mouseX, mouseY, mouseButton);
        this.clicked = false;
        this.tabClicked = false;
        
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (this.confirmDismissed) {
            this.confirmDismissed = false;
            return;
        }
        if (this.confirmMode != 0) {
            return;
        }
    }
    
    private GuiPageButtonList.GuiListEntry[] createCustomPropertyPage() {
        // Get total number of page list entries,
        // and add an additional entry for float/int/string properties to accommodate label entry
        int numEntries = ModernBetaRegistries.PROPERTY.getKeys().size() * 2;
        
        // Build map based on mod ID
        Map<String, List<ResourceLocation>> modRegistryKeys = new LinkedHashMap<>();
        for (ResourceLocation registryKey : ModernBetaRegistries.PROPERTY.getKeys()) {
            String namespace = registryKey.getNamespace();
            
            if (!modRegistryKeys.containsKey(namespace)) {
                modRegistryKeys.put(namespace, new LinkedList<>());
                numEntries += 2;
            }
            
            modRegistryKeys.get(namespace).add(registryKey);
        }
        
        GuiPageButtonList.GuiListEntry[] pageList = new GuiPageButtonList.GuiListEntry[numEntries];
        
        int ndx = 0;
        for (String namespace : modRegistryKeys.keySet()) {
            List<ResourceLocation> registryKeys = modRegistryKeys.get(namespace);
            
            pageList[ndx++] = createGuiLabelNoPrefix(this.customId++, I18n.format(PREFIX + namespace));
            pageList[ndx++] = null;
            
            for (ResourceLocation registryKey : registryKeys) {
                Property<?> property = this.settings.customProperties.get(registryKey);
                
                pageList[ndx++] = createGuiLabelNoPrefix(this.customId++, I18n.format(PREFIX + getFormattedRegistryString(registryKey)) + ":");
                pageList[ndx++] = property.visitGui(this.new CreateGuiPropertyVisitor(), this.customId);
                
                this.customIds.put(this.customId++, registryKey);
            }
        }
        
        return pageList;
    }
    
    private void randomizeGuiComponent(Gui guiComponent, Set<Gui> biomeButtonComponents, Set<Gui> baseSliderComponents, Set<Gui> baseButtonComponents) {
        if (guiComponent instanceof GuiButton && ((GuiButton)guiComponent).enabled) {
            GuiButton guiButtonComponent = (GuiButton)guiComponent;
            
            if (baseButtonComponents.contains(guiButtonComponent)) {
                int buttonId = GuiIdentifiers.BASE_SETTINGS
                    .keySet()
                    .stream()
                    .filter(id -> this.pageList.getComponent(id) == guiButtonComponent)
                    .findFirst()
                    .orElse(-1);
                
                if (guiButtonComponent instanceof GuiListButton && buttonId != -1) {
                    ResourceLocation randomKey = null;
                    String langName = null;
                    
                    switch(buttonId) {
                        case GuiIdentifiers.PG0_B_CHUNK:
                            randomKey = ModernBetaRegistries.CHUNK_SOURCE.getRandomEntry(this.random).getKey();
                            langName = NbtTags.CHUNK_SOURCE;
                            break;
                        case GuiIdentifiers.PG0_B_BIOME:
                            randomKey = ModernBetaRegistries.BIOME_SOURCE.getRandomEntry(this.random).getKey();
                            langName = NbtTags.BIOME_SOURCE;
                            break;
                        case GuiIdentifiers.PG0_B_SURFACE:
                            randomKey = ModernBetaRegistries.SURFACE_BUILDER.getRandomEntry(this.random).getKey();
                            langName = NbtTags.SURFACE_BUILDER;
                            break;
                        case GuiIdentifiers.PG0_B_CARVER:
                            randomKey = ModernBetaRegistries.CAVE_CARVER.getRandomEntry(this.random).getKey();
                            langName = NbtTags.CAVE_CARVER;
                            break;
                    }
                    
                    if (randomKey != null && langName != null) {
                        String registryName = randomKey.toString();
                        
                        GuiIdentifiers.BASE_SETTINGS.get(buttonId).accept(registryName, this.settings);
                        this.setTextButton(buttonId, getFormattedRegistryName(registryName, langName, -1));
                    }
                }
                
            } else if (baseSliderComponents.contains(guiButtonComponent)) {
                if (guiButtonComponent instanceof GuiSlider) {
                    GuiSlider guiSlider = (GuiSlider)guiButtonComponent;
                    
                    float randomPos = this.random.nextFloat();
                    guiSlider.setSliderPosition(MathHelper.clamp(randomPos, 0.0f, 1.0f));
                    
                }
            
            } else if (biomeButtonComponents.contains(guiButtonComponent)) {
                int buttonId = GuiIdentifiers.BIOME_SETTINGS
                    .keySet()
                    .stream()
                    .filter(id -> this.pageList.getComponent(id) == guiButtonComponent)
                    .findFirst()
                    .orElse(-1);
                
                if (guiButtonComponent instanceof GuiListButton && buttonId != -1) {
                    String registryName = BiomeUtil.getRandomBiome(this.random).getRegistryName().toString();
                    boolean prefix = buttonId == GuiIdentifiers.PG0_B_FIXED;
                    int truncateLen = buttonId == GuiIdentifiers.PG0_B_FIXED ? BIOME_TRUNCATE_LEN : -1;
                    
                    GuiIdentifiers.BIOME_SETTINGS.get(buttonId).accept(registryName,  this.settings);
                    this.setTextButton(buttonId, getFormattedBiomeName(registryName, prefix, truncateLen));
                }
                
            } else {
                if (guiButtonComponent instanceof GuiSlider) {
                    GuiSlider guiSlider = (GuiSlider)guiButtonComponent;
                    
                    float randomFloat = guiSlider.getSliderPosition() * (0.75f + this.random.nextFloat() * 0.5f) + (this.random.nextFloat() * 0.1f - 0.05f);
                    guiSlider.setSliderPosition(MathHelper.clamp(randomFloat, 0.0f, 1.0f));
                    
                } else if (guiButtonComponent instanceof GuiListButton) {
                    ((GuiListButton)guiButtonComponent).setValue(this.random.nextBoolean());
                    
                }
            }
            
        }
    }

    private String getFormattedValue(int entry, float entryValue) {
        if (this.customIds.containsKey(entry)) {
            Property<?> property = this.settings.customProperties.get(this.customIds.get(entry));
            
            if (property instanceof FloatProperty) {
                return String.format("%2.3f", entryValue);
            
            } else if (property instanceof IntProperty) {
                return String.format("%d", (int)entryValue);
                
            } else if (property instanceof ListProperty) {
                ListProperty listProperty = (ListProperty)property;
                
                return listProperty.getValues()[(int)entryValue];
            }
        }
        
        switch (entry) {
            case GuiIdentifiers.PG4_S_MAIN_NS_X:
            case GuiIdentifiers.PG4_S_MAIN_NS_Y:
            case GuiIdentifiers.PG4_S_MAIN_NS_Z:
            case GuiIdentifiers.PG4_S_SCLE_NS_X:
            case GuiIdentifiers.PG4_S_SCLE_NS_Z:
            case GuiIdentifiers.PG4_S_DPTH_NS_X:
            case GuiIdentifiers.PG4_S_DPTH_NS_Z:
            case GuiIdentifiers.PG4_S_COORD_SCL:
            case GuiIdentifiers.PG4_S_HEIGH_SCL:
            case GuiIdentifiers.PG4_S_UPPER_LIM:
            case GuiIdentifiers.PG4_S_LOWER_LIM:
                
            case GuiIdentifiers.PG5_F_MAIN_NS_X:
            case GuiIdentifiers.PG5_F_MAIN_NS_Y:
            case GuiIdentifiers.PG5_F_MAIN_NS_Z:
            case GuiIdentifiers.PG5_F_SCLE_NS_X:
            case GuiIdentifiers.PG5_F_SCLE_NS_Z:
            case GuiIdentifiers.PG5_F_DPTH_NS_X:
            case GuiIdentifiers.PG5_F_DPTH_NS_Z:
            case GuiIdentifiers.PG5_F_COORD_SCL:
            case GuiIdentifiers.PG5_F_HEIGH_SCL:
            case GuiIdentifiers.PG5_F_UPPER_LIM:
            case GuiIdentifiers.PG5_F_LOWER_LIM:
                return String.format("%5.3f", entryValue);
                
            case GuiIdentifiers.PG4_S_BASE_SIZE:
            case GuiIdentifiers.PG4_S_STRETCH_Y:
            case GuiIdentifiers.PG4_S_TEMP_SCL:
            case GuiIdentifiers.PG4_S_RAIN_SCL:
            case GuiIdentifiers.PG4_S_DETL_SCL:
            case GuiIdentifiers.PG4_S_B_DPTH_WT:
            case GuiIdentifiers.PG4_S_B_DPTH_OF:
            case GuiIdentifiers.PG4_S_B_SCLE_WT:
            case GuiIdentifiers.PG4_S_B_SCLE_OF:
            
            case GuiIdentifiers.PG5_F_BASE_SIZE:
            case GuiIdentifiers.PG5_F_STRETCH_Y:
            case GuiIdentifiers.PG5_F_TEMP_SCL:
            case GuiIdentifiers.PG5_F_RAIN_SCL:
            case GuiIdentifiers.PG5_F_DETL_SCL:
            case GuiIdentifiers.PG5_F_B_DPTH_WT:
            case GuiIdentifiers.PG5_F_B_DPTH_OF:
            case GuiIdentifiers.PG5_F_B_SCLE_WT:
            case GuiIdentifiers.PG5_F_B_SCLE_OF:
                return String.format("%2.3f", entryValue);
                
            case GuiIdentifiers.PG0_S_CAVE_WIDTH:
                return String.format("%2.1f", entryValue);
            
            case GuiIdentifiers.PG0_S_CHUNK: {
                ResourceLocation registryKey = ModernBetaRegistries.CHUNK_SOURCE.getKeys().get((int)entryValue);
                
                return I18n.format(PREFIX + "chunkSource." + getFormattedRegistryString(registryKey));
            }
            case GuiIdentifiers.PG0_S_BIOME: {
                ResourceLocation registryKey = ModernBetaRegistries.BIOME_SOURCE.getKeys().get((int)entryValue);
                
                return I18n.format(PREFIX + "biomeSource." + getFormattedRegistryString(registryKey));
            }
            case GuiIdentifiers.PG0_S_SURFACE: {
                ResourceLocation registryKey = ModernBetaRegistries.SURFACE_BUILDER.getKeys().get((int)entryValue);
                
                return I18n.format(PREFIX + "surfaceBuilder." + getFormattedRegistryString(registryKey));
            }
            case GuiIdentifiers.PG0_S_CARVER: {
                ResourceLocation registryKey = ModernBetaRegistries.CAVE_CARVER.getKeys().get((int)entryValue);
                
                return I18n.format(PREFIX + "caveCarver." + getFormattedRegistryString(registryKey));
            }
            case GuiIdentifiers.PG1_S_LEVEL_THEME: {
                String key = IndevTheme.values()[(int)entryValue].id;
                
                return I18n.format(PREFIX + "levelTheme." + key);
            }
            case GuiIdentifiers.PG1_S_LEVEL_TYPE: {
                String key = IndevType.values()[(int)entryValue].id;
                
                return I18n.format(PREFIX + "levelType." + key);
            }
            case GuiIdentifiers.PG1_S_LEVEL_HOUSE: {
                String key = IndevHouse.values()[(int)entryValue].id;
                
                return I18n.format(PREFIX + "levelHouse." + key);
            }
            case GuiIdentifiers.PG1_S_LAYER_TYPE: {
                String key = GenLayerType.values()[(int)entryValue].id;
                
                return I18n.format(PREFIX + "layerType." + key);
            }
            
            case GuiIdentifiers.PG1_S_LEVEL_WIDTH: return String.format("%d", ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)entryValue]);
            case GuiIdentifiers.PG1_S_LEVEL_LENGTH: return String.format("%d", ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)entryValue]);
            case GuiIdentifiers.PG1_S_LEVEL_HEIGHT: return String.format("%d", ModernBetaGeneratorSettings.LEVEL_HEIGHTS[(int)entryValue]);
            
            default: return String.format("%d", (int)entryValue);
        }
    }

    private void restoreDefaults() {
        String defaultPreset = ModernBetaConfig.guiOptions.defaultPreset;
        this.settings = ModernBetaGeneratorSettings.Factory.jsonToFactory(defaultPreset);
        
        this.createPagedList();
        this.setSettingsModified(false);
    }
    
    private void enterConfirmation(int id) {
        this.confirmMode = id;
        this.setConfirmationControls(true);
    }
    
    private void exitConfirmation() throws IOException {
        switch (this.confirmMode) {
            case GuiIdentifiers.FUNC_DONE:
                this.actionPerformed((GuiButton)this.pageList.getComponent(GuiIdentifiers.FUNC_DONE));
                break;
            case GuiIdentifiers.FUNC_DFLT:
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
        this.defaults.enabled = (this.settingsModified && !setConfirm);
        this.presets.enabled = !setConfirm;
        this.preview.enabled = !setConfirm;
        this.pageList.setActive(!setConfirm);
        
        for (Entry<Integer, GuiButton> pageTab : this.pageTabMap.entrySet()) {
            pageTab.getValue().enabled = !setConfirm;
        }
    }
    
    private void updatePageControls() {
        int page = this.pageList.getPage();
        
        this.subtitle = I18n.format("book.pageIndicator", page + 1, this.pageList.getPageCount());
        this.randomize.enabled = page < 5 || page == 6;
        
        for (Entry<Integer, GuiButton> pageTab : this.pageTabMap.entrySet()) {
            if (pageTab.getKey().intValue() == GuiIdentifiers.FUNC_INITIAL_TAB + page) {
                pageTab.getValue().enabled = false;
            } else {
                pageTab.getValue().enabled = true;
            }
        }
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
    
    private void setTextButton(int id, String value) {
        Gui guiComponent = this.pageList.getComponent(id);
        if (guiComponent != null) {
            ((GuiButton)guiComponent).displayString = value;
        }
    }
    
    private int getLevelSeaLevel() {
        String chunkSource = this.settings.chunkSource;
        String levelType = this.settings.levelType;
        int levelHeight = this.settings.levelHeight;

        int levelSeaLevel = -1;
        if (chunkSource.equals(ModernBetaBuiltInTypes.Chunk.INDEV.getRegistryString())) {
            levelSeaLevel = levelType.equals(IndevType.FLOATING.id) ? 0 : levelHeight - 32;
        } else if (chunkSource.equals(ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.getRegistryString())) {
            levelSeaLevel = levelHeight / 2;
        } else {
            levelSeaLevel = -1;
        }
        
        return levelSeaLevel;
    }
    
    private void playSound() {
        if (!this.clicked && !this.randomClicked) {
            SoundUtil.playClickSound(this.mc.getSoundHandler());
        }
    }
    
    private Set<Gui> getBaseSliderComponents() {
        Set<Gui> set = new HashSet<>();
        set.add(this.pageList.getComponent(GuiIdentifiers.PG0_S_CHUNK));
        set.add(this.pageList.getComponent(GuiIdentifiers.PG0_S_BIOME));
        set.add(this.pageList.getComponent(GuiIdentifiers.PG0_S_SURFACE));
        set.add(this.pageList.getComponent(GuiIdentifiers.PG0_S_CARVER));

        set.add(this.pageList.getComponent(GuiIdentifiers.PG1_S_LEVEL_THEME));
        set.add(this.pageList.getComponent(GuiIdentifiers.PG1_S_LEVEL_TYPE));
        set.add(this.pageList.getComponent(GuiIdentifiers.PG1_S_LEVEL_HOUSE));
        
        set.add(this.pageList.getComponent(GuiIdentifiers.PG1_S_LAYER_TYPE));
        
        return set;
    }
    
    private Set<Gui> getBiomeButtonComponents() {
        return GuiIdentifiers.BIOME_SETTINGS.keySet().stream().map(id -> this.pageList.getComponent(id)).collect(Collectors.toSet());
    }
    
    private Set<Gui> getBaseButtonComponents() {
        return GuiIdentifiers.BASE_SETTINGS.keySet().stream().map(id -> this.pageList.getComponent(id)).collect(Collectors.toSet());
    }
    
    private void setGuiEnabled() {
        ModernBetaGeneratorSettings settings = this.settings.build();
        
        // Set default enabled for certain options
        if (this.pageList != null) {
            for (Entry<ResourceLocation, GuiPredicate> entry : ModernBetaClientRegistries.GUI_PREDICATE.getEntrySet()) {
                int[] guiIds = entry.getValue().getIds();
                boolean enabled = entry.getValue().test(settings);
                
                for (int i = 0; i < guiIds.length; ++i) {
                    this.setButtonEnabled(guiIds[i], enabled);
                    this.setFieldEnabled(guiIds[i], enabled);
                }
                
                if (guiIds.length <= 0) {
                    if (this.customIds.containsValue(entry.getKey())) {
                        int customId = this.customIds.inverse().get(entry.getKey());
                        
                        this.setButtonEnabled(customId, enabled);
                        this.setFieldEnabled(customId, enabled);
                    }
                }
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
    
    private boolean isMouseOverPageTab(int mouseX, int mouseY) {
        if (this.pageTabMap != null) {
            for (Entry<Integer, GuiButton> pageTab : this.pageTabMap.entrySet()) {
                if (pageTab.getValue().isMouseOver()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private List<ResourceLocation> getBiomeKeys() {
        return ForgeRegistries.BIOMES.getEntries()
            .stream()
            .map(e -> e.getKey())
            .collect(Collectors.toCollection(LinkedList::new));
    }
    
    private void openBiomeScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial) {
        Function<ResourceLocation, String> nameFormatter = key -> ForgeRegistries.BIOMES.getValue(key).getBiomeName();
        this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(this, consumer, nameFormatter, initial, "biome", this.getBiomeKeys()));
    }
    
    private void openRegistryScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial, String nbtTag, List<ResourceLocation> registryKeys) {
        Function<ResourceLocation, String> nameFormatter = key -> I18n.format(String.format("%s.%s.%s.%s", "createWorld.customize.custom", nbtTag, key.getNamespace(), key.getPath()));
        this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(this, consumer, nameFormatter, initial, nbtTag, registryKeys));
    }
    
    private static String getFormattedRegistryString(ResourceLocation registryKey) {
        return registryKey.getNamespace() + "." + registryKey.getPath();
    }
    
    private static GuiPageButtonList.GuiLabelEntry createGuiLabel(int id, String... tags) {
        return new GuiPageButtonList.GuiLabelEntry(id, I18n.format(PREFIX_LABEL + String.join(".", tags)), true);
    }
    
    private static GuiPageButtonList.GuiLabelEntry createGuiLabelNoPrefix(int id, String... tags) {
        return new GuiPageButtonList.GuiLabelEntry(id, String.join(".", tags), true);
    }
    
    private static GuiPageButtonList.GuiSlideEntry createGuiSlider(int id, String tag, float minValue, float maxValue, float initialValue, FormatHelper formatHelper) {
        return new GuiPageButtonList.GuiSlideEntry(id, I18n.format(PREFIX + tag), true, formatHelper, minValue, maxValue, initialValue);
    }
    
    private static GuiPageButtonList.GuiButtonEntry createGuiButton(int id, String tag, boolean initialValue) {
        return new GuiPageButtonList.GuiButtonEntry(id, I18n.format(PREFIX + tag), true, initialValue);
    }
    
    private static GuiPageButtonList.EditBoxEntry createGuiField(int id, String formattedValue, Predicate<String> predicate) {
        return new GuiPageButtonList.EditBoxEntry(id, formattedValue, true, predicate);
    }
    
    private static String getFormattedRegistryName(String registryName, String langName, int truncateLen) {
        ResourceLocation registryKey = new ResourceLocation(registryName);
        String formattedName = I18n.format(String.format("%s%s.%s.%s", PREFIX, langName, registryKey.getNamespace(), registryKey.getPath()));
        
        // Truncate if registry name is too long to fit in button
        if (truncateLen > 0 && formattedName.length() > truncateLen) {
            formattedName = formattedName.substring(0, truncateLen) + "...";
        }
        
        return String.format("%s: %s", I18n.format(PREFIX + langName), formattedName);
    }
    
    private static String getFormattedBiomeName(String registryName, boolean prefix, int truncateLen) {
        Biome biome = BiomeUtil.getBiome(new ResourceLocation(registryName), "generator");
        String biomeName = biome.getBiomeName();
        
        // Truncate if biome name is too long to fit in button
        if (truncateLen > 0 && biomeName.length() > truncateLen) {
            biomeName = biomeName.substring(0, truncateLen) + "...";
        }
        
        return prefix ? String.format("%s: %s", I18n.format(PREFIX + "fixedBiome"), biomeName) : biomeName;
    }

    private static int getNdx(int[] arr, int val) {
        for (int i = 0; i < arr.length; ++i) {
            if (val == arr[i])
                return i;
        }
        
        return 0;
    }
    
    private static float roundToThreeDec(float entryValue) {
        DF_THREE.setRoundingMode(RoundingMode.FLOOR);
        
        return Floats.tryParse(DF_THREE.format(entryValue));
    }
    
    private static float roundToTwoDec(float entryValue) {
        DF_THREE.setRoundingMode(RoundingMode.FLOOR);
        
        return Floats.tryParse(DF_ONE.format(entryValue));
    }

    private class CreateGuiPropertyVisitor implements GuiPropertyVisitor {
        @Override
        public GuiPageButtonList.GuiListEntry visit(BooleanProperty property, int guiIdentifier) {
            return createGuiButton(guiIdentifier, "enabled", property.getValue());
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(FloatProperty property, int guiIdentifier) {
            String formattedValue = String.format(property.getFormatter(), property.getValue());
            
            switch(property.getGuiType()) {
                case FIELD:
                    return createGuiField(guiIdentifier, formattedValue, property.getStringPredicate());
                case SLIDER:
                    return createGuiSlider(
                        guiIdentifier,
                        "entry",
                        property.getMinValue(),
                        property.getMaxValue(), 
                        property.getValue(),
                        GuiScreenCustomizeWorld.this
                    );
                default: 
                    return createGuiField(guiIdentifier, formattedValue, property.getStringPredicate());
            }
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(IntProperty property, int guiIdentifier) {
            String formattedValue = String.format(property.getFormatter(), property.getValue());
            
            switch(property.getGuiType()) {
                case FIELD:
                    return createGuiField(guiIdentifier, formattedValue, property.getStringPredicate());
                case SLIDER:
                    return createGuiSlider(
                        guiIdentifier,
                        "entry",
                        property.getMinValue(),
                        property.getMaxValue(), 
                        property.getValue(),
                        GuiScreenCustomizeWorld.this
                    );
                default: 
                    return createGuiField(guiIdentifier, formattedValue, property.getStringPredicate());
            }
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(StringProperty property, int guiIdentifier) {
            return createGuiField(guiIdentifier, property.getValue(), string -> true);
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(ListProperty property, int guiIdentifier) {
            int listNdx = property.indexOf(property.getValue());
            if (listNdx == -1) 
                listNdx = 0;
            
            return createGuiSlider(
                guiIdentifier,
                "entry",
                0.0f,
                property.getValues().length - 1,
                listNdx, 
                GuiScreenCustomizeWorld.this
            );
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(BiomeProperty property, int guiIdentifier) {
            return createGuiButton(guiIdentifier, "enabled", true);
        }
        
    }
    
    private class SetEntryValuePropertyVisitor implements EntryValuePropertyVisitor {
        @Override
        public void visit(BooleanProperty property, int guiIdentifier, boolean value, ResourceLocation registryKey) {
            property.setValue(value);
            GuiScreenCustomizeWorld.this.setTextButton(
                guiIdentifier,
                I18n.format(property.getValue() ? "gui.yes" : "gui.no")
            );
        }

        @Override
        public void visit(FloatProperty property, int guiIdentifier, Object value, ResourceLocation registryKey) {
            if (property.getGuiType() == PropertyGuiType.FIELD) {
                String entryString = (String)value;
                
                float entryValue = 0.0f;
                float newEntryValue = 0.0f;
                
                try {
                    entryValue = Float.parseFloat(entryString);
                    
                } catch (NumberFormatException e) { }

                property.setValue(MathHelper.clamp(entryValue, property.getMinValue(), property.getMaxValue()));
                newEntryValue = property.getValue();
                
                if (newEntryValue != entryValue) {
                    ((GuiTextField)GuiScreenCustomizeWorld.this.pageList.getComponent(guiIdentifier))
                        .setText(GuiScreenCustomizeWorld.this.getFormattedValue(guiIdentifier, newEntryValue));
                }
                
            } else if (property.getGuiType() == PropertyGuiType.SLIDER) {
                property.setValue((Float)value);
            }
        }

        @Override
        public void visit(IntProperty property, int guiIdentifier, Object value, ResourceLocation registryKey) {
            if (property.getGuiType() == PropertyGuiType.FIELD) {
                String entryString = (String)value;
                
                float entryValue = 0.0f;
                float newEntryValue = 0.0f;
                
                try {
                    entryValue = Float.parseFloat(entryString);
                    
                } catch (NumberFormatException e) { }
                
                property.setValue((int)MathHelper.clamp(entryValue, property.getMinValue(), property.getMaxValue()));
                newEntryValue = property.getValue();
                
                if (newEntryValue != entryValue) {
                    ((GuiTextField)GuiScreenCustomizeWorld.this.pageList.getComponent(guiIdentifier))
                        .setText(GuiScreenCustomizeWorld.this.getFormattedValue(guiIdentifier, newEntryValue));
                }
                
            } else if (property.getGuiType() == PropertyGuiType.SLIDER) {
                property.setValue(((Float)value).intValue());
            }
        }

        @Override
        public void visit(StringProperty property, int guiIdentifier, String value, ResourceLocation registryKey) {
            property.setValue(value);
        }

        @Override
        public void visit(ListProperty property, int guiIdentifier, float value, ResourceLocation registryKey) {
            property.setValue(property.getValues()[(int)value]);
        }

        @Override
        public void visit(BiomeProperty property, int guiIdentifier, ResourceLocation registryKey) {
            GuiScreenCustomizeWorld.this.openBiomeScreen(
                (str, factory) -> ((BiomeProperty)factory.customProperties.get(registryKey)).setValue(str),
                property.getValue()
            );
        };
        
    }
}