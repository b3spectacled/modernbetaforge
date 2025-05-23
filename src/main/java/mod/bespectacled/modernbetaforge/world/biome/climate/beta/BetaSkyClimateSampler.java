package mod.bespectacled.modernbetaforge.world.biome.climate.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.SkyClimateChunk;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class BetaSkyClimateSampler {
    private final SimplexOctaveNoise tempOctaveNoise;
    
    private final ChunkCache<SkyClimateChunk> skyClimateCache;
    
    private final double tempScale;
    
    public BetaSkyClimateSampler(long seed) {
        this(seed, ModernBetaGeneratorSettings.build());
    }
    
    public BetaSkyClimateSampler(long seed, ModernBetaGeneratorSettings settings) {
        this.tempOctaveNoise = new SimplexOctaveNoise(new Random(seed * 9871L), 4);
        
        this.skyClimateCache = new ChunkCache<>(
            "sky",
            (chunkX, chunkZ) -> new SkyClimateChunk(chunkX, chunkZ, this::sampleSkyTempNoise)
        );
        
        this.tempScale = 0.025 / settings.tempNoiseScale;
    }
    
    public double sampleSkyTemp(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.skyClimateCache.get(chunkX, chunkZ).sample(x, z);
    }
    
    private double sampleSkyTempNoise(int x, int z) {
        return this.tempOctaveNoise.sample(x, z, this.tempScale, this.tempScale, 0.5);
    }
}
