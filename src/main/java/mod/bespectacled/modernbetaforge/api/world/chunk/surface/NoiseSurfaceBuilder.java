package mod.bespectacled.modernbetaforge.api.world.chunk.surface;

import java.util.Random;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public abstract class NoiseSurfaceBuilder extends SurfaceBuilder {
    private final PerlinOctaveNoise defaultBeachOctaveNoise;
    private final PerlinOctaveNoise defaultSurfaceOctaveNoise;
    
    private final boolean useBedrockRandom;
    private final boolean useSandstoneRandom;
    private final boolean swapCoords;

    /**
     * Constructs a new abstract NoiseSurfaceBuilder.
     * 
     * @param chunkSource The chunkSource object.
     * @param settings The generator settings.
     * @param useBedrockRandom Whether a separate Random should be used to generate bedrock.
     * @param useSandstoneRandom Whether a separate Random should be used to generate sandstone.
     * @param swapCoords Whether to swap x/z-coordinates when sampling noise.
     */
    public NoiseSurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings, boolean useBedrockRandom, boolean useSandstoneRandom, boolean swapCoords) {
        super(chunkSource, settings);
        
        Random random = new Random(chunkSource.getSeed());
        this.defaultBeachOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        this.defaultSurfaceOctaveNoise = new PerlinOctaveNoise(random, 4, true);

        this.useBedrockRandom = useBedrockRandom;
        this.useSandstoneRandom = useSandstoneRandom;
        this.swapCoords = swapCoords;
    }
    
    @Override
    public final void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);
        Random bedrockRandom = this.createSurfaceRandom(chunkX, chunkZ);
        Random sandstoneRandom = this.createSurfaceRandom(chunkX, chunkZ);
        
        BiConsumer<Integer, Integer> surfaceGenerator = (localX, localZ) -> {
            int x = startX + localX;
            int z = startZ + localZ;
            
            boolean isBeach = this.isBeach(x, z, random);
            boolean isGravelBeach = this.isGravelBeach(x, z, random);
            
            int surfaceDepth = this.sampleSurfaceDepth(x, z, random);
            int runDepth = -1;
            
            Biome biome = biomes[localX + localZ * 16];
            boolean isNether = this.isNether(biome);

            IBlockState topBlock = biome.topBlock;
            IBlockState fillerBlock = biome.fillerBlock;
            
            // Skip if used custom surface generation
            if (this.useCustomSurfaceBuilder(world, biome, chunkPrimer, random, x, z, false)) {
                return;
            }

            for (int y = this.getWorldHeight() - 1; y >= 0; y--) {
                if (this.isBedrock(y, this.useBedrockRandom ? bedrockRandom : random)) {
                    chunkPrimer.setBlockState(localX, y, localZ, BlockStates.BEDROCK);
                    continue;
                }

                IBlockState blockState = chunkPrimer.getBlockState(localX, y, localZ);

                if (BlockStates.isAir(blockState)) {
                    runDepth = -1;
                    continue;
                }

                if (!BlockStates.isEqual(blockState, this.defaultBlock)) {
                    continue;
                }

                if (runDepth == -1) {
                    if (this.isBasin(surfaceDepth)) {
                        topBlock = BlockStates.AIR;
                        fillerBlock = this.defaultBlock;
                        
                    } else if (this.atBeachDepth(y)) {
                        topBlock = biome.topBlock;
                        fillerBlock = biome.fillerBlock;

                        if (isGravelBeach) {
                            topBlock = this.getGravelBeachTopBlock(biome, isNether);
                            fillerBlock = this.getGravelBeachFillerBlock(biome, isNether);
                        }

                        if (isBeach) {
                            IBlockState beachBlock = this.getBeachBlock(biome, isNether);
                            
                            topBlock = beachBlock;
                            fillerBlock = beachBlock;
                        }
                    }

                    if (y < this.getSeaLevel() && BlockStates.isAir(topBlock)) {
                        topBlock = this.defaultFluid;
                    }

                    runDepth = surfaceDepth;
                    
                    if (y >= this.getSeaLevel() - 1) {
                        chunkPrimer.setBlockState(localX, y, localZ, topBlock);
                    } else {
                        chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);
                    }

                    continue;
                }

                if (runDepth <= 0) {
                    continue;
                }

                runDepth--;
                chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);

                // Generates layer of sandstone starting at lowest block of sand, of height 1 to 4.
                if (this.useSandstone() && runDepth == 0 && BlockStates.isEqual(fillerBlock, BlockStates.SAND)) {
                    runDepth = this.useSandstoneRandom ? sandstoneRandom.nextInt(4) : random.nextInt(4);
                    fillerBlock = fillerBlock.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ?
                        BlockStates.RED_SANDSTONE :
                        BlockStates.SANDSTONE;
                }
            }
        };

        if (this.swapCoords) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    surfaceGenerator.accept(localX, localZ);
                }
            }
            
        } else {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localX = 0; localX < 16; localX++) {
                    surfaceGenerator.accept(localX, localZ);
                }
            }
            
        }
    }

    /**
     * Determines whether beaches should generate at the given coordinates.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param random The random used to vary surface transition. Can be null if transitions aren't implemented.
     * @return Whether beaches should generate at the given coordinates.
     */
    public abstract boolean isBeach(int x, int z, @Nullable Random random);

    /**
     * Determines whether gravel beaches should generate at the given coordinates.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param random The random used to vary surface transition. Can be null if transitions aren't implemented.
     * @return Whether gravel beaches should generate at the given coordinates.
     */
    public abstract boolean isGravelBeach(int x, int z, @Nullable Random random);
    
    /**
     * Samples the surface depth at the given coordinates.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param random The random used to vary surface transition. Can be null if transitions aren't implemented.
     * @return The surface depth used to determine the depth of topsoil.
     */
    public abstract int sampleSurfaceDepth(int x, int z, @Nullable Random random);
    
    /**
     * Determines whether stone basins should generate given the surface depth.
     * 
     * @param surfaceDepth The surface depth noise value.
     * @return Whether a stone basin should generate at the given coordinates.
     */
    public abstract boolean isBasin(int surfaceDepth);
    
    /**
     * Determines whether the y-value is the correct height for generating beaches.
     * 
     * @param y x-coordinate in block coordinates.
     * @return Whether beaches should generate at the given y-value.
     */
    public boolean atBeachDepth(int y) {
        return y >= this.getSeaLevel() - 4 && y <= this.getSeaLevel() + 1;
    }
    
    /**
     * Gets the PerlinOctaveNoise sampler used for beach generation.
     * Will try to use the sampler from {@link ChunkSource#getBeachOctaveNoise() getBeachOctaveNoise} if possible, otherwise a default sampler.
     * 
     * @return The noise sampler.
     */
    protected PerlinOctaveNoise getBeachOctaveNoise() {
        return this.chunkSource.getBeachOctaveNoise().orElse(this.defaultBeachOctaveNoise);
    }
    
    /**
     * Gets the PerlinOctaveNoise sampler used for surface generation.
     * Will try to use the sampler from {@link ChunkSource#getSurfaceOctaveNoise() getSurfaceOctaveNoise} if possible, otherwise a default sampler.
     *
     * @return The noise sampler.
     */
    protected PerlinOctaveNoise getSurfaceOctaveNoise() {
        return this.chunkSource.getSurfaceOctaveNoise().orElse(this.defaultSurfaceOctaveNoise);
    }
    
    /**
     * Gets the random variation for generating edges of surfaces.
     * 
     * @param random The random to sample. Can be null.
     * @return A double value for varying surface transition. If {@code random} is null then returns 0.0.
     */
    protected double getSurfaceVariation(@Nullable Random random) {
        return random != null ? random.nextDouble() : 0.0;
    }
    
    /**
     * Gets the top blockstate for gravel beach generation.
     * 
     * @param biome The biome to check for selecting the blockstate.
     * @param isNether Whether the biome is a Nether biome.
     * @return The top block for gravel beach generation.
     */
    private IBlockState getGravelBeachTopBlock(Biome biome, boolean isNether) {
        if (isNether) {
            return BlockStates.GRAVEL;
        }
        
        return BlockStates.AIR;
    }
    
    /**
     * Gets the filler blockstate for gravel beach generation.
     * 
     * @param biome The biome to check for selecting the blockstate.
     * @param isNether Whether the biome is a Nether biome.
     * @return The filler blockstate for gravel beach generation.
     */
    private IBlockState getGravelBeachFillerBlock(Biome biome, boolean isNether) {
        if (isNether) {
            return this.defaultBlock;
        }
        
        return BlockStates.GRAVEL;
    }

    /**
     * Gets the blockstate for beach generation.
     * 
     * @param biome The biome to check for selecting the blockstate.
     * @param isNether Whether the biome is a Nether biome.
     * @return The blockstate for beach generation.
     */
    private IBlockState getBeachBlock(Biome biome, boolean isNether) {
        if (isNether) {
            return Blocks.SOUL_SAND.getDefaultState();
        }
        
        return BlockStates.SAND;
    }
    
    /*
     * Gets whether the biome should be treated as the Nether for purpose of beach generation.
     * 
     * @param biome The biome to check.
     * @return Whether to treat the current region as the Nether.
     */
    private boolean isNether(Biome biome) {
        return BiomeDictionary.hasType(biome, Type.NETHER);
    }
}
