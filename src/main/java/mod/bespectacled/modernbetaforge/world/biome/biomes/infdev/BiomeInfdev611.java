package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.world.biome.BiomeDecorator;

public class BiomeInfdev611 extends ModernBetaBiome {
    public BiomeInfdev611() {
        super(new BiomeProperties("Infdev 611")
            .setTemperature(0.5f)
            .setRainfall(0.5f)
            .setBaseHeight(0.37f)
            .setHeightVariation(0.5f)
        );
        
        this.skyColor = ModernBetaBiomeColors.INFDEV_611_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.INFDEV_611_FOG_COLOR;
        
        this.grassColor = ModernBetaBiomeColors.OLD_GRASS_COLOR;
        this.foliageColor = ModernBetaBiomeColors.OLD_FOLIAGE_COLOR;
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorInfdev611());
    }
}
