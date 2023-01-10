package mod.bespectacled.modernbetaforge.world.biome.beta;

import java.util.Iterator;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.passive.EntityRabbit;

public class BiomeBetaTundra extends BiomeBeta {
    public BiomeBetaTundra() {
        super(new BiomeProperties("Beta Tundra")
            .setTemperature(0.0F)
            .setRainfall(0.5F)
            .setSnowEnabled()
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        if (ModernBetaConfig.mobOptions.useNewMobs) {
            this.spawnableCreatureList.clear();
            this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 10, 2, 3));
            this.spawnableCreatureList.add(new SpawnListEntry(EntityPolarBear.class, 1, 1, 2));
        }
        
        Iterator<SpawnListEntry> monsterIterator = this.spawnableMonsterList.iterator();
        while (monsterIterator.hasNext()) {
            SpawnListEntry spawnListEntry = monsterIterator.next();
            if (spawnListEntry.entityClass == EntitySkeleton.class) {
                monsterIterator.remove();
            }
        }
        
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 20, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityStray.class, 80, 4, 4));

        this.skyColor = ModernBetaBiomeColors.BETA_COLD_SKY_COLOR;
    }
    
}
