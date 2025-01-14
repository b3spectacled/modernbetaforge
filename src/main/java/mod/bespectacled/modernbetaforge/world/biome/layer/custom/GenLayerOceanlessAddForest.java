package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerOceanlessAddForest extends GenLayer {
    public GenLayerOceanlessAddForest(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentX = areaX - 1;
        int parentY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentInts = this.parent.getInts(parentX, parentY, parentWidth, parentHeight);
        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                int sw = parentInts[x + 0 + (y + 0) * parentWidth];
                int se = parentInts[x + 2 + (y + 0) * parentWidth];
                int nw = parentInts[x + 0 + (y + 2) * parentWidth];
                int ne = parentInts[x + 2 + (y + 2) * parentWidth];
                int ctr = parentInts[x + 1 + (y + 1) * parentWidth];
                this.initChunkSeed((long)(x + areaX), (long)(y + areaY));

                int chance = 1;
                int id = 1;

                if (sw != 0 && this.nextInt(chance++) == 0) {
                    id = sw;
                }

                if (se != 0 && this.nextInt(chance++) == 0) {
                    id = se;
                }

                if (nw != 0 && this.nextInt(chance++) == 0) {
                    id = nw;
                }

                if (ne != 0 && this.nextInt(chance++) == 0) {
                    id = ne;
                }

                if (this.nextInt(3) == 0) {
                    ints[x + y * areaWidth] = id;
                    
                } else if (id == 4) {
                    ints[x + y * areaWidth] = 4;
                    
                } else {
                    ints[x + y * areaWidth] = ctr;
                    
                }
            }
        }

        return ints;
    }
}