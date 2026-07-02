package mod.bespectacled.modernbetaforge.compat.futuremc;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import thedarkcolour.core.util.UtilKt;
import thedarkcolour.futuremc.config.FConfig;
import thedarkcolour.futuremc.entity.bee.EntityBee;
import thedarkcolour.futuremc.world.gen.feature.BeeNestGenerator;

public class WorldGenBeeNest {
    public static void generateForOak(World world, Random random, BlockPos pos, int height) {
        if (cannotGenerate(world, random, pos)) {
            return;
        }
        
        EnumFacing offset = BeeNestGenerator.INSTANCE.getVALID_OFFSETS()[random.nextInt(3)];
        MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), pos.getY() + height - 4, pos.getZ()).move(offset);
        
        if (world.isAirBlock(mutablePos) && world.isAirBlock(mutablePos.move(EnumFacing.SOUTH))) {
            BeeNestGenerator.INSTANCE.placeBeeHive(world, random, mutablePos.move(EnumFacing.NORTH));
        }
    }
    
    public static void generateForFancyOak(World world, Random random, BlockPos pos, int height, WorldGenAbstractTree tree) {
        if (cannotGenerate(world, random, pos)) {
            return;
        }
        
        EnumFacing direction = BeeNestGenerator.INSTANCE.getVALID_OFFSETS()[random.nextInt(3)];
        MutableBlockPos mutablePos = new MutableBlockPos(pos.offset(direction));
        
        for (int i = 0; i < height; ++i) {
            if (world.isAirBlock(mutablePos.move(EnumFacing.UP))) {
                continue;
            }
            
            if (tree.isReplaceable(world, mutablePos.move(EnumFacing.DOWN)) && tree.isReplaceable(world, mutablePos.move(EnumFacing.SOUTH))) {
                world.setBlockToAir(mutablePos);
                BeeNestGenerator.INSTANCE.placeBeeHive(world, random, mutablePos.move(EnumFacing.NORTH));
            }
        }
    }
    
    private static boolean cannotGenerate(World world, Random random, BlockPos pos) {
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(world);
        
        if (!FConfig.INSTANCE.getBuzzyBees().bee.enabled) {
            return true;
        }
        
        if (settings.getBooleanProperty(CompatFutureMC.KEY_BEE_NEST_FLOWER_CHECK) && hasNoFlowersNearby(world, pos)) {
            return true;
        }

        Biome biome = world.getBiome(pos);
        if (random.nextDouble() > UtilKt.getDoubleOrDefault(BeeNestGenerator.INSTANCE.getBIOMES_AND_CHANCES(), biome.getRegistryName(), 0.0)) {
            return true;
        }
        return false;
    }
    
    private static boolean hasNoFlowersNearby(World world, BlockPos pos) {
        int startX = pos.getX() - 2;
        int startY = pos.getY() - 1;
        int startZ = pos.getZ() - 2;
        int endX = pos.getX() + 2;
        int endY = pos.getY() + 1;
        int endZ = pos.getZ() + 2;
        
        if (world.isAreaLoaded(startX, startY, startZ, endX, endY, endZ, true)) {
            for (BlockPos localPos : BlockPos.getAllInBoxMutable(startX, startY, startZ, endX, endY, endZ)) {
                if (EntityBee.isFlowerValid(world.getBlockState(localPos))) {
                    return false;
                }
            }
        }

        return true;
    }
}
