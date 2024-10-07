package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaSnowyBeach extends BiomeBeta {
    public BiomeBetaSnowyBeach() {
        super(new BiomeProperties("Beta Snowy Beach")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setBaseHeight(0.0f)
            .setHeightVariation(0.025f)
            .setSnowEnabled()
        );
        
        this.topBlock = BlockStates.SAND;
        this.fillerBlock = BlockStates.SAND;
        
        this.spawnableCreatureList.clear();

        this.skyColor = ModernBetaBiomeColors.BETA_COLD_SKY_COLOR;
    }
    
}
