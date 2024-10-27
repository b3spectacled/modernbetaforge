package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.entity.EnumCreatureType;

public class BiomeInfdev extends ModernBetaBiome {
    public BiomeInfdev(BiomeProperties properties) {
        super(properties
            .setTemperature(0.5f)
            .setRainfall(0.5f)
            .setBaseHeight(BASE_HEIGHT_TEMPERATE)
            .setHeightVariation(HEIGHT_VARY_TEMPERATE)
        );
        
        this.skyColor = ModernBetaBiomeColors.INFDEV_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.INFDEV_FOG_COLOR;
        
        this.grassColor = ModernBetaBiomeColors.OLD_GRASS_COLOR;
        this.foliageColor = ModernBetaBiomeColors.OLD_FOLIAGE_COLOR;
        
        this.populateSpawnableMobs(EnumCreatureType.MONSTER, SPIDER, SKELETON, ZOMBIE, CREEPER);
        this.populateSpawnableMobs(EnumCreatureType.CREATURE, SHEEP, PIG);
        this.populateAdditionalMobs(EnumCreatureType.MONSTER, false, SLIME);
        this.populateAdditionalMobs(EnumCreatureType.CREATURE, false, CHICKEN, COW);
        this.populateAdditionalMobs(null, true, WOLF_FOREST);
    }
}
