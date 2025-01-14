package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerOceanlessAddMoreSnow extends GenLayer {
    private static final int FOREST = Biome.getIdForBiome(Biomes.FOREST);
    private static final int SNOW_CHANCE = 1;
    
    public GenLayerOceanlessAddMoreSnow(long seed, GenLayer parent) {
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
                int sw = parentInts[x + 0 + (y + 0) * parentWidth];
                int se = parentInts[x + 2 + (y + 0) * parentWidth];
                int nw = parentInts[x + 0 + (y + 2) * parentWidth];
                int ne = parentInts[x + 2 + (y + 2) * parentWidth];
                int ctr = parentInts[x + 1 + (y + 1) * parentWidth];
                this.initChunkSeed((long)(x + areaX), (long)(y + areaY));
                
                int adjacent = 0;
                
                if (sw == FOREST) adjacent++;
                if (se == FOREST) adjacent++;
                if (nw == FOREST) adjacent++;
                if (ne == FOREST) adjacent++;

                if (adjacent > 1 && ctr == 1 && this.nextInt(SNOW_CHANCE) == 0) {
                    ints[x + y * areaWidth] = FOREST;
                } else {
                    ints[x + y * areaWidth] = ctr;
                }
            }
        }

        return ints;
    }

}
