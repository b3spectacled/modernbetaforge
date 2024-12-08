package mod.bespectacled.modernbetaforge.api.world.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings.SlideSettings;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceRules;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class NoiseChunkSource extends ChunkSource {
    protected final int verticalNoiseResolution;   // Number of blocks in a vertical subchunk
    protected final int horizontalNoiseResolution; // Number of blocks in a horizontal subchunk 
    
    protected final int noiseSizeX; // Number of horizontal subchunks along x
    protected final int noiseSizeZ; // Number of horizontal subchunks along z
    protected final int noiseSizeY; // Number of vertical subchunks
    protected final int noiseTopY;  // Number of positive (y >= 0) vertical subchunks
    
    protected final SlideSettings topSlide;
    protected final SlideSettings bottomSlide;
    
    protected final ChunkCache<NoiseSource> noiseCache;
    protected final ChunkCache<HeightmapChunk> heightmapCache;

    private final SurfaceBuilder surfaceBuilder;

    public NoiseChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings settings,
        ModernBetaNoiseSettings noiseSettings,
        long seed,
        boolean mapFeaturesEnabled
    ) {
        super(world, chunkGenerator, settings, noiseSettings, seed, mapFeaturesEnabled);
        
        this.verticalNoiseResolution = noiseSettings.sizeVertical * 4;
        this.horizontalNoiseResolution = noiseSettings.sizeHorizontal * 4;
        
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = Math.floorDiv(this.worldHeight, this.verticalNoiseResolution);
        this.noiseTopY = Math.floorDiv(this.worldHeight, this.verticalNoiseResolution);
        
        this.topSlide = noiseSettings.topSlideSettings;
        this.bottomSlide = noiseSettings.bottomSlideSettings;
        
        this.noiseCache = new ChunkCache<>("noise", this::sampleInitialNoise);
        this.heightmapCache = new ChunkCache<>("heightmap", this::sampleHeightmap);

        this.surfaceBuilder = ModernBetaRegistries.SURFACE
            .getOrElse(settings.surfaceBuilder, ModernBetaBuiltInTypes.Surface.BETA.id)
            .apply(world, this, settings);
    }

    
    /**
     * Create base chunk (only stone and water is set) given chunk coordinates.
     *
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * 
     */
    @Override
    public void provideBaseChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        this.generateTerrain(chunkPrimer, chunkX, chunkZ);
    }
    
    /**
     * Build surface for given chunk primer and chunk coordinates.
     *
     * @param biomes Biome array for chunk
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * 
     */
    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        this.surfaceBuilder.provideSurface(biomes, chunkPrimer, chunkX, chunkZ);
    }
    
    /**
     * Sample height at given x/z coordinate. Initially generates heightmap for entire chunk, 
     * if chunk containing x/z coordinates has never been sampled.
     *
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type HeightmapChunk heightmap type.
     * 
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
     * Generates noise for a column at startNoiseX + localNoiseX / startNoiseZ + localNoiseZ.
     * 
     * @param buffer Buffer of size noiseSizeY + 1 to store noise column
     * @param startNoiseX x-coordinate start of chunk in noise coordinates.
     * @param startNoiseZ z-coordinate start of chunk in noise coordinates.
     * @param localNoiseX Current subchunk index along x-axis.
     * @param localNoiseZ Current subchunk index along z-axis.
     */
    protected abstract void sampleNoiseColumn(double[] buffer, int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ);
    
    /**
     * Interpolates density to set terrain curve at top and bottom of the world.
     * 
     * @param density Base density.
     * @param noiseY y-coordinate in noise coordinates.
     * 
     * @return Modified noise density.
     */
    protected double applySlides(double density, int noiseY) {
        if (this.topSlide.slideSize > 0.0) {
            double delta = ((double)(this.noiseSizeY - noiseY) - this.topSlide.slideOffset) / this.topSlide.slideSize;
            density = MathUtil.clampedLerp(this.topSlide.slideTarget, density, delta);
        }
        
        if (this.bottomSlide.slideSize > 0.0) {
            double delta = ((double)noiseY - this.bottomSlide.slideOffset) / this.bottomSlide.slideSize;
            density = MathUtil.clampedLerp(this.bottomSlide.slideTarget, density, delta);
        }
        
        return density;
    }
    
    /**
     * Generates the base terrain for a given chunk.
     * 
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    private void generateTerrain(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        // Create and populate noise providers
        List<NoiseSource> noiseSources = new ArrayList<>();
        
        NoiseSource baseNoiseSource = this.noiseCache.get(chunkX, chunkZ);
        BlockSource baseBlockSource = this.getBaseBlockSource(baseNoiseSource);
        
        // Create and populate block sources
        BlockSourceRules blockSources = new BlockSourceRules.Builder()
            .add(baseBlockSource)
            .build();

        // Sample initial noise.
        // Base noise should be added after this,
        // since base noise is sampled when fetched from cache.
        noiseSources.forEach(noiseSource -> noiseSource.sampleInitialNoise(chunkX * this.noiseSizeX, chunkZ * this.noiseSizeZ));
        noiseSources.add(baseNoiseSource);
        
        for (int subChunkX = 0; subChunkX < this.noiseSizeX; ++subChunkX) {
            int noiseX = subChunkX;
            
            for (int subChunkZ = 0; subChunkZ < this.noiseSizeZ; ++subChunkZ) {
                int noiseZ = subChunkZ;
                
                for (int subChunkY = 0; subChunkY < this.noiseSizeY; ++subChunkY) {
                    int noiseY = subChunkY;
                    
                    noiseSources.forEach(noiseProvider -> noiseProvider.sampleNoiseCorners(noiseX, noiseY, noiseZ));
                    
                    for (int subY = 0; subY < this.verticalNoiseResolution; ++subY) {
                        int y = subY + subChunkY * this.verticalNoiseResolution;

                        double deltaY = subY / (double)this.verticalNoiseResolution;
                        noiseSources.forEach(noiseProvider -> noiseProvider.sampleNoiseY(deltaY));
                        
                        for (int subX = 0; subX < this.horizontalNoiseResolution; ++subX) {
                            int localX = subX + subChunkX * this.horizontalNoiseResolution;
                            int x = startX + localX;
                            
                            double deltaX = subX / (double)this.horizontalNoiseResolution;
                            noiseSources.forEach(noiseProvider -> noiseProvider.sampleNoiseX(deltaX));
                            
                            for (int subZ = 0; subZ < this.horizontalNoiseResolution; ++subZ) {
                                int localZ = subZ + subChunkZ * this.horizontalNoiseResolution;
                                int z = startZ + localZ;
                                
                                double deltaZ = subZ / (double)this.horizontalNoiseResolution;
                                noiseSources.forEach(noiseProvider -> noiseProvider.sampleNoiseZ(deltaZ));
                                
                                IBlockState blockState = blockSources.sample(x, y, z);
                                if (blockState.equals(BlockStates.AIR)) continue;
                                
                                chunkPrimer.setBlockState(localX, y, localZ, blockState);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Sample initial noise for a given chunk.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * 
     * @return NoiseSource containing initial noise values for the chunk.
     */
    private NoiseSource sampleInitialNoise(int chunkX, int chunkZ) {
        NoiseSource noiseSource = new NoiseSource(
            this::sampleNoiseColumn,
            this.noiseSizeX,
            this.noiseSizeY,
            this.noiseSizeZ
        );
        
        noiseSource.sampleInitialNoise(chunkX * this.noiseSizeX, chunkZ * this.noiseSizeZ);
        
        return noiseSource;
    }


    /**
     * Generates a heightmap for the chunk containing the given x/z coordinates
     * and returns to {@link #getHeight(int, int, net.minecraft.world.Heightmap.Type)} 
     * to cache and return the height.
     * 
     * @param chunkX x-coordinate in chunk coordinates to sample all y-values for.
     * @param chunkZ z-coordinate in chunk coordinates to sample all y-values for.
     * 
     * @return A HeightmapChunk, containing an array of ints containing the heights for the entire chunk.
     */
    private HeightmapChunk sampleHeightmap(int chunkX, int chunkZ) {
        short minStructureHeight = 32;
        short minHeight = 0;
        short worldMinY = 0;
        short worldHeight = (short)this.worldHeight;
        
        NoiseSource noiseSource = this.noiseCache.get(chunkX, chunkZ);
        
        short[] heightmapSurface = new short[256];
        short[] heightmapOcean = new short[256];
        short[] heightmapFloor = new short[256];
        short[] heightmapStructure = new short[256];
        
        Arrays.fill(heightmapSurface, minHeight);
        Arrays.fill(heightmapOcean, minHeight);
        Arrays.fill(heightmapFloor, worldMinY);
        Arrays.fill(heightmapStructure, minStructureHeight);
        
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
                                
                                double density = noiseSource.sample();
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
                }
            }
        }
        
        // Construct new heightmap cache from generated heightmap array
        return new HeightmapChunk(heightmapSurface, heightmapOcean, heightmapFloor, heightmapStructure);
    }
    
    /**
     * Creates block source to sample BlockState at block coordinates given base noise provider.
     * 
     * @param noiseSource Primary noise provider to sample density noise.
     * @param blockSource Default block source
     * 
     * @return BlockSource to sample blockstate at x/y/z block coordinates.
     */
    private BlockSource getBaseBlockSource(NoiseSource noiseSource) {
        return (x, y, z) -> {
            double density = noiseSource.sample();
            
            IBlockState blockState = BlockStates.AIR;
            if (density > 0.0) {
                blockState = this.defaultBlock;
            } else if (y < this.getSeaLevel()) {
                blockState = this.defaultFluid;
            }
            
            return blockState;
        };
    }
}
