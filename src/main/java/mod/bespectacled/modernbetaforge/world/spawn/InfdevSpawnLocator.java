package mod.bespectacled.modernbetaforge.world.spawn;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfdevSpawnLocator implements SpawnLocator {
    @Override
    public BlockPos locateSpawn(World world, BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        return new BlockPos(0, chunkSource.getHeight(world, 0, 0, HeightmapChunk.Type.OCEAN) + 1, 0);
    }
}
