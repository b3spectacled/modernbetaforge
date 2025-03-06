package mod.bespectacled.modernbetaforge.world.carver;

import java.util.Set;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineExtended extends MapGenRavine {
    @SuppressWarnings("unused") private final Block defaultBlock;
    private final Set<Block> defaultFluids;

    public MapGenRavineExtended(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.defaultBlock = chunkSource.getDefaultBlock().getBlock();
        this.defaultFluids = MapGenBetaCave.getDefaultFluids(chunkSource.getDefaultFluid());
    }
    
    @Override
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        return this.defaultFluids.contains(data.getBlockState(x, y, z).getBlock());
    }
}
