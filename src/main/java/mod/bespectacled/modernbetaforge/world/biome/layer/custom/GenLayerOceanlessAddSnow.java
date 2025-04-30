package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerOceanlessAddSnow extends GenLayer {
    public static final int DEFAULT_SNOWY_BIOME_CHANCE = 6; // Vanilla is 6
    
    private static final int FOREST = Biome.getIdForBiome(Biomes.FOREST);
    private static final int EXTREME_HILLS = Biome.getIdForBiome(Biomes.EXTREME_HILLS);
    private static final int PLAINS = Biome.getIdForBiome(Biomes.PLAINS);
    
    private final int snowyBiomeChance;
    
    public GenLayerOceanlessAddSnow(long seed, GenLayer parent) {
        this(seed, parent, DEFAULT_SNOWY_BIOME_CHANCE);
    }
    
    public GenLayerOceanlessAddSnow(long seed, GenLayer parent, int snowyBiomeChance) {
        super(seed);
        
        this.parent = parent;
        this.snowyBiomeChance = snowyBiomeChance;
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

                int chance = this.nextInt(this.snowyBiomeChance);
                
                if (chance == 0) {        // Governs icy biome chance
                    ctr = FOREST;
                } else if (chance <= 1) { // Governs temperate biome chance, including taigas and extreme hills
                    ctr = EXTREME_HILLS;
                } else {
                    ctr = PLAINS;         // Governs hot biome chance
                }

                ints[x + y * areaWidth] = ctr;
            }
        }

        return ints;
    }

}
