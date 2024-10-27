package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaFrozenOcean extends BiomeBeta {
    public BiomeBetaFrozenOcean() {
        super(new BiomeProperties("Beta Frozen Ocean")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setBaseHeight(BASE_HEIGHT_OCEAN)
            .setHeightVariation(HEIGHT_VARY_OCEAN)
            .setSnowEnabled()
            //.setWaterColor(0)
        );
        
        this.skyColor = ModernBetaBiomeColors.BETA_COLD_SKY_COLOR;

        this.spawnableCreatureList.clear();
    }
    
}
