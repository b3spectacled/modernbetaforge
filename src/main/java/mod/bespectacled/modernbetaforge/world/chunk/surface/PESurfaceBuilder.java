package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.mersenne.MTRandom;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class PESurfaceBuilder extends SurfaceBuilder {
    public PESurfaceBuilder(World world, NoiseChunkSource chunkSource, ModernBetaChunkGeneratorSettings settings) {
        super(world, chunkSource, settings);
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        double scale = 0.03125;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        int worldHeight = this.getWorldHeight();
        int seaLevel = this.getSeaLevel();
        boolean useSandstone = this.useSandstone();
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);
        
        double[] sandNoise = this.getBeachOctaveNoise().sampleBeta(
            chunkX * 16, chunkZ * 16, 0.0, 
            16, 16, 1,
            scale, scale, 1.0
        );
        
        double[] gravelNoise = this.getBeachOctaveNoise().sampleBeta(
            chunkX * 16, 109.0134, chunkZ * 16, 
            16, 1, 16, 
            scale, 1.0, scale
        );
        
        double[] surfaceNoise = this.getSurfaceOctaveNoise().sampleBeta(
            chunkX * 16, chunkZ * 16, 0.0, 
            16, 16, 1,
            scale * 2.0, scale * 2.0, scale * 2.0
        );

        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                int x = startX + localX;
                int z = startZ + localZ;

                // MCPE uses nextFloat() instead of nextDouble()
                boolean genSandBeach = sandNoise[localZ + localX * 16] + random.nextFloat() * 0.2 > 0.0;
                boolean genGravelBeach = gravelNoise[localZ + localX * 16] + random.nextFloat() * 0.2 > 3.0;
                
                int surfaceDepth = (int) (surfaceNoise[localZ + localX * 16] / 3.0 + 3.0 + random.nextFloat() * 0.25);
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];

                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;
                
                // Skip if used custom surface generation
                if (this.useCustomSurfaceBuilder(biome, chunkPrimer, random, x, z)) {
                    continue;
                }

                // Generate from top to bottom of world
                for (int y = worldHeight - 1; y >= 0; y--) {

                    // Randomly place bedrock from y=0 (or minHeight) to y=5
                    if (y <= random.nextInt(5)) {
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
                        if (surfaceDepth <= 0) { // Generate stone basin if noise permits
                            topBlock = BlockStates.AIR;
                            fillerBlock = this.defaultBlock;
                            
                        } else if (y >= seaLevel - 4 && y <= seaLevel + 1) { // Generate beaches at this y range
                            topBlock = biome.topBlock;
                            fillerBlock = biome.fillerBlock;

                            if (genGravelBeach) {
                                topBlock = BlockStates.AIR; // This reduces gravel beach height by 1
                                fillerBlock = BlockStates.GRAVEL;
                            }

                            if (genSandBeach) {
                                topBlock = BlockStates.SAND;
                                fillerBlock = BlockStates.SAND;
                            }
                        }

                        if (y < seaLevel && BlockStates.isAir(topBlock)) { // Generate water bodies
                            topBlock = this.defaultFluid;
                        }

                        runDepth = surfaceDepth;
                        
                        if (y >= seaLevel - 1) {
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
                    if (useSandstone && runDepth == 0 && BlockStates.isEqual(fillerBlock, BlockStates.SAND)) {
                        runDepth = random.nextInt(4);
                        fillerBlock = BlockStates.SANDSTONE;
                    }
                }
            }
        }
    }
    
    /*
     * MCPE uses different values to seed random surface generation.
     */
    @Override
    protected Random createSurfaceRandom(int chunkX, int chunkZ) {
        long seed = (long)chunkX * 0x14609048 + (long)chunkZ * 0x7ebe2d5;
        
        return new MTRandom(seed);
    }

}
