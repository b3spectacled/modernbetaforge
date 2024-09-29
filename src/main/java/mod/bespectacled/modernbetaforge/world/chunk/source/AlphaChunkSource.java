package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class AlphaChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise minLimitOctaveNoise;
    private final PerlinOctaveNoise maxLimitOctaveNoise;
    private final PerlinOctaveNoise mainOctaveNoise;
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise scaleOctaveNoise;
    private final PerlinOctaveNoise depthOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    public AlphaChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings settings,
        long seed,
        boolean mapFeaturesEnabled,
        ModernBetaNoiseSettings noiseSettings
    ) {
        super(world, chunkGenerator, settings, seed, mapFeaturesEnabled, noiseSettings);

        // Noise Generators
        this.minLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.maxLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.mainOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.scaleOctaveNoise = new PerlinOctaveNoise(this.random, 10, true);
        this.depthOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);

        this.setForestOctaveNoise(this.forestOctaveNoise);
        this.setBeachOctaveNoise(this.beachOctaveNoise);
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        double scale = 0.03125;

        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        int bedrockFloor = this.worldMinY + this.bedrockFloor;
        
        Random rand = this.createSurfaceRandom(chunkX, chunkZ);
        
        double[] sandNoise = beachOctaveNoise.sampleAlpha(
            chunkX * 16, chunkZ * 16, 0.0,
            16, 16, 1,
            scale, scale, 1.0
        );
        
        double[] gravelNoise = beachOctaveNoise.sampleAlpha(
            chunkZ * 16, 109.0134, chunkX * 16,
            16, 1, 16,
            scale, 1.0, scale
        );
        
        double[] surfaceNoise = surfaceOctaveNoise.sampleAlpha(
            chunkX * 16, chunkZ * 16, 0.0,
            16, 16, 1,
            scale * 2.0, scale * 2.0, scale * 2.0
        );
        
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = startX + localX;
                int z = startZ + localZ;

                boolean genSandBeach = sandNoise[localX + localZ * 16] + rand.nextDouble() * 0.2 > 0.0;
                boolean genGravelBeach = gravelNoise[localX + localZ * 16] + rand.nextDouble() * 0.2 > 3.0;
                
                int surfaceDepth = (int) (surfaceNoise[localX + localZ * 16] / 3.0 + 3.0 + rand.nextDouble() * 0.25);
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];
                
                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;
                
                // Skip if used custom surface generation or if below minimum surface level.
                if (this.useCustomSurfaceBuilder(biome, chunkPrimer, rand, x, z)) {
                    continue;
                }
                
                // Generate from top to bottom of world
                for (int y = this.worldTopY - 1; y >= this.worldMinY; y--) {
                    
                    // Place bedrock
                    if (y <= bedrockFloor + rand.nextInt(6) - 1) {
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
                            
                        } else if (y >= this.seaLevel - 4 && y <= this.seaLevel + 1) { // Generate beaches at this y range
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
                        
                        if (y < this.seaLevel && BlockStates.isAir(topBlock)) {
                            topBlock = this.defaultFluid;
                        }

                        runDepth = surfaceDepth;
                        
                        if (y >= this.seaLevel - 1) {
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
                }
            }
        }
    }
    
    @Override
    protected void sampleNoiseColumn(
        double[] buffer,
        int startNoiseX,
        int startNoiseZ,
        int localNoiseX,
        int localNoiseZ
    ) {
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;

        double depthNoiseScaleX = this.settings.depthNoiseScaleX; // Default: 100
        double depthNoiseScaleZ = this.settings.depthNoiseScaleZ;
        
        double coordinateScale = this.settings.coordinateScale;
        double heightScale = this.settings.heightScale;
        
        double mainNoiseScaleX = this.settings.mainNoiseScaleX; // Default: 80
        double mainNoiseScaleY = this.settings.mainNoiseScaleY; // Default: 160
        double mainNoiseScaleZ = this.settings.mainNoiseScaleZ;

        double lowerLimitScale = this.settings.lowerLimitScale;
        double upperLimitScale = this.settings.upperLimitScale;
        
        double baseSize = this.settings.baseSize;
        double heightStretch = this.settings.stretchY;
        
        double scale = this.scaleOctaveNoise.sample(noiseX, 0, noiseZ, 1.0, 0.0, 1.0);
        double depth = this.depthOctaveNoise.sample(noiseX, 0, noiseZ, depthNoiseScaleX, 0.0, depthNoiseScaleZ);
        
        double islandOffset = this.getIslandOffset(noiseX, noiseZ);
        
        scale = (scale + 256.0) / 512.0;
        
        if (scale > 1.0) {
            scale = 1.0; 
        }

        depth /= 8000.0;
        
        if (depth < 0.0) {
            depth = -depth;
        }

        depth = depth * 3.0 - 3.0;

        if (depth < 0.0) {
            depth /= 2.0;
            if (depth < -1.0) {
                depth = -1.0;
            }

            depth /= 1.4;
            depth /= 2.0; // Omitting this creates the Infdev 20100611 generator.

            scale = 0.0;

        } else {
            if (depth > 1.0) {
                depth = 1.0;
            }
            depth /= 6.0;
        }

        scale += 0.5;
        depth = depth * baseSize / 8.0;
        depth = baseSize + depth * 4.0;
        
        
        for (int noiseY = 0; noiseY < buffer.length; ++noiseY) {
            
            double density;
            double densityOffset = this.getOffset(noiseY, heightStretch, depth, scale);

            double mainNoise = (this.mainOctaveNoise.sample(
                noiseX, noiseY, noiseZ,
                coordinateScale / mainNoiseScaleX, 
                heightScale / mainNoiseScaleY, 
                coordinateScale / mainNoiseScaleZ
            ) / 10.0 + 1.0) / 2.0;
            
            if (mainNoise < 0.0) {
                density = this.minLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / lowerLimitScale;
                
            } else if (mainNoise > 1.0) {
                density = this.maxLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / upperLimitScale;
                
            } else {
                double minLimitNoise = this.minLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / lowerLimitScale;
                
                double maxLimitNoise = this.maxLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / upperLimitScale;
                
                density = minLimitNoise + (maxLimitNoise - minLimitNoise) * mainNoise;
            }

            density -= densityOffset;
            density += islandOffset;
            density = this.applySlides(density, noiseY);
            
            buffer[noiseY] = density;
        }
    }
    
    private double getOffset(int noiseY, double heightStretch, double depth, double scale) {
        double offset = (((double)noiseY - depth) * heightStretch) / scale;
        
        if (offset < 0.0)
            offset *= 4.0;
        
        return offset;
    }
}
