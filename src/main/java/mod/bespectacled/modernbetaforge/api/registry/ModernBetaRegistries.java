package mod.bespectacled.modernbetaforge.api.registry;

import java.util.function.Supplier;

import mod.bespectacled.modernbetaforge.api.datafix.ModDataFix;
import mod.bespectacled.modernbetaforge.api.property.Property;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverCustom;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseColumnSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseHeightSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings.SlideSettings;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.api.world.spawn.WorldSpawner;
import mod.bespectacled.modernbetaforge.util.datafix.ModDataFixer;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.gen.MapGenBase;

public class ModernBetaRegistries {
    /**
     * Holds registered {@link ChunkSourceCreator interfaces} which instantiate {@link ChunkSource chunk sources}.
     * Register terrain generators here.
     */
    public static final ModernBetaRegistry<ChunkSourceCreator> CHUNK_SOURCE;
    
    /**
     * Holds registered {@link BiomeSourceCreator interfaces} which instantiate {@link BiomeSource biome sources}.
     * Register biome generators here.
     */
    public static final ModernBetaRegistry<BiomeSourceCreator> BIOME_SOURCE;
    
    /**
     * Holds registered {@link BiomeResolverCreator interfaces} which instantiate {@link BiomeResolverCustom biome resolvers}.
     * Register biome resolvers to replace biomes sampled at a specific point within an existing {@link BiomeSource biome source} here.
     */
    public static final ModernBetaRegistry<BiomeResolverCreator> BIOME_RESOLVER;
    
    /**
     * Holds registered {@link NoiseSamplerCreator interfaces} which instantiate {@link NoiseSampler noise samplers}.
     * Register noise samplers to modify or replace terrain densities sampled at a specific point here.
     * Only used with {@link NoiseChunkSource NoiseChunkSource}-based terrain generators.
     */
    public static final ModernBetaRegistry<NoiseSamplerCreator> NOISE_SAMPLER;
    
    /**
     * Holds registered {@link NoiseColumnSamplerCreator interfaces} which instantiate {@link NoiseColumnSampler noise column samplers}.
     * Register noise column samplers to sample and interpolate densities within a chunk detached from the actual terrain generation.
     * Only used with {@link NoiseChunkSource NoiseChunkSource}-based terrain generators.
     */
    public static final ModernBetaRegistry<NoiseColumnSamplerCreator> NOISE_COLUMN_SAMPLER;
    
    /**
     * Holds registered {@link NoiseHeightSamplerCreator interfaces} which instantiate {@link NoiseHeightSampler noise height samplers}.
     * Register noise height samplers to modify or replace base terrain heights sampled at a specific point here.
     * Only used with {@link NoiseChunkSource NoiseChunkSource}-based terrain generators.
     */
    public static final ModernBetaRegistry<NoiseHeightSamplerCreator> NOISE_HEIGHT_SAMPLER;
    
    /**
     * Holds registered {@link NoiseSettings} containers.
     * Register information related to a {@link NoiseChunkSource} generator's noise dimensions and {@link SlideSettings slide settings} here.
     * The registry key should be the same as the one used to register the chunk source in {@link #CHUNK_SOURCE}.
     * Only used with {@link NoiseChunkSource NoiseChunkSource}-based terrain generators.
     */
    public static final ModernBetaRegistry<NoiseSettings> NOISE_SETTING;
    
    /**
     * Holds registered {@link SurfaceBuilderCreator interfaces} which instantiate {@link SurfaceBuilder surface builders}.
     * Register surface builders to modify surface blocks here.
     */
    public static final ModernBetaRegistry<SurfaceBuilderCreator> SURFACE_BUILDER;
    
    /**
     * Holds registered {@link CarverCreator interfaces} which instantiate non-cave carvers extending from {@link MapGenBase}.
     * Register non-cave carvers (i.e. ravines, etc.) here.
     */
    public static final ModernBetaRegistry<CarverCreator> CARVER;
    
    /**
     * Holds registered {@link CaveCarverCreator interfaces} which instantiate cave carvers extending from {@link MapGenBase}.
     * Register cave carvers here.
     */
    public static final ModernBetaRegistry<CaveCarverCreator> CAVE_CARVER;
    
    /**
     * Holds registered {@link BlockSourceCreator interfaces} which instantiate {@link BlockSource block sources}.
     * Register block sources to sample a {@link IBlockState} at a specific point here.
     */
    public static final ModernBetaRegistry<BlockSourceCreator> BLOCK_SOURCE;
    
    /**
     * Holds registered {@link WorldSpawner world spawners}.
     * Register world spawners to modify the position players initially spawn at here.
     */
    public static final ModernBetaRegistry<WorldSpawner> WORLD_SPAWNER;
    
    /**
     * Holds registered {@link Supplier suppliers} for {@link Block blocks} to be used as a default block.
     * Register default blocks for terrain generation (e.g. stone, netherrack, etc.) here.
     */
    public static final ModernBetaRegistry<Supplier<Block>> DEFAULT_BLOCK;
    
    /**
     * Holds registered {@link ModDataFix datafixes} for the {@link ModDataFixer}.
     * For datafixes to function, the mod must also register to {@link ModernBetaModRegistry}
     * with its mod ID and current data version.
     */
    public static final ModernBetaRegistry<ModDataFix> MOD_DATA_FIX;
    
    /**
     * Holds registered {@link Property properties} for adding new generator settings.
     * Register generator settings for add-on mods for later fetching in {@link ModernBetaGeneratorSettings} here.
     */
    public static final ModernBetaRegistry<Property<?>> PROPERTY;
    
    static {
        CHUNK_SOURCE = new ModernBetaRegistry<>("CHUNK_SOURCE");
        BIOME_SOURCE = new ModernBetaRegistry<>("BIOME_SOURCE");
        BIOME_RESOLVER = new ModernBetaRegistry<>("BIOME_RESOLVER");
        NOISE_SAMPLER = new ModernBetaRegistry<>("NOISE_SAMPLER");
        NOISE_COLUMN_SAMPLER = new ModernBetaRegistry<>("NOISE_COLUMN_SAMPLER");
        NOISE_HEIGHT_SAMPLER = new ModernBetaRegistry<>("NOISE_HEIGHT_SAMPLER");
        NOISE_SETTING = new ModernBetaRegistry<>("NOISE_SETTINGS");
        SURFACE_BUILDER = new ModernBetaRegistry<>("SURFACE_BUILDER");
        CARVER = new ModernBetaRegistry<>("CARVER");
        CAVE_CARVER = new ModernBetaRegistry<>("CAVE_CARVER");
        BLOCK_SOURCE = new ModernBetaRegistry<>("BLOCK_SOURCE");
        WORLD_SPAWNER = new ModernBetaRegistry<>("WORLD_SPAWNER");
        DEFAULT_BLOCK = new ModernBetaRegistry<>("DEFAULT_BLOCK");
        MOD_DATA_FIX = new ModernBetaRegistry<>("MOD_DATA_FIX");
        PROPERTY = new ModernBetaRegistry<>("PROPERTY");
    }
    
    @FunctionalInterface
    public static interface ChunkSourceCreator {
        ChunkSource apply(long seed, ModernBetaGeneratorSettings settings, BiomeSource biomeSource);
    }
    
    @FunctionalInterface
    public static interface BiomeSourceCreator {
        BiomeSource apply(long seed, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface BiomeResolverCreator {
        BiomeResolverCustom apply(ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface NoiseSamplerCreator {
        NoiseSampler apply(NoiseChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface NoiseColumnSamplerCreator {
        NoiseColumnSampler apply(NoiseChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface NoiseHeightSamplerCreator {
        NoiseHeightSampler apply(NoiseChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface SurfaceBuilderCreator {
        SurfaceBuilder apply(ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface CarverCreator {
        MapGenBase apply(ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface CaveCarverCreator {
        MapGenBase apply(ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface BlockSourceCreator {
        BlockSource apply(ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
}
