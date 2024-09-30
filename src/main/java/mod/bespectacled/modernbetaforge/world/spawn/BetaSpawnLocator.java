package mod.bespectacled.modernbetaforge.world.spawn;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaDesert;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class BetaSpawnLocator implements SpawnLocator {
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        if (!(chunkSource instanceof NoiseChunkSource))
            return SpawnLocator.DEFAULT.locateSpawn(spawnPos, chunkSource, biomeSource);
        
        NoiseChunkSource noiseChunkSource = (NoiseChunkSource)chunkSource;
        PerlinOctaveNoise beachOctaveNoise = noiseChunkSource.getBeachOctaveNoise().get();
        
        int x = 0;
        int z = 0;
        int attempts = 0;
        
        Random random = new Random();
        
        while(!this.isSandAt(x, z, chunkSource, biomeSource, beachOctaveNoise)) {
            if (attempts > 10000) {
                x = 0;
                z = 0;
                break;
            }
            
            x += random.nextInt(64) - random.nextInt(64);
            z += random.nextInt(64) - random.nextInt(64);
            
            attempts++;
        }
        
        int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE) + 1;
        
        return new BlockPos(x, y, z);
    }
    
    private boolean isSandAt(int x, int z, ChunkSource chunkSource, BiomeSource biomeSource, PerlinOctaveNoise beachOctaveNoise) {
        int seaLevel = chunkSource.getSeaLevel();
        int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
        
        Biome biome = biomeSource.getBiome(x, z);
        
        return (biome instanceof BiomeBetaDesert && y >= seaLevel - 1) || (beachOctaveNoise.sample(x * 0.03125, z * 0.03125, 0.0) > 0.0 && y >= seaLevel - 1 && y < seaLevel + 2);
    }
}
