package mod.bespectacled.modernbetaforge.world.carver;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

public class MapGenCavesExtended extends MapGenCaves {
    private final Block defaultBlock;
    private final Set<Block> defaultFluids;
    private final Set<Block> carvableBlocks;
    
    private final int worldFloor;
    private final MutableBlockPos mutablePos;
    
    public MapGenCavesExtended(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.defaultBlock = chunkSource.getDefaultBlock().getBlock();
        this.defaultFluids = MapGenBetaCave.getDefaultFluids(chunkSource.getDefaultFluid());
        this.carvableBlocks = this.initCarvableBlocks(this.defaultBlock, MapGenBetaCave.getAdditionalCarvableBlocks(chunkSource, settings)).build();
        
        this.worldFloor = chunkSource.getWorldFloor();
        this.mutablePos = new MutableBlockPos();
    }
    
    @Override
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        Block block = data.getBlockState(x, y, z).getBlock();
        
        return this.defaultFluids.contains(block);
    }
    
    @Override
    protected boolean canReplaceBlock(IBlockState blockState, IBlockState blockStateUp) {
        return super.canReplaceBlock(blockState, blockStateUp) || this.carvableBlocks.contains(blockState.getBlock());
    }
    
    @Override
    protected void digBlock(ChunkPrimer chunkPrimer, int x, int y, int z, int chunkX, int chunkZ, boolean isTopSoil, IBlockState blockState, IBlockState blockStateUp) {
        Biome biome = this.world.getBiome(this.mutablePos.setPos(x + chunkX * 16, 0, z + chunkZ * 16));
        
        Block block = blockState.getBlock();
        Block topBlock = biome.topBlock.getBlock();
        Block fillerBlock = biome.fillerBlock.getBlock();

        if (this.canReplaceBlock(blockState, blockStateUp) || block == topBlock || block == fillerBlock) {
            if (y - 1 < this.worldFloor + ModernBetaGeneratorSettings.CARVER_LAVA_LEVEL) {
                chunkPrimer.setBlockState(x, y, z, BlockStates.LAVA);
            } else {
                chunkPrimer.setBlockState(x, y, z, BlockStates.AIR);

                if (isTopSoil && chunkPrimer.getBlockState(x, y - 1, z).getBlock() == fillerBlock) {
                    chunkPrimer.setBlockState(x, y - 1, z, topBlock.getDefaultState());
                }
            }
        }
    }

    protected ImmutableSet.Builder<Block> initCarvableBlocks(Block defaultBlock, List<Block> additionalCarvableBlocks) {
        ImmutableSet.Builder<Block> carvableBlocks = new ImmutableSet.Builder<>();
        carvableBlocks.add(defaultBlock).add(defaultBlock);
        carvableBlocks.addAll(additionalCarvableBlocks);
        
        return carvableBlocks;
    }
}
