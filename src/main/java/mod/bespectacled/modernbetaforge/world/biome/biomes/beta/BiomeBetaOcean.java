package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaOcean extends BiomeBeta {
    public BiomeBetaOcean() {
        super(new BiomeProperties("Beta Ocean")
            .setTemperature(0.5f)
            .setRainfall(0.5f)
            .setBaseHeight(-1.0f)
            .setHeightVariation(0.1f)
            //.setWaterColor(0)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        this.spawnableCreatureList.clear();

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
}
