package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;

public class BiomeBetaForest extends BiomeBeta {
    private static final WorldGenBirchTree BIRCH_TREE_FEATURE = new WorldGenBirchTree(false, false);
    
    public BiomeBetaForest() {
        super(new BiomeProperties("Beta Forest")
            .setTemperature(0.7f)
            .setRainfall(0.8f)
            .setBaseHeight(BASE_HEIGHT_HIGH)
            .setHeightVariation(HEIGHT_VARY_HIGH)
        );

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        if (random.nextInt(5) == 0) {
            return BIRCH_TREE_FEATURE;
        }
        
        if (random.nextInt(3) == 0) {
            return ModernBetaBiome.BIG_TREE_FEATURE;
        }
        
        return ModernBetaBiome.TREE_FEATURE;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        if (!settings.useBirchTrees)
            // Revert to pre-Beta behavior of spawning fancy oaks with 1/10 chance instead of 1/3
            return super.getRandomTreeFeature(random);
        
        return this.getRandomTreeFeature(random);
    }
    
    @Override
    protected void populateAdditionalWolves() {
        this.additionalWolves.add(WOLF_FOREST);
    }
}
