package mod.bespectacled.modernbetaforge.world.feature;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenNoOp extends WorldGenerator {
    public static final WorldGenNoOp INSTANCE = new WorldGenNoOp();
    
    @Override
    public boolean generate(World world, Random random, BlockPos blockPos) {
        return true;
    }
    
}
