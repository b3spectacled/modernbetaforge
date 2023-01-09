package mod.bespectacled.modernbetaforge.world.biome.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaFrozenOcean extends BiomeBeta {
    public BiomeBetaFrozenOcean() {
        super(new BiomeProperties("Beta Frozen Ocean")
            .setTemperature(0.0F)
            .setRainfall(0.5F)
            .setSnowEnabled()
            //.setWaterColor(0)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        this.spawnableCreatureList.clear();
        
        this.skyColor = ModernBetaBiomeColors.BETA_COLD_SKY_COLOR;
    }
    
}
