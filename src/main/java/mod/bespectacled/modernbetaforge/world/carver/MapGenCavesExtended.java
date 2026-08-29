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
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

public class MapGenCavesExtended extends MapGenCaves {
    private final Block defaultBlock;
    private final Set<Block> defaultFluids;
    private final Set<Block> carvables;
    
    private final int worldFloor;
    private final MutableBlockPos mutablePos;
    
    public MapGenCavesExtended(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.defaultBlock = chunkSource.getDefaultBlock().getBlock();
        this.defaultFluids = MapGenBetaCave.getDefaultFluids(chunkSource.getDefaultFluid());
        this.carvables = this.initializeCarvables(this.defaultBlock).build();
        
        this.worldFloor = chunkSource.getWorldFloor();
        this.mutablePos = new MutableBlockPos();
    }
    
    @Override
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        Block block = data.getBlockState(x, y, z).getBlock();
        
        return this.defaultFluids.contains(block);
    }
    
    @Override
    protected boolean canReplaceBlock(IBlockState blockState, IBlockState blockStateUp) {
        return super.canReplaceBlock(blockState, blockStateUp) || this.carvables.contains(blockState.getBlock());
    }
    
    @Override
    protected void digBlock(ChunkPrimer chunkPrimer, int x, int y, int z, int chunkX, int chunkZ, boolean isTopSoil, IBlockState blockState, IBlockState blockStateUp) {
        Biome biome = this.world.getBiome(this.mutablePos.setPos(x + chunkX * 16, 0, z + chunkZ * 16));
        
        Block block = blockState.getBlock();
        Block topBlock = biome.topBlock.getBlock();
        Block fillerBlock = biome.fillerBlock.getBlock();

        if (this.canReplaceBlock(blockState, blockStateUp) || block == topBlock || block == fillerBlock) {
            if (y - 1 < this.worldFloor + ModernBetaGeneratorSettings.CARVER_LAVA_LEVEL) {
                chunkPrimer.setBlockState(x, y, z, BlockStates.LAVA);
            } else {
                chunkPrimer.setBlockState(x, y, z, BlockStates.AIR);

                if (isTopSoil && chunkPrimer.getBlockState(x, y - 1, z).getBlock() == fillerBlock) {
                    chunkPrimer.setBlockState(x, y - 1, z, topBlock.getDefaultState());
                }
            }
        }
    }

    protected ImmutableSet.Builder<Block> initializeCarvables(Block defaultBlock) {
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
                ModernBeta.log(Level.DEBUG, String.format("Adding vanilla cave carvables from mod '%s'", entry.getKey()));
                
                carvables.addAll(((CarverCompat)compat).getCarvables());
            }
        }
        
        return carvables;
    }
}
