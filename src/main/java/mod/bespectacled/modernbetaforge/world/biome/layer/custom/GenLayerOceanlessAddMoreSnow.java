package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerOceanlessAddMoreSnow extends GenLayer {
    private static final int FOREST = Biome.getIdForBiome(Biomes.FOREST);
    private static final int PLAINS = Biome.getIdForBiome(Biomes.PLAINS);
    private static final int DEFAULT_SNOWY_BIOME_CHANCE = 1;
    
    private final int snowyBiomeChance;
    private final int biomeToReplace;
    
    public GenLayerOceanlessAddMoreSnow(long seed, GenLayer parent) {
        this(seed, parent, DEFAULT_SNOWY_BIOME_CHANCE, PLAINS);
    }
    
    public GenLayerOceanlessAddMoreSnow(long seed, GenLayer parent, int snowyBiomeChance) {
        this(seed, parent, snowyBiomeChance, PLAINS);
    }
    
    public GenLayerOceanlessAddMoreSnow(long seed, GenLayer parent, int snowyBiomeChance, int biomeToReplace) {
        super(seed);
        
        this.parent = parent;
        this.snowyBiomeChance = snowyBiomeChance;
        this.biomeToReplace = biomeToReplace;
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

                if (adjacent > 1 && ctr == this.biomeToReplace && this.nextInt(this.snowyBiomeChance) == 0) {
                    ints[x + y * areaWidth] = FOREST;
                } else {
                    ints[x + y * areaWidth] = ctr;
                }
            }
        }

        return ints;
    }

}
