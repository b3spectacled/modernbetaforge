package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaIceDesert extends BiomeBeta {
    public BiomeBetaIceDesert() {
        super(new BiomeProperties("Beta Ice Desert")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setBaseHeight(BASE_HEIGHT_LOW)
            .setHeightVariation(HEIGHT_VARY_LOW)
            .setSnowEnabled()
        );
        
        this.topBlock = BlockStates.SAND;
        this.fillerBlock = BlockStates.SAND;
        
        // Should always clear to prevent passive mobs from spawning in desert
        this.spawnableCreatureList.clear();
        
        this.skyColor = ModernBetaBiomeColors.BETA_COLD_SKY_COLOR;
    }
    
    @Override
    protected void populateAdditionalCreatures() {
        this.additionalCreatures.add(RABBIT_TUNDRA);
        this.additionalCreatures.add(POLAR_BEAR);
    }
    
    @Override
    protected void populateAdditionalMonsters() {
        super.populateAdditionalMonsters();
        this.additionalMonsters.add(STRAY);
        
        // Vanilla spawners
        // this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 20, 4, 4));
        // this.spawnableMonsterList.add(new SpawnListEntry(EntityStray.class, 80, 4, 4));
    }
    
}
