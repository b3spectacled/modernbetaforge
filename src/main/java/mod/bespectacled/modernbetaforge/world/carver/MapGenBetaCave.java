package mod.bespectacled.modernbetaforge.world.carver;

import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.compat.BiomeCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public class MapGenBetaCave extends MapGenBase {
    protected final Block defaultBlock;
    protected final BlockStaticLiquid defaultFluid;
    protected final BlockDynamicLiquid defaultFlowing;
    
    protected final Set<Block> carvables;
    
    protected final float caveWidth;
    protected final int caveHeight;
    protected final int caveCount;
    protected final int caveChance;
    
    protected final Random tunnelRandom;
    
    public MapGenBetaCave(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this(chunkSource.getDefaultBlock(), chunkSource.getDefaultFluid(), settings.caveWidth, settings.caveHeight, settings.caveCount, settings.caveChance);
    }
    
    public MapGenBetaCave() {
        this(BlockStates.STONE, BlockStates.WATER, 1.0f, 128, 40, 15);
    }
    
    protected MapGenBetaCave(IBlockState defaultBlock, IBlockState defaultFluid, float caveWidth, int caveHeight, int caveCount, int caveChance) {
        super();
        
        this.defaultBlock = defaultBlock.getBlock();
        this.defaultFluid = (BlockStaticLiquid)defaultFluid.getBlock();
        this.defaultFlowing = BlockLiquid.getFlowingBlock(defaultFluid.getMaterial());
        
        this.carvables = this.initializeCarvables(defaultBlock.getBlock());
        
        this.caveWidth = caveWidth;
        this.caveHeight = caveHeight;
        this.caveCount = caveCount;
        this.caveChance = caveChance;
        
        this.tunnelRandom = new Random();
    }

    @Override
    public void generate(World world, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer) {
        this.world = world;
        this.rand.setSeed(world.getSeed());
        
        long randomLong0 = (this.rand.nextLong() / 2L) * 2L + 1L;
        long randomLong1 = (this.rand.nextLong() / 2L) * 2L + 1L;
        
        for (int chunkX = originChunkX - this.range; chunkX <= originChunkX + this.range; ++chunkX) {
            for (int chunkZ = originChunkZ - this.range; chunkZ <= originChunkZ + this.range; ++chunkZ) {
                long chunkSeed = (long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ world.getSeed();
                this.rand.setSeed(chunkSeed);
                this.tunnelRandom.setSeed(chunkSeed);
                
                this.recursiveGenerate(world, chunkX, chunkZ, originChunkX, originChunkZ, chunkPrimer);
            }
        }
    }
    
    @Override
    protected void recursiveGenerate(World world, int chunkX, int chunkZ, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer) {
        int caveCount = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(this.caveCount) + 1) + 1);
        if (this.rand.nextInt(this.caveChance) != 0) {
            caveCount = 0;
        }

        for (int i = 0; i < caveCount; ++i) {
            double x = chunkX * 16 + this.rand.nextInt(16); // Starts
            double y = this.getCaveY(this.rand);
            double z = chunkZ * 16 + this.rand.nextInt(16);

            int tunnelCount = 1;
            if (this.rand.nextInt(4) == 0) {
                this.carveCave(chunkPrimer, originChunkX, originChunkZ, x, y, z);
                tunnelCount += this.rand.nextInt(4);
            }

            for (int j = 0; j < tunnelCount; ++j) {
                float tunnelC = this.rand.nextFloat() * 3.141593F * 2.0F;
                float f1 = ((this.rand.nextFloat() - 0.5F) * 2.0F) / 8F;
                float tunnelSysWidth = this.getTunnelSystemWidth(this.rand, this.tunnelRandom);

                this.carveTunnels(chunkPrimer, originChunkX, originChunkZ, x, y, z, tunnelSysWidth, tunnelC, f1, 0, 0, this.getTunnelWHRatio());
            }
        }
    }
    
    protected boolean canCarveBranch(int mainChunkX, int mainChunkZ, double x, double z, int branch, int branchCount, float baseWidth) {
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
        if (maxY > this.caveHeight - 8) {
            maxY = this.caveHeight - 8;
        }
    
        if (minZ < 0) {
            minZ = 0;
        }
        if (maxZ > 16) {
            maxZ = 16;
        }
    
        if (this.isRegionUncarvable(chunkPrimer, chunkX, chunkZ, minX, maxX, minY, maxY, minZ, maxZ)) { 
            return false;
        }
    
        for (int localX = minX; localX < maxX; localX++) {
            double scaledLocalX = (((double) (localX + chunkX * 16) + 0.5) - x) / yaw;
    
            for (int localZ = minZ; localZ < maxZ; localZ++) {
                double scaledLocalZ = (((double) (localZ + chunkZ * 16) + 0.5) - z) / yaw;
                boolean isGrassBlock = false;
                
                for (int localY = maxY; localY >= minY; localY--) {
                    double scaledLocalY = (((double) localY - 1 + 0.5) - y) / pitch;
    
                    if (this.isPositionExcluded(scaledLocalX, scaledLocalY, scaledLocalZ)) {
                        Block block = chunkPrimer.getBlockState(localX, localY, localZ).getBlock();
    
                        if (block == Blocks.GRASS) {
                            isGrassBlock = true;
                        }
    
                        this.carveAtPoint(chunkPrimer, localX, localY, localZ, block, isGrassBlock);
                    }
                }
            }
        }
    
        return true;
    }

    protected void carveAtPoint(ChunkPrimer chunkPrimer, int localX, int localY, int localZ, Block block, boolean isGrassBlock) {
        if (this.carvables.contains(block)) {
            if (localY - 1 < 10) { // Set lava below y = 10
                chunkPrimer.setBlockState(localX, localY, localZ, Blocks.LAVA.getDefaultState());
            } else {
                chunkPrimer.setBlockState(localX, localY, localZ, Blocks.AIR.getDefaultState());
    
                // This replaces carved-out dirt with grass, if block that was removed was grass.
                if (isGrassBlock && chunkPrimer.getBlockState(localX, localY - 1, localZ).getBlock() == Blocks.DIRT) {
                    chunkPrimer.setBlockState(localX, localY - 1, localZ, Blocks.GRASS.getDefaultState());
                }
            }
        }
    }

    protected int getCaveY(Random random) {
        return random.nextInt(random.nextInt(this.caveHeight - 8) + 8);
    }

    protected float getTunnelSystemWidth(Random random, Random tunnelRandom) {
        return this.getBaseTunnelSystemWidth(random) * this.getTunnelWidthFactor(tunnelRandom);
    }
    
    protected double getTunnelWHRatio() {
        return 1.0;
    }
    
    protected final float getTunnelWidthFactor(Random random) {
        float min = Math.min(this.caveWidth, 1.0f);
        float max = Math.max(this.caveWidth, 1.0f);
        
        return min + random.nextFloat() * (max - min);
    }

    protected final float getBaseTunnelSystemWidth(Random random) {
        return random.nextFloat() * 2.0f + random.nextFloat();
    }

    private void carveCave(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double x, double y, double z) {
        this.carveTunnels(chunkPrimer, chunkX, chunkZ, x, y, z, 1.0F + this.rand.nextFloat() * 6F, 0.0F, 0.0F, -1, -1, 0.5);
    }
    
    private void carveTunnels(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, double x, double y, double z, float tunnelSysWidth, float tunnelC, float f1, int branch, int branchCount, double tunnelWHRatio) {
        float f2 = 0.0F;
        float f3 = 0.0F;

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
                this.carveTunnels(chunkPrimer, chunkX, chunkZ, x, y, z, newRandom.nextFloat() * 0.5F + 0.5F, tunnelC - 1.570796F, f1 / 3F, branch, branchCount, 1.0);
                this.carveTunnels(chunkPrimer, chunkX, chunkZ, x, y, z, newRandom.nextFloat() * 0.5F + 0.5F, tunnelC + 1.570796F, f1 / 3F, branch, branchCount, 1.0);
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

    private boolean isRegionUncarvable(ChunkPrimer chunkPrimer, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ) {
        for (int relX = relMinX; relX < relMaxX; relX++) {
            for (int relZ = relMinZ; relZ < relMaxZ; relZ++) {
                for (int relY = maxY + 1; relY >= minY - 1; relY--) {

                    if (relY < 0 || relY >= this.caveHeight) {
                        continue;
                    }

                    Block block = chunkPrimer.getBlockState(relX, relY, relZ).getBlock();

                    if (block == this.defaultFluid || block == this.defaultFlowing) {
                        return true;
                    }

                    if (relY != minY - 1 && this.isOnBoundary(relMinX, relMaxX, relMinZ, relMaxZ, relX, relZ)) {
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
    
    private Set<Block> initializeCarvables(Block defaultBlock) {
        ImmutableSet.Builder<Block> carvables = new ImmutableSet.Builder<>();
        
        // Add default blocks
        carvables.add(defaultBlock)
            .add(Blocks.GRASS)
            .add(Blocks.DIRT)
            .add(Blocks.COAL_ORE)
            .add(Blocks.IRON_ORE);
        
        // Add modded blocks
        for (Entry<String, Compat> entry : ModCompat.LOADED_MODS.entrySet()) {
            Compat compat = entry.getValue();
            if (compat instanceof BiomeCompat) {
                ModernBeta.log(Level.DEBUG, String.format("Adding carvables from mod '%s'", entry.getKey()));
                
                carvables.addAll(((BiomeCompat)compat).getCustomCarvables());
            }
        }
        
        return carvables.build();
    }
}
