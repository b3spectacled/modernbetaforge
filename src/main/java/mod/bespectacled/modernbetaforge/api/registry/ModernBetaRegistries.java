package mod.bespectacled.modernbetaforge.api.registry;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSource.NoiseColumnSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.api.world.setting.Property;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers.DataFix;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.noise.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.storage.WorldInfo;

public class ModernBetaRegistries {
    public static final ModernBetaRegistry<ChunkSourceCreator> CHUNK;
    public static final ModernBetaRegistry<BiomeSourceCreator> BIOME;
    public static final ModernBetaRegistry<NoiseSamplerCreator> NOISE;
    public static final ModernBetaRegistry<ModernBetaNoiseSettings> NOISE_SETTING;
    public static final ModernBetaRegistry<SurfaceBuilderCreator> SURFACE;
    public static final ModernBetaRegistry<CaveCarverCreator> CARVER;
    public static final ModernBetaRegistry<BlockSourceCreator> BLOCK;
    public static final ModernBetaRegistry<Property<?>> PROPERTY;
    public static final ModernBetaRegistry<DataFix> DATA_FIX;
    
    static {
        CHUNK = new ModernBetaRegistry<>("CHUNK");
        BIOME = new ModernBetaRegistry<>("BIOME");
        NOISE = new ModernBetaRegistry<>("NOISE");
        NOISE_SETTING = new ModernBetaRegistry<>("NOISE_SETTINGS");
        SURFACE = new ModernBetaRegistry<>("SURFACE");
        CARVER = new ModernBetaRegistry<>("CARVER");
        BLOCK = new ModernBetaRegistry<>("BLOCK");
        PROPERTY = new ModernBetaRegistry<>("PROPERTY");
        DATA_FIX = new ModernBetaRegistry<>("DATA_FIX");
    }
    
    @FunctionalInterface
    public static interface ChunkSourceCreator {
        ChunkSource apply(
            World world,
            ModernBetaChunkGenerator chunkGenerator,
            ModernBetaGeneratorSettings settings
        );
    }
    
    @FunctionalInterface
    public static interface BiomeSourceCreator {
        BiomeSource apply(WorldInfo worldInfo);
    }
    
    @FunctionalInterface
    public static interface NoiseSamplerCreator {
        NoiseColumnSampler apply(World world, ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface SurfaceBuilderCreator {
        SurfaceBuilder apply(World world, ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface CaveCarverCreator {
        MapGenBase apply(World world, ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface BlockSourceCreator {
        BlockSource apply(World world, ChunkSource chunkSource, ModernBetaGeneratorSettings settings);
    }
}
