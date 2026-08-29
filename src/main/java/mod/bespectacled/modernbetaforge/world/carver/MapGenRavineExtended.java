package mod.bespectacled.modernbetaforge.world.carver;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.compat.CarverCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenRavineExtended extends MapGenRavine {
    private static final Set<Biome> EXCEPTION_BIOMES;
    
    private final Block defaultBlock;
    private final Set<Block> defaultFluids;
    
    private final Set<Block> carvables;
    private final Set<Block> uncarvables;
    
    private final int ravineChance;
    private final int worldFloor;
    private final MutableBlockPos mutablePos;

    public MapGenRavineExtended(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.defaultBlock = chunkSource.getDefaultBlock().getBlock();
        this.defaultFluids = MapGenBetaCave.getDefaultFluids(chunkSource.getDefaultFluid());
        
        this.carvables = this.initializeCarvables(this.defaultBlock).build();
        this.uncarvables = this.initializeUncarvables().build();
        
        this.ravineChance = settings.ravineChance;
        this.worldFloor = settings.floor;
        this.mutablePos = new MutableBlockPos();
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
    protected void digBlock(ChunkPrimer chunkPrimer, int x, int y, int z, int chunkX, int chunkZ, boolean isTopSoil) {
        Biome biome = this.world.getBiome(this.mutablePos.setPos(x + chunkX * 16, 0, z + chunkZ * 16));
        
        Block block = chunkPrimer.getBlockState(x, y, z).getBlock();
        Block topBlock = Blocks.GRASS;
        Block fillerBlock = Blocks.DIRT;
        
        if (this.isExceptionBiome(biome)) {
            topBlock = biome.topBlock.getBlock();
            fillerBlock = biome.fillerBlock.getBlock();
        }
        
        if ((block == topBlock || block == fillerBlock || this.carvables.contains(block)) && !this.uncarvables.contains(block)) {
            if (y - 1 < this.worldFloor + ModernBetaGeneratorSettings.CARVER_LAVA_LEVEL) {
                chunkPrimer.setBlockState(x, y, z, FLOWING_LAVA);
            } else {
                chunkPrimer.setBlockState(x, y, z, AIR);

                if (isTopSoil && chunkPrimer.getBlockState(x, y - 1, z).getBlock() == fillerBlock) {
                    chunkPrimer.setBlockState(x, y - 1, z, topBlock.getDefaultState());
                }
            }
        }
    }
    
    @Override
    protected boolean isOceanBlock(ChunkPrimer chunkPrimer, int x, int y, int z, int chunkX, int chunkZ) {
        return this.defaultFluids.contains(chunkPrimer.getBlockState(x, y, z).getBlock());
    }

    private ImmutableSet.Builder<Block> initializeCarvables(Block defaultBlock) {
        ImmutableSet.Builder<Block> carvables = new ImmutableSet.Builder<>();
        
        // Add default blocks
        carvables.add(defaultBlock)
            .add(Blocks.STONE)
            .add(Blocks.COAL_ORE)
            .add(Blocks.IRON_ORE)
            ;
        
        // Add modded blocks
        for (Entry<String, Compat> entry : ModCompat.LOADED_COMPATS.entrySet()) {
            Compat compat = entry.getValue();
            if (compat instanceof CarverCompat) {
                ModernBeta.log(Level.DEBUG, String.format("Adding ravine carvables from mod '%s'", entry.getKey()));
                
                carvables.addAll(((CarverCompat)compat).getCarvables());
            }
        }
        
        return carvables;
    }
    
    private ImmutableSet.Builder<Block> initializeUncarvables() {
        ImmutableSet.Builder<Block> uncarvables = new ImmutableSet.Builder<>();
        uncarvables.add(Blocks.SAND);

        return uncarvables;
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
            .add(ModernBetaBiomeHolders.BETA_BEACH)
            .add(ModernBetaBiomeHolders.BETA_SNOWY_BEACH)
            .add(ModernBetaBiomeHolders.BETA_DESERT)
            .add(ModernBetaBiomeHolders.BETA_ICE_DESERT)
            .build();
    }
}
