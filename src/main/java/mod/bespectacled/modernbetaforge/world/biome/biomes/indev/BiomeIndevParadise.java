package mod.bespectacled.modernbetaforge.world.biome.biomes.indev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeIndevParadise extends BiomeIndev {
    public BiomeIndevParadise() {
        super(new BiomeProperties("Indev Paradise").setRainDisabled());
        
        this.skyColor = ModernBetaBiomeColors.INDEV_PARADISE_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.INDEV_PARADISE_FOG_COLOR;
        this.cloudColor = ModernBetaBiomeColors.INDEV_PARADISE_CLOUD_COLOR;
    }
}
