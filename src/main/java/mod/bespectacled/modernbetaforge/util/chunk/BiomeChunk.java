package mod.bespectacled.modernbetaforge.util.chunk;

import java.util.function.BiFunction;

import net.minecraft.world.biome.Biome;

public class BiomeChunk {
    private final Biome[] biomes;

    public BiomeChunk(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome[]> chunkFunc) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        this.biomes = chunkFunc.apply(startX, startZ);
    }
    
    public BiomeChunk(Biome[] biomes) {
        this.biomes = biomes;
    }
    
    public Biome sample(int x, int z) {
        return this.biomes[(x & 0xF) + (z & 0xF) * 16];
    }
    
    public Biome[] getBiomes() {
        return this.biomes.clone();
    }
}
