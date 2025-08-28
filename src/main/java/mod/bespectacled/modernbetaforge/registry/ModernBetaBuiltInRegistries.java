package mod.bespectacled.modernbetaforge.registry;

import java.util.function.Supplier;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiCustomizePreset;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.EntityEntryProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.property.Property;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.property.StringProperty;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.BiomeSourceCreator;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.CarverCreator;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.CaveCarverCreator;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.ChunkSourceCreator;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.SurfaceBuilderCreator;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistry;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings;
import mod.bespectacled.modernbetaforge.api.world.spawn.WorldSpawner;
import mod.bespectacled.modernbetaforge.client.gui.GuiCustomizePresets;
import mod.bespectacled.modernbetaforge.client.gui.GuiPredicates;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixTags;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers.DataFix;
import mod.bespectacled.modernbetaforge.world.biome.source.BetaBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.PEBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.ReleaseBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.SingleBiomeSource;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBeta18Cave;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBetaCave;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBetaCaveHell;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBetaCaveUnderwater;
import mod.bespectacled.modernbetaforge.world.carver.MapGenCavesExtended;
import mod.bespectacled.modernbetaforge.world.carver.MapGenNoOp;
import mod.bespectacled.modernbetaforge.world.carver.MapGenRavineExtended;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.noise.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.chunk.source.AlphaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.BetaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Classic23aChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.DebugNoiseChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.EndChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.IndevChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev227ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev415ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev420ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev611ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.PEChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.SkylandsChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.surface.Alpha12SurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.AlphaSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.BetaSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.Infdev227SurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.InfdevSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.PESurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.surface.ReleaseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.spawn.BetaWorldSpawner;
import mod.bespectacled.modernbetaforge.world.spawn.FarLandsWorldSpawner;
import mod.bespectacled.modernbetaforge.world.spawn.InfdevWorldSpawner;
import mod.bespectacled.modernbetaforge.world.spawn.NoOpWorldSpawner;
import mod.bespectacled.modernbetaforge.world.spawn.PEWorldSpawner;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModernBetaBuiltInRegistries {
    public static void registerChunkSources() {
        ModernBetaRegistry<ChunkSourceCreator> registry = ModernBetaRegistries.CHUNK_SOURCE;
        
        registry.register(ModernBetaBuiltInTypes.Chunk.BETA.getRegistryKey(), BetaChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryKey(), AlphaChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getRegistryKey(), SkylandsChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryKey(), Infdev611ChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.INFDEV_420.getRegistryKey(), Infdev420ChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.INFDEV_415.getRegistryKey(), Infdev415ChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.INFDEV_227.getRegistryKey(), Infdev227ChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.INDEV.getRegistryKey(), IndevChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.getRegistryKey(), Classic23aChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.PE.getRegistryKey(), PEChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.RELEASE.getRegistryKey(), ReleaseChunkSource::new);
        registry.register(ModernBetaBuiltInTypes.Chunk.END.getRegistryKey(), EndChunkSource::new);
        
        if (ModernBetaConfig.debugOptions.registerDebugNoiseChunkSource) {
            registry.register(ModernBetaBuiltInTypes.Chunk.DEBUG_NOISE.getRegistryKey(), DebugNoiseChunkSource::new);
        }
    }
    
    public static void registerBiomeSources() {
        ModernBetaRegistry<BiomeSourceCreator> registry = ModernBetaRegistries.BIOME_SOURCE;
        
        registry.register(ModernBetaBuiltInTypes.Biome.BETA.getRegistryKey(), BetaBiomeSource::new);
        registry.register(ModernBetaBuiltInTypes.Biome.SINGLE.getRegistryKey(), SingleBiomeSource::new);
        registry.register(ModernBetaBuiltInTypes.Biome.PE.getRegistryKey(), PEBiomeSource::new);
        registry.register(ModernBetaBuiltInTypes.Biome.RELEASE.getRegistryKey(), ReleaseBiomeSource::new);
    }
    
    public static void registerNoiseSettings() {
        ModernBetaRegistry<NoiseSettings> registry = ModernBetaRegistries.NOISE_SETTING;
        
        registry.register(ModernBetaBuiltInTypes.Chunk.BETA.getRegistryKey(), ModernBetaNoiseSettings.BETA);
        registry.register(ModernBetaBuiltInTypes.Chunk.ALPHA.getRegistryKey(), ModernBetaNoiseSettings.ALPHA);
        registry.register(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getRegistryKey(), ModernBetaNoiseSettings.SKYLANDS);
        registry.register(ModernBetaBuiltInTypes.Chunk.INFDEV_611.getRegistryKey(), ModernBetaNoiseSettings.INFDEV_611);
        registry.register(ModernBetaBuiltInTypes.Chunk.INFDEV_420.getRegistryKey(), ModernBetaNoiseSettings.INFDEV_420);
        registry.register(ModernBetaBuiltInTypes.Chunk.INFDEV_415.getRegistryKey(), ModernBetaNoiseSettings.INFDEV_415);
        registry.register(ModernBetaBuiltInTypes.Chunk.PE.getRegistryKey(), ModernBetaNoiseSettings.PE);
        registry.register(ModernBetaBuiltInTypes.Chunk.RELEASE.getRegistryKey(), ModernBetaNoiseSettings.RELEASE);
        registry.register(ModernBetaBuiltInTypes.Chunk.END.getRegistryKey(), ModernBetaNoiseSettings.END);
        
        if (ModernBetaConfig.debugOptions.registerDebugNoiseChunkSource) {
            registry.register(ModernBetaBuiltInTypes.Chunk.DEBUG_NOISE.getRegistryKey(), ModernBetaNoiseSettings.BETA);
        }
    }
    
    public static void registerSurfaceBuilders() {
        ModernBetaRegistry<SurfaceBuilderCreator> registry = ModernBetaRegistries.SURFACE_BUILDER;
        
        registry.register(ModernBetaBuiltInTypes.Surface.BETA.getRegistryKey(), BetaSurfaceBuilder::new);
        registry.register(ModernBetaBuiltInTypes.Surface.ALPHA.getRegistryKey(), AlphaSurfaceBuilder::new);
        registry.register(ModernBetaBuiltInTypes.Surface.ALPHA_1_2.getRegistryKey(), Alpha12SurfaceBuilder::new);
        registry.register(ModernBetaBuiltInTypes.Surface.INFDEV_227.getRegistryKey(), Infdev227SurfaceBuilder::new);
        registry.register(ModernBetaBuiltInTypes.Surface.INFDEV.getRegistryKey(), InfdevSurfaceBuilder::new);
        registry.register(ModernBetaBuiltInTypes.Surface.PE.getRegistryKey(), PESurfaceBuilder::new);
        registry.register(ModernBetaBuiltInTypes.Surface.RELEASE.getRegistryKey(), ReleaseSurfaceBuilder::new);
    }
    
    public static void registerCarvers() {
        ModernBetaRegistry<CarverCreator> registry = ModernBetaRegistries.CARVER;
        
        registry.register(ModernBetaChunkGenerator.CAVE_KEY, (chunkSource, settings) -> 
            TerrainGen.getModdedMapGen(
                ModernBetaRegistries.CAVE_CARVER.get(settings.caveCarver).apply(chunkSource, settings),
                EventType.CAVE
            )
        );
        registry.register(ModernBetaChunkGenerator.RAVINE_KEY, (chunkSource, settings) -> 
            settings.useRavines ?
                TerrainGen.getModdedMapGen(new MapGenRavineExtended(chunkSource, settings), EventType.RAVINE) :
                new MapGenNoOp(chunkSource, settings)
        );
        registry.register(ModernBetaChunkGenerator.CAVE_WATER_KEY, (chunkSource, settings) ->
            settings.useUnderwaterCaves ?
                TerrainGen.getModdedMapGen(new MapGenBetaCaveUnderwater(chunkSource, settings), EventType.CUSTOM) :
                new MapGenNoOp(chunkSource, settings)
        );
    }
    
    public static void registerCaveCarvers() {
        ModernBetaRegistry<CaveCarverCreator> registry = ModernBetaRegistries.CAVE_CARVER;
        
        registry.register(ModernBetaBuiltInTypes.Carver.BETA.getRegistryKey(), MapGenBetaCave::new);
        registry.register(ModernBetaBuiltInTypes.Carver.BETA_1_8.getRegistryKey(), MapGenBeta18Cave::new);
        registry.register(ModernBetaBuiltInTypes.Carver.BETA_NETHER.getRegistryKey(), MapGenBetaCaveHell::new);
        registry.register(ModernBetaBuiltInTypes.Carver.RELEASE.getRegistryKey(), MapGenCavesExtended::new);
        registry.register(ModernBetaBuiltInTypes.Carver.NONE.getRegistryKey(), MapGenNoOp::new);
    }
    
    public static void registerWorldSpawners() {
        ModernBetaRegistry<WorldSpawner> registry = ModernBetaRegistries.WORLD_SPAWNER;
        
        registry.register(ModernBetaBuiltInTypes.WorldSpawner.BETA.getRegistryKey(), new BetaWorldSpawner());
        registry.register(ModernBetaBuiltInTypes.WorldSpawner.INFDEV.getRegistryKey(), new InfdevWorldSpawner());
        registry.register(ModernBetaBuiltInTypes.WorldSpawner.PE.getRegistryKey(), new PEWorldSpawner());
        registry.register(ModernBetaBuiltInTypes.WorldSpawner.FAR_LANDS.getRegistryKey(), new FarLandsWorldSpawner());
        registry.register(ModernBetaBuiltInTypes.WorldSpawner.DEFAULT.getRegistryKey(), WorldSpawner.DEFAULT);
        registry.register(ModernBetaBuiltInTypes.WorldSpawner.RELEASE.getRegistryKey(), new NoOpWorldSpawner());
    }
    
    public static void registerDefaultBlocks() {
        ModernBetaRegistry<Supplier<Block>> registry = ModernBetaRegistries.DEFAULT_BLOCK;
        
        registry.register(Blocks.STONE.getRegistryName(), () -> Blocks.STONE);
        registry.register(Blocks.NETHERRACK.getRegistryName(), () -> Blocks.NETHERRACK);
        registry.register(Blocks.END_STONE.getRegistryName(), () -> Blocks.END_STONE);
    }
    
    public static void registerProperties() {
        ModernBetaRegistry<Property<?>> registry = ModernBetaRegistries.PROPERTY;
        
        registry.register(ModernBeta.createRegistryKey("booleanProp"), new BooleanProperty(true));
        registry.register(ModernBeta.createRegistryKey("intProp"), new IntProperty(4, -50, 500, PropertyGuiType.SLIDER));
        registry.register(ModernBeta.createRegistryKey("intProp2"), new IntProperty(4, -50, 500, PropertyGuiType.FIELD));
        registry.register(ModernBeta.createRegistryKey("floatProp"), new FloatProperty(13.0f, 0.0f, 500.0f, PropertyGuiType.SLIDER));
        registry.register(ModernBeta.createRegistryKey("floatProp2"), new FloatProperty(13.0f, 0.0f, 500.0f, PropertyGuiType.FIELD));
        registry.register(ModernBeta.createRegistryKey("floatProp3"), new FloatProperty(0.5f, 0.0f, 1.0f, PropertyGuiType.SLIDER, 1));
        registry.register(ModernBeta.createRegistryKey("floatProp4"), new FloatProperty(0.5f, 0.0f, 1.0f, PropertyGuiType.FIELD, 2));
        registry.register(ModernBeta.createRegistryKey("stringProp"), new StringProperty("test"));
        registry.register(ModernBeta.createRegistryKey("listProp"), new ListProperty(1, new String[] { "test0", "test1", "test2" }));
        registry.register(ModernBeta.createRegistryKey("biomeProp"), new BiomeProperty(Biomes.PLAINS.getRegistryName()));
        registry.register(ModernBeta.createRegistryKey("blockProp"), new BlockProperty(Blocks.GRASS.getRegistryName()));
        registry.register(ModernBeta.createRegistryKey("fluidProp"), new BlockProperty(Blocks.WATER.getRegistryName(), key -> ForgeRegistryUtil.getFluidBlockRegistryNames().contains(key)));
        registry.register(ModernBeta.createRegistryKey("entityProp"), new EntityEntryProperty(new ResourceLocation("pig")));
    }
    
    public static void registerDataFixes() {
        ModernBetaRegistry<DataFix> registry = ModernBetaRegistries.DATA_FIX;
        
        registry.register(DataFixTags.DESERT_BIOMES, new DataFix(NbtTags.DESERT_BIOMES, DataFixers::fixDesertBiomes));
        registry.register(DataFixTags.FOREST_BIOMES, new DataFix(NbtTags.FOREST_BIOMES, DataFixers::fixForestBiomes));
        registry.register(DataFixTags.ICE_DESERT_BIOMES, new DataFix(NbtTags.ICE_DESERT_BIOMES, DataFixers::fixIceDesertBiomes));
        registry.register(DataFixTags.PLAINS_BIOMES, new DataFix(NbtTags.PLAINS_BIOMES, DataFixers::fixPlainsBiomes));
        registry.register(DataFixTags.RAINFOREST_BIOMES, new DataFix(NbtTags.RAINFOREST_BIOMES, DataFixers::fixRainforestBiomes));
        registry.register(DataFixTags.SAVANNA_BIOMES, new DataFix(NbtTags.SAVANNA_BIOMES, DataFixers::fixSavannaBiomes));
        registry.register(DataFixTags.SHRUBLAND_BIOMES, new DataFix(NbtTags.SHRUBLAND_BIOMES, DataFixers::fixShrublandBiomes));
        registry.register(DataFixTags.SEASONAL_FOREST_BIOMES, new DataFix(NbtTags.SEASONAL_FOREST_BIOMES, DataFixers::fixSeasonalForestBiomes));
        registry.register(DataFixTags.SWAMPLAND_BIOMES, new DataFix(NbtTags.SWAMPLAND_BIOMES, DataFixers::fixSwamplandBiomes));
        registry.register(DataFixTags.TAIGA_BIOMES, new DataFix(NbtTags.TAIGA_BIOMES, DataFixers::fixTaigaBiomes));
        registry.register(DataFixTags.TUNDRA_BIOMES, new DataFix(NbtTags.TUNDRA_BIOMES, DataFixers::fixTundraBiomes));
        registry.register(DataFixTags.USE_SANDSTONE, new DataFix(NbtTags.USE_SANDSTONE, DataFixers::fixSandstone));
        registry.register(DataFixTags.SPAWN_WOLVES, new DataFix(NbtTags.SPAWN_WOLVES, DataFixers::fixWolves));
        registry.register(DataFixTags.SURFACE_BUILDER, new DataFix(NbtTags.SURFACE_BUILDER, DataFixers::fixSurfaces));
        registry.register(DataFixTags.FIX_BIOME_DEPTH_SCALE, new DataFix(NbtTags.USE_BIOME_DEPTH_SCALE, DataFixers::fixBiomeDepthScale));
        registry.register(DataFixTags.SURFACE_SKYLANDS, new DataFix(NbtTags.SURFACE_BUILDER, DataFixers::fixSkylandsSurface));
        registry.register(DataFixTags.FIX_SINGLE_BIOME, new DataFix(NbtTags.DEPR_FIXED_BIOME, DataFixers::fixSingleBiome));
        registry.register(DataFixTags.FIX_USE_INDEV_HOUSE, new DataFix(NbtTags.DEPR_USE_INDEV_HOUSE, DataFixers::fixIndevHouse));
        registry.register(DataFixTags.FIX_RESOURCE_LOCATION_CHUNK, new DataFix(NbtTags.CHUNK_SOURCE, DataFixers::fixResourceLocationChunk));
        registry.register(DataFixTags.FIX_RESOURCE_LOCATION_BIOME, new DataFix(NbtTags.BIOME_SOURCE, DataFixers::fixResourceLocationBiome));
        registry.register(DataFixTags.FIX_RESOURCE_LOCATION_SURFACE, new DataFix(NbtTags.SURFACE_BUILDER, DataFixers::fixResourceLocationSurface));
        registry.register(DataFixTags.FIX_RESOURCE_LOCATION_CARVER, new DataFix(NbtTags.CAVE_CARVER, DataFixers::fixResourceLocationCarver));
        registry.register(DataFixTags.FIX_SCALE_NOISE_SCALE_X, new DataFix(NbtTags.SCALE_NOISE_SCALE_X, DataFixers::fixScaleNoiseScaleX));
        registry.register(DataFixTags.FIX_SCALE_NOISE_SCALE_Z, new DataFix(NbtTags.SCALE_NOISE_SCALE_Z, DataFixers::fixScaleNoiseScaleZ));
        registry.register(DataFixTags.FIX_LAYER_SIZE, new DataFix(NbtTags.LAYER_SIZE, DataFixers::fixLayerSize));
        registry.register(DataFixTags.FIX_CAVE_CARVER_NONE, new DataFix(NbtTags.CAVE_CARVER, DataFixers::fixCaveCarverNone));
        registry.register(DataFixTags.FIX_WORLD_SPAWNER, new DataFix(NbtTags.WORLD_SPAWNER, DataFixers::fixWorldSpawner));
        registry.register(DataFixTags.FIX_DEFAULT_FLUID, new DataFix(NbtTags.DEFAULT_FLUID, DataFixers::fixDefaultFluid));
        registry.register(DataFixTags.FIX_SAND_DISKS, new DataFix(NbtTags.USE_SAND_DISKS, DataFixers::fixSandDisks));
        registry.register(DataFixTags.FIX_GRAVEL_DISKS, new DataFix(NbtTags.USE_GRAVEL_DISKS, DataFixers::fixGravelDisks));
        registry.register(DataFixTags.FIX_CLAY_DISKS, new DataFix(NbtTags.USE_CLAY_DISKS, DataFixers::fixClayDisks));
        registry.register(DataFixTags.FIX_DOUBLE_PLANTS, new DataFix(NbtTags.USE_DOUBLE_PLANTS, DataFixers::fixDoublePlants));
        registry.register(DataFixTags.FIX_SNOWY_BIOME_CHANCE, new DataFix(NbtTags.SNOWY_BIOME_CHANCE, DataFixers::fixSnowyBiomeChance));
        registry.register(DataFixTags.FIX_LAYER_VERSION_1600, new DataFix(NbtTags.LAYER_VERSION, DataFixers::fixLayerVersion1600));
        registry.register(DataFixTags.FIX_BOP_COMPAT, new DataFix(NbtTags.DEPR_USE_MODDED_BIOMES, DataFixers::fixBoPCompat));
        registry.register(DataFixTags.FIX_REPLACE_RIVER_BIOMES, new DataFix(NbtTags.REPLACE_RIVER_BIOMES, DataFixers::fixReplaceRiverBiomes));
        registry.register(DataFixTags.FIX_RELEASE_WORLD_SPAWNER, new DataFix(NbtTags.WORLD_SPAWNER, DataFixers::fixReleaseWorldSpawner));
    }

    @SideOnly(Side.CLIENT)
    public static void registerPresets() {
        ModernBetaRegistry<GuiCustomizePreset> registry = ModernBetaClientRegistries.GUI_PRESET;
        
        registry.register(GuiCustomizePresets.CLASSIC_BETA, GuiCustomizePresets.PRESET_CLASSIC_BETA);
        registry.register(GuiCustomizePresets.CLASSIC_ALPHA_1_2, GuiCustomizePresets.PRESET_CLASSIC_ALPHA_1_2);
        registry.register(GuiCustomizePresets.CLASSIC_ALPHA, GuiCustomizePresets.PRESET_CLASSIC_ALPHA);
        registry.register(GuiCustomizePresets.CLASSIC_ALPHA_WINTER, GuiCustomizePresets.PRESET_CLASSIC_ALPHA_WINTER);
        registry.register(GuiCustomizePresets.CLASSIC_INFDEV_611, GuiCustomizePresets.PRESET_CLASSIC_INFDEV_611);
        registry.register(GuiCustomizePresets.CLASSIC_INFDEV_420, GuiCustomizePresets.PRESET_CLASSIC_INFDEV_420);
        registry.register(GuiCustomizePresets.CLASSIC_INFDEV_415, GuiCustomizePresets.PRESET_CLASSIC_INFDEV_415);
        registry.register(GuiCustomizePresets.CLASSIC_INFDEV_227, GuiCustomizePresets.PRESET_CLASSIC_INFDEV_227);
        registry.register(GuiCustomizePresets.CLASSIC_INDEV_ISLAND, GuiCustomizePresets.PRESET_CLASSIC_INDEV_ISLAND);
        registry.register(GuiCustomizePresets.CLASSIC_0_0_23A, GuiCustomizePresets.PRESET_CLASSIC_0_0_23A);
        registry.register(GuiCustomizePresets.CLASSIC_SKYLANDS, GuiCustomizePresets.PRESET_CLASSIC_SKYLANDS);
        registry.register(GuiCustomizePresets.BETA_SKYLANDS, GuiCustomizePresets.PRESET_BETA_SKYLANDS);
        registry.register(GuiCustomizePresets.BETA_END, GuiCustomizePresets.PRESET_BETA_END);
        registry.register(GuiCustomizePresets.BETA_PE, GuiCustomizePresets.PRESET_BETA_PE);
        registry.register(GuiCustomizePresets.BETA_REALISTIC, GuiCustomizePresets.PRESET_BETA_REALISTIC);
        registry.register(GuiCustomizePresets.BETA_PLUS, GuiCustomizePresets.PRESET_BETA_PLUS);
        registry.register(GuiCustomizePresets.BETA_RELEASE, GuiCustomizePresets.PRESET_BETA_RELEASE);
        
        if (ModernBetaConfig.debugOptions.registerDebugNoiseChunkSource) {
            registry.register(GuiCustomizePresets.DEBUG_NOISE, GuiCustomizePresets.PRESET_DEBUG_NOISE);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerPredicates() {
        ModernBetaRegistry<GuiPredicate> registry = ModernBetaClientRegistries.GUI_PREDICATE;
        
        registry.register(GuiPredicate.SURFACE_BUILDER, GuiPredicates.SURFACE_BUILDER_TEST);
        registry.register(GuiPredicate.SPAWN_LOCATOR, GuiPredicates.SPAWN_LOCATOR_TEST);
        registry.register(GuiPredicate.SINGLE_BIOME, GuiPredicates.SINGLE_BIOME_TEST);
        registry.register(GuiPredicate.REPLACE_OCEAN, GuiPredicates.REPLACE_OCEAN_TEST);
        registry.register(GuiPredicate.REPLACE_BEACH, GuiPredicates.REPLACE_BEACH_TEST);
        registry.register(GuiPredicate.REPLACE_RIVER, GuiPredicates.REPLACE_RIVER_TEST);
        registry.register(GuiPredicate.SEA_LEVEL, GuiPredicates.SEA_LEVEL_TEST);
        registry.register(GuiPredicate.CAVE_WIDTH, GuiPredicates.CAVE_WIDTH_TEST);
        registry.register(GuiPredicate.CAVE_HEIGHT, GuiPredicates.CAVE_HEIGHT_TEST);
        registry.register(GuiPredicate.CAVE_COUNT, GuiPredicates.CAVE_COUNT_TEST);
        registry.register(GuiPredicate.CAVE_CHANCE, GuiPredicates.CAVE_CHANCE_TEST);
        registry.register(GuiPredicate.USE_STRONGHOLDS, GuiPredicates.USE_STRONGHOLDS_TEST);
        registry.register(GuiPredicate.USE_VILLAGES, GuiPredicates.USE_VILLAGES_TEST);
        registry.register(GuiPredicate.USE_VILLAGE_VARIANTS, GuiPredicates.USE_VILLAGE_VARIANTS_TEST);
        registry.register(GuiPredicate.USE_TEMPLES, GuiPredicates.USE_TEMPLES_TEST);
        registry.register(GuiPredicate.USE_MONUMENTS, GuiPredicates.USE_MONUMENTS_TEST);
        registry.register(GuiPredicate.USE_MANSIONS, GuiPredicates.USE_MANSIONS_TEST);
        registry.register(GuiPredicate.DUNGEON_CHANCE, GuiPredicates.DUNGEON_CHANCE_TEST);
        registry.register(GuiPredicate.WATER_LAKE_CHANCE, GuiPredicates.WATER_LAKE_CHANCE_TEST);
        registry.register(GuiPredicate.LAVA_LAKE_CHANCE, GuiPredicates.LAVA_LAKE_CHANCE_TEST);
        registry.register(GuiPredicate.USE_SANDSTONE, GuiPredicates.USE_SANDSTONE_TEST);
        registry.register(GuiPredicate.USE_OLD_NETHER, GuiPredicates.USE_OLD_NETHER_TEST);
        registry.register(GuiPredicate.USE_NETHER_CAVES, GuiPredicates.USE_NETHER_CAVES_TEST);
        registry.register(GuiPredicate.USE_FORTRESSES, GuiPredicates.USE_FORTRESSES_TEST);
        registry.register(GuiPredicate.USE_LAVA_POCKETS, GuiPredicates.USE_LAVA_POCKETS_TEST);
        registry.register(GuiPredicate.LEVEL_THEME, GuiPredicates.LEVEL_THEME_TEST);
        registry.register(GuiPredicate.LEVEL_TYPE, GuiPredicates.LEVEL_TYPE_TEST);
        registry.register(GuiPredicate.LEVEL_WIDTH, GuiPredicates.LEVEL_WIDTH_TEST);
        registry.register(GuiPredicate.LEVEL_HEIGHT, GuiPredicates.LEVEL_HEIGHT_TEST);
        registry.register(GuiPredicate.LEVEL_LENGTH, GuiPredicates.LEVEL_LENGTH_TEST);
        registry.register(GuiPredicate.LEVEL_HOUSE, GuiPredicates.LEVEL_HOUSE_TEST);
        registry.register(GuiPredicate.LEVEL_CAVE_WIDTH, GuiPredicates.LEVEL_CAVE_WIDTH_TEST);
        registry.register(GuiPredicate.USE_INDEV_CAVES, GuiPredicates.USE_INDEV_CAVES_TEST);
        registry.register(GuiPredicate.USE_INFDEV_WALLS, GuiPredicates.USE_INFDEV_WALLS_TEST);
        registry.register(GuiPredicate.USE_INFDEV_PYRAMIDS, GuiPredicates.USE_INFDEV_PYRAMIDS_TEST);
        registry.register(GuiPredicate.USE_TALL_GRASS, GuiPredicates.USE_TALL_GRASS_TEST);
        registry.register(GuiPredicate.USE_NEW_FLOWERS, GuiPredicates.USE_NEW_FLOWERS_TEST);
        registry.register(GuiPredicate.USE_DOUBLE_PLANTS, GuiPredicates.USE_DOUBLE_PLANTS_TEST);
        registry.register(GuiPredicate.USE_LILY_PADS, GuiPredicates.USE_LILY_PADS_TEST);
        registry.register(GuiPredicate.USE_MELONS, GuiPredicates.USE_MELONS_TEST);
        registry.register(GuiPredicate.USE_DESERT_WELLS, GuiPredicates.USE_DESERT_WELLS_TEST);
        registry.register(GuiPredicate.USE_FOSSILS, GuiPredicates.USE_FOSSILS_TEST);
        registry.register(GuiPredicate.USE_SAND_DISKS, GuiPredicates.USE_SAND_DISKS_TEST);
        registry.register(GuiPredicate.USE_GRAVEL_DISKS, GuiPredicates.USE_GRAVEL_DISKS_TEST);
        registry.register(GuiPredicate.USE_CLAY_DISKS, GuiPredicates.USE_CLAY_DISKS_TEST);
        registry.register(GuiPredicate.USE_BIRCH_TREES, GuiPredicates.USE_BIRCH_TREES_TEST);
        registry.register(GuiPredicate.USE_PINE_TREES, GuiPredicates.USE_PINE_TREES_TEST);
        registry.register(GuiPredicate.USE_SWAMP_TREES, GuiPredicates.USE_SWAMP_TREES_TEST);
        registry.register(GuiPredicate.USE_JUNGLE_TREES, GuiPredicates.USE_JUNGLE_TREES_TEST);
        registry.register(GuiPredicate.USE_ACACIA_TREES, GuiPredicates.USE_ACACIA_TREES_TEST);
        registry.register(GuiPredicate.USE_NEW_FANCY_OAK_TREES, GuiPredicates.USE_NEW_FANCY_OAK_TREES_TEST);
        registry.register(GuiPredicate.SPAWN_NEW_MONSTER_MOBS, GuiPredicates.SPAWN_NEW_MONSTER_MOBS_TEST);
        registry.register(GuiPredicate.SPAWN_NEW_CREATURE_MOBS, GuiPredicates.SPAWN_NEW_CREATURE_MOBS_TEST);
        registry.register(GuiPredicate.SPAWN_WATER_MOBS, GuiPredicates.SPAWN_WATER_MOBS_TEST);
        registry.register(GuiPredicate.SPAWN_AMBIENT_MOBS, GuiPredicates.SPAWN_AMBIENT_MOBS_TEST);
        registry.register(GuiPredicate.SPAWN_WOLVES, GuiPredicates.SPAWN_WOLVES_TEST);
        registry.register(GuiPredicate.CLAY_SIZE, GuiPredicates.CLAY_SIZE_TEST);
        registry.register(GuiPredicate.CLAY_COUNT, GuiPredicates.CLAY_COUNT_TEST);
        registry.register(GuiPredicate.CLAY_MIN_HEIGHT, GuiPredicates.CLAY_MIN_HEIGHT_TEST);
        registry.register(GuiPredicate.CLAY_MAX_HEIGHT, GuiPredicates.CLAY_MAX_HEIGHT_TEST);
        registry.register(GuiPredicate.EMERALD_SIZE, GuiPredicates.EMERALD_SIZE_TEST);
        registry.register(GuiPredicate.EMERALD_COUNT, GuiPredicates.EMERALD_COUNT_TEST);
        registry.register(GuiPredicate.EMERALD_MIN_HEIGHT, GuiPredicates.EMERALD_MIN_HEIGHT_TEST);
        registry.register(GuiPredicate.EMERALD_MAX_HEIGHT, GuiPredicates.EMERALD_MAX_HEIGHT_TEST);
        registry.register(GuiPredicate.QUARTZ_SIZE, GuiPredicates.QUARTZ_SIZE_TEST);
        registry.register(GuiPredicate.QUARTZ_COUNT, GuiPredicates.QUARTZ_COUNT_TEST);
        registry.register(GuiPredicate.MAGMA_SIZE, GuiPredicates.MAGMA_SIZE_TEST);
        registry.register(GuiPredicate.MAGMA_COUNT, GuiPredicates.MAGMA_COUNT_TEST);
        registry.register(GuiPredicate.COORDINATE_SCALE, GuiPredicates.COORDINATE_SCALE_TEST);
        registry.register(GuiPredicate.HEIGHT_SCALE, GuiPredicates.HEIGHT_SCALE_TEST);
        registry.register(GuiPredicate.LOWER_LIMIT_SCALE, GuiPredicates.LOWER_LIMIT_SCALE_TEST);
        registry.register(GuiPredicate.UPPER_LIMIT_SCALE, GuiPredicates.UPPER_LIMIT_SCALE_TEST);
        registry.register(GuiPredicate.SCALE_NOISE_SCALE_X, GuiPredicates.SCALE_NOISE_SCALE_X_TEST);
        registry.register(GuiPredicate.SCALE_NOISE_SCALE_Z, GuiPredicates.SCALE_NOISE_SCALE_Z_TEST);
        registry.register(GuiPredicate.DEPTH_NOISE_SCALE_X, GuiPredicates.DEPTH_NOISE_SCALE_X_TEST);
        registry.register(GuiPredicate.DEPTH_NOISE_SCALE_Z, GuiPredicates.DEPTH_NOISE_SCALE_Z_TEST);
        registry.register(GuiPredicate.MAIN_NOISE_SCALE_X, GuiPredicates.MAIN_NOISE_SCALE_X_TEST);
        registry.register(GuiPredicate.MAIN_NOISE_SCALE_Y, GuiPredicates.MAIN_NOISE_SCALE_Y_TEST);
        registry.register(GuiPredicate.MAIN_NOISE_SCALE_Z, GuiPredicates.MAIN_NOISE_SCALE_Z_TEST);
        registry.register(GuiPredicate.BASE_SIZE, GuiPredicates.BASE_SIZE_TEST);
        registry.register(GuiPredicate.STRETCH_Y, GuiPredicates.STRETCH_Y_TEST);
        registry.register(GuiPredicate.HEIGHT, GuiPredicates.HEIGHT_TEST);
        registry.register(GuiPredicate.TEMP_NOISE_SCALE, GuiPredicates.TEMP_NOISE_SCALE_TEST);
        registry.register(GuiPredicate.RAIN_NOISE_SCALE, GuiPredicates.RAIN_NOISE_SCALE_TEST);
        registry.register(GuiPredicate.DETAIL_NOISE_SCALE, GuiPredicates.DETAIL_NOISE_SCALE_TEST);
        registry.register(GuiPredicate.SNOW_LINE_OFFSET, GuiPredicates.SNOW_LINE_OFFSET_TEST);
        registry.register(GuiPredicate.USE_CLIMATE_FEATURES, GuiPredicates.USE_CLIMATE_FEATURES_TEST);
        registry.register(GuiPredicate.BIOME_DEPTH_WEIGHT, GuiPredicates.BIOME_DEPTH_WEIGHT_TEST);
        registry.register(GuiPredicate.BIOME_DEPTH_OFFSET, GuiPredicates.BIOME_DEPTH_OFFSET_TEST);
        registry.register(GuiPredicate.BIOME_SCALE_WEIGHT, GuiPredicates.BIOME_SCALE_WEIGHT_TEST);
        registry.register(GuiPredicate.BIOME_SCALE_OFFSET, GuiPredicates.BIOME_SCALE_OFFSET_TEST);
        registry.register(GuiPredicate.BIOME_SIZE, GuiPredicates.BIOME_SIZE_TEST);
        registry.register(GuiPredicate.RIVER_SIZE, GuiPredicates.RIVER_SIZE_TEST);
        registry.register(GuiPredicate.USE_BIOME_DEPTH_SCALE, GuiPredicates.USE_BIOME_DEPTH_SCALE_TEST);
        registry.register(GuiPredicate.LAYER_TYPE, GuiPredicates.LAYER_TYPE_TEST);
        registry.register(GuiPredicate.LAYER_SIZE, GuiPredicates.LAYER_SIZE_TEST);
        registry.register(GuiPredicate.SNOWY_BIOME_CHANCE, GuiPredicates.SNOWY_BIOME_CHANCE_TEST);
        registry.register(GuiPredicate.BASE_BIOME, GuiPredicates.BASE_BIOME_TEST);
        registry.register(GuiPredicate.OCEAN_BIOME, GuiPredicates.OCEAN_BIOME_TEST);
        registry.register(GuiPredicate.BEACH_BIOME, GuiPredicates.BEACH_BIOME_TEST);
        registry.register(GuiPredicate.END_ISLAND_OFFSET, GuiPredicates.END_ISLAND_OFFSET_TEST);
        registry.register(GuiPredicate.END_ISLAND_WEIGHT, GuiPredicates.END_ISLAND_WEIGHT_TEST);
        registry.register(GuiPredicate.END_OUTER_ISLAND_DISTANCE, GuiPredicates.END_OUTER_ISLAND_DISTANCE_TEST);
        registry.register(GuiPredicate.END_OUTER_ISLAND_OFFSET, GuiPredicates.END_OUTER_ISLAND_OFFSET_TEST);
        registry.register(GuiPredicate.USE_END_OUTER_ISLANDS, GuiPredicates.USE_END_OUTER_ISLANDS_TEST);
    }
}
