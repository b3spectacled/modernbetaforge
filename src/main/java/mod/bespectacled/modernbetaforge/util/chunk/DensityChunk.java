package mod.bespectacled.modernbetaforge.util.chunk;

public class DensityChunk {
    private final double[] densities;
    
    public DensityChunk(double[] densities) {
        this.densities = densities;
    }
    
    public double sample(int x, int y, int z) {
        return this.densities[(y * 16 + (z & 0xF)) * 16 + (x & 0xF)];
    }
}
