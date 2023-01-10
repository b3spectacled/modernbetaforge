package mod.bespectacled.modernbetaforge.world.biome.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaShrubland extends BiomeBeta {
    public BiomeBetaShrubland() {
        super(new BiomeProperties("Beta Shrubland")
            .setTemperature(0.7F)
            .setRainfall(0.4F)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
    
}
