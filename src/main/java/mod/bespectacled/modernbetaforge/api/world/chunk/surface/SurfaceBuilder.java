package mod.bespectacled.modernbetaforge.api.world.chunk.surface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeLists;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class SurfaceBuilder {
    protected final World world;
    protected final ModernBetaChunkGeneratorSettings settings;
    protected final NoiseChunkSource chunkSource;
    protected final IBlockState defaultBlock;
    protected final IBlockState defaultFluid;
    
    private final SimplexOctaveNoise surfaceOctaveNoise;

    // Set for specifying which biomes should use their vanilla surface builders.
    // Done on per-biome basis for best mod compatibility.
    private final Set<Biome> biomesWithCustomSurfaces = new HashSet<Biome>(
        ModernBetaBiomeLists.BUILTIN_BIOMES_WITH_CUSTOM_SURFACES
    );
    
    public SurfaceBuilder(World world, NoiseChunkSource chunkSource, ModernBetaChunkGeneratorSettings settings) {
        this.world = world;
        this.chunkSource = chunkSource;
        this.settings = settings;
        this.defaultBlock = BlockStates.STONE;
        this.defaultFluid = BlockStates.WATER;
        
        this.surfaceOctaveNoise = new SimplexOctaveNoise(new Random(world.getWorldInfo().getSeed()), 4);
        
        // Init custom/vanilla surface info
        this.biomesWithCustomSurfaces.addAll(
            Arrays.asList(ModernBetaConfig.generatorOptions.biomesWithCustomSurfaces)
                .stream()
                .map(id -> BiomeUtil.getBiome(id, "custom surface config"))
                .collect(Collectors.toList())
        );
    }
    
    public abstract void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
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
     * 
     * @param biome Biome with surface builder to use.
     * @param biomeId Biome identifier, used to check if it uses valid custom surface builder.
     * @param region
     * @param chunk
     * @param random
     * @param mutable Mutable BlockPos at block coordinates position.
     * 
     * @return True if biome is included in valid biomes set and has run surface builder. False if not included and not run.
     */
    protected boolean useCustomSurfaceBuilder(Biome biome, ChunkPrimer chunkPrimer, Random random, int x, int z) {
        if (this.biomesWithCustomSurfaces.contains(biome)) {
            double surfaceNoise = this.surfaceOctaveNoise.sample(x, z, 0.0625, 0.0625, 1.0);
            
            // Reverse x/z because ??? why is it done this way in the surface gen code ????
            // Special surfaces won't properly generate if x/z are provided in the correct order, because WTF?!
            biome.genTerrainBlocks(this.world, random, chunkPrimer, z, x, surfaceNoise);
            
            return true;
        }
        
        return false;
    }
}
