package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.spawn.InfdevSpawnLocator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class Infdev420ChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise minLimitOctaveNoise;
    private final PerlinOctaveNoise maxLimitOctaveNoise;
    private final PerlinOctaveNoise mainOctaveNoise;
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    public Infdev420ChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings settings,
        ModernBetaNoiseSettings noiseSettings,
        long seed,
        boolean mapFeaturesEnabled
    ) {
        super(world, chunkGenerator, settings, noiseSettings, seed, mapFeaturesEnabled);

        this.minLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.maxLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.mainOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        new PerlinOctaveNoise(this.random, 5, true); // Unused in original source
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 5, true);

        this.setForestOctaveNoise(this.forestOctaveNoise);
        this.setBeachOctaveNoise(this.beachOctaveNoise);
    }
    
    @Override
    public SpawnLocator getSpawnLocator() {
        return new InfdevSpawnLocator();
    }
    
    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        double scale = 0.03125;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        int bedrockFloor = this.worldMinY + this.bedrockFloor;
        
        Random rand = this.createSurfaceRandom(chunkX, chunkZ);
        Random bedrockRand = this.createSurfaceRandom(chunkX, chunkZ);

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                boolean genSandBeach = this.beachOctaveNoise.sample(
                    x * scale,
                    z * scale,
                    0.0
                ) + rand.nextDouble() * 0.2 > 0.0;
                
                boolean genGravelBeach = this.beachOctaveNoise.sample(
                    z * scale, 
                    109.0134,
                    x * scale
                ) + rand.nextDouble() * 0.2 > 3.0;
                
                double surfaceNoise = this.surfaceOctaveNoise.sampleXY(
                    x * scale * 2.0,
                    z * scale * 2.0
                );
                
                int surfaceDepth = (int)(surfaceNoise / 3.0 + 3.0 + rand.nextDouble() * 0.25);
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];

                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;

                // Skip if used custom surface generation or if below minimum surface level.
                if (this.useCustomSurfaceBuilder(biome, chunkPrimer, rand, x, z)) {
                    continue;
                }
                
                for (int y = this.worldTopY - 1; y >= this.worldMinY; --y) {
                    
                    // Place bedrock
                    if (y <= bedrockFloor + bedrockRand.nextInt(5)) {
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
                                
                            } else if (y >= this.seaLevel - 4 && y <= this.seaLevel + 1) {
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
                            
                            if (y < this.seaLevel && BlockStates.isAir(topBlock)) { // Generate water bodies
                                topBlock = this.defaultFluid;
                            }
                            
                            blockState = y >= this.seaLevel - 1 || (y < this.seaLevel - 1 && BlockStates.isAir(chunkPrimer.getBlockState(localX, y + 1, localZ))) ?
                                topBlock : 
                                fillerBlock;
                            
                            chunkPrimer.setBlockState(localX, y, localZ, blockState);
                            
                        } else if (runDepth > 0) {
                            chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);
                            
                            --runDepth;
                        }
                    }

                    // Generates layer of sandstone starting at lowest block of sand, of height 1 to 4.
                    if (this.settings.useSandstone && runDepth == 0 && BlockStates.isEqual(fillerBlock, BlockStates.SAND)) {
                        runDepth = rand.nextInt(4);
                        fillerBlock = BlockStates.SANDSTONE;
                    }
                }
            }
        }
    }
    
    @Override
    protected void sampleNoiseColumn(double[] buffer, int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        double coordinateScale = this.settings.coordinateScale;
        double heightScale = this.settings.heightScale;
        
        double mainNoiseScaleX = this.settings.mainNoiseScaleX;
        double mainNoiseScaleY = this.settings.mainNoiseScaleY;
        double mainNoiseScaleZ = this.settings.mainNoiseScaleZ;

        double lowerLimitScale = this.settings.lowerLimitScale;
        double upperLimitScale = this.settings.upperLimitScale;
        
        double baseSize = this.settings.baseSize;
        double heightStretch = this.settings.stretchY;
        
        for (int noiseY = 0; noiseY < buffer.length; ++noiseY) {
            double density;
            double densityOffset = this.getOffset(noiseY, baseSize, heightStretch);
            
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
            density = this.applySlides(density, noiseY);
            
            buffer[noiseY] = density;
        }
    }
    
    private double getOffset(int noiseY, double baseSize, double heightStretch) {
        double offset = ((double) noiseY - baseSize) * heightStretch;
        
        if (offset < 0.0)
            offset *= 2.0;
        
        return offset;
    }
}