package mod.bespectacled.modernbetaforge.world.spawn;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.WorldSpawner;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import net.minecraft.util.math.BlockPos;

public class InfdevWorldSpawner implements WorldSpawner {
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        return new BlockPos(0, chunkSource.getHeight(0, 0, HeightmapChunk.Type.OCEAN) + 1, 0);
    }
}
