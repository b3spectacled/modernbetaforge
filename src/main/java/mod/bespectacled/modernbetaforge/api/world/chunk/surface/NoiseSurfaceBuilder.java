package mod.bespectacled.modernbetaforge.api.world.chunk.surface;

import java.util.Random;
import java.util.Set;
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
            
            boolean isPrimaryBeach = this.isPrimaryBeach(x, z, random);
            boolean isSecondaryBeach = this.isSecondaryBeach(x, z, random);
            
            int surfaceDepth = this.sampleSurfaceDepth(x, z, random);
            int runDepth = -1;
            
            Biome biome = biomes[localX + localZ * 16];
            Set<Type> types = BiomeDictionary.getTypes(biome);

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

                        if (isSecondaryBeach) {
                            topBlock = this.getSecondaryBeachTopBlock(types);
                            fillerBlock = this.getSecondaryBeachFillerBlock(types);
                        }

                        if (isPrimaryBeach) {
                            topBlock = this.getPrimaryBeachTopBlock(types);
                            fillerBlock = this.getPrimaryBeachFillerBlock(types);
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
     * Determines whether primary beaches should generate at the given coordinates.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param random The random used to vary surface transition. Can be null if transitions aren't implemented.
     * @return Whether primary beaches should generate at the given coordinates.
     */
    public abstract boolean isPrimaryBeach(int x, int z, @Nullable Random random);

    /**
     * Determines whether secondary beaches should generate at the given coordinates.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param random The random used to vary surface transition. Can be null if transitions aren't implemented.
     * @return Whether secondary beaches should generate at the given coordinates.
     */
    public abstract boolean isSecondaryBeach(int x, int z, @Nullable Random random);
    
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
     * Gets the top blockstate for primary beach generation.
     * 
     * @param types The set of {@link BiomeDictionary.Type biome types} that the biome may belong to.
     * @return The top blockstate for primary beach generation.
     */
    public IBlockState getPrimaryBeachTopBlock(Set<Type> types) {
        if (types.contains(Type.NETHER)) {
            return Blocks.SOUL_SAND.getDefaultState();
        }
        
        return BlockStates.SAND;
    }

    /**
     * Gets the filler blockstate for primary beach generation.
     * 
     * @param types The set of {@link BiomeDictionary.Type biome types} that the biome may belong to.
     * @return The filler blockstate for primary beach generation.
     */
    public IBlockState getPrimaryBeachFillerBlock(Set<Type> types) {
        if (types.contains(Type.NETHER)) {
            return Blocks.SOUL_SAND.getDefaultState();
        }
        
        return BlockStates.SAND;
    }

    /**
     * Gets the top blockstate for secondary beach generation.
     * 
     * @param types The set of {@link BiomeDictionary.Type biome types} that the biome may belong to.
     * @return The top block for secondary beach generation.
     */
    public IBlockState getSecondaryBeachTopBlock(Set<Type> types) {
        if (types.contains(Type.NETHER)) {
            return BlockStates.GRAVEL;
        }
        
        return BlockStates.AIR;
    }

    /**
     * Gets the filler blockstate for secondary beach generation.
     * 
     * @param types The set of {@link BiomeDictionary.Type biome types} that the biome may belong to.
     * @return The filler blockstate for secondary beach generation.
     */
    public IBlockState getSecondaryBeachFillerBlock(Set<Type> types) {
        if (types.contains(Type.NETHER)) {
            return this.defaultBlock;
        }
        
        return BlockStates.GRAVEL;
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
}
