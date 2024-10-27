package mod.bespectacled.modernbetaforge.world.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenHellSpring extends WorldGenerator {
    private final Block block;
    
    public WorldGenHellSpring(Block block) {
        this.block = block;
    }
    
    @Override
    public boolean generate(World world, Random random, BlockPos pos) {
        int sidesClosed = 0;
        int sidesOpened = 0;
        
        if (world.getBlockState(pos.up()).getBlock() != Blocks.NETHERRACK) {
            return false;
        }
        
        if (!world.isAirBlock(pos) && world.getBlockState(pos).getBlock() != Blocks.NETHERRACK) {
            return false;
        }

        if (world.getBlockState(pos.west()).getBlock() == Blocks.NETHERRACK) {
            ++sidesClosed;
        }

        if (world.getBlockState(pos.east()).getBlock() == Blocks.NETHERRACK) {
            ++sidesClosed;
        }

        if (world.getBlockState(pos.north()).getBlock() == Blocks.NETHERRACK) {
            ++sidesClosed;
        }

        if (world.getBlockState(pos.south()).getBlock() == Blocks.NETHERRACK) {
            ++sidesClosed;
        }

        if (world.getBlockState(pos.down()).getBlock() == Blocks.NETHERRACK) {
            ++sidesClosed;
        }

        if (world.isAirBlock(pos.west())) {
            ++sidesOpened;
        }

        if (world.isAirBlock(pos.east())) {
            ++sidesOpened;
        }

        if (world.isAirBlock(pos.north())) {
            ++sidesOpened;
        }

        if (world.isAirBlock(pos.south())) {
            ++sidesOpened;
        }

        if (world.isAirBlock(pos.down())) {
            ++sidesOpened;
        }
        
        if (sidesClosed == 4 && sidesOpened == 1) {
            IBlockState blockState = this.block.getDefaultState();
            world.setBlockState(pos, blockState, 2);
            world.immediateBlockTick(pos, blockState, random);
        }
        
        return true;
    }
    
}
