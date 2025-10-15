package mod.bespectacled.modernbetaforge.world.feature;

import java.util.Random;
import java.util.function.Consumer;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class WorldGenMinableMutable extends WorldGenMinable {
    public static final Predicate<IBlockState> STONE_PREDICATE = new Predicate<IBlockState>() {
        @Override
        public boolean apply(IBlockState blockState) {
            if (blockState != null && blockState.getBlock() == Blocks.STONE) {
                return ((BlockStone.EnumType)blockState.getValue(BlockStone.VARIANT)).isNatural();
            }
            
            return false;
        }
    };
            
    private final OreType oreType;
    
    public WorldGenMinableMutable(IBlockState state, int blockCount, Predicate<IBlockState> predicate, OreType oreType) {
        super(state, blockCount, predicate);
        
        this.oreType = oreType;
    }
    
    public WorldGenMinableMutable(IBlockState state, int blockCount, OreType oreType) {
        this(state, blockCount, STONE_PREDICATE, oreType);
    }
    
    @Override
    public boolean generate(World world, Random random, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean useOldOre = this.oreType == OreType.CLASSIC;
        
        int startX = blockPos.getX();
        int startY = blockPos.getY();
        int startZ = blockPos.getZ();
        
        float theta = random.nextFloat() * 3.141593f;
        float numBlocks = (float)this.numberOfBlocks;
        
        double maxBX = (float)(startX + 8) + MathHelper.sin(theta) * numBlocks / 8.0f;
        double minBX = (float)(startX + 8) - MathHelper.sin(theta) * numBlocks / 8.0f;
        double maxBZ = (float)(startZ + 8) + MathHelper.cos(theta) * numBlocks / 8.0f;
        double minBZ = (float)(startZ + 8) - MathHelper.cos(theta) * numBlocks / 8.0f;
        double maxBY = startY + random.nextInt(3) + (useOldOre ? 2 : -2);
        double minBY = startY + random.nextInt(3) + (useOldOre ? 2 : -2);
        
        Consumer<Integer> oreGenerator = i -> {
            double progress = (double)i / numBlocks;
            double bX = maxBX + (minBX - maxBX) * progress;
            double bY = maxBY + (minBY - maxBY) * progress;
            double bZ = maxBZ + (minBZ - maxBZ) * progress;
            
            double f = random.nextDouble() * (double)this.numberOfBlocks / 16.0;
            float tTheta = 3.141593f * (float)progress;
            
            double tW = (double)(MathHelper.sin(tTheta) + 1.0f) * f + 1.0;
            double tH = (double)(MathHelper.sin(tTheta) + 1.0f) * f + 1.0;

            int minX = MathHelper.floor(bX - tW / 2.0);
            int maxX = MathHelper.floor(bX + tW / 2.0);
            int minY = MathHelper.floor(bY - tH / 2.0);
            int maxY = MathHelper.floor(bY + tH / 2.0);
            int minZ = MathHelper.floor(bZ - tW / 2.0);
            int maxZ = MathHelper.floor(bZ + tW / 2.0);

            for (int x = minX; x <= maxX; ++x) {
                double dX = ((double)x + 0.5 - bX) / (tW / 2.0);
                if (dX * dX >= 1.0) {
                    continue;
                }
                
                for (int y = minY; y <= maxY; ++y) {
                    double dY = ((double)y + 0.5 - bY) / (tH / 2.0);
                    if (dX * dX + dY * dY >= 1.0) {
                        continue;
                    }
                    
                    for (int z = minZ; z <= maxZ; ++z) {
                        double dZ = ((double)z + 0.5 - bZ) / (tW / 2.0);
                        if (dX * dX + dY * dY + dZ * dZ >= 1.0) {
                            continue;
                        }

                        IBlockState state = world.getBlockState(mutablePos.setPos(x, y, z));
                        Block block = state.getBlock();
                        
                        if (block.isReplaceableOreGen(state, world, mutablePos, this.predicate)) {
                            world.setBlockState(mutablePos, this.oreBlock, 2);
                        }
                    }
                }
            }
        };

        if (useOldOre) {
            for (int i = 0; i <= this.numberOfBlocks; ++i) {
                oreGenerator.accept(i);
            }
            
        } else {
            for (int i = 0; i < this.numberOfBlocks; ++i) {
                oreGenerator.accept(i);
            }
        }

        return true;
    }
}
