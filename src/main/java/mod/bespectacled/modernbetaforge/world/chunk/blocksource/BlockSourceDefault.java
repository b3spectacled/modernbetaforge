package mod.bespectacled.modernbetaforge.world.chunk.blocksource;

import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import net.minecraft.block.state.IBlockState;

public class BlockSourceDefault implements BlockSource {
    private final IBlockState defaultBlock;
    private IBlockState blockState;
    
    public BlockSourceDefault(IBlockState defaultBlock) {
        this.defaultBlock = defaultBlock;
        this.blockState = BlockStates.AIR;
    }
    
    public void setBlockState(IBlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public IBlockState sample(int x, int y, int z) {
        if (this.blockState.equals(this.defaultBlock))
            return null;
        
        return this.blockState;
    }
}
