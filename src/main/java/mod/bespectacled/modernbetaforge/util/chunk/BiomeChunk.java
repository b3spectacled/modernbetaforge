package mod.bespectacled.modernbetaforge.util.chunk;

import java.util.function.BiFunction;

import net.minecraft.world.biome.Biome;

public class BiomeChunk {
    private Biome[] biomes = new Biome[256];

    public BiomeChunk(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome[]> chunkFunc) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        this.biomes = chunkFunc.apply(startX, startZ);
    }
    
    public Biome sample(int x, int z) {
        return this.biomes[(z & 0xF) + (x & 0xF) * 16];
    }
}
