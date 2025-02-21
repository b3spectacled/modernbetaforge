package mod.bespectacled.modernbetaforge.world.carver;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineExtended extends MapGenRavine {
    @SuppressWarnings("unused") private final Block defaultBlock;
    private final Set<Block> defaultFluids;

    public MapGenRavineExtended(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.defaultBlock = chunkSource.getDefaultBlock().getBlock();
        this.defaultFluids = new HashSet<>();
        
        this.defaultFluids.add(chunkSource.getDefaultFluid().getBlock());
        try {
            this.defaultFluids.add(BlockLiquid.getFlowingBlock(chunkSource.getDefaultFluid().getMaterial()));
        } catch (Exception e) {
            ModernBeta.log(Level.DEBUG, "Ravine carver fluid is not flowable!");
        }
    }
    
    @Override
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        return this.defaultFluids.contains(data.getBlockState(x, y, z).getBlock());
    }
}
