package mod.bespectacled.modernbetaforge.world.carver;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public class MapGenBetaCave extends MapGenBase {
    public MapGenBetaCave() {
        super();
    }
    
    @Override
    public void generate(World world, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer) {
        this.world = world;
        this.rand.setSeed(world.getSeed());
        
        long randomLong0 = (this.rand.nextLong() / 2L) * 2L + 1L;
        long randomLong1 = (this.rand.nextLong() / 2L) * 2L + 1L;
        
        for (int chunkX = originChunkX - this.range; chunkX <= originChunkX + this.range; ++chunkX) {
            for (int chunkZ = originChunkZ - this.range; chunkZ <= originChunkZ + this.range; ++chunkZ) {
                
                this.rand.setSeed((long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ world.getSeed());
                this.recursiveGenerate(world, chunkX, chunkZ, originChunkX, originChunkZ, chunkPrimer);
            }
        }
    }
    
    @Override
    protected void recursiveGenerate(World world, int chunkX, int chunkZ, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer) {
        int caveCount = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
        if (this.rand.nextInt(getMaxCaveCount()) != 0) {
            caveCount = 0;
        }

        for (int i = 0; i < caveCount; ++i) {
            double x = chunkX * 16 + this.rand.nextInt(16); // Starts
            double y = getCaveY(this.rand);
            double z = chunkZ * 16 + this.rand.nextInt(16);

            int tunnelCount = 1;
            if (this.rand.nextInt(4) == 0) {
                this.carveCave(chunkPrimer, originChunkX, originChunkZ, x, y, z);
                tunnelCount += this.rand.nextInt(4);
            }

            for (int j = 0; j < tunnelCount; ++j) {
                float f = this.rand.nextFloat() * 3.141593F * 2.0F;
                float f1 = ((this.rand.nextFloat() - 0.5F) * 2.0F) / 8F;
                float tunnelSysWidth = getTunnelSystemWidth(this.rand);

                carveTunnels(chunkPrimer, originChunkX, originChunkZ, x, y, z, tunnelSysWidth, f, f1, 0, 0, 1.0);
            }
        }
    }
    
    private void carveCave(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double x, double y, double z) {
        carveTunnels(chunkPrimer, chunkX, chunkZ, x, y, z, 1.0F + this.rand.nextFloat() * 6F, 0.0F, 0.0F, -1, -1, 0.5);
    }
    
    private void carveTunnels(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double x, double y, double z, float tunnelSysWidth, float f1, float f2, int branch, int branchCount, double tunnelWHRatio) {
        float f3 = 0.0F;
        float f4 = 0.0F;

        Random newRandom = new Random(this.rand.nextLong());

        if (branchCount <= 0) {
            int someNumMaxStarts = 8 * 16 - 16;
            branchCount = someNumMaxStarts - newRandom.nextInt(someNumMaxStarts / 4);
        }

        boolean noStarts = false;
        if (branch == -1) {
            branch = branchCount / 2;
            noStarts = true;
        }

        int randBranch = newRandom.nextInt(branchCount / 2) + branchCount / 4;
        boolean vary = newRandom.nextInt(6) == 0;

        for (; branch < branchCount; branch++) {
            double yaw = 1.5 + (double) (MathHelper.sin(((float) branch * 3.141593F) / (float) branchCount) * tunnelSysWidth * 1.0F);
            double pitch = yaw * tunnelWHRatio;

            float f5 = MathHelper.cos(f2);
            float f6 = MathHelper.sin(f2);

            x += MathHelper.cos(f1) * f5;
            y += f6;
            z += MathHelper.sin(f1) * f5;

            f2 *= vary ? 0.92F : 0.7F;

            f2 += f4 * 0.1F;
            f1 += f3 * 0.1F;

            f4 *= 0.9F;
            f3 *= 0.75F;

            f4 += (newRandom.nextFloat() - newRandom.nextFloat()) * newRandom.nextFloat() * 2.0F;
            f3 += (newRandom.nextFloat() - newRandom.nextFloat()) * newRandom.nextFloat() * 4F;

            if (!noStarts && branch == randBranch && tunnelSysWidth > 1.0F) {
                carveTunnels(chunkPrimer, chunkX, chunkZ, x, y, z, newRandom.nextFloat() * 0.5F + 0.5F, f1 - 1.570796F, f2 / 3F, branch, branchCount, 1.0);
                carveTunnels(chunkPrimer, chunkX, chunkZ, x, y, z, newRandom.nextFloat() * 0.5F + 0.5F, f1 + 1.570796F, f2 / 3F, branch, branchCount, 1.0);
                return;
            }

            if (!noStarts && newRandom.nextInt(4) == 0) {
                continue;
            }

            if (!canCarveBranch(chunkX, chunkZ, x, z, branch, branchCount, tunnelSysWidth)) {
                return;
            }
            
            carveRegion(chunkPrimer, 0, 64, chunkX, chunkZ, x, y, z, yaw, pitch); 

            if (noStarts) {
                break;
            }
        }
    }

    protected boolean carveRegion(ChunkPrimer chunkPrimer, long seed, int seaLevel, int chunkX, int chunkZ, double x, double y, double z, double yaw, double pitch) {
        double ctrX = chunkX * 16 + 8;
        double ctrZ = chunkZ * 16 + 8;

        if ( // Check for valid tunnel starts, I guess? Or to prevent overlap?
        x < ctrX - 16.0 - yaw * 2.0 || z < ctrZ - 16.0 - yaw * 2.0 || x > ctrX + 16.0 + yaw * 2.0 || z > ctrZ + 16.0 + yaw * 2.0) {
            return false;
        }

        int minX = MathHelper.floor(x - yaw) - chunkX * 16 - 1; // Get min and max extents of tunnel, relative to chunk coords
        int maxX = (MathHelper.floor(x + yaw) - chunkX * 16) + 1;

        int minY = MathHelper.floor(y - pitch) - 1;
        int maxY = MathHelper.floor(y + pitch) + 1;

        int minZ = MathHelper.floor(z - yaw) - chunkZ * 16 - 1;
        int maxZ = (MathHelper.floor(z + yaw) - chunkZ * 16) + 1;

        if (minX < 0) {
            minX = 0;
        }
        if (maxX > 16) {
            maxX = 16;
        }

        if (minY < 1) {
            minY = 1;
        }
        if (maxY > 120) {
            maxY = 120;
        }

        if (minZ < 0) {
            minZ = 0;
        }
        if (maxZ > 16) {
            maxZ = 16;
        }

        if (isRegionUncarvable(chunkPrimer, chunkX, chunkZ, minX, maxX, minY, maxY, minZ, maxZ)) { 
            return false;
        }

        for (int relX = minX; relX < maxX; relX++) {
            double scaledRelX = (((double) (relX + chunkX * 16) + 0.5) - x) / yaw;

            for (int relZ = minZ; relZ < maxZ; relZ++) {
                double scaledRelZ = (((double) (relZ + chunkZ * 16) + 0.5) - z) / yaw;
                boolean isGrassBlock = false;
                
                for (int relY = maxY; relY >= minY; relY--) {
                    double scaledRelY = (((double) relY - 1 + 0.5) - y) / pitch;

                    if (isPositionExcluded(scaledRelX, scaledRelY, scaledRelZ)) {
                        Block block = chunkPrimer.getBlockState(relX, relY, relZ).getBlock();

                        if (block == Blocks.GRASS) {
                            isGrassBlock = true;
                        }

                        // Don't use canCarveBlock for accuracy, for now.
                        if (block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRASS) { 
                            if (relY - 1 < 10) { // Set lava below y = 10
                                chunkPrimer.setBlockState(relX, relY, relZ, Blocks.LAVA.getDefaultState());
                            } else {
                                chunkPrimer.setBlockState(relX, relY, relZ, Blocks.AIR.getDefaultState());

                                // This replaces carved-out dirt with grass, if block that was removed was grass.
                                if (isGrassBlock && chunkPrimer.getBlockState(relX, relY - 1, relZ).getBlock() == Blocks.DIRT) {
                                    chunkPrimer.setBlockState(relX, relY - 1, relZ, Blocks.GRASS.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean canCarveBranch(int mainChunkX, int mainChunkZ, double x, double z, int branch, int branchCount, float baseWidth) {
        double ctrX = mainChunkX * 16 + 8;
        double ctrZ = mainChunkZ * 16 + 8;

        double d1 = x - ctrX;
        double d2 = z - ctrZ;
        double d3 = branchCount - branch;
        double d4 = baseWidth + 2.0F + 16F;

        if ((d1 * d1 + d2 * d2) - d3 * d3 > d4 * d4) {
            return false;
        }

        return true;
    }

    private boolean isRegionUncarvable(ChunkPrimer chunkPrimer, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ) {
        for (int relX = relMinX; relX < relMaxX; relX++) {
            for (int relZ = relMinZ; relZ < relMaxZ; relZ++) {
                for (int relY = maxY + 1; relY >= minY - 1; relY--) {

                    if (relY < 0 || relY >= 128) {
                        continue;
                    }

                    Block block = chunkPrimer.getBlockState(relX, relY, relZ).getBlock();

                    if (block.equals(Blocks.WATER) || block.equals(Blocks.FLOWING_WATER)) {
                        return true;
                    }

                    if (relY != minY - 1 && isOnBoundary(relMinX, relMaxX, relMinZ, relMaxZ, relX, relZ)) {
                        relY = minY;
                    }
                }

            }
        }

        return false;
    }

    private boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ) {
        return scaledRelativeY > -0.7 && scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ < 1.0;
    }

    private boolean isOnBoundary(int minX, int maxX, int minZ, int maxZ, int relX, int relZ) {
        return relX != minX && relX != maxX - 1 && relZ != minZ && relZ != maxZ - 1;
    }

    private int getMaxCaveCount() {
        return 15;
    }

    private int getCaveY(Random random) {
        return random.nextInt(random.nextInt(120) + 8);
    }

    private float getTunnelSystemWidth(Random random) {
        float width = random.nextFloat() * 2.0f + random.nextFloat();
        
        return width;
    }
}
