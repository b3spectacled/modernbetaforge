package mod.bespectacled.modernbetaforge.world.chunk.source;

import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.spawn.BetaSpawnLocator;
import net.minecraft.world.World;

public class AlphaChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise minLimitOctaveNoise;
    private final PerlinOctaveNoise maxLimitOctaveNoise;
    private final PerlinOctaveNoise mainOctaveNoise;
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise scaleOctaveNoise;
    private final PerlinOctaveNoise depthOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    private final boolean isInfdev611;
    
    public AlphaChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaGeneratorSettings settings
    ) {
        this(world, chunkGenerator, settings, false);
    }
    
    public AlphaChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaGeneratorSettings settings,
        boolean isInfdev611
    ) {
        super(world, chunkGenerator, settings);
        
        this.minLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.maxLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.mainOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.scaleOctaveNoise = new PerlinOctaveNoise(this.random, 10, true);
        this.depthOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        
        this.isInfdev611 = isInfdev611;

        this.setBeachOctaveNoise(this.beachOctaveNoise);
        this.setSurfaceOctaveNoise(this.surfaceOctaveNoise);
        this.setForestOctaveNoise(this.forestOctaveNoise);
    }

    @Override
    public SpawnLocator getSpawnLocator() {
        return new BetaSpawnLocator();
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
            
            if (!isInfdev611)
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
