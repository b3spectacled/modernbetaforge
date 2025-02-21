package mod.bespectacled.modernbetaforge.world.spawn;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.NoiseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.api.world.spawn.WorldSpawner;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BetaWorldSpawner implements WorldSpawner {
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        SurfaceBuilder surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER
            .get(chunkSource.getGeneratorSettings().surfaceBuilder)
            .apply(chunkSource, chunkSource.getGeneratorSettings());

        int x = 0;
        int z = 0;
        int attempts = 0;
        
        Random random = new Random();
        
        while(!this.isSandAt(x, z, chunkSource, biomeSource, surfaceBuilder, random)) {
            if (attempts > 10000) {
                return WorldSpawner.DEFAULT.locateSpawn(spawnPos, chunkSource, biomeSource);
            }
            
            x += random.nextInt(64) - random.nextInt(64);
            z += random.nextInt(64) - random.nextInt(64);
            
            attempts++;
        }
        
        int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.FLOOR) + 1;
        
        return new BlockPos(x, y, z);
    }
    
    private boolean isSandAt(int x, int z, ChunkSource chunkSource, BiomeSource biomeSource, SurfaceBuilder surfaceBuilder, Random random) {
        int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.FLOOR);
        int seaLevel = chunkSource.getSeaLevel();
        
        boolean isSandyBiome = BiomeDictionary.getBiomes(Type.SANDY).contains(biomeSource.getBiome(x, z)) && y >= seaLevel - 1;
        
        if (surfaceBuilder instanceof NoiseSurfaceBuilder) {
            NoiseSurfaceBuilder noiseSurfaceBuilder = (NoiseSurfaceBuilder)surfaceBuilder;
            boolean atBeachDepth = noiseSurfaceBuilder.atBeachDepth(y) && y >= seaLevel - 1;
            
            return isSandyBiome || atBeachDepth && noiseSurfaceBuilder.isBeach(x, z, random) && !noiseSurfaceBuilder.isGravelBeach(x, z, random);
        }
        
        return isSandyBiome;
    }
}
