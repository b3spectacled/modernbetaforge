package mod.bespectacled.modernbetaforge.api.world.chunk.source;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureComponent;

public abstract class ChunkSource {
    protected static final int OCEAN_MIN_DEPTH = 4;
    protected static final int DEEP_OCEAN_MIN_DEPTH = 16;
    
    protected final long seed;
    protected final ModernBetaGeneratorSettings settings;
    protected final Random random;
    
    protected final IBlockState defaultBlock;
    protected final IBlockState defaultFluid;

    protected final int worldHeight;
    protected final int seaLevel;
    
    protected final List<BlockSource> blockSources;
    
    private Optional<PerlinOctaveNoise> beachOctaveNoise;
    private Optional<PerlinOctaveNoise> surfaceOctaveNoise;
    private Optional<PerlinOctaveNoise> forestOctaveNoise;
    
    /**
     * Constructs an abstract ChunkSource to hold basic underlying terrain generation information.
     * 
     * @param seed The world seed.
     * @param settings The generator settings.
     */
    public ChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        this.seed = seed;
        this.settings = settings;
        this.random = new Random(seed);
        
        this.defaultBlock = BlockStates.STONE;
        this.defaultFluid = settings.useLavaOceans ? BlockStates.LAVA : BlockStates.WATER;
        
        this.worldHeight = settings.height;
        this.seaLevel = settings.seaLevel;
        
        this.blockSources = ModernBetaRegistries.BLOCK_SOURCE
            .getValues()
            .stream()
            .map(e -> e.apply(this, this.settings))
            .collect(Collectors.toList());
        
        this.beachOctaveNoise = Optional.empty();
        this.surfaceOctaveNoise = Optional.empty();
        this.forestOctaveNoise = Optional.empty();
        
        // Set default cloud height
        this.setCloudHeight(this.worldHeight - 20);
    }
    
    /**
     * Create initial chunk given chunk coordinates.
     * Used to sample for biome injection and creation of initial biome map.
     * If using biomes for chunk generation, DO NOT use {@link BiomeProvider#getBiome(net.minecraft.util.math.BlockPos) BiomeProvider.getBiome()} from {@link World}.
     * At this stage, the biomes have not actually been set for {@link ModernBetaBiome} to sample from.
     * Instead, get the biome provider, cast to {@link ModernBetaBiomeProvider} and use {@link ModernBetaBiomeProvider#getBiomeSource()} to sample biomes, or do final generation in {@link #provideProcessedChunk(World, ChunkPrimer, int, int, List) processedChunk} to get processed biome map.
     * 
     * @param world The world object
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    public abstract void provideInitialChunk(World world, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    /**
     * Create processed chunk given chunk coordinates.
     * This exists to allow terrain to be modified or regenerated for village placement, which needs to occur after {@link #provideInitialChunk(World, ChunkPrimer, int, int) provideInitialChunk} so villages can be placed correctly.
     * This does not need to be implemented and can be left empty, if you don't wish to do additional processing.
     * 
     * @param world The world object
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @param structureComponents The list of structure components that at least partially occupy this chunk.
     */
    public abstract void provideProcessedChunk(World world, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, List<StructureComponent> structureComponents);

    /**
     * Build surface for given chunk primer and chunk coordinates.
     * 
     * @param world The world object
     * @param biomes Biome array for chunk
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    public abstract void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    /**
     * Sample height at given x/z coordinate.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type {@link HeightmapChunk.Type}.
     *
     * @return The y-coordinate of top block at x/z.
     */
    public abstract int getHeight(int x, int z, HeightmapChunk.Type type);

    /**
     * Gets the chunk source sea level set in the chunk generator settings.
     * 
     * @return The chunk source sea level.
     */
    public int getSeaLevel() {
        return this.settings.seaLevel;
    }
    
    /**
     * Gets the default block for the chunk source, usually stone.
     * 
     * @return The default block blockstate.
     */
    public IBlockState getDefaultBlock() {
        return this.defaultBlock;
    }
    
    /**
     * Gets the default fluid for the chunk source, either water or lava.
     * 
     * @return The default fluid blockstate.
     */
    public IBlockState getDefaultFluid() {
        return this.defaultFluid;
    }
    
    /**
     * Gets the chunk generator settings.
     * 
     * @return The {@link ModernBetaGeneratorSettings} settings.
     */
    public ModernBetaGeneratorSettings getGeneratorSettings() {
        return this.settings;
    }
    
    
    /**
     * Gets the spawn locator used to initially place the player spawn.
     * 
     * @return The spawn locator, {@link SpawnLocator#DEFAULT} by default. 
     */
    public SpawnLocator getSpawnLocator() {
        return SpawnLocator.DEFAULT;
    }
    
    /**
     * Gets the PerlinOctaveNoise sampler used for beach generation.
     * 
     * @return An optional containing the noise sampler, may be null.
     */
    public Optional<PerlinOctaveNoise> getBeachOctaveNoise() {
        return this.beachOctaveNoise;
    }
    
    /**
     * Gets the PerlinOctaveNoise sampler used for surface generation.
     * 
     * @return An optional containing the noise sampler, may be null.
     */
    public Optional<PerlinOctaveNoise> getSurfaceOctaveNoise() {
        return this.surfaceOctaveNoise;
    }
    
    /**
     * Gets the PerlinOctaveNoise sampler used for tree placement.
     * 
     * @return An optional containing the noise sampler, may be null.
     */
    public Optional<PerlinOctaveNoise> getForestOctaveNoise() {
        return this.forestOctaveNoise;
    }
    
    /**
     * Gets the world seed.
     * 
     * @return The world seed.
     */
    public long getSeed() {
        return this.seed;
    }

    /**
     * Indicate whether the chunk at the given coordinates should be skipped.
     * This is called in several generation methods and used by {@link FiniteChunkSource}.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return Whether the chunk should be skipped.
     */
    public boolean skipChunk(int chunkX, int chunkZ) {
        return false;
    }
    
    /**
     * Prunes the chuck at the given coordinates.
     * This is used by {@link FiniteChunkSource}.
     * 
     * @param world The world object
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    public void pruneChunk(World world, int chunkX, int chunkZ) { }
    
    /**
     * Builds the ruleset used for biome injection.
     * 
     * @param biomeProvider The biome provider.
     * @return The built biome injection rules.
     */
    public BiomeInjectionRules buildBiomeInjectorRules(ModernBetaBiomeProvider biomeProvider) {
        boolean replaceOceans = this.getGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getGeneratorSettings().replaceBeachBiomes;
        
        BiomeInjectionRules.Builder builder = new BiomeInjectionRules.Builder();
        
        Predicate<BiomeInjectionContext> deepOceanPredicate = context -> 
            this.atOceanDepth(context.pos.getY(), DEEP_OCEAN_MIN_DEPTH);
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> 
            this.atOceanDepth(context.pos.getY(), OCEAN_MIN_DEPTH);
            
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            this.atBeachDepth(context.pos.getY()) && this.isBeachBlock(context.state);
            
        if (replaceBeaches && biomeProvider.getBiomeSource() instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)biomeProvider.getBiomeSource();
            
            builder.add(beachPredicate, biomeResolverBeach::getBeachBiome, BiomeInjectionStep.POST_SURFACE);
        }
        
        if (replaceOceans && biomeProvider.getBiomeSource() instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)biomeProvider.getBiomeSource();
    
            builder.add(deepOceanPredicate, biomeResolverOcean::getDeepOceanBiome, BiomeInjectionStep.PRE_SURFACE);
            builder.add(oceanPredicate, biomeResolverOcean::getOceanBiome, BiomeInjectionStep.PRE_SURFACE);
        }
        
        return builder.build();
    }

    /**
     * Sets the cloud height in the {@link ModernBetaWorldType} world type.
     * 
     * @param cloudHeight y-coordinate of the new cloud height, in block coordinates.
     */
    protected void setCloudHeight(int cloudHeight) {
        ModernBetaWorldType.INSTANCE.setCloudHeight(cloudHeight);
    }

    /**
     * Sets the default PerlinOctaveNoise sampler used for beach generation for the default surface generator in {@link ModernBetaBiome}.
     * 
     * @param beachOctaveNoise The noise sampler, may be null.
     */
    protected void setBeachOctaveNoise(PerlinOctaveNoise beachOctaveNoise) {
        this.beachOctaveNoise = Optional.ofNullable(beachOctaveNoise);
        
        // Set beach noise for builtin Modern Biome surface builder
        ModernBetaBiome.setBeachOctaveNoise(beachOctaveNoise);
    }

    /**
     * Sets the PerlinOctaveNoise sampler used for surface generation.
     * 
     * @param surfaceOctaveNoise The noise sampler, may be null.
     */
    protected void setSurfaceOctaveNoise(PerlinOctaveNoise surfaceOctaveNoise) {
        this.surfaceOctaveNoise = Optional.ofNullable(surfaceOctaveNoise);
    }
    
    /**
     * Sets the PerlinOctaveNoise sampler used for tree placement.
     * 
     * @param forestOctaveNoise The noise sampler, may be null.
     */
    protected void setForestOctaveNoise(PerlinOctaveNoise forestOctaveNoise) {
        this.forestOctaveNoise = Optional.ofNullable(forestOctaveNoise);
    }

    /**
     * Tests whether a given height is where a beach generates.
     * 
     * @param topHeight y-coordinate of the highest block for a particular position.
     * @return Whether the given height for a position is at the depth where beaches generated.
     */
    protected boolean atBeachDepth(int topHeight) {
        int seaLevel = this.getSeaLevel();
        
        return topHeight >= seaLevel - 4 && topHeight <= seaLevel + 1;
    }

    /**
     * Tests whether a given block state is a beach (sand) block.
     * 
     * @param blockState Block state to be tested.
     * @return Whether the given block state is a sand block.
     */
    protected boolean isBeachBlock(IBlockState blockState) {
        Block block = blockState.getBlock();
        
        // Only handle sand beaches,
        // due to limitation of heightmap cache.
        return block == Blocks.SAND;
    }
    
    /**
     * Tests whether a given height is at ocean depth.
     * 
     * @param topHeight y-coordinate of the highest block for a particular position.
     * @param oceanDepth The height depth to test the height against.
     * @return Whether the given height is below the ocean depth value.
     */
    protected boolean atOceanDepth(int topHeight, int oceanDepth) {
        return topHeight < this.getSeaLevel() - oceanDepth;
    }

    /**
     * Tests whether a given block state is the default fluid block.
     * 
     * @param blockState Block state to be tested.
     * @return Whether the given block state is the default fluid block.
     */
    protected boolean isFluidBlock(IBlockState blockState) {
        return blockState.getBlock() == this.defaultFluid.getBlock();
    }
}
