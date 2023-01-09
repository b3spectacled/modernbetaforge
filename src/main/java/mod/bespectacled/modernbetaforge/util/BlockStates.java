package mod.bespectacled.modernbetaforge.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

@SuppressWarnings("deprecation")
public class BlockStates {
    public static final IBlockState GRASS_BLOCK = Blocks.GRASS.getDefaultState();
    public static final IBlockState PODZOL = Blocks.DIRT.getStateFromMeta(2);
    public static final IBlockState DIRT = Blocks.DIRT.getDefaultState();
    
    public static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    public static final IBlockState SAND = Blocks.SAND.getDefaultState();
    public static final IBlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    public static final IBlockState CLAY = Blocks.CLAY.getDefaultState();
    
    public static final IBlockState STONE = Blocks.STONE.getDefaultState();
    public static final IBlockState AIR = Blocks.AIR.getDefaultState();
    public static final IBlockState WATER = Blocks.WATER.getDefaultState();
    public static final IBlockState ICE = Blocks.ICE.getDefaultState();
    public static final IBlockState SNOW = Blocks.SNOW.getDefaultState();
    public static final IBlockState LAVA = Blocks.LAVA.getDefaultState();
    public static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    
    public static final IBlockState GRASS = Blocks.TALLGRASS.getDefaultState();
    public static final IBlockState FERN = Blocks.TALLGRASS.getStateFromMeta(2);
    
    public static final IBlockState OAK_LEAVES = Blocks.LEAVES.getDefaultState();
    public static final IBlockState OAK_LOG = Blocks.LOG.getDefaultState();
    
    public static boolean isAir(IBlockState blockState) {
        return blockState.getBlock().isAir(blockState, null, null);
    }
    
    public static boolean isEqual(IBlockState blockState0, IBlockState blockState1) {
        return blockState0.getBlock().equals(blockState1.getBlock());
    }
}