package mod.bespectacled.modernbetaforge.api.world.chunk;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.chunk.data.FiniteLevelDataHandler;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceDefault;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceRules;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.spawn.IndevSpawnLocator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.storage.loot.LootTableList;

public abstract class FiniteChunkSource extends ChunkSource {
    private static final boolean DEBUG_LEVEL_DATA_HANDLER = true;
    
    private static final int MIN_WIDTH = 64;
    private static final int MAX_WIDTH = 1024;
    private static final int MIN_HEIGHT = 64;
    private static final int MAX_HEIGHT = 256;
    
    protected static final Block PLACEHOLDER_BLOCK = Blocks.ANVIL;
    protected static final int MAX_FLOODS = 640;
    
    protected final int levelWidth;
    protected final int levelLength;
    protected final int levelHeight;
    protected final int[] levelHeightmap;
    
    private final IndevHouse levelHouse;
    private final LevelDataContainer levelDataContainer;
    
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
        this.levelHeightmap = new int[this.levelWidth * this.levelLength];
        
        this.levelHouse = IndevHouse.fromId(this.settings.levelHouse);
        this.levelDataContainer = ModernBetaConfig.generatorOptions.saveIndevLevels ?
            this.tryLoadLevel() :
            new LevelDataContainer(this.levelWidth, this.levelHeight, this.levelLength);
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
        x += this.levelWidth / 2;
        z += this.levelLength / 2;
        
        if (!this.inLevelBounds(x, 0, z)) 
            return this.getBorderHeight(x, z, type);
        
        this.pregenerateLevelOrWait();
        return this.getLevelHeight(x, z, type);
    }
    
    public Block getLevelBlock(int x, int y, int z) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        y = MathHelper.clamp(y, 0, this.levelHeight - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);

        return this.levelDataContainer.levelData[(y * this.levelLength + z) * this.levelWidth + x];
    }
    
    public void setLevelBlock(int x, int y, int z, Block block) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        y = MathHelper.clamp(y, 0, this.levelHeight - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);
        
        this.levelDataContainer.levelData[(y * this.levelLength + z) * this.levelWidth + x] = block;
    }
    
    public int getLevelHeight(int x, int z, HeightmapChunk.Type type) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);
        
        Predicate<Block> testBlock = block -> {
            switch(type) {
                case SURFACE: return block == Blocks.AIR || block == this.defaultFluid.getBlock();
                case OCEAN: return block == Blocks.AIR;
                case FLOOR: return block == Blocks.AIR || block == this.defaultFluid.getBlock();
                default: return block == Blocks.AIR;
            }
        };
        
        int y;
        for (y = this.levelHeight; testBlock.test(this.getLevelBlock(x, y, z)) && y > 0; --y);
        
        return y;
    }
    
    public int getLevelWidth() {
        return this.levelWidth;
    }
    
    public int getLevelLength() {
        return this.levelLength;
    }
    
    public int getLevelHeight() {
        return this.levelHeight;
    }
    
    public void buildHouse(WorldServer world, BlockPos spawnPos, boolean isBonusChestEnabled) {
        if (this.levelHouse == IndevHouse.NONE)
            return;
        
        this.logPhase("Building");
        
        int spawnX = spawnPos.getX();
        int spawnY = spawnPos.getY() + 1;
        int spawnZ = spawnPos.getZ();
        MutableBlockPos blockPos = new MutableBlockPos();
        Random random = new Random(world.getSeed());

        Block wallBlock = this.levelHouse.wallBlock;
        Block floorBlock = this.levelHouse.floorBlock;
        
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
        
        if (isBonusChestEnabled) {
            IBlockState chestState = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
            world.setBlockState(blockPos.setPos(spawnX, spawnY - 1, spawnZ - 2),  chestState, 2);
            
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity instanceof TileEntityChest) {
                ((TileEntityChest)tileEntity).setLootTable(LootTableList.CHESTS_SPAWN_BONUS_CHEST, random.nextLong());
            }
        }
    }

    public boolean inWorldBounds(int x, int z) {
        return this.inWorldBounds(x, z, 0);
    }

    public boolean inWorldBounds(int x, int z, int padding) {
        x += this.levelWidth / 2;
        z += this.levelLength / 2;
        
        if (x >= padding && x < this.levelWidth - padding && z >= padding && z < this.levelLength - padding) {
            return true;
        }
        
        return false;
    }
    
    public boolean hasPregenerated() {
        return this.levelDataContainer.generated;
    }

    @Override
    protected boolean skipChunk(int chunkX, int chunkZ) {
        return this.skipChunk(chunkX, chunkZ, 0);
    }

    @Override
    protected boolean skipChunk(int chunkX, int chunkZ, int chunkPadding) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        return !this.inWorldBounds(startX, startZ, chunkPadding << 4);
    }
    
    @Override
    protected void pruneChunk(int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        MutableBlockPos blockPos = new MutableBlockPos();
        
        if (this.inWorldBounds(startX, startZ))
            return;
        
        for (int x = startX; x < startX + 16; ++x) {
            for (int z = startZ; z < startZ + 16; ++z) {
                int height = this.getHeight(x, z, HeightmapChunk.Type.OCEAN);
                
                for (int y = this.world.getActualHeight() - 1; y > height; --y) {
                    if (this.world.getBlockState(blockPos.setPos(x, y, z)).getBlock() != Blocks.AIR)
                        this.world.setBlockState(blockPos, BlockStates.AIR);
                }
            }
        }
    }

    protected abstract void pregenerateTerrain();
    
    protected abstract void generateBorder(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    protected abstract int getBorderHeight(int x, int z, HeightmapChunk.Type type);
    
    protected boolean inLevelBounds(int x, int y, int z) {
        return x >= 0 && x < this.levelWidth && y >= 0 && y < this.levelHeight && z >= 0 && z < this.levelLength;
    }
    
    protected boolean atLevelBounds(int x, int y, int z) {
        return x == 0 || x == this.levelWidth - 1 || y == 0 || y == this.levelHeight - 1 || z == 0 || z == this.levelLength - 1;
    }
    
    protected void pregenerateLevelOrWait() {
        if (!this.levelDataContainer.generated) {
            this.pregenerateTerrain();
            this.levelDataContainer.generated = true;
            
            if (ModernBetaConfig.generatorOptions.saveIndevLevels) {
                this.trySaveLevel();
                
                if (DEBUG_LEVEL_DATA_HANDLER) {
                    this.debugLevelDataHandler();
                }
            }
        }
    }
    
    protected void fillOblateSpheroid(float centerX, float centerY, float centerZ, float radius, Block fillBlock) {
        for (int x = (int)(centerX - radius); x <= (int)(centerX + radius); ++x) {
            for (int y = (int)(centerY - radius); y <= (int)(centerY + radius); ++y) {
                for (int z = (int)(centerZ - radius); z <= (int)(centerZ + radius); ++z) {
                
                    float dx = (float)x - centerX;
                    float dy = (float)y - centerY;
                    float dz = (float)z - centerZ;
                    
                    if ((dx * dx + dy * dy * 2.0f + dz * dz) < radius * radius && this.inLevelBounds(x, y, z)) {
                        Block block = this.getLevelBlock(x, y, z);
                        
                        if (block == this.defaultBlock.getBlock()) {
                            this.setLevelBlock(x, y, z, fillBlock);
                        }
                    }
                }
            }
        }
    }
    
    protected int flood(int x, int y, int z, Block fillBlock, Block replaceBlock) {
        return this.flood(x, y, z, fillBlock, replaceBlock, null);
    }
    
    /*
     * Not the original algorithm!
     * There may be small differences in generation compared to the original!
     * 
     * The algorithm should basically just be flooding downwards and outwards given a starting position, however,
     * what I'm fairly certain the original algorithm does when flooding along map edges:
     * 
     * Flooding can touch edges of the map (x = 0 / z = 0 / x = levelWidth - 1 / z = levelLength - 1)
     * if doing along-axis floods (does a check for filler block id 255 which is only done for random position floods)
     * or flooding underground, otherwise cancel the flood.
     * 
     * However, there seems to be a bug where the check if x = levelWidth - 1 does not actually use x,
     * so the original level generator allows water to generate where x = levelWidth - 1 above ground.
     * This bug is fixed here, because I'm not sure how to consistently reproduce it.
     * 
     */
    protected int flood(int startX, int startY, int startZ, Block fillBlock, Block replaceBlock, List<Vec3d> floodedPositions) {
        ArrayDeque<Vec3d> positions = new ArrayDeque<>();
        int flooded = 0;
        
        Vec3d startPos = new Vec3d(startX, startY, startZ);
        positions.add(startPos);
        
        int x = startX;
        int y = startY;
        int z = startZ;
        
        while (!positions.isEmpty()) {
            Vec3d pos = positions.poll();
            x = (int)pos.x;
            y = (int)pos.y;
            z = (int)pos.z;
            
            Block block = this.getLevelBlock(x, y, z);
    
            if (block == replaceBlock) {
                this.setLevelBlock(x, y, z, fillBlock);
                flooded++;
                
                if (floodedPositions != null)
                    floodedPositions.add(pos);
                
                if (floodedPositions != null && y >= this.getLevelHeight(x, z, Type.FLOOR) && this.atLevelBounds(x, y, z))
                    return -1;
                
                if (floodedPositions != null && flooded > MAX_FLOODS)
                    break;
                
                if (y - 1 >= 0)               this.tryFlood(x, y - 1, z, replaceBlock, positions);
                if (x - 1 >= 0)               this.tryFlood(x - 1, y, z, replaceBlock, positions);
                if (x + 1 < this.levelWidth)  this.tryFlood(x + 1, y, z, replaceBlock, positions);
                if (z - 1 >= 0)               this.tryFlood(x, y, z - 1, replaceBlock, positions);
                if (z + 1 < this.levelLength) this.tryFlood(x, y, z + 1, replaceBlock, positions);
            }
        }

        return flooded;
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
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        int offsetX = this.levelWidth / 2;
        int offsetZ = this.levelLength / 2;
        
        // Create and populate block sources
        BlockSourceDefault defaultSource = new BlockSourceDefault();
        BlockSourceRules blockSources = new BlockSourceRules.Builder()
            .add((x, y, z) -> defaultSource.sample(x, y, z))
            .build();
        
        for (int localX = 0; localX < 16; ++localX) {
            int x = localX + startX;
            
            for (int localZ = 0; localZ < 16; ++localZ) {
                int z = localZ + startZ;
                
                for (int y = this.levelHeight - 1; y >= 0; --y) {
                    Block block = this.getLevelBlock(x + offsetX, y, z + offsetZ);
                    defaultSource.setBlockState(block.getDefaultState());
                    
                    chunkPrimer.setBlockState(localX, y, localZ, blockSources.sample(x, y, z));
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
    
    private LevelDataContainer tryLoadLevel() {
        FiniteLevelDataHandler dataHandler = new FiniteLevelDataHandler(this.world, this);
        LevelDataContainer levelDataContainer;
        
        ModernBeta.log(Level.INFO, String.format("Attempting to read Indev file '%s'..", FiniteLevelDataHandler.FILE_NAME));
        try {
            dataHandler.readFromDisk();
            Block[] levelData = dataHandler.getLevelData();
            
            if (levelData.length != this.levelWidth * this.levelHeight * this.levelLength) {
                throw new IllegalStateException("Indev level size from file was somehow corrupted!");
            }

            levelDataContainer = new LevelDataContainer(levelData);
            
            ModernBeta.log(Level.INFO, String.format("Indev file '%s' was loaded..", FiniteLevelDataHandler.FILE_NAME));
        } catch (Exception e) {
            levelDataContainer = new LevelDataContainer(this.levelWidth, this.levelHeight, this.levelLength);
            
            ModernBeta.log(Level.WARN, String.format(
                "Indev file '%s' couldn't be loaded. Level will be generated and then saved!",
                FiniteLevelDataHandler.FILE_NAME
            ));
        }
        
        return levelDataContainer;
    }
    
    private boolean trySaveLevel() {
        FiniteLevelDataHandler dataHandler = new FiniteLevelDataHandler(this.world, this);
        boolean saved = false;
        
        try {
            dataHandler.setLevelData(this.levelDataContainer.levelData);
            dataHandler.writeToDisk();
            
            ModernBeta.log(Level.INFO, String.format("Indev file '%s' was saved..", FiniteLevelDataHandler.FILE_NAME));
            saved = true;
        } catch (Exception e) {
            ModernBeta.log(Level.ERROR, String.format("Indev file '%s' couldn't be saved!", FiniteLevelDataHandler.FILE_NAME));
            ModernBeta.log(Level.ERROR, e.getMessage());
        }
        
        return saved;
    }
    
    private void debugLevelDataHandler() {
        FiniteLevelDataHandler dataHandler = new FiniteLevelDataHandler(this.world, this);

        ModernBeta.log(Level.INFO, String.format("Attempting to read Indev file '%s'..", FiniteLevelDataHandler.FILE_NAME));
        try {
            dataHandler.readFromDisk();
            Block[] readLevelData = dataHandler.getLevelData();
            
            for (int i = 0; i < this.levelDataContainer.levelData.length; ++i) {
                Block expected = this.levelDataContainer.levelData[i];
                Block found = readLevelData[i];
                
                if (expected != found) {
                    ModernBeta.log(
                        Level.INFO,     
                        String.format(
                            "Level data did not match, expected %s, found %s at index %d!",
                            expected.getRegistryName(),
                            found.getRegistryName(),
                            i
                        )
                    );
                }
            }
            
            ModernBeta.log(Level.INFO, String.format("Indev file '%s' was debugged..", FiniteLevelDataHandler.FILE_NAME));
        } catch (Exception e) {
            ModernBeta.log(Level.WARN, String.format(
                "Indev file '%s' couldn't be loaded. Level will be generated and then saved!",
                FiniteLevelDataHandler.FILE_NAME
            ));
        }
    }
    
    private static class LevelDataContainer {
        private final Block[] levelData;
        private boolean generated;
        
        private LevelDataContainer(int levelWidth, int levelHeight, int levelLength) {
            this.levelData = new Block[levelWidth * levelHeight * levelLength];
            Arrays.fill(this.levelData, Blocks.AIR);
            
            this.generated = false;
        }
        
        private LevelDataContainer(Block[] levelData) {
            this.levelData = levelData;
            this.generated = true;
        }
    }
}
