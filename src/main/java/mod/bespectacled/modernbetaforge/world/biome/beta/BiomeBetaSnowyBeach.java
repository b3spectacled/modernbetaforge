package mod.bespectacled.modernbetaforge.world.biome.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaSnowyBeach extends BiomeBeta {
    public BiomeBetaSnowyBeach() {
        super(new BiomeProperties("Beta Snowy Beach")
            .setTemperature(0.0F)
            .setRainfall(0.5F)
            .setSnowEnabled()
        );
        
        this.topBlock = BlockStates.SAND;
        this.fillerBlock = BlockStates.SAND;
        
        this.spawnableCreatureList.clear();


        this.skyColor = ModernBetaBiomeColors.BETA_COLD_SKY_COLOR;
    }
    
}
