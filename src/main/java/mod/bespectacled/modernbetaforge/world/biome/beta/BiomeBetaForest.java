package mod.bespectacled.modernbetaforge.world.biome.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeBetaForest extends BiomeBeta {
    public BiomeBetaForest() {
        super(new BiomeProperties("Beta Forest")
            .setTemperature(0.7F)
            .setRainfall(0.8F)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 5, 4, 4));

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        if (random.nextInt(5) == 0) {
            return BiomeBeta.BIRCH_TREE_FEATURE;
        }
        
        if (random.nextInt(3) == 0) {
            return ModernBetaBiome.BIG_TREE_FEATURE;
        }
        
        return ModernBetaBiome.TREE_FEATURE;
    }
}
