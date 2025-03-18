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
    private final int numberOfBlocks;
    
    public WorldGenClay(int numberOfBlocks) {
        this.block = Blocks.CLAY;
        this.numberOfBlocks = numberOfBlocks;
    }

    @Override
    public boolean generate(World world, Random random, BlockPos blockPos) {
        if (world.getBlockState(blockPos).getMaterial() != Material.WATER) {
            return false;
        }
        
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        int originX = blockPos.getX();
        int originY = blockPos.getY();
        int originZ = blockPos.getZ();
        
        float radius = random.nextFloat() * 3.141593F;
        
        double x0 = (float)(originX + 8) + (MathHelper.sin(radius) * (float)numberOfBlocks) / 8F;
        double x1 = (float)(originX + 8) - (MathHelper.sin(radius) * (float)numberOfBlocks) / 8F;
        double z0 = (float)(originZ + 8) + (MathHelper.cos(radius) * (float)numberOfBlocks) / 8F;
        double z1 = (float)(originZ + 8) - (MathHelper.cos(radius) * (float)numberOfBlocks) / 8F;
        
        double y0 = originY + random.nextInt(3) + 2;
        double y1 = originY + random.nextInt(3) + 2;
        
        for(int block = 0; block <= this.numberOfBlocks; block++) {
            double d6 = x0 + ((x1 - x0) * (double)block) / (double)numberOfBlocks;
            double d7 = y0 + ((y1 - y0) * (double)block) / (double)numberOfBlocks;
            double d8 = z0 + ((z1 - z0) * (double)block) / (double)numberOfBlocks;
            
            double d9 = (random.nextDouble() * (double)numberOfBlocks) / 16D;
            
            double d10 = (double)(MathHelper.sin(((float)block * 3.141593F) / (float)numberOfBlocks) + 1.0F) * d9 + 1.0;
            double d11 = (double)(MathHelper.sin(((float)block * 3.141593F) / (float)numberOfBlocks) + 1.0F) * d9 + 1.0;
            
            int minX = MathHelper.floor(d6 - d10 / 2.0);
            int maxX = MathHelper.floor(d6 + d10 / 2.0);
            int minY = MathHelper.floor(d7 - d11 / 2.0);
            int maxY = MathHelper.floor(d7 + d11 / 2.0);
            int minZ = MathHelper.floor(d8 - d10 / 2.0);
            int maxZ = MathHelper.floor(d8 + d10 / 2.0);
            
            for(int x = minX; x <= maxX; x++) {
                for(int y = minY; y <= maxY; y++) {
                    for(int z = minZ; z <= maxZ; z++) {
                        double dX = (((double)x + 0.5D) - d6) / (d10 / 2.0);
                        double dY = (((double)y + 0.5D) - d7) / (d11 / 2.0);
                        double dZ = (((double)z + 0.5D) - d8) / (d10 / 2.0);
                        
                        if(dX * dX + dY * dY + dZ * dZ >= 1.0) {
                            continue;
                        }
                        
                        Block curBlock = world.getBlockState(mutablePos.setPos(x, y, z)).getBlock();
                        
                        if(curBlock == Blocks.SAND) {
                            world.setBlockState(mutablePos, this.block.getDefaultState());
                        }
                    }
                }
            }
        }

        return true;
    }

}
