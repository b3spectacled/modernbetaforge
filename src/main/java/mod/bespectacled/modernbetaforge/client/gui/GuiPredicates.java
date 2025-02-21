package mod.bespectacled.modernbetaforge.client.gui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.source.NoiseBiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.compat.CompatDynamicTrees;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.chunk.source.SkylandsChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
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
public class GuiPredicates {
    private static final Map<ResourceLocation, List<Integer>> NOISE_SETTINGS = new LinkedHashMap<>();
    private static final List<Integer> DEFAULT_NOISE_SETTINGS = ImmutableList.of(
        GuiIdentifiers.PG4_S_MAIN_NS_X,
        GuiIdentifiers.PG4_S_MAIN_NS_Y,
        GuiIdentifiers.PG4_S_MAIN_NS_Z,
        GuiIdentifiers.PG4_S_COORD_SCL,
        GuiIdentifiers.PG4_S_HEIGH_SCL,
        GuiIdentifiers.PG4_S_UPPER_LIM,
        GuiIdentifiers.PG4_S_LOWER_LIM,
        GuiIdentifiers.PG4_S_HEIGH_LIM
    );
    
    private static final MapGenStronghold STRONGHOLD = new MapGenStronghold();
    
    public static final GuiPredicate SURFACE_BUILDER_TEST;
    public static final GuiPredicate SPAWN_LOCATOR_TEST;
    public static final GuiPredicate SINGLE_BIOME_TEST;
    public static final GuiPredicate REPLACE_OCEAN_TEST;
    public static final GuiPredicate REPLACE_BEACH_TEST;
    public static final GuiPredicate SEA_LEVEL_TEST;
    public static final GuiPredicate CAVE_WIDTH_TEST;
    public static final GuiPredicate CAVE_HEIGHT_TEST;
    public static final GuiPredicate CAVE_COUNT_TEST;
    public static final GuiPredicate CAVE_CHANCE_TEST;
    public static final GuiPredicate USE_STRONGHOLDS_TEST;
    public static final GuiPredicate USE_VILLAGES_TEST;
    public static final GuiPredicate USE_VILLAGE_VARIANTS_TEST;
    public static final GuiPredicate USE_TEMPLES_TEST;
    public static final GuiPredicate USE_MONUMENTS_TEST;
    public static final GuiPredicate USE_MANSIONS_TEST;
    public static final GuiPredicate DUNGEON_CHANCE_TEST;
    public static final GuiPredicate WATER_LAKE_CHANCE_TEST;
    public static final GuiPredicate LAVA_LAKE_CHANCE_TEST;
    public static final GuiPredicate USE_SANDSTONE_TEST;
    public static final GuiPredicate USE_OLD_NETHER_TEST;
    public static final GuiPredicate USE_NETHER_CAVES_TEST;
    public static final GuiPredicate USE_FORTRESSES_TEST;
    public static final GuiPredicate USE_LAVA_POCKETS_TEST;
    
    public static final GuiPredicate LEVEL_THEME_TEST;
    public static final GuiPredicate LEVEL_TYPE_TEST;
    public static final GuiPredicate LEVEL_WIDTH_TEST;
    public static final GuiPredicate LEVEL_LENGTH_TEST;
    public static final GuiPredicate LEVEL_HEIGHT_TEST;
    public static final GuiPredicate LEVEL_HOUSE_TEST;
    public static final GuiPredicate USE_INDEV_CAVES_TEST;
    public static final GuiPredicate LEVEL_CAVE_WIDTH_TEST;
    public static final GuiPredicate USE_INFDEV_WALLS_TEST;
    public static final GuiPredicate USE_INFDEV_PYRAMIDS_TEST;
    public static final GuiPredicate RIVER_SIZE_TEST;
    public static final GuiPredicate LAYER_SIZE_TEST;
    public static final GuiPredicate LAYER_TYPE_TEST;
    
    public static final GuiPredicate USE_TALL_GRASS_TEST;
    public static final GuiPredicate USE_NEW_FLOWERS_TEST;
    public static final GuiPredicate USE_LILY_PADS_TEST;
    public static final GuiPredicate USE_MELONS_TEST;
    public static final GuiPredicate USE_DESERT_WELLS_TEST;
    public static final GuiPredicate USE_FOSSILS_TEST;
    public static final GuiPredicate USE_BIRCH_TREES_TEST;
    public static final GuiPredicate USE_PINE_TREES_TEST;
    public static final GuiPredicate USE_SWAMP_TREES_TEST;
    public static final GuiPredicate USE_JUNGLE_TREES_TEST;
    public static final GuiPredicate USE_ACACIA_TREES_TEST;
    public static final GuiPredicate SPAWN_NEW_CREATURE_MOBS_TEST;
    public static final GuiPredicate SPAWN_NEW_MONSTER_MOBS_TEST;
    public static final GuiPredicate SPAWN_WATER_MOBS_TEST;
    public static final GuiPredicate SPAWN_AMBIENT_MOBS_TEST;
    public static final GuiPredicate SPAWN_WOLVES_TEST;
    public static final GuiPredicate USE_MODDED_BIOMES_TEST;
    public static final GuiPredicate BIOME_SIZE_TEST;
    
    public static final GuiPredicate CLAY_SIZE_TEST;
    public static final GuiPredicate CLAY_COUNT_TEST;
    public static final GuiPredicate CLAY_MIN_HEIGHT_TEST;
    public static final GuiPredicate CLAY_MAX_HEIGHT_TEST;
    public static final GuiPredicate EMERALD_SIZE_TEST;
    public static final GuiPredicate EMERALD_COUNT_TEST;
    public static final GuiPredicate EMERALD_MIN_HEIGHT_TEST;
    public static final GuiPredicate EMERALD_MAX_HEIGHT_TEST;
    public static final GuiPredicate QUARTZ_SIZE_TEST;
    public static final GuiPredicate QUARTZ_COUNT_TEST;
    public static final GuiPredicate MAGMA_SIZE_TEST;
    public static final GuiPredicate MAGMA_COUNT_TEST;
    
    public static final GuiPredicate COORDINATE_SCALE_TEST;
    public static final GuiPredicate HEIGHT_SCALE_TEST;
    public static final GuiPredicate LOWER_LIMIT_SCALE_TEST;
    public static final GuiPredicate UPPER_LIMIT_SCALE_TEST;
    public static final GuiPredicate SCALE_NOISE_SCALE_X_TEST;
    public static final GuiPredicate SCALE_NOISE_SCALE_Z_TEST;
    public static final GuiPredicate DEPTH_NOISE_SCALE_X_TEST;
    public static final GuiPredicate DEPTH_NOISE_SCALE_Z_TEST;
    public static final GuiPredicate MAIN_NOISE_SCALE_X_TEST;
    public static final GuiPredicate MAIN_NOISE_SCALE_Y_TEST;
    public static final GuiPredicate MAIN_NOISE_SCALE_Z_TEST;
    public static final GuiPredicate BASE_SIZE_TEST;
    public static final GuiPredicate STRETCH_Y_TEST;
    public static final GuiPredicate HEIGHT_TEST;
    public static final GuiPredicate TEMP_NOISE_SCALE_TEST;
    public static final GuiPredicate RAIN_NOISE_SCALE_TEST;
    public static final GuiPredicate DETAIL_NOISE_SCALE_TEST;
    public static final GuiPredicate BIOME_DEPTH_WEIGHT_TEST;
    public static final GuiPredicate BIOME_DEPTH_OFFSET_TEST;
    public static final GuiPredicate BIOME_SCALE_WEIGHT_TEST;
    public static final GuiPredicate BIOME_SCALE_OFFSET_TEST;
    public static final GuiPredicate USE_BIOME_DEPTH_SCALE_TEST;
    public static final GuiPredicate END_ISLAND_WEIGHT_TEST;
    public static final GuiPredicate END_ISLAND_OFFSET_TEST;
    public static final GuiPredicate END_OUTER_ISLAND_DISTANCE_TEST;
    public static final GuiPredicate END_OUTER_ISLAND_OFFSET_TEST;
    public static final GuiPredicate USE_END_OUTER_ISLANDS_TEST;
    
    public static final GuiPredicate BASE_BIOME_TEST;
    public static final GuiPredicate OCEAN_BIOME_TEST;
    public static final GuiPredicate BEACH_BIOME_TEST;
    
    public static final GuiPredicate DEV_BIOME_PROP_TEST;
    
    private static boolean isChunkInstanceOf(ModernBetaGeneratorSettings settings, Class<?> clazz) {
        ChunkSource chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(settings.chunkSource).apply(0L, settings);
        
        return clazz.isAssignableFrom(chunkSource.getClass());
    }
    
    private static boolean isBiomeInstanceOf(ModernBetaGeneratorSettings settings, Class<?> clazz) {
        BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(settings.biomeSource).apply(0L, settings);
        
        return clazz.isAssignableFrom(biomeSource.getClass());
    }
    
    private static boolean isChunkEqualTo(ModernBetaGeneratorSettings settings, ModernBetaBuiltInTypes.Chunk type) {
        return settings.chunkSource.equals(type.getRegistryKey());
    }
    
    private static boolean isBiomeEqualTo(ModernBetaGeneratorSettings settings, ModernBetaBuiltInTypes.Biome type) {
        return settings.biomeSource.equals(type.getRegistryKey());
    }
    
    private static boolean isSurfaceEqualTo(ModernBetaGeneratorSettings settings, ModernBetaBuiltInTypes.Surface type) {
        return settings.surfaceBuilder.equals(type.getRegistryKey());
    }
    
    private static boolean isCarverEqualTo(ModernBetaGeneratorSettings settings, ModernBetaBuiltInTypes.Carver type) {
        return settings.caveCarver.equals(type.getRegistryKey());
    }
    
    private static boolean isCarverEnabled(ModernBetaGeneratorSettings settings) {
        return !settings.caveCarver.equals(ModernBetaBuiltInTypes.Carver.NONE.getRegistryKey());
    }
    
    private static boolean isFiniteChunk(ModernBetaGeneratorSettings settings) {
        ChunkSource chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(settings.chunkSource).apply(0L, settings);
        
        return chunkSource instanceof FiniteChunkSource;
    }
    
    private static boolean isSingleBiome(ModernBetaGeneratorSettings settings) {
        return isBiomeEqualTo(settings, ModernBetaBuiltInTypes.Biome.SINGLE);
    }
    
    private static boolean isModernBetaBiome(ModernBetaGeneratorSettings settings) {
        Biome biome = ForgeRegistries.BIOMES.getValue(settings.singleBiome);
        
        return biome instanceof ModernBetaBiome;
    }
    
    private static boolean isBetaBiome(ModernBetaGeneratorSettings settings) {
        Biome biome = ForgeRegistries.BIOMES.getValue(settings.singleBiome);
        
        return biome instanceof BiomeBeta;
    }
    
    private static boolean isBetaOrPEBiomeSource(ModernBetaGeneratorSettings settings) {
        return isBiomeEqualTo(settings, ModernBetaBuiltInTypes.Biome.BETA) || isBiomeEqualTo(settings, ModernBetaBuiltInTypes.Biome.PE);
    }
    
    private static boolean isBetaOrPESource(ModernBetaGeneratorSettings settings) {
        return 
            isChunkEqualTo(settings, ModernBetaBuiltInTypes.Chunk.BETA) ||
            isBiomeEqualTo(settings, ModernBetaBuiltInTypes.Biome.BETA) ||
            isChunkEqualTo(settings, ModernBetaBuiltInTypes.Chunk.PE) ||
            isBiomeEqualTo(settings, ModernBetaBuiltInTypes.Biome.PE);
    }
    
    private static boolean containsNoiseSetting(ModernBetaGeneratorSettings settings, int guiId) {
        ResourceLocation registryKey = settings.chunkSource;
        ChunkSource chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(registryKey).apply(0L, settings);
        
        if (!(chunkSource instanceof NoiseChunkSource)) {
            return false;
        }
        
        if (!NOISE_SETTINGS.containsKey(registryKey)) {
            return DEFAULT_NOISE_SETTINGS.contains(guiId);
        }
        
        return NOISE_SETTINGS.get(registryKey).contains(guiId);
    }

    static {
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.BETA.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_SCLE_NS_X,
                GuiIdentifiers.PG4_S_SCLE_NS_Z,
                GuiIdentifiers.PG4_S_DPTH_NS_X,
                GuiIdentifiers.PG4_S_DPTH_NS_Z,
                GuiIdentifiers.PG4_S_BASE_SIZE,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_STRETCH_Y,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_SCLE_NS_X,
                GuiIdentifiers.PG4_S_SCLE_NS_Z,
                GuiIdentifiers.PG4_S_DPTH_NS_X,
                GuiIdentifiers.PG4_S_DPTH_NS_Z,
                GuiIdentifiers.PG4_S_BASE_SIZE,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_STRETCH_Y,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_SCLE_NS_X,
                GuiIdentifiers.PG4_S_SCLE_NS_Z,
                GuiIdentifiers.PG4_S_DPTH_NS_X,
                GuiIdentifiers.PG4_S_DPTH_NS_Z,
                GuiIdentifiers.PG4_S_BASE_SIZE,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_STRETCH_Y,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_420.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_BASE_SIZE,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_STRETCH_Y,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_415.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_227.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_HEIGH_LIM
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.SKYLANDS.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM
            )
        );

        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.PE.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_SCLE_NS_X,
                GuiIdentifiers.PG4_S_SCLE_NS_Z,
                GuiIdentifiers.PG4_S_DPTH_NS_X,
                GuiIdentifiers.PG4_S_DPTH_NS_Z,
                GuiIdentifiers.PG4_S_BASE_SIZE,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_STRETCH_Y,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.RELEASE.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_DPTH_NS_X,
                GuiIdentifiers.PG4_S_DPTH_NS_Z,
                GuiIdentifiers.PG4_S_BASE_SIZE,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_STRETCH_Y,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM,
                
                GuiIdentifiers.PG4_S_B_DPTH_WT,
                GuiIdentifiers.PG4_S_B_DPTH_OF,
                GuiIdentifiers.PG4_S_B_SCLE_WT,
                GuiIdentifiers.PG4_S_B_SCLE_OF,
                
                GuiIdentifiers.PG4_B_USE_BDS
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.END.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG4_S_MAIN_NS_X,
                GuiIdentifiers.PG4_S_MAIN_NS_Y,
                GuiIdentifiers.PG4_S_MAIN_NS_Z,
                GuiIdentifiers.PG4_S_SCLE_NS_X,
                GuiIdentifiers.PG4_S_SCLE_NS_Z,
                GuiIdentifiers.PG4_S_COORD_SCL,
                GuiIdentifiers.PG4_S_HEIGH_SCL,
                GuiIdentifiers.PG4_S_UPPER_LIM,
                GuiIdentifiers.PG4_S_LOWER_LIM,
                GuiIdentifiers.PG4_S_HEIGH_LIM,
                GuiIdentifiers.PG4_S_END_WT,
                GuiIdentifiers.PG4_S_END_OF,
                GuiIdentifiers.PG4_B_USE_END_OUT,
                GuiIdentifiers.PG4_S_END_OUT_DT,
                GuiIdentifiers.PG4_S_END_OUT_OF
            )
        );
        
        SURFACE_BUILDER_TEST = new GuiPredicate(
            settings -> {
                boolean isFloating = isChunkInstanceOf(settings, SkylandsChunkSource.class);

                return !isFloating && !isFiniteChunk(settings);
            },
            GuiIdentifiers.PG0_S_SURFACE, GuiIdentifiers.PG0_B_SURFACE
        );
        SPAWN_LOCATOR_TEST = new GuiPredicate(settings -> !isFiniteChunk(settings), GuiIdentifiers.PG0_S_SPAWN, GuiIdentifiers.PG0_B_SPAWN);
        SINGLE_BIOME_TEST = new GuiPredicate(settings -> isSingleBiome(settings), GuiIdentifiers.PG0_B_FIXED);
        REPLACE_OCEAN_TEST = new GuiPredicate(settings -> isBiomeInstanceOf(settings, BiomeResolverOcean.class), GuiIdentifiers.PG0_B_USE_OCEAN);
        REPLACE_BEACH_TEST = new GuiPredicate(settings -> isBiomeInstanceOf(settings, BiomeResolverBeach.class), GuiIdentifiers.PG0_B_USE_BEACH);
        SEA_LEVEL_TEST = new GuiPredicate(SURFACE_BUILDER_TEST::test, GuiIdentifiers.PG0_S_SEA_LEVEL);
        CAVE_WIDTH_TEST = new GuiPredicate(settings -> !isCarverEqualTo(settings, ModernBetaBuiltInTypes.Carver.RELEASE) && isCarverEnabled(settings), GuiIdentifiers.PG0_S_CAVE_WIDTH);
        CAVE_HEIGHT_TEST = new GuiPredicate(CAVE_WIDTH_TEST::test, GuiIdentifiers.PG0_S_CAVE_HEIGHT);
        CAVE_COUNT_TEST = new GuiPredicate(CAVE_WIDTH_TEST::test, GuiIdentifiers.PG0_S_CAVE_COUNT);
        CAVE_CHANCE_TEST = new GuiPredicate(CAVE_WIDTH_TEST::test, GuiIdentifiers.PG0_S_CAVE_CHANCE);
        USE_STRONGHOLDS_TEST = new GuiPredicate(
            settings -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(settings.singleBiome);
    
                return isSingleBiome(settings) ? STRONGHOLD.allowedBiomes.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_HOLDS
        );
        USE_VILLAGES_TEST = new GuiPredicate(
            settings -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(settings.singleBiome);
    
                return isSingleBiome(settings) ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_VILLAGES
        );
        USE_VILLAGE_VARIANTS_TEST = new GuiPredicate(
            settings -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(settings.singleBiome);
                boolean hasVillages = isSingleBiome(settings) ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
    
                return hasVillages && settings.useVillages; 
            },
            GuiIdentifiers.PG0_B_USE_VILLAGE_VARIANTS
        );
        USE_TEMPLES_TEST = new GuiPredicate(
            settings -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(settings.singleBiome);
    
                return isSingleBiome(settings) ? MapGenScatteredFeature.BIOMELIST.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_TEMPLES
        );
        USE_MONUMENTS_TEST = new GuiPredicate(
            settings -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(settings.singleBiome);
    
                return isSingleBiome(settings) ? StructureOceanMonument.SPAWN_BIOMES.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_MONUMENTS
        );
        USE_MANSIONS_TEST = new GuiPredicate(
            settings -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(settings.singleBiome);
    
                return isSingleBiome(settings) ? WoodlandMansion.ALLOWED_BIOMES.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_MANSIONS
        );
        DUNGEON_CHANCE_TEST = new GuiPredicate(settings -> settings.useDungeons, GuiIdentifiers.PG0_S_DUNGEON_CHANCE);
        WATER_LAKE_CHANCE_TEST = new GuiPredicate(settings -> settings.useWaterLakes, GuiIdentifiers.PG0_S_WATER_LAKE_CHANCE);
        LAVA_LAKE_CHANCE_TEST = new GuiPredicate(settings -> settings.useLavaLakes, GuiIdentifiers.PG0_S_LAVA_LAKE_CHANCE);
        USE_SANDSTONE_TEST = new GuiPredicate(
            settings -> {
                boolean isReleaseSurface = isSurfaceEqualTo(settings, ModernBetaBuiltInTypes.Surface.RELEASE);
                
                return !isReleaseSurface && !isFiniteChunk(settings);
            },
            GuiIdentifiers.PG0_B_USE_SANDSTONE
        );
        USE_OLD_NETHER_TEST = new GuiPredicate(settings -> ModCompat.isNetherCompatible(), GuiIdentifiers.PG0_B_USE_OLD_NETHER);
        USE_NETHER_CAVES_TEST = new GuiPredicate(settings -> settings.useOldNether && ModCompat.isNetherCompatible(), GuiIdentifiers.PG0_B_USE_NETHER_CAVES);
        USE_FORTRESSES_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG0_B_USE_FORTRESSES);
        USE_LAVA_POCKETS_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG0_B_USE_LAVA_POCKETS);
        LEVEL_THEME_TEST = new GuiPredicate(settings -> isChunkEqualTo(settings, ModernBetaBuiltInTypes.Chunk.INDEV), GuiIdentifiers.PG1_S_LEVEL_THEME);
        LEVEL_TYPE_TEST = new GuiPredicate(LEVEL_THEME_TEST::test, GuiIdentifiers.PG1_S_LEVEL_TYPE);
        LEVEL_WIDTH_TEST = new GuiPredicate(settings -> isFiniteChunk(settings), GuiIdentifiers.PG1_S_LEVEL_WIDTH);
        LEVEL_LENGTH_TEST = new GuiPredicate(LEVEL_WIDTH_TEST::test, GuiIdentifiers.PG1_S_LEVEL_LENGTH);
        LEVEL_HEIGHT_TEST = new GuiPredicate(LEVEL_WIDTH_TEST::test, GuiIdentifiers.PG1_S_LEVEL_HEIGHT);
        LEVEL_HOUSE_TEST = new GuiPredicate(LEVEL_WIDTH_TEST::test, GuiIdentifiers.PG1_S_LEVEL_HOUSE);
        USE_INDEV_CAVES_TEST = new GuiPredicate(LEVEL_WIDTH_TEST::test, GuiIdentifiers.PG1_B_USE_INDEV_CAVES);
        LEVEL_CAVE_WIDTH_TEST = new GuiPredicate(settings -> isFiniteChunk(settings) && settings.useIndevCaves, GuiIdentifiers.PG1_S_LEVEL_CAVE_WIDTH);
        USE_INFDEV_WALLS_TEST = new GuiPredicate(settings -> isChunkEqualTo(settings, ModernBetaBuiltInTypes.Chunk.INFDEV_227), GuiIdentifiers.PG1_B_USE_INFDEV_WALLS);
        USE_INFDEV_PYRAMIDS_TEST = new GuiPredicate(USE_INFDEV_WALLS_TEST::test, GuiIdentifiers.PG1_B_USE_INFDEV_PYRAMIDS);
        RIVER_SIZE_TEST = new GuiPredicate(
            settings -> isChunkEqualTo(settings, ModernBetaBuiltInTypes.Chunk.RELEASE) && !isBiomeInstanceOf(settings, NoiseBiomeSource.class),
            GuiIdentifiers.PG1_S_RIVER_SZ
        );
        LAYER_SIZE_TEST = new GuiPredicate(RIVER_SIZE_TEST::test, GuiIdentifiers.PG1_S_LAYER_SZ);
        LAYER_TYPE_TEST = new GuiPredicate(RIVER_SIZE_TEST::test,GuiIdentifiers.PG1_S_LAYER_TYPE);
        
        USE_TALL_GRASS_TEST = new GuiPredicate(
            settings -> {
                boolean isBetaPEBiomeSource = isBetaOrPEBiomeSource(settings);
                boolean isFixedBiomeSource = isSingleBiome(settings);
    
                return isBetaPEBiomeSource || isFixedBiomeSource && isModernBetaBiome(settings);
            },
            GuiIdentifiers.PG2_B_USE_GRASS
        );
        USE_NEW_FLOWERS_TEST = new GuiPredicate(
            settings -> {
                boolean isBetaPEBiomeSource = isBetaOrPEBiomeSource(settings);
                boolean isFixedBiomeSource = isSingleBiome(settings);
    
                return isBetaPEBiomeSource || isFixedBiomeSource && isBetaBiome(settings);
            },
            GuiIdentifiers.PG2_B_USE_FLOWERS
        );
        USE_LILY_PADS_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG2_B_USE_PADS);
        USE_MELONS_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG2_B_USE_MELONS);
        USE_DESERT_WELLS_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG2_B_USE_WELLS);
        USE_FOSSILS_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG2_B_USE_FOSSILS);
        USE_BIRCH_TREES_TEST = new GuiPredicate(
            settings -> {
                boolean isBetaPEBiomeSource = isBetaOrPEBiomeSource(settings);
                boolean isFixedBiomeSource = isSingleBiome(settings);
                boolean isDynamicTreesLoaded = ModCompat.isModLoaded(ModCompat.MOD_DYNAMIC_TREES);
                
                return (!isDynamicTreesLoaded || isDynamicTreesLoaded && !CompatDynamicTrees.isEnabled()) && (isBetaPEBiomeSource || isFixedBiomeSource && isBetaBiome(settings));
            },
            GuiIdentifiers.PG2_B_USE_BIRCH
        );
        USE_PINE_TREES_TEST = new GuiPredicate(USE_BIRCH_TREES_TEST::test, GuiIdentifiers.PG2_B_USE_PINE);
        USE_SWAMP_TREES_TEST = new GuiPredicate(USE_BIRCH_TREES_TEST::test, GuiIdentifiers.PG2_B_USE_SWAMP);
        USE_JUNGLE_TREES_TEST = new GuiPredicate(USE_BIRCH_TREES_TEST::test, GuiIdentifiers.PG2_B_USE_JUNGLE);
        USE_ACACIA_TREES_TEST = new GuiPredicate(USE_BIRCH_TREES_TEST::test, GuiIdentifiers.PG2_B_USE_ACACIA);
        SPAWN_NEW_CREATURE_MOBS_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_B_SPAWN_CREATURE);
        SPAWN_NEW_MONSTER_MOBS_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_B_SPAWN_MONSTER);
        SPAWN_WATER_MOBS_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_B_SPAWN_WATER);
        SPAWN_AMBIENT_MOBS_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_B_SPAWN_AMBIENT);
        SPAWN_WOLVES_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_B_SPAWN_WOLVES);
        USE_MODDED_BIOMES_TEST = new GuiPredicate(
            settings -> isBiomeEqualTo(settings, ModernBetaBuiltInTypes.Biome.RELEASE),
            GuiIdentifiers.PG2_B_USE_MODDED_BIOMES
        );
        BIOME_SIZE_TEST = new GuiPredicate(
            settings -> isBiomeEqualTo(settings, ModernBetaBuiltInTypes.Biome.RELEASE),
            GuiIdentifiers.PG2_S_BIOME_SZ
        );
        
        CLAY_SIZE_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG3_S_CLAY_SIZE);
        CLAY_COUNT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG3_S_CLAY_CNT);
        CLAY_MIN_HEIGHT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG3_S_CLAY_MIN);
        CLAY_MAX_HEIGHT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG3_S_CLAY_MAX);
        EMERALD_SIZE_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG3_S_EMER_SIZE);
        EMERALD_COUNT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG3_S_EMER_CNT);
        EMERALD_MIN_HEIGHT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG3_S_EMER_MIN);
        EMERALD_MAX_HEIGHT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG3_S_EMER_MAX);
        QUARTZ_SIZE_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG3_S_QRTZ_SIZE);
        QUARTZ_COUNT_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG3_S_QRTZ_CNT);
        MAGMA_SIZE_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG3_S_MGMA_SIZE);
        MAGMA_COUNT_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG3_S_MGMA_CNT);
        
        COORDINATE_SCALE_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_COORD_SCL), GuiIdentifiers.PG4_S_COORD_SCL, GuiIdentifiers.PG5_F_COORD_SCL);
        HEIGHT_SCALE_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_HEIGH_SCL), GuiIdentifiers.PG4_S_HEIGH_SCL, GuiIdentifiers.PG5_F_HEIGH_SCL);
        LOWER_LIMIT_SCALE_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_LOWER_LIM), GuiIdentifiers.PG4_S_LOWER_LIM, GuiIdentifiers.PG5_F_LOWER_LIM);
        UPPER_LIMIT_SCALE_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_UPPER_LIM), GuiIdentifiers.PG4_S_UPPER_LIM, GuiIdentifiers.PG5_F_UPPER_LIM);
        SCALE_NOISE_SCALE_X_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_SCLE_NS_X), GuiIdentifiers.PG4_S_SCLE_NS_X, GuiIdentifiers.PG5_F_SCLE_NS_X);
        SCALE_NOISE_SCALE_Z_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_SCLE_NS_Z), GuiIdentifiers.PG4_S_SCLE_NS_Z, GuiIdentifiers.PG5_F_SCLE_NS_Z);
        DEPTH_NOISE_SCALE_X_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_DPTH_NS_X), GuiIdentifiers.PG4_S_DPTH_NS_X, GuiIdentifiers.PG5_F_DPTH_NS_X);
        DEPTH_NOISE_SCALE_Z_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_DPTH_NS_Z), GuiIdentifiers.PG4_S_DPTH_NS_Z, GuiIdentifiers.PG5_F_DPTH_NS_Z);
        MAIN_NOISE_SCALE_X_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_MAIN_NS_X), GuiIdentifiers.PG4_S_MAIN_NS_X, GuiIdentifiers.PG5_F_MAIN_NS_X);
        MAIN_NOISE_SCALE_Y_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_MAIN_NS_Y), GuiIdentifiers.PG4_S_MAIN_NS_Y, GuiIdentifiers.PG5_F_MAIN_NS_Y);
        MAIN_NOISE_SCALE_Z_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_MAIN_NS_Z), GuiIdentifiers.PG4_S_MAIN_NS_Z, GuiIdentifiers.PG5_F_MAIN_NS_Z);
        BASE_SIZE_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_BASE_SIZE), GuiIdentifiers.PG4_S_BASE_SIZE, GuiIdentifiers.PG5_F_BASE_SIZE);
        STRETCH_Y_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_STRETCH_Y), GuiIdentifiers.PG4_S_STRETCH_Y, GuiIdentifiers.PG5_F_STRETCH_Y);
        HEIGHT_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_HEIGH_LIM), GuiIdentifiers.PG4_S_HEIGH_LIM, GuiIdentifiers.PG5_F_HEIGH_LIM);
        TEMP_NOISE_SCALE_TEST = new GuiPredicate(settings -> isBetaOrPESource(settings), GuiIdentifiers.PG4_S_TEMP_SCL, GuiIdentifiers.PG5_F_TEMP_SCL);
        RAIN_NOISE_SCALE_TEST = new GuiPredicate(TEMP_NOISE_SCALE_TEST::test, GuiIdentifiers.PG4_S_RAIN_SCL, GuiIdentifiers.PG5_F_RAIN_SCL);
        DETAIL_NOISE_SCALE_TEST = new GuiPredicate(TEMP_NOISE_SCALE_TEST::test, GuiIdentifiers.PG4_S_DETL_SCL, GuiIdentifiers.PG5_F_DETL_SCL);
        BIOME_DEPTH_WEIGHT_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_B_DPTH_WT), GuiIdentifiers.PG4_S_B_DPTH_WT, GuiIdentifiers.PG5_F_B_DPTH_WT);
        BIOME_DEPTH_OFFSET_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_B_DPTH_OF), GuiIdentifiers.PG4_S_B_DPTH_OF, GuiIdentifiers.PG5_F_B_DPTH_OF);
        BIOME_SCALE_WEIGHT_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_B_SCLE_WT), GuiIdentifiers.PG4_S_B_SCLE_WT, GuiIdentifiers.PG5_F_B_SCLE_WT);
        BIOME_SCALE_OFFSET_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_B_SCLE_OF), GuiIdentifiers.PG4_S_B_SCLE_OF, GuiIdentifiers.PG5_F_B_SCLE_OF);
        USE_BIOME_DEPTH_SCALE_TEST = new GuiPredicate(
            settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_B_USE_BDS) && !isBiomeInstanceOf(settings, NoiseBiomeSource.class),
            GuiIdentifiers.PG4_B_USE_BDS
        );
        END_ISLAND_WEIGHT_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_END_WT), GuiIdentifiers.PG4_S_END_WT, GuiIdentifiers.PG5_F_END_WT);
        END_ISLAND_OFFSET_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_END_OF), GuiIdentifiers.PG4_S_END_OF, GuiIdentifiers.PG5_F_END_OF);
        USE_END_OUTER_ISLANDS_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_B_USE_END_OUT), GuiIdentifiers.PG4_B_USE_END_OUT);
        END_OUTER_ISLAND_DISTANCE_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_END_OUT_DT) && settings.useEndOuterIslands, GuiIdentifiers.PG4_S_END_OUT_DT, GuiIdentifiers.PG5_F_END_OUT_DT);
        END_OUTER_ISLAND_OFFSET_TEST = new GuiPredicate(settings -> containsNoiseSetting(settings, GuiIdentifiers.PG4_S_END_OUT_OF) && settings.useEndOuterIslands, GuiIdentifiers.PG4_S_END_OUT_OF, GuiIdentifiers.PG5_F_END_OUT_OF);
        
        BASE_BIOME_TEST = new GuiPredicate(
            settings -> isBetaOrPEBiomeSource(settings),
            GuiIdentifiers.PG6_DSRT_LAND,
            GuiIdentifiers.PG6_FRST_LAND,
            GuiIdentifiers.PG6_ICED_LAND,
            GuiIdentifiers.PG6_PLNS_LAND,
            GuiIdentifiers.PG6_RAIN_LAND,
            GuiIdentifiers.PG6_SAVA_LAND,
            GuiIdentifiers.PG6_SEAS_LAND,
            GuiIdentifiers.PG6_SHRB_LAND,
            GuiIdentifiers.PG6_SWMP_LAND,
            GuiIdentifiers.PG6_TAIG_LAND,
            GuiIdentifiers.PG6_TUND_LAND
        );
        OCEAN_BIOME_TEST = new GuiPredicate(
            settings -> isBetaOrPEBiomeSource(settings) && settings.replaceOceanBiomes,
            GuiIdentifiers.PG6_DSRT_OCEAN,
            GuiIdentifiers.PG6_FRST_OCEAN,
            GuiIdentifiers.PG6_ICED_OCEAN,
            GuiIdentifiers.PG6_PLNS_OCEAN,
            GuiIdentifiers.PG6_RAIN_OCEAN,
            GuiIdentifiers.PG6_SAVA_OCEAN,
            GuiIdentifiers.PG6_SEAS_OCEAN,
            GuiIdentifiers.PG6_SHRB_OCEAN,
            GuiIdentifiers.PG6_SWMP_OCEAN,
            GuiIdentifiers.PG6_TAIG_OCEAN,
            GuiIdentifiers.PG6_TUND_OCEAN
        );
        BEACH_BIOME_TEST = new GuiPredicate(
            settings -> isBetaOrPEBiomeSource(settings) && settings.replaceBeachBiomes,
            GuiIdentifiers.PG6_DSRT_BEACH,
            GuiIdentifiers.PG6_FRST_BEACH,
            GuiIdentifiers.PG6_ICED_BEACH,
            GuiIdentifiers.PG6_PLNS_BEACH,
            GuiIdentifiers.PG6_RAIN_BEACH,
            GuiIdentifiers.PG6_SAVA_BEACH,
            GuiIdentifiers.PG6_SEAS_BEACH,
            GuiIdentifiers.PG6_SHRB_BEACH,
            GuiIdentifiers.PG6_SWMP_BEACH,
            GuiIdentifiers.PG6_TAIG_BEACH,
            GuiIdentifiers.PG6_TUND_BEACH
        );
        DEV_BIOME_PROP_TEST = new GuiPredicate(settings -> {
            ResourceLocation registryKey = ModernBeta.createRegistryKey("booleanProp");
            
            if (settings.containsProperty(registryKey)) {
                return settings.getBooleanProperty(registryKey);
            }
            
            return true;
        });
    }
}
