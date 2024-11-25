package mod.bespectacled.modernbetaforge.world.chunk.blocksource;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import net.minecraft.block.state.IBlockState;

public class BlockSourceDefault implements BlockSource {
    private IBlockState blockState;
    
    public BlockSourceDefault() {
        this.blockState = BlockStates.AIR;
    }
    
    public void setBlockState(IBlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public IBlockState sample(int x, int y, int z) {
        return this.blockState;
    }
}
