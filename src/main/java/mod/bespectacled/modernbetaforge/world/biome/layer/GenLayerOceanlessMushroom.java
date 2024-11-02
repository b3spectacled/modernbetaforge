package mod.bespectacled.modernbetaforge.world.biome.layer;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerOceanlessMushroom extends GenLayer {
    private static final int JUNGLE = Biome.getIdForBiome(Biomes.JUNGLE);
    private static final int MUSHROOM = Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND);
    
    public GenLayerOceanlessMushroom(long seed, GenLayer parent) {
        super(seed);
        super.parent = parent;
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
                int sw = parentInts[x + 0 + (y + 0) * k];
                int se = parentInts[x + 2 + (y + 0) * k];
                int nw = parentInts[x + 0 + (y + 2) * k];
                int ne = parentInts[x + 2 + (y + 2) * k];
                int ctr = parentInts[x + 1 + (y + 1) * k];
                this.initChunkSeed((long)(x + areaX), (long)(y + areaY));

                if (ctr == JUNGLE && sw == JUNGLE && se == JUNGLE && nw == JUNGLE && ne == JUNGLE && this.nextInt(50) == 0) {
                    ints[x + y * areaWidth] = MUSHROOM;
                } else {
                    ints[x + y * areaWidth] = ctr;
                }
            }
        }

        return ints;
    }

}
