package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.world.biome.BiomeDecorator;

public class BiomeInfdev227 extends BiomeInfdev {
    public BiomeInfdev227() {
        super(new BiomeProperties("Infdev 227"));
        
        this.skyColor = ModernBetaBiomeColors.INFDEV_227_SKY_COLOR;
        this.fogColor = ModernBetaBiomeColors.INFDEV_227_FOG_COLOR;
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorInfdev227());
    }
}
