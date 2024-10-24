package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaSeasonalForest extends BiomeBeta {
    public BiomeBetaSeasonalForest() {
        super(new BiomeProperties("Beta Seasonal Forest")
            .setTemperature(1.0f)
            .setRainfall(0.7f)
            .setBaseHeight(BASE_HEIGHT_HIGH)
            .setHeightVariation(HEIGHT_VARY_HIGH)
        );

        this.skyColor = ModernBetaBiomeColors.BETA_WARM_SKY_COLOR;
    }
    
}
