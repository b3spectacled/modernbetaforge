package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class InfdevSurfaceBuilder extends SurfaceBuilder {
    public InfdevSurfaceBuilder(World world, NoiseChunkSource chunkSource, ModernBetaChunkGeneratorSettings settings) {
        super(world, chunkSource, settings);
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        double scale = 0.03125;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        int worldHeight = this.settings.height;
        int seaLevel = this.settings.seaLevel;
        boolean useSandstone = this.settings.useSandstone;
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);
        Random bedrockRandom = this.createSurfaceRandom(chunkX, chunkZ);
        Random sandstoneRandom = this.createSurfaceRandom(chunkX, chunkZ);

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                boolean genSandBeach = this.chunkSource.getBeachOctaveNoise().get().sample(
                    x * scale,
                    z * scale,
                    0.0
                ) + random.nextDouble() * 0.2 > 0.0;
                
                boolean genGravelBeach = this.chunkSource.getBeachOctaveNoise().get().sample(
                    z * scale, 
                    109.0134,
                    x * scale
                ) + random.nextDouble() * 0.2 > 3.0;
                
                double surfaceNoise = this.chunkSource.getSurfaceOctaveNoise().get().sampleXY(
                    x * scale * 2.0,
                    z * scale * 2.0
                );
                
                int surfaceDepth = (int)(surfaceNoise / 3.0 + 3.0 + random.nextDouble() * 0.25);
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];

                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;

                // Skip if used custom surface generation or if below minimum surface level.
                if (this.useCustomSurfaceBuilder(biome, chunkPrimer, random, x, z)) {
                    continue;
                }
                
                for (int y = worldHeight - 1; y >= 0; --y) {
                    
                    // Place bedrock
                    if (y <= bedrockRandom.nextInt(5)) {
                        chunkPrimer.setBlockState(localX, y, localZ, BlockStates.BEDROCK);
                        continue;
                    }
                    
                    IBlockState blockState = chunkPrimer.getBlockState(localX, y, localZ);
                    
                    if (BlockStates.isAir(blockState)) { // Skip if air block
                        runDepth = -1;
                        
                    } else if (BlockStates.isEqual(blockState, this.defaultBlock)) {
                        if (runDepth == -1) {
                            if (surfaceDepth <= 0) {
                                topBlock = BlockStates.AIR;
                                fillerBlock = this.defaultBlock;
                                
                            } else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                                topBlock = biome.topBlock;
                                fillerBlock = biome.fillerBlock;
                                
                                if (genGravelBeach) {
                                    topBlock = BlockStates.AIR;
                                    fillerBlock = BlockStates.GRAVEL;
                                }
                                
                                if (genSandBeach) {
                                    topBlock = BlockStates.SAND;
                                    fillerBlock = BlockStates.SAND;
                                }
                            }
                            
                            runDepth = surfaceDepth;
                            
                            if (y < seaLevel && BlockStates.isAir(topBlock)) { // Generate water bodies
                                topBlock = this.defaultFluid;
                            }
                            
                            blockState = y >= seaLevel - 1 || (y < seaLevel - 1 && BlockStates.isAir(chunkPrimer.getBlockState(localX, y + 1, localZ))) ?
                                topBlock : 
                                fillerBlock;
                            
                            chunkPrimer.setBlockState(localX, y, localZ, blockState);
                            
                        } else if (runDepth > 0) {
                            chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);
                            
                            --runDepth;
                        }
                    }

                    // Generates layer of sandstone starting at lowest block of sand, of height 1 to 4.
                    if (useSandstone && runDepth == 0 && BlockStates.isEqual(fillerBlock, BlockStates.SAND)) {
                        runDepth = sandstoneRandom.nextInt(4);
                        fillerBlock = BlockStates.SANDSTONE;
                    }
                }
            }
        }
    }

}
