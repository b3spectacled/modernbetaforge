package mod.bespectacled.modernbetaforge.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
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
    
    public static void drawDebugMap(BiomeSource biomeSource, int width, int length) {
        ModernBeta.log(Level.DEBUG, "Attempting to create biome map..");
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_RGB);
        
        int offsetX = width / 2;
        int offsetZ = length / 2;
        
        for (int localX = 0; localX < width; ++localX) {
            for (int localZ = 0; localZ < length; ++localZ) {
                int x = localX - offsetX;
                int z = localZ - offsetZ;
                
                Biome biome = biomeSource.getBiome(x, z);

                int color = MathUtil.convertColorComponentsToInt(0, 255, 0);
                if (BiomeDictionary.hasType(biome, Type.OCEAN) || BiomeDictionary.hasType(biome, Type.RIVER)) {
                    color = MathUtil.convertColorComponentsToInt(0, 0, 255);
                }
                
                if (x > -5 && x < 5 && z > -5 && z < 5) {
                    color = MathUtil.convertColorComponentsToInt(255, 0, 0);
                }
                
                image.setRGB(localX, localZ, color);
            }
        }
        
        try {
            File file = new File("biome_map.png");
            ImageIO.write(image, "png", file);
            
            ModernBeta.log(Level.DEBUG, "Saved biome map to '" + file.getAbsolutePath() + "'");
        } catch (IOException e) {
            ModernBeta.log(Level.DEBUG, "Couldn't save biome map image!");
        }
    }
}
