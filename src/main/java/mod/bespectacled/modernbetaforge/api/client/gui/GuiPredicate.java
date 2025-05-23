package mod.bespectacled.modernbetaforge.api.client.gui;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.client.gui.GuiIdentifiers;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;

public class GuiPredicate {
    public static final ResourceLocation SURFACE_BUILDER = createRegistryKey(NbtTags.SURFACE_BUILDER);
    public static final ResourceLocation SPAWN_LOCATOR = createRegistryKey(NbtTags.WORLD_SPAWNER);
    public static final ResourceLocation SINGLE_BIOME = createRegistryKey(NbtTags.SINGLE_BIOME);
    public static final ResourceLocation REPLACE_OCEAN = createRegistryKey(NbtTags.REPLACE_OCEAN_BIOMES);
    public static final ResourceLocation REPLACE_BEACH = createRegistryKey(NbtTags.REPLACE_BEACH_BIOMES);
    public static final ResourceLocation REPLACE_RIVER = createRegistryKey(NbtTags.REPLACE_RIVER_BIOMES);
    public static final ResourceLocation SEA_LEVEL = createRegistryKey(NbtTags.SEA_LEVEL);
    public static final ResourceLocation CAVE_WIDTH = createRegistryKey(NbtTags.CAVE_WIDTH);
    public static final ResourceLocation CAVE_HEIGHT = createRegistryKey(NbtTags.CAVE_HEIGHT);
    public static final ResourceLocation CAVE_COUNT = createRegistryKey(NbtTags.CAVE_COUNT);
    public static final ResourceLocation CAVE_CHANCE = createRegistryKey(NbtTags.CAVE_CHANCE);
    public static final ResourceLocation USE_STRONGHOLDS = createRegistryKey(NbtTags.USE_STRONGHOLDS);
    public static final ResourceLocation USE_VILLAGES = createRegistryKey(NbtTags.USE_VILLAGES);
    public static final ResourceLocation USE_VILLAGE_VARIANTS = createRegistryKey(NbtTags.USE_VILLAGE_VARIANTS);
    public static final ResourceLocation USE_TEMPLES = createRegistryKey(NbtTags.USE_TEMPLES);
    public static final ResourceLocation USE_MONUMENTS = createRegistryKey(NbtTags.USE_MONUMENTS);
    public static final ResourceLocation USE_MANSIONS = createRegistryKey(NbtTags.USE_MANSIONS);
    public static final ResourceLocation DUNGEON_CHANCE = createRegistryKey(NbtTags.DUNGEON_CHANCE);
    public static final ResourceLocation WATER_LAKE_CHANCE = createRegistryKey(NbtTags.WATER_LAKE_CHANCE);
    public static final ResourceLocation LAVA_LAKE_CHANCE = createRegistryKey(NbtTags.LAVA_LAKE_CHANCE);
    public static final ResourceLocation USE_SANDSTONE = createRegistryKey(NbtTags.USE_SANDSTONE);
    public static final ResourceLocation USE_OLD_NETHER = createRegistryKey(NbtTags.USE_OLD_NETHER);
    public static final ResourceLocation USE_NETHER_CAVES = createRegistryKey(NbtTags.USE_NETHER_CAVES);
    public static final ResourceLocation USE_FORTRESSES = createRegistryKey(NbtTags.USE_FORTRESSES);
    public static final ResourceLocation USE_LAVA_POCKETS = createRegistryKey(NbtTags.USE_LAVA_POCKETS);
    
    public static final ResourceLocation LEVEL_THEME = createRegistryKey(NbtTags.LEVEL_THEME);
    public static final ResourceLocation LEVEL_TYPE = createRegistryKey(NbtTags.LEVEL_TYPE);
    public static final ResourceLocation LEVEL_WIDTH = createRegistryKey(NbtTags.LEVEL_WIDTH);
    public static final ResourceLocation LEVEL_LENGTH = createRegistryKey(NbtTags.LEVEL_LENGTH);
    public static final ResourceLocation LEVEL_HEIGHT = createRegistryKey(NbtTags.LEVEL_HEIGHT);
    public static final ResourceLocation LEVEL_HOUSE = createRegistryKey(NbtTags.LEVEL_HOUSE);
    public static final ResourceLocation LEVEL_CAVE_WIDTH = createRegistryKey(NbtTags.LEVEL_CAVE_WIDTH);
    public static final ResourceLocation USE_INDEV_CAVES = createRegistryKey(NbtTags.USE_INDEV_CAVES);
    public static final ResourceLocation USE_INFDEV_WALLS = createRegistryKey(NbtTags.USE_INFDEV_WALLS);
    public static final ResourceLocation USE_INFDEV_PYRAMIDS = createRegistryKey(NbtTags.USE_INFDEV_PYRAMIDS);
    
    public static final ResourceLocation USE_TALL_GRASS = createRegistryKey(NbtTags.USE_TALL_GRASS);
    public static final ResourceLocation USE_NEW_FLOWERS = createRegistryKey(NbtTags.USE_NEW_FLOWERS);
    public static final ResourceLocation USE_DOUBLE_PLANTS = createRegistryKey(NbtTags.USE_DOUBLE_PLANTS);
    public static final ResourceLocation USE_LILY_PADS = createRegistryKey(NbtTags.USE_LILY_PADS);
    public static final ResourceLocation USE_MELONS = createRegistryKey(NbtTags.USE_MELONS);
    public static final ResourceLocation USE_DESERT_WELLS = createRegistryKey(NbtTags.USE_DESERT_WELLS);
    public static final ResourceLocation USE_FOSSILS = createRegistryKey(NbtTags.USE_FOSSILS);
    public static final ResourceLocation USE_SAND_DISKS = createRegistryKey(NbtTags.USE_SAND_DISKS);
    public static final ResourceLocation USE_GRAVEL_DISKS = createRegistryKey(NbtTags.USE_GRAVEL_DISKS);
    public static final ResourceLocation USE_CLAY_DISKS = createRegistryKey(NbtTags.USE_CLAY_DISKS);
    public static final ResourceLocation USE_BIRCH_TREES = createRegistryKey(NbtTags.USE_BIRCH_TREES);
    public static final ResourceLocation USE_PINE_TREES = createRegistryKey(NbtTags.USE_PINE_TREES);
    public static final ResourceLocation USE_SWAMP_TREES = createRegistryKey(NbtTags.USE_SWAMP_TREES);
    public static final ResourceLocation USE_JUNGLE_TREES = createRegistryKey(NbtTags.USE_JUNGLE_TREES);
    public static final ResourceLocation USE_ACACIA_TREES = createRegistryKey(NbtTags.USE_ACACIA_TREES);
    public static final ResourceLocation SPAWN_NEW_CREATURE_MOBS = createRegistryKey(NbtTags.SPAWN_NEW_CREATURE_MOBS);
    public static final ResourceLocation SPAWN_NEW_MONSTER_MOBS = createRegistryKey(NbtTags.SPAWN_NEW_MONSTER_MOBS);
    public static final ResourceLocation SPAWN_WATER_MOBS = createRegistryKey(NbtTags.SPAWN_WATER_MOBS);
    public static final ResourceLocation SPAWN_AMBIENT_MOBS = createRegistryKey(NbtTags.SPAWN_AMBIENT_MOBS);
    public static final ResourceLocation SPAWN_WOLVES = createRegistryKey(NbtTags.SPAWN_WOLVES);
    public static final ResourceLocation BIOME_SIZE = createRegistryKey(NbtTags.BIOME_SIZE);
    
    public static final ResourceLocation CLAY_SIZE = createRegistryKey(NbtTags.CLAY_SIZE);
    public static final ResourceLocation CLAY_COUNT = createRegistryKey(NbtTags.CLAY_COUNT);
    public static final ResourceLocation CLAY_MIN_HEIGHT = createRegistryKey(NbtTags.CLAY_MIN_HEIGHT);
    public static final ResourceLocation CLAY_MAX_HEIGHT = createRegistryKey(NbtTags.CLAY_MAX_HEIGHT);
    public static final ResourceLocation EMERALD_SIZE = createRegistryKey(NbtTags.EMERALD_SIZE);
    public static final ResourceLocation EMERALD_COUNT = createRegistryKey(NbtTags.EMERALD_COUNT);
    public static final ResourceLocation EMERALD_MIN_HEIGHT = createRegistryKey(NbtTags.EMERALD_MIN_HEIGHT);
    public static final ResourceLocation EMERALD_MAX_HEIGHT = createRegistryKey(NbtTags.EMERALD_MAX_HEIGHT);
    public static final ResourceLocation QUARTZ_SIZE = createRegistryKey(NbtTags.QUARTZ_SIZE);
    public static final ResourceLocation QUARTZ_COUNT = createRegistryKey(NbtTags.QUARTZ_COUNT);
    public static final ResourceLocation MAGMA_SIZE = createRegistryKey(NbtTags.MAGMA_SIZE);
    public static final ResourceLocation MAGMA_COUNT = createRegistryKey(NbtTags.MAGMA_COUNT);
    
    public static final ResourceLocation COORDINATE_SCALE = createRegistryKey(NbtTags.COORDINATE_SCALE);
    public static final ResourceLocation HEIGHT_SCALE = createRegistryKey(NbtTags.HEIGHT_SCALE);
    public static final ResourceLocation LOWER_LIMIT_SCALE = createRegistryKey(NbtTags.LOWER_LIMIT_SCALE);
    public static final ResourceLocation UPPER_LIMIT_SCALE = createRegistryKey(NbtTags.UPPER_LIMIT_SCALE);
    public static final ResourceLocation SCALE_NOISE_SCALE_X = createRegistryKey(NbtTags.SCALE_NOISE_SCALE_X);
    public static final ResourceLocation SCALE_NOISE_SCALE_Z = createRegistryKey(NbtTags.SCALE_NOISE_SCALE_Z);
    public static final ResourceLocation DEPTH_NOISE_SCALE_X = createRegistryKey(NbtTags.DEPTH_NOISE_SCALE_X);
    public static final ResourceLocation DEPTH_NOISE_SCALE_Z = createRegistryKey(NbtTags.DEPTH_NOISE_SCALE_Z);
    public static final ResourceLocation MAIN_NOISE_SCALE_X = createRegistryKey(NbtTags.MAIN_NOISE_SCALE_X);
    public static final ResourceLocation MAIN_NOISE_SCALE_Y = createRegistryKey(NbtTags.MAIN_NOISE_SCALE_Y);
    public static final ResourceLocation MAIN_NOISE_SCALE_Z = createRegistryKey(NbtTags.MAIN_NOISE_SCALE_Z);
    public static final ResourceLocation BASE_SIZE = createRegistryKey(NbtTags.BASE_SIZE);
    public static final ResourceLocation STRETCH_Y = createRegistryKey(NbtTags.STRETCH_Y);
    public static final ResourceLocation HEIGHT = createRegistryKey(NbtTags.HEIGHT);
    public static final ResourceLocation TEMP_NOISE_SCALE = createRegistryKey(NbtTags.TEMP_NOISE_SCALE);
    public static final ResourceLocation RAIN_NOISE_SCALE = createRegistryKey(NbtTags.RAIN_NOISE_SCALE);
    public static final ResourceLocation DETAIL_NOISE_SCALE = createRegistryKey(NbtTags.DETAIL_NOISE_SCALE);
    public static final ResourceLocation BIOME_DEPTH_WEIGHT = createRegistryKey(NbtTags.BIOME_DEPTH_WEIGHT);
    public static final ResourceLocation BIOME_DEPTH_OFFSET = createRegistryKey(NbtTags.BIOME_DEPTH_OFFSET);
    public static final ResourceLocation BIOME_SCALE_WEIGHT = createRegistryKey(NbtTags.BIOME_SCALE_WEIGHT);
    public static final ResourceLocation BIOME_SCALE_OFFSET = createRegistryKey(NbtTags.BIOME_SCALE_OFFSET);
    public static final ResourceLocation RIVER_SIZE = createRegistryKey(NbtTags.RIVER_SIZE);
    public static final ResourceLocation USE_BIOME_DEPTH_SCALE = createRegistryKey(NbtTags.USE_BIOME_DEPTH_SCALE);
    public static final ResourceLocation LAYER_TYPE = createRegistryKey(NbtTags.LAYER_TYPE);
    public static final ResourceLocation LAYER_SIZE = createRegistryKey(NbtTags.LAYER_SIZE);
    public static final ResourceLocation SNOWY_BIOME_CHANCE = createRegistryKey(NbtTags.SNOWY_BIOME_CHANCE);
    
    public static final ResourceLocation BASE_BIOME = createRegistryKey(NbtTags.BASE_BIOME);
    public static final ResourceLocation OCEAN_BIOME = createRegistryKey(NbtTags.OCEAN_BIOME);
    public static final ResourceLocation BEACH_BIOME = createRegistryKey(NbtTags.BEACH_BIOME);
    
    public static final ResourceLocation END_ISLAND_OFFSET = createRegistryKey(NbtTags.END_ISLAND_OFFSET);
    public static final ResourceLocation END_ISLAND_WEIGHT = createRegistryKey(NbtTags.END_ISLAND_WEIGHT);
    public static final ResourceLocation END_OUTER_ISLAND_DISTANCE = createRegistryKey(NbtTags.END_OUTER_ISLAND_DISTANCE);
    public static final ResourceLocation END_OUTER_ISLAND_OFFSET = createRegistryKey(NbtTags.END_OUTER_ISLAND_OFFSET);
    public static final ResourceLocation USE_END_OUTER_ISLANDS = createRegistryKey(NbtTags.USE_END_OUTER_ISLANDS);
    
    private Predicate<ModernBetaGeneratorSettings> predicate;
    private final int[] guiIds;
    
    /**
     * Constructs a new GuiPredicate with an associated GUI integer id and its predicate consuming generator settings.
     * 
     * @param predicate The predicate used to test if the GUI button associated with the id should be enabled.
     * @param guiIds A list of integer ids that is associate the GUI buttons, can be found in {@link GuiIdentifiers}.
     */
    public GuiPredicate(Predicate<ModernBetaGeneratorSettings> predicate, int... guiIds) {
        this.predicate = predicate;
        this.guiIds = guiIds;
    }
    
    /**
     * Append to the current predicate with a new OR condition.
     * 
     * @param predicate The new predicate to test in addition to the existing predicate.
     * @return This GuiPredicate object, so the statements can be chained.
     */
    public GuiPredicate or(Predicate<ModernBetaGeneratorSettings> predicate) {
        this.predicate = this.predicate.or(predicate);
        
        return this;
    }
    
    /**
     * Append to the current predicate with a new AND condition.
     * 
     * @param predicate The new predicate to test in addition to the existing predicate.
     * @return This GuiPredicate object, so the statements can be chained.
     */
    public GuiPredicate and(Predicate<ModernBetaGeneratorSettings> predicate) {
        this.predicate = this.predicate.and(predicate);
        
        return this;
    }
    
    /**
     * Tests the predicate.
     * 
     * @param settings The settings to use to test.
     * @return Whether the test is passed. This is then used to set the GUI button's enabled state.
     */
    public boolean test(ModernBetaGeneratorSettings settings) {
        return this.predicate.test(settings);
    }
    
    /**
     * Gets the GUI id associated with the predicate. GUI ids for built-in buttons can be found in {@link GuiIdentifiers}.
     * 
     * @return An integer denoting the GUI button's id.
     */
    public int[] getIds() {
        return this.guiIds;
    }
    
    private static ResourceLocation createRegistryKey(String name) {
        return ModernBeta.createRegistryKey(name);
    }
}
