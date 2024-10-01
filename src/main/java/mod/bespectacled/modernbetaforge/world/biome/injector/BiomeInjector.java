package mod.bespectacled.modernbetaforge.world.biome.injector;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class BiomeInjector {
    public static final int OCEAN_MIN_DEPTH = 4;
    public static final int DEEP_OCEAN_MIN_DEPTH = 16;
    
    private final ChunkSource chunkSource;
    private final BiomeSource biomeSource;
    
    private final BiomeInjectionRules rules;
    private final ChunkCache<BiomeChunk> biomeCache;
    
    public BiomeInjector(ChunkSource chunkSource, BiomeSource biomeSource) {
        this.chunkSource = chunkSource;
        this.biomeSource = biomeSource;
        
        boolean replaceOceans = chunkSource.getChunkGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = chunkSource.getChunkGeneratorSettings().replaceBeachBiomes;
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> 
            this.atOceanDepth(context.topHeight, OCEAN_MIN_DEPTH);
            
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            this.atBeachDepth(context.topHeight) && this.isBeachBlock(context.topState);
        
        BiomeInjectionRules.Builder builder = new BiomeInjectionRules.Builder();
        
        if (replaceBeaches && this.biomeSource instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)this.biomeSource;
            
            builder.add(beachPredicate, biomeResolverBeach::getBeachBiome, "beach");
        }
        
        if (replaceOceans && this.biomeSource instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)this.biomeSource;
            
            builder.add(oceanPredicate, biomeResolverOcean::getOceanBiome, "ocean");
        }
        
        this.rules = builder.build();
        this.biomeCache = new ChunkCache<>(
            "injected_biomes_fast",
            512,
            true,
            (chunkX, chunkZ) -> new BiomeChunk(chunkX, chunkZ, this.chunkSource, this::getInjectedBiome)
        );
    }
    
    public void getInjectedBiomes(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                int x = localX + startX;
                int z = localZ + startZ;
                
                int topHeight = this.chunkSource.getHeight(x, z, Type.SURFACE);
                IBlockState topState = chunkPrimer.getBlockState(localX, topHeight, localZ);
                BiomeInjectionContext context = new BiomeInjectionContext(topHeight, topState);
                
                Biome biome = this.getInjectedBiome(context, x, z);
                if (biome != null) {
                    biomes[localX + localZ * 16] = biome;
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

    private boolean atOceanDepth(int topHeight, int oceanDepth) {
        return topHeight < this.chunkSource.getSeaLevel() - oceanDepth;
    }

    private boolean atBeachDepth(int topHeight) {
        int seaLevel = this.chunkSource.getSeaLevel();
        
        return topHeight >= seaLevel - 4 && topHeight <= seaLevel + 1;
    }
    
    private boolean isBeachBlock(IBlockState blockState) {
        Block block = blockState.getBlock();
        
        // Only handle sand beaches,
        // due to limitation of heightmap cache.
        return block == Blocks.SAND;
    }
}