package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import net.minecraft.world.biome.BiomeDecorator;

public class BiomeInfdev420 extends BiomeInfdev {
    public BiomeInfdev420() {
        super(new BiomeProperties("Infdev 420"));
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorInfdev420());
    }
}
