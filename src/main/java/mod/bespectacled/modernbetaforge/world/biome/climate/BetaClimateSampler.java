package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ClimateChunk;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import net.minecraft.util.math.MathHelper;

public class BetaClimateSampler {
    private final SimplexOctaveNoise tempNoiseOctaves;
    private final SimplexOctaveNoise rainNoiseOctaves;
    private final SimplexOctaveNoise detailNoiseOctaves;
    
    private final ChunkCache<ClimateChunk> climateCache;
    
    private final double tempScale;
    private final double rainScale;
    private final double detailScale;
    
    public BetaClimateSampler(long seed) {
        this(seed, 1D, 1D, 1D);
    }
    
    public BetaClimateSampler(long seed, double tempScale, double rainScale, double detailScale) {
        this.tempNoiseOctaves = new SimplexOctaveNoise(new Random(seed * 9871L), 4);
        this.rainNoiseOctaves = new SimplexOctaveNoise(new Random(seed * 39811L), 4);
        this.detailNoiseOctaves = new SimplexOctaveNoise(new Random(seed * 543321L), 2);
        
        this.climateCache = new ChunkCache<>(
            "climate", 
            512, 
            true, 
            (chunkX, chunkZ) -> new ClimateChunk(chunkX, chunkZ, this::sampleClimateNoise)
        );
        
        this.tempScale = 0.02500000037252903D / tempScale;
        this.rainScale = 0.05000000074505806D / rainScale;
        this.detailScale = 0.25D / detailScale;
    }

    public Clime sampleClime(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.climateCache.get(chunkX, chunkZ).sampleClime(x, z);
    }
    
    public Clime sampleClimateNoise(int x, int z) {
        double temp = this.tempNoiseOctaves.sample(x, z, this.tempScale, this.tempScale, 0.25D);
        double rain = this.rainNoiseOctaves.sample(x, z, this.rainScale, this.rainScale, 0.33333333333333331D);
        double detail = this.detailNoiseOctaves.sample(x, z, this.detailScale, this.detailScale, 0.58823529411764708D);

        detail = detail * 1.1D + 0.5D;

        temp = (temp * 0.15D + 0.7D) * 0.99D + detail * 0.01D;
        rain = (rain * 0.15D + 0.5D) * 0.998D + detail * 0.002D;

        temp = 1.0D - (1.0D - temp) * (1.0D - temp);
        
        return new Clime(MathHelper.clamp(temp, 0.0, 1.0), MathHelper.clamp(rain, 0.0, 1.0));
    }
}
