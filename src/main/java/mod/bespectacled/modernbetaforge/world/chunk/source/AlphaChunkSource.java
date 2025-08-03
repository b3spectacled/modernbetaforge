package mod.bespectacled.modernbetaforge.world.chunk.source;

import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseHeight;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class AlphaChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise scaleOctaveNoise;
    private final PerlinOctaveNoise depthOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    private final boolean isInfdev611;
    
    public AlphaChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        this(seed, settings, false);
    }
    
    public AlphaChunkSource(long seed, ModernBetaGeneratorSettings settings, boolean isInfdev611) {
        super(seed, settings);
        
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
    protected NoiseHeight sampleNoiseHeight(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        double scaleNoiseScaleX = this.settings.scaleNoiseScaleX;
        double scaleNoiseScaleZ = this.settings.scaleNoiseScaleZ;
        double depthNoiseScaleX = this.settings.depthNoiseScaleX;
        double depthNoiseScaleZ = this.settings.depthNoiseScaleZ;
        double baseSize = this.settings.baseSize;
        
        double scale = this.scaleOctaveNoise.sample(noiseX, 0, noiseZ, scaleNoiseScaleX, 0.0, scaleNoiseScaleZ);
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
            
            if (!this.isInfdev611)
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
        
        return new NoiseHeight(scale, depth);
    }

    @Override
    protected double sampleNoiseOffset(int noiseY, double scale, double depth) {
        double offset = (((double)noiseY - depth) * this.settings.stretchY) / scale;
        
        if (offset < 0.0)
            offset *= 4.0;
        
        return offset;
    }
}
