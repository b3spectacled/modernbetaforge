package mod.bespectacled.modernbetaforge.world.biome.injector;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.util.chunk.InjectorChunk;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class BiomeInjector {
    private final ChunkCache<InjectorChunk> biomeCache;
    private final BiomeInjectionRules rules;
    
    public BiomeInjector(ChunkSource chunkSource, BiomeSource biomeSource, BiomeInjectionRules rules) {
        this.biomeCache = new ChunkCache<>(
            "cached_injected_biomes",
            512,
            true,
            (chunkX, chunkZ) -> new InjectorChunk(chunkX, chunkZ, chunkSource, biomeSource, this::getInjectedBiome, this::getInjectionId)
        );
        
        this.rules = rules;
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
        
        return this.biomeCache.get(chunkX, chunkZ).sample(x, z);
    }
    
    public String getCachedInjectionId(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.biomeCache.get(chunkX, chunkZ).getId(x, z);
    }
    
    private Biome getInjectedBiome(BiomeInjectionContext context, int x, int z) {
        return this.rules.test(context, x, z);
    }
    
    private byte getInjectionId(BiomeInjectionContext context, int x, int z) {
        return this.rules.testId(context, x, z);
    }
}