package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;

public class BiomeBetaForest extends BiomeBeta {
    private static final WorldGenBirchTree BIRCH_TREE_FEATURE = new WorldGenBirchTree(false, false);
    
    public BiomeBetaForest() {
        super(new BiomeProperties("Beta Forest")
            .setTemperature(0.7f)
            .setRainfall(0.8f)
            .setBaseHeight(0.37f)
            .setHeightVariation(0.5f)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 5, 4, 4));

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
            return super.getRandomTreeFeature(random);
        
        return this.getRandomTreeFeature(random);
    }
}
