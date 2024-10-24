package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;

public class BiomeBetaTundra extends BiomeBeta {
    public BiomeBetaTundra() {
        super(new BiomeProperties("Beta Tundra")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setBaseHeight(BASE_HEIGHT_LOW)
            .setHeightVariation(HEIGHT_VARY_LOW)
            .setSnowEnabled()
        );

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
