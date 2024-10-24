package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeBetaSavanna extends BiomeBeta {
    private static final WorldGenSavannaTree ACACIA_TREE_FEATURE = new WorldGenSavannaTree(false);
    
    public BiomeBetaSavanna() {
        super(new BiomeProperties("Beta Savanna")
            .setTemperature(0.7f)
            .setRainfall(0.1f)
            .setBaseHeight(BASE_HEIGHT_LOW)
            .setHeightVariation(HEIGHT_VARY_LOW)
        );

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        if (!settings.useAcaciaTrees)
            return super.getRandomTreeFeature(random);
        
        if (random.nextInt(5) == 0) {
            return ModernBetaBiome.TREE_FEATURE;
        }
        
        return ACACIA_TREE_FEATURE;
    }
    
    @Override
    protected void populateAdditionalCreatures() {
        this.additionalCreatures.add(HORSE_SAVANNA);
        this.additionalCreatures.add(DONKEY_SAVANNA);
        this.additionalCreatures.add(LLAMA);
    }
}
