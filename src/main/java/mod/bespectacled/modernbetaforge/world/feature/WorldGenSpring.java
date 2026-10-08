package mod.bespectacled.modernbetaforge.world.feature;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.compat.depthsupdate.CompatDepthsUpdate;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class WorldGenSpring extends WorldGenerator {
    private final Block blockLiquid;
    private final Set<Block> replaceableBlocks;
    
    public WorldGenSpring(Block blockLiquid, ModernBetaGeneratorSettings settings) {
        this.blockLiquid = blockLiquid;
        this.replaceableBlocks = initReplaceableBlocks(settings);
    }

    @Override
    public boolean generate(World world, Random random, BlockPos pos) {
        IBlockState originState = world.getBlockState(pos);
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        int sidesClosed = 0;
        int sidesOpened = 0;
        
        if (!this.replaceableBlocks.contains(world.getBlockState(move(mutablePos, pos, EnumFacing.UP)).getBlock())) {
            return false;
        }
        
        if (!this.replaceableBlocks.contains(world.getBlockState(move(mutablePos, pos, EnumFacing.DOWN)).getBlock())) {
            return false;
        }

        if (!originState.getBlock().isAir(originState, world, pos) && !this.replaceableBlocks.contains(originState.getBlock())) {
            return false;
        }
        
        if (this.replaceableBlocks.contains(world.getBlockState(move(mutablePos, pos, EnumFacing.WEST)).getBlock())) {
            ++sidesClosed;
        }

        if (this.replaceableBlocks.contains(world.getBlockState(move(mutablePos, pos, EnumFacing.EAST)).getBlock())) {
            ++sidesClosed;
        }

        if (this.replaceableBlocks.contains(world.getBlockState(move(mutablePos, pos, EnumFacing.NORTH)).getBlock())) {
            ++sidesClosed;
        }

        if (this.replaceableBlocks.contains(world.getBlockState(move(mutablePos, pos, EnumFacing.SOUTH)).getBlock())) {
            ++sidesClosed;
        }

        if (world.isAirBlock(move(mutablePos, pos, EnumFacing.WEST))) {
            ++sidesOpened;
        }

        if (world.isAirBlock(move(mutablePos, pos, EnumFacing.EAST))) {
            ++sidesOpened;
        }

        if (world.isAirBlock(move(mutablePos, pos, EnumFacing.NORTH))) {
            ++sidesOpened;
        }

        if (world.isAirBlock(move(mutablePos, pos, EnumFacing.SOUTH))) {
            ++sidesOpened;
        }

        if (sidesClosed == 3 && sidesOpened == 1) {
            IBlockState blockState = this.blockLiquid.getDefaultState();
            world.setBlockState(pos, blockState, 2);
            world.immediateBlockTick(pos, blockState, random);
        }

        return true;
    }
    
    private static MutableBlockPos move(MutableBlockPos mutablePos, BlockPos originPos, EnumFacing direction) {
        return mutablePos.setPos(originPos).move(direction);
    }

    private static Set<Block> initReplaceableBlocks(ModernBetaGeneratorSettings settings) {
        ImmutableSet.Builder<Block> replaceables = new ImmutableSet.Builder<>();
        replaceables.add(ForgeRegistryUtil.get(settings.defaultBlock, ForgeRegistries.BLOCKS));
        
        if (ModCompat.isCompatLoaded(CompatDepthsUpdate.MOD_ID)) {
            boolean useCompat = settings.getBooleanProperty(CompatDepthsUpdate.KEY_USE_COMPAT);
            boolean useDeepslate = settings.getBooleanProperty(CompatDepthsUpdate.KEY_USE_DEEPSLATE);
            
            if (useCompat && useDeepslate) {
                replaceables.add(settings.getBlockProperty(CompatDepthsUpdate.KEY_DEEPSLATE_BLOCK));
            }
        }
        
        return replaceables.build();
    }
}
