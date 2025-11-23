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
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BetaWorldSpawner implements WorldSpawner {
    private static final int MAX_ATTEMPTS = 10000;
    
    @Override
    public BlockPos locateSpawn(BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        SurfaceBuilder surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER
            .get(chunkSource.getGeneratorSettings().surfaceBuilder)
            .apply(chunkSource, chunkSource.getGeneratorSettings());

        int x = 0;
        int z = 0;
        int attempts = 0;
        
        MutableBlockPos mutablePos = new MutableBlockPos(x, 0, z);
        Random random = new Random();
        
        while(!this.isSandAt(x, z, chunkSource, biomeSource, surfaceBuilder, random)) {
            if (attempts > MAX_ATTEMPTS) {
                return WorldSpawner.DEFAULT.locateSpawn(spawnPos, chunkSource, biomeSource);
            }

            this.setRandomPosition(mutablePos, random);
            x = mutablePos.getX();
            z = mutablePos.getZ();
            
            attempts++;
        }
        
        int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.FLOOR) + 1;
        
        return new BlockPos(x, y, z);
    }
    
    protected void setRandomPosition(MutableBlockPos mutablePos, Random random) {
        int x = mutablePos.getX();
        int z = mutablePos.getZ();
        
        x += random.nextInt(64) - random.nextInt(64);
        z += random.nextInt(64) - random.nextInt(64);
        
        mutablePos.setPos(x, mutablePos.getY(), z);
    }
    
    private boolean isSandAt(int x, int z, ChunkSource chunkSource, BiomeSource biomeSource, SurfaceBuilder surfaceBuilder, Random random) {
        int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.FLOOR);
        int seaLevel = chunkSource.getSeaLevel();
        
        boolean isSandyBiome = BiomeDictionary.getBiomes(Type.SANDY).contains(biomeSource.getBiome(x, z)) && this.aboveSeaLevel(y, seaLevel);
        
        if (surfaceBuilder instanceof NoiseSurfaceBuilder) {
            NoiseSurfaceBuilder noiseSurfaceBuilder = (NoiseSurfaceBuilder)surfaceBuilder;
            boolean atBeachDepth = noiseSurfaceBuilder.atBeachDepth(y + 1) && this.aboveSeaLevel(y, seaLevel);
            
            return isSandyBiome || atBeachDepth && noiseSurfaceBuilder.isPrimaryBeach(x, z, random) && !noiseSurfaceBuilder.isSecondaryBeach(x, z, random);
        }
        
        return isSandyBiome;
    }
    
    private boolean aboveSeaLevel(int y, int seaLevel) {
        return y >= seaLevel - 1;
    }
}
