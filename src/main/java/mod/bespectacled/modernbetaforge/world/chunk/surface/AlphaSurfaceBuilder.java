package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class AlphaSurfaceBuilder extends SurfaceBuilder {
    public AlphaSurfaceBuilder(World world, ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(world, chunkSource, settings);
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        double scale = 0.03125;

        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);
        Random sandstoneRandom = this.createSurfaceRandom(chunkX, chunkZ);
        
        double[] sandNoise = this.getBeachOctaveNoise().sampleAlpha(
            chunkX * 16, chunkZ * 16, 0.0,
            16, 16, 1,
            scale, scale, 1.0
        );
        
        double[] gravelNoise = this.getBeachOctaveNoise().sampleAlpha(
            chunkZ * 16, 109.0134, chunkX * 16,
            16, 1, 16,
            scale, 1.0, scale
        );
        
        double[] surfaceNoise = this.getSurfaceOctaveNoise().sampleAlpha(
            chunkX * 16, chunkZ * 16, 0.0,
            16, 16, 1,
            scale * 2.0, scale * 2.0, scale * 2.0
        );
        
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = startX + localX;
                int z = startZ + localZ;

                boolean genSandBeach = sandNoise[localX + localZ * 16] + random.nextDouble() * 0.2 > 0.0;
                boolean genGravelBeach = gravelNoise[localX + localZ * 16] + random.nextDouble() * 0.2 > 3.0;
                
                int surfaceDepth = (int) (surfaceNoise[localX + localZ * 16] / 3.0 + 3.0 + random.nextDouble() * 0.25);
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];
                
                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;
                
                // Skip if used custom surface generation or if below minimum surface level.
                if (this.useCustomSurfaceBuilder(biome, chunkPrimer, random, x, z, false)) {
                    continue;
                }
                
                // Generate from top to bottom of world
                for (int y = this.getWorldHeight() - 1; y >= 0; y--) {
                    
                    // Place bedrock
                    if (this.useBedrock() && y <= random.nextInt(6) - 1) {
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
                            
                        } else if (y >= this.getSeaLevel() - 4 && y <= this.getSeaLevel() + 1) { // Generate beaches at this y range
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
                        
                        if (y < this.getSeaLevel() && BlockStates.isAir(topBlock)) {
                            topBlock = this.defaultFluid;
                        }

                        runDepth = surfaceDepth;
                        
                        if (y >= this.getSeaLevel() - 1) {
                            chunkPrimer.setBlockState(localX, y, localZ, topBlock);
                        } else {
                            chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);
                        }

                        continue;
                    }
                    
                    if (runDepth > 0) { 
                        runDepth--;
                        chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);
                    }

                    // Generates layer of sandstone starting at lowest block of sand, of height 1 to 4.
                    if (this.useSandstone() && runDepth == 0 && BlockStates.isEqual(fillerBlock, BlockStates.SAND)) {
                        runDepth = sandstoneRandom.nextInt(4);
                        fillerBlock = fillerBlock.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ?
                            BlockStates.RED_SANDSTONE :
                            BlockStates.SANDSTONE;
                    }
                }
            }
        }
    }
}
