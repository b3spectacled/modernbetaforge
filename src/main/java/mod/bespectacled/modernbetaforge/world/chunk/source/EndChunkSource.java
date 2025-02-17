package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.MathHelper;

public class EndChunkSource extends SkylandsChunkSource {
    private final PerlinOctaveNoise scaleOctaveNoise;
    
    public EndChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        Random random = new Random(seed);
        new PerlinOctaveNoise(random, 16, true);
        new PerlinOctaveNoise(random, 16, true);
        new PerlinOctaveNoise(random, 8, true);
        this.scaleOctaveNoise = new PerlinOctaveNoise(random, 10, true);
    }
    
    @Override
    protected NoiseScaleDepth sampleNoiseScaleDepth(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        double endIslandOffset = this.settings.endIslandOffset;
        double endIslandWeight = this.settings.endIslandWeight;
        
        double scaleNoiseScaleX = this.settings.scaleNoiseScaleX;
        double scaleNoiseScaleZ = this.settings.scaleNoiseScaleZ;

        double scale = this.scaleOctaveNoise.sampleXZ(noiseX, noiseZ, scaleNoiseScaleX, scaleNoiseScaleZ);
        
        double depth = endIslandOffset - Math.sqrt(noiseX * noiseX + noiseZ * noiseZ) * endIslandWeight;
        depth = MathHelper.clamp(depth, -100.0, 80.0);

        scale = MathHelper.clamp(scale, 0.0, 1.0);
        scale += 0.5;
        
        return new NoiseScaleDepth(scale, depth);
    }

    @Override
    protected double sampleNoiseOffset(int noiseY, double scale, double depth) {
        return 8.0 - depth;
    }
}
