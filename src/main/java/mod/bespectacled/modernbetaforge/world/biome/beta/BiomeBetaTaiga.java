package mod.bespectacled.modernbetaforge.world.biome.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeBetaTaiga extends BiomeBeta {
    public BiomeBetaTaiga() {
        super(new BiomeProperties("Beta Taiga")
            .setTemperature(0.0F)
            .setRainfall(0.5F)
            .setSnowEnabled()
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 8, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 4, 2, 3));

        this.skyColor = ModernBetaBiomeColors.BETA_COOL_SKY_COLOR;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        if (random.nextInt(3) == 0) {
            return BiomeBeta.PINE_FEATURE_1;
        }
        
        return BiomeBeta.PINE_FEATURE_2;
    }
}
