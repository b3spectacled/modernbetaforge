package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.mersenne.MTRandom;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.source.PEBiomeSource;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.spawn.PESpawnLocator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class PEChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise minLimitOctaveNoise;
    private final PerlinOctaveNoise maxLimitOctaveNoise;
    private final PerlinOctaveNoise mainOctaveNoise;
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise scaleOctaveNoise;
    private final PerlinOctaveNoise depthOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    private final ClimateSampler climateSampler;
    private final Random random;

    public PEChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings settings,
        ModernBetaNoiseSettings noiseSettings,
        long seed,
        boolean mapFeaturesEnabled
    ) {
        super(world, chunkGenerator, settings, noiseSettings, seed, mapFeaturesEnabled);
        
        // Use Mersenne Twister random instead of Java random
        this.random = new MTRandom(seed);
        
        this.minLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.maxLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.mainOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.scaleOctaveNoise = new PerlinOctaveNoise(this.random, 10, true);
        this.depthOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        
        this.climateSampler = this.biomeProvider.getBiomeSource() instanceof ClimateSampler ?
            (ClimateSampler)this.biomeProvider.getBiomeSource() :
            new PEBiomeSource(world.getWorldInfo());
        
        this.setForestOctaveNoise(this.forestOctaveNoise);
        this.setBeachOctaveNoise(this.beachOctaveNoise);
    }

    @Override
    public SpawnLocator getSpawnLocator() {
        return new PESpawnLocator();
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        double scale = 0.03125;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        int bedrockFloor = this.worldMinY + this.bedrockFloor;
        
        Random rand = this.createSurfaceRandom(chunkX, chunkZ);
        
        double[] sandNoise = this.beachOctaveNoise.sampleBeta(
            chunkX * 16, chunkZ * 16, 0.0, 
            16, 16, 1,
            scale, scale, 1.0
        );
        
        double[] gravelNoise = this.beachOctaveNoise.sampleBeta(
            chunkX * 16, 109.0134, chunkZ * 16, 
            16, 1, 16, 
            scale, 1.0, scale
        );
        
        double[] surfaceNoise = this.surfaceOctaveNoise.sampleBeta(
            chunkX * 16, chunkZ * 16, 0.0, 
            16, 16, 1,
            scale * 2.0, scale * 2.0, scale * 2.0
        );

        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                int x = startX + localX;
                int z = startZ + localZ;

                // MCPE uses nextFloat() instead of nextDouble()
                boolean genSandBeach = sandNoise[localZ + localX * 16] + rand.nextFloat() * 0.2 > 0.0;
                boolean genGravelBeach = gravelNoise[localZ + localX * 16] + rand.nextFloat() * 0.2 > 3.0;
                
                int surfaceDepth = (int) (surfaceNoise[localZ + localX * 16] / 3.0 + 3.0 + rand.nextFloat() * 0.25);
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];

                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;
                
                // Skip if used custom surface generation
                if (this.useCustomSurfaceBuilder(biome, chunkPrimer, rand, x, z)) {
                    continue;
                }

                // Generate from top to bottom of world
                for (int y = this.worldTopY - 1; y >= this.worldMinY; y--) {

                    // Randomly place bedrock from y=0 (or minHeight) to y=5
                    if (y <= bedrockFloor + rand.nextInt(5)) {
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

                        if (y < this.seaLevel && BlockStates.isAir(topBlock)) { // Generate water bodies
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

                    if (runDepth <= 0) {
                        continue;
                    }

                    runDepth--;
                    chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);

                    // Generates layer of sandstone starting at lowest block of sand, of height 1 to 4.
                    if (runDepth == 0 && BlockStates.isEqual(fillerBlock, BlockStates.SAND)) {
                        runDepth = rand.nextInt(4);
                        fillerBlock = BlockStates.SANDSTONE;
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
        int horizNoiseResolution = 16 / (this.noiseSizeX + 1);
        int x = (startNoiseX / this.noiseSizeX * 16) + localNoiseX * horizNoiseResolution + horizNoiseResolution / 2;
        int z = (startNoiseZ / this.noiseSizeZ * 16) + localNoiseZ * horizNoiseResolution + horizNoiseResolution / 2;
        
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        double depthNoiseScaleX = this.settings.depthNoiseScaleX; // Default: 200
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

        double scale = this.scaleOctaveNoise.sampleXZ(noiseX, noiseZ, 1.121, 1.121);
        double depth = this.depthOctaveNoise.sampleXZ(noiseX, noiseZ, depthNoiseScaleX, depthNoiseScaleZ);
        
        Clime clime = this.climateSampler.sample(x, z);
        double temp = clime.temp();
        double rain = clime.rain() * temp;
        
        rain = 1.0 - rain;
        rain *= rain;
        rain *= rain;
        rain = 1.0 - rain;

        scale = (scale + 256.0) / 512.0;
        scale *= rain;
        
        if (scale > 1.0) {
            scale = 1.0;
        }
        
        depth /= 8000.0;

        if (depth < 0.0) {
            depth = -depth * 0.3;
        }

        depth = depth * 3.0 - 2.0;

        if (depth < 0.0) {
            depth /= 2.0;

            if (depth < -1.0) {
                depth = -1.0;
            }

            depth /= 1.4;
            depth /= 2.0;

            scale = 0.0;

        } else {
            if (depth > 1.0) {
                depth = 1.0;
            }
            depth /= 8.0;
        }

        if (scale < 0.0) {
            scale = 0.0;
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
            density = this.applySlides(density, noiseY);
            
            buffer[noiseY] = density;
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
    
    private double getOffset(int noiseY, double heightStretch, double depth, double scale) {
        double offset = (((double)noiseY - depth) * heightStretch) / scale;
        
        if (offset < 0.0)
            offset *= 4.0;
        
        return offset;
    }
}
