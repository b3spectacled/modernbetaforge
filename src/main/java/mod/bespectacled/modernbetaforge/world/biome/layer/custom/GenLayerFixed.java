package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerFixed extends GenLayer {
    public static final int PLAINS = Biome.getIdForBiome(Biomes.PLAINS);
    public static final int OCEAN = Biome.getIdForBiome(Biomes.OCEAN);
    
    private final int fixedBiome;
    
    public GenLayerFixed(long seed, int fixedBiome) {
        super(seed);
        
        this.fixedBiome = fixedBiome;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] ints = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                ints[x + y * areaWidth] = this.fixedBiome;
            }
        }

        return ints;
    }

}
