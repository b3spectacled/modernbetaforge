package mod.bespectacled.modernbetaforge.world.spawn;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class FarLandsSpawnLocator implements SpawnLocator {
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        if (!(chunkSource instanceof NoiseChunkSource)) {
            return SpawnLocator.DEFAULT.locateSpawn(spawnPos, chunkSource, biomeSource);
        }

        ResourceLocation noiseSettingsKey = new ResourceLocation(chunkSource.getGeneratorSettings().chunkSource);
        NoiseSettings noiseSettings = ModernBetaRegistries.NOISE_SETTING.get(noiseSettingsKey);
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
        
        return SpawnLocator.DEFAULT.locateSpawn(new BlockPos(x, 0, z), chunkSource, biomeSource);
    }
}
