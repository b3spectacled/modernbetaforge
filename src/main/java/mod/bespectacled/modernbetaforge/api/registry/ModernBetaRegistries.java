package mod.bespectacled.modernbetaforge.api.registry;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class ModernBetaRegistries {
    public static final ModernBetaRegistry<ChunkSourceCreator> CHUNK;
    public static final ModernBetaRegistry<BiomeSourceCreator> BIOME;
    public static final ModernBetaRegistry<ModernBetaNoiseSettings> NOISE;
    public static final ModernBetaRegistry<SurfaceBuilderCreator> SURFACE;
    public static final ModernBetaRegistry<DataFix> DATA_FIX;
    
    static {
        CHUNK = new ModernBetaRegistry<>("CHUNK");
        BIOME = new ModernBetaRegistry<>("BIOME");
        NOISE = new ModernBetaRegistry<>("NOISE");
        SURFACE = new ModernBetaRegistry<>("SURFACE");
        DATA_FIX = new ModernBetaRegistry<>("DATA_FIX");
    }
    
    @FunctionalInterface
    public static interface ChunkSourceCreator {
        ChunkSource apply(
            World world,
            ModernBetaChunkGenerator chunkGenerator,
            ModernBetaChunkGeneratorSettings chunkGeneratorSettings,
            ModernBetaNoiseSettings noiseSettings,
            long seed,
            boolean mapFeaturesEnabled
        );
    }
    
    @FunctionalInterface
    public static interface BiomeSourceCreator {
        BiomeSource apply(WorldInfo worldInfo);
    }
    
    @FunctionalInterface
    public static interface SurfaceBuilderCreator {
        SurfaceBuilder apply(World world, NoiseChunkSource chunkSource, ModernBetaChunkGeneratorSettings settings);
    }
    
    @FunctionalInterface
    public static interface DataFix {
        void apply(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject);
    }
}
