package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseHeight;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.MathHelper;

public class Infdev415ChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise minLimitOctaveNoise;
    private final PerlinOctaveNoise maxLimitOctaveNoise;
    private final PerlinOctaveNoise mainOctaveNoise;
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    public Infdev415ChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        Random random = this.createRandom(seed);
        this.minLimitOctaveNoise = new PerlinOctaveNoise(random, 16, true);
        this.maxLimitOctaveNoise = new PerlinOctaveNoise(random, 16, true);
        this.mainOctaveNoise = new PerlinOctaveNoise(random, 8, true);
        this.beachOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        new PerlinOctaveNoise(random, 5, true); // Unused in original source
        this.forestOctaveNoise = new PerlinOctaveNoise(random, 5, true);

        this.setBeachOctaveNoise(this.beachOctaveNoise);
        this.setSurfaceOctaveNoise(this.surfaceOctaveNoise);
        this.setForestOctaveNoise(this.forestOctaveNoise);
    }
    
    @Override
    public long getPopulationSeed(int chunkX, int chunkZ) {
        return (long)chunkX * 318279123L + (long)chunkZ * 919871212L;
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

        double coordinateScale = this.settings.coordinateScale; // Default: 684.412
        double heightScale = this.settings.heightScale;         // Default: 984.412
        
        double mainNoiseScaleX = this.settings.mainNoiseScaleX; // Default: 80
        double mainNoiseScaleY = this.settings.mainNoiseScaleY; // Default: 400
        double mainNoiseScaleZ = this.settings.mainNoiseScaleZ;
        
        double lowerLimitScale = this.settings.lowerLimitScale;
        double upperLimitScale = this.settings.upperLimitScale;
        
        for (int noiseY = 0; noiseY < buffer.length; ++noiseY) {
            double density;
            double densityOffset = this.sampleNoiseOffset(noiseY, 0.0, 0.0);
            
            // Default values: 8.55515, 1.71103, 8.55515
            double mainNoiseVal = this.mainOctaveNoise.sample(
                noiseX * coordinateScale / mainNoiseScaleX,
                noiseY * coordinateScale / mainNoiseScaleY, // Original source uses 684.412 instead of 984.412, so using coordinateScale here.
                noiseZ * coordinateScale / mainNoiseScaleZ
            ) / 2.0;
            
            if (mainNoiseVal < -1.0) {
                density = this.minLimitOctaveNoise.sample(
                    noiseX * coordinateScale, 
                    noiseY * heightScale, 
                    noiseZ * coordinateScale
                ) / lowerLimitScale;
                
                density -= densityOffset;
                
                density = this.clampNoise(density);
                
            } else if (mainNoiseVal > 1.0) {
                density = this.maxLimitOctaveNoise.sample(
                    noiseX * coordinateScale, 
                    noiseY * heightScale, 
                    noiseZ * coordinateScale
                ) / upperLimitScale;

                density -= densityOffset;
                
                density = this.clampNoise(density);
                
            } else {
                double minLimitVal = this.minLimitOctaveNoise.sample(
                    noiseX * coordinateScale, 
                    noiseY * heightScale, 
                    noiseZ * coordinateScale
                ) / lowerLimitScale;
                
                double maxLimitVal = this.maxLimitOctaveNoise.sample(
                    noiseX * coordinateScale, 
                    noiseY * heightScale, 
                    noiseZ * coordinateScale
                ) / upperLimitScale;
                
                minLimitVal -= densityOffset;
                maxLimitVal -= densityOffset;
                
                minLimitVal = this.clampNoise(minLimitVal);
                maxLimitVal = this.clampNoise(maxLimitVal);
                                
                double delta = (mainNoiseVal + 1.0) / 2.0;
                density = minLimitVal + (maxLimitVal - minLimitVal) * delta;
            };
            
            buffer[noiseY] = density;
        }
    }
    
    @Override
    protected NoiseHeight sampleNoiseHeight(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        return NoiseHeight.ZERO;
    }

    @Override
    protected double sampleNoiseOffset(int noiseY, double scale, double depth) {
        // Check if y (in scaled space) is below sealevel
        // and increase density accordingly.
        //double offset = y * 4.0 - 64.0;
        double offset = noiseY * this.verticalNoiseResolution - (double)this.seaLevel;
        
        if (offset < 0.0)
            offset *= 3.0;
        
        return offset;
    }

    private double clampNoise(double density) {
        return MathHelper.clamp(density, -10.0, 10.0);
    }
}
