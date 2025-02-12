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
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeLists;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class SurfaceBuilder {
    protected final IBlockState defaultBlock;
    protected final IBlockState defaultFluid;
    
    protected final ChunkSource chunkSource;
    protected final ModernBetaGeneratorSettings settings;
    
    private final SimplexOctaveNoise surfaceOctaveNoise;

    // Set for specifying which biomes should use their vanilla surface builders.
    // Done on per-biome basis for best mod compatibility.
    private final Set<Biome> biomesWithCustomSurfaces = new HashSet<>(
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
        this.surfaceOctaveNoise = new SimplexOctaveNoise(random, 4);

        // Init custom/vanilla surface info
        this.biomesWithCustomSurfaces.addAll(
            Arrays.asList(ModernBetaConfig.generatorOptions.biomesWithCustomSurfaces)
                .stream()
                .map(id -> ForgeRegistryUtil.getRegistryEntry(new ResourceLocation(id), ForgeRegistries.BIOMES))
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
    public boolean isBedrock(int y, Random random) {
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
     * 
     * @param biome The biome to check.
     * @return Whether the given biome uses a custom surface.
     */
    public boolean isCustomSurface(Biome biome) {
        return this.biomesWithCustomSurfaces.contains(biome);
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
            double surfaceNoise = this.surfaceOctaveNoise.sample(x, z, 0.0625, 0.0625, 1.0);
            
            // Reverse x/z because ??? why is it done this way in the surface gen code ????
            // Special surfaces won't properly generate if x/z are provided in the correct order, because WTF?!
            biome.genTerrainBlocks(world, random, chunkPrimer, z, x, surfaceNoise);
            
            return true;
        }
        
        return false;
    }
}
