package mod.bespectacled.modernbetaforge.api.world.chunk.surface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.compat.BiomeCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeLists;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class SurfaceBuilder {
    protected final IBlockState defaultBlock;
    protected final IBlockState defaultFluid;
    
    protected final ChunkSource chunkSource;
    protected final ModernBetaGeneratorSettings settings;
    
    private final SimplexOctaveNoise vanillaSurfaceOctaveNoise;
    @Deprecated private final PerlinOctaveNoise defaultBeachOctaveNoise;
    @Deprecated private final PerlinOctaveNoise defaultSurfaceOctaveNoise;

    // Set for specifying which biomes should use their vanilla surface builders.
    // Done on per-biome basis for best mod compatibility.
    private final Set<Biome> biomesWithCustomSurfaces = new HashSet<Biome>(
        ModernBetaBiomeLists.BUILTIN_BIOMES_WITH_CUSTOM_SURFACES
    );
    
    /**
     * Constructs a new abstract SurfaceBuilder with basic surface information.
     * 
     * @param chunkSource Associated chunkSource object.
     * @param settings The generator settings.
     */
    public SurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.defaultBlock = BlockStates.STONE;
        this.defaultFluid = settings.useLavaOceans ? BlockStates.LAVA : BlockStates.WATER;
        
        this.chunkSource = chunkSource;
        this.settings = settings;
        
        Random random = new Random(chunkSource.getSeed());
        this.vanillaSurfaceOctaveNoise = new SimplexOctaveNoise(random, 4);
        this.defaultBeachOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        this.defaultSurfaceOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        
        // Init custom/vanilla surface info
        this.biomesWithCustomSurfaces.addAll(
            Arrays.asList(ModernBetaConfig.generatorOptions.biomesWithCustomSurfaces)
                .stream()
                .map(id -> BiomeUtil.getBiome(new ResourceLocation(id), "custom_surfaces"))
                .collect(Collectors.toList())
        );
        
        // Init modded surface info
        for (Entry<String, Compat> entry : ModCompat.LOADED_MODS.entrySet()) {
            Compat compat = entry.getValue();
            if (compat instanceof BiomeCompat) {
                BiomeCompat biomeCompat = (BiomeCompat)compat;
                this.biomesWithCustomSurfaces.addAll(biomeCompat.getCustomSurfaces());
            }
        }
    }
    
    /**
     * Replace default blocks with biome-specific topsoil blocks and set in the chunk.
     * 
     * @param world The world object.
     * @param biomes Array of biomes in the chunk.
     * @param chunkPrimer Blockstate data for the chunk.
     * @param chunkX x-coordinate in chunk coordinates.
     * @param chunkZ z-coordinate in chunk coordinates.
     */
    public abstract void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    /**
     * Determines whether bedrock should generate at the given y-level.
     * 
     * @param y y-coordinate in block coordinates
     * @param random The random used to vary bedrock.
     * @return Whether bedrock should generate at the given coordinates.
     */
    public boolean generatesBedrock(int y, Random random) {
        return this.useBedrock() && y <= random.nextInt(5);
    }
    
    /**
     * Get a new Random object initialized with chunk coordinates for seed, for surface generation.
     * 
     * @param chunkX x-coordinate in chunk coordinates.
     * @param chunkZ z-coordinate in chunk coordinates.
     * @return New Random object initialized with chunk coordinates for seed.
     */
    public Random createSurfaceRandom(int chunkX, int chunkZ) {
        long seed = (long)chunkX * 0x4f9939f508L + (long)chunkZ * 0x1ef1565bd5L;
        
        return new Random(seed);
    }

    /**
     * Checks whether a given biome is within the set of biomes using custom surfaces.
     * @deprecated This is no longer used.
     * 
     * @param biome The biome to check.
     * @return Whether the given biome uses a custom surface.
     */
    public boolean isCustomSurface(Biome biome) {
        return this.biomesWithCustomSurfaces.contains(biome);
    }

    /**
     * Samples the surface depth at the given coordinates.
     * @deprecated This method has been moved to {@link NoiseSurfaceBuilder}.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param random The random used to vary surface transition.
     * @return The surface depth used to determine the depth of topsoil.
     */
    @Deprecated
    public int sampleSurfaceDepth(int x, int z, Random random) {
        return Integer.MIN_VALUE;
    }

    /**
     * Determines whether stone basins should generate given the surface depth.
     * @deprecated This method has been moved to {@link NoiseSurfaceBuilder}.
     * 
     * @param surfaceDepth The surface depth noise value.
     * @return Whether a stone basin should generate at the given coordinates.
     */
    @Deprecated
    public boolean generatesBasin(int surfaceDepth) {
        return false;
    }

    /**
     * Gets whether the surface builder generates beaches.
     * @deprecated This was previously used just for the map previewer. Don't use this.
     * 
     * @return Whether the surface builder generates beaches. False by default.
     */
    @Deprecated
    public boolean generatesBeaches() {
        return false;
    }
    
    /**
     * Gets the world height, from generator settings.
     * 
     * @return The world height.
     */
    protected int getWorldHeight() {
        return this.settings.height;
    }
    
    /**
     * Gets the sea level, from the chunk source.
     * 
     * @return The sea level.
     */
    protected int getSeaLevel() {
        return this.chunkSource.getSeaLevel();
    }
    
    /**
     * Indicates whether sandstone should be generated underneath beaches or deserts, from the generator settings.
     * 
     * @return Whether sandstone should be generated.
     */
    protected boolean useSandstone() {
        return this.settings.useSandstone;
    }
    
    /**
     * Indicates whether bedrock should be generated at the bottom of the world.
     * Probably won't add a 'useBedrock' setting to maintain congruous bedrock generation when using custom surfaces.
     * 
     * @return Whether bedrock should be generated.
     */
    protected boolean useBedrock() {
        return true;
    }

    /**
     * Use a biome-specific surface builder, at a given x/z-coordinate and topmost y-coordinate.
     * Valid biomes are checked on per-biome basis using identifier from {@link #biomesWithCustomSurfaces} set.
     * 
     * @param world The world object.
     * @param biome Biome with surface builder to use.
     * @param chunkPrimer Chunk primer.
     * @param random Random
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param override Force usage of vanilla surface builder.
     * 
     * @return True if biome is included in valid biomes set and has run surface builder. False if not included and not run.
     * 
     */
    protected boolean useCustomSurfaceBuilder(World world, Biome biome, ChunkPrimer chunkPrimer, Random random, int x, int z, boolean override) {
        if (this.biomesWithCustomSurfaces.contains(biome) || override) {
            double surfaceNoise = this.vanillaSurfaceOctaveNoise.sample(x, z, 0.0625, 0.0625, 1.0);
            
            // Reverse x/z because ??? why is it done this way in the surface gen code ????
            // Special surfaces won't properly generate if x/z are provided in the correct order, because WTF?!
            biome.genTerrainBlocks(world, random, chunkPrimer, z, x, surfaceNoise);
            
            return true;
        }
        
        return false;
    }

    /**
     * Gets the PerlinOctaveNoise sampler used for beach generation.
     * Will try to use the sampler from {@link ChunkSource#getBeachOctaveNoise() getBeachOctaveNoise} if possible, otherwise a default sampler.
     * @deprecate This method has been moved to {@link NoiseSurfaceBuilder}.
     * 
     * @return The noise sampler.
     */
    @Deprecated
    protected PerlinOctaveNoise getBeachOctaveNoise() {
        return this.chunkSource.getBeachOctaveNoise().orElse(this.defaultBeachOctaveNoise);
    }

    /**
     * Gets the PerlinOctaveNoise sampler used for surface generation.
     * Will try to use the sampler from {@link ChunkSource#getSurfaceOctaveNoise() getSurfaceOctaveNoise} if possible, otherwise a default sampler.
     * @deprecate This method has been moved to {@link NoiseSurfaceBuilder}.
     *
     * @return The noise sampler.
     */
    @Deprecated
    protected PerlinOctaveNoise getSurfaceOctaveNoise() {
        return this.chunkSource.getSurfaceOctaveNoise().orElse(this.defaultSurfaceOctaveNoise);
    }
}
