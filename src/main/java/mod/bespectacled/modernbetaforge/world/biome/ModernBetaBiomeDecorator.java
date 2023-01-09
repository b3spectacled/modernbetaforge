package mod.bespectacled.modernbetaforge.world.biome;

import java.util.Random;

import net.minecraft.world.biome.BiomeDecorator;

public abstract class ModernBetaBiomeDecorator extends BiomeDecorator {
    protected int getOreHeight(Random random, int minHeight, int maxHeight) {
        if (maxHeight < minHeight) {
            int height = minHeight;
            
            minHeight = maxHeight;
            maxHeight = height;
        } else if (maxHeight == minHeight) {
            if (minHeight < 255) {
                ++maxHeight;
            } else {
                --minHeight;
            }
        }
        
        return random.nextInt(maxHeight - minHeight) + minHeight;
    }
    
    protected int getLapisHeight(Random random, int centerHeight, int spread) {
        return random.nextInt(spread) + random.nextInt(spread) + centerHeight - spread;
    }
}
