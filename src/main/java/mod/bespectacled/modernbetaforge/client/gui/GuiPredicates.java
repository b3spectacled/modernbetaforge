package mod.bespectacled.modernbetaforge.client.gui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.NoiseBiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings.Factory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class GuiPredicates {
    private static final Map<ResourceLocation, List<Integer>> NOISE_SETTINGS = new LinkedHashMap<>();
    private static final MapGenStronghold STRONGHOLD = new MapGenStronghold();
    
    public static final GuiPredicate SURFACE_BUILDER_TEST;
    public static final GuiPredicate CAVE_CARVER_TEST;
    public static final GuiPredicate SINGLE_BIOME_TEST;
    public static final GuiPredicate REPLACE_OCEAN_TEST;
    public static final GuiPredicate REPLACE_BEACH_TEST;
    public static final GuiPredicate SEA_LEVEL_TEST;
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
    public static final GuiPredicate USE_INFDEV_WALLS_TEST;
    public static final GuiPredicate USE_INFDEV_PYRAMIDS_TEST;
    
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
    public static final GuiPredicate BIOME_SIZE_TEST;
    public static final GuiPredicate RIVER_SIZE_TEST;
    public static final GuiPredicate USE_BIOME_DEPTH_SCALE_TEST;
    
    public static final GuiPredicate BASE_BIOME_TEST;
    public static final GuiPredicate OCEAN_BIOME_TEST;
    public static final GuiPredicate BEACH_BIOME_TEST;
    
    private static boolean isChunkEqualTo(Factory factory, ModernBetaBuiltInTypes.Chunk type) {
        return factory.chunkSource.equals(type.getRegistryString());
    }
    
    private static boolean isBiomeEqualTo(Factory factory, ModernBetaBuiltInTypes.Biome type) {
        return factory.biomeSource.equals(type.getRegistryString());
    }
    
    private static boolean isSurfaceEqualTo(Factory factory, ModernBetaBuiltInTypes.Surface type) {
        return factory.surfaceBuilder.equals(type.getRegistryString());
    }
    
    private static boolean isCarverEqualTo(Factory factory, ModernBetaBuiltInTypes.Carver type) {
        return factory.caveCarver.equals(type.getRegistryString());
    }
    
    private static boolean isFiniteChunk(Factory factory) {
        ChunkSource chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(new ResourceLocation(factory.chunkSource)).apply(0L, factory.build());
        
        return chunkSource instanceof FiniteChunkSource;
    }
    
    private static boolean isSingleBiome(Factory factory) {
        return isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.SINGLE);
    }
    
    private static boolean isModernBetaBiome(Factory factory) {
        Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
        
        return biome instanceof ModernBetaBiome;
    }
    
    private static boolean isBetaBiome(Factory factory) {
        Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
        
        return biome instanceof BiomeBeta;
    }
    
    private static boolean isBetaOrPEBiomeSource(Factory factory) {
        return isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.BETA) || isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.PE);
    }
    
    private static boolean isBetaOrPESource(Factory factory) {
        return 
            isChunkEqualTo(factory, ModernBetaBuiltInTypes.Chunk.BETA) ||
            isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.BETA) ||
            isChunkEqualTo(factory, ModernBetaBuiltInTypes.Chunk.PE) ||
            isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.PE);
    }
    
    private static boolean containsNoiseSetting(Factory factory, int guiId) {
        ResourceLocation chunkSource = new ResourceLocation(factory.chunkSource);
        
        if (!NOISE_SETTINGS.containsKey(chunkSource)) {
            return false;
        }
        
        return NOISE_SETTINGS.get(chunkSource).contains(guiId);
    }

    static {
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.BETA.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_SCLE_NS_X,
                GuiIdentifiers.PG3_S_SCLE_NS_Z,
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
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_SCLE_NS_X,
                GuiIdentifiers.PG3_S_SCLE_NS_Z,
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
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_SCLE_NS_X,
                GuiIdentifiers.PG3_S_SCLE_NS_Z,
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
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_420.getRegistryKey(),
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
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_415.getRegistryKey(),
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
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.INFDEV_227.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG3_S_HEIGH_LIM
            )
        );
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.SKYLANDS.getRegistryKey(),
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

        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.PE.getRegistryKey(),
            ImmutableList.of(
                GuiIdentifiers.PG3_S_MAIN_NS_X,
                GuiIdentifiers.PG3_S_MAIN_NS_Y,
                GuiIdentifiers.PG3_S_MAIN_NS_Z,
                GuiIdentifiers.PG3_S_SCLE_NS_X,
                GuiIdentifiers.PG3_S_SCLE_NS_Z,
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
        
        NOISE_SETTINGS.put(
            ModernBetaBuiltInTypes.Chunk.RELEASE.getRegistryKey(),
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
                GuiIdentifiers.PG3_S_B_SCLE_WT,
                GuiIdentifiers.PG3_S_B_SCLE_OF,
                GuiIdentifiers.PG3_S_BIOME_SZ,
                GuiIdentifiers.PG3_S_RIVER_SZ,
                
                GuiIdentifiers.PG3_B_USE_BDS
            )
        );
        
        SURFACE_BUILDER_TEST = new GuiPredicate(
            factory -> {
                boolean isSkylands = isChunkEqualTo(factory, ModernBetaBuiltInTypes.Chunk.SKYLANDS);

                return !isSkylands && !isFiniteChunk(factory);
            },
            GuiIdentifiers.PG0_S_SURFACE, GuiIdentifiers.PG0_B_SURFACE
        );
        CAVE_CARVER_TEST = new GuiPredicate(factory -> factory.useCaves, GuiIdentifiers.PG0_S_CARVER, GuiIdentifiers.PG0_B_CARVER);
        SINGLE_BIOME_TEST = new GuiPredicate(factory -> isSingleBiome(factory), GuiIdentifiers.PG0_B_FIXED);
        REPLACE_OCEAN_TEST = new GuiPredicate(
            factory -> {
                BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(new ResourceLocation(factory.biomeSource)).apply(0L, factory.build());
    
                return biomeSource instanceof BiomeResolverOcean;
            },
            GuiIdentifiers.PG0_B_USE_OCEAN
        );
        REPLACE_BEACH_TEST = new GuiPredicate(
            factory -> {
                BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(new ResourceLocation(factory.biomeSource)).apply(0L, factory.build());
    
                return biomeSource instanceof BiomeResolverBeach;
            },
            GuiIdentifiers.PG0_B_USE_BEACH
        );
        SEA_LEVEL_TEST = new GuiPredicate(SURFACE_BUILDER_TEST::test, GuiIdentifiers.PG0_S_SEA_LEVEL);
        CAVE_HEIGHT_TEST = new GuiPredicate(factory -> !isCarverEqualTo(factory, ModernBetaBuiltInTypes.Carver.RELEASE) && factory.useCaves, GuiIdentifiers.PG0_S_CAVE_HEIGHT);
        CAVE_COUNT_TEST = new GuiPredicate(CAVE_HEIGHT_TEST::test, GuiIdentifiers.PG0_S_CAVE_COUNT);
        CAVE_CHANCE_TEST = new GuiPredicate(CAVE_HEIGHT_TEST::test, GuiIdentifiers.PG0_S_CAVE_CHANCE);
        USE_STRONGHOLDS_TEST = new GuiPredicate(
            factory -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
    
                return isSingleBiome(factory) ? STRONGHOLD.allowedBiomes.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_HOLDS
        );
        USE_VILLAGES_TEST = new GuiPredicate(
            factory -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
    
                return isSingleBiome(factory) ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_VILLAGES
        );
        USE_VILLAGE_VARIANTS_TEST = new GuiPredicate(
            factory -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
                boolean hasVillages = isSingleBiome(factory) ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
    
                return hasVillages && factory.useVillages; 
            },
            GuiIdentifiers.PG0_B_USE_VILLAGE_VARIANTS
        );
        USE_TEMPLES_TEST = new GuiPredicate(
            factory -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
    
                return isSingleBiome(factory) ? MapGenScatteredFeature.BIOMELIST.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_TEMPLES
        );
        USE_MONUMENTS_TEST = new GuiPredicate(
            factory -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
    
                return isSingleBiome(factory) ? StructureOceanMonument.SPAWN_BIOMES.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_MONUMENTS
        );
        USE_MANSIONS_TEST = new GuiPredicate(
            factory -> {
                Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
    
                return isSingleBiome(factory) ? WoodlandMansion.ALLOWED_BIOMES.contains(biome) : true;
            },
            GuiIdentifiers.PG0_B_USE_MANSIONS
        );
        DUNGEON_CHANCE_TEST = new GuiPredicate(factory -> factory.useDungeons, GuiIdentifiers.PG0_S_DUNGEON_CHANCE);
        WATER_LAKE_CHANCE_TEST = new GuiPredicate(factory -> factory.useWaterLakes, GuiIdentifiers.PG0_S_WATER_LAKE_CHANCE);
        LAVA_LAKE_CHANCE_TEST = new GuiPredicate(factory -> factory.useLavaLakes, GuiIdentifiers.PG0_S_LAVA_LAKE_CHANCE);
        USE_SANDSTONE_TEST = new GuiPredicate(
            factory -> {
                boolean isReleaseSurface = isSurfaceEqualTo(factory, ModernBetaBuiltInTypes.Surface.RELEASE);
                
                return !isReleaseSurface && !isFiniteChunk(factory);
            },
            GuiIdentifiers.PG0_B_USE_SANDSTONE
        );
        USE_OLD_NETHER_TEST = new GuiPredicate(factory -> !ModCompat.isBoPLoaded(), GuiIdentifiers.PG0_B_USE_OLD_NETHER);
        USE_NETHER_CAVES_TEST = new GuiPredicate(factory -> factory.useOldNether && !ModCompat.isBoPLoaded(), GuiIdentifiers.PG0_B_USE_NETHER_CAVES);
        USE_FORTRESSES_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG0_B_USE_FORTRESSES);
        USE_LAVA_POCKETS_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG0_B_USE_LAVA_POCKETS);
        LEVEL_THEME_TEST = new GuiPredicate(factory -> isChunkEqualTo(factory, ModernBetaBuiltInTypes.Chunk.INDEV), GuiIdentifiers.PG0_S_LEVEL_THEME);
        LEVEL_TYPE_TEST = new GuiPredicate(LEVEL_THEME_TEST::test, GuiIdentifiers.PG0_S_LEVEL_TYPE);
        LEVEL_WIDTH_TEST = new GuiPredicate(factory -> isFiniteChunk(factory), GuiIdentifiers.PG0_S_LEVEL_WIDTH);
        LEVEL_LENGTH_TEST = new GuiPredicate(LEVEL_WIDTH_TEST::test, GuiIdentifiers.PG0_S_LEVEL_LENGTH);
        LEVEL_HEIGHT_TEST = new GuiPredicate(LEVEL_WIDTH_TEST::test, GuiIdentifiers.PG0_S_LEVEL_HEIGHT);
        LEVEL_HOUSE_TEST = new GuiPredicate(LEVEL_WIDTH_TEST::test, GuiIdentifiers.PG0_S_LEVEL_HOUSE);
        USE_INDEV_CAVES_TEST = new GuiPredicate(LEVEL_WIDTH_TEST::test, GuiIdentifiers.PG0_B_USE_INDEV_CAVES);
        USE_INFDEV_WALLS_TEST = new GuiPredicate(factory -> isChunkEqualTo(factory, ModernBetaBuiltInTypes.Chunk.INFDEV_227), GuiIdentifiers.PG0_B_USE_INFDEV_WALLS);
        USE_INFDEV_PYRAMIDS_TEST = new GuiPredicate(USE_INFDEV_WALLS_TEST::test, GuiIdentifiers.PG0_B_USE_INFDEV_PYRAMIDS);
        USE_TALL_GRASS_TEST = new GuiPredicate(
            factory -> {
                boolean isBetaPEBiomeSource = isBetaOrPEBiomeSource(factory);
                boolean isFixedBiomeSource = isSingleBiome(factory);
    
                return isBetaPEBiomeSource || isFixedBiomeSource && isModernBetaBiome(factory);
            },
            GuiIdentifiers.PG1_B_USE_GRASS
        );
        USE_NEW_FLOWERS_TEST = new GuiPredicate(
            factory -> {
                boolean isBetaPEBiomeSource = isBetaOrPEBiomeSource(factory);
                boolean isFixedBiomeSource = isSingleBiome(factory);
    
                return isBetaPEBiomeSource || isFixedBiomeSource && isBetaBiome(factory);
            },
            GuiIdentifiers.PG1_B_USE_FLOWERS
        );
        USE_LILY_PADS_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_PADS);
        USE_MELONS_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_MELONS);
        USE_DESERT_WELLS_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_WELLS);
        USE_FOSSILS_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_FOSSILS);
        USE_BIRCH_TREES_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_BIRCH);
        USE_PINE_TREES_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_PINE);
        USE_SWAMP_TREES_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_SWAMP);
        USE_JUNGLE_TREES_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_JUNGLE);
        USE_ACACIA_TREES_TEST = new GuiPredicate(USE_NEW_FLOWERS_TEST::test, GuiIdentifiers.PG1_B_USE_ACACIA);
        SPAWN_NEW_CREATURE_MOBS_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG1_B_SPAWN_CREATURE);
        SPAWN_NEW_MONSTER_MOBS_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG1_B_SPAWN_MONSTER);
        SPAWN_WATER_MOBS_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG1_B_SPAWN_WATER);
        SPAWN_AMBIENT_MOBS_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG1_B_SPAWN_AMBIENT);
        SPAWN_WOLVES_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG1_B_SPAWN_WOLVES);
        USE_MODDED_BIOMES_TEST = new GuiPredicate(factory -> isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.RELEASE), GuiIdentifiers.PG1_B_USE_MODDED_BIOMES);
        CLAY_SIZE_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_S_CLAY_SIZE);
        CLAY_COUNT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_S_CLAY_CNT);
        CLAY_MIN_HEIGHT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_S_CLAY_MIN);
        CLAY_MAX_HEIGHT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_S_CLAY_MAX);
        EMERALD_SIZE_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_S_EMER_SIZE);
        EMERALD_COUNT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_S_EMER_CNT);
        EMERALD_MIN_HEIGHT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_S_EMER_MIN);
        EMERALD_MAX_HEIGHT_TEST = new GuiPredicate(USE_TALL_GRASS_TEST::test, GuiIdentifiers.PG2_S_EMER_MAX);
        QUARTZ_SIZE_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG2_S_QRTZ_SIZE);
        QUARTZ_COUNT_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG2_S_QRTZ_CNT);
        MAGMA_SIZE_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG2_S_MGMA_SIZE);
        MAGMA_COUNT_TEST = new GuiPredicate(USE_NETHER_CAVES_TEST::test, GuiIdentifiers.PG2_S_MGMA_CNT);
        
        COORDINATE_SCALE_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_COORD_SCL), GuiIdentifiers.PG3_S_COORD_SCL, GuiIdentifiers.PG4_F_COORD_SCL);
        HEIGHT_SCALE_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_HEIGH_SCL), GuiIdentifiers.PG3_S_HEIGH_SCL, GuiIdentifiers.PG4_F_HEIGH_SCL);
        LOWER_LIMIT_SCALE_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_LOWER_LIM), GuiIdentifiers.PG3_S_LOWER_LIM, GuiIdentifiers.PG4_F_LOWER_LIM);
        UPPER_LIMIT_SCALE_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_UPPER_LIM), GuiIdentifiers.PG3_S_UPPER_LIM, GuiIdentifiers.PG4_F_UPPER_LIM);
        SCALE_NOISE_SCALE_X_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_SCLE_NS_X), GuiIdentifiers.PG3_S_SCLE_NS_X, GuiIdentifiers.PG4_F_SCLE_NS_X);
        SCALE_NOISE_SCALE_Z_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_SCLE_NS_Z), GuiIdentifiers.PG3_S_SCLE_NS_Z, GuiIdentifiers.PG4_F_SCLE_NS_Z);
        DEPTH_NOISE_SCALE_X_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_DPTH_NS_X), GuiIdentifiers.PG3_S_DPTH_NS_X, GuiIdentifiers.PG4_F_DPTH_NS_X);
        DEPTH_NOISE_SCALE_Z_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_DPTH_NS_Z), GuiIdentifiers.PG3_S_DPTH_NS_Z, GuiIdentifiers.PG4_F_DPTH_NS_Z);
        MAIN_NOISE_SCALE_X_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_MAIN_NS_X), GuiIdentifiers.PG3_S_MAIN_NS_X, GuiIdentifiers.PG4_F_MAIN_NS_X);
        MAIN_NOISE_SCALE_Y_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_MAIN_NS_Y), GuiIdentifiers.PG3_S_MAIN_NS_Y, GuiIdentifiers.PG4_F_MAIN_NS_Y);
        MAIN_NOISE_SCALE_Z_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_MAIN_NS_Z), GuiIdentifiers.PG3_S_MAIN_NS_Z, GuiIdentifiers.PG4_F_MAIN_NS_Z);
        BASE_SIZE_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_BASE_SIZE), GuiIdentifiers.PG3_S_BASE_SIZE, GuiIdentifiers.PG4_F_BASE_SIZE);
        STRETCH_Y_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_STRETCH_Y), GuiIdentifiers.PG3_S_STRETCH_Y, GuiIdentifiers.PG4_F_STRETCH_Y);
        HEIGHT_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_HEIGH_LIM), GuiIdentifiers.PG3_S_HEIGH_LIM, GuiIdentifiers.PG4_F_HEIGH_LIM);
        TEMP_NOISE_SCALE_TEST = new GuiPredicate(factory -> isBetaOrPESource(factory), GuiIdentifiers.PG3_S_TEMP_SCL, GuiIdentifiers.PG4_F_TEMP_SCL);
        RAIN_NOISE_SCALE_TEST = new GuiPredicate(TEMP_NOISE_SCALE_TEST::test, GuiIdentifiers.PG3_S_RAIN_SCL, GuiIdentifiers.PG4_F_RAIN_SCL);
        DETAIL_NOISE_SCALE_TEST = new GuiPredicate(TEMP_NOISE_SCALE_TEST::test, GuiIdentifiers.PG3_S_DETL_SCL, GuiIdentifiers.PG4_F_DETL_SCL);
        BIOME_DEPTH_WEIGHT_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_B_DPTH_WT), GuiIdentifiers.PG3_S_B_DPTH_WT, GuiIdentifiers.PG4_F_B_DPTH_WT);
        BIOME_DEPTH_OFFSET_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_B_DPTH_OF), GuiIdentifiers.PG3_S_B_DPTH_OF, GuiIdentifiers.PG4_F_B_DPTH_OF);
        BIOME_SCALE_WEIGHT_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_B_SCLE_WT), GuiIdentifiers.PG3_S_B_SCLE_WT, GuiIdentifiers.PG4_F_B_SCLE_WT);
        BIOME_SCALE_OFFSET_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_B_SCLE_OF), GuiIdentifiers.PG3_S_B_SCLE_OF, GuiIdentifiers.PG4_F_B_SCLE_OF);
        BIOME_SIZE_TEST = new GuiPredicate(
            factory -> {
                boolean isSingleBiomeSource = isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.SINGLE);
                boolean isReleaseBiomeSource = isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.RELEASE);
                
                return containsNoiseSetting(factory, GuiIdentifiers.PG3_S_BIOME_SZ) && !isSingleBiomeSource || isReleaseBiomeSource;
            },
            GuiIdentifiers.PG3_S_BIOME_SZ, GuiIdentifiers.PG4_F_BIOME_SZ
        );
        RIVER_SIZE_TEST = new GuiPredicate(factory -> containsNoiseSetting(factory, GuiIdentifiers.PG3_S_RIVER_SZ), GuiIdentifiers.PG3_S_RIVER_SZ, GuiIdentifiers.PG4_F_RIVER_SZ);
        USE_BIOME_DEPTH_SCALE_TEST = new GuiPredicate(
            factory -> {
                BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(new ResourceLocation(factory.biomeSource)).apply(0L, factory.build());
                
                return containsNoiseSetting(factory, GuiIdentifiers.PG3_B_USE_BDS)  && !(biomeSource instanceof NoiseBiomeSource);
            },
            GuiIdentifiers.PG3_B_USE_BDS
        );
        BASE_BIOME_TEST = new GuiPredicate(
            factory -> isBetaOrPEBiomeSource(factory),
            GuiIdentifiers.PG5_DSRT_LAND,
            GuiIdentifiers.PG5_FRST_LAND,
            GuiIdentifiers.PG5_ICED_LAND,
            GuiIdentifiers.PG5_PLNS_LAND,
            GuiIdentifiers.PG5_RAIN_LAND,
            GuiIdentifiers.PG5_SAVA_LAND,
            GuiIdentifiers.PG5_SEAS_LAND,
            GuiIdentifiers.PG5_SHRB_LAND,
            GuiIdentifiers.PG5_SWMP_LAND,
            GuiIdentifiers.PG5_TAIG_LAND,
            GuiIdentifiers.PG5_TUND_LAND
        );
        OCEAN_BIOME_TEST = new GuiPredicate(
            factory -> isBetaOrPEBiomeSource(factory) && factory.replaceOceanBiomes,
            GuiIdentifiers.PG5_DSRT_OCEAN,
            GuiIdentifiers.PG5_FRST_OCEAN,
            GuiIdentifiers.PG5_ICED_OCEAN,
            GuiIdentifiers.PG5_PLNS_OCEAN,
            GuiIdentifiers.PG5_RAIN_OCEAN,
            GuiIdentifiers.PG5_SAVA_OCEAN,
            GuiIdentifiers.PG5_SEAS_OCEAN,
            GuiIdentifiers.PG5_SHRB_OCEAN,
            GuiIdentifiers.PG5_SWMP_OCEAN,
            GuiIdentifiers.PG5_TAIG_OCEAN,
            GuiIdentifiers.PG5_TUND_OCEAN
        );
        BEACH_BIOME_TEST = new GuiPredicate(
            factory -> isBetaOrPEBiomeSource(factory) && factory.replaceBeachBiomes,
            GuiIdentifiers.PG5_DSRT_BEACH,
            GuiIdentifiers.PG5_FRST_BEACH,
            GuiIdentifiers.PG5_ICED_BEACH,
            GuiIdentifiers.PG5_PLNS_BEACH,
            GuiIdentifiers.PG5_RAIN_BEACH,
            GuiIdentifiers.PG5_SAVA_BEACH,
            GuiIdentifiers.PG5_SEAS_BEACH,
            GuiIdentifiers.PG5_SHRB_BEACH,
            GuiIdentifiers.PG5_SWMP_BEACH,
            GuiIdentifiers.PG5_TAIG_BEACH,
            GuiIdentifiers.PG5_TUND_BEACH
        );
    }
}
