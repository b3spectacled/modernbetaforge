package mod.bespectacled.modernbetaforge.world.carver;

import java.util.HashSet;
import java.util.List;
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
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class MapGenBetaCave extends MapGenBase {
    private static final int STRUCTURE_PADDING_XZ = 4;
    private static final int STRUCTURE_PADDING_Y = 8;
    
    protected static final int LAVA_LEVEL = 10;
    
    protected final Block defaultBlock;
    protected final Set<Block> defaultFluids;
    protected final Block defaultFill;
    
    protected final Set<Block> carvables;
    protected final Random tunnelRandom;
    protected final Random featureRandom;
    
    protected final float caveWidth;
    protected final int caveHeight;
    protected final int caveCount;
    protected final int caveChance;
    
    private List<StructureComponent> structureComponents;
    
    public MapGenBetaCave(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this(chunkSource.getDefaultBlock(), chunkSource.getDefaultFluid(), BlockStates.AIR, settings.caveWidth, settings.caveHeight, settings.caveCount, settings.caveChance);
    }
    
    public MapGenBetaCave() {
        this(BlockStates.STONE, BlockStates.WATER, BlockStates.AIR, 1.0f, 128, 40, 15);
    }
    
    protected MapGenBetaCave(IBlockState defaultBlock, IBlockState defaultFluid, IBlockState defaultFill, float caveWidth, int caveHeight, int caveCount, int caveChance) {
        super();
        
        this.defaultBlock = defaultBlock.getBlock();
        this.defaultFluids = getDefaultFluids(defaultFluid);
        this.defaultFill = defaultFill.getBlock();
        
        this.carvables = this.initializeCarvables(defaultBlock.getBlock()).build();
        
        this.caveWidth = caveWidth;
        this.caveHeight = caveHeight;
        this.caveCount = caveCount;
        this.caveChance = caveChance;
        
        this.tunnelRandom = new Random();
        this.featureRandom = new Random();
    }
    
    public void generate(World world, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer, Biome[] biomes, List<StructureComponent> structureComponents) {
        this.structureComponents = structureComponents;
        
        this.generate(world, originChunkX, originChunkZ, chunkPrimer);
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
                this.setRandoms(chunkSeed);
                
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
            if (localY - 1 < LAVA_LEVEL) { // Set lava below y = 10
                chunkPrimer.setBlockState(localX, localY, localZ, Blocks.LAVA.getDefaultState());
            } else {
                chunkPrimer.setBlockState(localX, localY, localZ, this.defaultFill.getDefaultState());
    
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
        return this.getBaseTunnelSystemWidth(random) * this.getTunnelWidthMultiplier(tunnelRandom);
    }
    
    protected double getTunnelWHRatio() {
        return 1.0;
    }
    
    protected final float getTunnelWidthMultiplier(Random random) {
        return MathUtil.getRandomFloatInRange(ModernBetaGeneratorSettings.MIN_CAVE_WIDTH, this.caveWidth, random);
    }

    protected final float getBaseTunnelSystemWidth(Random random) {
        return random.nextFloat() * 2.0f + random.nextFloat();
    }
    
    protected final void setRandoms(long chunkSeed) {
        this.rand.setSeed(chunkSeed);
        this.tunnelRandom.setSeed(chunkSeed);
        this.featureRandom.setSeed(chunkSeed);
    }

    protected ImmutableSet.Builder<Block> initializeCarvables(Block defaultBlock) {
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
        
        return carvables;
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

    private boolean isRegionUncarvable(ChunkPrimer chunkPrimer, int mainChunkX, int mainChunkZ, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        int startX = mainChunkX << 4;
        int startZ = mainChunkZ << 4;
        
        MutableBlockPos mutablePos = new MutableBlockPos();
        IChunkGenerator chunkGenerator = ((WorldServer)this.world).getChunkProvider().chunkGenerator;
        boolean isModernBetaChunkGenerator = chunkGenerator instanceof ModernBetaChunkGenerator;
        
        for (int localX = minX; localX < maxX; localX++) {
            int x = startX + localX;
            
            for (int localZ = minZ; localZ < maxZ; localZ++) {
                int z = startZ + localZ;
                int height = 255;
                
                if (isModernBetaChunkGenerator) {
                    height = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource().getHeight(x, z, HeightmapChunk.Type.STRUCTURE);
                }
                
                for (int y = maxY + 1; y >= minY - 1; y--) {
                    mutablePos.setPos(x, y, z);
                    
                    if (y < 0 || y >= this.caveHeight) {
                        continue;
                    }
                    
                    if (this.defaultFluids.contains(chunkPrimer.getBlockState(localX, y, localZ).getBlock())) {
                        return true;
                    }
                    
                    if (this.structureComponents != null && !this.structureComponents.isEmpty() && isModernBetaChunkGenerator) {
                        for (StructureComponent component : this.structureComponents) {
                            StructureBoundingBox box = component.getBoundingBox();
                            
                            int boxMinX = box.minX - STRUCTURE_PADDING_XZ;
                            int boxMaxX = box.maxX + STRUCTURE_PADDING_XZ;
                            int boxMinZ = box.minZ - STRUCTURE_PADDING_XZ;
                            int boxMaxZ = box.maxZ + STRUCTURE_PADDING_XZ;
                            int minHeight = height - STRUCTURE_PADDING_Y;
                            
                            if (x >= boxMinX && x <= boxMaxX && z >= boxMinZ && z <= boxMaxZ && y >= minHeight) {
                                return true;
                            }
                        }
                    }

                    if (y != minY - 1 && this.isOnBoundary(minX, maxX, minZ, maxZ, localX, localZ)) {
                        y = minY;
                    }
                }

            }
        }

        return false;
    }

    private boolean isPositionExcluded(double scaledX, double scaledY, double scaledZ) {
        return scaledY > -0.7 && scaledX * scaledX + scaledY * scaledY + scaledZ * scaledZ < 1.0;
    }

    private boolean isOnBoundary(int minX, int maxX, int minZ, int maxZ, int localX, int localZ) {
        return localX != minX && localX != maxX - 1 && localZ != minZ && localZ != maxZ - 1;
    }
    
    public static Set<Block> getDefaultFluids(IBlockState defaultFluid) {
        Set<Block> defaultFluids = new HashSet<>();
        
        defaultFluids.add(defaultFluid.getBlock());
        try {
            defaultFluids.add(BlockLiquid.getFlowingBlock(defaultFluid.getMaterial()));
        } catch (Exception e) {
            ModernBeta.log(Level.DEBUG, "Cave carver fluid is not flowable!");
        }
        
        return defaultFluids;
    }
}
