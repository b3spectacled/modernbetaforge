package mod.bespectacled.modernbetaforge.world.chunk.blocksource;

import java.util.ArrayList;
import java.util.List;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import net.minecraft.block.state.IBlockState;

public class BlockSourceRules implements BlockSource {
    private final List<BlockSource> rules;
    
    private BlockSourceRules(List<BlockSource> blockSources) {
        this.rules = blockSources;
    }

    @Override
    public IBlockState sample(int x, int y, int z) {
        for (BlockSource blockSource : this.rules) {
            IBlockState blockState = blockSource.sample(x, y, z);
            
            if (blockState == null) continue;
            
            return blockState;
        }
        
        return BlockStates.AIR;
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
        
        public BlockSourceRules build() {
            return new BlockSourceRules(this.rules);
        }
    }
}