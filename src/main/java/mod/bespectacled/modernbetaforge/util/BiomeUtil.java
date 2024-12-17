package mod.bespectacled.modernbetaforge.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BiomeUtil {
    public static Biome getBiome(ResourceLocation biomeKey) {
        return getBiome(biomeKey, "");
    }
    
    public static Biome getBiome(ResourceLocation biomeKey, String context) {
        Biome biome = ForgeRegistries.BIOMES.getValue(biomeKey);
        
        if (biome == null) {
            String errorStr = String.format("[Modern Beta] Biome '%s' does not exist! Please check your %s settings.", biomeKey.toString(), context);
            
            throw new IllegalArgumentException(errorStr);
        }
        
        return biome;
    }
    
    public static Biome getRandomBiome(Random random) {
        List<Biome> biomes = new ArrayList<>(ForgeRegistries.BIOMES.getValuesCollection());
        
        return biomes.get(random.nextInt(biomes.size()));
    }
}
