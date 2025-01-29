package mod.bespectacled.modernbetaforge.world.spawn;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import net.minecraft.util.math.BlockPos;

public class FarLandsSpawnLocator implements SpawnLocator {
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        if (!(chunkSource instanceof NoiseChunkSource)) {
            return SpawnLocator.DEFAULT.locateSpawn(spawnPos, chunkSource, biomeSource);
        }

        Random random = new Random();
        
        double coordinateScale = chunkSource.getGeneratorSettings().coordinateScale;
        double proportionalityX = 12550820.0 * 684.412;
        double proportionalityZ = 12550820.0 * 684.412;

        int x = (int)(proportionalityX / coordinateScale - random.nextDouble() * 64.0 - random.nextDouble() * 64.0);
        int z = (int)(proportionalityZ / coordinateScale - random.nextDouble() * 64.0 - random.nextDouble() * 64.0);
        
        x = random.nextBoolean() ? x : random.nextBoolean() ? -x : 0;
        z = random.nextBoolean() ? z : random.nextBoolean() ? -z : 0;
        
        return SpawnLocator.DEFAULT.locateSpawn(new BlockPos(x, 0, z), chunkSource, biomeSource);
    }
}
