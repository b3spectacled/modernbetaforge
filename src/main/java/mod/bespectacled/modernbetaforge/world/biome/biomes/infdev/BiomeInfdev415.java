package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import net.minecraft.world.biome.BiomeDecorator;

public class BiomeInfdev415 extends BiomeInfdev {
    public BiomeInfdev415() {
        super(new BiomeProperties("Infdev 415"));
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorInfdev415());
    }
}
