package mod.bespectacled.modernbetaforge.world.biome.climate.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ClimateChunk;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.MathHelper;

public class BetaClimateSampler {
    private final SimplexOctaveNoise tempOctaveNoise;
    private final SimplexOctaveNoise rainOctaveNoise;
    private final SimplexOctaveNoise detailOctaveNoise;
    
    private final ChunkCache<ClimateChunk> climateCache;
    
    private final double tempScale;
    private final double rainScale;
    private final double detailScale;
    
    public BetaClimateSampler(long seed) {
        this(seed, ModernBetaGeneratorSettings.build());
    }
    
    public BetaClimateSampler(long seed, ModernBetaGeneratorSettings settings) {
        this.tempOctaveNoise = new SimplexOctaveNoise(new Random(seed * 9871L), 4);
        this.rainOctaveNoise = new SimplexOctaveNoise(new Random(seed * 39811L), 4);
        this.detailOctaveNoise = new SimplexOctaveNoise(new Random(seed * 543321L), 2);
        
        this.climateCache = new ChunkCache<>("climate", (chunkX, chunkZ) -> new ClimateChunk(chunkX, chunkZ, this::sampleClimateNoise));
        
        this.tempScale = 0.025 / settings.tempNoiseScale;
        this.rainScale = 0.050 / settings.rainNoiseScale;
        this.detailScale = 0.25 / settings.detailNoiseScale;
    }

    public Clime sampleClime(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.climateCache.get(chunkX, chunkZ).sample(x, z);
    }
    
    public Clime sampleClimateNoise(int x, int z) {
        double temp = this.tempOctaveNoise.sample(x, z, this.tempScale, this.tempScale, 0.25);
        double rain = this.rainOctaveNoise.sample(x, z, this.rainScale, this.rainScale, 0.33333333333333331);
        double detail = this.detailOctaveNoise.sample(x, z, this.detailScale, this.detailScale, 0.58823529411764708);

        detail = detail * 1.1 + 0.5;

        temp = (temp * 0.15 + 0.7) * 0.99 + detail * 0.01;
        rain = (rain * 0.15 + 0.5) * 0.998 + detail * 0.002;

        temp = 1.0 - (1.0 - temp) * (1.0 - temp);
        
        return new Clime(MathHelper.clamp(temp, 0.0, 1.0), MathHelper.clamp(rain, 0.0, 1.0));
    }
}
