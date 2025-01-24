package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.NoiseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.SurfaceNoiseChunk;
import mod.bespectacled.modernbetaforge.util.mersenne.MTRandom;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class PESurfaceBuilder extends NoiseSurfaceBuilder {
    private final ChunkCache<SurfaceNoiseChunk> sandCache;
    private final ChunkCache<SurfaceNoiseChunk> gravelCache;
    private final ChunkCache<SurfaceNoiseChunk> surfaceCache;
    
    public PESurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings, false, false, false);
        
        this.sandCache = new ChunkCache<>("sand", 16, this::sampleSandNoise);
        this.gravelCache = new ChunkCache<>("gravel", 16, this::sampleGravelNoise);
        this.surfaceCache = new ChunkCache<>("surface", 16, this::sampleSurfaceNoise);
    }
    
    /*
     * MCPE uses different values to seed random surface generation.
     */
    @Override
    public Random createSurfaceRandom(int chunkX, int chunkZ) {
        long seed = (long)chunkX * 0x14609048 + (long)chunkZ * 0x7ebe2d5;
        
        return new MTRandom(seed);
    }

    @Override
    public boolean isBeach(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.sandCache.get(chunkX, chunkZ).getNoise()[(z & 0xF) + (x & 0xF) * 16];

        // MCPE uses nextFloat() instead of nextDouble()
        return noise + random.nextFloat() * 0.2 > 0.0;
    }
    
    @Override
    public boolean isGravelBeach(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.gravelCache.get(chunkX, chunkZ).getNoise()[(z & 0xF) + (x & 0xF) * 16];

        // MCPE uses nextFloat() instead of nextDouble()
        return noise + random.nextFloat() * 0.2 > 3.0;
    }
    
    @Override
    public int sampleSurfaceDepth(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.surfaceCache.get(chunkX, chunkZ).getNoise()[(z & 0xF) + (x & 0xF) * 16];

        // MCPE uses nextFloat() instead of nextDouble()
        return (int)(noise / 3.0 + 3.0 + random.nextFloat() * 0.25);
    }
    
    @Override
    public boolean isBasin(int surfaceDepth) {
        return surfaceDepth <= 0;
    }
    
    private SurfaceNoiseChunk sampleSandNoise(int chunkX, int chunkZ) {
        return new SurfaceNoiseChunk(
            this.getBeachOctaveNoise().sampleBeta(
                chunkX << 4, chunkZ << 4, 0.0, 
                16, 16, 1,
                0.03125, 0.03125, 1.0
            )
        );
    }
    
    private SurfaceNoiseChunk sampleGravelNoise(int chunkX, int chunkZ) {
        return new SurfaceNoiseChunk(
            this.getBeachOctaveNoise().sampleBeta(
                chunkX << 4, 109.0134, chunkZ << 4, 
                16, 1, 16, 
                0.03125, 1.0, 0.03125
            )
        );
    }
    
    private SurfaceNoiseChunk sampleSurfaceNoise(int chunkX, int chunkZ) {
        return new SurfaceNoiseChunk(
            this.getSurfaceOctaveNoise().sampleBeta(
                chunkX << 4, chunkZ << 4, 0.0, 
                16, 16, 1,
                0.03125 * 2.0, 0.03125 * 2.0, 0.03125 * 2.0
            )
        );
    }
}
