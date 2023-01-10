package mod.bespectacled.modernbetaforge.world.biome.beta;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;

public class BiomeBetaSavanna extends BiomeBeta {
    public BiomeBetaSavanna() {
        super(new BiomeProperties("Beta Savanna")
            .setTemperature(0.7F)
            .setRainfall(0.1F)
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
    
}
