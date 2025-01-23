package mod.bespectacled.modernbetaforge.util.chunk;

public class SurfaceNoiseChunk {
    private final double[] noise;
    
    public SurfaceNoiseChunk(double[] noise) {
        this.noise = noise;
    }
    
    public double[] getNoise() {
        return this.noise;
    }
}
