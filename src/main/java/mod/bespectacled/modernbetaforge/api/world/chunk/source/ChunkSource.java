package mod.bespectacled.modernbetaforge.api.world.chunk.source;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.BiomeResolverCreator;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverCustom;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.WorldSpawner;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjector;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class ChunkSource {
    protected static final int OCEAN_MIN_DEPTH = 4;
    protected static final int DEEP_OCEAN_MIN_DEPTH = 16;
    
    protected final long seed;
    protected final ModernBetaGeneratorSettings settings;
    protected final BiomeSource biomeSource;
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
     * @param biomeSource The biome source.
     */
    public ChunkSource(long seed, ModernBetaGeneratorSettings settings, BiomeSource biomeSource) {
        this.seed = seed;
        this.settings = settings;
        this.biomeSource = biomeSource;
        this.random = this.createRandom(seed);
        
        this.defaultBlock = ModernBetaRegistries.DEFAULT_BLOCK.get(settings.defaultBlock).get().getDefaultState();
        this.defaultFluid = ForgeRegistries.BLOCKS.getValue(settings.defaultFluid).getDefaultState();
        
        this.worldHeight = settings.height;
        this.seaLevel = settings.seaLevel;
        
        this.blockSources = ModernBetaRegistries.BLOCK_SOURCE
            .getValues()
            .stream()
            .map(e -> e.apply(this, this.settings))
            .collect(Collectors.toList());
        
        Random random = this.createRandom(seed);
        this.beachOctaveNoise = Optional.ofNullable(new PerlinOctaveNoise(random, 4, true));
        this.surfaceOctaveNoise = Optional.ofNullable(new PerlinOctaveNoise(random, 4, true));
        this.forestOctaveNoise = Optional.ofNullable(new PerlinOctaveNoise(random, 8, true));
        
        // Set default cloud height
        this.setCloudHeight(this.worldHeight - 20);
    }
    
    /**
     * Create initial chunk given chunk coordinates.
     * Used to sample for biome injection and creation of initial biome map.
     * If using biomes for chunk generation, DO NOT use {@link BiomeProvider#getBiome(net.minecraft.util.math.BlockPos) BiomeProvider.getBiome()} from {@link World}.
     * At this stage, the biomes have not actually been set for {@link ModernBetaBiome} to sample from.
     * Instead, get the biome provider, cast to {@link ModernBetaBiomeProvider} and use {@link ModernBetaBiomeProvider#getBiomeSource()} to sample biomes, or do final generation in {@link #provideProcessedChunk(ChunkPrimer, int, int, List) processedChunk} to get processed biome map.
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    public abstract void provideInitialChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    /**
     * Create processed chunk given chunk coordinates.
     * This exists to allow terrain to be modified or regenerated for village placement, which needs to occur after {@link #provideInitialChunk(ChunkPrimer, int, int) provideInitialChunk} so villages can be placed correctly.
     * This does not need to be implemented and can be left empty, if you don't wish to do additional processing.
     * 
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @param structureComponents The list of structure components that at least partially occupy this chunk.
     */
    public abstract void provideProcessedChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, List<StructureComponent> structureComponents);

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
     * Gets the world spawner used to initially place the player spawn.
     * 
     * @return The world spawner, {@link WorldSpawner#DEFAULT} by default. 
     */
    public WorldSpawner getWorldSpawner() {
        return ModernBetaRegistries.WORLD_SPAWNER.getOrElse(this.settings.worldSpawner, WorldSpawner.DEFAULT);
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
     * Gets the population seed based on chunk coordinates.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return The seed to use for feature population.
     */
    public long getPopulationSeed(int chunkX, int chunkZ) {
        Random random = new Random(this.seed);
        long randomLong0 = (random.nextLong() / 2L) * 2L + 1L;
        long randomLong1 = (random.nextLong() / 2L) * 2L + 1L;

        return (long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ this.seed;
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
     * Creates the rule set builder used for biome injection.
     * 
     * @param biomeSource The biome source.
     * @return The biome injection rules.
     */
    public BiomeInjectionRules.Builder createBiomeInjectionRules(BiomeSource biomeSource) {
        boolean replaceOceans = this.getGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getGeneratorSettings().replaceBeachBiomes;
        
        BiomeInjectionRules.Builder builder = new BiomeInjectionRules.Builder();
        
        Predicate<BiomeInjectionContext> deepOceanPredicate = context -> 
            BiomeInjector.atOceanDepth(context.getPos().getY(), DEEP_OCEAN_MIN_DEPTH, this.getSeaLevel());
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> 
            BiomeInjector.atOceanDepth(context.getPos().getY(), OCEAN_MIN_DEPTH, this.getSeaLevel());
            
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            BiomeInjector.atBeachDepth(context.getPos().getY(), this.getSeaLevel()) && BiomeInjector.isBeachBlock(context.getState(), context.getBiome());
        
        if (replaceOceans && biomeSource instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)biomeSource;
    
            builder.add(deepOceanPredicate, biomeResolverOcean::getDeepOceanBiome, BiomeInjectionStep.PRE_SURFACE);
            builder.add(oceanPredicate, biomeResolverOcean::getOceanBiome, BiomeInjectionStep.PRE_SURFACE);
        }
        
        if (replaceBeaches && biomeSource instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)biomeSource;
            
            builder.add(beachPredicate, biomeResolverBeach::getBeachBiome, BiomeInjectionStep.POST_SURFACE);
        }
        
        for (BiomeResolverCreator resolverCreator : ModernBetaRegistries.BIOME_RESOLVER.getValues()) {
            BiomeResolverCustom customResolver = resolverCreator.apply(this, this.settings);
            
            if (customResolver.useCustomResolver()) {
                builder.add(customResolver.getCustomPredicate(), (x, z) -> customResolver.getCustomBiome(x, z, biomeSource), BiomeInjectionStep.CUSTOM);
            }
        }
        
        return builder;
    }
    
    /**
     * Creates a new Random for world generation.
     * 
     * @param seed The world seed
     * @return A new Random initialized with the world seed.
     */
    protected Random createRandom(long seed) {
        return new Random(seed);
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
}
