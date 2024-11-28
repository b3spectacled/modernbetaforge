package mod.bespectacled.modernbetaforge.util.noise;

import java.util.Random;

public class PerlinOctaveNoiseCombined {
    private PerlinOctaveNoise firstNoise;
    private PerlinOctaveNoise secondNoise;
    
    public PerlinOctaveNoiseCombined(Random random, int octaves, boolean useOffset) {
        this.firstNoise = new PerlinOctaveNoise(random, octaves, useOffset);
        this.secondNoise = new PerlinOctaveNoise(random, octaves, useOffset);
    }
    
    public PerlinOctaveNoiseCombined(PerlinOctaveNoise firstNoise, PerlinOctaveNoise secondNoise) {
        this.firstNoise = firstNoise;
        this.secondNoise = secondNoise;
    }
    
    public final double sample(double x, double y) {
        return this.firstNoise.sampleXY(x + this.secondNoise.sampleXY(x, y), y);
    }
}
