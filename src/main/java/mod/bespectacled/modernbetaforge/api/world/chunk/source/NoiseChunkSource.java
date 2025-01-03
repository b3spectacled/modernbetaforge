package mod.bespectacled.modernbetaforge.api.world.chunk.source;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.DensityChunk;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceRules;
import mod.bespectacled.modernbetaforge.world.chunk.source.SkylandsChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.structure.StructureWeightSampler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureComponent;

public abstract class NoiseChunkSource extends ChunkSource {
    protected final int verticalNoiseResolution;   // Number of blocks in a vertical subchunk
    protected final int horizontalNoiseResolution; // Number of blocks in a horizontal subchunk 
    
    protected final int noiseSizeX; // Number of horizontal subchunks along x
    protected final int noiseSizeZ; // Number of horizontal subchunks along z
    protected final int noiseSizeY; // Number of vertical subchunks
    protected final int noiseTopY;  // Number of positive (y >= 0) vertical subchunks
    
    private final ChunkCache<HeightmapChunk> heightmapCache;
    private final ChunkCache<DensityChunk> densityCache;
    
    private final List<NoiseSampler> noiseSamplers;
    private final Map<ResourceLocation, NoiseSource> noiseSources;
    
    private final NoiseSettings noiseSettings;
    private final SurfaceBuilder surfaceBuilder;

    /**
     * Constructs an abstract NoiseChunkSource with necessary noise setting information.
     * 
     * @param seed The world seed.
     * @param settings The generator settings.
     */
    public NoiseChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        NoiseSettings noiseSettings = ModernBetaRegistries.NOISE_SETTING.get(new ResourceLocation(settings.chunkSource));
        
        this.verticalNoiseResolution = noiseSettings.sizeVertical * 4;
        this.horizontalNoiseResolution = noiseSettings.sizeHorizontal * 4;
        
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = Math.floorDiv(this.worldHeight, this.verticalNoiseResolution);
        this.noiseTopY = Math.floorDiv(this.worldHeight, this.verticalNoiseResolution);
        
        this.heightmapCache = new ChunkCache<>("heightmap", this::sampleHeightmap);
        this.densityCache = new ChunkCache<>("density", this::sampleDensities);
        
        this.noiseSamplers = ModernBetaRegistries.NOISE_SAMPLER.getValues()
            .stream()
            .map(entry -> entry.apply(this, this.settings))
            .collect(Collectors.toCollection(LinkedList<NoiseSampler>::new));
        this.noiseSources = new LinkedHashMap<>();
        ModernBetaRegistries.NOISE_COLUMN_SAMPLER.getEntrySet().forEach(entry -> noiseSources.put(
            entry.getKey(),
            new NoiseSource(
                entry.getValue().apply(this, this.settings),
                this.noiseSizeX,
                this.noiseSizeY,
                this.noiseSizeZ
            )
        ));
        
        this.noiseSettings = noiseSettings;
        this.surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER
            .getOrElse(new ResourceLocation(settings.surfaceBuilder), ModernBetaBuiltInTypes.Surface.BETA.getRegistryKey())
            .apply(this, settings);
    }
    
    /**
     * Inherited from {@link ChunkSource#provideInitialChunk(World, ChunkPrimer, int, int) provideInitialChunk}.
     * This does not generate terrain, only samples the initial densities.
     * 
     * @param world The world object
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    @Override
    public void provideInitialChunk(World world, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        this.densityCache.get(chunkX, chunkZ);
    }
    
    /**
     * Inherited from {@link ChunkSource#provideProcessedChunk(World, ChunkPrimer, int, int, List) provideProcessedChunk}.
     * This actually generates the terrain, after collection of village component placements.
     * 
     * @param world The world object
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @param structureComponents The list of structure components that at least partially occupy this chunk.
     */
    @Override
    public void provideProcessedChunk(World world, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, List<StructureComponent> structureComponents) {
        this.generateTerrain(world, chunkPrimer, chunkX, chunkZ, structureComponents);
    }
    
    /**
     * Inherited from {@link ChunkSource#provideSurface(World, Biome[], ChunkPrimer, int, int) provideSurface}.
     * 
     * @param world The world object
     * @param biomes Biome array for chunk
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    @Override
    public void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        this.surfaceBuilder.provideSurface(world, biomes, chunkPrimer, chunkX, chunkZ);
    }
    
    /**
     * Inherited from {@link ChunkSource#getHeight(World, int, int, mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type) getHeight}.
     * Initially generates heightmap for entire chunk, if chunk containing x/z coordinates has never been sampled.
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type {@link HeightmapChunk.Type}.
     *
     * @return The y-coordinate of top block at x/z.
     * 
     */
    @Override
    public int getHeight(World world, int x, int z, HeightmapChunk.Type type) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.heightmapCache.get(chunkX, chunkZ).getHeight(x, z, type);
    }
    
    /**
     * Gets sampled terrain density from a particular layer identified by ResouceLocation key.
     * 
     * @param key The terrain density layer
     * @param x x-coordinate in block coordinates.
     * @param y y-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @return A terrain density.
     */
    public double getDensity(ResourceLocation key, int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.densityCache.get(chunkX, chunkZ).sample(key, x, y, z);
    }
    
    /**
     * Generates noise for a column at startNoiseX + localNoiseX / startNoiseZ + localNoiseZ.
     * 
     * @param buffer Buffer of size noiseSizeY + 1 to store noise column
     * @param startNoiseX x-coordinate start of chunk in noise coordinates.
     * @param startNoiseZ z-coordinate start of chunk in noise coordinates.
     * @param localNoiseX Current subchunk index along x-axis.
     * @param localNoiseZ Current subchunk index along z-axis.
     */
    protected abstract void sampleNoiseColumn(
        double[] buffer,
        int startNoiseX,
        int startNoiseZ,
        int localNoiseX,
        int localNoiseZ
    );

    /**
     * Generates the base terrain for a given chunk.
     * @param world The world object
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    private void generateTerrain(World world, ChunkPrimer chunkPrimer, int chunkX, int chunkZ, List<StructureComponent> structureComponents) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        int sizeX = this.horizontalNoiseResolution * this.noiseSizeX;
        int sizeZ = this.horizontalNoiseResolution * this.noiseSizeZ;
        int sizeY = this.verticalNoiseResolution * this.noiseSizeY;
        
        StructureWeightSampler weightSampler = new StructureWeightSampler(structureComponents);
        DensityChunk densityChunk = this.densityCache.get(chunkX, chunkZ);
        
        // Build block source rules
        BlockSourceRules blockSources = new BlockSourceRules.Builder(this.defaultBlock)
            .add(this.getInitialBlockSource(world, densityChunk, weightSampler))
            .add(this.blockSources)
            .build();
        
        for (int localX = 0; localX < sizeX; ++localX) {
            int x = localX + startX;
            
            for (int localZ = 0; localZ < sizeZ; ++localZ) {
                int z = localZ + startZ;
                
                for (int y = 0; y < sizeY; ++y) {
                    chunkPrimer.setBlockState(localX, y, localZ, blockSources.sample(x, y, z));
                }
            }
        }
    }
    
    /**
     * Generates a heightmap for the chunk containing the given x/z coordinates
     * and returns to {@link #getHeight(World, int, int, net.minecraft.world.Heightmap.Type) getHeight} 
     * to cache and return the height.
     * 
     * @param chunkX x-coordinate in chunk coordinates to sample all y-values for.
     * @param chunkZ z-coordinate in chunk coordinates to sample all y-values for.
     * @return A HeightmapChunk, containing an array of ints containing the heights for the entire chunk.
     */
    private HeightmapChunk sampleHeightmap(int chunkX, int chunkZ) {
        short minStructureHeight = this instanceof SkylandsChunkSource ? (short)32 : 0;
        short minHeight = 0;
        short worldMinY = 0;
        short worldHeight = (short)this.worldHeight;
        
        short[] heightmapSurface = new short[256];
        short[] heightmapOcean = new short[256];
        short[] heightmapFloor = new short[256];
        short[] heightmapStructure = new short[256];
        
        Arrays.fill(heightmapSurface, minHeight);
        Arrays.fill(heightmapOcean, minHeight);
        Arrays.fill(heightmapFloor, worldMinY);
        Arrays.fill(heightmapStructure, minStructureHeight);
        
        int sizeX = this.horizontalNoiseResolution * this.noiseSizeX;
        int sizeZ = this.horizontalNoiseResolution * this.noiseSizeZ;
        int sizeY = this.verticalNoiseResolution * this.noiseSizeY;
        
        DensityChunk densityChunk = this.densityCache.get(chunkX, chunkZ);
        
        for (int x = 0; x < sizeX; ++x) {
            for (int z = 0; z < sizeZ; ++z) {
                for (int y = 0; y < sizeY; ++y) {
                    
                    double density = densityChunk.sample(x, y, z);
                    boolean isSolid = density > 0.0;
                    
                    short height = (short)y;
                    int ndx = z + x * 16;
                    
                    // Capture topmost solid/fluid block height.
                    if (y < this.getSeaLevel() || isSolid) {
                        heightmapOcean[ndx] = height;
                        heightmapStructure[ndx] = height;
                    }
                    
                    // Capture topmost solid block height.
                    if (isSolid) {
                        heightmapSurface[ndx] = height;
                    }
                    
                    // Capture lowest solid block height.
                    // First, set max world height as flag when hitting first solid layer
                    // then set the actual height value when hitting first non-solid layer.
                    // This handles situations where the bottom of the world may not be solid,
                    // i.e. Skylands-style world types.
                    if (isSolid && heightmapFloor[ndx] == worldMinY) {
                        heightmapFloor[ndx] = worldHeight;
                    }
                    
                    if (!isSolid && heightmapFloor[ndx] == worldHeight) {
                        heightmapFloor[ndx] = (short)(height - 1);
                    }
                }
            }
        }
        
        // Construct new heightmap cache from generated heightmap array
        return new HeightmapChunk(heightmapSurface, heightmapOcean, heightmapFloor, heightmapStructure);
    }

    /**
     * Sample initial noise for a given chunk.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return NoiseSource containing initial noise values for the chunk.
     */
    private NoiseSource createInitialNoiseSource(int chunkX, int chunkZ) {
        NoiseSource noiseSource = new NoiseSource(
            (buffer, startX, startZ, localX, localZ, sizeX, sizeY, sizeZ) -> this.sampleNoiseColumn(
                buffer,
                startX,
                startZ,
                localX,
                localZ
            ),
            this.noiseSizeX,
            this.noiseSizeY,
            this.noiseSizeZ
        );
        
        return noiseSource;
    }

    /**
     * Creates block source to sample BlockState at block coordinates given base noise provider.
     * 
     * @param world The world object
     * @param noiseSource Primary noise provider to sample density noise.
     * @param blockSource Default block source
     * 
     * @return BlockSource to sample blockstate at x/y/z block coordinates.
     */
    private BlockSource getInitialBlockSource(World world, DensityChunk densityChunk, StructureWeightSampler weightSampler) {
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        return (x, y, z) -> {
            IBlockState blockState = BlockStates.AIR;
            double density = densityChunk.sample(x, y, z);
            
            density = MathHelper.clamp(density / 200.0, -1.0, 1.0);
            density = density / 2.0 - density * density * density / 24.0;
            
            density += weightSampler.sample(world, this, mutablePos.setPos(x, y, z));
            
            if (density > 0.0) {
                blockState = null;
            } else if (y < this.getSeaLevel()) {
                blockState = this.defaultFluid;
            }
            
            return blockState;
        };
    }
    
    /**
     * Samples and interpolates all terrain densities for a given chunk. This information is cached for later use.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return Container of interpolated densities, to be cached and used in other methods for terrain generation and height sampling.
     */
    private DensityChunk sampleDensities(int chunkX, int chunkZ) {
        int sizeX = this.horizontalNoiseResolution * this.noiseSizeX;
        int sizeZ = this.horizontalNoiseResolution * this.noiseSizeZ;
        int sizeY = this.verticalNoiseResolution * this.noiseSizeY;

        // Create noise sources and sample.
        Map<ResourceLocation, NoiseSource> noiseSources = new LinkedHashMap<>(this.noiseSources);
        noiseSources.put(DensityChunk.INITIAL, this.createInitialNoiseSource(chunkX, chunkZ));
        noiseSources.entrySet().forEach(entry -> entry.getValue().sampleInitialNoise(
            chunkX * this.noiseSizeX,
            chunkZ * this.noiseSizeZ,
            this.noiseSettings,
            entry.getKey().equals(DensityChunk.INITIAL) ? this.noiseSamplers : ImmutableList.of()
        ));
        
        Map<ResourceLocation, double[]> densityMap = new LinkedHashMap<>();
        for (Entry<ResourceLocation, NoiseSource> entry : noiseSources.entrySet()) {
            NoiseSource noiseSource = entry.getValue();
            double[] densities = new double[sizeX * sizeZ * sizeY];
            
            for (int subChunkX = 0; subChunkX < this.noiseSizeX; ++subChunkX) {
                for (int subChunkZ = 0; subChunkZ < this.noiseSizeZ; ++subChunkZ) {
                    for (int subChunkY = 0; subChunkY < this.noiseSizeY; ++subChunkY) {
                        noiseSource.sampleNoiseCorners(subChunkX, subChunkY, subChunkZ);
                        
                        for (int subY = 0; subY < this.verticalNoiseResolution; ++subY) {
                            int y = subY + subChunkY * this.verticalNoiseResolution;

                            double deltaY = subY / (double)this.verticalNoiseResolution;
                            noiseSource.sampleNoiseY(deltaY);
                            
                            for (int subX = 0; subX < this.horizontalNoiseResolution; ++subX) {
                                int x = subX + subChunkX * this.horizontalNoiseResolution;
                                
                                double deltaX = subX / (double)this.horizontalNoiseResolution;
                                noiseSource.sampleNoiseX(deltaX);
                                
                                for (int subZ = 0; subZ < this.horizontalNoiseResolution; ++subZ) {
                                    int z = subZ + subChunkZ * this.horizontalNoiseResolution;
                                    
                                    double deltaZ = subZ / (double)this.horizontalNoiseResolution;
                                    noiseSource.sampleNoiseZ(deltaZ);
                                    
                                    densities[(y * 16 + x) * 16 + z] = noiseSource.sample();
                                }
                            }
                        }
                    }
                }
            }
            
            densityMap.put(entry.getKey(), densities);
        }
        
        return new DensityChunk(densityMap);
    }
}
