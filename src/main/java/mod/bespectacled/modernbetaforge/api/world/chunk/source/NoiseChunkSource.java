package mod.bespectacled.modernbetaforge.api.world.chunk.source;

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
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceRules;
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
    
    private final Map<ResourceLocation, NoiseSampler> noiseSamplers;
    private final Map<ResourceLocation, NoiseSource> noiseSources;
    
    private final NoiseSettings noiseSettings;
    private final SurfaceBuilder surfaceBuilder;
    
    private final PerlinOctaveNoise minLimitOctaveNoise;
    private final PerlinOctaveNoise maxLimitOctaveNoise;
    private final PerlinOctaveNoise mainOctaveNoise;

    /**
     * Constructs an abstract NoiseChunkSource with necessary noise setting information.
     * 
     * @param seed The world seed.
     * @param settings The generator settings.
     */
    public NoiseChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        NoiseSettings noiseSettings = ModernBetaRegistries.NOISE_SETTING.get(settings.chunkSource);
        
        this.verticalNoiseResolution = noiseSettings.sizeVertical * 4;
        this.horizontalNoiseResolution = noiseSettings.sizeHorizontal * 4;
        
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = Math.floorDiv(this.worldHeight, this.verticalNoiseResolution);
        this.noiseTopY = Math.floorDiv(this.worldHeight, this.verticalNoiseResolution);
        
        this.heightmapCache = new ChunkCache<>("heightmap", this::sampleHeightmap);
        this.densityCache = new ChunkCache<>("density", this::sampleDensities);
        
        this.noiseSamplers = new LinkedHashMap<>();
        this.noiseSources = new LinkedHashMap<>();
        
        ModernBetaRegistries.NOISE_SAMPLER.getEntrySet().forEach(entry -> this.noiseSamplers.put(
            entry.getKey(),
            entry.getValue().apply(this, this.settings)
        ));
        ModernBetaRegistries.NOISE_COLUMN_SAMPLER.getEntrySet().forEach(entry -> this.noiseSources.put(
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
            .getOrElse(settings.surfaceBuilder, ModernBetaBuiltInTypes.Surface.BETA.getRegistryKey())
            .apply(this, settings);
        
        this.minLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.maxLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.mainOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
    }
    
    /**
     * Inherited from {@link ChunkSource#provideInitialChunk(ChunkPrimer, int, int) provideInitialChunk}.
     * This does not generate terrain, only samples the initial densities.
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    @Override
    public void provideInitialChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        this.densityCache.get(chunkX, chunkZ);
    }
    
    /**
     * Inherited from {@link ChunkSource#provideProcessedChunk(ChunkPrimer, int, int, List) provideProcessedChunk}.
     * This actually generates the terrain, after collection of village component placements.
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @param structureComponents The list of structure components that at least partially occupy this chunk.
     */
    @Override
    public void provideProcessedChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, List<StructureComponent> structureComponents) {
        this.generateTerrain(chunkPrimer, chunkX, chunkZ, structureComponents);
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
     * Inherited from {@link ChunkSource#getHeight(int, int, mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type) getHeight}.
     * Initially generates heightmap for entire chunk, if chunk containing x/z coordinates has never been sampled.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type {@link HeightmapChunk.Type}.
     * @return The y-coordinate of top block at x/z.
     * 
     */
    @Override
    public int getHeight(int x, int z, HeightmapChunk.Type type) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.heightmapCache.get(chunkX, chunkZ).getHeight(x, z, type);
    }
    
    /**
     * Gets sampled terrain density from a particular layer identified by ResourceLocation key.
     * 
     * @param key The terrain density layer key
     * @param x x-coordinate in block coordinates.
     * @param y y-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @return The terrain density.
     */
    public double getDensity(ResourceLocation key, int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.densityCache.get(chunkX, chunkZ).sample(key, x, y, z);
    }
    
    /**
     * Gets a noise sampler identified by ResourceLocation key.
     * 
     * @param key The noise sampler key
     * @return The noise sampler.
     */
    public NoiseSampler getNoiseSampler(ResourceLocation key) {
        if (!this.noiseSamplers.containsKey(key)) {
            String error = String.format("[Modern Beta] Noise Sampler map does not contain key '%s'!", key);
            
            throw new IllegalArgumentException(error);
        }
        
        return this.noiseSamplers.get(key);
    }
    
    /**
     * Gets the noise settings container.
     * 
     * @return The noise settings.
     */
    public NoiseSettings getNoiseSettings() {
        return this.noiseSettings;
    }
    
    /**
     * Gets the noise size values for the current chunk source.
     * 
     * @return A String representing the current noise size values.
     */
    public String debugNoiseSettings() {
        return String.format("Noise Sizes: %d %d %d", this.noiseSizeX, this.noiseSizeY, this.noiseSizeZ);
    }
    
    /**
     * Gets the local noise coordinates for the given block coordinates.
     * 
     * @param x x-coordinate in block coordinates.
     * @param y y-coordinate in block cooridnates.
     * @param z z-coordinate in block coordinates.
     * @return A String representing the current noise coordinates.
     */
    public String debugNoiseCoordinates(int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        int localNoiseX = (x & 0xF) / this.noiseSizeX;
        int localNoiseZ = (z & 0xF) / this.noiseSizeZ;
        int noiseY = y / this.verticalNoiseResolution;
        
        return String.format("Noise: %d %d %d in %d %d", localNoiseX, noiseY, localNoiseZ, chunkX, chunkZ);
    }

    /**
     * Gets the scale, depth, and offset values for the given block coordinates.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @return A String representing the current coordinates' scale, depth, and offset values.
     */
    public String debugNoiseModifiers(int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        int startNoiseX = chunkX * this.noiseSizeX;
        int startNoiseZ = chunkZ * this.noiseSizeZ;
        
        int localNoiseX = (x & 0xF) / this.noiseSizeX;
        int localNoiseZ = (z & 0xF) / this.noiseSizeZ;
        int noiseY = y / this.verticalNoiseResolution;
        
        NoiseScaleDepth noiseScaleDepth = this.sampleNoiseScaleDepth(startNoiseX, startNoiseZ, localNoiseX, localNoiseZ);
        double scale = noiseScaleDepth.scale;
        double depth = noiseScaleDepth.depth;
        double offset = this.sampleNoiseOffset(noiseY, scale, depth);
        
        return String.format("Scale: %.3f Depth: %.3f Offset: %.3f", scale, depth, offset);
    }
    
    /**
     * Samples noise for a column at startNoiseX + localNoiseX, startNoiseZ + localNoiseZ.
     * The startNoise and localNoise values should be added to produce the actual noise coordinate; they are kept separate for calculating accurate Beta/PE generation.
     * You can override this if necessary, but otherwise just implement {@link #sampleNoiseScaleDepth(int, int, int, int) getNoiseScaleDepth}
     * and {@link #sampleNoiseOffset(int, double, double) getNoiseOffset}.
     * 
     * @param buffer Buffer of size noiseSizeY + 1 to store noise column
     * @param startNoiseX x-coordinate start of chunk in noise coordinates.
     * @param startNoiseZ z-coordinate start of chunk in noise coordinates.
     * @param localNoiseX Current subchunk index along x-axis.
     * @param localNoiseZ Current subchunk index along z-axis.
     */
    protected void sampleNoiseColumn(double[] buffer, int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ) {
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        double coordinateScale = this.settings.coordinateScale;
        double heightScale = this.settings.heightScale;
        
        double mainNoiseScaleX = this.settings.mainNoiseScaleX;
        double mainNoiseScaleY = this.settings.mainNoiseScaleY;
        double mainNoiseScaleZ = this.settings.mainNoiseScaleZ;
        
        double lowerLimitScale = this.settings.lowerLimitScale;
        double upperLimitScale = this.settings.upperLimitScale;
        
        NoiseScaleDepth noiseScaleDepth = this.sampleNoiseScaleDepth(startNoiseX, startNoiseZ, localNoiseX, localNoiseZ);
        double scale = noiseScaleDepth.scale;
        double depth = noiseScaleDepth.depth;

        for (int noiseY = 0; noiseY < buffer.length; ++noiseY) {
            double density;
            double densityOffset = this.sampleNoiseOffset(noiseY, scale, depth);
    
            double mainNoise = (this.mainOctaveNoise.sample(
                noiseX, noiseY, noiseZ,
                coordinateScale / mainNoiseScaleX, 
                heightScale / mainNoiseScaleY, 
                coordinateScale / mainNoiseScaleZ
            ) / 10.0 + 1.0) / 2.0;
            
            if (mainNoise < 0.0) {
                density = this.minLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / lowerLimitScale;
                
            } else if (mainNoise > 1.0) {
                density = this.maxLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / upperLimitScale;
                
            } else {
                double minLimitNoise = this.minLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / lowerLimitScale;
                
                double maxLimitNoise = this.maxLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / upperLimitScale;
                
                density = minLimitNoise + (maxLimitNoise - minLimitNoise) * mainNoise;
            }
            
            buffer[noiseY] = density - densityOffset;
        }
    }
    
    /**
     * Samples the scale and depth values at startNoiseX + localNoiseX, startNoiseZ + localNoiseZ.
     * The startNoise and localNoise values should be added to produce the actual noise coordinate; they are kept separate for calculating accurate Beta/PE generation.
     * 
     * @param startNoiseX x-coordinate start of chunk in noise coordinates.
     * @param startNoiseZ z-coordinate start of chunk in noise coordinates.
     * @param localNoiseX Current subchunk index along x-axis.
     * @param localNoiseZ Current subchunk index along z-axis.
     * @return A NoiseScaleDepth containing the sampled scaled and depth values.
     */
    protected abstract NoiseScaleDepth sampleNoiseScaleDepth(int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ);
    
    /**
     * Samples the noise offset at the given noise y-coordinate.
     * 
     * @param noiseY y-coordinate in noise coordinates.
     * @param scale The terrain scale modifier.
     * @param depth The terrain depth modifier.
     * @return The offset by which to modify the density value at this noise y-coordinate.
     */
    protected abstract double sampleNoiseOffset(int noiseY, double scale, double depth);

    /**
     * Generates the base terrain for a given chunk.
     * 
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @param structureComponents The list of structure components in the given chunk
     */
    private void generateTerrain(ChunkPrimer chunkPrimer, int chunkX, int chunkZ, List<StructureComponent> structureComponents) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        int sizeX = this.horizontalNoiseResolution * this.noiseSizeX;
        int sizeZ = this.horizontalNoiseResolution * this.noiseSizeZ;
        int sizeY = this.verticalNoiseResolution * this.noiseSizeY;
        
        StructureWeightSampler weightSampler = new StructureWeightSampler(structureComponents);
        DensityChunk densityChunk = this.densityCache.get(chunkX, chunkZ);
        
        // Build block source rules
        BlockSourceRules blockSources = new BlockSourceRules.Builder(this.defaultBlock)
            .add(this.getInitialBlockSource(densityChunk, weightSampler))
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
        short worldMinY = 0;
        short worldHeight = (short)this.worldHeight;
        short minStructureHeight = 32;
        
        short[] heightmapSurface = new short[256];
        short[] heightmapOcean = new short[256];
        short[] heightmapFloor = new short[256];
        short[] heightmapStructure = new short[256];
        
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
                        
                        // Capture structure height at lowest possible solid block height,
                        // if above a certain height.
                        if (height >= 8) {
                            heightmapStructure[ndx] = height;
                        }
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
                    
                    // If no solid ground found (i.e. Skylands-style world types),
                    // then place structure height at 32.
                    if (height == 0 && heightmapStructure[ndx] == 0) {
                        heightmapStructure[ndx] = minStructureHeight;
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
     * @return NoiseSource containing initial noise values for the chunk.
     */
    private NoiseSource createInitialNoiseSource() {
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
     * @param noiseSource Primary noise provider to sample density noise.
     * @param blockSource Default block source
     * 
     * @return BlockSource to sample blockstate at x/y/z block coordinates.
     */
    private BlockSource getInitialBlockSource(DensityChunk densityChunk, StructureWeightSampler weightSampler) {
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        return (x, y, z) -> {
            IBlockState blockState = BlockStates.AIR;
            double density = densityChunk.sample(x, y, z);
            
            density = MathHelper.clamp(density / 200.0, -1.0, 1.0);
            density = density / 2.0 - density * density * density / 24.0;
            
            density += weightSampler.sample(this, mutablePos.setPos(x, y, z));
            
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
        
        List<NoiseSampler> noiseSamplers = this.noiseSamplers.entrySet()
            .stream()
            .map(e -> e.getValue())
            .collect(Collectors.toCollection(LinkedList::new));

        // Create noise sources and sample.
        Map<ResourceLocation, NoiseSource> noiseSources = new LinkedHashMap<>(this.noiseSources);
        noiseSources.put(DensityChunk.INITIAL, this.createInitialNoiseSource());
        noiseSources.entrySet().forEach(entry -> entry.getValue().sampleInitialNoise(
            chunkX * this.noiseSizeX,
            chunkZ * this.noiseSizeZ,
            this.noiseSettings,
            entry.getKey().equals(DensityChunk.INITIAL) ? noiseSamplers : ImmutableList.of()
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
    
    protected static class NoiseScaleDepth {
        public static final NoiseScaleDepth ZERO = new NoiseScaleDepth(0.0, 0.0);
        
        public final double scale;
        public final double depth;
        
        public NoiseScaleDepth(double scale, double depth) {
            this.scale = scale;
            this.depth = depth;
        }
    }
}
