package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaSeasonalForest extends BiomeBeta {
    public BiomeBetaSeasonalForest() {
        super(new BiomeProperties("Beta Seasonal Forest")
            .setTemperature(1.0f)
            .setRainfall(0.7f)
            .setBaseHeight(0.37f)
            .setHeightVariation(0.5f)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;

        this.skyColor = ModernBetaBiomeColors.BETA_WARM_SKY_COLOR;
    }
    
}
