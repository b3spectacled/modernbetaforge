package mod.bespectacled.modernbetaforge.util;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class BlockStates {
    public static final IBlockState GLASS = Blocks.GLASS.getDefaultState();
    
    public static final IBlockState GRASS_BLOCK = Blocks.GRASS.getDefaultState();
    public static final IBlockState DIRT = Blocks.DIRT.getDefaultState();
    
    public static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    public static final IBlockState SAND = Blocks.SAND.getDefaultState();
    public static final IBlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    public static final IBlockState RED_SANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();
    public static final IBlockState CLAY = Blocks.CLAY.getDefaultState();
    
    public static final IBlockState STONE = Blocks.STONE.getDefaultState();
    public static final IBlockState AIR = Blocks.AIR.getDefaultState();
    public static final IBlockState WATER = Blocks.WATER.getDefaultState();
    public static final IBlockState ICE = Blocks.ICE.getDefaultState();
    public static final IBlockState SNOW = Blocks.SNOW.getDefaultState();
    public static final IBlockState LAVA = Blocks.LAVA.getDefaultState();
    public static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    public static final IBlockState OBSIDIAN = Blocks.OBSIDIAN.getDefaultState();
    public static final IBlockState BRICK = Blocks.BRICK_BLOCK.getDefaultState();
    
    public static final IBlockState GRASS = Blocks.TALLGRASS.getDefaultState();
    public static final IBlockState FERN = Blocks.TALLGRASS.getStateFromMeta(2);

    public static final IBlockState OAK_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
    public static final IBlockState OAK_LEAVES = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
    
    public static final IBlockState JUNGLE_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
    public static final IBlockState JUNGLE_LEAVES = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
    
    public static final IBlockState COAL_ORE = Blocks.COAL_ORE.getDefaultState();
    public static final IBlockState IRON_ORE = Blocks.IRON_ORE.getDefaultState();
    public static final IBlockState GOLD_ORE = Blocks.GOLD_ORE.getDefaultState();
    public static final IBlockState REDSTONE_ORE = Blocks.REDSTONE_ORE.getDefaultState();
    public static final IBlockState DIAMOND_ORE = Blocks.DIAMOND_ORE.getDefaultState();
    public static final IBlockState LAPIS_ORE = Blocks.LAPIS_ORE.getDefaultState();
    public static final IBlockState EMERALD_ORE = Blocks.EMERALD_ORE.getDefaultState();
    
    public static final IBlockState GRANITE = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE);
    public static final IBlockState DIORITE = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
    public static final IBlockState ANDESITE = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);
    
    public static final IBlockState QUARTZ_ORE = Blocks.QUARTZ_ORE.getDefaultState();
    public static final IBlockState MAGMA = Blocks.MAGMA.getDefaultState();
    
    public static boolean isAir(IBlockState blockState) {
        return blockState.getBlock().isAir(blockState, null, null);
    }
    
    public static boolean isEqual(IBlockState blockState0, IBlockState blockState1) {
        return blockState0.getBlock().equals(blockState1.getBlock());
    }
}