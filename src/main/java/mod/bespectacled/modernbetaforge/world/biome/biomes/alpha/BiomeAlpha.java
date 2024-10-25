package mod.bespectacled.modernbetaforge.world.biome.biomes.alpha;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.world.biome.BiomeDecorator;

public abstract class BiomeAlpha extends ModernBetaBiome {
    public BiomeAlpha(BiomeProperties properties) {
        super(properties
            .setBaseHeight(BASE_HEIGHT_TEMPERATE)
            .setHeightVariation(HEIGHT_VARY_TEMPERATE)
        );
        
        this.skyColor = ModernBetaBiomeColors.ALPHA_SKY_COLOR;
        
        this.grassColor = ModernBetaBiomeColors.OLD_GRASS_COLOR;
        this.foliageColor = ModernBetaBiomeColors.OLD_FOLIAGE_COLOR;
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorAlpha());
    }
    
    @Override
    protected void populateAdditionalWolves() {
        this.additionalWolves.add(WOLF_FOREST);
    }
}
