package mod.bespectacled.modernbetaforge.world.biome.climate.pe;

import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.SkyClimateChunk;
import mod.bespectacled.modernbetaforge.util.mersenne.MTRandom;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class PESkyClimateSampler {
    private final PerlinOctaveNoise tempOctaveNoise;
    
    private final ChunkCache<SkyClimateChunk> skyClimateCache;
    
    private final double tempScale;
    
    public PESkyClimateSampler(long seed) {
        this(seed, ModernBetaGeneratorSettings.build());
    }
    
    public PESkyClimateSampler(long seed, ModernBetaGeneratorSettings settings) {
        this.tempOctaveNoise = new PerlinOctaveNoise(new MTRandom(seed * 9871L), 4, true);
        
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
        return this.tempOctaveNoise.sampleXZ(x, z, this.tempScale, this.tempScale);
    }
}
