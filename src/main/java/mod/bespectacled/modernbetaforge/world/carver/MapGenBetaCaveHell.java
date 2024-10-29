package mod.bespectacled.modernbetaforge.world.carver;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenBetaCaveHell extends MapGenBetaCave {
    public MapGenBetaCaveHell() {
        super(Blocks.LAVA, Blocks.FLOWING_LAVA);
    }
    
    @Override
    protected int getBaseCaveCount() {
        return 10;
    }
    
    @Override
    protected int getRegionalCaveChance() {
        return 5;
    }
    
    @Override
    protected int getCaveY(Random random) {
        return random.nextInt(128);
    }
    
    @Override
    protected float getTunnelSystemWidth(Random random) {
        return super.getTunnelSystemWidth(random) * 2.0f;
    }

    @Override
    protected double getTunnelWHRatio() {
        return 0.5;
    }
    
    @Override
    protected void carveAtPoint(ChunkPrimer chunkPrimer, int localX, int localY, int localZ, Block block, boolean isGrassBlock) {
        if (block == Blocks.NETHERRACK || block == Blocks.DIRT || block == Blocks.GRASS) {
            chunkPrimer.setBlockState(localX, localY, localZ, Blocks.AIR.getDefaultState());
        }
    }
}
