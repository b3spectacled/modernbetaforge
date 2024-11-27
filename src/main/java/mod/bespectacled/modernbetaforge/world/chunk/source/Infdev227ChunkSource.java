package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceDefault;
import mod.bespectacled.modernbetaforge.world.chunk.blocksource.BlockSourceRules;
import mod.bespectacled.modernbetaforge.world.spawn.InfdevSpawnLocator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class Infdev227ChunkSource extends ChunkSource {
    private PerlinOctaveNoise heightNoise0;
    private PerlinOctaveNoise heightNoise1;
    private PerlinOctaveNoise highOctaveNoise;
    private PerlinOctaveNoise lowOctaveNoise;
    private PerlinOctaveNoise selectorOctaveNoise;
    private PerlinOctaveNoise detailOctaveNoise;
    
    private final ChunkCache<int[]> heightmapCache;
    private final SurfaceBuilder surfaceBuilder;

    public Infdev227ChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings chunkGeneratorSettings,
        ModernBetaNoiseSettings noiseSettings,
        long seed,
        boolean mapFeaturesEnabled
    ) {
        super(world, chunkGenerator, chunkGeneratorSettings, noiseSettings, seed, mapFeaturesEnabled);
        this.heightNoise0 = new PerlinOctaveNoise(this.random, 16, true);
        this.heightNoise1 = new PerlinOctaveNoise(this.random, 16, true);
        this.highOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        this.lowOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.selectorOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.detailOctaveNoise = new PerlinOctaveNoise(this.random, 5, true);
        
        this.heightmapCache = new ChunkCache<>("heightmap", this::sampleHeightmapChunk);
        this.surfaceBuilder = ModernBetaRegistries.SURFACE
            .get(this.settings.surfaceBuilder)
            .apply(world, this, chunkGeneratorSettings);
        
        // Cloud height was y128 in this version
        this.setCloudHeight(128);
    }
    
    @Override
    public SpawnLocator getSpawnLocator() {
        return new InfdevSpawnLocator();
    }

    @Override
    public void provideBaseChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        this.generateTerrain(chunkPrimer, chunkX, chunkZ);
    }

    @Override
    public void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        this.surfaceBuilder.provideSurface(biomes, chunkPrimer, chunkX, chunkZ);
    }

    @Override
    public int getHeight(int x, int z, Type type) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        int[] heightmap = this.heightmapCache.get(chunkX, chunkZ); 
        int height = heightmap[(z & 0xF) + (x & 0xF) * 16];
        
        if (type == HeightmapChunk.Type.OCEAN && height < this.seaLevel)
            height = this.seaLevel;
        
        return height + 1;
    }
    
    @Override
    public int getSeaLevel() {
        // Real sea level is one block higher
        return this.settings.seaLevel + 1;
    }
    
    private void generateTerrain(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        int[] heightmap = this.heightmapCache.get(chunkX, chunkZ);
        Random random = new Random();
        
        // Create and populate block sources
        BlockSourceDefault defaultSource = new BlockSourceDefault();
        BlockSourceRules blockSources = new BlockSourceRules.Builder()
            .add((x, y, z) -> defaultSource.sample(x, y, z))
            .build();
        
        for (int localX = 0; localX < 16; ++localX) {
            int x = startX + localX;
            int rX = x / 1024;
            
            for (int localZ = 0; localZ < 16; ++localZ) {    
                int z = startZ + localZ;
                int rZ = z / 1024;
                
                int height = heightmap[(z & 0xF) + (x & 0xF) * 16];
                
                for (int y = 0; y < this.settings.height; ++y) {
                    IBlockState blockState = BlockStates.AIR;
                    
                    if (this.settings.useInfdevWalls && (x == 0 || z == 0) && y <= height + 2) {
                        blockState = BlockStates.OBSIDIAN;
                        
                    } else if (y <= height) {
                        blockState = this.defaultBlock;
                        
                    } else if (y <= this.seaLevel) {
                        blockState = this.defaultFluid;
                        
                    }
                    
                    if (this.settings.useInfdevPyramids) {
                        random.setSeed(rX + rZ * 13871);
                        int bX = (rX << 10) + 128 + random.nextInt(512);
                        int bZ = (rZ << 10) + 128 + random.nextInt(512);
                        
                        bX = x - bX;
                        bZ = z - bZ;
                        
                        if (bX < 0) {
                            bX = -bX;
                        }
                        
                        if (bZ < 0) {
                            bZ = -bZ;
                        }
                        
                        if (bZ > bX) {
                            bX = bZ;
                        }
                        
                        if ((bX = 127 - bX) == 255) {
                            bX = 1;
                        }
                        
                        if (bX < height) {
                            bX = height;
                        }
                        
                        if (y <= bX && (BlockStates.isAir(blockState)|| BlockStates.isEqual(blockState, this.defaultFluid))) {
                            blockState = Blocks.BRICK_BLOCK.getDefaultState();
                        }
                    }
                    
                    defaultSource.setBlockState(blockState);
                    chunkPrimer.setBlockState(localX, y, localZ, blockSources.sample(x, y, z));
                }
            }
        }
    }

    private int sampleHeightmap(int x, int z) {
        float baseHeight = (float)(
            this.heightNoise0.sample(x / 0.03125f, 0.0, z / 0.03125f) - 
            this.heightNoise1.sample(x / 0.015625f, 0.0, z / 0.015625f)) / 512.0f / 4.0f;
        float heightSelector = (float)this.selectorOctaveNoise.sampleXY(x / 4.0f, z / 4.0f);
        float detailNoise = (float)this.detailOctaveNoise.sampleXY(x / 8.0f, z / 8.0f) / 8.0f;
        
        float additionalHeight = heightSelector > 0.0f ? 
            ((float)(this.highOctaveNoise.sampleXY(x * 0.25714284f * 2.0f, z * 0.25714284f * 2.0f) * detailNoise / 4.0)) :
            ((float)(this.lowOctaveNoise.sampleXY(x * 0.25714284f, z * 0.25714284f) * detailNoise));
            
        int height = (int)(baseHeight + this.seaLevel + additionalHeight);

        if ((float)this.selectorOctaveNoise.sampleXY(x, z) < 0.0f) {
            height = height / 2 << 1;
            
            if ((float)this.selectorOctaveNoise.sampleXY(x / 5, z / 5) < 0.0f) {
                ++height;
            }
        }
        
        return height;
    }
    
    private int[] sampleHeightmapChunk(int chunkX, int chunkZ) {
        int[] heightmap = new int[256];
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        int ndx = 0;
        for (int x = startX; x < startX + 16; ++x) {
            for (int z = startZ; z < startZ + 16; ++z) {
                heightmap[ndx++] = this.sampleHeightmap(x, z);
            }
        }
        
        return heightmap;
    }
}
