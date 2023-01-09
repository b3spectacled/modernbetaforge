package mod.bespectacled.modernbetaforge.world.biome.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaBeach extends BiomeBeta {
    public BiomeBetaBeach() {
        super(new BiomeProperties("Beta Beach")
            .setTemperature(0.5F)
            .setRainfall(0.5F)
            //.setWaterColor(16711680)
        );
        
        this.topBlock = BlockStates.SAND;
        this.fillerBlock = BlockStates.SAND;
        
        this.spawnableCreatureList.clear();
        
        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
}
