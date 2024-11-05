package mod.bespectacled.modernbetaforge.util.chunk;

import java.util.function.BiFunction;

public class SkyClimateChunk {
    private final double temps[] = new double[256];
    
    public SkyClimateChunk(int chunkX, int chunkZ, BiFunction<Integer, Integer, Double> chunkFunc) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        int ndx = 0;
        for (int x = startX; x < startX + 16; ++x) {
            for (int z = startZ; z < startZ + 16; ++z) {    
                this.temps[ndx] = chunkFunc.apply(x, z);
                
                ndx++;
            }
        }
    }
    
    public double sample(int x, int z) {
        return this.temps[(z & 0xF) + (x & 0xF) * 16];
    }
}
