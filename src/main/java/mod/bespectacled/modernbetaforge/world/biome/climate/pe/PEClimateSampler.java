package mod.bespectacled.modernbetaforge.world.biome.climate.pe;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ClimateChunk;
import mod.bespectacled.modernbetaforge.util.mersenne.MTRandom;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.util.math.MathHelper;

public class PEClimateSampler {
    private final PerlinOctaveNoise tempOctaveNoise;
    private final PerlinOctaveNoise rainOctaveNoise;
    private final PerlinOctaveNoise detailOctaveNoise;
    
    private final ChunkCache<ClimateChunk> climateCache;
    
    private final double tempScale;
    private final double rainScale;
    private final double detailScale;
    
    public PEClimateSampler(long seed) {
        this(seed, new ModernBetaChunkGeneratorSettings.Factory().build());
    }
    
    public PEClimateSampler(long seed, ModernBetaChunkGeneratorSettings settings) {
        this.tempOctaveNoise = new PerlinOctaveNoise(new MTRandom(seed * 9871L), 4, true);
        this.rainOctaveNoise = new PerlinOctaveNoise(new MTRandom(seed * 39811L), 4, true);
        this.detailOctaveNoise = new PerlinOctaveNoise(new MTRandom(seed * 543321L), 2, true);
        
        this.climateCache = new ChunkCache<>(
            "climate", 
            512, 
            true, 
            (chunkX, chunkZ) -> new ClimateChunk(chunkX, chunkZ, this::sampleClimateNoise)
        );
        
        this.tempScale = 0.025 / settings.tempNoiseScale;
        this.rainScale = 0.050 / settings.rainNoiseScale;
        this.detailScale = 0.25 / settings.detailNoiseScale;
    }

    public Clime sampleClime(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.climateCache.get(chunkX, chunkZ).sampleClime(x, z);
    }
    
    public Clime sampleClimateNoise(int x, int z) {
        double temp = this.tempOctaveNoise.sampleXZ(x, z, this.tempScale, this.tempScale);
        double rain = this.rainOctaveNoise.sampleXZ(x, z, this.rainScale, this.rainScale);
        double detail = this.detailOctaveNoise.sampleXZ(x, z, this.detailScale, this.detailScale);

        detail = detail * 1.1 + 0.5;

        temp = (temp * 0.15 + 0.7) * 0.99 + detail * 0.01;
        rain = (rain * 0.15 + 0.5) * 0.998 + detail * 0.002;

        temp = 1.0 - (1.0 - temp) * (1.0 - temp);
        
        return new Clime(MathHelper.clamp(temp, 0.0, 1.0), MathHelper.clamp(rain, 0.0, 1.0));
    }
}
