package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Iterator;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.passive.EntityRabbit;

public class BiomeBetaIceDesert extends BiomeBeta {
    public BiomeBetaIceDesert() {
        super(new BiomeProperties("Beta Ice Desert")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setBaseHeight(0.2f)
            .setHeightVariation(0.3f)
            .setSnowEnabled()
        );
        
        this.topBlock = BlockStates.SAND;
        this.fillerBlock = BlockStates.SAND;
        
        // Should always clear to prevent passive mobs from spawning in desert
        this.spawnableCreatureList.clear();
        
        if (ModernBetaConfig.mobOptions.useNewMobs) {
            this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 10, 2, 3));
            this.spawnableCreatureList.add(new SpawnListEntry(EntityPolarBear.class, 1, 1, 2));
            
            Iterator<SpawnListEntry> monsterIterator = this.spawnableMonsterList.iterator();
            while (monsterIterator.hasNext()) {
                SpawnListEntry spawnListEntry = monsterIterator.next();
                if (spawnListEntry.entityClass == EntitySkeleton.class) {
                    monsterIterator.remove();
                }
            }
            
            this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 20, 4, 4));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityStray.class, 80, 4, 4));
        }
        
        this.skyColor = ModernBetaBiomeColors.BETA_COLD_SKY_COLOR;
    }
    
}
