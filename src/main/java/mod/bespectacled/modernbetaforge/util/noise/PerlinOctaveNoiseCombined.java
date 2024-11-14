package mod.bespectacled.modernbetaforge.util.noise;

public class PerlinOctaveNoiseCombined {
    private PerlinOctaveNoise firstNoise;
    private PerlinOctaveNoise secondNoise;
    
    public PerlinOctaveNoiseCombined(PerlinOctaveNoise firstNoise, PerlinOctaveNoise secondNoise) {
        this.firstNoise = firstNoise;
        this.secondNoise = secondNoise;
    }
    
    public final double sample(double x, double y) {
        return this.firstNoise.sampleXY(x + this.secondNoise.sampleXY(x, y), y);
    }
}
