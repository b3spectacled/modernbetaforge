package mod.bespectacled.modernbetaforge.api.world.chunk.blocksource;

import net.minecraft.block.state.IBlockState;

@FunctionalInterface
public interface BlockSource {
    /**
     * Initialize the block source for the current chunk.
     * Useful for setting the chunk seed, etc.
     * 
     * @param chunkX x-coordinate in chunk coordinates.
     * @param chunkZ z-coordinate in chunk coordinates.
     */
    default void init(int chunkX, int chunkZ) { }
    
    /**
     * Samples a block state for initial terrain generation.
     * 
     * @param x x-coordinate in block coordinates.
     * @param y y-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @return Block state at given coordinates. May be null.
     */
    IBlockState sample(int x, int y, int z);
}