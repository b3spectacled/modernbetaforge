package mod.bespectacled.modernbetaforge.world.chunk.source;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.chunk.surface.SkylandsSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class SkylandsChunkSource extends NoiseChunkSource {
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    private final SurfaceBuilder surfaceBuilder;
    
    public SkylandsChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        new PerlinOctaveNoise(this.random, 10, true);
        new PerlinOctaveNoise(this.random, 16, true);
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);

        this.setBeachOctaveNoise(this.beachOctaveNoise);
        this.setSurfaceOctaveNoise(this.surfaceOctaveNoise);
        this.setForestOctaveNoise(this.forestOctaveNoise);
        
        this.surfaceBuilder = new SkylandsSurfaceBuilder(this, settings);
        
        this.setCloudHeight(8);
    }
    
    @Override
    public int getSeaLevel() {
        return 0;
    }
    
    @Override
    public void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        this.surfaceBuilder.provideSurface(world, biomes, chunkPrimer, chunkX, chunkZ);
    }
    
    @Override
    public BiomeInjectionRules buildBiomeInjectorRules(BiomeSource biomeSource) {
        return new BiomeInjectionRules.Builder().build();
    }
    
    @Override
    protected NoiseScaleDepth sampleNoiseScaleDepth(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        return NoiseScaleDepth.ZERO;
    }

    @Override
    protected double sampleNoiseOffset(int noiseY, double scale, double depth) {
        return 8.0;
    }
}
