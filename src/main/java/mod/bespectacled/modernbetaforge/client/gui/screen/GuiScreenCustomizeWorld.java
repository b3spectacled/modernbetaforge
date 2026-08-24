package mod.bespectacled.modernbetaforge.client.gui.screen;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.client.property.GuiProperty;
import mod.bespectacled.modernbetaforge.api.client.property.ScreenProperty;
import mod.bespectacled.modernbetaforge.api.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.EntityEntryProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.property.Property;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.property.RangedProperty;
import mod.bespectacled.modernbetaforge.api.property.RegistryProperty;
import mod.bespectacled.modernbetaforge.api.property.StringProperty;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.client.gui.GuiColors;
import mod.bespectacled.modernbetaforge.client.gui.GuiIdentifiers;
import mod.bespectacled.modernbetaforge.client.gui.element.GuiButtonNav;
import mod.bespectacled.modernbetaforge.client.gui.element.GuiButtonTab;
import mod.bespectacled.modernbetaforge.client.gui.modal.GuiModalChangelist;
import mod.bespectacled.modernbetaforge.client.gui.modal.GuiModalConfirm;
import mod.bespectacled.modernbetaforge.client.gui.screen.GuiScreenCustomizePreview.PreviewSettings;
import mod.bespectacled.modernbetaforge.client.settings.KeyBindings;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.compat.dynamictrees.CompatDynamicTrees;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.FormattedPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.util.ExecutorWrapper;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.util.PresetUtil;
import mod.bespectacled.modernbetaforge.util.SoundUtil;
import mod.bespectacled.modernbetaforge.world.biome.layer.GenLayerType;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevTheme;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevType;
import mod.bespectacled.modernbetaforge.world.feature.OreType;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiPageButtonList.GuiEntry;
import net.minecraft.client.gui.GuiPageButtonList.GuiListEntry;
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
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizeWorld extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder {
    private static final ResourceLocation TOOLTIP_BACKGROUND = new ResourceLocation("textures/blocks/cobblestone.png");
    private static final String PREFIX_ADDON = "createWorld.customize.custom.";
    private static final String PREFIX = "createWorld.customize.custom.modernbetaforge.";
    private static final String PREFIX_TAB = "createWorld.customize.custom.tab.modernbetaforge.";
    private static final String PREFIX_LABEL = "createWorld.customize.custom.label.modernbetaforge.";
    
    private static final int PAGE_TITLE_HEIGHT = 7;
    
    private static final int PAGELIST_PADDING_TOP = 40;
    private static final int PAGELIST_PADDING_BOTTOM = 32;
    private static final int PAGELIST_SCROLLBAR_PADDING = 24;
    private static final int DEFAULT_NAME_TRUNCATE_LEN = 132;

    private static final int BUTTON_WIDTH = 70;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SLOT_HEIGHT = 25;
    
    private static final int TAB_SPACE = 2;
    private static final int TAB_BUTTON_WIDTH = 44;
    private static final int TAB_BUTTON_HEIGHT = 20;
    
    private static final int TOOLTIP_MAX_WIDTH = 140;
    private static final int TOOLTIP_LINE_SPACING = 3;
    private static final long TOOLTIP_DELAY = 250L;
    
    private final GuiCreateWorld parent;
    private final Predicate<String> floatFilter;
    private final Predicate<String> intFilter;
    private final ModernBetaGeneratorSettings.Factory defaultSettings;
    private final Random random;
    private final Map<Integer, Boolean> enabledMap;
    private final ExecutorWrapper executor;
    
    protected String title;
    protected String[] pageNames;
    protected Map<Integer, GuiButtonTab> pageTabMap;

    private ModernBetaGeneratorSettings.Factory settings;
    private ModernBetaGeneratorSettings.Factory prevSettings;
    private ModernBetaGeneratorSettings builtSettings;
    private GuiPageButtonList pageList;
    private GuiPageButtonList.GuiListEntry[][] pageArray;
    private GuiButton buttonDone;
    private GuiButton buttonRandomize;
    private GuiButton buttonDefaults;
    private GuiButton buttonPresets;
    private GuiButton buttonPreview;
    private GuiButton buttonNavL;
    private GuiButton buttonNavR;
    private boolean settingsModified;
    private boolean clicked;
    private boolean clickedRandom;
    private long lastNavPressed;
    private int customId;
    private int prevHoveredId;
    private int hoveredId;
    private long lastHovered;
    private BiMap<Integer, ResourceLocation> propertyMap;
    private BiMap<Integer, ResourceLocation> guiPropertyMap;
    private Map<Integer, String> translationKeyMap;
    private int tabStartX;
    private int tabEndX;
    private boolean isFocused;
    private boolean displayNavButtons;
    private PreviewSettings previewSettings;
    
    public GuiScreenCustomizeWorld(GuiCreateWorld parent, String string) {
        this.title = I18n.format("options.customizeTitle");
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
        
        String defaultPreset = PresetUtil.getDefaultPreset();
        this.defaultSettings = ModernBetaGeneratorSettings.Factory.jsonToFactory(defaultPreset);
        this.random = new Random();
        this.parent = parent;
        this.enabledMap = new HashMap<>();
        this.executor = new ExecutorWrapper(1, "customization");
        
        this.previewSettings = new PreviewSettings();
        this.isFocused = true;
        this.displayNavButtons = ModernBetaConfig.guiOptions.displayNavButtons;
        
        this.loadValues(string);
    }
    
    private void createPagedList() {
        this.customId = GuiIdentifiers.CUSTOM_INITIAL_ID;
        this.propertyMap = HashBiMap.create();
        this.guiPropertyMap = HashBiMap.create();
        this.translationKeyMap = new HashMap<>();
        
        int chunkSourceId = ModernBetaRegistries.CHUNK_SOURCE.getKeys().indexOf(new ResourceLocation(this.settings.chunkSource));
        int biomeSourceId = ModernBetaRegistries.BIOME_SOURCE.getKeys().indexOf(new ResourceLocation(this.settings.biomeSource));
        int surfaceBuilderId = ModernBetaRegistries.SURFACE_BUILDER.getKeys().indexOf(new ResourceLocation(this.settings.surfaceBuilder));
        int caveCarverId = ModernBetaRegistries.CAVE_CARVER.getKeys().indexOf(new ResourceLocation(this.settings.caveCarver));
        int worldSpawnerId = ModernBetaRegistries.WORLD_SPAWNER.getKeys().indexOf(new ResourceLocation(this.settings.worldSpawner));
        
        int defaultBlockId = ModernBetaRegistries.DEFAULT_BLOCK.getKeys().indexOf(new ResourceLocation(this.settings.defaultBlock));
        int defaultFluidId = ForgeRegistryUtil.getFluidBlockRegistryNames().indexOf(new ResourceLocation(this.settings.defaultFluid));
        
        int levelThemeId = IndevTheme.fromId(this.settings.levelTheme).ordinal();
        int levelTypeId = IndevType.fromId(this.settings.levelType).ordinal();
        int levelWidth = getNdx(ModernBetaGeneratorSettings.LEVEL_WIDTHS, this.settings.levelWidth);
        int levelLength = getNdx(ModernBetaGeneratorSettings.LEVEL_WIDTHS, this.settings.levelLength);
        int levelHeight = getNdx(ModernBetaGeneratorSettings.LEVEL_HEIGHTS, this.settings.levelHeight);
        int levelHouseId = IndevHouse.fromId(this.settings.levelHouse).ordinal();
        int levelSeaLevel = this.getLevelSeaLevel();
        String levelSeaLevelStr = levelSeaLevel == -1 ? "" : Integer.toString(levelSeaLevel);
        
        int layerTypeId = GenLayerType.fromId(this.settings.layerType).ordinal();
        int oreTypeId = OreType.fromId(this.settings.oreType).ordinal();
        
        boolean useMenu = ModernBetaConfig.guiOptions.useMenusForBasicSettings;
        GuiPageButtonList.GuiListEntry chunkEntry = useMenu ? 
            this.createGuiButton(GuiIdentifiers.PG0_B_CHUNK, NbtTags.CHUNK_SOURCE, true) :
            this.createGuiSlider(GuiIdentifiers.PG0_S_CHUNK, NbtTags.CHUNK_SOURCE, 0f, ModernBetaRegistries.CHUNK_SOURCE.getKeys().size() - 1, chunkSourceId, this);
        GuiPageButtonList.GuiListEntry biomeEntry = useMenu ? 
            this.createGuiButton(GuiIdentifiers.PG0_B_BIOME, NbtTags.BIOME_SOURCE, true) :
            this.createGuiSlider(GuiIdentifiers.PG0_S_BIOME, NbtTags.BIOME_SOURCE, 0f, ModernBetaRegistries.BIOME_SOURCE.getKeys().size() - 1, biomeSourceId, this);
        GuiPageButtonList.GuiListEntry surfaceEntry = useMenu ? 
            this.createGuiButton(GuiIdentifiers.PG0_B_SURFACE, NbtTags.SURFACE_BUILDER, true) :
            this.createGuiSlider(GuiIdentifiers.PG0_S_SURFACE, NbtTags.SURFACE_BUILDER, 0f, ModernBetaRegistries.SURFACE_BUILDER.getKeys().size() - 1, surfaceBuilderId, this);
        GuiPageButtonList.GuiListEntry carverEntry = useMenu ? 
            this.createGuiButton(GuiIdentifiers.PG0_B_CARVER, NbtTags.CAVE_CARVER, true) :
            this.createGuiSlider(GuiIdentifiers.PG0_S_CARVER, NbtTags.CAVE_CARVER, 0f, ModernBetaRegistries.CAVE_CARVER.getKeys().size() - 1, caveCarverId, this);
        GuiPageButtonList.GuiListEntry spawnEntry = useMenu ? 
            this.createGuiButton(GuiIdentifiers.PG0_B_SPAWN, NbtTags.WORLD_SPAWNER, true) :
            this.createGuiSlider(GuiIdentifiers.PG0_S_SPAWN, NbtTags.WORLD_SPAWNER, 0f, ModernBetaRegistries.WORLD_SPAWNER.getKeys().size() - 1, worldSpawnerId, this);
        GuiPageButtonList.GuiListEntry blockEntry = useMenu ? 
            this.createGuiButton(GuiIdentifiers.PG0_B_BLOCK, NbtTags.DEFAULT_BLOCK, true) :
            this.createGuiSlider(GuiIdentifiers.PG0_S_BLOCK, NbtTags.DEFAULT_BLOCK, 0f, ModernBetaRegistries.DEFAULT_BLOCK.getKeys().size() - 1, defaultBlockId, this);
        GuiPageButtonList.GuiListEntry fluidEntry = useMenu ? 
            this.createGuiButton(GuiIdentifiers.PG0_B_FLUID, NbtTags.DEFAULT_FLUID, true) :
            this.createGuiSlider(GuiIdentifiers.PG0_S_FLUID, NbtTags.DEFAULT_FLUID, 0f, ForgeRegistryUtil.getFluidBlockRegistryNames().size() - 1, defaultFluidId, this);
        
        GuiPageButtonList.GuiListEntry[] pageBasic = {
            chunkEntry,
            biomeEntry,
            surfaceEntry,
            this.createGuiButton(GuiIdentifiers.PG0_B_FIXED, NbtTags.SINGLE_BIOME, true),
            carverEntry,
            spawnEntry,

            this.createGuiLabel(GuiIdentifiers.PG0_L_BIOME_REPLACEMENT, "page0", "biomeReplacement"),
            null,
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_OCEAN, NbtTags.REPLACE_OCEAN_BIOMES, this.settings.replaceOceanBiomes),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_BEACH, NbtTags.REPLACE_BEACH_BIOMES, this.settings.replaceBeachBiomes),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_RIVER, NbtTags.REPLACE_RIVER_BIOMES, this.settings.replaceRiverBiomes),
            null,
            
            this.createGuiLabel(GuiIdentifiers.PG0_L_BASIC_FEATURES, "page0", "overworld"),
            null,
            blockEntry,
            fluidEntry,
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_SANDSTONE, NbtTags.USE_SANDSTONE, this.settings.useSandstone),
            this.createGuiSlider(GuiIdentifiers.PG0_S_SEA_LEVEL, NbtTags.SEA_LEVEL, ModernBetaGeneratorSettings.getMinSeaLevel(), ModernBetaGeneratorSettings.getMaxSeaLevel(), (float)this.settings.seaLevel, this),
            this.createGuiSlider(GuiIdentifiers.PG0_S_CAVE_WIDTH, NbtTags.CAVE_WIDTH, ModernBetaGeneratorSettings.MIN_CAVE_WIDTH, ModernBetaGeneratorSettings.MAX_CAVE_WIDTH, this.settings.caveWidth, this),
            this.createGuiSlider(GuiIdentifiers.PG0_S_CAVE_HEIGHT, NbtTags.CAVE_HEIGHT, ModernBetaGeneratorSettings.getMinCaveHeight(), ModernBetaGeneratorSettings.getMaxCaveHeight(), (float)this.settings.caveHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG0_S_CAVE_COUNT, NbtTags.CAVE_COUNT, ModernBetaGeneratorSettings.MIN_CAVE_COUNT, ModernBetaGeneratorSettings.MAX_CAVE_COUNT, (float)this.settings.caveCount, this),
            this.createGuiSlider(GuiIdentifiers.PG0_S_CAVE_CHANCE, NbtTags.CAVE_CHANCE, ModernBetaGeneratorSettings.MIN_CAVE_CHANCE, ModernBetaGeneratorSettings.MAX_CAVE_CHANCE, (float)this.settings.caveChance, this),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_RAVINES, NbtTags.USE_RAVINES, this.settings.useRavines),
            this.createGuiSlider(GuiIdentifiers.PG0_S_RAVINE_CHANCE, NbtTags.RAVINE_CHANCE, ModernBetaGeneratorSettings.MIN_RAVINE_CHANCE, ModernBetaGeneratorSettings.MAX_RAVINE_CHANCE, (float)this.settings.ravineChance, this),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_UNDERWATER_CAVES, NbtTags.USE_UNDERWATER_CAVES, this.settings.useUnderwaterCaves),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_SHAFTS, NbtTags.USE_MINESHAFTS, this.settings.useMineShafts),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_VILLAGES, NbtTags.USE_VILLAGES, this.settings.useVillages),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_VILLAGE_VARIANTS, NbtTags.USE_VILLAGE_VARIANTS, this.settings.useVillageVariants),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_HOLDS, NbtTags.USE_STRONGHOLDS, this.settings.useStrongholds),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_TEMPLES, NbtTags.USE_TEMPLES, this.settings.useTemples),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_MONUMENTS, NbtTags.USE_MONUMENTS, this.settings.useMonuments),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_MANSIONS, NbtTags.USE_MANSIONS, this.settings.useMansions),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_DUNGEONS, NbtTags.USE_DUNGEONS, this.settings.useDungeons),
            this.createGuiSlider(GuiIdentifiers.PG0_S_DUNGEON_CHANCE, NbtTags.DUNGEON_CHANCE, ModernBetaGeneratorSettings.MIN_DUNGEON_CHANCE, ModernBetaGeneratorSettings.MAX_DUNGEON_CHANCE, (float)this.settings.dungeonChance, this),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_WATER_LAKES, NbtTags.USE_WATER_LAKES, this.settings.useWaterLakes),
            this.createGuiSlider(GuiIdentifiers.PG0_S_WATER_LAKE_CHANCE, NbtTags.WATER_LAKE_CHANCE, ModernBetaGeneratorSettings.MIN_WATER_LAKE_CHANCE, ModernBetaGeneratorSettings.MAX_WATER_LAKE_CHANCE, (float)this.settings.waterLakeChance, this),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_LAVA_LAKES, NbtTags.USE_LAVA_LAKES, this.settings.useLavaLakes),
            this.createGuiSlider(GuiIdentifiers.PG0_S_LAVA_LAKE_CHANCE, NbtTags.LAVA_LAKE_CHANCE, ModernBetaGeneratorSettings.MIN_LAVA_LAKE_CHANCE, ModernBetaGeneratorSettings.MAX_LAVA_LAKE_CHANCE, (float)this.settings.lavaLakeChance, this),
            
            this.createGuiLabel(GuiIdentifiers.PG0_L_NETHER_FEATURES, "page0", "nether"),
            null,
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_OLD_NETHER, NbtTags.USE_OLD_NETHER, this.settings.useOldNether),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_NETHER_CAVES, NbtTags.USE_NETHER_CAVES, this.settings.useNetherCaves),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_FORTRESSES, NbtTags.USE_FORTRESSES, this.settings.useFortresses),
            this.createGuiButton(GuiIdentifiers.PG0_B_USE_LAVA_POCKETS, NbtTags.USE_LAVA_POCKETS, this.settings.useLavaPockets)
        };
        
        GuiPageButtonList.GuiListEntry[] pageChunk = {
            this.createGuiLabel(GuiIdentifiers.PG1_L_INFDEV_227_FEATURES, "page1", "infdev227"),
            null,
            this.createGuiButton(GuiIdentifiers.PG1_B_USE_INFDEV_WALLS, NbtTags.USE_INFDEV_WALLS, this.settings.useInfdevWalls),
            this.createGuiButton(GuiIdentifiers.PG1_B_USE_INFDEV_PYRAMIDS, NbtTags.USE_INFDEV_PYRAMIDS, this.settings.useInfdevPyramids),
            
            this.createGuiLabel(GuiIdentifiers.PG1_L_INDEV_FEATURES, "page1", "indev"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_THEME, NbtTags.LEVEL_THEME, 0f, IndevTheme.values().length - 1, levelThemeId, this),
            this.createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_TYPE, NbtTags.LEVEL_TYPE, 0f, IndevType.values().length - 1, levelTypeId, this),
            this.createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_WIDTH, NbtTags.LEVEL_WIDTH, 0f, ModernBetaGeneratorSettings.LEVEL_WIDTHS.length - 1, levelWidth, this),
            this.createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_LENGTH, NbtTags.LEVEL_LENGTH, 0f, ModernBetaGeneratorSettings.LEVEL_WIDTHS.length - 1, levelLength, this),
            this.createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_HEIGHT, NbtTags.LEVEL_HEIGHT, 0f, ModernBetaGeneratorSettings.LEVEL_HEIGHTS.length - 1, levelHeight, this),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG0_L_INDEV_SEA_LEVEL, ": " + levelSeaLevelStr, false, PREFIX + "seaLevel"),                                                                                                                                     
            this.createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_HOUSE, NbtTags.LEVEL_HOUSE, 0f, IndevHouse.values().length - 1, levelHouseId, this),
            this.createGuiButton(GuiIdentifiers.PG1_B_USE_INDEV_CAVES, NbtTags.USE_INDEV_CAVES, this.settings.useIndevCaves),
            this.createGuiSlider(GuiIdentifiers.PG1_S_LEVEL_CAVE_WIDTH, NbtTags.LEVEL_CAVE_WIDTH, ModernBetaGeneratorSettings.MIN_LEVEL_CAVE_WIDTH, ModernBetaGeneratorSettings.MAX_LEVEL_CAVE_WIDTH, this.settings.levelCaveWidth, this),
            null,
            
            this.createGuiLabel(GuiIdentifiers.PG1_L_RELEASE_FEATURES, "page1", "release"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG1_S_LAYER_SZ, NbtTags.LAYER_SIZE, ModernBetaGeneratorSettings.MIN_BIOME_SIZE, ModernBetaGeneratorSettings.MAX_BIOME_SIZE, this.settings.layerSize, this),
            this.createGuiSlider(GuiIdentifiers.PG1_S_RIVER_SZ, "riverRarity", ModernBetaGeneratorSettings.MIN_RIVER_SIZE, ModernBetaGeneratorSettings.MAX_RIVER_SIZE, this.settings.riverSize, this),
            this.createGuiSlider(GuiIdentifiers.PG1_S_LAYER_TYPE, NbtTags.LAYER_TYPE, 0f, GenLayerType.values().length - 1, layerTypeId, this)
        };
        
        GuiPageButtonList.GuiListEntry[] pageBiome = {
            this.createGuiLabel(GuiIdentifiers.PG2_L_BETA, "page2", "beta"),
            null,
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_GRASS, NbtTags.USE_TALL_GRASS, this.settings.useTallGrass),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_FLOWERS, NbtTags.USE_NEW_FLOWERS, this.settings.useNewFlowers),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_DOUBLE, NbtTags.USE_DOUBLE_PLANTS, this.settings.useDoublePlants),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_PADS, NbtTags.USE_LILY_PADS, this.settings.useLilyPads),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_MELONS, NbtTags.USE_MELONS, this.settings.useMelons),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_WELLS, NbtTags.USE_DESERT_WELLS, this.settings.useDesertWells),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_FOSSILS, NbtTags.USE_FOSSILS, this.settings.useFossils),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_SAND_DISKS, NbtTags.USE_SAND_DISKS, this.settings.useSandDisks),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_GRAV_DISKS, NbtTags.USE_GRAVEL_DISKS, this.settings.useGravelDisks),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_CLAY_DISKS, NbtTags.USE_CLAY_DISKS, this.settings.useClayDisks),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_FANCY_OAK, NbtTags.USE_NEW_FANCY_OAK_TREES, this.settings.useNewFancyOakTrees),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_BIRCH, NbtTags.USE_BIRCH_TREES, this.settings.useBirchTrees),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_PINE, NbtTags.USE_PINE_TREES, this.settings.usePineTrees),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_SWAMP, NbtTags.USE_SWAMP_TREES, this.settings.useSwampTrees),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_JUNGLE, NbtTags.USE_JUNGLE_TREES, this.settings.useJungleTrees),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_ACACIA, NbtTags.USE_ACACIA_TREES, this.settings.useAcaciaTrees),
            this.createGuiButton(GuiIdentifiers.PG2_B_USE_DARK_OAK, NbtTags.USE_DARK_OAK_TREES, this.settings.useDarkOakTrees),
            null,
            
            this.createGuiLabel(GuiIdentifiers.PG2_L_RELEASE, "page2", "release"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG2_S_BIOME_SZ, NbtTags.BIOME_SIZE, ModernBetaGeneratorSettings.MIN_BIOME_SIZE, ModernBetaGeneratorSettings.MAX_BIOME_SIZE, this.settings.biomeSize, this),
            this.createGuiSlider(GuiIdentifiers.PG2_S_SNOWY_CHANCE, NbtTags.SNOWY_BIOME_CHANCE, ModernBetaGeneratorSettings.MIN_SNOWY_BIOME_CHANCE, ModernBetaGeneratorSettings.MAX_SNOWY_BIOME_CHANCE, this.settings.snowyBiomeChance, this),
        
            this.createGuiLabel(GuiIdentifiers.PG2_L_MOBS, "page2", "mobSpawn"),
            null,
            this.createGuiButton(GuiIdentifiers.PG2_B_SPAWN_CREATURE, NbtTags.SPAWN_NEW_CREATURE_MOBS, this.settings.spawnNewCreatureMobs),
            this.createGuiButton(GuiIdentifiers.PG2_B_SPAWN_MONSTER, NbtTags.SPAWN_NEW_MONSTER_MOBS, this.settings.spawnNewMonsterMobs),
            this.createGuiButton(GuiIdentifiers.PG2_B_SPAWN_WATER, NbtTags.SPAWN_WATER_MOBS, this.settings.spawnWaterMobs),
            this.createGuiButton(GuiIdentifiers.PG2_B_SPAWN_AMBIENT, NbtTags.SPAWN_AMBIENT_MOBS, this.settings.spawnAmbientMobs),
            this.createGuiButton(GuiIdentifiers.PG2_B_SPAWN_WOLVES, NbtTags.SPAWN_WOLVES, this.settings.spawnWolves),
            null
        };
        
        GuiPageButtonList.GuiListEntry[] pageOre = {
            this.createGuiSlider(GuiIdentifiers.PG3_S_ORE_TYPE, NbtTags.ORE_TYPE, 0f, OreType.values().length - 1, oreTypeId, this),
            null,
                
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_DIRT_NAME, false, "tile.dirt.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIRT_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.dirtSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIRT_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.dirtCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIRT_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.dirtMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIRT_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.dirtMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_GRAV_NAME, false, "tile.gravel.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_GRAV_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.gravelSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GRAV_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.gravelCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GRAV_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.gravelMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GRAV_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.gravelMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_GRAN_NAME, false, "tile.stone.granite.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_GRAN_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.graniteSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GRAN_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.graniteCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GRAN_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.graniteMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GRAN_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.graniteMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_DIOR_NAME, false, "tile.stone.diorite.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIOR_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.dioriteSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIOR_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.dioriteCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIOR_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.dioriteMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIOR_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.dioriteMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_ANDE_NAME, false, "tile.stone.andesite.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_ANDE_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.andesiteSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_ANDE_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.andesiteCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_ANDE_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.andesiteMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_ANDE_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.andesiteMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_COAL_NAME, false, "tile.oreCoal.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_COAL_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.coalSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_COAL_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.coalCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_COAL_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.coalMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_COAL_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.coalMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_IRON_NAME, false, "tile.oreIron.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_IRON_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.ironSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_IRON_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.ironCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_IRON_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.ironMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_IRON_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.ironMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_GOLD_NAME, false, "tile.oreGold.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_GOLD_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.goldSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GOLD_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.goldCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GOLD_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.goldMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_GOLD_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.goldMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_REDS_NAME, false, "tile.oreRedstone.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_REDS_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.redstoneSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_REDS_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.redstoneCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_REDS_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.redstoneMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_REDS_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.redstoneMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_DIAM_NAME, false, "tile.oreDiamond.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIAM_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.diamondSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIAM_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.diamondCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIAM_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.diamondMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_DIAM_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.diamondMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_LAPS_NAME, false, "tile.oreLapis.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_LAPS_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.lapisSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_LAPS_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.lapisCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_LAPS_CTR, "center", ModernBetaGeneratorSettings.getMinOreCenter(), ModernBetaGeneratorSettings.getMaxOreCenter(), (float)this.settings.lapisCenterHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_LAPS_SPR, "spread", ModernBetaGeneratorSettings.getMinOreSpread(), ModernBetaGeneratorSettings.getMaxOreSpread(), (float)this.settings.lapisSpread, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_EMER_NAME, false, "tile.oreEmerald.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_EMER_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.emeraldSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_EMER_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.emeraldCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_EMER_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.emeraldMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_EMER_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.emeraldMaxHeight, this),

            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_CLAY_NAME, false, "tile.clay.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_CLAY_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.claySize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_CLAY_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.clayCount, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_CLAY_MIN, "minHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.clayMinHeight, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_CLAY_MAX, "maxHeight", ModernBetaGeneratorSettings.getMinOreHeight(), ModernBetaGeneratorSettings.getMaxOreHeight(), (float)this.settings.clayMaxHeight, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_QRTZ_NAME, false, "tile.netherquartz.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_QRTZ_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.quartzSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_QRTZ_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.quartzCount, this),

            this.createGuiLabelNoPrefix(GuiIdentifiers.PG3_L_MGMA_NAME, false, "tile.magma.name"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG3_S_MGMA_SIZE, "size", ModernBetaGeneratorSettings.MIN_ORE_SIZE, ModernBetaGeneratorSettings.MAX_ORE_SIZE, (float)this.settings.magmaSize, this),
            this.createGuiSlider(GuiIdentifiers.PG3_S_MGMA_CNT, "count", ModernBetaGeneratorSettings.MIN_ORE_COUNT, ModernBetaGeneratorSettings.MAX_ORE_COUNT, (float)this.settings.magmaCount, this),
        };
        
        GuiPageButtonList.GuiListEntry[] pageNoise0 = {
            this.createGuiSlider(GuiIdentifiers.PG4_S_MAIN_NS_X, NbtTags.MAIN_NOISE_SCALE_X, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE, this.settings.mainNoiseScaleX, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_MAIN_NS_Y, NbtTags.MAIN_NOISE_SCALE_Y, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE, this.settings.mainNoiseScaleY, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_MAIN_NS_Z, NbtTags.MAIN_NOISE_SCALE_Z, ModernBetaGeneratorSettings.MIN_MAIN_NOISE, ModernBetaGeneratorSettings.MAX_MAIN_NOISE, this.settings.mainNoiseScaleZ, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_SCLE_NS_X, NbtTags.SCALE_NOISE_SCALE_X, ModernBetaGeneratorSettings.MIN_SCALE_NOISE, ModernBetaGeneratorSettings.MAX_SCALE_NOISE, this.settings.scaleNoiseScaleX, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_SCLE_NS_Z, NbtTags.SCALE_NOISE_SCALE_Z, ModernBetaGeneratorSettings.MIN_SCALE_NOISE, ModernBetaGeneratorSettings.MAX_SCALE_NOISE, this.settings.scaleNoiseScaleZ, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_DPTH_NS_X, NbtTags.DEPTH_NOISE_SCALE_X, ModernBetaGeneratorSettings.MIN_DEPTH_NOISE, ModernBetaGeneratorSettings.MAX_DEPTH_NOISE, this.settings.depthNoiseScaleX, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_DPTH_NS_Z, NbtTags.DEPTH_NOISE_SCALE_Z, ModernBetaGeneratorSettings.MIN_DEPTH_NOISE, ModernBetaGeneratorSettings.MAX_DEPTH_NOISE, this.settings.depthNoiseScaleZ, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_BASE_SIZE, NbtTags.BASE_SIZE, ModernBetaGeneratorSettings.MIN_BASE_SIZE, ModernBetaGeneratorSettings.MAX_BASE_SIZE, this.settings.baseSize, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_COORD_SCL, NbtTags.COORDINATE_SCALE, ModernBetaGeneratorSettings.MIN_COORD_SCALE, ModernBetaGeneratorSettings.MAX_COORD_SCALE, this.settings.coordinateScale, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_HEIGH_SCL, NbtTags.HEIGHT_SCALE, ModernBetaGeneratorSettings.MIN_HEIGHT_SCALE, ModernBetaGeneratorSettings.MAX_HEIGHT_SCALE, this.settings.heightScale, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_STRETCH_Y, NbtTags.STRETCH_Y, ModernBetaGeneratorSettings.MIN_STRETCH_Y, ModernBetaGeneratorSettings.MAX_STRETCH_Y, this.settings.stretchY, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_UPPER_LIM, NbtTags.UPPER_LIMIT_SCALE, ModernBetaGeneratorSettings.MIN_LIMIT_SCALE, ModernBetaGeneratorSettings.MAX_LIMIT_SCALE, this.settings.upperLimitScale, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_LOWER_LIM, NbtTags.LOWER_LIMIT_SCALE, ModernBetaGeneratorSettings.MIN_LIMIT_SCALE, ModernBetaGeneratorSettings.MAX_LIMIT_SCALE, this.settings.lowerLimitScale, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_HEIGH_LIM, NbtTags.HEIGHT, ModernBetaGeneratorSettings.getMinHeight(), ModernBetaGeneratorSettings.getMaxHeight(), this.settings.height, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_HEIGH_FLR, NbtTags.FLOOR, ModernBetaGeneratorSettings.getMinFloor(), ModernBetaGeneratorSettings.getMaxFloor(), this.settings.floor, this),
            null,
            
            this.createGuiLabel(GuiIdentifiers.PG4_L_BETA_LABL, "page4", "beta"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG4_S_TEMP_SCL, NbtTags.TEMP_NOISE_SCALE, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE, this.settings.tempNoiseScale, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_RAIN_SCL, NbtTags.RAIN_NOISE_SCALE, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE, this.settings.rainNoiseScale, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_DETL_SCL, NbtTags.DETAIL_NOISE_SCALE, ModernBetaGeneratorSettings.MIN_BIOME_SCALE, ModernBetaGeneratorSettings.MAX_BIOME_SCALE, this.settings.detailNoiseScale, this),
            this.createGuiButton(GuiIdentifiers.PG4_B_TERR_FIX, NbtTags.USE_TERRAIN_COORD_FIX, this.settings.useTerrainCoordFix),
            
            this.createGuiLabel(GuiIdentifiers.PG4_L_RELE_LABL, "page4", "release"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG4_S_B_DPTH_WT, NbtTags.BIOME_DEPTH_WEIGHT, ModernBetaGeneratorSettings.MIN_BIOME_WEIGHT, ModernBetaGeneratorSettings.MAX_BIOME_WEIGHT, this.settings.biomeDepthWeight, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_B_DPTH_OF, NbtTags.BIOME_DEPTH_OFFSET, ModernBetaGeneratorSettings.MIN_BIOME_OFFSET, ModernBetaGeneratorSettings.MAX_BIOME_OFFSET, this.settings.biomeDepthOffset, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_B_SCLE_WT, NbtTags.BIOME_SCALE_WEIGHT, ModernBetaGeneratorSettings.MIN_BIOME_WEIGHT, ModernBetaGeneratorSettings.MAX_BIOME_WEIGHT, this.settings.biomeScaleWeight, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_B_SCLE_OF, NbtTags.BIOME_SCALE_OFFSET, ModernBetaGeneratorSettings.MIN_BIOME_OFFSET, ModernBetaGeneratorSettings.MAX_BIOME_OFFSET, this.settings.biomeScaleOffset, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_R_DPTH_WT, NbtTags.RIVER_DEPTH_WEIGHT, ModernBetaGeneratorSettings.MIN_RIVER_WEIGHT, ModernBetaGeneratorSettings.MAX_RIVER_WEIGHT, this.settings.riverDepthWeight, this),
            this.createGuiButton(GuiIdentifiers.PG4_B_USE_BDS, NbtTags.USE_BIOME_DEPTH_SCALE, this.settings.useBiomeDepthScale),
            this.createGuiButton(GuiIdentifiers.PG4_B_USE_AMP, NbtTags.USE_AMPLIFIED, this.settings.useAmplified),
            null,
            
            this.createGuiLabel(GuiIdentifiers.PG4_L_END_LABL, "page4", "end"),
            null,
            this.createGuiSlider(GuiIdentifiers.PG4_S_END_WT, NbtTags.END_ISLAND_WEIGHT, ModernBetaGeneratorSettings.MIN_END_WEIGHT, ModernBetaGeneratorSettings.MAX_END_WEIGHT, this.settings.endIslandWeight, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_END_OF, NbtTags.END_ISLAND_OFFSET, ModernBetaGeneratorSettings.MIN_END_OFFSET, ModernBetaGeneratorSettings.MAX_END_OFFSET, this.settings.endIslandOffset, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_END_OUT_DT, NbtTags.END_OUTER_ISLAND_DISTANCE, ModernBetaGeneratorSettings.MIN_END_DIST, ModernBetaGeneratorSettings.MAX_END_DIST, this.settings.endOuterIslandDistance, this),
            this.createGuiSlider(GuiIdentifiers.PG4_S_END_OUT_OF, NbtTags.END_OUTER_ISLAND_OFFSET, ModernBetaGeneratorSettings.MIN_END_OFFSET, ModernBetaGeneratorSettings.MAX_END_OFFSET, this.settings.endOuterIslandOffset, this),
            this.createGuiButton(GuiIdentifiers.PG4_B_USE_END_OUT, NbtTags.USE_END_OUTER_ISLANDS, this.settings.useEndOuterIslands),
            null
        };
        
        GuiPageButtonList.GuiListEntry[] pageNoise1 = {
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_MAIN_NS_X, true, PREFIX + NbtTags.MAIN_NOISE_SCALE_X),
            this.createGuiField(GuiIdentifiers.PG5_F_MAIN_NS_X, String.format("%5.3f", this.settings.mainNoiseScaleX), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_MAIN_NS_Y, true, PREFIX + NbtTags.MAIN_NOISE_SCALE_Y),
            this.createGuiField(GuiIdentifiers.PG5_F_MAIN_NS_Y, String.format("%5.3f", this.settings.mainNoiseScaleY), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_MAIN_NS_Z, true, PREFIX + NbtTags.MAIN_NOISE_SCALE_Z),
            this.createGuiField(GuiIdentifiers.PG5_F_MAIN_NS_Z, String.format("%5.3f", this.settings.mainNoiseScaleZ), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_SCLE_NS_X, true, PREFIX + NbtTags.SCALE_NOISE_SCALE_X),
            this.createGuiField(GuiIdentifiers.PG5_F_SCLE_NS_X, String.format("%5.3f", this.settings.scaleNoiseScaleX), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_SCLE_NS_Z, true, PREFIX + NbtTags.SCALE_NOISE_SCALE_Z),
            this.createGuiField(GuiIdentifiers.PG5_F_SCLE_NS_Z, String.format("%5.3f", this.settings.scaleNoiseScaleZ), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_DPTH_NS_X, true, PREFIX + NbtTags.DEPTH_NOISE_SCALE_X),
            this.createGuiField(GuiIdentifiers.PG5_F_DPTH_NS_X, String.format("%5.3f", this.settings.depthNoiseScaleX), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_DPTH_NS_Z, true, PREFIX + NbtTags.DEPTH_NOISE_SCALE_Z),
            this.createGuiField(GuiIdentifiers.PG5_F_DPTH_NS_Z, String.format("%5.3f", this.settings.depthNoiseScaleZ), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_BASE_SIZE, true, PREFIX + NbtTags.BASE_SIZE),
            this.createGuiField(GuiIdentifiers.PG5_F_BASE_SIZE, String.format("%2.3f", this.settings.baseSize), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_COORD_SCL, true, PREFIX + NbtTags.COORDINATE_SCALE),
            this.createGuiField(GuiIdentifiers.PG5_F_COORD_SCL, String.format("%5.3f", this.settings.coordinateScale), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_HEIGH_SCL, true, PREFIX + NbtTags.HEIGHT_SCALE),
            this.createGuiField(GuiIdentifiers.PG5_F_HEIGH_SCL, String.format("%5.3f", this.settings.heightScale), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_STRETCH_Y, true, PREFIX + NbtTags.STRETCH_Y),
            this.createGuiField(GuiIdentifiers.PG5_F_STRETCH_Y, String.format("%2.3f", this.settings.stretchY), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_UPPER_LIM, true, PREFIX + NbtTags.UPPER_LIMIT_SCALE),
            this.createGuiField(GuiIdentifiers.PG5_F_UPPER_LIM, String.format("%5.3f", this.settings.upperLimitScale), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_LOWER_LIM, true, PREFIX + NbtTags.LOWER_LIMIT_SCALE),
            this.createGuiField(GuiIdentifiers.PG5_F_LOWER_LIM, String.format("%5.3f", this.settings.lowerLimitScale), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_HEIGH_LIM, true, PREFIX + NbtTags.HEIGHT),
            this.createGuiField(GuiIdentifiers.PG5_F_HEIGH_LIM, String.format("%d", this.settings.height), this.intFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_HEIGH_FLR, true, PREFIX + NbtTags.FLOOR),
            this.createGuiField(GuiIdentifiers.PG5_F_HEIGH_FLR, String.format("%d",  this.settings.floor), this.intFilter),

            this.createGuiLabel(GuiIdentifiers.PG4_L_BETA_LABL, "page5", "beta"),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_TEMP_SCL, true, PREFIX + NbtTags.TEMP_NOISE_SCALE),
            this.createGuiField(GuiIdentifiers.PG5_F_TEMP_SCL, String.format("%2.3f", this.settings.tempNoiseScale), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_RAIN_SCL, true, PREFIX + NbtTags.RAIN_NOISE_SCALE),
            this.createGuiField(GuiIdentifiers.PG5_F_RAIN_SCL, String.format("%2.3f", this.settings.rainNoiseScale), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_DETL_SCL, true, PREFIX + NbtTags.DETAIL_NOISE_SCALE),
            this.createGuiField(GuiIdentifiers.PG5_F_DETL_SCL, String.format("%2.3f", this.settings.detailNoiseScale), this.floatFilter),

            this.createGuiLabel(GuiIdentifiers.PG4_L_RELE_LABL, "page5", "release"),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_B_DPTH_WT, true, PREFIX + NbtTags.BIOME_DEPTH_WEIGHT),
            this.createGuiField(GuiIdentifiers.PG5_F_B_DPTH_WT, String.format("%2.3f", this.settings.biomeDepthWeight), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_B_DPTH_OF, true, PREFIX + NbtTags.BIOME_DEPTH_OFFSET),
            this.createGuiField(GuiIdentifiers.PG5_F_B_DPTH_OF, String.format("%2.3f", this.settings.biomeDepthOffset), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_B_SCLE_WT, true, PREFIX + NbtTags.BIOME_SCALE_WEIGHT),
            this.createGuiField(GuiIdentifiers.PG5_F_B_SCLE_WT, String.format("%2.3f", this.settings.biomeScaleWeight), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_B_SCLE_OF, true, PREFIX + NbtTags.BIOME_SCALE_OFFSET),
            this.createGuiField(GuiIdentifiers.PG5_F_B_SCLE_OF, String.format("%2.3f", this.settings.biomeScaleOffset), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_R_DPTH_WT, true, PREFIX + NbtTags.RIVER_DEPTH_WEIGHT),
            this.createGuiField(GuiIdentifiers.PG5_F_R_DPTH_WT, String.format("%2.3f", this.settings.riverDepthWeight), this.floatFilter),
            
            this.createGuiLabel(GuiIdentifiers.PG4_L_END_LABL, "page5", "end"),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_END_WT, true, PREFIX + NbtTags.END_ISLAND_WEIGHT),
            this.createGuiField(GuiIdentifiers.PG5_F_END_WT, String.format("%2.3f", this.settings.endIslandWeight), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_END_OF, true, PREFIX + NbtTags.END_ISLAND_OFFSET),
            this.createGuiField(GuiIdentifiers.PG5_F_END_OF, String.format("%2.3f", this.settings.endIslandOffset), this.floatFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_END_OUT_DT, true, PREFIX + NbtTags.END_OUTER_ISLAND_DISTANCE),
            this.createGuiField(GuiIdentifiers.PG5_F_END_OUT_DT, String.format("%d", this.settings.endOuterIslandDistance), this.intFilter),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG5_L_END_OUT_OF, true, PREFIX + NbtTags.END_OUTER_ISLAND_OFFSET),
            this.createGuiField(GuiIdentifiers.PG5_F_END_OUT_OF, String.format("%2.3f", this.settings.endOuterIslandOffset), this.floatFilter)
        };
        
        GuiPageButtonList.GuiListEntry[] pageClimate = {
            this.createGuiButton(GuiIdentifiers.PG6_B_CLIMATE_FEAT, NbtTags.USE_CLIMATE_FEATURES, this.settings.useClimateFeatures),
            this.createGuiSlider(GuiIdentifiers.PG6_S_SNOW_OFFSET, NbtTags.SNOW_LINE_OFFSET, ModernBetaGeneratorSettings.getMinSeaLevel(), ModernBetaGeneratorSettings.getMaxSeaLevel(), this.settings.snowLineOffset, this),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_DSRT_LABL, false, PREFIX + NbtTags.DESERT_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_DSRT_LAND, NbtTags.DESERT_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_DSRT_OCEAN, NbtTags.DESERT_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_DSRT_BEACH, NbtTags.DESERT_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_FRST_LABL, false, PREFIX + NbtTags.FOREST_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_FRST_LAND, NbtTags.FOREST_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_FRST_OCEAN, NbtTags.FOREST_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_FRST_BEACH, NbtTags.FOREST_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_ICED_LABL, false, PREFIX + NbtTags.ICE_DESERT_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_ICED_LAND, NbtTags.ICE_DESERT_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_ICED_OCEAN, NbtTags.ICE_DESERT_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_ICED_BEACH, NbtTags.ICE_DESERT_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_PLNS_LABL, false, PREFIX + NbtTags.PLAINS_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_PLNS_LAND, NbtTags.PLAINS_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_PLNS_OCEAN, NbtTags.PLAINS_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_PLNS_BEACH, NbtTags.PLAINS_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_RAIN_LABL, false, PREFIX + NbtTags.RAINFOREST_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_RAIN_LAND, NbtTags.RAINFOREST_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_RAIN_OCEAN, NbtTags.RAINFOREST_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_RAIN_BEACH, NbtTags.RAINFOREST_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_SAVA_LABL, false, PREFIX + NbtTags.SAVANNA_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SAVA_LAND, NbtTags.SAVANNA_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SAVA_OCEAN, NbtTags.SAVANNA_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SAVA_BEACH, NbtTags.SAVANNA_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_SHRB_LABL, false, PREFIX + NbtTags.SHRUBLAND_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SHRB_LAND, NbtTags.SHRUBLAND_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SHRB_OCEAN, NbtTags.SHRUBLAND_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SHRB_BEACH, NbtTags.SHRUBLAND_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_SEAS_LABL, false, PREFIX + NbtTags.SEASONAL_FOREST_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SEAS_LAND, NbtTags.SEASONAL_FOREST_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SEAS_OCEAN, NbtTags.SEASONAL_FOREST_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SEAS_BEACH, NbtTags.SEASONAL_FOREST_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_SWMP_LABL, false, PREFIX + NbtTags.SWAMPLAND_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SWMP_LAND, NbtTags.SWAMPLAND_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SWMP_OCEAN, NbtTags.SWAMPLAND_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_SWMP_BEACH, NbtTags.SWAMPLAND_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_TAIG_LABL, false, PREFIX + NbtTags.TAIGA_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_TAIG_LAND, NbtTags.TAIGA_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_TAIG_OCEAN, NbtTags.TAIGA_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_TAIG_BEACH, NbtTags.TAIGA_BIOME_BEACH, true),
            
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_TUND_LABL, false, PREFIX + NbtTags.TUNDRA_BIOMES),
            null,
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_LAND_LABL, true, PREFIX + NbtTags.BASE_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_TUND_LAND, NbtTags.TUNDRA_BIOME_BASE, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_OCEAN_LABL, true, PREFIX + NbtTags.OCEAN_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_TUND_OCEAN, NbtTags.TUNDRA_BIOME_OCEAN, true),
            this.createGuiLabelNoPrefix(GuiIdentifiers.PG6_BEACH_LABL, true, PREFIX + NbtTags.BEACH_BIOME),
            this.createGuiButton(GuiIdentifiers.PG6_TUND_BEACH, NbtTags.TUNDRA_BIOME_BEACH, true)
        };
        
        GuiPageButtonList.GuiListEntry[] pageCustom = this.createCustomPropertyPage();
        
        if (!ModCompat.NETHER_MANAGER.isCompatible()) {
            pageBasic = Arrays.copyOf(pageBasic, pageBasic.length + 2);
            pageBasic[pageBasic.length - 2] = this.createGuiLabel(GuiIdentifiers.PG0_L_NETHER_BOP, TextFormatting.GRAY, "page0", "netherIncompatible");
            pageBasic[pageBasic.length - 1] = null;
        }
        
        if (ModCompat.isCompatLoaded(CompatDynamicTrees.MOD_ID) && CompatDynamicTrees.isEnabled()) {
            pageBiome = Arrays.copyOf(pageBiome, pageBiome.length + 2);
            pageBiome[pageBiome.length - 2] = this.createGuiLabel(GuiIdentifiers.PG0_L_TREES, TextFormatting.GRAY, "page2", "treesIncompatible");
            pageBiome[pageBiome.length - 1] = null;
        }
        
        GuiPageButtonList.GuiListEntry[][] pageArray = new GuiPageButtonList.GuiListEntry[][] {
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
            pageArray = Arrays.copyOf(pageArray, pageArray.length - 1);
        }
        
        this.pageArray = pageArray;
        this.pageList = new GuiPageButtonList(
            this.mc,
            this.width,
            this.height,
            PAGELIST_PADDING_TOP,
            this.height - PAGELIST_PADDING_BOTTOM,
            BUTTON_SLOT_HEIGHT,
            this,
            pageArray
        );
        
        this.pageList.width += PAGELIST_SCROLLBAR_PADDING;
        
        // Set text for primary options
        this.setTextButton(GuiIdentifiers.PG0_B_CHUNK, getFormattedRegistryName(this.settings.chunkSource, NbtTags.CHUNK_SOURCE, DEFAULT_NAME_TRUNCATE_LEN, true));
        this.setTextButton(GuiIdentifiers.PG0_B_BIOME, getFormattedRegistryName(this.settings.biomeSource, NbtTags.BIOME_SOURCE, DEFAULT_NAME_TRUNCATE_LEN, true));
        this.setTextButton(GuiIdentifiers.PG0_B_SURFACE, getFormattedRegistryName(this.settings.surfaceBuilder, NbtTags.SURFACE_BUILDER, DEFAULT_NAME_TRUNCATE_LEN, true));
        this.setTextButton(GuiIdentifiers.PG0_B_CARVER, getFormattedRegistryName(this.settings.caveCarver, NbtTags.CAVE_CARVER, DEFAULT_NAME_TRUNCATE_LEN, true));
        this.setTextButton(GuiIdentifiers.PG0_B_SPAWN, getFormattedRegistryName(this.settings.worldSpawner, NbtTags.WORLD_SPAWNER, DEFAULT_NAME_TRUNCATE_LEN, true));
        
        // Set text for default block options
        this.setTextButton(GuiIdentifiers.PG0_B_BLOCK, getFormattedBlockName(this.settings.defaultBlock, NbtTags.DEFAULT_BLOCK, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG0_B_FLUID, getFormattedFluidName(this.settings.defaultFluid, NbtTags.DEFAULT_FLUID, DEFAULT_NAME_TRUNCATE_LEN));
        
        // Set biome text for Single Biome button
        this.setTextButton(GuiIdentifiers.PG0_B_FIXED, getFormattedBiomeName(this.settings.singleBiome, NbtTags.SINGLE_BIOME, DEFAULT_NAME_TRUNCATE_LEN));
        
        // Set biome text for Beta Biome buttons
        this.setTextButton(GuiIdentifiers.PG6_DSRT_LAND, getFormattedBiomeName(this.settings.desertBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_DSRT_OCEAN, getFormattedBiomeName(this.settings.desertBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_DSRT_BEACH, getFormattedBiomeName(this.settings.desertBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_FRST_LAND, getFormattedBiomeName(this.settings.forestBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_FRST_OCEAN, getFormattedBiomeName(this.settings.forestBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_FRST_BEACH, getFormattedBiomeName(this.settings.forestBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_ICED_LAND, getFormattedBiomeName(this.settings.iceDesertBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_ICED_OCEAN, getFormattedBiomeName(this.settings.iceDesertBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_ICED_BEACH, getFormattedBiomeName(this.settings.iceDesertBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_PLNS_LAND, getFormattedBiomeName(this.settings.plainsBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_PLNS_OCEAN, getFormattedBiomeName(this.settings.plainsBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_PLNS_BEACH, getFormattedBiomeName(this.settings.plainsBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_RAIN_LAND, getFormattedBiomeName(this.settings.rainforestBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_RAIN_OCEAN, getFormattedBiomeName(this.settings.rainforestBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_RAIN_BEACH, getFormattedBiomeName(this.settings.rainforestBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_SAVA_LAND, getFormattedBiomeName(this.settings.savannaBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_SAVA_OCEAN, getFormattedBiomeName(this.settings.savannaBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_SAVA_BEACH, getFormattedBiomeName(this.settings.savannaBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_SHRB_LAND, getFormattedBiomeName(this.settings.shrublandBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_SHRB_OCEAN, getFormattedBiomeName(this.settings.shrublandBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_SHRB_BEACH, getFormattedBiomeName(this.settings.shrublandBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_SEAS_LAND, getFormattedBiomeName(this.settings.seasonalForestBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_SEAS_OCEAN, getFormattedBiomeName(this.settings.seasonalForestBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_SEAS_BEACH, getFormattedBiomeName(this.settings.seasonalForestBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_SWMP_LAND, getFormattedBiomeName(this.settings.swamplandBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_SWMP_OCEAN, getFormattedBiomeName(this.settings.swamplandBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_SWMP_BEACH, getFormattedBiomeName(this.settings.swamplandBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_TAIG_LAND, getFormattedBiomeName(this.settings.taigaBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_TAIG_OCEAN, getFormattedBiomeName(this.settings.taigaBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_TAIG_BEACH, getFormattedBiomeName(this.settings.taigaBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setTextButton(GuiIdentifiers.PG6_TUND_LAND, getFormattedBiomeName(this.settings.tundraBiomeBase, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_TUND_OCEAN, getFormattedBiomeName(this.settings.tundraBiomeOcean, DEFAULT_NAME_TRUNCATE_LEN));
        this.setTextButton(GuiIdentifiers.PG6_TUND_BEACH, getFormattedBiomeName(this.settings.tundraBiomeBeach, DEFAULT_NAME_TRUNCATE_LEN));
        
        this.setPropertyText();
    }

    private void createPageTabs() {
        this.tabStartX = this.width / 2 - (TAB_BUTTON_WIDTH * this.pageList.getPageCount() / 2) - (TAB_SPACE *  this.pageList.getPageCount() / 2);
        
        int id = GuiIdentifiers.FUNC_INITIAL_TAB;
        int x = this.tabStartX;
        int y = this.pageList.top + this.pageList.headerPadding - TAB_BUTTON_HEIGHT;
        int width = TAB_BUTTON_WIDTH;
        int height = TAB_BUTTON_HEIGHT;
        
        this.pageTabMap = new LinkedHashMap<>();
        for (int i = 0; i < this.pageList.getPageCount(); ++i) {
            String text = I18n.format(this.pageNames[i]);
            boolean selected = this.pageList.getPage() == i;
            
            GuiButtonTab guiButton = new GuiButtonTab(id + i, x, y, width, height, text, selected);
            
            this.pageTabMap.put(
                GuiIdentifiers.FUNC_INITIAL_TAB + i,
                this.<GuiButtonTab>addButton(guiButton)
            );
            
            x += TAB_BUTTON_WIDTH + TAB_SPACE;
        }
        
        this.tabEndX = x - TAB_SPACE;
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        
        int curPage = 0;
        int curScroll = 0;
        
        if (this.pageList != null) {
            curPage = this.pageList.getPage();
            curScroll = this.pageList.getAmountScrolled();
        }
        
        this.buttonList.clear();

        int centerX = this.width / 2;
        
        int buttonY = this.height - 27;
        int defaultsX = centerX - BUTTON_WIDTH * 2 - BUTTON_WIDTH / 2 - 6;
        int randomizeX = centerX - BUTTON_WIDTH - BUTTON_WIDTH / 2 - 3;
        int previewX = centerX - BUTTON_WIDTH / 2;
        int presetsX = centerX + BUTTON_WIDTH / 2 + 3;
        int doneX = centerX + BUTTON_WIDTH / 2 + BUTTON_WIDTH + 6;

        this.buttonDefaults = this.addButton(new GuiButton(GuiIdentifiers.FUNC_DFLT, defaultsX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "defaults")));
        this.buttonRandomize = this.addButton(new GuiButton(GuiIdentifiers.FUNC_RAND, randomizeX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "randomize")));
        this.buttonPreview = this.addButton(new GuiButton(GuiIdentifiers.FUNC_PRVW, previewX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "preview")));
        this.buttonPresets = this.addButton(new GuiButton(GuiIdentifiers.FUNC_PRST, presetsX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "presets")));
        this.buttonDone = this.addButton(new GuiButton(GuiIdentifiers.FUNC_DONE, doneX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format(PREFIX + "confirm")));
        
        this.createPagedList();
        this.pageList.setPage(curPage);
        this.pageList.scrollBy(curScroll);
        this.createPageTabs();
        
        int tabY = this.pageList.top + this.pageList.headerPadding - TAB_BUTTON_HEIGHT;
        int navY = tabY + (TAB_BUTTON_HEIGHT - GuiButtonNav.BUTTON_SIZE) / 2;
        int navXL = this.tabStartX - GuiButtonNav.getButtonWidth(this.mc, KeyBindings.LEFT_NAV_KEY.getDisplayName()) - 3;
        int navXR = this.tabEndX + 3;

        this.buttonNavL = this.addButton(new GuiButtonNav(this.mc, GuiIdentifiers.FUNC_LNAV, navXL, navY, KeyBindings.LEFT_NAV_KEY.getDisplayName()));
        this.buttonNavR = this.addButton(new GuiButtonNav(this.mc, GuiIdentifiers.FUNC_RNAV, navXR, navY, KeyBindings.RIGHT_NAV_KEY.getDisplayName()));
        
        // Set default enabled for certain options
        this.initButtonValidity();
        this.updateSettingValidity();
        this.setSettingsModified(this.isSettingsModified());
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.pageList.handleMouseInput();
    }
    
    @Override
    public String getText(int entry, String entryString, float entryValue) {
        // Do not append colon for custom property entries
        if (this.propertyMap.containsKey(entry) || this.guiPropertyMap.containsKey(entry)) {
            return this.getFormattedValue(entry, entryValue);
        }

        return entryString + ": " + this.getFormattedValue(entry, entryValue);
    }

    @Override
    public void setEntryValue(int entry, String entryString) {
        if (this.propertyMap.containsKey(entry)) {
            ResourceLocation registryKey = this.propertyMap.get(entry);
            
            Property<?> property = this.settings.customProperties.get(registryKey);
            property.visitEntryValue(new SetEntryValuePropertyVisitor(), entry, entryString, registryKey);
            
        } else if (this.guiPropertyMap.containsKey(entry)) {
            ResourceLocation registryKey = this.guiPropertyMap.get(entry);
            
            GuiProperty<?> property = ModernBetaClientRegistries.GUI_PROPERTY.get(registryKey);
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
                    this.settings.height = (int)MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.getMinHeight(), ModernBetaGeneratorSettings.getMaxHeight());
                    newEntryValue = this.settings.height;
                    break;
                case GuiIdentifiers.PG5_F_HEIGH_FLR:
                    this.settings.floor = (int)MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.getMinFloor(), ModernBetaGeneratorSettings.getMaxFloor());
                    newEntryValue = this.settings.floor;
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
                case GuiIdentifiers.PG5_F_R_DPTH_WT:
                    this.settings.riverDepthWeight = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_RIVER_WEIGHT, ModernBetaGeneratorSettings.MAX_RIVER_WEIGHT);
                    newEntryValue = this.settings.riverDepthWeight;
                    break;
                case GuiIdentifiers.PG5_F_END_WT:
                    this.settings.endIslandWeight = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_END_WEIGHT, ModernBetaGeneratorSettings.MAX_END_WEIGHT);
                    newEntryValue = this.settings.endIslandWeight;
                    break;
                case GuiIdentifiers.PG5_F_END_OF:
                    this.settings.endIslandOffset = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_END_OFFSET, ModernBetaGeneratorSettings.MAX_END_OFFSET);
                    newEntryValue = this.settings.endIslandOffset;
                    break;
                case GuiIdentifiers.PG5_F_END_OUT_DT:
                    this.settings.endOuterIslandDistance = (int)MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_END_DIST, ModernBetaGeneratorSettings.MAX_END_DIST);
                    newEntryValue = this.settings.endOuterIslandDistance;
                    break;
                case GuiIdentifiers.PG5_F_END_OUT_OF:
                    this.settings.endOuterIslandOffset = MathHelper.clamp(entryValue, ModernBetaGeneratorSettings.MIN_END_OFFSET, ModernBetaGeneratorSettings.MAX_END_OFFSET);
                    newEntryValue = this.settings.endOuterIslandOffset;
                    break;
            }

            if (newEntryValue != entryValue && entryValue != 0.0f) {
                ((GuiTextField)this.pageList.getComponent(entry)).setText(this.getFormattedValue(entry, newEntryValue));
            }
            
            if (entry >= GuiIdentifiers.PG5_F_MAIN_NS_X && entry <= GuiIdentifiers.PG5_FIELD_END) {
                Gui gui = this.pageList.getComponent(GuiIdentifiers.offsetBackward(entry));
                if (gui != null) {
                    ((GuiSlider)gui).setSliderValue(newEntryValue, false);
                }
            }
        }
        
        this.setSettingsModified(this.isSettingsModified());
    }

    @Override
    public void setEntryValue(int entry, boolean entryValue) {
        if (this.propertyMap.containsKey(entry)) {
            ResourceLocation registryKey = this.propertyMap.get(entry);
            
            Property<?> property = this.settings.customProperties.get(registryKey);
            property.visitEntryValue(new SetEntryValuePropertyVisitor(), entry, entryValue, registryKey);
            
        } else if (this.guiPropertyMap.containsKey(entry)) {
            ResourceLocation registryKey = this.guiPropertyMap.get(entry);
            
            GuiProperty<?> property = ModernBetaClientRegistries.GUI_PROPERTY.get(registryKey);
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
                case GuiIdentifiers.PG0_B_SPAWN:
                    this.openRegistryScreen((str, factory) -> factory.worldSpawner = str, settings.worldSpawner, NbtTags.WORLD_SPAWNER,  ModernBetaRegistries.WORLD_SPAWNER.getKeys());
                    break;
                case GuiIdentifiers.PG0_B_FIXED:
                    this.openBiomeScreen((str, factory) -> factory.singleBiome = str, settings.singleBiome);
                    break;
                case GuiIdentifiers.PG0_B_BLOCK:
                    this.openBlockScreen((str, factory) -> factory.defaultBlock = str, settings.defaultBlock, NbtTags.DEFAULT_BLOCK, key -> ModernBetaRegistries.DEFAULT_BLOCK.contains(key));
                    break;
                case GuiIdentifiers.PG0_B_FLUID:
                    this.openFluidScreen((str, factory) -> factory.defaultFluid = str, settings.defaultFluid, NbtTags.DEFAULT_FLUID, key -> true);
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
                case GuiIdentifiers.PG0_B_USE_RIVER:
                    this.settings.replaceRiverBiomes = entryValue;
                    break;
                    
                case GuiIdentifiers.PG2_B_USE_GRASS:
                    this.settings.useTallGrass = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_FLOWERS:
                    this.settings.useNewFlowers = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_DOUBLE:
                    this.settings.useDoublePlants = entryValue;
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
                case GuiIdentifiers.PG2_B_USE_SAND_DISKS:
                    this.settings.useSandDisks = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_GRAV_DISKS:
                    this.settings.useGravelDisks = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_CLAY_DISKS:
                    this.settings.useClayDisks = entryValue;
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
                case GuiIdentifiers.PG2_B_USE_DARK_OAK:
                    this.settings.useDarkOakTrees = entryValue;
                    break;
                case GuiIdentifiers.PG2_B_USE_FANCY_OAK:
                    this.settings.useNewFancyOakTrees = entryValue;
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
                case GuiIdentifiers.PG0_B_USE_SANDSTONE:
                    this.settings.useSandstone = entryValue;
                    break;
                case GuiIdentifiers.PG0_B_USE_UNDERWATER_CAVES:
                    this.settings.useUnderwaterCaves = entryValue;
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
                    
                case GuiIdentifiers.PG4_B_TERR_FIX:
                    this.settings.useTerrainCoordFix = entryValue;
                    break;
                case GuiIdentifiers.PG4_B_USE_BDS:
                    this.settings.useBiomeDepthScale = entryValue;
                    break;
                case GuiIdentifiers.PG4_B_USE_END_OUT:
                    this.settings.useEndOuterIslands = entryValue;
                    break;
                case GuiIdentifiers.PG4_B_USE_AMP:
                    this.settings.useAmplified = entryValue;
                    break;
                    
                case GuiIdentifiers.PG6_B_CLIMATE_FEAT:
                    this.settings.useClimateFeatures = entryValue;
                    break;
            }
        }

        this.updateSettingValidity();
        this.setSettingsModified(this.isSettingsModified());
        this.playSound();
    }

    @Override
    public void setEntryValue(int entry, float entryValue) {
        if (this.propertyMap.containsKey(entry)) {
            ResourceLocation registryKey = this.propertyMap.get(entry);
            
            Property<?> property = this.settings.customProperties.get(registryKey);
            property.visitEntryValue(new SetEntryValuePropertyVisitor(), entry, entryValue, registryKey);
            
        } else if (this.guiPropertyMap.containsKey(entry)) {
            ResourceLocation registryKey = this.guiPropertyMap.get(entry);
            
            GuiProperty<?> property = ModernBetaClientRegistries.GUI_PROPERTY.get(registryKey);
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
                case GuiIdentifiers.PG4_S_HEIGH_FLR:
                    this.settings.floor = (int)entryValue;
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
                case GuiIdentifiers.PG4_S_R_DPTH_WT:
                    this.settings.riverDepthWeight = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_END_OF:
                    this.settings.endIslandOffset = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_END_WT:
                    this.settings.endIslandWeight = roundToThreeDec(entryValue);
                    break;
                case GuiIdentifiers.PG4_S_END_OUT_DT:
                    this.settings.endOuterIslandDistance = (int)entryValue;
                    break;
                case GuiIdentifiers.PG4_S_END_OUT_OF:
                    this.settings.endOuterIslandOffset = roundToThreeDec(entryValue);
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
                case GuiIdentifiers.PG0_S_SPAWN:
                    this.settings.worldSpawner = ModernBetaRegistries.WORLD_SPAWNER.getKeys().get((int)entryValue).toString();
                    break;
                case GuiIdentifiers.PG0_S_BLOCK:
                    this.settings.defaultBlock = ModernBetaRegistries.DEFAULT_BLOCK.getKeys().get((int)entryValue).toString();
                    break;
                case GuiIdentifiers.PG0_S_FLUID:
                    this.settings.defaultFluid = ForgeRegistryUtil.getFluidBlockRegistryNames().get((int)entryValue).toString();
                    break;
                
                case GuiIdentifiers.PG0_S_SEA_LEVEL:
                    this.settings.seaLevel = (int)entryValue;
                    break;
                case GuiIdentifiers.PG0_S_CAVE_WIDTH:
                    this.settings.caveWidth = roundToOneDec(entryValue);
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
                case GuiIdentifiers.PG0_S_RAVINE_CHANCE:
                    this.settings.ravineChance = (int)entryValue;
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
                case GuiIdentifiers.PG1_S_LEVEL_CAVE_WIDTH:
                    this.settings.levelCaveWidth = roundToOneDec(entryValue);
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
                case GuiIdentifiers.PG2_S_SNOWY_CHANCE:
                    this.settings.snowyBiomeChance = (int)entryValue;
                    break;
    
                case GuiIdentifiers.PG3_S_ORE_TYPE:
                    this.settings.oreType = OreType.values()[(int)entryValue].id;
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
                    
                case GuiIdentifiers.PG6_S_SNOW_OFFSET:
                    this.settings.snowLineOffset = (int)entryValue;
                    break;
            }
            
            if (entry >= GuiIdentifiers.PG4_S_MAIN_NS_X && entry <= GuiIdentifiers.PG4_SLIDER_END) {
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

        this.updateSettingValidity();
        this.setSettingsModified(this.isSettingsModified());
        this.playSound();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.pageList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, PAGE_TITLE_HEIGHT, GuiColors.RGB_WHITE);
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        // Tooltips
        this.updateHoveredTooltip(mouseX, mouseY);
        this.drawHoveredTooltip(mouseX, mouseY);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        
        Gui guiComponent = this.pageList.getFocusedControl();
        if (guiComponent instanceof GuiTextField && ((GuiTextField)guiComponent).isFocused()) {
            ((GuiTextField)guiComponent).updateCursorCounter();
        }
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    public ModernBetaGeneratorSettings.Factory getDefaultSettings() {
        return ModernBetaGeneratorSettings.Factory.jsonToFactory(this.defaultSettings.toString());
    }

    public String getSettingsString() {
        return this.settings.toString().replace("\n", "");
    }
    
    public String getPreviousSettingsString() {
        return this.parent.chunkProviderSettingsJson.isEmpty() ?
            this.defaultSettings.toString() :
            this.parent.chunkProviderSettingsJson;
    }

    public void loadValues(String string) {
        if (string != null && !string.isEmpty()) {
            this.settings = ModernBetaGeneratorSettings.Factory.jsonToFactory(string);
        } else {
            this.settings = new ModernBetaGeneratorSettings.Factory();
        }
    }
    
    public boolean isSettingsModified() {
        return !this.settings.equals(this.defaultSettings);
    }

    public void setSettingsModified(boolean settingsModified) {
        this.settingsModified = settingsModified;
        this.buttonDefaults.enabled = settingsModified && this.isFocused;
    }
    
    public void setPreviewSettings(PreviewSettings previewSettings) {
        this.previewSettings = previewSettings;
    }
    
    public void setWorldSeed(String seed) {
        this.parent.worldSeed = seed;
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        String title;
    	
        if (!guiButton.enabled) {
            return;
        }
        
        switch (guiButton.id) {
            case GuiIdentifiers.FUNC_DONE:
                if (!ModernBetaConfig.guiOptions.displaySettingsConfirmation) {
                    this.parent.chunkProviderSettingsJson = this.settings.toString();
                    this.exit();
                } else {
                    String generatorOptions = this.parent.chunkProviderSettingsJson.trim();
                    boolean isEqualToPrev = generatorOptions.equals(this.settings.toString());
                    boolean isEqualToDefault = generatorOptions.isEmpty() && this.defaultSettings.equals(this.settings);
                    
                    if (isEqualToPrev || isEqualToDefault) {
                        ModernBeta.log(Level.DEBUG, "No changes were made..");
                        this.exit();
                    } else {
                        Consumer<GuiModalChangelist> onConfirmSettings = modal -> {
                            this.parent.chunkProviderSettingsJson = this.settings.toString();
                            this.exit();
                        };
                        Consumer<GuiModalChangelist> onDiscardSettings = modal -> this.exit();
                        
                        this.isFocused = false;
                        this.mc.displayGuiScreen(new GuiModalChangelist(this, onConfirmSettings, modal -> this.isFocused = true, onDiscardSettings));
                    }
                }
               
                break;
            case GuiIdentifiers.FUNC_RAND:
                this.clickedRandom = true;
                this.updateButtonValidity();
                
                this.executor.queueRunnable(() -> {
                    GuiListEntry[] entries = this.pageArray[this.pageList.getPage()];
                    for (int i = 0; i < entries.length; ++i) {
                        GuiListEntry entry = entries[i];
                        
                        if (entry != null) {
                            this.randomizeGuiComponent(entry.getId());
                        }
                    }
                    
                    this.clickedRandom = false;
                    this.setSettingsModified(this.isSettingsModified());
                    this.updateButtonValidity();
                });
                
                break;
            case GuiIdentifiers.FUNC_DFLT:
                if (this.settingsModified) {
                    Consumer<GuiModalConfirm> onConfirm = modal -> {
                        this.restoreDefaults();
                        this.isFocused = true;
                        this.mc.displayGuiScreen(new GuiScreenCustomizeWorld(this.parent, this.settings.toString()));
                    };

                    title = I18n.format(PREFIX + "confirm.title");
                    String text = I18n.format(PREFIX + "confirm.info");

                    this.isFocused = false;
                    this.mc.displayGuiScreen(new GuiModalConfirm(this, title, 200, 100, onConfirm, modal -> this.isFocused = true, text, GuiColors.RGB_LIGHT_RED));
                }
                break;
            case GuiIdentifiers.FUNC_PRST:
                this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this));
                break;
            case GuiIdentifiers.FUNC_PRVW:
                this.mc.displayGuiScreen(new GuiScreenCustomizePreview(this, this.parent.worldSeed, this.getBuiltSettings(), this.previewSettings));
                break;
            case GuiIdentifiers.FUNC_LNAV:
                this.modifyPageValue(-1);
                break;
            case GuiIdentifiers.FUNC_RNAV:
                this.modifyPageValue(1);
                break;
        }
        
        if (this.pageTabMap.containsKey(guiButton.id)) {
            this.pageList.setPage(guiButton.id - GuiIdentifiers.FUNC_INITIAL_TAB);
            this.updateButtonValidity();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        
        boolean usedSpecialKey = false;
        
        switch (keyCode) {
            case Keyboard.KEY_DOWN:
                this.modifyFocusValue(-1.0f);
                usedSpecialKey = true;
                break;
            case Keyboard.KEY_UP:
                this.modifyFocusValue(1.0f);
                usedSpecialKey = true;
                break;
        }
        
        if (this.displayNavButtons && KeyBindings.LEFT_NAV_KEY.isActiveAndMatches(keyCode)) {
            usedSpecialKey = this.modifyPageValue(System.currentTimeMillis() - this.lastNavPressed > 50L ? -1 : 0);
            this.lastNavPressed = System.currentTimeMillis();
        } else if (this.displayNavButtons && KeyBindings.RIGHT_NAV_KEY.isActiveAndMatches(keyCode)) {
            usedSpecialKey = this.modifyPageValue(System.currentTimeMillis() - this.lastNavPressed > 50L ? 1 : 0);
            this.lastNavPressed = System.currentTimeMillis();
        }
        
        if (!usedSpecialKey) {
            this.pageList.onKeyPressed(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.pageList.mouseClicked(mouseX, mouseY, mouseButton);
        this.clicked = true;
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.pageList.mouseReleased(mouseX, mouseY, mouseButton);
        
        this.clicked = false;
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }
    
    protected void setPropertyText() {
        for (Entry<Integer, ResourceLocation> entry : this.propertyMap.entrySet()) {
            ResourceLocation registryKey = entry.getValue();
            Property<?> property = this.settings.customProperties.get(registryKey);
            String formattedName = property.visitNameFormatter(new NameFormatterPropertyVisitor(), registryKey);
            
            if (formattedName != null && !formattedName.isEmpty()) {
                this.setTextButton(entry.getKey(), formattedName);
            }
        }
        
        for (Entry<Integer, ResourceLocation> entry : this.guiPropertyMap.entrySet()) {
            ResourceLocation registryKey = entry.getValue();
            GuiProperty<?> property = ModernBetaClientRegistries.GUI_PROPERTY.get(registryKey);
            String formattedName = property.visitNameFormatter(new NameFormatterPropertyVisitor(), registryKey);
            
            if (formattedName != null && !formattedName.isEmpty()) {
                this.setTextButton(entry.getKey(), formattedName);
            }
        }
    }
    
    private GuiPageButtonList.GuiListEntry[] createCustomPropertyPage() {
        // Get total number of page list entries,
        // and add an additional entry for float/int/string properties to accommodate label entry
        List<ResourceLocation> propertyKeys = new ArrayList<>();
        propertyKeys.addAll(ModernBetaRegistries.PROPERTY.getKeys());
        propertyKeys.addAll(ModernBetaClientRegistries.GUI_PROPERTY.getKeys());
        int numEntries = propertyKeys.size() * 2;

        // Build map based on mod ID
        Map<String, Tuple<List<ResourceLocation>, List<ResourceLocation>>> modRegistryKeys = new LinkedHashMap<>();
        
        // Populate initial map
        for (ResourceLocation registryKey : propertyKeys) {
            String namespace = registryKey.getNamespace();
            
            // Add new entry list if encountering new namespace (unique mod)
            if (!modRegistryKeys.containsKey(namespace)) {
                modRegistryKeys.put(namespace, new Tuple<>(new LinkedList<>(), new LinkedList<>()));
                numEntries += 2;
                
                // Add additional entries if namespace has info
                if (I18n.hasKey(PREFIX_ADDON + namespace + ".info")) {
                    numEntries += 2;
                }
            }
        }
        
        // Populate setting keys
        for (ResourceLocation registryKey : ModernBetaRegistries.PROPERTY.getKeys()) {
            // Ignore entries that should be hidden
            if (!ModernBetaRegistries.PROPERTY.get(registryKey).getDisplay()) {
                numEntries -= 2;
                continue;
            }
            
            String namespace = registryKey.getNamespace();
            
            // Add additional entries if mod property has info
            if (I18n.hasKey(PREFIX_ADDON + getFormattedRegistryString(registryKey) + ".info")) {
                numEntries += 2;
            }
            
            modRegistryKeys.get(namespace).getFirst().add(registryKey);
        }
        
        // Populate GUI keys
        for (ResourceLocation registryKey : ModernBetaClientRegistries.GUI_PROPERTY.getKeys()) {
            String namespace = registryKey.getNamespace();
            
            // Add additional entries if mod property has info
            if (I18n.hasKey(PREFIX_ADDON + getFormattedRegistryString(registryKey) + ".gui.info")) {
                numEntries += 2;
            }
            
            modRegistryKeys.get(namespace).getSecond().add(registryKey);
        }
        
        GuiPageButtonList.GuiListEntry[] pageList = new GuiPageButtonList.GuiListEntry[numEntries];
        
        int ndx = 0;
        for (String namespace : modRegistryKeys.keySet()) {
            List<ResourceLocation> registryKeys = modRegistryKeys.get(namespace).getFirst();
            List<ResourceLocation> guiRegistryKeys = modRegistryKeys.get(namespace).getSecond();
            
            pageList[ndx++] = this.createGuiLabelNoPrefix(this.customId++, false, PREFIX_ADDON + namespace);
            pageList[ndx++] = null;
            
            // Create GUI elements for main settings
            for (ResourceLocation registryKey : registryKeys) {
                Property<?> property = this.settings.customProperties.get(registryKey);
                String localizationKey = PREFIX_ADDON + getFormattedRegistryString(registryKey);
                int propertyId;

                pageList[ndx++] = this.createGuiLabelNoPrefix(this.customId++, true, localizationKey);
                pageList[ndx++] = property.visitGui(this.new CreateGuiPropertyVisitor(), propertyId = this.customId++);
                
                if (I18n.hasKey(localizationKey + ".info")) {
                    pageList[ndx++] = this.createGuiLabelNoPrefix(this.customId++, false, TextFormatting.GRAY, localizationKey + ".info");
                    pageList[ndx++] = null;
                }
                
                this.propertyMap.put(propertyId, registryKey);
            }
            
            // Create GUI elements for GUI settings
            for (ResourceLocation registryKey : guiRegistryKeys) {
                GuiProperty<?> property = ModernBetaClientRegistries.GUI_PROPERTY.get(registryKey);
                String localizationKey = PREFIX_ADDON + getFormattedRegistryString(registryKey);
                int propertyId;

                pageList[ndx++] = this.createGuiLabelNoPrefix(this.customId++, true, localizationKey);
                pageList[ndx++] = property.visitGui(this.new CreateGuiPropertyVisitor(), propertyId = this.customId++);
                
                if (I18n.hasKey(localizationKey + ".gui.info")) {
                    pageList[ndx++] = this.createGuiLabelNoPrefix(this.customId++, false, TextFormatting.GRAY, localizationKey + ".gui.info");
                    pageList[ndx++] = null;
                }
                
                this.guiPropertyMap.put(propertyId, registryKey);
            }
            
            // Add general mod info string if there is one
            if (I18n.hasKey(PREFIX_ADDON + namespace + ".info")) {
                pageList[ndx++] = this.createGuiLabelNoPrefix(this.customId++, false, TextFormatting.GRAY, PREFIX_ADDON + namespace + ".info");
                pageList[ndx++] = null;
            }
        }
        
        return pageList;
    }
    
    private void randomizeGuiComponent(int id) {
        Gui gui = this.pageList.getComponent(id);
        
        if (gui instanceof GuiButton && ((GuiButton)gui).enabled) {
            GuiButton guiButton = (GuiButton)gui;
            
            if (GuiIdentifiers.BASE_BUTTON_SETTINGS.containsKey(id)) {
                if (guiButton instanceof GuiListButton) {
                    ResourceLocation randomKey = null;
                    String langName = null;
                    
                    switch (id) {
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
                        case GuiIdentifiers.PG0_B_SPAWN:
                            randomKey = ModernBetaRegistries.WORLD_SPAWNER.getRandomEntry(this.random).getKey();
                            langName = NbtTags.WORLD_SPAWNER;
                            break;
                        case GuiIdentifiers.PG0_B_BLOCK:
                            randomKey = ModernBetaRegistries.DEFAULT_BLOCK.getRandomEntry(this.random).getKey();
                            langName = NbtTags.DEFAULT_BLOCK;
                            break;
                        case GuiIdentifiers.PG0_B_FLUID:
                            randomKey = ForgeRegistryUtil.getRandomFluidRegistryName(this.random);
                            langName = NbtTags.DEFAULT_FLUID;
                            break;
                    }
                    
                    if (randomKey != null && langName != null) {
                        String registryName = randomKey.toString();
                        String formattedName = id == GuiIdentifiers.PG0_B_BLOCK ?
                            getFormattedBlockName(registryName, langName, DEFAULT_NAME_TRUNCATE_LEN) :
                                id == GuiIdentifiers.PG0_B_FLUID ? 
                                getFormattedFluidName(registryName, langName, DEFAULT_NAME_TRUNCATE_LEN) :
                                getFormattedRegistryName(registryName, langName, DEFAULT_NAME_TRUNCATE_LEN, true);
                        
                        GuiIdentifiers.BASE_BUTTON_SETTINGS.get(id).accept(registryName, this.settings);
                        this.setTextButton(id, formattedName);
                    }
                }
                
            } else if (GuiIdentifiers.BASE_SLIDER_SETTINGS.contains(id)) {
                if (guiButton instanceof GuiSlider) {
                    GuiSlider guiSlider = (GuiSlider)guiButton;
                    
                    float randomPos = this.random.nextFloat() * 2.0f;
                    guiSlider.setSliderPosition(MathHelper.clamp(randomPos, 0.0f, 1.0f));
                    
                }
            
            } else if (GuiIdentifiers.BIOME_SETTINGS.containsKey(id)) {
                if (guiButton instanceof GuiListButton) {
                    String registryName = ForgeRegistryUtil.getRandom(this.random, ForgeRegistries.BIOMES).getRegistryName().toString();
                    String langName = id == GuiIdentifiers.PG0_B_FIXED ? NbtTags.SINGLE_BIOME : "";
                    
                    GuiIdentifiers.BIOME_SETTINGS.get(id).accept(registryName,  this.settings);
                    this.setTextButton(id, getFormattedBiomeName(registryName, langName, DEFAULT_NAME_TRUNCATE_LEN));
                }
                
            } else {
                if (guiButton instanceof GuiSlider) {
                    GuiSlider guiSlider = (GuiSlider)guiButton;
                    
                    float randomFloat = guiSlider.getSliderPosition() * (0.75f + this.random.nextFloat() * 0.5f) + (this.random.nextFloat() * 0.1f - 0.05f);
                    guiSlider.setSliderPosition(MathHelper.clamp(randomFloat, 0.0f, 1.0f));
                    
                } else if (guiButton instanceof GuiListButton) {
                    ((GuiListButton)guiButton).setValue(this.random.nextBoolean());
                    
                }
            }
        }
    }

    private String getFormattedValue(int entry, float entryValue) {
        if (this.propertyMap.containsKey(entry)) {
            ResourceLocation registryKey = this.propertyMap.get(entry);
            Property<?> property = this.settings.customProperties.get(registryKey);
            
            if (property instanceof ListProperty) {
                ListProperty listProperty = (ListProperty)property;
                String entryText = String.format("createWorld.customize.custom.%s.%s.", registryKey.getNamespace(), registryKey.getPath());

                return I18n.format(entryText + listProperty.getValues()[(int)entryValue]);
            } else if (property instanceof IntProperty) {
                IntProperty intProperty = (IntProperty)property;
                
                return String.format(intProperty.getFormatter(), (int)entryValue);
            } else {
                return String.format(property.getFormatter(), entryValue);
            }
        }
        
        if (this.guiPropertyMap.containsKey(entry)) {
            ResourceLocation registryKey = this.propertyMap.get(entry);
            GuiProperty<?> property = ModernBetaClientRegistries.GUI_PROPERTY.get(registryKey);

            return String.format(property.getFormatter(), entryValue);
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
            case GuiIdentifiers.PG4_S_R_DPTH_WT:
            case GuiIdentifiers.PG4_S_END_WT:
            case GuiIdentifiers.PG4_S_END_OF:
            case GuiIdentifiers.PG4_S_END_OUT_OF:
            
            case GuiIdentifiers.PG5_F_BASE_SIZE:
            case GuiIdentifiers.PG5_F_STRETCH_Y:
            case GuiIdentifiers.PG5_F_TEMP_SCL:
            case GuiIdentifiers.PG5_F_RAIN_SCL:
            case GuiIdentifiers.PG5_F_DETL_SCL:
            case GuiIdentifiers.PG5_F_B_DPTH_WT:
            case GuiIdentifiers.PG5_F_B_DPTH_OF:
            case GuiIdentifiers.PG5_F_B_SCLE_WT:
            case GuiIdentifiers.PG5_F_B_SCLE_OF:
            case GuiIdentifiers.PG5_F_R_DPTH_WT:
            case GuiIdentifiers.PG5_F_END_WT:
            case GuiIdentifiers.PG5_F_END_OF:
            case GuiIdentifiers.PG5_F_END_OUT_OF:
                return String.format("%2.3f", entryValue);
                
            case GuiIdentifiers.PG0_S_CAVE_WIDTH:
            case GuiIdentifiers.PG1_S_LEVEL_CAVE_WIDTH:
                return String.format("%2.1f", entryValue);
            
            case GuiIdentifiers.PG0_S_CHUNK: {
                ResourceLocation registryKey = ModernBetaRegistries.CHUNK_SOURCE.getKeys().get((int)entryValue);
                String registryName = I18n.format(PREFIX + NbtTags.CHUNK_SOURCE);
                String registryEntry = I18n.format(PREFIX + NbtTags.CHUNK_SOURCE + "." + getFormattedRegistryString(registryKey));
                int registryNameWidth = this.fontRenderer.getStringWidth(registryName);
                
                return getTruncatedString(registryEntry, Math.max(DEFAULT_NAME_TRUNCATE_LEN - registryNameWidth, 0));
            }
            case GuiIdentifiers.PG0_S_BIOME: {
                ResourceLocation registryKey = ModernBetaRegistries.BIOME_SOURCE.getKeys().get((int)entryValue);
                String registryName = I18n.format(PREFIX + NbtTags.BIOME_SOURCE);
                String registryEntry = I18n.format(PREFIX + NbtTags.BIOME_SOURCE + "." + getFormattedRegistryString(registryKey));
                int registryNameWidth = this.fontRenderer.getStringWidth(registryName);
                
                return getTruncatedString(registryEntry, Math.max(DEFAULT_NAME_TRUNCATE_LEN - registryNameWidth, 0));
            }
            case GuiIdentifiers.PG0_S_SURFACE: {
                ResourceLocation registryKey = ModernBetaRegistries.SURFACE_BUILDER.getKeys().get((int)entryValue);
                String registryName = I18n.format(PREFIX + NbtTags.SURFACE_BUILDER);
                String registryEntry = I18n.format(PREFIX + NbtTags.SURFACE_BUILDER + "." + getFormattedRegistryString(registryKey));
                int registryNameWidth = this.fontRenderer.getStringWidth(registryName);
                
                return getTruncatedString(registryEntry, Math.max(DEFAULT_NAME_TRUNCATE_LEN - registryNameWidth, 0));
            }
            case GuiIdentifiers.PG0_S_CARVER: {
                ResourceLocation registryKey = ModernBetaRegistries.CAVE_CARVER.getKeys().get((int)entryValue);
                String registryName = I18n.format(PREFIX + NbtTags.CAVE_CARVER);
                String registryEntry = I18n.format(PREFIX + NbtTags.CAVE_CARVER + "." + getFormattedRegistryString(registryKey));
                int registryNameWidth = this.fontRenderer.getStringWidth(registryName);
                
                return getTruncatedString(registryEntry, Math.max(DEFAULT_NAME_TRUNCATE_LEN - registryNameWidth, 0));
            }
            case GuiIdentifiers.PG0_S_SPAWN: {
                ResourceLocation registryKey = ModernBetaRegistries.WORLD_SPAWNER.getKeys().get((int)entryValue);
                String registryName = I18n.format(PREFIX + NbtTags.WORLD_SPAWNER);
                String registryEntry = I18n.format(PREFIX + NbtTags.WORLD_SPAWNER + "." + getFormattedRegistryString(registryKey));
                int registryNameWidth = this.fontRenderer.getStringWidth(registryName);
                
                return getTruncatedString(registryEntry, Math.max(DEFAULT_NAME_TRUNCATE_LEN - registryNameWidth, 0));
            }
            case GuiIdentifiers.PG0_S_BLOCK: {
                ResourceLocation registryKey = ModernBetaRegistries.DEFAULT_BLOCK.getKeys().get((int)entryValue);
                String registryName = I18n.format(PREFIX + NbtTags.DEFAULT_BLOCK);
                String registryEntry = ForgeRegistries.BLOCKS.getValue(registryKey).getLocalizedName();
                int registryNameWidth = this.fontRenderer.getStringWidth(registryName);
                
                return getTruncatedString(registryEntry, Math.max(DEFAULT_NAME_TRUNCATE_LEN - registryNameWidth, 0));
            }
            case GuiIdentifiers.PG0_S_FLUID: {
                ResourceLocation registryKey = ForgeRegistryUtil.getFluidBlockRegistryNames().get((int)entryValue);
                String registryName = I18n.format(PREFIX + NbtTags.DEFAULT_FLUID);
                String registryEntry = ForgeRegistryUtil.getFluidLocalizedName(registryKey);
                int registryNameWidth = this.fontRenderer.getStringWidth(registryName);

                return getTruncatedString(registryEntry, Math.max(DEFAULT_NAME_TRUNCATE_LEN - registryNameWidth, 0));
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
            case GuiIdentifiers.PG3_S_ORE_TYPE: {
                String key = OreType.values()[(int)entryValue].id;
                
                return I18n.format(PREFIX + "oreType." + key);
            }
            
            case GuiIdentifiers.PG1_S_LEVEL_WIDTH: return String.format("%d", ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)entryValue]);
            case GuiIdentifiers.PG1_S_LEVEL_LENGTH: return String.format("%d", ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)entryValue]);
            case GuiIdentifiers.PG1_S_LEVEL_HEIGHT: return String.format("%d", ModernBetaGeneratorSettings.LEVEL_HEIGHTS[(int)entryValue]);
            
            default: return String.format("%d", (int)entryValue);
        }
    }

    private ModernBetaGeneratorSettings getBuiltSettings() {
        if (!this.settings.equals(this.prevSettings)) {
            this.builtSettings = this.settings.build();
            this.prevSettings = ModernBetaGeneratorSettings.Factory.jsonToFactory(this.settings.toString());
        }
        
        return this.builtSettings;
    }

    private void restoreDefaults() {
        String defaultPreset = PresetUtil.getDefaultPreset();
        this.settings = ModernBetaGeneratorSettings.Factory.jsonToFactory(defaultPreset);
        
        this.createPagedList();
        this.setSettingsModified(false);
    }
    
    private void initButtonValidity() {
        // Primary, Tab buttons
        this.updateButtonValidity();
        
        // List buttons
        this.pageList.setActive(this.isFocused);
    }
    
    private void updateButtonValidity() {
        int page = this.pageList.getPage();
        
        // Primary buttons
        this.buttonRandomize.enabled =  this.isFocused && !this.clickedRandom && (page < 5 || page == 6);
        this.buttonDone.enabled = this.isFocused;
        this.buttonDefaults.enabled = this.isFocused && this.settingsModified;
        this.buttonPresets.enabled = this.isFocused;
        this.buttonPreview.enabled = this.isFocused;
        
        // Nav buttons
        this.buttonNavL.visible = this.displayNavButtons;
        this.buttonNavR.visible = this.displayNavButtons;
        this.buttonNavL.enabled = this.isFocused && this.pageList.getPage() > 0;
        this.buttonNavR.enabled = this.isFocused && this.pageList.getPage() < this.pageList.getPageCount() - 1;
        
        // Tab buttons
        for (Entry<Integer, GuiButtonTab> pageTab : this.pageTabMap.entrySet()) {
            int id = pageTab.getKey().intValue();
            GuiButtonTab tab = pageTab.getValue();
            
            if (id == GuiIdentifiers.FUNC_INITIAL_TAB + this.pageList.getPage()) {
                tab.enabled = false;
                tab.setSelected(true);
            } else {
                tab.enabled = this.isFocused;
                tab.setSelected(false);
            }
        }
    }
    
    private void updateSettingValidity() {
        ModernBetaGeneratorSettings settings = this.getBuiltSettings();
        this.enabledMap.clear();
        
        // Set default enabled for certain options
        if (this.pageList != null) {
            for (Entry<ResourceLocation, GuiPredicate> entry : ModernBetaClientRegistries.GUI_PREDICATE.getEntrySet()) {
                int[] guiIds = entry.getValue().getIds();
                boolean enabled = entry.getValue().test(settings) && this.isFocused;
                
                if (guiIds.length <= 0) {
                    if (this.propertyMap.containsValue(entry.getKey())) {
                        int customId = this.propertyMap.inverse().get(entry.getKey());
                        this.setGuiEnabled(customId, enabled);
                    }
                    
                    if (this.guiPropertyMap.containsValue(entry.getKey())) {
                        int customId = this.guiPropertyMap.inverse().get(entry.getKey());
                        this.setGuiEnabled(customId, enabled);
                    }
                } else {
                    for (int i = 0; i < guiIds.length; ++i) {
                        this.setGuiEnabled(guiIds[i], enabled);
                    }
                }
            }
        }
    }
    
    private void updateHoveredTooltip(int mouseX, int mouseY) {
        this.prevHoveredId = this.hoveredId;
        this.hoveredId = -1;
        
        for (int i = 0; i < this.pageList.getSize(); ++i) {
            GuiEntry entry = this.pageList.getListEntry(i);
            
            Gui gui0 = entry.getComponent1();
            Gui gui1 = entry.getComponent2();
            
            int guiId0 = this.isGuiHovered(gui0, mouseX, mouseY);
            int guiId1 = this.isGuiHovered(gui1, mouseX, mouseY);
            
            if (guiId0 >= 0) {
                this.hoveredId = guiId0;
            } else if (guiId1 >= 0) {
                this.hoveredId = guiId1;
            }
        }
        
        if (this.hoveredId != this.prevHoveredId) {
            this.lastHovered = System.currentTimeMillis();
        }
    }
    
    private void drawHoveredTooltip(int mouseX, int mouseY) {
        if (!this.isFocused || !this.translationKeyMap.containsKey(this.hoveredId)) {
            return;
        }
        
        String tooltipKey = this.translationKeyMap.get(this.hoveredId) + ".tooltip";
        Gui gui = this.pageList.getComponent(this.hoveredId);
        List<String> tooltips = new ArrayList<>();

        int guiWidth = 0;
        int guiHeight = 0;
        int guiX = 0;
        int guiY = 0;
        
        if (gui instanceof GuiButton) {
            GuiButton button = (GuiButton)gui;
            
            guiWidth = button.width;
            guiHeight = button.height;
            guiX = button.x;
            guiY = button.y + button.height;
        } else if (gui instanceof GuiLabel) {
            GuiLabel label = (GuiLabel)gui;
            
            guiWidth = label.width;
            guiHeight = label.height;
            guiX = label.x;
            guiY = label.y + label.height;
        }
        
        // Description
        if (this.hoveredId == GuiIdentifiers.PG0_L_NETHER_BOP) {
            String tooltip = I18n.format(this.translationKeyMap.get(this.hoveredId) + ".tooltip");
            int tooltipWidth = this.fontRenderer.getStringWidth(tooltip);
            List<String> incompatibleMods = ModCompat.NETHER_MANAGER.getIncompatibleMods();
            String modList = "";
            
            for (int i = 0; i < incompatibleMods.size(); ++i) {
                modList += TextFormatting.AQUA + incompatibleMods.get(i);
                
                if (i < incompatibleMods.size() - 1) {
                    modList += TextFormatting.RESET + ", ";
                }
            }

            tooltips.add(tooltip);
            tooltips.addAll(this.fontRenderer.listFormattedStringToWidth(modList, tooltipWidth));
        } else if (!tooltipKey.isEmpty() && I18n.hasKey(tooltipKey)) {
            String tooltip = I18n.format(tooltipKey);
            
            tooltips.addAll(this.fontRenderer.listFormattedStringToWidth(tooltip, TOOLTIP_MAX_WIDTH));
        }
        
        // Add min/max ranges, if a custom property 
        // Offset by one since the actual property button is next to the button
        if (this.propertyMap.containsKey(this.hoveredId + 1)) {
            Property<?> property = ModernBetaRegistries.PROPERTY.get(this.propertyMap.get(this.hoveredId + 1));
            
            if (property instanceof RangedProperty<?>) {
                RangedProperty<?> rangedProperty = (RangedProperty<?>)property;
                
                tooltips.add(TextFormatting.AQUA + String.format("%s: ", I18n.format(PREFIX + "min")) + TextFormatting.YELLOW + rangedProperty.getMinValue().toString());
                tooltips.add(TextFormatting.AQUA + String.format("%s: ", I18n.format(PREFIX + "max")) + TextFormatting.YELLOW + rangedProperty.getMaxValue().toString());
            }
        } else if (GuiIdentifiers.RANGED_SETTINGS.containsKey(this.hoveredId)) {
            Tuple<Supplier<Number>, Supplier<Number>> range = GuiIdentifiers.RANGED_SETTINGS.get(this.hoveredId);
            
            tooltips.add(TextFormatting.AQUA + String.format("%s: ", I18n.format(PREFIX + "min")) + TextFormatting.YELLOW + range.getFirst().get().toString());
            tooltips.add(TextFormatting.AQUA + String.format("%s: ", I18n.format(PREFIX + "max")) + TextFormatting.YELLOW + range.getSecond().get().toString());
        }
        
        if (!tooltips.isEmpty() && System.currentTimeMillis() - this.lastHovered > TOOLTIP_DELAY) {
            int paddingL = 5;
            int paddingT = 5;
            int paddingR = 2;
            int paddingB = 3;
            
            int tooltipHeight = this.fontRenderer.FONT_HEIGHT * tooltips.size() + TOOLTIP_LINE_SPACING * (tooltips.size() - 1);
            int tooltipWidth = this.getMaxStringWidth(tooltips);
            
            int rectH = tooltipHeight + paddingT + paddingB;
            int rectW = tooltipWidth + paddingL + paddingR;
            
            boolean bottomClips = guiY + rectH > this.pageList.bottom;
            
            int offsetX = (guiWidth - rectW) / 2;
            int offsetY = bottomClips ? -2 : 1;
            
            if (bottomClips) {
                guiY -= guiHeight + rectH;
            }
            
            int rectL = guiX + offsetX;
            int rectR = guiX + offsetX + rectW;
            int rectT = guiY + offsetY;
            int rectB = guiY + offsetY + rectH;
            
            int texL = rectL + 1;
            int texR = rectR;
            int texT = rectT + 1;
            int texB = rectB;
            
            double texU = (texR - texL) * 0.03125;
            double texV = (texB - texT) * 0.03125;
            
            this.drawHorizontalLine(rectL, rectR, rectT, GuiColors.ARGB_LIGHT_GREY);
            this.drawHorizontalLine(rectL, rectR, rectB, GuiColors.ARGB_DARK_GREY);
            this.drawVerticalLine(rectL, rectT, rectB, GuiColors.ARGB_LIGHT_GREY);
            this.drawVerticalLine(rectR, rectT, rectB, GuiColors.ARGB_DARK_GREY);
            
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            
            this.mc.getTextureManager().bindTexture(TOOLTIP_BACKGROUND);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(texL, texB, 0.0).tex(0.0, texV).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(texR, texB, 0.0).tex(texU, texV).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(texR, texT, 0.0).tex(texU, 0.0).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(texL, texT, 0.0).tex(0.0, 0.0).color(64, 64, 64, 64).endVertex();
            
            tessellator.draw();

            this.drawSplitString(tooltips, rectL + paddingL, rectT + paddingT, GuiColors.RGB_WHITE);
        }
    }
    
    private void drawSplitString(List<String> strings, int x, int y, int color) {
        for (String str : strings) {
            this.fontRenderer.drawStringWithShadow(str, x, y, color);
            y += this.fontRenderer.FONT_HEIGHT + TOOLTIP_LINE_SPACING;
        }
    }
    
    private int getMaxStringWidth(List<String> strings) {
        int maxWidth = 0;
        
        for (String s : strings) {
            int width = this.fontRenderer.getStringWidth(s); 
            
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        
        return maxWidth;
    }
    
    private int isGuiHovered(Gui gui, int mouseX, int mouseY) {
        if (mouseY < this.pageList.top || mouseY > this.pageList.bottom) {
            return -1;
        } else if (gui instanceof GuiButton) {
            GuiButton button = (GuiButton)gui;
            
            return button.isMouseOver() ? button.id : -1;
        } else if (gui instanceof GuiLabel) {
            GuiLabel label = (GuiLabel)gui;
            
            return mouseX >= label.x && mouseY >= label.y && mouseX < label.x + label.width && mouseY < label.y + label.height ? label.id : -1;
        }
        
        return -1;
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
        int guiTextId = guiText.getId();
        
        if (guiTextValue == null || this.enabledMap.containsKey(guiTextId) && !this.enabledMap.get(guiTextId)) {
            return;
        }
        
        guiTextValue += increment;
        String guiTextString = this.getFormattedValue(guiText.getId(), guiTextValue);
        guiText.setText(guiTextString);
        
        this.setEntryValue(guiTextId, guiTextString);
    }
    
    private boolean modifyPageValue(int amount) {
        Gui guiComponent = this.pageList.getFocusedControl();
        if (amount == 0 || guiComponent instanceof GuiTextField && ((GuiTextField)guiComponent).isFocused()) {
            return false;
        }
        
        int page = this.pageList.getPage();
        int pageNext = page + amount;
        int pageCount = this.pageList.getPageCount();
        
        if (pageNext >= 0 && pageNext < pageCount) {
            this.pageList.setPage(pageNext);
            this.pageTabMap.get(GuiIdentifiers.FUNC_INITIAL_TAB + page).setSelected(false);
            this.pageTabMap.get(GuiIdentifiers.FUNC_INITIAL_TAB + pageNext).setSelected(true);
            this.updateButtonValidity();
            this.playSound();
        }
        
        return true;
    }
    
    private void setTextButton(int id, String value) {
        Gui guiComponent = this.pageList.getComponent(id);
        if (guiComponent != null && guiComponent instanceof GuiButton) {
            ((GuiButton)guiComponent).displayString = value;
        }
    }
    
    private int getLevelSeaLevel() {
        ModernBetaGeneratorSettings settings = this.getBuiltSettings();
        BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(settings.biomeSource).apply(0L, settings);
        ChunkSource chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(settings.chunkSource).apply(0L, settings, biomeSource);
        
        int levelSeaLevel = -1;
        if (chunkSource instanceof FiniteChunkSource) {
            levelSeaLevel = chunkSource.getSeaLevel();
        }
        
        return levelSeaLevel;
    }
    
    private void playSound() {
        if (!this.clicked && !this.clickedRandom) {
            SoundUtil.playClickSound(this.mc.getSoundHandler());
        }
    }
    
    private void exit() {
        this.executor.shutdown();
        this.mc.displayGuiScreen(this.parent);
    }
    
    private void setGuiEnabled(int id, boolean enabled) {
        this.setButtonEnabled(id, enabled);
        this.setFieldEnabled(id, enabled);
        this.enabledMap.put(id, enabled);
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
    
    private void openBiomeScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial) {
        this.openBiomeScreen(consumer, initial, key -> true);
    }
    
    private void openBiomeScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial, Predicate<ResourceLocation> predicate) {
        Function<ResourceLocation, String> nameFormatter = key -> ForgeRegistries.BIOMES.getValue(key).getBiomeName();
        this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(this, consumer, nameFormatter, initial, "biome", ForgeRegistryUtil.getKeys(ForgeRegistries.BIOMES, predicate)));
    }

    private void openBlockScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial, Predicate<ResourceLocation> predicate) {
        this.openBlockScreen(consumer, initial, "block", predicate);
    }
    
    private void openBlockScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial, String nbtTag, Predicate<ResourceLocation> predicate) {
        Function<ResourceLocation, String> nameFormatter = key -> {
            Block block = ForgeRegistryUtil.get(key, ForgeRegistries.BLOCKS);
            
            return ForgeRegistryUtil.isForgeFluid(block) ? 
                ForgeRegistryUtil.getFluidLocalizedName(key) :
                ForgeRegistries.BLOCKS.getValue(key).getLocalizedName();
        };
        
        this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(this, consumer, nameFormatter, initial, nbtTag, ForgeRegistryUtil.getKeys(ForgeRegistries.BLOCKS, predicate)));
    }
    
    private void openFluidScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial, String nbtTag, Predicate<ResourceLocation> predicate) {
        Function<ResourceLocation, String> nameFormatter = key -> ForgeRegistryUtil.getFluidLocalizedName(key);
        this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(this, consumer, nameFormatter, initial, nbtTag, ForgeRegistryUtil.getFluidBlockRegistryNames()));
    }
    
    private void openEntityScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial, Predicate<ResourceLocation> predicate) {
        Function<ResourceLocation, String> nameFormatter = key -> ForgeRegistries.ENTITIES.getValue(key).getName();
        this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(this, consumer, nameFormatter, initial, "entity", ForgeRegistryUtil.getKeys(ForgeRegistries.ENTITIES, predicate)));
    }
    
    private void openRegistryScreen(BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer, String initial, String nbtTag, List<ResourceLocation> registryKeys) {
        Function<ResourceLocation, String> nameFormatter = key -> I18n.format(String.format("%s%s.%s.%s", PREFIX, nbtTag, key.getNamespace(), key.getPath()));
        this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(this, consumer, nameFormatter, initial, nbtTag, registryKeys));
    }
    
    private GuiPageButtonList.GuiLabelEntry createGuiLabel(int id, String... tags) {
        return this.createGuiLabel(id, TextFormatting.RESET, tags);
    }
    
    private GuiPageButtonList.GuiLabelEntry createGuiLabel(int id, TextFormatting formatting, String... tags) {
        String key = PREFIX_LABEL + String.join(".", tags);
        this.translationKeyMap.put(id, key);
        
        return new GuiPageButtonList.GuiLabelEntry(id, formatting + I18n.format(key), true);
    }
    
    private GuiPageButtonList.GuiLabelEntry createGuiLabelNoPrefix(int id, boolean addColon, String... tags) {
        return this.createGuiLabelNoPrefix(id, "", addColon, tags);
    }
    
    private GuiPageButtonList.GuiLabelEntry createGuiLabelNoPrefix(int id, String suffix, boolean addColon, String... tags) {
        return this.createGuiLabelNoPrefix(id, addColon, suffix, TextFormatting.RESET, tags);
    }
    
    private GuiPageButtonList.GuiLabelEntry createGuiLabelNoPrefix(int id, boolean addColon, TextFormatting formatting, String... tags) {
        return this.createGuiLabelNoPrefix(id, addColon, "", formatting, tags);
    }
    
    private GuiPageButtonList.GuiLabelEntry createGuiLabelNoPrefix(int id, boolean addColon, String suffix, TextFormatting formatting, String... tags) {
        String key = String.join(".", tags);
        this.translationKeyMap.put(id, key);
        
        return new GuiPageButtonList.GuiLabelEntry(id, formatting + I18n.format(key) + suffix + (addColon ? ":" : ""), true);
    }
    
    private GuiPageButtonList.GuiSlideEntry createGuiSlider(int id, String tag, float minValue, float maxValue, float initialValue, FormatHelper formatHelper) {
        String key = PREFIX + tag;
        this.translationKeyMap.put(id, key);
        
        return new GuiPageButtonList.GuiSlideEntry(id, I18n.format(key), true, formatHelper, minValue, maxValue, initialValue);
    }
    
    private GuiPageButtonList.GuiButtonEntry createGuiButton(int id, String tag, boolean initialValue) {
        String key = PREFIX + tag;
        this.translationKeyMap.put(id, key);
        
        return new GuiPageButtonList.GuiButtonEntry(id, I18n.format(key), true, initialValue);
    }
    
    private GuiPageButtonList.EditBoxEntry createGuiField(int id, String formattedValue, Predicate<String> predicate) {
        return new GuiPageButtonList.EditBoxEntry(id, formattedValue, true, predicate);
    }
    
    private static String getFormattedRegistryString(ResourceLocation registryKey) {
        return registryKey.getNamespace() + "." + registryKey.getPath();
    }

    private static String getFormattedRegistryName(String registryName, String langName, int truncateLen, boolean includeTitle) {
        ResourceLocation registryKey = new ResourceLocation(registryName);
        String formattedName = I18n.format(String.format("%s%s.%s.%s", PREFIX, langName, registryKey.getNamespace(), registryKey.getPath()));
        String formattedText = includeTitle ? String.format("%s: %s", I18n.format(PREFIX + langName), formattedName) : formattedName;

        return getTruncatedString(formattedText, truncateLen);
    }
    
    private static String getFormattedBiomeName(String registryName) {
        return getFormattedBiomeName(registryName, "", -1);
    }
    
    private static String getFormattedBiomeName(String registryName, int truncateLen) {
        return getFormattedBiomeName(registryName, "", truncateLen);
    }
    
    private static String getFormattedBiomeName(String registryName, String langName, int truncateLen) {
        return getFormattedForgeRegistryName(
            registryName,
            langName,
            truncateLen,
            key -> ForgeRegistryUtil.get(new ResourceLocation(key), ForgeRegistries.BIOMES).getBiomeName()
        );
    }
    
    private static String getFormattedBlockName(String registryName, String langName, int truncateLen) {
        return getFormattedForgeRegistryName(
            registryName,
            langName,
            truncateLen,
            key -> ForgeRegistryUtil.get(new ResourceLocation(key), ForgeRegistries.BLOCKS).getLocalizedName()
        );
    }
    
    private static String getFormattedFluidName(String registryName, String langName, int truncateLen) {
        return getFormattedForgeRegistryName(
            registryName,
            langName,
            truncateLen,
            key -> ForgeRegistryUtil.getFluidLocalizedName(new ResourceLocation(key))
        );
    }
    
    private static String getFormattedForgeRegistryName(String registryName, String langName, int truncateLen, Function<String, String> nameFormatter) {
        String formattedName = nameFormatter.apply(registryName);
        String formattedText = !langName.isEmpty() ? String.format("%s: %s", I18n.format(PREFIX + langName), formattedName) : formattedName;
        
        return getTruncatedString(formattedText, truncateLen);
    }
    
    private static String getTruncatedString(String string, int truncateLen) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        int stringWidth = fontRenderer.getStringWidth(string);
        
        if (truncateLen > 0 && stringWidth > truncateLen) {
            string = fontRenderer.trimStringToWidth(string, truncateLen) + "...";
        }
        
        return string;
    }

    private static int getNdx(int[] arr, int val) {
        for (int i = 0; i < arr.length; ++i) {
            if (val == arr[i])
                return i;
        }
        
        return 0;
    }
    
    private static float roundToThreeDec(float entryValue) {
        BigDecimal bigDecimal = new BigDecimal(entryValue);
        bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        
        return bigDecimal.floatValue();
    }
    
    private static float roundToOneDec(float entryValue) {
        BigDecimal bigDecimal = new BigDecimal(entryValue);
        bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
        
        return bigDecimal.floatValue();
    }
    
    public static class NameFormatterPropertyVisitor implements FormattedPropertyVisitor {
        @Override
        public String visit(BooleanProperty property, ResourceLocation registryKey) {
            return I18n.format(property.getValue() ? "gui.yes" : "gui.no");
        }
    
        @Override
        public String visit(FloatProperty property, ResourceLocation registryKey) {
            return String.format(property.getFormatter(), property.getValue());
        }
    
        @Override
        public String visit(IntProperty property, ResourceLocation registryKey) {
            return String.format(property.getFormatter(), property.getValue());
        }
    
        @Override
        public String visit(StringProperty property, ResourceLocation registryKey) {
            return property.getValue();
        }
    
        @Override
        public String visit(ListProperty property, ResourceLocation registryKey) {
            String namespace = registryKey.getNamespace();
            String path = registryKey.getPath();
            String entryText = String.format("createWorld.customize.custom.%s.%s.", namespace, path);

            return I18n.format(entryText + property.getValue());
        }
    
        @Override
        public String visit(BiomeProperty property, ResourceLocation registryKey) {
            return getFormattedBiomeName(property.getValue());
        }
    
        @Override
        public String visit(BlockProperty property, ResourceLocation registryKey) {
            ResourceLocation blockKey = new ResourceLocation(property.getValue());
            Block block = ForgeRegistries.BLOCKS.getValue(blockKey);
            
            Function<String, String> nameFormatter = key -> ForgeRegistryUtil.isForgeFluid(block) ? 
                ForgeRegistryUtil.getFluidLocalizedName(new ResourceLocation(key)) :
                ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key)).getLocalizedName();
                
            return getFormattedForgeRegistryName(property.getValue(), "", DEFAULT_NAME_TRUNCATE_LEN, nameFormatter);
        }
    
        @Override
        public String visit(EntityEntryProperty property, ResourceLocation registryKey) {
            Function<String, String> nameFormatter = key ->
                ForgeRegistries.ENTITIES.getValue(new ResourceLocation(key)).getName();
            
            return getFormattedForgeRegistryName(property.getValue(), "", DEFAULT_NAME_TRUNCATE_LEN, nameFormatter);
        }
    
        @Override
        public String visit(ScreenProperty property, ResourceLocation registryKey) {
            return I18n.format(PREFIX + "propertyScreen");
        }
        
        @Override
        public String visit(RegistryProperty<?> property, ResourceLocation registryKey) {
            return getFormattedRegistryName(property.getValue(), property.getRegistry().getName(), DEFAULT_NAME_TRUNCATE_LEN, false);
        }
        
    }

    private class CreateGuiPropertyVisitor implements GuiPropertyVisitor {
        @Override
        public GuiPageButtonList.GuiListEntry visit(BooleanProperty property, int guiIdentifier) {
            return GuiScreenCustomizeWorld.this.createGuiButton(guiIdentifier, "enabled", property.getValue());
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(FloatProperty property, int guiIdentifier) {
            String formattedValue = String.format(property.getFormatter(), property.getValue());
            
            switch(property.getGuiType()) {
                case FIELD:
                    return GuiScreenCustomizeWorld.this.createGuiField(guiIdentifier, formattedValue, property.getStringPredicate());
                case SLIDER:
                    return GuiScreenCustomizeWorld.this.createGuiSlider(
                        guiIdentifier,
                        "entry",
                        property.getMinValue(),
                        property.getMaxValue(), 
                        property.getValue(),
                        GuiScreenCustomizeWorld.this
                    );
                default: 
                    return GuiScreenCustomizeWorld.this.createGuiField(guiIdentifier, formattedValue, property.getStringPredicate());
            }
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(IntProperty property, int guiIdentifier) {
            String formattedValue = String.format(property.getFormatter(), property.getValue());
            
            switch(property.getGuiType()) {
                case FIELD:
                    return GuiScreenCustomizeWorld.this.createGuiField(guiIdentifier, formattedValue, property.getStringPredicate());
                case SLIDER:
                    return GuiScreenCustomizeWorld.this.createGuiSlider(
                        guiIdentifier,
                        "entry",
                        property.getMinValue(),
                        property.getMaxValue(), 
                        property.getValue(),
                        GuiScreenCustomizeWorld.this
                    );
                default: 
                    return GuiScreenCustomizeWorld.this.createGuiField(guiIdentifier, formattedValue, property.getStringPredicate());
            }
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(StringProperty property, int guiIdentifier) {
            return GuiScreenCustomizeWorld.this.createGuiField(guiIdentifier, property.getValue(), string -> true);
        }

        @Override
        public GuiPageButtonList.GuiListEntry visit(ListProperty property, int guiIdentifier) {
            int listNdx = property.indexOf(property.getValue());
            if (listNdx == -1) 
                listNdx = 0;
            
            return GuiScreenCustomizeWorld.this.createGuiSlider(
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
            return GuiScreenCustomizeWorld.this.createGuiButton(guiIdentifier, "", true);
        }

        @Override
        public GuiListEntry visit(BlockProperty property, int guiIdentifier) {
            return GuiScreenCustomizeWorld.this.createGuiButton(guiIdentifier, "", true);
        }

        @Override
        public GuiListEntry visit(EntityEntryProperty property, int guiIdentifier) {
            return GuiScreenCustomizeWorld.this.createGuiButton(guiIdentifier, "", true);
        }

        @Override
        public GuiListEntry visit(ScreenProperty property, int guiIdentifier) {
            return GuiScreenCustomizeWorld.this.createGuiButton(guiIdentifier, "", true);
        }
        
        @Override
        public GuiListEntry visit(RegistryProperty<?> property, int guiIdentifier) {
            return GuiScreenCustomizeWorld.this.createGuiButton(guiIdentifier, "", true);
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

                property.setValue(entryValue);
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
                
                property.setValue((int)entryValue);
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
                property.getValue(),
                property.getFilter()::test
            );
        }

        @Override
        public void visit(BlockProperty property, int guiIdentifier, ResourceLocation registryKey) {
            GuiScreenCustomizeWorld.this.openBlockScreen(
                (str, factory) -> ((BlockProperty)factory.customProperties.get(registryKey)).setValue(str),
                property.getValue(),
                property.getFilter()::test
            );
        }

        @Override
        public void visit(EntityEntryProperty property, int guiIdentifier, ResourceLocation registryKey) {
            GuiScreenCustomizeWorld.this.openEntityScreen(
                (str, factory) -> ((EntityEntryProperty)factory.customProperties.get(registryKey)).setValue(str),
                property.getValue(),
                property.getFilter()::test
            );
        }

        @Override
        public void visit(ScreenProperty property, int guiIdentifier, ResourceLocation registryKey) {
            GuiScreen screen = property.getValue().apply(GuiScreenCustomizeWorld.this, registryKey);
            GuiScreenCustomizeWorld.this.mc.displayGuiScreen(screen);
        };
        
        @Override
        public void visit(RegistryProperty<?> property, int guiIdentifier, ResourceLocation registryKey) {
            GuiScreenCustomizeWorld.this.openRegistryScreen(
                (str, factory) -> ((RegistryProperty<?>)factory.customProperties.get(registryKey)).setValue(str),
                property.getValue(),
                property.getRegistry().getName(),
                property.getRegistry().getKeys()
            );
        }
        
    }
}