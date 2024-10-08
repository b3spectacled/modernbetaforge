package mod.bespectacled.modernbetaforge.world.biome.injector;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class BiomeInjector {
    private final BiomeInjectionRules.Builder builder;
    private final ChunkCache<BiomeChunk> biomeCache;

    private BiomeInjectionRules rules;
    
    public BiomeInjector(ChunkSource chunkSource, BiomeSource biomeSource) {
        this.builder = new BiomeInjectionRules.Builder();
        
        this.biomeCache = new ChunkCache<>(
            "cached_injected_biomes",
            512,
            true,
            (chunkX, chunkZ) -> new BiomeChunk(chunkX, chunkZ, chunkSource, biomeSource, this::getInjectedBiome)
        );
        
        this.rules = this.builder.build();
    }
    
    public void addRule(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, String id) {
        this.builder.add(rule, resolver, id);
    }
    
    public void buildRules() {
        this.rules = this.builder.build();
    }
    
    public void getInjectedBiomes(Biome[] biomes, ChunkPrimer chunkPrimer, ChunkSource chunkSource, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                int x = localX + startX;
                int z = localZ + startZ;
                int ndx = localX + localZ * 16;
                
                int topHeight = chunkSource.getHeight(x, z, Type.SURFACE);
                BlockPos topPos = new BlockPos(x, topHeight, z);
                IBlockState topState = chunkPrimer.getBlockState(localX, topHeight, localZ);
                Biome biome = biomes[ndx];
                
                BiomeInjectionContext context = new BiomeInjectionContext(topPos, topState, biome);
                
                Biome injectedBiome = this.getInjectedBiome(context, x, z);
                if (injectedBiome != null) {
                    biomes[ndx] = injectedBiome;
                }
            }
        }
    }
    
    public Biome getCachedInjectedBiome(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.biomeCache.get(chunkX, chunkZ).sampleBiome(x, z);
    }
    
    private Biome getInjectedBiome(BiomeInjectionContext context, int x, int z) {
        return this.rules.test(context, x, z);
    }
}