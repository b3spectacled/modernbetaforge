package mod.bespectacled.modernbetaforge.compat.bettermineshafts;

import java.util.Random;
import java.util.function.BiFunction;

import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import com.yungnickyoung.minecraft.bettermineshafts.world.MapGenBetterMineshaft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class MapGenBetterMineshaftOptimized extends MapGenBetterMineshaft {
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return canSpawnStructureAtCoords(chunkX, chunkZ, this.rand, (cX, cZ) -> this.world.getBiome(new BlockPos((cX << 4) + 8, 64, (cZ << 4) + 8)));
    }
    
    public static boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, Random random, BiFunction<Integer, Integer, Biome> biomeFunc) {
        // Move spawn rate check to beginning of method to reduce chance of calling getBiome,
        // which is very expensive for Modern Beta worlds!
        if (random.nextDouble() >= Configuration.mineshaftSpawnRate) {
            return false;
        }

        Biome biome = biomeFunc.apply((chunkX << 4) + 8, (chunkZ << 4) + 8);
        boolean isOcean = BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN);
        boolean isBeach = BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH);
        
        return !isOcean && !isBeach;
    }
}
