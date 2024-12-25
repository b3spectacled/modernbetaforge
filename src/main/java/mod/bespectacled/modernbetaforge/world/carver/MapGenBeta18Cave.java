package mod.bespectacled.modernbetaforge.world.carver;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenBeta18Cave extends MapGenBetaCave {
    public MapGenBeta18Cave(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings);
    }
    
    public MapGenBeta18Cave() {
        super(BlockStates.STONE, BlockStates.WATER, 128, 40, 15);
    }
    
    @Override
    public void generate(World world, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer) {
        this.world = world;
        this.rand.setSeed(world.getSeed());
        
        long randomLong0 = this.rand.nextLong();
        long randomLong1 = this.rand.nextLong();
        
        for (int chunkX = originChunkX - this.range; chunkX <= originChunkX + this.range; ++chunkX) {
            for (int chunkZ = originChunkZ - this.range; chunkZ <= originChunkZ + this.range; ++chunkZ) {
                
                long randomLong2 = (long)chunkX * randomLong0;
                long randomLong3 = (long)chunkZ * randomLong1;
                this.rand.setSeed(randomLong2 ^ randomLong3 ^ world.getSeed());
                
                this.recursiveGenerate(world, chunkX, chunkZ, originChunkX, originChunkZ, chunkPrimer);
            }
        }
    }
    
    @Override
    protected void recursiveGenerate(World world, int chunkX, int chunkZ, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer) {
        int caveCount = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(this.getBaseCaveCount()) + 1) + 1);
        if (this.rand.nextInt(this.getRegionalCaveChance()) != 0) {
            caveCount = 0;
        }

        for (int i = 0; i < caveCount; ++i) {
            double x = chunkX * 16 + this.rand.nextInt(16); // Starts
            double y = this.getCaveY(this.rand);
            double z = chunkZ * 16 + this.rand.nextInt(16);

            int tunnelCount = 1;
            if (this.rand.nextInt(4) == 0) {
                this.carveCave(this.rand.nextLong(), chunkPrimer, originChunkX, originChunkZ, x, y, z);
                tunnelCount += this.rand.nextInt(4);
            }

            for (int j = 0; j < tunnelCount; ++j) {
                float tunnelC = this.rand.nextFloat() * 3.141593F * 2.0F;
                float f1 = ((this.rand.nextFloat() - 0.5F) * 2.0F) / 8F;
                float tunnelSysWidth = this.getTunnelSystemWidth(this.rand);

                this.carveTunnels(this.rand.nextLong(), chunkPrimer, originChunkX, originChunkZ, x, y, z, tunnelSysWidth, tunnelC, f1, 0, 0, this.getTunnelWHRatio());
            }
        }
    }
    
    @Override
    protected float getTunnelSystemWidth(Random random) {
        float tunnelSysWidth = super.getTunnelSystemWidth(random);
        
        if (random.nextInt(10) == 0) {
            tunnelSysWidth *= random.nextFloat() * random.nextFloat() * 3F + 1.0F;
        }
        
        return tunnelSysWidth;
    }

    private void carveCave(long seed, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double x, double y, double z) {
        this.carveTunnels(seed, chunkPrimer, chunkX, chunkZ, x, y, z, 1.0F + this.rand.nextFloat() * 6F, 0.0F, 0.0F, -1, -1, 0.5);
    }
    
    private void carveTunnels(long seed, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double x, double y, double z, float tunnelSysWidth, float tunnelC, float f1, int branch, int branchCount, double tunnelWHRatio) {
        float f2 = 0.0F;
        float f3 = 0.0F;

        Random newRandom = new Random(seed);

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

            float f5 = MathHelper.cos(f1);
            float f6 = MathHelper.sin(f1);

            x += MathHelper.cos(tunnelC) * f5;
            y += f6;
            z += MathHelper.sin(tunnelC) * f5;

            f1 *= vary ? 0.92F : 0.7F;

            f1 += f3 * 0.1F;
            tunnelC += f2 * 0.1F;

            f3 *= 0.9F;
            f2 *= 0.75F;

            f3 += (newRandom.nextFloat() - newRandom.nextFloat()) * newRandom.nextFloat() * 2.0F;
            f2 += (newRandom.nextFloat() - newRandom.nextFloat()) * newRandom.nextFloat() * 4F;
            
            if (!noStarts && branch == randBranch && tunnelSysWidth > 1.0F) {
                this.carveTunnels(newRandom.nextLong(), chunkPrimer, chunkX, chunkZ, x, y, z, newRandom.nextFloat() * 0.5F + 0.5F, tunnelC - 1.570796F, f1 / 3F, branch, branchCount, 1.0);
                this.carveTunnels(newRandom.nextLong(), chunkPrimer, chunkX, chunkZ, x, y, z, newRandom.nextFloat() * 0.5F + 0.5F, tunnelC + 1.570796F, f1 / 3F, branch, branchCount, 1.0);
                return;
            }

            if (!noStarts && newRandom.nextInt(4) == 0) {
                continue;
            }

            if (!this.canCarveBranch(chunkX, chunkZ, x, z, branch, branchCount, tunnelSysWidth)) {
                return;
            }
            
            this.carveRegion(chunkPrimer, 0, 64, chunkX, chunkZ, x, y, z, yaw, pitch); 

            if (noStarts) {
                break;
            }
        }
    }
}