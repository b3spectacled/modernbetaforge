package mod.bespectacled.modernbetaforge.world.feature;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class WorldGenMinableMutable extends WorldGenMinable {
    public WorldGenMinableMutable(IBlockState state, int blockCount, Predicate<IBlockState> predicate) {
        super(state, blockCount, predicate);
    }
    
    public WorldGenMinableMutable(IBlockState state, int blockCount) {
        super(state, blockCount);
    }
    
    @Override
    public boolean generate(World world, Random random, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        int startX = blockPos.getX();
        int startY = blockPos.getY();
        int startZ = blockPos.getZ();
        
        float theta = random.nextFloat() * (float)Math.PI;
        float numBlocks = (float)this.numberOfBlocks;
        
        double maxBX = (double)((float)(startX + 8) + MathHelper.sin(theta) * numBlocks / 8.0f);
        double minBX = (double)((float)(startX + 8) - MathHelper.sin(theta) * numBlocks / 8.0f);
        double maxBZ = (double)((float)(startZ + 8) + MathHelper.cos(theta) * numBlocks / 8.0f);
        double minBZ = (double)((float)(startZ + 8) - MathHelper.cos(theta) * numBlocks / 8.0f);
        double maxBY = (double)(startY + random.nextInt(3) - 2);
        double minBY = (double)(startY + random.nextInt(3) - 2);

        for (int b = 0; b < this.numberOfBlocks; ++b) {
            float progress = (float)b / numBlocks;
            double bX = maxBX + (minBX - maxBX) * (double)progress;
            double bY = maxBY + (minBY - maxBY) * (double)progress;
            double bZ = maxBZ + (minBZ - maxBZ) * (double)progress;
            
            double f = random.nextDouble() * (double)this.numberOfBlocks / 16.0;
            theta = (float)Math.PI * progress;
            
            double tW = (double)(MathHelper.sin(theta) + 1.0f) * f + 1.0;
            double tH = (double)(MathHelper.sin(theta) + 1.0f) * f + 1.0;

            int minX = MathHelper.floor(bX - tW / 2.0);
            int maxX = MathHelper.floor(bX + tW / 2.0);
            int minY = MathHelper.floor(bY - tH / 2.0);
            int maxY = MathHelper.floor(bY + tH / 2.0);
            int minZ = MathHelper.floor(bZ - tW / 2.0);
            int maxZ = MathHelper.floor(bZ + tW / 2.0);

            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        double dX = ((double)x + 0.5D - bX) / (tW / 2.0);
                        double dY = ((double)y + 0.5D - bY) / (tH / 2.0);
                        double dZ = ((double)z + 0.5D - bZ) / (tW / 2.0);
                        
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
        }

        return true;
    }
}
