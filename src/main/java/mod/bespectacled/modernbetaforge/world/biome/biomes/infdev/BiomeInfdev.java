package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeInfdev extends ModernBetaBiome {
    public BiomeInfdev(BiomeProperties properties) {
        super(properties
            .setTemperature(0.5f)
            .setRainfall(0.5f)
            .setBaseHeight(BASE_HEIGHT_HIGH)
            .setHeightVariation(HEIGHT_VARY_HIGH)
        );
        
        this.skyColor = ModernBetaBiomeColors.INFDEV_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.INFDEV_FOG_COLOR;
        
        this.grassColor = ModernBetaBiomeColors.OLD_GRASS_COLOR;
        this.foliageColor = ModernBetaBiomeColors.OLD_FOLIAGE_COLOR;
    }

    @Override
    protected void populateAdditionalCreatures() { 
        super.populateAdditionalCreatures();
        this.additionalCreatures.add(CHICKEN);
        this.additionalCreatures.add(COW);
    }

    @Override
    protected void populateAdditionalMonsters() {
        super.populateAdditionalMonsters();
        this.additionalMonsters.add(SLIME);
    }
    
    @Override
    protected void populateAdditionalWolves() {
        this.additionalWolves.add(WOLF_FOREST);
    }
    
    @Override
    protected void populateSpawnableCreatures() {
        this.spawnableCreatureList.clear();
        
        this.spawnableCreatureList.add(SHEEP);
        this.spawnableCreatureList.add(PIG);
    }
    
    @Override
    protected void populateSpawnableMonsters() {
        this.spawnableMonsterList.clear();
        
        this.spawnableMonsterList.add(SPIDER);
        this.spawnableMonsterList.add(ZOMBIE);
        this.spawnableMonsterList.add(SKELETON);
        this.spawnableMonsterList.add(CREEPER);
    }
}
