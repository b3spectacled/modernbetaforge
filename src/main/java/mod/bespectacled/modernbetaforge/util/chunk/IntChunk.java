package mod.bespectacled.modernbetaforge.util.chunk;

import java.util.function.BiFunction;

public class IntChunk {
    private final int[] ints;
    
    public IntChunk(int chunkX, int chunkZ, BiFunction<Integer, Integer, int[]> chunkFunc) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        this.ints = chunkFunc.apply(startX, startZ);
    }
    
    public IntChunk(int[] ints) {
        this.ints = ints;
    }
    
    public int sample(int x, int z) {
        return this.ints[(x & 0xF) + (z & 0xF) * 16];
    }
}
