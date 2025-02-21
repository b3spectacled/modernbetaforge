package mod.bespectacled.modernbetaforge.world.spawn;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.WorldSpawner;
import net.minecraft.util.math.BlockPos;

public class FarLandsWorldSpawner implements WorldSpawner {
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        if (!(chunkSource instanceof NoiseChunkSource)) {
            return WorldSpawner.DEFAULT.locateSpawn(spawnPos, chunkSource, biomeSource);
        }

        NoiseSettings noiseSettings = ModernBetaRegistries.NOISE_SETTING.get(chunkSource.getGeneratorSettings().chunkSource);
        Random random = new Random();
        
        double coordinateScale = chunkSource.getGeneratorSettings().coordinateScale;
        double proportionalityX = 12550800.0 * 684.412;
        double proportionalityZ = 12550800.0 * 684.412;

        int x = (int)(proportionalityX / coordinateScale);
        int z = (int)(proportionalityZ / coordinateScale);
        
        x *= noiseSettings.sizeHorizontal;
        z *= noiseSettings.sizeHorizontal;
        
        x -= 512 - random.nextInt(256) - random.nextInt(256);
        z -= 512 - random.nextInt(256) - random.nextInt(256);
        
        x = random.nextBoolean() ? x : random.nextBoolean() ? -x : 0;
        z = random.nextBoolean() ? z : random.nextBoolean() || x == 0 ? -z : 0;
        
        return WorldSpawner.DEFAULT.locateSpawn(new BlockPos(x, 0, z), chunkSource, biomeSource);
    }
}
