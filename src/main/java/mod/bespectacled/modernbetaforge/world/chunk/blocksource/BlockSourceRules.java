package mod.bespectacled.modernbetaforge.world.chunk.blocksource;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import net.minecraft.block.state.IBlockState;

public class BlockSourceRules implements BlockSource {
    private final List<BlockSource> rules;
    private final IBlockState defaultBlock;
    
    private BlockSourceRules(List<BlockSource> rules, IBlockState defaultBlock) {
        this.rules = rules;
        this.defaultBlock = defaultBlock;
    }

    @Override
    public IBlockState sample(int x, int y, int z) {
        for (BlockSource blockSource : this.rules) {
            IBlockState blockState = blockSource.sample(x, y, z);
            
            if (blockState == null) continue;
            
            return blockState;
        }
        
        return ModernBetaConfig.debugOptions.debugBlockSources ? BlockStates.AIR : this.defaultBlock;
    }
    
    public static class Builder {
        private final List<BlockSource> rules;
        private final IBlockState defaultBlock;
        
        public Builder(IBlockState defaultBlock) {
            this.rules = new LinkedList<>();
            this.defaultBlock = defaultBlock;
        }
        
        public Builder add(BlockSource blockSource) {
            this.rules.add(blockSource);
            
            return this;
        }
        
        public Builder add(Collection<BlockSource> blockSources) {
            this.rules.addAll(blockSources);
            
            return this;
        }
        
        public BlockSourceRules build() {
            return new BlockSourceRules(this.rules, this.defaultBlock);
        }
    }
}