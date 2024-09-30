package mod.bespectacled.modernbetaforge.api.world.spawn;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import net.minecraft.util.math.BlockPos;

public interface SpawnLocator {
    /**
     * Locate initial player spawn point.
     * 
     * @param spawnPos Starting player spawn block position.
     * @param chunkSource Modern Beta chunk source.
     * @param biomeSource Modern Beta biome source.
     * 
     * @return Block position for initial player spawn point.
     */
    BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource);
    
    public static final SpawnLocator DEFAULT = new SpawnLocator() {
        @Override
        public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
            int centerX = spawnPos.getX();
            int centerZ = spawnPos.getZ();
            int radius = 64;
            
            while(true) {
                int r2 = radius * radius;

                for (int dX = centerX - radius; dX < centerX + radius; ++dX) {
                    for (int dZ = centerZ - radius; dZ < centerZ + radius; ++dZ) {
                        double distance = MathUtil.distance(centerX, centerZ, dX, dZ);
                        
                        if (distance < r2) {
                            int y = chunkSource.getHeight(dX, dZ, HeightmapChunk.Type.SURFACE);
                            
                            if (y > 16) {
                                return new BlockPos(dX, y + 1, dZ);
                            }
                        }
                    }
                }
                
                radius *= 2;
            }
        }
    };
}
