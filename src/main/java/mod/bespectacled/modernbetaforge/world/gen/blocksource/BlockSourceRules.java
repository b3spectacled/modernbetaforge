package mod.bespectacled.modernbetaforge.world.gen.blocksource;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;

public class BlockSourceRules implements BlockSource {
    private final List<BlockSource> rules;
    private final IBlockState defaultBlock;
    
    private BlockSourceRules(List<BlockSource> blockSources, IBlockState defaultBlock) {
        this.rules = blockSources;
        this.defaultBlock = defaultBlock;
    }

    @Override
    public IBlockState sample(int x, int y, int z) {
        for (BlockSource blockSource : this.rules) {
            IBlockState blockState = blockSource.sample(x, y, z);
            
            if (blockState == null) continue;
            
            return blockState;
        }
        
        return this.defaultBlock;
    }
    
    public static class Builder {
        private final List<BlockSource> rules;
        
        public Builder() {
            this.rules = new ArrayList<>();
        }
        
        public Builder add(BlockSource blockSource) {
            this.rules.add(blockSource);
            
            return this;
        }
        
        public BlockSourceRules build(IBlockState defaultBlock) {
            return new BlockSourceRules(this.rules, defaultBlock);
        }
    }
}