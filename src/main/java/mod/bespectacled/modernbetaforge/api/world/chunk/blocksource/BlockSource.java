package mod.bespectacled.modernbetaforge.api.world.chunk.blocksource;

import net.minecraft.block.state.IBlockState;

@FunctionalInterface
public interface BlockSource {
    /**
     * Samples a block state for initial terrain generation.
     * 
     * @param x x-coordinate in block coordinates.
     * @param y y-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @return Block state at given coordinates. May be null.
     */
    public IBlockState sample(int x, int y, int z);
}