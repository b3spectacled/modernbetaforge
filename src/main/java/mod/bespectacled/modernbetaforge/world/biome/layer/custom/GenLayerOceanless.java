package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerOceanless extends GenLayer {
    private static final int PLAINS = Biome.getIdForBiome(Biomes.PLAINS);
    
    public GenLayerOceanless(long seed) {
        super(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                ints[x + y * areaWidth] = PLAINS;
            }
        }

        return ints;
    }

}
