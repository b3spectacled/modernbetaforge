package mod.bespectacled.modernbetaforge.world.chunk;

import java.util.List;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.util.DebugUtil;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

public class ModernBetaChunkGenerator extends ChunkGeneratorOverworld {
    private final ChunkSource chunkSource;
    
    public ModernBetaChunkGenerator(World world, String generatorOptions) {
        super(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
        
        ModernBetaChunkGeneratorSettings settings = generatorOptions != null ?
            ModernBetaChunkGeneratorSettings.build(generatorOptions) :
            ModernBetaChunkGeneratorSettings.build();
        
        this.chunkSource = ModernBetaRegistries.CHUNK
            .get(settings.chunkSource)
            .apply(world, this, settings);

        // Important for correct structure spawning when y < seaLevel, e.g. villages, monuments
        world.setSeaLevel(this.chunkSource.getSeaLevel());
    }
    
    /*
     * Handled in generateChunk, but also used by WoodlandMansion.
     * 
     */
    @Override
    public void setBlocksInChunk(int x, int z, ChunkPrimer chunkPrimer) {
        this.chunkSource.provideInitialChunk(chunkPrimer, x, z);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        DebugUtil.startDebug(DebugUtil.SECTION_GEN_CHUNK);
        Chunk chunk = this.chunkSource.provideChunk(chunkX, chunkZ);
        DebugUtil.endDebug(DebugUtil.SECTION_GEN_CHUNK);
        
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        this.chunkSource.populateChunk(chunkX, chunkZ);
    }
    
    /*
     * Handled in generateChunk
     */
    @Override
    public void replaceBiomeBlocks(int chunkX, int chunkZ, ChunkPrimer chunkPrimer, Biome[] biomes) {}

    @Override
    public boolean generateStructures(Chunk chunk, int chunkX, int chunkZ) {
        return this.chunkSource.generateStructures(chunk, chunkX, chunkZ);
    }

    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return this.chunkSource.getPossibleCreatures(creatureType, pos);
    }

    @Override
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos position, boolean findUnexplored) {
        return this.chunkSource.getNearestStructurePos(world, structureName, position, findUnexplored);
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {
        this.chunkSource.recreateStructures(chunk, x, z);
    }

    @Override
    public boolean isInsideStructure(World world, String structureName, BlockPos pos) {
        return this.chunkSource.isInsideStructure(world, structureName, pos);
    }
    
    public ChunkSource getChunkSource() {
        return this.chunkSource;
    }
}
