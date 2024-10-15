package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeBetaSavanna extends BiomeBeta {
    private static final WorldGenSavannaTree ACACIA_TREE_FEATURE = new WorldGenSavannaTree(false);
    
    public BiomeBetaSavanna() {
        super(new BiomeProperties("Beta Savanna")
            .setTemperature(0.7f)
            .setRainfall(0.1f)
            .setBaseHeight(0.2f)
            .setHeightVariation(0.3f)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        if (ModernBetaConfig.mobOptions.useNewMobs) {
            this.spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 1, 2, 6));
            this.spawnableCreatureList.add(new SpawnListEntry(EntityDonkey.class, 1, 1, 1));
            this.spawnableCreatureList.add(new SpawnListEntry(EntityLlama.class, 1, 4, 4));
        }

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        if (!settings.useAcaciaTrees)
            return super.getRandomTreeFeature(random);
        
        return ACACIA_TREE_FEATURE;
    }
}
