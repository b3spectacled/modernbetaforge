package mod.bespectacled.modernbetaforge.compat.depthsupdate;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.state.IBlockState;

public class BlockSourceDeepslate implements BlockSource {
    private final Random random;
    private final boolean useDeepslate;
    private final IBlockState deepslateBlock;
    private final int deepslateMaxY;
    private final int deepslateRange;
    
    public BlockSourceDeepslate(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.random = new Random();
        this.useDeepslate = settings.getBooleanProperty(CompatDepthsUpdate.KEY_USE_COMPAT) && settings.getBooleanProperty(CompatDepthsUpdate.KEY_USE_DEEPSLATE);
        this.deepslateBlock = settings.getBlockProperty(CompatDepthsUpdate.KEY_DEEPSLATE_BLOCK).getDefaultState();
        this.deepslateMaxY = settings.getIntProperty(CompatDepthsUpdate.KEY_DEEPSLATE_MAX_Y);
        this.deepslateRange = settings.getIntProperty(CompatDepthsUpdate.KEY_DEEPSLATE_RANGE);
    }
    
    @Override
    public void init(int chunkX, int chunkZ) {
        this.random.setSeed((long)chunkX * 0x4f9939f508L + (long)chunkZ * 0x1ef1565bd5L);
    }
    
    @Override
    public IBlockState sample(int x, int y, int z) {
        if (!this.useDeepslate || y >= this.deepslateMaxY) {
            return null;
        }
        
        if (y <= this.deepslateMaxY - this.deepslateRange) {
            return this.deepslateBlock;
        }
        
        double chance = (this.deepslateMaxY - y) / (double)this.deepslateRange;
        return this.random.nextDouble() < chance ? this.deepslateBlock : null;
    }

}
