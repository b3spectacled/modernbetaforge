package mod.bespectacled.modernbetaforge.api.world.chunk.surface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.compat.BiomeCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeLists;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class SurfaceBuilder {
    protected final IBlockState defaultBlock;
    protected final IBlockState defaultFluid;
    
    private final World world;
    private final ChunkSource chunkSource;
    private final ModernBetaChunkGeneratorSettings settings;
    private final SimplexOctaveNoise vanillaSurfaceOctaveNoise;
    private final PerlinOctaveNoise defaultBeachOctaveNoise;
    private final PerlinOctaveNoise defaultSurfaceOctaveNoise;

    // Set for specifying which biomes should use their vanilla surface builders.
    // Done on per-biome basis for best mod compatibility.
    private final Set<Biome> biomesWithCustomSurfaces = new HashSet<Biome>(
        ModernBetaBiomeLists.BUILTIN_BIOMES_WITH_CUSTOM_SURFACES
    );
    
    public SurfaceBuilder(World world, ChunkSource chunkSource, ModernBetaChunkGeneratorSettings settings) {
        this.defaultBlock = BlockStates.STONE;
        this.defaultFluid = settings.useLavaOceans ? BlockStates.LAVA : BlockStates.WATER;
        
        this.world = world;
        this.chunkSource = chunkSource;
        this.settings = settings;
        
        Random random = new Random(world.getWorldInfo().getSeed());
        this.vanillaSurfaceOctaveNoise = new SimplexOctaveNoise(random, 4);
        this.defaultBeachOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        this.defaultSurfaceOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        
        // Init custom/vanilla surface info
        this.biomesWithCustomSurfaces.addAll(
            Arrays.asList(ModernBetaConfig.generatorOptions.biomesWithCustomSurfaces)
                .stream()
                .map(id -> BiomeUtil.getBiome(id, "custom surface config"))
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
     * @param biomes Array of biomes in the chunk.
     * @param chunkPrimer Blockstate data for the chunk.
     * @param chunkX x-coordinate in chunk coordinates.
     * @param chunkZ z-coordinate in chunk coordinates.
     * 
     * @return New Random object initialized with chunk coordinates for seed.
     */
    public abstract void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    protected PerlinOctaveNoise getBeachOctaveNoise() {
        return this.chunkSource.getBeachOctaveNoise().orElse(this.defaultBeachOctaveNoise);
    }
    
    protected PerlinOctaveNoise getSurfaceOctaveNoise() {
        return this.chunkSource.getSurfaceOctaveNoise().orElse(this.defaultSurfaceOctaveNoise);
    }
    
    protected int getWorldHeight() {
        return this.settings.height;
    }
    
    protected int getSeaLevel() {
        return this.chunkSource.getSeaLevel();
    }
    
    protected boolean useSandstone() {
        return this.settings.useSandstone;
    }
    
    /*
     * Probably won't add a 'useBedrock' setting to maintain congruous bedrock generation when using custom surfaces
     * 
     */
    protected boolean useBedrock() {
        return true;
    }
    
    /**
     * Get a new Random object initialized with chunk coordinates for seed, for surface generation.
     * 
     * @param chunkX x-coordinate in chunk coordinates.
     * @param chunkZ z-coordinate in chunk coordinates.
     * 
     * @return New Random object initialized with chunk coordinates for seed.
     */
    protected Random createSurfaceRandom(int chunkX, int chunkZ) {
        long seed = (long)chunkX * 0x4f9939f508L + (long)chunkZ * 0x1ef1565bd5L;
        
        return new Random(seed);
    } 
    
    /**
     * Use a biome-specific surface builder, at a given x/z-coordinate and topmost y-coordinate.
     * Valid biomes are checked on per-biome basis using identifier from BIOMES_WITH_CUSTOM_SURFACES set.
     * Also generates ocean floor surfaces if used with ReleaseChunkSource.
     * 
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
    protected boolean useCustomSurfaceBuilder(Biome biome, ChunkPrimer chunkPrimer, Random random, int x, int z, boolean override) {
        if (this.biomesWithCustomSurfaces.contains(biome) || override) {
            double surfaceNoise = this.vanillaSurfaceOctaveNoise.sample(x, z, 0.0625, 0.0625, 1.0);
            
            // Reverse x/z because ??? why is it done this way in the surface gen code ????
            // Special surfaces won't properly generate if x/z are provided in the correct order, because WTF?!
            biome.genTerrainBlocks(this.world, random, chunkPrimer, z, x, surfaceNoise);
            
            return true;
        }
        
        return false;
    }
}
