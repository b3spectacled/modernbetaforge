package mod.bespectacled.modernbetaforge.world.carver;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineExtended extends MapGenRavine {
    private static final Set<Biome> EXCEPTION_BIOMES;
    
    private final Block defaultBlock;
    private final Set<Block> defaultFluids;
    private final int ravineChance;

    public MapGenRavineExtended(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.defaultBlock = chunkSource.getDefaultBlock().getBlock();
        this.defaultFluids = MapGenBetaCave.getDefaultFluids(chunkSource.getDefaultFluid());
        this.ravineChance = 5;
    }
    
    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer) {
        if (this.rand.nextInt(this.ravineChance) == 0) {
            double x = chunkX * 16 + this.rand.nextInt(16);
            double y = this.rand.nextInt(this.rand.nextInt(40) + 8) + 20;
            double z = chunkZ * 16 + this.rand.nextInt(16);
            int tunnelCount = 1;

            for (int i = 0; i < tunnelCount; ++i)  {
                float tunnelC = this.rand.nextFloat() * ((float)Math.PI * 2f);
                float f1 = (this.rand.nextFloat() - 0.5f) * 2.0f / 8.0f;
                float tunnelSysWidth = (this.rand.nextFloat() * 2.0f + this.rand.nextFloat()) * 2.0f;
                
                this.addTunnel(this.rand.nextLong(), originChunkX, originChunkZ, chunkPrimer, x, y, z, tunnelSysWidth, tunnelC, f1, 0, 0, 3.0);
            }
        }
    }
    
    @Override
    protected void digBlock(ChunkPrimer chunkPrimer, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
        Biome biome = this.world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        
        Block block = chunkPrimer.getBlockState(x, y, z).getBlock();
        Block topBlock = this.isExceptionBiome(biome) ? Blocks.GRASS.getDefaultState().getBlock() : biome.topBlock.getBlock();
        Block fillerBlock = this.isExceptionBiome(biome) ? Blocks.DIRT.getDefaultState().getBlock() : biome.fillerBlock.getBlock();

        if (block == this.defaultBlock || block == topBlock || block == fillerBlock) {
            if (y - 1 < 10) {
                chunkPrimer.setBlockState(x, y, z, FLOWING_LAVA);
            } else {
                chunkPrimer.setBlockState(x, y, z, AIR);

                if (foundTop && chunkPrimer.getBlockState(x, y - 1, z).getBlock() == fillerBlock) {
                    chunkPrimer.setBlockState(x, y - 1, z, topBlock.getDefaultState());
                }
            }
        }
    }
    
    @Override
    protected boolean isOceanBlock(ChunkPrimer chunkPrimer, int x, int y, int z, int chunkX, int chunkZ) {
        return this.defaultFluids.contains(chunkPrimer.getBlockState(x, y, z).getBlock());
    }
    
    private boolean isExceptionBiome(Biome biome) {
        return EXCEPTION_BIOMES.contains(biome);
    }
    
    static {
        EXCEPTION_BIOMES = ImmutableSet.<Biome>builder()
            .add(Biomes.BEACH)
            .add(Biomes.DESERT)
            .add(Biomes.MUSHROOM_ISLAND)
            .add(Biomes.MUSHROOM_ISLAND_SHORE)
            .build();
    }
}
