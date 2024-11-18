package mod.bespectacled.modernbetaforge.world.biome.biomes.indev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeIndevNormal extends BiomeIndev {
    public BiomeIndevNormal() {
        super(new BiomeProperties("Indev Normal"));
        
        this.skyColor = ModernBetaBiomeColors.INDEV_NORMAL_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.INDEV_NORMAL_FOG_COLOR;
    }
}
