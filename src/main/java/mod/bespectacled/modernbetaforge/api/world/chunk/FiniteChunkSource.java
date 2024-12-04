package mod.bespectacled.modernbetaforge.api.world.chunk;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.chunk.data.FiniteLevelDataHandler;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorMinecraftServer;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
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
    
    private final IndevHouse levelHouse;
    private final LevelDataContainer levelDataContainer;
    
    private String phase;
    @SuppressWarnings("unused")
    private float phaseProgress;
    
    public FiniteChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings chunkGeneratorSettings,
        ModernBetaNoiseSettings noiseSettings,
        long seed,
        boolean mapFeaturesEnabled
    ) {
        super(world, chunkGenerator, chunkGeneratorSettings, noiseSettings, seed, mapFeaturesEnabled);
        
        this.levelWidth = MathHelper.clamp(settings.levelWidth >> 4 << 4, MIN_WIDTH, MAX_WIDTH);
        this.levelLength = MathHelper.clamp(settings.levelLength >> 4 << 4, MIN_WIDTH, MAX_WIDTH);
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

        return this.levelDataContainer.getLevelBlock(x, y, z, this.levelWidth, this.levelLength);
    }
    
    public void setLevelBlock(int x, int y, int z, Block block) {
        x = MathHelper.clamp(x, 0, this.levelWidth - 1);
        y = MathHelper.clamp(y, 0, this.levelHeight - 1);
        z = MathHelper.clamp(z, 0, this.levelLength - 1);

        this.levelDataContainer.setLevelBlock(x, y, z, this.levelWidth, this.levelLength, block);
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
    
    protected int flood(int x, int y, int z, Block fillBlock, Block replaceBlock) {
        return this.flood(x, y, z, fillBlock, replaceBlock, null);
    }
    
    /*
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
    
    protected void setPhase(String phase) {
        this.phase = phase;
        
        if (this.world.getMinecraftServer() != null) {
            AccessorMinecraftServer accessor = (AccessorMinecraftServer)this.world.getMinecraftServer();
            accessor.invokeSetUserMessage(this.phase + "..");
        }
        
        ModernBeta.log(Level.INFO, this.phase + "..");
    }
    
    protected void setPhaseProgress(float phaseProgress) {
        this.phaseProgress = phaseProgress;

        /*
        if (this.world.getMinecraftServer() != null) {
            AccessorMinecraftServer accessor = (AccessorMinecraftServer)this.world.getMinecraftServer();
            String progressStr = String.format("%s.. %d", this.phase, (int)(this.phaseProgress * 100.0f));
            
            accessor.invokeSetUserMessage(progressStr);
        }
        */
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
                Biome biome = this.biomeProvider.getBiomeSource().getBiome(x, z);
                
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
    
    private boolean inCaveBounds(int x, int y, int z) {
        return x > 0 && x < this.levelWidth - 1 && y > 0 && y < this.levelHeight - 1 && z > 0 && z < this.levelLength - 1;
    }
    
    private void tryFlood(int x, int y, int z, Block replaceBlock, Deque<Vec3d> positions) {
        Block block = this.getLevelBlock(x, y, z);
        
        if (block == replaceBlock) {
            positions.add(new Vec3d(x, y, z));
        }
    }
    
    private LevelDataContainer tryLoadLevel() {
        FiniteLevelDataHandler dataHandler = new FiniteLevelDataHandler(this.world, this);
        LevelDataContainer levelDataContainer;
        
        ModernBeta.log(Level.INFO, String.format("Attempting to read level file '%s'..", FiniteLevelDataHandler.FILE_NAME));
        try {
            dataHandler.readFromDisk();
            levelDataContainer = dataHandler.getLevelData(this.levelWidth, this.levelHeight, this.levelLength);
            
            ModernBeta.log(Level.INFO, String.format("Level file '%s' was loaded..", FiniteLevelDataHandler.FILE_NAME));
        } catch (Exception e) {
            levelDataContainer = new LevelDataContainer(this.levelWidth, this.levelHeight, this.levelLength);
            
            ModernBeta.log(Level.WARN, String.format(
                "Level file '%s' is missing or corrupted and couldn't be loaded. Level will be generated and then saved!",
                FiniteLevelDataHandler.FILE_NAME
            ));
            ModernBeta.log(Level.WARN, "Error: " + e.getMessage());
        }
        
        return levelDataContainer;
    }
    
    private boolean trySaveLevel() {
        FiniteLevelDataHandler dataHandler = new FiniteLevelDataHandler(this.world, this);
        boolean saved = false;
        
        try {
            dataHandler.setLevelData(this.levelDataContainer.levelData, this.levelDataContainer.levelMap);
            dataHandler.writeToDisk();
            
            ModernBeta.log(Level.INFO, String.format("Level file '%s' was saved..", FiniteLevelDataHandler.FILE_NAME));
            saved = true;
        } catch (Exception e) {
            ModernBeta.log(Level.ERROR, String.format("Level file '%s' couldn't be saved!", FiniteLevelDataHandler.FILE_NAME));
            ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
        }
        
        return saved;
    }
    
    private void debugLevelDataHandler() {
        FiniteLevelDataHandler dataHandler = new FiniteLevelDataHandler(this.world, this);

        ModernBeta.log(Level.INFO, String.format("Attempting to read level file '%s'..", FiniteLevelDataHandler.FILE_NAME));
        try {
            dataHandler.readFromDisk();
            LevelDataContainer readLevelData = dataHandler.getLevelData(this.levelWidth, this.levelHeight, this.levelLength);
            
            for (int x = 0; x < this.levelWidth; ++x) {
                for (int y = 0; y < this.levelHeight; ++y) {
                    for (int z = 0; z < this.levelLength; ++z) {
                        Block expected = this.levelDataContainer.getLevelBlock(x, y, z, this.levelWidth, this.levelLength);
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
            
            ModernBeta.log(Level.INFO, String.format("Level file '%s' was validated with no errors found..", FiniteLevelDataHandler.FILE_NAME));
        } catch (Exception e) {
            ModernBeta.log(Level.WARN, String.format(
                "Level file '%s' is missing or corrupted and couldn't be loaded. Level will be generated and then saved!",
                FiniteLevelDataHandler.FILE_NAME
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
        
        public LevelDataContainer(int levelWidth, int levelHeight, int levelLength) {
            this.levelData = new byte[levelWidth * levelHeight * levelLength];
            this.levelMap = HashBiMap.create();
            this.levelBlockMap = HashBiMap.create();
            
            this.blockId = 0;
            this.generated = false;
            
            this.levelMap.put(this.blockId++, Blocks.AIR.getRegistryName().toString());
            Arrays.fill(this.levelData, this.levelMap.inverse().get(Blocks.AIR.getRegistryName().toString()));
        }
        
        public LevelDataContainer(byte[] levelData, BiMap<Byte, String> levelMap) {
            this.levelData = levelData;
            this.levelMap = levelMap;
            this.levelBlockMap = HashBiMap.create();
            this.generated = true;
        }
        
        private Block getLevelBlock(int x, int y, int z, int levelWidth, int levelLength) {
            byte blockId = this.levelData[(y * levelLength + z) * levelWidth + x];
            
            if (!this.levelBlockMap.containsKey(blockId)) {
                String registryName = this.levelMap.get(blockId);
                
                this.levelBlockMap.put(blockId, ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName)));
            }
            
            return this.levelBlockMap.get(blockId);
        }
        
        public void setLevelBlock(int x, int y, int z, int levelWidth, int levelLength, Block block) {
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
