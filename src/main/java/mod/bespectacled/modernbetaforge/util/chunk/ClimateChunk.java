package mod.bespectacled.modernbetaforge.util.chunk;

import java.util.function.BiFunction;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;

public class ClimateChunk {
    private final Clime climes[] = new Clime[256];
    
    public ClimateChunk(int chunkX, int chunkZ, BiFunction<Integer, Integer, Clime> chunkFunc) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        int ndx = 0;
        for (int x = startX; x < startX + 16; ++x) {
            for (int z = startZ; z < startZ + 16; ++z) {
                this.climes[ndx++] = chunkFunc.apply(x, z);
            }
        }
    }
    
    public Clime sample(int x, int z) {
        return this.climes[(z & 0xF) + (x & 0xF) * 16];
    }
}
