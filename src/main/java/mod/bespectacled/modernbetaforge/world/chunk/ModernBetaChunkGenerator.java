package mod.bespectacled.modernbetaforge.world.chunk;

import java.util.List;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
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
    
    public ModernBetaChunkGenerator(World world, long seed, boolean mapFeaturesEnabled, String generatorOptions) {
        super(world, seed, mapFeaturesEnabled, generatorOptions);
        
        ModernBetaChunkGeneratorSettings settings = generatorOptions != null ?
            ModernBetaChunkGeneratorSettings.build(generatorOptions) :
            ModernBetaChunkGeneratorSettings.build();
        
        ModernBetaNoiseSettings noiseSettings = ModernBetaRegistries.NOISE.getOrElse(settings.chunkSource, ModernBetaNoiseSettings.BETA);
        this.chunkSource = ModernBetaRegistries.CHUNK
            .get(settings.chunkSource)
            .apply(world, this, settings, noiseSettings, seed, mapFeaturesEnabled);

        // Important for correct structure spawning when y < seaLevel, e.g. villages, monuments
        world.setSeaLevel(this.chunkSource.getSeaLevel());
    }
    
    /*
     * Handled in generateChunk, but also used by WoodlandMansion.
     * 
     */
    @Override
    public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
        this.chunkSource.provideBaseChunk(primer, x, z);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        return this.chunkSource.provideChunk(chunkX, chunkZ);
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
