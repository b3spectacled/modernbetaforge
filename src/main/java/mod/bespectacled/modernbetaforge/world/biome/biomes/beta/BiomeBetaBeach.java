package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaBeach extends BiomeBeta {
    public BiomeBetaBeach() {
        super(new BiomeProperties("Beta Beach")
            .setTemperature(0.5f)
            .setRainfall(0.5f)
            .setBaseHeight(0.0f)
            .setHeightVariation(0.025f)
            //.setWaterColor(16711680)
        );
        
        this.topBlock = BlockStates.SAND;
        this.fillerBlock = BlockStates.SAND;
        
        this.spawnableCreatureList.clear();
        
        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
}
