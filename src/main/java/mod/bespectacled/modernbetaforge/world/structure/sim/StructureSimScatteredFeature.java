package mod.bespectacled.modernbetaforge.world.structure.sim;

import java.util.Random;
import java.util.function.BiFunction;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;

public class StructureSimScatteredFeature extends StructureSim {
    private final int maxDistanceBetweenScatteredFeatures;

    public StructureSimScatteredFeature(long seed) {
        super(seed);
        
        this.maxDistanceBetweenScatteredFeatures = 32;
    }

    @Override
    public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome> biomeFunc) {
        int startChunkX = chunkX;
        int startChunkZ = chunkZ;

        if (chunkX < 0) {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (chunkZ < 0) {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int featureChunkX = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int featureChunkZ = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        Random random = this.setWorldSeed(featureChunkX, featureChunkZ, 14357617);
        featureChunkX = featureChunkX * this.maxDistanceBetweenScatteredFeatures;
        featureChunkZ = featureChunkZ * this.maxDistanceBetweenScatteredFeatures;
        featureChunkX = featureChunkX + random.nextInt(this.maxDistanceBetweenScatteredFeatures - 8);
        featureChunkZ = featureChunkZ + random.nextInt(this.maxDistanceBetweenScatteredFeatures - 8);

        if (startChunkX == featureChunkX && startChunkZ == featureChunkZ) {
            int x = (startChunkX << 4) + 8;
            int z = (startChunkZ << 4) + 8;
            
            return MapGenScatteredFeature.BIOMELIST.contains(biomeFunc.apply(x, z));
        }

        return false;
    }

}
