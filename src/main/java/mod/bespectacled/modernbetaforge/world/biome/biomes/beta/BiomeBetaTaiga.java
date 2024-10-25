package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeBetaTaiga extends BiomeBeta {
    private static final WorldGenTaiga1 PINE_TREE_FEATURE_1 = new WorldGenTaiga1();
    private static final WorldGenTaiga2 PINE_TREE_FEATURE_2 = new WorldGenTaiga2(false);
    
    public BiomeBetaTaiga() {
        super(new BiomeProperties("Beta Taiga")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setBaseHeight(BASE_HEIGHT_TEMPERATE)
            .setHeightVariation(HEIGHT_VARY_TEMPERATE)
            .setSnowEnabled()
        );
        
        this.skyColor = ModernBetaBiomeColors.BETA_COOL_SKY_COLOR;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        if (random.nextInt(3) == 0) {
            return PINE_TREE_FEATURE_1;
        }
        
        return PINE_TREE_FEATURE_2;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        if (!settings.usePineTrees)
            return super.getRandomTreeFeature(random);
        
        return this.getRandomTreeFeature(random);
    }
    
    @Override
    protected void populateAdditionalCreatures() {
        this.additionalCreatures.add(RABBIT);
    }
    
    @Override
    protected void populateAdditionalWolves() {
        this.additionalWolves.add(WOLF_TAIGA);
    }
}
