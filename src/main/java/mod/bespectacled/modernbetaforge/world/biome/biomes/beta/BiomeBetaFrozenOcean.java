package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaFrozenOcean extends BiomeBeta {
    public BiomeBetaFrozenOcean() {
        super(new BiomeProperties("Beta Frozen Ocean")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setBaseHeight(-1.0f)
            .setHeightVariation(0.1f)
            .setSnowEnabled()
            //.setWaterColor(0)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        this.spawnableCreatureList.clear();
        
        this.skyColor = ModernBetaBiomeColors.BETA_COLD_SKY_COLOR;
    }
    
}
