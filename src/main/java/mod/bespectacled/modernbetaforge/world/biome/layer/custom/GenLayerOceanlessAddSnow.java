package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerOceanlessAddSnow extends GenLayer {
    private static final int FOREST = Biome.getIdForBiome(Biomes.FOREST);
    private static final int EXTREME_HILLS = Biome.getIdForBiome(Biomes.EXTREME_HILLS);
    private static final int PLAINS = Biome.getIdForBiome(Biomes.PLAINS);
    private static final int SNOW_CHANCE = 6; // Vanilla is 6
    
    public GenLayerOceanlessAddSnow(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentX = areaX - 1;
        int parentY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentInts = this.parent.getInts(parentX, parentY, parentWidth, parentHeight);
        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                int ctr = parentInts[x + 1 + (y + 1) * parentWidth];
                this.initChunkSeed((long)(x + areaX), (long)(y + areaY));

                int chance = this.nextInt(SNOW_CHANCE);

                if (chance == 0) {
                    ctr = FOREST;
                } else if (chance <= 1) {
                    ctr = EXTREME_HILLS;
                } else {
                    ctr = PLAINS;
                }

                ints[x + y * areaWidth] = ctr;
            }
        }

        return ints;
    }

}
