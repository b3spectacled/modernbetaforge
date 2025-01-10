package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerSmallIslands extends GenLayer {
    private static final int OCEAN = Biome.getIdForBiome(Biomes.OCEAN);
    private static final int PLAINS = Biome.getIdForBiome(Biomes.PLAINS);
    
    public GenLayerSmallIslands(long seed) {
        super(seed);
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);
        
        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                ints[x + y * areaWidth] = OCEAN;
            }
        }

        if (areaX > -areaWidth && areaX <= 0 && areaY > -areaHeight && areaY <= 0) {
            ints[-areaX + -areaY * areaWidth] = PLAINS;
        }

        return ints;
    }
    
}
