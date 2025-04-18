package mod.bespectacled.modernbetaforge.api.registry;

import java.util.function.Supplier;

import mod.bespectacled.modernbetaforge.api.property.Property;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverCustom;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseColumnSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.api.world.spawn.WorldSpawner;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers.DataFix;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.world.gen.MapGenBase;

public class ModernBetaRegistries {
    public static final ModernBetaRegistry<ChunkSourceCreator> CHUNK_SOURCE;
    public static final ModernBetaRegistry<BiomeSourceCreator> BIOME_SOURCE;
    public static final ModernBetaRegistry<BiomeResolverCreator> BIOME_RESOLVER;
    public static final ModernBetaRegistry<NoiseSamplerCreator> NOISE_SAMPLER;
    public static final ModernBetaRegistry<NoiseColumnSamplerCreator> NOISE_COLUMN_SAMPLER;
    public static final ModernBetaRegistry<NoiseSettings> NOISE_SETTING;
    public static final ModernBetaRegistry<SurfaceBuilderCreator> SURFACE_BUILDER;
    public static final ModernBetaRegistry<CarverCreator> CARVER;
    public static final ModernBetaRegistry<CaveCarverCreator> CAVE_CARVER;
    public static final ModernBetaRegistry<BlockSourceCreator> BLOCK_SOURCE;
    public static final ModernBetaRegistry<WorldSpawner> WORLD_SPAWNER;
    public static final ModernBetaRegistry<Supplier<Block>> DEFAULT_BLOCK;
    public static final ModernBetaRegistry<DataFix> DATA_FIX;
    public static final ModernBetaRegistry<Property<?>> PROPERTY;
    
    static {
        CHUNK_SOURCE = new ModernBetaRegistry<>("CHUNK_SOURCE");
        BIOME_SOURCE = new ModernBetaRegistry<>("BIOME_SOURCE");
        BIOME_RESOLVER = new ModernBetaRegistry<>("BIOME_RESOLVER");
        NOISE_SAMPLER = new ModernBetaRegistry<>("NOISE_SAMPLER");
        NOISE_COLUMN_SAMPLER = new ModernBetaRegistry<>("NOISE_COLUMN_SAMPLER");
        NOISE_SETTING = new ModernBetaRegistry<>("NOISE_SETTINGS");
        SURFACE_BUILDER = new ModernBetaRegistry<>("SURFACE_BUILDER");
        CARVER = new ModernBetaRegistry<>("CARVER");
        CAVE_CARVER = new ModernBetaRegistry<>("CAVE_CARVER");
        BLOCK_SOURCE = new ModernBetaRegistry<>("BLOCK_SOURCE");
        WORLD_SPAWNER = new ModernBetaRegistry<>("WORLD_SPAWNER");
        DEFAULT_BLOCK = new ModernBetaRegistry<>("DEFAULT_BLOCK");
        DATA_FIX = new ModernBetaRegistry<>("DATA_FIX");
        PROPERTY = new ModernBetaRegistry<>("PROPERTY");
    }
    
    @FunctionalInterface
    public static interface ChunkSourceCreator {
        ChunkSource apply(long seed, ModernBetaGeneratorSettings settings);
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
