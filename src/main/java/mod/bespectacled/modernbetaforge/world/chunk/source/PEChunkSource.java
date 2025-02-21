package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.mersenne.MTRandom;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.source.PEBiomeSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class PEChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise scaleOctaveNoise;
    private final PerlinOctaveNoise depthOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    private final ClimateSampler climateSampler;

    public PEChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);

        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.scaleOctaveNoise = new PerlinOctaveNoise(this.random, 10, true);
        this.depthOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        
        BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE
                .get(settings.biomeSource)
                .apply(seed, settings);
        this.climateSampler = biomeSource instanceof ClimateSampler ?
            (ClimateSampler)biomeSource :
            new PEBiomeSource(seed, settings);

        this.setBeachOctaveNoise(this.beachOctaveNoise);
        this.setSurfaceOctaveNoise(this.surfaceOctaveNoise);
        this.setForestOctaveNoise(this.forestOctaveNoise);
    }
    
    @Override
    protected Random createRandom(long seed) {
        // Use Mersenne Twister random instead of Java random
        return new MTRandom(seed);
    }

    @Override
    protected NoiseScaleDepth sampleNoiseScaleDepth(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        int horizNoiseResolution = 16 / (this.noiseSizeX + 1);
        int x = (startNoiseX / this.noiseSizeX * 16) + localNoiseX * horizNoiseResolution + horizNoiseResolution / 2;
        int z = (startNoiseZ / this.noiseSizeZ * 16) + localNoiseZ * horizNoiseResolution + horizNoiseResolution / 2;
        
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        double scaleNoiseScaleX = this.settings.scaleNoiseScaleX;
        double scaleNoiseScaleZ = this.settings.scaleNoiseScaleZ;
        double depthNoiseScaleX = this.settings.depthNoiseScaleX;
        double depthNoiseScaleZ = this.settings.depthNoiseScaleZ;
        double baseSize = this.settings.baseSize;

        double scale = this.scaleOctaveNoise.sampleXZ(noiseX, noiseZ, scaleNoiseScaleX, scaleNoiseScaleZ);
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
        
        return new NoiseScaleDepth(scale, depth);
    }

    @Override
    protected double sampleNoiseOffset(int noiseY, double scale, double depth) {
        double offset = (((double)noiseY - depth) * this.settings.stretchY) / scale;
        
        if (offset < 0.0)
            offset *= 4.0;
        
        return offset;
    }
}
