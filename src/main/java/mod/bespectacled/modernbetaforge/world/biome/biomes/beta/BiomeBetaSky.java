package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.entity.passive.EntityChicken;

public class BiomeBetaSky extends BiomeBeta {
    public BiomeBetaSky() {
        super(new BiomeProperties("Beta Sky")
            .setTemperature(0.5f)
            .setRainfall(0.0f)
            .setBaseHeight(BASE_HEIGHT_LOW)
            .setHeightVariation(HEIGHT_VARY_LOW)
            .setRainDisabled()
        );

        this.skyColor = ModernBetaBiomeColors.SKYLANDS_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.SKYLANDS_FOG_COLOR;
        
        this.spawnableCreatureList.clear();
        this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10, 4, 4));
        
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        
        this.additionalCreatures.clear();
        this.additionalMonsters.clear();
        this.additionalWaterCreatures.clear();
        this.additionalCaveCreatures.clear();
    }
    
}
