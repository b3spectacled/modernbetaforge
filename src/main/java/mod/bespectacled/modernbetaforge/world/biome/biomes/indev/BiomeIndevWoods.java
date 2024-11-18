package mod.bespectacled.modernbetaforge.world.biome.biomes.indev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeIndevWoods extends BiomeIndev {
    public BiomeIndevWoods() {
        super(new BiomeProperties("Indev Woods"));
        
        this.skyColor = ModernBetaBiomeColors.INDEV_WOODS_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.INDEV_WOODS_FOG_COLOR;
        this.cloudColor = ModernBetaBiomeColors.INDEV_WOODS_CLOUD_COLOR;
    }
}
