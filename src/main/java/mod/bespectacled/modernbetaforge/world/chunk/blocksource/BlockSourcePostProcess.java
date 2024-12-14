package mod.bespectacled.modernbetaforge.world.chunk.blocksource;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import net.minecraft.block.state.IBlockState;

public class BlockSourcePostProcess implements BlockSource {
    private final BlockSourceRules blockSources;
    private final Set<IBlockState> whitelist;
    private final IBlockState defaultFluid;
    private final int seaLevel;
    
    private IBlockState blockState;
    private double density;
    
    public BlockSourcePostProcess(IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel) {
        this(ImmutableSet.of(), defaultBlock,  defaultFluid, seaLevel);
    }
    
    public BlockSourcePostProcess(Set<IBlockState> whitelist, IBlockState defaultBlock, IBlockState defaultFluid, int seaLevel) {
        this.blockSources = new BlockSourceRules.Builder(defaultBlock)
            .add(this::sampleWhitelisted)
            .add(this::sampleInitial)
            .build();
        
        this.whitelist = whitelist;
        this.defaultFluid = defaultFluid;
        this.seaLevel = seaLevel;
        
        this.blockState = BlockStates.AIR;
        this.density = 0.0;
    }
    
    public void setBlockState(IBlockState blockState) {
        this.blockState = blockState;
    }
    
    public void setDensity(double density) {
        this.density = density;
    }

    @Override
    public IBlockState sample(int x, int y, int z) {
        return this.blockSources.sample(x, y, z);
    }
    
    public IBlockState sampleInitial(int x, int y, int z) {
        IBlockState blockState = BlockStates.AIR;
        
        if (this.density > 0.0) {
            blockState = null;
        } else if (y < this.seaLevel) {
            blockState = this.defaultFluid;
        }
        
        return blockState;
    }
    
    public IBlockState sampleWhitelisted(int x, int y, int z) {
        if (this.whitelist.contains(this.blockState)) {
            return this.blockState;
        }
        
        return null;
    }
}
