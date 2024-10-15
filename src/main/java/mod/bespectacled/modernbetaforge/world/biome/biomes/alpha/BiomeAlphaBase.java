package mod.bespectacled.modernbetaforge.world.biome.biomes.alpha;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.world.biome.BiomeDecorator;

public abstract class BiomeAlphaBase extends ModernBetaBiome {
    public BiomeAlphaBase(BiomeProperties properties) {
        super(properties
            .setBaseHeight(0.37f)
            .setHeightVariation(0.5f)
        );
        
        this.skyColor = ModernBetaBiomeColors.ALPHA_SKY_COLOR;
        
        this.grassColor = ModernBetaBiomeColors.OLD_GRASS_COLOR;
        this.foliageColor = ModernBetaBiomeColors.OLD_FOLIAGE_COLOR;
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorAlpha());
    }
}
