package mod.bespectacled.modernbetaforge.world.biome.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaSeasonalForest extends BiomeBeta {
    public BiomeBetaSeasonalForest() {
        super(new BiomeProperties("Beta Seasonal Forest")
            .setTemperature(1.0F)
            .setRainfall(0.7F)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;


        this.skyColor = ModernBetaBiomeColors.BETA_WARM_SKY_COLOR;
    }
    
}
