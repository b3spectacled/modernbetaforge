package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaShrubland extends BiomeBeta {
    public BiomeBetaShrubland() {
        super(new BiomeProperties("Beta Shrubland")
            .setTemperature(0.7f)
            .setRainfall(0.4f)
            .setBaseHeight(BASE_HEIGHT_LOW)
            .setHeightVariation(HEIGHT_VARY_LOW)
        );

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
    
}
