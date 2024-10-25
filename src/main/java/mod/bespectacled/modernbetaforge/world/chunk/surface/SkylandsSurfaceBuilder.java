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

public class SkylandsSurfaceBuilder extends SurfaceBuilder {
    public SkylandsSurfaceBuilder(World world, NoiseChunkSource chunkSource, ModernBetaChunkGeneratorSettings settings) {
        super(world, chunkSource, settings);
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        double scale = 0.03125;

        int startX = chunkX * 16;
        int startZ = chunkZ * 16;

        int worldHeight = this.settings.height;
        boolean useSandstone = this.settings.useSandstone;

        Random random = this.createSurfaceRandom(chunkX, chunkZ);
        
        double[] surfaceNoise = this.chunkSource.getSurfaceOctaveNoise().get().sampleBeta(
            chunkX * 16, chunkZ * 16, 0.0, 
            16, 16, 1,
            scale * 2.0, scale * 2.0, scale * 2.0
        );
        
        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                int x = startX + localX; 
                int z = startZ + localZ;

                int surfaceDepth = (int) (surfaceNoise[localZ + localX * 16] / 3.0 + 3.0 + random.nextDouble() * 0.25);
                
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];

                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;

                // Skip if used custom surface generation or if below minimum surface level.
                if (this.useCustomSurfaceBuilder(biome, chunkPrimer, random, x, z)) {
                    continue;
                }
                
                // Generate from top to bottom of world
                for (int y = worldHeight - 1; y >= 0; y--) {
                    
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
                        }

                        runDepth = surfaceDepth;
                        
                        blockState = (y >= 0) ? 
                            topBlock : 
                            fillerBlock;
                        
                        chunkPrimer.setBlockState(localX, y, localZ, blockState);

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
}
