package mod.bespectacled.modernbetaforge.world.carver;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenBetaCaveHell extends MapGenBetaCave {
    public MapGenBetaCaveHell(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings);
    }
    
    public MapGenBetaCaveHell() {
        super(BlockStates.NETHERRACK, BlockStates.LAVA, BlockStates.AIR, 1.0f, 128, 10, 5);
    }
    
    @Override
    protected int getCaveY(Random random) {
        return random.nextInt(this.caveHeight);
    }
    
    @Override
    protected float getTunnelSystemWidth(Random random, Random tunnelRandom) {
        return this.getBaseTunnelSystemWidth(random) * 2.0f;
    }

    @Override
    protected double getTunnelWHRatio() {
        return 0.5;
    }
    
    @Override
    protected void carveAtPoint(ChunkPrimer chunkPrimer, int localX, int localY, int localZ, Block block, boolean isGrassBlock) {
        if (this.carvables.contains(block)) {
            chunkPrimer.setBlockState(localX, localY, localZ, Blocks.AIR.getDefaultState());
        }
    }
}
