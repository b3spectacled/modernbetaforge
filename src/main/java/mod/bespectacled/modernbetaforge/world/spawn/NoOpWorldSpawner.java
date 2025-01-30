package mod.bespectacled.modernbetaforge.world.spawn;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.WorldSpawner;
import net.minecraft.util.math.BlockPos;

public class NoOpWorldSpawner implements WorldSpawner {
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        return null;
    }
}
