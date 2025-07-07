package mod.bespectacled.modernbetaforge.api.world.spawn;

import java.util.Random;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import net.minecraft.util.math.BlockPos;

public interface WorldSpawner {
    /**
     * Locates initial player spawn point.
     * 
     * @param spawnPos Starting player spawn block position.
     * @param chunkSource Modern Beta chunk source.
     * @param biomeSource Modern Beta biome source.
     * 
     * @return Block position for initial player spawn point.
     */
    BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource);
    
    /**
     * The default world spawner.
     * This will attempt to find solid ground with adjacent blocks to spawn the player.
     */
    public static final WorldSpawner DEFAULT = new WorldSpawner() {
        @Override
        public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
            int radius = 64;
            int minAdjacent = 9;
            
            int numTries = 0;
            int maxTries = 32768;
            
            Random random = new Random(chunkSource.getSeed());
            int centerX = spawnPos.getX() + random.nextInt(256) - 128;
            int centerZ = spawnPos.getZ() + random.nextInt(256) - 128;
            
            while (numTries < maxTries) {
                int r2 = radius * radius;

                for (int dX = centerX - radius; dX < centerX + radius; ++dX) {
                    for (int dZ = centerZ - radius; dZ < centerZ + radius; ++dZ) {
                        double distance = MathUtil.distance(centerX, centerZ, dX, dZ);
                        
                        if (distance < r2) {
                            int surfaceY = chunkSource.getHeight(dX, dZ, HeightmapChunk.Type.SURFACE);
                            int oceanY = chunkSource.getHeight(dX, dZ, HeightmapChunk.Type.OCEAN);
                            
                            if (surfaceY >= oceanY && surfaceY > 0) {
                                // Check if there are surrounding blocks, relevant for Skylands worlds
                                int numAdjacent = 0;
                                for (int aX = dX - 1; aX <= dX + 1; ++aX) {
                                    for (int aZ = dZ - 1; aZ <= dZ + 1; ++aZ) {
                                        int aY = chunkSource.getHeight(aX, aZ, HeightmapChunk.Type.SURFACE);
                                        
                                        if (aY >= surfaceY - 2 && aY <= surfaceY + 2) {
                                            numAdjacent++;
                                        }
                                    }
                                }
                                
                                // Only spawn if the spawn position is surrounded by blocks
                                if (numAdjacent >= minAdjacent) {
                                    ModernBeta.log(Level.DEBUG, String.format("Found spawn at %d/%d/%d with %d adjacent blocks..", dX, surfaceY + 1, dZ, numAdjacent));
                                    return new BlockPos(dX, surfaceY + 1, dZ);
                                }
                            }
                            
                            numTries++;
                        }
                    }
                }
                
                radius *= 2;
            }
            
            ModernBeta.log(Level.WARN, "Unable to locate a default spawn, using vanilla algorithm..");
            return null;
        }
    };
}
