package mod.bespectacled.modernbetaforge.world.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenClay extends WorldGenerator {
    private final Block block;
    private final int numBlocks;
    
    public WorldGenClay(int numberOfBlocks) {
        this.block = Blocks.CLAY;
        this.numBlocks = numberOfBlocks;
    }

    @Override
    public boolean generate(World world, Random random, BlockPos blockPos) {
        if (world.getBlockState(blockPos).getMaterial() != Material.WATER) {
            return false;
        }
        
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        int startX = blockPos.getX();
        int startY = blockPos.getY();
        int startZ = blockPos.getZ();
        
        // Ranges from [0, 1]
        float theta = random.nextFloat() * 3.141593f;
        float numBlocks = (float)this.numBlocks;
        
        // Calculate minimum and maximum extents of ore blob
        // Example: If ore size is 33, then range for value added to start coord is [0, 4.125] 
        double maxBX = (float)(startX + 8) + (MathHelper.sin(theta) * numBlocks) / 8f;
        double minBX = (float)(startX + 8) - (MathHelper.sin(theta) * numBlocks) / 8f;
        double maxBZ = (float)(startZ + 8) + (MathHelper.cos(theta) * numBlocks) / 8f;
        double minBZ = (float)(startZ + 8) - (MathHelper.cos(theta) * numBlocks) / 8f;
        double maxBY = startY + random.nextInt(3) + 2;
        double minBY = startY + random.nextInt(3) + 2;
        
        for(int b = 0; b <= this.numBlocks; b++) {
            // Get initial coordinates for placement,
            // ranging between min and max as a function of block / numBlocks
            double bX = maxBX + ((minBX - maxBX) * (double)b) / numBlocks;
            double bY = maxBY + ((minBY - maxBY) * (double)b) / numBlocks;
            double bZ = maxBZ + ((minBZ - maxBZ) * (double)b) / numBlocks;
            
            double f = (random.nextDouble() * (double)numBlocks) / 16.0;
            theta = ((float)b * 3.141593f) / numBlocks;
            
            double tW = (double)(MathHelper.sin(theta) + 1.0f) * f + 1.0;
            double tH = (double)(MathHelper.sin(theta) + 1.0f) * f + 1.0;
            
            int minX = MathHelper.floor(bX - tW / 2.0);
            int maxX = MathHelper.floor(bX + tW / 2.0);
            int minY = MathHelper.floor(bY - tH / 2.0);
            int maxY = MathHelper.floor(bY + tH / 2.0);
            int minZ = MathHelper.floor(bZ - tW / 2.0);
            int maxZ = MathHelper.floor(bZ + tW / 2.0);
            
            for(int x = minX; x <= maxX; x++) {
                for(int y = minY; y <= maxY; y++) {
                    for(int z = minZ; z <= maxZ; z++) {
                        double dX = (((double)x + 0.5) - bX) / (tW / 2.0);
                        double dY = (((double)y + 0.5) - bY) / (tH / 2.0);
                        double dZ = (((double)z + 0.5) - bZ) / (tW / 2.0);
                        
                        if (dX * dX + dY * dY + dZ * dZ >= 1.0) {
                            continue;
                        }
                        
                        Block block = world.getBlockState(mutablePos.setPos(x, y, z)).getBlock();
                        
                        if (block == Blocks.SAND) {
                            world.setBlockState(mutablePos, this.block.getDefaultState());
                        }
                    }
                }
            }
        }

        return true;
    }
}
