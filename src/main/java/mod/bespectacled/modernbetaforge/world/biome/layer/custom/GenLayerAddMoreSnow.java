package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerAddMoreSnow extends GenLayer {
    private static final int SNOW_CHANCE = 3; // Vanilla is 6
    
    public GenLayerAddMoreSnow(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int i = areaX - 1;
        int j = areaY - 1;
        int k = areaWidth + 2;
        int l = areaHeight + 2;
        int[] parentInts = this.parent.getInts(i, j, k, l);
        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                int ctr = parentInts[x + 1 + (y + 1) * k];
                this.initChunkSeed((long)(x + areaX), (long)(y + areaY));

                int chance = this.nextInt(SNOW_CHANCE);

                if (chance == 0) {
                    ctr = 4;
                } else if (chance <= 1) {
                    ctr = 3;
                }

                ints[x + y * areaWidth] = ctr;
            }
        }

        return ints;
    }

}
