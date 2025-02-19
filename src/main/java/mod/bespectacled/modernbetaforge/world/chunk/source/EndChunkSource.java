package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

public class EndChunkSource extends SkylandsChunkSource {
    private final PerlinOctaveNoise scaleOctaveNoise;
    private final NoiseGeneratorSimplex islandNoise;
    
    public EndChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        Random random = new Random(seed);
        new PerlinOctaveNoise(random, 16, true);
        new PerlinOctaveNoise(random, 16, true);
        new PerlinOctaveNoise(random, 8, true);
        this.scaleOctaveNoise = new PerlinOctaveNoise(random, 10, true);
        new PerlinOctaveNoise(random, 16, true);
        this.islandNoise = new NoiseGeneratorSimplex(random);
    }
    
    @Override
    protected NoiseScaleDepth sampleNoiseScaleDepth(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;

        double scaleNoiseScaleX = this.settings.scaleNoiseScaleX;
        double scaleNoiseScaleZ = this.settings.scaleNoiseScaleZ;

        double scale = this.scaleOctaveNoise.sampleXZ(noiseX, noiseZ, scaleNoiseScaleX, scaleNoiseScaleZ);
        double depth = this.getIslandDepth(startNoiseX, startNoiseZ, localNoiseX, localNoiseZ);

        scale = MathHelper.clamp(scale, 0.0, 1.0);
        scale += 0.5;
        
        return new NoiseScaleDepth(scale, depth);
    }

    @Override
    protected double sampleNoiseOffset(int noiseY, double scale, double depth) {
        return 8.0 - depth;
    }
    
    private double getIslandDepth(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        double endIslandOffset = this.settings.endIslandOffset;
        double endIslandWeight = this.settings.endIslandWeight;
        double endOuterIslandOffset = this.settings.endOuterIslandOffset;
        long endOuterIslandDistance = (long)Math.pow(this.settings.endOuterIslandDistance, 2);

        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        int x = noiseX / this.noiseSizeX;
        int z = noiseZ / this.noiseSizeZ;

        int eX = noiseX % this.noiseSizeX;
        int eZ = noiseZ % this.noiseSizeZ;
        
        double depth = endIslandOffset - MathHelper.sqrt(noiseX * noiseX + noiseZ * noiseZ) * endIslandWeight;
        depth = this.clampDepth(depth);

        if (this.settings.useEndOuterIslands) {
            for (int localX = -12; localX <= 12; ++localX) {
                for (int localZ = -12; localZ <= 12; ++localZ) {
                    long outerX = x + localX;
                    long outerZ = z + localZ;
    
                    if (outerX * outerX + outerZ * outerZ > endOuterIslandDistance && this.islandNoise.getValue(outerX, outerZ) < -0.8999999761581421) {
                        double endOuterIslandWeight = (MathHelper.abs(outerX) * 3439.0 + MathHelper.abs(outerZ) * 147.0) % 13.0 + 9.0;
                        double endOuterNoiseX = (double)(eX - localX * this.noiseSizeX);
                        double endOuterNoiseZ = (double)(eZ - localZ * this.noiseSizeZ);
                        
                        double outerDepth = endOuterIslandOffset - MathHelper.sqrt(endOuterNoiseX * endOuterNoiseX + endOuterNoiseZ * endOuterNoiseZ) * endOuterIslandWeight;
                        outerDepth = clampDepth(outerDepth);
                        
                        depth = Math.max(depth, outerDepth);
                    }
                }
            }
        }

        return depth;
    }
    
    private double clampDepth(double depth) {
        return MathHelper.clamp(depth, -100.0, 80.0);
    }
}
