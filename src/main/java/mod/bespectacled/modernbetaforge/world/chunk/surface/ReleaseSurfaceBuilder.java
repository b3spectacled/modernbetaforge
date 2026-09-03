package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class ReleaseSurfaceBuilder extends SurfaceBuilder {
    public ReleaseSurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings);
    }

    @Override
    public void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);
        IBlockState[] blockColumn = new IBlockState[this.getWorldHeight() - this.getWorldFloor() + 1];

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                Biome biome = biomes[localX + localZ * 16];
                this.preProcessBedrock(chunkPrimer, localX, localZ, blockColumn);
                this.useCustomSurfaceBuilder(world, biome, chunkPrimer, random, x, z, true);
                this.postProcessBedrock(chunkPrimer, localX, localZ, random, blockColumn);
            }
        }
    }
    
    private void preProcessBedrock(ChunkPrimer chunkPrimer, int localX, int localZ, IBlockState[] blockColumn) {
        // Pre-process bedrock for variable height (extended) worlds
        if (this.getWorldFloor() != 0) {
            for (int y = this.getWorldHeight(); y >= this.getWorldFloor(); y--) {
                blockColumn[y - this.getWorldFloor()] = chunkPrimer.getBlockState(localX, y, localZ);
            }
        }
    }
    
    private void postProcessBedrock(ChunkPrimer chunkPrimer, int localX, int localZ, Random random, IBlockState[] blockColumn) {
        // Post-process bedrock for variable height (extended) worlds
        if (this.getWorldFloor() != 0) {
            for (int y = this.getWorldHeight(); y >= this.getWorldFloor(); y--) {
                IBlockState blockState = chunkPrimer.getBlockState(localX, y, localZ);
                IBlockState prevBlockState = blockColumn[y - this.getWorldFloor()];
                
                // Replace vanilla bedrock layer with pre-surface generation block,
                // which would have been supplied by the block source rules
                if (blockState.getBlock() == Blocks.BEDROCK) {
                    chunkPrimer.setBlockState(localX, y, localZ, prevBlockState);
                }
                
                if (this.isBedrock(y, random)) {
                    chunkPrimer.setBlockState(localX, y, localZ, BlockStates.BEDROCK);
                }
            }
        }
    }
    
    @Override
    public boolean replacesDefaultBlock() {
        return this.defaultBlock.equals(BlockStates.STONE);
    }
}
