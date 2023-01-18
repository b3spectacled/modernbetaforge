package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.SkyClimateChunk;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;

public class BetaSkyClimateSampler {
    private final SimplexOctaveNoise tempNoiseOctaves;
    
    private final ChunkCache<SkyClimateChunk> skyClimateCache;
    
    private final double tempScale;
    
    public BetaSkyClimateSampler(long seed) {
        this(seed, 1D);
    }
    
    public BetaSkyClimateSampler(long seed, double tempScale) {
        this.tempNoiseOctaves = new SimplexOctaveNoise(new Random(seed * 9871L), 4);
        
        this.skyClimateCache = new ChunkCache<>(
            "sky", 
            256, 
            true, 
            (chunkX, chunkZ) -> new SkyClimateChunk(chunkX, chunkZ, this::sampleSkyTempNoise)
        );
        
        this.tempScale = 0.02500000037252903D / tempScale;
    }
    
    public double sampleSkyTemp(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.skyClimateCache.get(chunkX, chunkZ).sampleTemp(x, z);
    }
    
    private double sampleSkyTempNoise(int x, int z) {
        return this.tempNoiseOctaves.sample(x, z, this.tempScale, this.tempScale, 0.5D);
    }
}
