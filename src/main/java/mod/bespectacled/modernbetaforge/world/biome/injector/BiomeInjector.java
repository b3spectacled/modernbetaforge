package mod.bespectacled.modernbetaforge.world.biome.injector;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeInjector {
    private final BiomeInjectionRules rules;
    
    public BiomeInjector(ChunkSource chunkSource, BiomeSource biomeSource, BiomeInjectionRules rules) {
        this.rules = rules;
    }
    
    public void injectBiomes(Biome[] biomes, ChunkPrimer chunkPrimer, ChunkSource chunkSource, int chunkX, int chunkZ, BiomeInjectionStep step) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                int x = localX + startX;
                int z = localZ + startZ;
                int ndx = localX + localZ * 16;
                
                int height = MathHelper.clamp(chunkSource.getHeight(x, z, Type.SURFACE), 0, 255);
                int heightAbove = MathHelper.clamp(chunkSource.getHeight(x, z, Type.SURFACE) + 1, 0, 255);
                
                BlockPos blockPos = new BlockPos(x, height, z);
                IBlockState blockState = chunkPrimer.getBlockState(localX, height, localZ);
                IBlockState blockStateAbove = chunkPrimer.getBlockState(localX, heightAbove, localZ);
                Biome biome = biomes[ndx];
                
                BiomeInjectionContext context = new BiomeInjectionContext(blockPos, blockState, blockStateAbove, biome);
                Biome injectedBiome = this.getInjectedBiome(context, x, z, step);
                if (injectedBiome != null) {
                    biomes[ndx] = injectedBiome;
                }
            }
        }
    }
    
    public Biome getInjectedBiome(BiomeInjectionContext context, int x, int z, BiomeInjectionStep step) {
        return this.rules.test(context, x, z, step);
    }
    
    public static boolean atBeachDepth(int topHeight, int seaLevel) {
        return topHeight >= seaLevel - 4 && topHeight <= seaLevel + 1;
    }
    
    public static boolean isBeachBlock(IBlockState blockState, Biome biome) {
        Block block = blockState.getBlock();
        
        // Only handle sand beaches,
        // due to limitation of heightmap cache.
        return block == (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER) ? Blocks.SOUL_SAND : Blocks.SAND);
    }
    
    public static boolean atOceanDepth(int topHeight, int oceanDepth, int seaLevel) {
        return topHeight < seaLevel - oceanDepth;
    }
    
    public static boolean isFluidBlock(IBlockState blockState, IBlockState defaultFluid) {
        return blockState.getBlock() == defaultFluid.getBlock();
    }
}