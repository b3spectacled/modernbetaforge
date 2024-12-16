package mod.bespectacled.modernbetaforge.world.spawn;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BetaSpawnLocator implements SpawnLocator {
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        if (!(chunkSource instanceof NoiseChunkSource))
            return SpawnLocator.DEFAULT.locateSpawn(spawnPos, chunkSource, biomeSource);
        
        NoiseChunkSource noiseChunkSource = (NoiseChunkSource)chunkSource;
        PerlinOctaveNoise beachOctaveNoise = noiseChunkSource.getBeachOctaveNoise().get();

        boolean failed = false;
        int x = 0;
        int z = 0;
        int attempts = 0;
        
        Random random = new Random();
        
        while(!this.isSandAt(x, z, chunkSource, biomeSource, beachOctaveNoise)) {
            if (attempts > 10000) {
                failed = true;
                x = 0;
                z = 0;
                
                break;
            }
            
            x += random.nextInt(64) - random.nextInt(64);
            z += random.nextInt(64) - random.nextInt(64);
            
            attempts++;
        }
        
        int y = chunkSource.getHeight(x, z, failed ? HeightmapChunk.Type.OCEAN : HeightmapChunk.Type.FLOOR) + 1;
        
        return new BlockPos(x, y, z);
    }
    
    private boolean isSandAt(int x, int z, ChunkSource chunkSource, BiomeSource biomeSource, PerlinOctaveNoise beachOctaveNoise) {
        int seaLevel = chunkSource.getSeaLevel();
        int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.FLOOR);
        
        Biome biome = biomeSource.getBiome(x, z);
        boolean isSandy = BiomeDictionary.getBiomes(Type.SANDY).contains(biome);
        
        return (isSandy && y >= seaLevel - 1) || (beachOctaveNoise.sample(x * 0.03125, z * 0.03125, 0.0) > 0.0 && y >= seaLevel - 1 && y < seaLevel + 2);
    }
}
