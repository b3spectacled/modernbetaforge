package mod.bespectacled.modernbetaforge.world.biome.biomes.indev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeDecorator;

public class BiomeIndev extends ModernBetaBiome {

    public BiomeIndev(BiomeProperties properties) {
        super(properties
            .setBaseHeight(BASE_HEIGHT_TEMPERATE)
            .setHeightVariation(HEIGHT_VARY_TEMPERATE)
            .setTemperature(0.5f)
            .setRainfall(0.5f)
        );

        this.grassColor = ModernBetaBiomeColors.OLD_GRASS_COLOR;
        this.foliageColor = ModernBetaBiomeColors.OLD_FOLIAGE_COLOR;
        
        this.populateSpawnableMobs(EnumCreatureType.MONSTER, SPIDER, SKELETON, ZOMBIE, CREEPER);
        this.populateSpawnableMobs(EnumCreatureType.CREATURE, SHEEP, PIG);
        this.populateAdditionalMobs(EnumCreatureType.MONSTER, false, SLIME);
        this.populateAdditionalMobs(EnumCreatureType.CREATURE, false, CHICKEN, COW);
        this.populateAdditionalMobs(null, true, WOLF_FOREST);
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorIndev());
    }
}
