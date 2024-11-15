package mod.bespectacled.modernbetaforge.api.world.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.spawn.IndevSpawnLocator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class FiniteChunkSource extends ChunkSource {
    private static final int MIN_WIDTH = 64;
    private static final int MAX_WIDTH = 1024;
    private static final int MIN_HEIGHT = 64;
    private static final int MAX_HEIGHT = 256;
    
    protected final int levelWidth;
    protected final int levelLength;
    protected final int levelHeight;
    protected final float levelCaveRadius;
    protected final int[] levelHeightmap;
    
    private final Block[][][] levelArr;
    
    private boolean pregenerated;
    
    public FiniteChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings chunkGeneratorSettings,
        ModernBetaNoiseSettings noiseSettings,
        long seed,
        boolean mapFeaturesEnabled
    ) {
         super(world, chunkGenerator, chunkGeneratorSettings, noiseSettings, seed, mapFeaturesEnabled);
        
        this.levelWidth = MathHelper.clamp(settings.levelWidth, MIN_WIDTH, MAX_WIDTH);
        this.levelLength = MathHelper.clamp(settings.levelLength, MIN_WIDTH, MAX_WIDTH);
        this.levelHeight = MathHelper.clamp(settings.levelHeight, MIN_HEIGHT, MAX_HEIGHT);
        this.levelCaveRadius = 1.0f;
        this.levelHeightmap = new int[this.levelWidth * this.levelLength];
        
        this.levelArr = new Block[this.levelWidth][this.levelHeight][this.levelLength];
        for (int x = 0; x < this.levelWidth; ++x) {
            for (int z = 0; z < this.levelLength; ++z) {
                for (int y = 0; y < this.levelHeight; ++y) {
                    this.setLevelBlock(x, y, z, Blocks.AIR);
                }
            }
        }
        
        this.pregenerated = false;
    }
    
    @Override
    public SpawnLocator getSpawnLocator() {
        return new IndevSpawnLocator();
    }

    @Override
    public void provideBaseChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        if (this.inWorldBounds(startX, startZ)) {
            this.pregenerateLevelOrWait();
            this.generateTerrain(chunkPrimer, chunkX, chunkZ);
        } else {
            this.generateBorder(chunkPrimer, chunkX, chunkZ);
        }
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) { }
    
    @Override
    public int getHeight(int x, int z, HeightmapChunk.Type type) {
        int seaLevel = this.getSeaLevel();
        
        x += this.levelWidth / 2;
        z += this.levelLength / 2;
        
        if (x < 0 || x >= this.levelWidth || z < 0 || z >= this.levelLength) 
            return seaLevel;
        
        return this.getLevelHighestBlock(x, z, type) - 1;
    }
    
    public Block getLevelBlock(int x, int y, int z) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        y = MathHelper.clamp(y, 0, this.levelHeight - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);
        
        return this.levelArr[x][y][z];
    }
    
    public void setLevelBlock(int x, int y, int z, Block block) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        y = MathHelper.clamp(y, 0, this.levelHeight - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);
        
        this.levelArr[x][y][z] = block;
    }
    
    public int getLevelHighestBlock(int x, int z, HeightmapChunk.Type type) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);
        
        Predicate<Block> testBlock = block -> {
            switch(type) {
                case SURFACE: return block == Blocks.AIR || block == this.defaultFluid.getBlock();
                case OCEAN: return block == Blocks.AIR;
                default: return block == Blocks.AIR;
            }
        };

        int y = this.levelHeight - 1;
        while (testBlock.test(this.getLevelBlock(x, y, z)) && y > 0) {
            --y;
        }
        
        return y;
    }
    
    public int getLevelWidth() {
        return this.levelWidth;
    }
    
    public int getLevelLength() {
        return this.levelLength;
    }
    
    public void buildHouse(WorldServer world, BlockPos spawnPos) {
        if (!this.settings.useIndevHouse)
            return;
        
        this.logPhase("Building");
        
        int spawnX = spawnPos.getX();
        int spawnY = spawnPos.getY() + 1;
        int spawnZ = spawnPos.getZ();
        MutableBlockPos blockPos = new MutableBlockPos();
        
        Block floorBlock = Blocks.STONE;
        Block wallBlock = Blocks.PLANKS;
        
        for (int x = spawnX - 3; x <= spawnX + 3; ++x) {
            for (int y = spawnY - 2; y <= spawnY + 2; ++y) {
                for (int z = spawnZ - 3; z <= spawnZ + 3; ++z) {
                    Block block = (y < spawnY - 1) ? Blocks.OBSIDIAN : Blocks.AIR;
                    
                    if (x == spawnX - 3 || z == spawnZ - 3 || x == spawnX + 3 || z == spawnZ + 3 || y == spawnY - 2 || y == spawnY + 2) {
                        block = floorBlock;
                        if (y >= spawnY - 1) {
                            block = wallBlock;
                        }
                    }
                    
                    if (z == spawnZ + 3 && x == spawnX && y >= spawnY - 1 && y <= spawnY) {
                        block = Blocks.AIR;
                    }
                    
                    world.setBlockState(blockPos.setPos(x, y, z), block.getDefaultState());
                }
            }
        }
        
        world.setBlockState(blockPos.setPos(spawnX - 3 + 1, spawnY, spawnZ), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
        world.setBlockState(blockPos.setPos(spawnX + 3 - 1, spawnY, spawnZ), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
    }

    @Override
    protected boolean skipChunk(int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        return !this.inWorldBounds(startX, startZ);
    }

    protected abstract void pregenerateTerrain();
    
    protected abstract void generateBorder(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);

    protected boolean inWorldBounds(int x, int z) {
        int halfWidth = this.levelWidth / 2;
        int halfLength = this.levelLength / 2;
        
        if (x >= -halfWidth && x < halfWidth && z >= -halfLength && z < halfLength) {
            return true;
        }
        
        return false;
    }

    protected boolean inLevelBounds(int x, int y, int z) {
        if (x < 0 || x >= this.levelWidth || y < 0 || y >= this.levelHeight || z < 0 || z >= this.levelLength) {
            return false;
        }
            
        return true;
    }
    
    protected List<Vec3d> flood(BlockPos blockPos, Block fillBlock, Block replaceBlock) {
        return this.flood(blockPos.getX(),  blockPos.getY(), blockPos.getZ(), fillBlock, replaceBlock);
    }
    
    protected List<Vec3d> flood(int x, int y, int z, Block fillBlock, Block replaceBlock) {
        ArrayDeque<Vec3d> positions = new ArrayDeque<>();
        ArrayList<Vec3d> floodedPositions = new ArrayList<>();
        
        Vec3d startPos = new Vec3d(x, y, z);
        positions.add(startPos);
        
        while (!positions.isEmpty()) {
            Vec3d pos = positions.poll();
            x = (int)pos.x;
            y = (int)pos.y;
            z = (int)pos.z;
            
            Block block = this.getLevelBlock(x, y, z);
    
            if (block == replaceBlock) {
                this.setLevelBlock(x, y, z, fillBlock);
                floodedPositions.add(pos);
                
                if (y - 1 >= 0)               this.tryFlood(x, y - 1, z, replaceBlock, positions);
                if (x - 1 >= 0)               this.tryFlood(x - 1, y, z, replaceBlock, positions);
                if (x + 1 < this.levelWidth)  this.tryFlood(x + 1, y, z, replaceBlock, positions);
                if (z - 1 >= 0)               this.tryFlood(x, y, z - 1, replaceBlock, positions);
                if (z + 1 < this.levelLength) this.tryFlood(x, y, z + 1, replaceBlock, positions);
            }
        }
        
        return floodedPositions;
    }
    
    protected void fillOblateSpheroid(float centerX, float centerY, float centerZ, float radius, Block fillBlock) {
        for (int x = (int)(centerX - radius); x < (int)(centerX + radius); ++x) {
            for (int y = (int)(centerY - radius); y < (int)(centerY + radius); ++y) {
                for (int z = (int)(centerZ - radius); z < (int)(centerZ + radius); ++z) {
                
                    float dx = x - centerX;
                    float dy = y - centerY;
                    float dz = z - centerZ;
                    
                    if ((dx * dx + dy * dy * 2.0f + dz * dz) < radius * radius && inLevelBounds(x, y, z)) {
                        Block block = this.getLevelBlock(x, y, z);
                        
                        if (block == this.defaultBlock.getBlock()) {
                            this.setLevelBlock(x, y, z, fillBlock);
                        }
                    }
                }
            }
        }
    }

    protected void pregenerateLevelOrWait() {
        if (!this.pregenerated) {
            this.pregenerateTerrain();
            this.pregenerated = true;
        }
    }

    protected void logPhase(String phase) {
        ModernBeta.log(Level.INFO, phase + "..");
    }
    
    protected BiomeInjectionRules buildBiomeInjectorRules() {
        boolean replaceOceans = this.getChunkGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getChunkGeneratorSettings().replaceBeachBiomes;
        
        BiomeInjectionRules.Builder builder = new BiomeInjectionRules.Builder();
        
        Predicate<BiomeInjectionContext> deepOceanPredicate = context -> 
            this.atOceanDepth(context.pos.getY(), DEEP_OCEAN_MIN_DEPTH) && this.isFluidBlock(context.stateAbove);
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> 
            this.atOceanDepth(context.pos.getY(), OCEAN_MIN_DEPTH) && this.isFluidBlock(context.stateAbove);
            
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            this.atBeachDepth(context.pos.getY()) && this.isBeachBlock(context.state);
            
        if (replaceBeaches && this.biomeProvider.getBiomeSource() instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)this.biomeProvider.getBiomeSource();
            
            builder.add(beachPredicate, biomeResolverBeach::getBeachBiome, BiomeInjectionRules.BEACH);
        }
        
        if (replaceOceans && this.biomeProvider.getBiomeSource() instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)this.biomeProvider.getBiomeSource();

            builder.add(deepOceanPredicate, biomeResolverOcean::getDeepOceanBiome, BiomeInjectionRules.DEEP_OCEAN);
            builder.add(oceanPredicate, biomeResolverOcean::getOceanBiome, BiomeInjectionRules.OCEAN);
        }
        
        return builder.build();
    }
    
    private void generateTerrain(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int offsetX = (chunkX + this.levelWidth / 16 / 2) * 16;
        int offsetZ = (chunkZ + this.levelLength / 16 / 2) * 16;
        
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                for (int y = this.levelHeight - 1; y >= 0; --y) {
                    Block block = this.getLevelBlock(offsetX + localX, y, offsetZ + localZ);
                    chunkPrimer.setBlockState(localX, y, localZ, block.getDefaultState());
                }
            }
        }
    }
    
    private void tryFlood(int x, int y, int z, Block replaceBlock, ArrayDeque<Vec3d> positions) {
        Block block = this.getLevelBlock(x, y, z);
        
        if (block == replaceBlock) {
            positions.add(new Vec3d(x, y, z));
        }
    }
}
