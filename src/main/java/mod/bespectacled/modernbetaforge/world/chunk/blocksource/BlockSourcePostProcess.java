package mod.bespectacled.modernbetaforge.world.chunk.blocksource;

import java.util.HashSet;
import java.util.Set;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class BlockSourcePostProcess implements BlockSource {
    private final Set<Block> baseBlocks;
    private final IBlockState defaultBlock;
    private final IBlockState defaultFluid;
    private final int seaLevel;
    
    private IBlockState blockState;
    private double density;
    
    public BlockSourcePostProcess(IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel) {
        this.baseBlocks = new HashSet<>();
        this.defaultBlock = defaultBlock;
        this.defaultFluid = defaultFluid;
        this.seaLevel = seaLevel;
        
        this.blockState = BlockStates.AIR;
        this.density = 0.0;
        
        this.baseBlocks.add(this.defaultBlock.getBlock());
        this.baseBlocks.add(this.defaultFluid.getBlock());
        this.baseBlocks.add(Blocks.AIR);
    }
    
    public void setBlockState(IBlockState blockState) {
        this.blockState = blockState;
    }
    
    public void setDensity(double density) {
        this.density = density;
    }

    @Override
    public IBlockState sample(int x, int y, int z) {
        IBlockState blockState = this.baseBlocks.contains(this.blockState.getBlock()) ? BlockStates.AIR : this.blockState;
        
        if (this.density > 0.0) {
            blockState = this.defaultBlock;
        } else if (y < this.seaLevel) {
            blockState = this.defaultFluid;
        }
        
        return blockState;
    }
}
