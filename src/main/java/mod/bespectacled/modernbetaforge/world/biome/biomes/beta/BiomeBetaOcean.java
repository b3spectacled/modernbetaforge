package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaOcean extends BiomeBeta {
    public BiomeBetaOcean() {
        super(new BiomeProperties("Beta Ocean")
            .setTemperature(0.5f)
            .setRainfall(0.5f)
            .setBaseHeight(BASE_HEIGHT_OCEAN)
            .setHeightVariation(HEIGHT_VARY_OCEAN)
            //.setWaterColor(0)
        );
        
        this.spawnableCreatureList.clear();

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
}
