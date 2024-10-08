package mod.bespectacled.modernbetaforge.api.registry;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class ModernBetaRegistries {
    public static final ModernBetaRegistry<ChunkSourceCreator> CHUNK;
    public static final ModernBetaRegistry<BiomeSourceCreator> BIOME;
    public static final ModernBetaRegistry<ModernBetaNoiseSettings> NOISE_SETTINGS;
    
    static {
        CHUNK = new ModernBetaRegistry<>("CHUNK");
        BIOME = new ModernBetaRegistry<>("BIOME");
        NOISE_SETTINGS = new ModernBetaRegistry<>("NOISE_SETTINGS");
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
}
