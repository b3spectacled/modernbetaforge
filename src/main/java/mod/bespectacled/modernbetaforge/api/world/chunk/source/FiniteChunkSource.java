package mod.bespectacled.modernbetaforge.api.world.chunk.source;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.data.FiniteDataHandler;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceDefault;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceRules;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.spawn.IndevSpawnLocator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class FiniteChunkSource extends ChunkSource {
    private static final boolean DEBUG_LEVEL_DATA_HANDLER = false;
    
    private static final int MIN_WIDTH = 64;
    private static final int MAX_WIDTH = 2560;
    private static final int MIN_HEIGHT = 64;
    private static final int MAX_HEIGHT = 256;
    
    protected static final Block PLACEHOLDER_BLOCK = Blocks.ANVIL;
    protected static final int MAX_FLOODS = 640;
    
    protected final int levelWidth;
    protected final int levelLength;
    protected final int levelHeight;
    protected final int[] levelHeightmap;
    protected final BiomeSource biomeSource;
    
    private final IndevHouse levelHouse;

    private Consumer<String> levelNotifier;
    private LevelDataContainer levelDataContainer;
    
    private String phase;
    @SuppressWarnings("unused")
    private float phaseProgress;
    
    /**
     * Constructs an abstract FiniteChunkSource with necessary level information.
     * 
     * @param seed The world seed.
     * @param settings The generator settings.
     */
    public FiniteChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.levelWidth = MathHelper.clamp(settings.levelWidth >> 4 << 4, MIN_WIDTH, MAX_WIDTH);
        this.levelLength = MathHelper.clamp(settings.levelLength >> 4 << 4, MIN_WIDTH, MAX_WIDTH);
        this.levelHeight = MathHelper.clamp(settings.levelHeight, MIN_HEIGHT, MAX_HEIGHT);
        this.levelHeightmap = new int[this.levelWidth * this.levelLength];
        this.biomeSource = ModernBetaRegistries.BIOME_SOURCE
            .get(new ResourceLocation(settings.biomeSource))
            .apply(seed, settings);
        
        this.levelHouse = IndevHouse.fromId(this.settings.levelHouse);
    }
    
    /**
     * Inherited from {@link ChunkSource#getSpawnLocator() getSpawnLocator}.
     * Uses {@link IndevSpawnLocator} by default.
     */
    @Override
    public SpawnLocator getSpawnLocator() {
        return new IndevSpawnLocator();
    }
    
    @Override
    public void provideInitialChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        if (this.inWorldBounds(startX, startZ)) {
            this.pregenerateLevelOrWait(this.levelDataContainer);
            this.generateTerrain(chunkPrimer, chunkX, chunkZ, this.biomeSource);
            
        } else {
            this.generateBorder(chunkPrimer, chunkX, chunkZ);
            
        }
    }
    
    @Override
    public void provideProcessedChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, List<StructureComponent> structureComponents) { }

    @Override
    public void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) { }
    
    /**
     * Inherited from {@link ChunkSource#getHeight(int, int, mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type) getHeight}.
     * Samples height from the generated level data.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type HeightmapChunk heightmap type.
     * @return The y-coordinate of top block at x/z.
     * 
     */
    @Override
    public int getHeight(int x, int z, HeightmapChunk.Type type) {
        x += this.levelWidth / 2;
        z += this.levelLength / 2;
        
        if (!this.inLevelBounds(x, 0, z)) {
            return this.getBorderHeight(x, z, type);
        }

        synchronized(this) {
            this.levelDataContainer = this.levelDataContainer == null || !this.levelDataContainer.generated ?
                new LevelDataContainer(this.levelWidth, this.levelHeight, this.levelLength) :
                this.levelDataContainer;
            
            this.pregenerateLevelOrWait(this.levelDataContainer);
        }
        
        return this.getLevelHeight(x, z, type);
    }
    
    /**
     * Gets a block given coordinates from level data.
     * 
     * @param x x-coordinate in block coordinates.
     * @param y y-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @return The block at the given coordinates.
     */
    public Block getLevelBlock(int x, int y, int z) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        y = MathHelper.clamp(y, 0, this.levelHeight - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);

        return this.levelDataContainer.getLevelBlock(x, y, z, this.levelWidth, this.levelLength);
    }
    
    /**
     * Sets a block into level data at the given coordinates.
     * 
     * @param x x-coordinate in block coordinates.
     * @param y y-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param block The block to set in level data.
     */
    public void setLevelBlock(int x, int y, int z, Block block) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        y = MathHelper.clamp(y, 0, this.levelHeight - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);

        this.levelDataContainer.setLevelBlock(x, y, z, this.levelWidth, this.levelLength, block);
    }
    
    /**
     * Gets the height from level data given x/z coordinates and the heightmap type.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type {@link HeightmapChunk.Type}.
     * @return The y-coordinate of top block at x/z.
     */
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
    
    /**
     * Gets the finite level width.
     * 
     * @return The level width.
     */
    public int getLevelWidth() {
        return this.levelWidth;
    }
    
    /**
     * Gets the finite level length.
     * 
     * @return The level length.
     */
    public int getLevelLength() {
        return this.levelLength;
    }
    
    /**
     * Gets the finite level height.
     * 
     * @return The level height.
     */
    public int getLevelHeight() {
        return this.levelHeight;
    }
    
    /**
     * Sets the level notifier consumer.
     * 
     * @param levelNotifier The level notifier consuming a String.
     */
    public void setLevelNotifier(Consumer<String> levelNotifier) {
        this.levelNotifier = levelNotifier;
    }
    
    /**
     * Loads from file or creates new level data container.
     * 
     * @param world The world object
     */
    public void loadOrCreateLevelDataContainer(World world) {
        if (this.levelDataContainer == null) {
            this.levelDataContainer = ModernBetaConfig.generatorOptions.saveIndevLevels ?
                this.tryLoadLevel(world) :
                new LevelDataContainer(this.levelWidth, this.levelHeight, this.levelLength);
        }
    }
    
    /**
     * Saves the level data container to file.
     * 
     * @param world The world object
     */
    public void saveLevelDataContainer(World world) {
        if (ModernBetaConfig.generatorOptions.saveIndevLevels) {
            this.trySaveLevel(world, this.levelDataContainer);
            
            if (DEBUG_LEVEL_DATA_HANDLER) {
                this.debugLevelDataHandler(world, this.levelDataContainer);
            }
        }
    }
    
    /**
     * Pregenerates the level terrain.
     */
    public void pregenerateTerrainOrWait() {
        this.pregenerateLevelOrWait(this.levelDataContainer);
    }

    /**
     * Generates the Indev starting house at the given coordinates.
     * 
     * @param world The world object.
     * @param spawnPos The player spawn block position.
     * @param isBonusChestEnabled Whether the bonus chest should be generated in the house.
     */
    public void buildHouse(WorldServer world, BlockPos spawnPos, boolean isBonusChestEnabled) {
        if (this.levelHouse == IndevHouse.NONE)
            return;
        
        this.setPhase("Building");
        
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

    /**
     * Indicates whether the given x/z world coordinates are within the level area.
     * 
     * @param x x-coordinate in block coordinates in world space.
     * @param z z-coordinate in block coordinates in world space.
     * @return Whether the given x/z coordinates are within the level area.
     */
    public boolean inWorldBounds(int x, int z) {
        x += this.levelWidth / 2;
        z += this.levelLength / 2;
        
        if (x >= 0 && x < this.levelWidth && z >= 0 && z < this.levelLength) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Indicates whether the level data has been generated.
     * 
     * @return Whether the level is generated and data can be fetched from the level data.
     */
    public boolean hasPregenerated() {
        if (this.levelDataContainer != null) {
            return this.levelDataContainer.generated;
        }
        
        return false;
    }

    /**
     * Indicate whether the chunk at the given coordinates should be skipped.
     * The chunk is skipped if the chunk coordinates being tested are outside of level bounds.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return Whether the chunk should be skipped.
     */
    @Override
    public boolean skipChunk(int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        return !this.inWorldBounds(startX, startZ);
    }
    
    /**
     * Prunes the chuck at the given coordinates.
     * The chunk is pruned if the chunk coordinates being tested are outside of level bounds.
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    @Override
    public void pruneChunk(World world, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        MutableBlockPos blockPos = new MutableBlockPos();
        
        if (this.inWorldBounds(startX, startZ))
            return;
        
        for (int x = startX; x < startX + 16; ++x) {
            for (int z = startZ; z < startZ + 16; ++z) {
                int height = this.getHeight(x, z, HeightmapChunk.Type.OCEAN);
                
                for (int y = world.getActualHeight() - 1; y > height; --y) {
                    if (world.getBlockState(blockPos.setPos(x, y, z)).getBlock() != Blocks.AIR)
                        world.setBlockState(blockPos, BlockStates.AIR);
                }
            }
        }
    }
    
    @Override
    public BiomeInjectionRules buildBiomeInjectorRules(BiomeSource biomeSource) {
        boolean replaceOceans = this.getGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getGeneratorSettings().replaceBeachBiomes;
        
        BiomeInjectionRules.Builder builder = new BiomeInjectionRules.Builder();
        
        Predicate<BiomeInjectionContext> deepOceanPredicate = context -> 
            this.atOceanDepth(context.pos.getY(), DEEP_OCEAN_MIN_DEPTH) && this.isFluidBlock(context.stateAbove);
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> 
            this.atOceanDepth(context.pos.getY(), OCEAN_MIN_DEPTH) && this.isFluidBlock(context.stateAbove);
            
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            this.atBeachDepth(context.pos.getY()) && this.isBeachBlock(context.state);
            
        if (replaceBeaches && biomeSource instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)biomeSource;
            
            builder.add(beachPredicate, biomeResolverBeach::getBeachBiome, BiomeInjectionStep.POST_SURFACE);
        }
        
        if (replaceOceans && biomeSource instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)biomeSource;
    
            builder.add(deepOceanPredicate, biomeResolverOcean::getDeepOceanBiome, BiomeInjectionStep.PRE_SURFACE);
            builder.add(oceanPredicate, biomeResolverOcean::getOceanBiome, BiomeInjectionStep.PRE_SURFACE);
        }
        
        return builder.build();
    }
    
    /**
     * Generates the level data.
     */
    protected abstract void pregenerateTerrain();
    
    /**
     * Generates the world chunks outside of the level bounds.
     *
     * @param chunkPrimer The chunk primer.
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    protected abstract void generateBorder(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    /**
     * Sample height at given x/z coordinate for coordinates outside of the level bounds.
     *
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type {@link HeightmapChunk.Type}.
     * @return The y-coordinate of top block at x/z outside of the level bounds.
     */
    protected abstract int getBorderHeight(int x, int z, HeightmapChunk.Type type);
    
    /**
     * Checks if the level data has generated yet, if not, then generates the level data.
     * If `saveIndevLevels` has been enabled, then the level will be saved to disk after generation.
     * 
     * @param levelDataContainer The level data container.
     */
    protected synchronized void pregenerateLevelOrWait(LevelDataContainer levelDataContainer) {
        if (!levelDataContainer.generated) {
            this.pregenerateTerrain();
            levelDataContainer.generated = true;
        }
    }

    /**
     * Indicates whether the given level coordinates are within the level area.
     * 
     * @param x x-coordinate in block coordinates in level space.
     * @param y y-coordinate in block coordinates in level space.
     * @param z z-coordinate in block coordinates in level space.
     * @return Whether the given x/z coordinates are within the level area.
     */
    protected boolean inLevelBounds(int x, int y, int z) {
        return x >= 0 && x < this.levelWidth && y >= 0 && y < this.levelHeight && z >= 0 && z < this.levelLength;
    }

    /**
     * Indicates whether the given level coordinates are at the level bounds.
     * 
     * @param x x-coordinate in block coordinates in level space.
     * @param y y-coordinate in block coordinates in level space.
     * @param z z-coordinate in block coordinates in level space.
     * @return Whether the given x/z coordinates are at the level bounds.
     */
    protected boolean atLevelBounds(int x, int y, int z) {
        return x == 0 || x == this.levelWidth - 1 || y == 0 || y == this.levelHeight - 1 || z == 0 || z == this.levelLength - 1;
    }
    
    /**
     * Fills a space in level data in a spheroid shape. Used to carve caves.
     * 
     * @param centerX x-coordinate in block coordinates in level space.
     * @param centerY y-coordinate in block coordinates in level space.
     * @param centerZ z-coordinate in block coordinates in level space.
     * @param radius Radius of spheroid shape.
     * @param fillBlock Block to fill into space.
     */
    protected void fillOblateSpheroid(float centerX, float centerY, float centerZ, float radius, Block fillBlock) {
        for (int x = (int)(centerX - radius); x <= (int)(centerX + radius); ++x) {
            for (int y = (int)(centerY - radius); y <= (int)(centerY + radius); ++y) {
                for (int z = (int)(centerZ - radius); z <= (int)(centerZ + radius); ++z) {
                
                    float dx = (float)x - centerX;
                    float dy = (float)y - centerY;
                    float dz = (float)z - centerZ;
                    
                    if ((dx * dx + dy * dy * 2.0f + dz * dz) < radius * radius && this.inCaveBounds(x, y, z)) {
                        Block block = this.getLevelBlock(x, y, z);
                        
                        if (block == this.defaultBlock.getBlock()) {
                            this.setLevelBlock(x, y, z, fillBlock);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Floods the level downward and outward from a given starting position and fill/replace blocks.
     * Does not provide the array of flooded positions to keep track, thus it will attempt to flood as many blocks as possible.
     * 
     * @param x x-coordinate in block coordinates in level space.
     * @param y y-coordinate in block coordinates in level space.
     * @param z z-coordinate in block coordinates in level space.
     * @param fillBlock The block to fill the space with.
     * @param replaceBlock The block to replace.
     * @return Number of flooded blocks if not exceeding {@link #MAX_FLOODS} or if floodedPositions is null. -1 otherwise.
     */
    protected int flood(int x, int y, int z, Block fillBlock, Block replaceBlock) {
        return this.flood(x, y, z, fillBlock, replaceBlock, null);
    }
    
    /**
     * Floods the level downward and outward from a given starting position and fill/replace blocks.
     * If `floodedPositions` is provided, then the method will only fill up to {@link #MAX_FLOODS} positions and then return.
     * The intent is to use a test block as the `fillBlock` (see {@link #PLACEHOLDER_BLOCK}), then fill with the actual fill block if the number of floods is below {@link #MAX_FLOODS}.
     * Otherwise you should refill with the original block using the array positions.
     * 
     * More comments below:
     * 
     * Not the original algorithm, but did extensive testing/reverse engineering against a modded instance of Indev,
     * so this is accurate except in one case -- doing edge floods for Inland worlds; this shouldn't seem possible given
     * that the edges should never be exposed, but for some reason some underground pockets along the level edge still
     * flood.
     * 
     * The algorithm should basically just be flooding downwards and outwards given a starting position, however,
     * what I'm fairly certain the original algorithm does when flooding along map edges:
     * 
     * Flooding can touch edges of the map (x = 0 / z = 0 / x = levelWidth - 1 / z = levelLength - 1 / y = 0 / y = levelHeight - 1)
     * if doing edge floods (does a check for filler block id 255 which is only done for random position floods),
     * otherwise cancel the flood.
     * 
     * However, there seems to be a bug where the check if x = levelWidth - 1 does not actually use x,
     * so the original level generator allows water to generate where x = levelWidth - 1 above ground.
     * This bug is fixed here, because I'm not sure how to consistently reproduce it.
     * 
     * @param startX x-coordinate in block coordinates in level space.
     * @param startY y-coordinate in block coordinates in level space.
     * @param startZ z-coordinate in block coordinates in level space.
     * @param fillBlock The block to fill the space with.
     * @param replaceBlock The block to replace.
     * @param floodedPositions The array of Vec3d positions that have been filled. This is used to track positions to quickly fill with another block type.
     * @return Number of flooded blocks if not exceeding {@link #MAX_FLOODS} or if floodedPositions is null. -1 otherwise.
     */
    protected int flood(int startX, int startY, int startZ, Block fillBlock, Block replaceBlock, Vec3d[] floodedPositions) {
        Deque<Vec3d> positions = new ArrayDeque<>();
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
                
                if (floodedPositions != null)
                    floodedPositions[flooded] = pos;
                
                flooded++;
                
                if (floodedPositions != null && this.atLevelBounds(x, y, z))
                    return -1;
                
                if (floodedPositions != null && flooded >= MAX_FLOODS)
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
    
    /**
     * Sets the current generation phase for the level. This is printed to the console and the level loading screen.
     * 
     * @param phase Generation phase.
     */
    protected void setPhase(String phase) {
        this.phase = phase;
        
        if (this.levelNotifier != null) {
            this.levelNotifier.accept(phase);
        }
        
        ModernBeta.log(Level.INFO, this.phase + "..");
    }
    
    /**
     * Sets the current generation phase progress for the level. Currently unused.
     * 
     * @param phaseProgress The decimal phase progress. (i.e. 50% equals 0.5)
     */
    protected void setPhaseProgress(float phaseProgress) {
        this.phaseProgress = phaseProgress;
    }
    
    /**
     * Takes the level data and sets it into the chunk primer for the actual terrain generation.
     * 
     * @param chunkPrimer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @param biomeSource The biome source.
     */
    private void generateTerrain(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, BiomeSource biomeSource) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        int offsetX = this.levelWidth / 2;
        int offsetZ = this.levelLength / 2;
        
        // Create and populate block sources
        BlockSourceDefault defaultSource = new BlockSourceDefault(this.defaultBlock);
        BlockSourceRules blockSources = new BlockSourceRules.Builder(this.defaultBlock)
            .add(defaultSource)
            .add(this.blockSources)
            .build();
        
        for (int localX = 0; localX < 16; ++localX) {
            int x = localX + startX;
            
            for (int localZ = 0; localZ < 16; ++localZ) {
                int z = localZ + startZ;
                Biome biome = biomeSource.getBiome(x, z);
                
                for (int y = this.levelHeight - 1; y >= 0; --y) {
                    Block block = this.getLevelBlock(x + offsetX, y, z + offsetZ);
                    IBlockState blockState = block.getDefaultState();
                    
                    // Replace grass/dirt blocks with biome top/filler blocks
                    if (block == Blocks.GRASS) {
                        blockState = biome.topBlock;
                    } else if (block == Blocks.DIRT) {
                        blockState = biome.fillerBlock;
                    }
                    
                    defaultSource.setBlockState(blockState);
                    chunkPrimer.setBlockState(localX, y, localZ, blockSources.sample(x, y, z));
                }
            }
        }
    }
    
    /**
     * Indicates whether the given level coordinates are within the cave carving bounds.
     * 
     * @param x x-coordinate in block coordinates in level space.
     * @param y y-coordinate in block coordinates in level space.
     * @param z z-coordinate in block coordinates in level space.
     * @return Whether the given x/z coordinates are within the cave carving bounds.
     */
    private boolean inCaveBounds(int x, int y, int z) {
        return x > 0 && x < this.levelWidth - 1 && y > 0 && y < this.levelHeight - 1 && z > 0 && z < this.levelLength - 1;
    }
    
    /**
     * Tries adding the level coordinates to the positions queue for flooding.
     * 
     * @param x x-coordinate in block coordinates in level space.
     * @param y y-coordinate in block coordinates in level space.
     * @param z z-coordinate in block coordinates in level space.
     * @param replaceBlock The block to fill.
     * @param positions Deque containing positions to flood.
     */
    private void tryFlood(int x, int y, int z, Block replaceBlock, Deque<Vec3d> positions) {
        Block block = this.getLevelBlock(x, y, z);
        
        if (block == replaceBlock) {
            positions.add(new Vec3d(x, y, z));
        }
    }
    
    /**
     * Attempts to load a saved finite level from disk. If successful, then level data will be populated and returned for world generation.
     * 
     * @param world The world object.
     * @return LevelDataContainer containing level data and level block map.
     */
    private LevelDataContainer tryLoadLevel(World world) {
        FiniteDataHandler dataHandler = new FiniteDataHandler(world, this);
        LevelDataContainer levelDataContainer;
        
        ModernBeta.log(Level.INFO, String.format("Attempting to read level file '%s'..", FiniteDataHandler.FILE_NAME));
        try {
            dataHandler.readFromDisk();
            levelDataContainer = dataHandler.getLevelData(this.levelWidth, this.levelHeight, this.levelLength);
            
            ModernBeta.log(Level.INFO, String.format("Level file '%s' was loaded..", FiniteDataHandler.FILE_NAME));
        } catch (Exception e) {
            levelDataContainer = new LevelDataContainer(this.levelWidth, this.levelHeight, this.levelLength);
            
            ModernBeta.log(Level.WARN, String.format(
                "Level file '%s' is missing or corrupted and couldn't be loaded. Level will be generated and then saved!",
                FiniteDataHandler.FILE_NAME
            ));
            ModernBeta.log(Level.WARN, "Error: " + e.getMessage());
        }
        
        return levelDataContainer;
    }
    
    /**
     * Attempts to save finite level data to disk. If successful, then the level data can be loaded later to avoid regenerating the entire level.
     * 
     * @param world The world object, passed into {@link FiniteDataHandler}.
     * @param levelDataContainer The active level data container to be saved.
     * @return Whether the file was successfully saved.
     */
    private boolean trySaveLevel(World world, LevelDataContainer levelDataContainer) {
        FiniteDataHandler dataHandler = new FiniteDataHandler(world, this);
        boolean saved = false;
        
        try {
            dataHandler.setLevelData(levelDataContainer.levelData, levelDataContainer.levelMap);
            dataHandler.writeToDisk();
            
            ModernBeta.log(Level.INFO, String.format("Level file '%s' was saved..", FiniteDataHandler.FILE_NAME));
            saved = true;
        } catch (Exception e) {
            ModernBeta.log(Level.ERROR, String.format("Level file '%s' couldn't be saved!", FiniteDataHandler.FILE_NAME));
            ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
        }
        
        return saved;
    }
    
    /**
     * Debugs the level data handler. If {@link #DEBUG_LEVEL_DATA_HANDLER} is set to true then this will be run after the level is saved.
     * The level file is read from disk and compared to the currently loaded level data to ensure data integrity.
     * 
     * @param world The world object, passed into {@link FiniteDataHandler}.
     * @param levelDataContainer The active level data container to be debugged against.
     */ 
    private void debugLevelDataHandler(World world, LevelDataContainer levelDataContainer) {
        FiniteDataHandler dataHandler = new FiniteDataHandler(world, this);

        ModernBeta.log(Level.INFO, String.format("Attempting to read level file '%s'..", FiniteDataHandler.FILE_NAME));
        try {
            dataHandler.readFromDisk();
            LevelDataContainer readLevelData = dataHandler.getLevelData(this.levelWidth, this.levelHeight, this.levelLength);
            
            for (int x = 0; x < this.levelWidth; ++x) {
                for (int y = 0; y < this.levelHeight; ++y) {
                    for (int z = 0; z < this.levelLength; ++z) {
                        Block expected = levelDataContainer.getLevelBlock(x, y, z, this.levelWidth, this.levelLength);
                        Block found = readLevelData.getLevelBlock(x, y, z, this.levelWidth, this.levelLength);
                        
                        if (expected != found) {
                            ModernBeta.log(
                                Level.INFO,     
                                String.format(
                                    "Level data did not match, expected %s, found %s at position %d/%d/%d!",
                                    expected.getRegistryName(),
                                    found.getRegistryName(),
                                    x, y, z
                                )
                            );
                        }
                    }
                }
            }
            
            ModernBeta.log(Level.INFO, String.format("Level file '%s' was validated with no errors found..", FiniteDataHandler.FILE_NAME));
        } catch (Exception e) {
            ModernBeta.log(Level.WARN, String.format(
                "Level file '%s' is missing or corrupted and couldn't be loaded. Level will be generated and then saved!",
                FiniteDataHandler.FILE_NAME
            ));
            ModernBeta.log(Level.WARN, "Error: " + e.getMessage());
        }
    }
    
    public static class LevelDataContainer {
        private final byte[] levelData;
        private final BiMap<Byte, String> levelMap;
        private final BiMap<Byte, Block> levelBlockMap;
        
        private byte blockId;
        private boolean generated;
        
        /**
         * Constructs a LevelDataContainer for level data.
         * The level data is a 1D byte area of size {@link FiniteChunkSource#levelWidth} * {@link FiniteChunkSource#levelHeight} * {@link FiniteChunkSource#levelLength}.
         * The level data is mapped to block references with a BiMap matching byte ids to block string identifiers and block references.
         * 
         * @param levelWidth The level width.
         * @param levelHeight The level height.
         * @param levelLength The level length.
         */
        public LevelDataContainer(int levelWidth, int levelHeight, int levelLength) {
            this.levelData = new byte[levelWidth * levelHeight * levelLength];
            this.levelMap = HashBiMap.create();
            this.levelBlockMap = HashBiMap.create();
            
            this.blockId = 0;
            this.generated = false;
            
            this.levelMap.put(this.blockId++, Blocks.AIR.getRegistryName().toString());
            Arrays.fill(this.levelData, this.levelMap.inverse().get(Blocks.AIR.getRegistryName().toString()));
        }
        
        /**
         * Constructs a LevelDataContainer from existing level data. This is called when reading level data from disk.
         * 
         * @param levelData Read level data.
         * @param levelMap Read level byte-block id map.
         */
        public LevelDataContainer(byte[] levelData, BiMap<Byte, String> levelMap) {
            this.levelData = levelData;
            this.levelMap = levelMap;
            this.levelBlockMap = HashBiMap.create();
            this.generated = true;
        }
        
        /**
         * Gets a block given coordinates from level data.
         * 
         * @param x x-coordinate in block coordinates in level space.
         * @param y y-coordinate in block coordinates in level space.
         * @param z z-coordinate in block coordinates in level space.
         * @param levelWidth The level width, used to offset the index into the level data array.
         * @param levelLength The level length, used to offset the index into the level dat array.
         * @return The block at the given coordinates.
         */
        private Block getLevelBlock(int x, int y, int z, int levelWidth, int levelLength) {
            byte blockId = this.levelData[(y * levelLength + z) * levelWidth + x];
            
            if (!this.levelBlockMap.containsKey(blockId)) {
                String registryName = this.levelMap.get(blockId);
                
                this.levelBlockMap.put(blockId, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName)));
            }
            
            return this.levelBlockMap.get(blockId);
        }
        
        /**
         * Sets a block into level data at the given coordinates.
         * 
         * @param x x-coordinate in block coordinates.
         * @param y y-coordinate in block coordinates.
         * @param z z-coordinate in block coordinates.
         * @param levelWidth The level width, used to offset the index into the level data array.
         * @param levelLength The level length, used to offset the index into the level dat array.
         * @param block The block to set in level data.
         */
        private void setLevelBlock(int x, int y, int z, int levelWidth, int levelLength, Block block) {
            String registryName = ForgeRegistries.BLOCKS.getKey(block).toString();
            
            if (!this.levelMap.containsValue(registryName)) {
                this.levelMap.put(this.blockId++, registryName);
            }
            
            if (this.levelMap.size() > 255) {
                throw new IndexOutOfBoundsException("Level data block map size exceeded 255!");
            }
            
            this.levelData[(y * levelLength + z) * levelWidth + x] = this.levelMap.inverse().get(registryName);
        }
    }
}
