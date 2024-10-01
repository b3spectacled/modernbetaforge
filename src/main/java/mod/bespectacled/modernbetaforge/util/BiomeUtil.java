package mod.bespectacled.modernbetaforge.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BiomeUtil {
    public static Biome getBiome(String biomeId, String context) {
        Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeId));
        
        if (biome == null) {
            String errorStr = String.format("[Modern Beta] Biome '%s' does not exist! Please check your %s settings.", biomeId, context);
            
            throw new IllegalArgumentException(errorStr);
        }
        
        return biome;
    }
}
