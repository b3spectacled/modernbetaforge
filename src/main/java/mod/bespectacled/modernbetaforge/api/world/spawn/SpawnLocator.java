package mod.bespectacled.modernbetaforge.api.world.spawn;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SpawnLocator {
    /**
     * Locates initial player spawn point.
     * 
     * @param world The world object.
     * @param spawnPos Starting player spawn block position.
     * @param chunkSource Modern Beta chunk source.
     * @param biomeSource Modern Beta biome source.
     * 
     * @return Block position for initial player spawn point.
     */
    BlockPos locateSpawn(World world, BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource);
    
    /**
     * The default spawn locator.
     * This will attempt to find solid ground with adjacent blocks to spawn the player.
     */
    public static final SpawnLocator DEFAULT = new SpawnLocator() {
        @Override
        public BlockPos locateSpawn(World world, BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
            int centerX = spawnPos.getX();
            int centerZ = spawnPos.getZ();
            int radius = 64;
            int minAdjacent = 9;
            
            while(true) {
                int r2 = radius * radius;

                for (int dX = centerX - radius; dX < centerX + radius; ++dX) {
                    for (int dZ = centerZ - radius; dZ < centerZ + radius; ++dZ) {
                        double distance = MathUtil.distance(centerX, centerZ, dX, dZ);
                        
                        if (distance < r2) {
                            int y = chunkSource.getHeight(world, dX, dZ, HeightmapChunk.Type.SURFACE);
                            
                            if (y >= chunkSource.getSeaLevel()) {
                                // Check if there are surrounding blocks, relevant for Skylands worlds
                                int numAdjacent = 0;
                                for (int aX = dX - 1; aX <= dX + 1; ++aX) {
                                    for (int aZ = dZ - 1; aZ <= dZ + 1; ++aZ) {
                                        int aY = chunkSource.getHeight(world, aX, aZ, HeightmapChunk.Type.SURFACE);
                                        
                                        if (aY >= y - 2 && aY <= y + 2) {
                                            numAdjacent++;
                                        }
                                    }
                                }
                                
                                // Only spawn if the spawn position is surrounded by blocks
                                if (numAdjacent >= minAdjacent) {
                                    return new BlockPos(dX, y + 1, dZ);
                                }
                            }
                        }
                    }
                }
                
                radius *= 2;
            }
        }
    };
}
