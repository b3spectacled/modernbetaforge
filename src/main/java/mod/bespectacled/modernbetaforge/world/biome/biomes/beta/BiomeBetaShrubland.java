package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaShrubland extends BiomeBeta {
    public BiomeBetaShrubland() {
        super(new BiomeProperties("Beta Shrubland")
            .setTemperature(0.7f)
            .setRainfall(0.4f)
            .setBaseHeight(0.2f)
            .setHeightVariation(0.3f)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
    
}
