package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;

public class BiomeBetaSeasonalForest extends BiomeBeta {
    private static final WorldGenCanopyTree DARK_OAK_TREE_FEATURE = new WorldGenCanopyTree(false); 
    
    public BiomeBetaSeasonalForest() {
        super(new BiomeProperties("Beta Seasonal Forest")
            .setTemperature(1.0f)
            .setRainfall(0.7f)
            .setBaseHeight(BASE_HEIGHT_TEMPERATE)
            .setHeightVariation(HEIGHT_VARY_TEMPERATE)
        );

        this.skyColor = ModernBetaBiomeColors.BETA_WARM_SKY_COLOR;
    }

    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaGeneratorSettings settings) {
        if (!settings.useDarkOakTrees)
            return this.getRandomTreeFeature(random);
        
        if (random.nextInt(5) == 0) {
            return ModernBetaBiome.TREE_FEATURE;
        }
        
        if (random.nextInt(3) == 0) {
            return ModernBetaBiome.BIG_TREE_FEATURE;
        }
        
        return DARK_OAK_TREE_FEATURE;
    }
    
}
