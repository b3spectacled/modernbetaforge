package mod.bespectacled.modernbetaforge.compat.cubicchunks;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;

import io.github.opencubicchunks.cubicchunks.api.util.Box;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubeGeneratorsRegistry;
import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer;
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator;
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.ICubicWorldInternal;
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.core.common.IGameRegistry;
import io.github.opencubicchunks.cubicchunks.core.util.CompatHandler;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import io.github.opencubicchunks.cubicchunks.core.worldgen.WorldgenHangWatchdog;
import io.github.opencubicchunks.cubicchunks.core.worldgen.generator.vanilla.VanillaCompatibilityGenerator;
import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.util.DebugUtil;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkPrimerExtended;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class CubicModernBetaChunkGenerator extends VanillaCompatibilityGenerator implements ICubeGenerator {
    private static final int INITIAL_CHUNK_CAPACITY = 256;
    private static final int MAX_POPULATOR_EXCEPTIONS = 10;
    
    private final ModernBetaChunkGenerator chunkGenerator;
    private final World world;
    private final ChunkCache<ChunkPrimerExtended> chunkPrimerCache;;
    private final int cubeSizeY;
    private final int cubeMinY;
    private final int cubeTopY;
    
    private int populatorExceptions;
    
    public CubicModernBetaChunkGenerator(ModernBetaChunkGenerator chunkGenerator, World world) {
        super(chunkGenerator, world);
        
        this.chunkGenerator = chunkGenerator;
        this.world = world;
        this.chunkPrimerCache = new ChunkCache<>("initial_chunk", INITIAL_CHUNK_CAPACITY, this::generateChunkPrimer);
        this.cubeSizeY = Math.floorDiv(chunkGenerator.getChunkSource().getWorldHeight() - chunkGenerator.getChunkSource().getWorldFloor(), Cube.SIZE);
        this.cubeMinY = Math.floorDiv(chunkGenerator.getChunkSource().getWorldFloor(), Cube.SIZE);
        this.cubeTopY = Math.floorDiv(chunkGenerator.getChunkSource().getWorldHeight(), Cube.SIZE);
    }

    @Override
    public void generateColumn(Chunk chunk) {
        super.generateColumn(chunk);
    }
    
    @Override
    public CubePrimer generateCube(int cubeX, int cubeY, int cubeZ) {
        return this.generateCube(cubeX, cubeY, cubeZ, new CubePrimer());
    }

    @Override
    public CubePrimer generateCube(int cubeX, int cubeY, int cubeZ, CubePrimer cubePrimer) {
        try {
            WorldgenHangWatchdog.startWorldGen();
            ChunkPrimerExtended chunkPrimer = this.chunkPrimerCache.get(cubeX, cubeZ);

            if (cubeY < this.cubeMinY || cubeY >= this.cubeTopY) {
                // Do nothing and allow cubes outside of world bounds to be void.
            } else {
                for (int localZ = 0; localZ < Cube.SIZE; ++localZ) {
                    for (int localX = 0; localX < Cube.SIZE; ++localX) {
                        for (int localY = 0; localY < Cube.SIZE; ++localY) {
                            int y = cubeY * Cube.SIZE + localY;
                            
                            cubePrimer.setBlockState(localX, localY, localZ, chunkPrimer.getBlockState(localX, y, localZ));
                        }
                    }
                }
            }

            return cubePrimer;
        } finally {
            WorldgenHangWatchdog.endWorldGen();
        }
    }

    @Override
    public void populate(ICube cube) {
        try {
            int cubeX = cube.getX();
            int cubeY = cube.getY();
            int cubeZ = cube.getZ();
            
            int cubeTopY = this.cubeTopY;
            int cubeMinY = this.cubeMinY;
            
            WorldgenHangWatchdog.startWorldGen();
            Random random = getCubeSpecificRandom(cubeX, cubeY, cubeZ);
            CubeGeneratorsRegistry.populateVanillaCubic(this.world, random, cube);
            
            if (cubeY < cubeMinY || cubeY >= cubeTopY) {
                return;
            }
            
            if (cubeY >= cubeMinY || cubeY < cubeTopY) {
                for (int y = cubeTopY - 1; y >= cubeMinY; y--) {
                    ((ICubicWorldInternal)this.world).getCubeFromCubeCoords(cubeX, y, cubeZ).setPopulated(true);
                }

                try {
                    CompatHandler.beforePopulate(this.world, this.chunkGenerator);
                    this.chunkGenerator.populate(cubeX, cubeZ);
                    
                } catch (IllegalArgumentException e) {
                    this.logPopulatorExceptions(e);
                } finally {
                    CompatHandler.afterPopulate(this.world);
                }
                
                this.applyModGenerators(cubeX, cubeZ, this.world, this.chunkGenerator, this.world.getChunkProvider());
            }
            
        } finally {
            WorldgenHangWatchdog.endWorldGen();
        }
    }

    @Override
    public Box getPopulationPregenerationRequirements(ICube cube) {
        return super.getPopulationPregenerationRequirements(cube);
    }

    @Override
    public Box getFullPopulationRequirements(ICube cube) {
        return super.getFullPopulationRequirements(cube);
    }

    @Override
    public BlockPos getClosestStructure(String structureName, BlockPos pos, boolean findUnexplored) {
        return this.chunkGenerator.getNearestStructurePos(this.world, structureName, pos, findUnexplored);
    }

    @Override
    public void recreateStructures(ICube cube) { }

    @Override
    public void recreateStructures(Chunk chunk) {
        this.chunkGenerator.recreateStructures(chunk, chunk.x, chunk.z);
    }
    
    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return this.chunkGenerator.getPossibleCreatures(creatureType, pos);
    }
    
    private ChunkPrimerExtended generateChunkPrimer(int chunkX, int chunkZ) {
        DebugUtil.startDebug(DebugUtil.SECTION_GEN_CHUNK);
        
        ChunkPrimerExtended chunkPrimer = new ChunkPrimerExtended(this.chunkGenerator.getChunkSource().getWorldHeight(), this.chunkGenerator.getChunkSource().getWorldFloor());
        Biome[] biomes = this.chunkGenerator.getBiomes(chunkX, chunkZ);
        this.chunkGenerator.generateChunkPrimer(chunkX, chunkZ, chunkPrimer, biomes);

        DebugUtil.endDebug(DebugUtil.SECTION_GEN_CHUNK);
        return chunkPrimer;
    }
    
    private void applyModGenerators(int x, int z, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        List<IWorldGenerator> generators = IGameRegistry.getSortedGeneratorList();
        if (generators == null) {
            IGameRegistry.computeGenerators();
            generators = IGameRegistry.getSortedGeneratorList();
            assert generators != null;
        }
        
        long worldSeed = world.getSeed();
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        long chunkSeed = (xSeed * x + zSeed * z) ^ worldSeed;

        for (IWorldGenerator generator : generators) {
            fmlRandom.setSeed(chunkSeed);
            
            try {
                CompatHandler.beforeGenerate(world, generator);
                generator.generate(fmlRandom, x, z, world, chunkGenerator, chunkProvider);
            } catch (IllegalArgumentException e) {
                this.logPopulatorExceptions(e);
            } finally {
                CompatHandler.afterGenerate(world);
            }
        }
    }
    
    private Random getCubeSpecificRandom(int cubeX, int cubeY, int cubeZ) {
        Random rand = new Random(world.getSeed());
        rand.setSeed(rand.nextInt() ^ cubeX);
        rand.setSeed(rand.nextInt() ^ cubeZ);
        rand.setSeed(rand.nextInt() ^ cubeY);
        
        return rand;
    }
    
    private void logPopulatorExceptions(IllegalArgumentException e) {
        if (this.populatorExceptions < MAX_POPULATOR_EXCEPTIONS) {
            StackTraceElement[] stack = e.getStackTrace();
            if (stack == null || stack.length < 1 || !stack[0].getClassName().equals(Random.class.getName()) || !stack[0].getMethodName().equals("nextInt")) {
                throw e;
            } else {
                ModernBeta.log(Level.ERROR, "Error while running Cubic Chunks populators, ignoring.. ");
                ModernBeta.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
            }
        } else if (this.populatorExceptions == MAX_POPULATOR_EXCEPTIONS) {
            ModernBeta.log(Level.ERROR, "Cubic Chunks populators are still erroring; errors will no longer be logged but offending populators will continue to cause issues.");
        }
        
        this.populatorExceptions++;
    }

}
