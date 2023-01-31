package mod.bespectacled.modernbetaforge.world.chunk.blocksource;

import net.minecraft.block.state.IBlockState;

@FunctionalInterface
public interface BlockSource {
    public IBlockState sample(int x, int y, int z);
}