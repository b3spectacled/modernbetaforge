package mod.bespectacled.modernbetaforge.world.gen;

import java.util.List;

import mod.bespectacled.modernbetaforge.api.world.gen.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.gen.ChunkSourceType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

public class ModernBetaChunkGenerator extends ChunkGeneratorOverworld {
    
    private final World world;
    private final long seed;  
    private final boolean mapFeaturesEnabled;
    
    private final ModernBetaChunkGeneratorSettings settings;
    
    private final ChunkSource chunkSource;
    
    public ModernBetaChunkGenerator(World world, long seed, boolean mapFeaturesEnabled, String generatorOptions) {
        super(world, seed, mapFeaturesEnabled, generatorOptions);
        
        this.world = world;
        this.seed = seed;
        this.mapFeaturesEnabled = mapFeaturesEnabled;
        
        this.settings = generatorOptions != null ?
            ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(generatorOptions).build() :
            new ModernBetaChunkGeneratorSettings.Factory().build();
        
        this.chunkSource = ChunkSourceType
            .fromId(this.settings.chunkSource)
            .create(this.world, this, this.settings, this.seed, this.mapFeaturesEnabled);
    }
    
    /*
     * Provided for when game needs it; handled in generateChunk
     */
    @Override
    public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
        this.chunkSource.provideBaseChunk(primer, x, z);
    }
    

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        return this.chunkSource.provideChunk(this.world, chunkX, chunkZ);
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        this.chunkSource.populateChunk(this.world, chunkX, chunkZ);
    }
    
    /*
     * Provided for when game needs it; handled in generateChunk
     */
    @Override
    public void replaceBiomeBlocks(int chunkX, int chunkZ, ChunkPrimer chunkPrimer, Biome[] biomes) {
        this.chunkSource.provideSurface(biomes, chunkPrimer, chunkX, chunkZ);
    }

    @Override
    public boolean generateStructures(Chunk chunk, int chunkX, int chunkZ) {
        return this.chunkSource.generateStructures(this.world, chunk, chunkX, chunkZ);
    }

    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return this.chunkSource.getPossibleCreatures(this.world, creatureType, pos);
    }

    @Override
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos position, boolean findUnexplored) {
        return this.chunkSource.getNearestStructurePos(world, structureName, position, findUnexplored);
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {
        this.chunkSource.recreateStructures(this.world, chunk, x, z);
    }

    @Override
    public boolean isInsideStructure(World world, String structureName, BlockPos pos) {
        return this.chunkSource.isInsideStructure(world, structureName, pos);
    }
    
    public ChunkSource getChunkSource() {
        return this.chunkSource;
    }
}
