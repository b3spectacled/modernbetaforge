package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.world.biome.BiomeDecorator;

public class BiomeInfdev420 extends ModernBetaBiome {
    public BiomeInfdev420() {
        super(new BiomeProperties("Infdev 420")
            .setTemperature(0.5f)
            .setRainfall(0.5f)
            .setBaseHeight(BASE_HEIGHT_HIGH)
            .setHeightVariation(HEIGHT_VARY_HIGH)
        );
        
        this.skyColor = ModernBetaBiomeColors.INFDEV_420_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.INFDEV_420_FOG_COLOR;
        
        this.grassColor = ModernBetaBiomeColors.OLD_GRASS_COLOR;
        this.foliageColor = ModernBetaBiomeColors.OLD_FOLIAGE_COLOR;
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorInfdev420());
    }
}
