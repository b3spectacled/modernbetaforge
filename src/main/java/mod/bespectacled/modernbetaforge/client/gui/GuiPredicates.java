package mod.bespectacled.modernbetaforge.client.gui;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.NbtTags;
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

public class GuiPredicates {
    private static final MapGenStronghold STRONGHOLD = new MapGenStronghold();
    
    public static final ResourceLocation SURFACE_BUILDER = createRegistryKey(NbtTags.SURFACE_BUILDER);
    public static final ResourceLocation CAVE_CARVER = createRegistryKey(NbtTags.CAVE_CARVER);
    public static final ResourceLocation SINGLE_BIOME = createRegistryKey(NbtTags.SINGLE_BIOME);
    public static final ResourceLocation REPLACE_OCEAN = createRegistryKey(NbtTags.REPLACE_OCEAN_BIOMES);
    public static final ResourceLocation REPLACE_BEACH = createRegistryKey(NbtTags.REPLACE_BEACH_BIOMES);
    public static final ResourceLocation SEA_LEVEL = createRegistryKey(NbtTags.SEA_LEVEL);
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
    public static final ResourceLocation USE_INDEV_CAVES = createRegistryKey(NbtTags.USE_INDEV_CAVES);
    public static final ResourceLocation USE_INFDEV_WALLS = createRegistryKey(NbtTags.USE_INFDEV_WALLS);
    public static final ResourceLocation USE_INFDEV_PYRAMIDS = createRegistryKey(NbtTags.USE_INFDEV_PYRAMIDS);
    public static final ResourceLocation USE_TALL_GRASS = createRegistryKey(NbtTags.USE_TALL_GRASS);
    public static final ResourceLocation USE_NEW_FLOWERS = createRegistryKey(NbtTags.USE_NEW_FLOWERS);
    public static final ResourceLocation USE_LILY_PADS = createRegistryKey(NbtTags.USE_LILY_PADS);
    public static final ResourceLocation USE_MELONS = createRegistryKey(NbtTags.USE_MELONS);
    public static final ResourceLocation USE_DESERT_WELLS = createRegistryKey(NbtTags.USE_DESERT_WELLS);
    public static final ResourceLocation USE_FOSSILS = createRegistryKey(NbtTags.USE_FOSSILS);
    public static final ResourceLocation USE_BIRCH_TREES = createRegistryKey(NbtTags.USE_BIRCH_TREES);
    public static final ResourceLocation USE_PINE_TREES = createRegistryKey(NbtTags.USE_PINE_TREES);
    public static final ResourceLocation USE_SWAMP_TREES = createRegistryKey(NbtTags.USE_SWAMP_TREES);
    public static final ResourceLocation USE_JUNGLE_TREES = createRegistryKey(NbtTags.USE_JUNGLE_TREES);
    public static final ResourceLocation USE_ACACIA_TREES = createRegistryKey(NbtTags.USE_ACACIA_TREES);
    
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
    
    private static ResourceLocation createRegistryKey(String name) {
        return ModernBeta.createRegistryKey(name);
    }

    static {
        SURFACE_BUILDER_TEST = new GuiPredicate(
            GuiIdentifiers.PG0_S_SURFACE,
            factory -> {
                boolean isSkylands = isChunkEqualTo(factory, ModernBetaBuiltInTypes.Chunk.SKYLANDS);

                return !isSkylands && !isFiniteChunk(factory);
            }
        );
        CAVE_CARVER_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_CARVER, factory -> factory.useCaves);
        SINGLE_BIOME_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_FIXED, factory -> isSingleBiome(factory));
        REPLACE_OCEAN_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_OCEAN, factory -> {
            BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(new ResourceLocation(factory.biomeSource)).apply(0L, factory.build());

            return biomeSource instanceof BiomeResolverOcean;
        });
        REPLACE_BEACH_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_BEACH, factory -> {
            BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(new ResourceLocation(factory.biomeSource)).apply(0L, factory.build());

            return biomeSource instanceof BiomeResolverBeach;
        });
        SEA_LEVEL_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_SEA_LEVEL, SURFACE_BUILDER_TEST::test);
        CAVE_HEIGHT_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_CAVE_HEIGHT, factory -> !isCarverEqualTo(factory, ModernBetaBuiltInTypes.Carver.RELEASE) && factory.useCaves);
        CAVE_COUNT_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_CAVE_COUNT, CAVE_HEIGHT_TEST::test);
        CAVE_CHANCE_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_CAVE_CHANCE, CAVE_HEIGHT_TEST::test);
        USE_STRONGHOLDS_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_HOLDS, factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));

            return isSingleBiome(factory) ? STRONGHOLD.allowedBiomes.contains(biome) : true;
        });
        USE_VILLAGES_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_VILLAGES, factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));

            return isSingleBiome(factory) ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;
        });
        USE_VILLAGE_VARIANTS_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_VILLAGE_VARIANTS, factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));
            boolean hasVillages = isSingleBiome(factory) ? MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biome) : true;

            return hasVillages && factory.useVillages; 
        });
        USE_TEMPLES_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_TEMPLES, factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));

            return isSingleBiome(factory) ? MapGenScatteredFeature.BIOMELIST.contains(biome) : true;
        });
        USE_MONUMENTS_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_MONUMENTS, factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));

            return isSingleBiome(factory) ? StructureOceanMonument.SPAWN_BIOMES.contains(biome) : true;
        });
        USE_MANSIONS_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_MANSIONS, factory -> {
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(factory.singleBiome));

            return isSingleBiome(factory) ? WoodlandMansion.ALLOWED_BIOMES.contains(biome) : true;
        });
        DUNGEON_CHANCE_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_DUNGEON_CHANCE, factory -> factory.useDungeons);
        WATER_LAKE_CHANCE_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_WATER_LAKE_CHANCE, factory -> factory.useWaterLakes);
        LAVA_LAKE_CHANCE_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_LAVA_LAKE_CHANCE, factory -> factory.useLavaLakes);
        USE_SANDSTONE_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_SANDSTONE, factory -> {
            boolean isReleaseSurface = isSurfaceEqualTo(factory, ModernBetaBuiltInTypes.Surface.RELEASE);
            
            return !isReleaseSurface && !isFiniteChunk(factory);
        });
        USE_OLD_NETHER_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_OLD_NETHER, factory -> !ModCompat.isBoPLoaded());
        USE_NETHER_CAVES_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_NETHER_CAVES, factory -> factory.useOldNether && !ModCompat.isBoPLoaded());
        USE_FORTRESSES_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_FORTRESSES, USE_NETHER_CAVES_TEST::test);
        USE_LAVA_POCKETS_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_LAVA_POCKETS, USE_NETHER_CAVES_TEST::test);
        LEVEL_THEME_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_LEVEL_THEME, factory -> isChunkEqualTo(factory, ModernBetaBuiltInTypes.Chunk.INDEV));
        LEVEL_TYPE_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_LEVEL_TYPE, LEVEL_THEME_TEST::test);
        LEVEL_WIDTH_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_LEVEL_WIDTH, factory -> isFiniteChunk(factory));
        LEVEL_LENGTH_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_LEVEL_LENGTH, LEVEL_WIDTH_TEST::test);
        LEVEL_HEIGHT_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_LEVEL_HEIGHT, LEVEL_WIDTH_TEST::test);
        LEVEL_HOUSE_TEST = new GuiPredicate(GuiIdentifiers.PG0_S_LEVEL_HOUSE, LEVEL_WIDTH_TEST::test);
        USE_INDEV_CAVES_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_INDEV_CAVES, LEVEL_WIDTH_TEST::test);
        USE_INFDEV_WALLS_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_INFDEV_WALLS, factory -> isChunkEqualTo(factory, ModernBetaBuiltInTypes.Chunk.INFDEV_227));
        USE_INFDEV_PYRAMIDS_TEST = new GuiPredicate(GuiIdentifiers.PG0_B_USE_INFDEV_PYRAMIDS, USE_INFDEV_WALLS_TEST::test);
        USE_TALL_GRASS_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_GRASS, factory -> {
            boolean isReleaseBiomeSource = isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.RELEASE);
            boolean isFixedBiomeSource = isSingleBiome(factory);
            boolean isModernBetaBiome = isModernBetaBiome(factory);

            return !isReleaseBiomeSource || (isFixedBiomeSource && isModernBetaBiome);
        });
        USE_NEW_FLOWERS_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_FLOWERS, factory -> {
            boolean isReleaseBiomeSource = isBiomeEqualTo(factory, ModernBetaBuiltInTypes.Biome.RELEASE);
            boolean isFixedBiomeSource = isSingleBiome(factory);
            boolean isBetaBiome = isBetaBiome(factory);

            return !isReleaseBiomeSource || (isFixedBiomeSource && isBetaBiome);
        });
        USE_LILY_PADS_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_PADS, USE_NEW_FLOWERS_TEST::test);
        USE_MELONS_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_MELONS, USE_NEW_FLOWERS_TEST::test);
        USE_DESERT_WELLS_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_WELLS, USE_NEW_FLOWERS_TEST::test);
        USE_FOSSILS_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_FOSSILS, USE_NEW_FLOWERS_TEST::test);
        USE_BIRCH_TREES_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_BIRCH, USE_NEW_FLOWERS_TEST::test);
        USE_PINE_TREES_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_PINE, USE_NEW_FLOWERS_TEST::test);
        USE_SWAMP_TREES_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_SWAMP, USE_NEW_FLOWERS_TEST::test);
        USE_JUNGLE_TREES_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_JUNGLE, USE_NEW_FLOWERS_TEST::test);
        USE_ACACIA_TREES_TEST = new GuiPredicate(GuiIdentifiers.PG1_B_USE_ACACIA, USE_NEW_FLOWERS_TEST::test);
    }
}
