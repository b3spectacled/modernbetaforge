package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.NoiseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.SurfaceNoiseChunk;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class Alpha12SurfaceBuilder extends NoiseSurfaceBuilder {
    private final ChunkCache<SurfaceNoiseChunk> sandCache;
    private final ChunkCache<SurfaceNoiseChunk> gravelCache;
    private final ChunkCache<SurfaceNoiseChunk> surfaceCache;
    
    public Alpha12SurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings, false, true, true);
        
        this.sandCache = new ChunkCache<>("sand", 16, this::sampleSandNoise);
        this.gravelCache = new ChunkCache<>("gravel", 16, this::sampleGravelNoise);
        this.surfaceCache = new ChunkCache<>("surface", 16, this::sampleSurfaceNoise);
    }
    
    @Override
    public boolean generatesBeaches(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.sandCache.get(chunkX, chunkZ).getNoise()[(x & 0xF) + (z & 0xF) * 16];
        
        return noise + random.nextDouble() * 0.2 > 0.0;
    }
    
    @Override
    public boolean generatesGravelBeaches(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.gravelCache.get(chunkX, chunkZ).getNoise()[(x & 0xF) + (z & 0xF) * 16];
        
        return noise + random.nextDouble() * 0.2 > 3.0;
    }
    
    @Override
    public int sampleSurfaceDepth(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.surfaceCache.get(chunkX, chunkZ).getNoise()[(x & 0xF) + (z & 0xF) * 16];
        
        return (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
    }
    
    @Override
    public boolean generatesBasin(int surfaceDepth) {
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
                chunkZ << 4, 109.0134, chunkX << 4,
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
