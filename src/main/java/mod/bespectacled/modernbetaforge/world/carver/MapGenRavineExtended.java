package mod.bespectacled.modernbetaforge.world.carver;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineExtended extends MapGenRavine {
    @SuppressWarnings("unused") private final Block defaultBlock;
    private final BlockStaticLiquid defaultFluid;
    private final BlockDynamicLiquid defaultFlowing;
    
    public MapGenRavineExtended(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.defaultBlock = chunkSource.getDefaultBlock().getBlock();
        this.defaultFluid = (BlockStaticLiquid)chunkSource.getDefaultFluid().getBlock();
        this.defaultFlowing = BlockLiquid.getFlowingBlock(chunkSource.getDefaultFluid().getMaterial());
    }
    
    @Override
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        Block block = data.getBlockState(x, y, z).getBlock();
        
        return block == this.defaultFluid || block == this.defaultFlowing;
    }
}
