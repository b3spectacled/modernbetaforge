package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import net.minecraft.world.biome.BiomeDecorator;

public class BiomeInfdev611 extends BiomeInfdev {
    public BiomeInfdev611() {
        super(new BiomeProperties("Infdev 611"));
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorInfdev611());
    }
}
