package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class Infdev227SurfaceBuilder extends SurfaceBuilder {    
    public Infdev227SurfaceBuilder(World world, ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(world, chunkSource, settings);
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int depth = 1;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);

        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];

                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;
                
                // Skip if used custom surface generation
                if (this.useCustomSurfaceBuilder(biome, chunkPrimer, random, x, z, false)) {
                    continue;
                }

                // Generate from top to bottom of world
                for (int y = this.getWorldHeight() - 1; y >= 0; y--) {

                    // Randomly place bedrock from y0 (or minHeight) to y5
                    if (this.useBedrock() && y <= random.nextInt(5)) {
                        chunkPrimer.setBlockState(localX, y, localZ, BlockStates.BEDROCK);
                        continue;
                    }

                    IBlockState blockState = chunkPrimer.getBlockState(localX, y, localZ);

                    if (BlockStates.isAir(blockState)) { // Skip if air block
                        runDepth = -1;
                        continue;
                    }

                    if (!BlockStates.isEqual(blockState, this.defaultBlock)) { // Skip if not stone
                        continue;
                    }

                    if (runDepth == -1) {
                        if (y < this.getSeaLevel() && BlockStates.isAir(topBlock)) { // Generate water bodies
                            topBlock = this.defaultFluid;
                        }

                        runDepth = depth;
                        
                        if (y >= this.getSeaLevel() - 1) {
                            chunkPrimer.setBlockState(localX, y, localZ, topBlock);
                        } else {
                            chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);
                        }

                        continue;
                    }

                    if (runDepth <= 0) {
                        continue;
                    }

                    runDepth--;
                    chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);

                    // Generates layer of sandstone starting at lowest block of sand, of height 1 to 4.
                    if (this.useSandstone() && runDepth == 0 && BlockStates.isEqual(fillerBlock, BlockStates.SAND)) {
                        runDepth = random.nextInt(2);
                        fillerBlock = fillerBlock.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ?
                            BlockStates.RED_SANDSTONE :
                            BlockStates.SANDSTONE;
                    }
                }
            }
        }
    }
}
