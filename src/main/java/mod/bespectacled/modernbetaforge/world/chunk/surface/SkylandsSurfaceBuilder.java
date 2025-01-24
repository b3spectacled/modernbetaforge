package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.NoiseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.SurfaceNoiseChunk;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class SkylandsSurfaceBuilder extends NoiseSurfaceBuilder {
    private final ChunkCache<SurfaceNoiseChunk> surfaceCache;
    
    public SkylandsSurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings, false, false, false);
        
        this.surfaceCache = new ChunkCache<>("surface", 16, this::sampleSurfaceNoise);
    }
    
    @Override
    public boolean isBedrock(int y, Random random) {
        return false;
    }

    @Override
    public int sampleSurfaceDepth(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.surfaceCache.get(chunkX, chunkZ).getNoise()[(z & 0xF) + (x & 0xF) * 16];
        
        return (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
    }

    @Override
    public boolean isBasin(int surfaceDepth) {
        return surfaceDepth <= 0;
    }

    @Override
    public boolean isBeach(int x, int z, Random random) {
        return false;
    }

    @Override
    public boolean isGravelBeach(int x, int z, Random random) {
        return false;
    }
    
    @Override
    protected boolean useCustomSurfaceBuilder(World world, Biome biome, ChunkPrimer chunkPrimer, Random random, int x, int z, boolean override) {
        return false;
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
