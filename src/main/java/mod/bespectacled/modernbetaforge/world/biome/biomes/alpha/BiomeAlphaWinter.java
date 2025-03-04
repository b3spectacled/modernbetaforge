package mod.bespectacled.modernbetaforge.world.biome.biomes.alpha;

public class BiomeAlphaWinter extends BiomeAlpha {
    public BiomeAlphaWinter() {
        super(new BiomeProperties("Alpha Winter")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setSnowEnabled()
        );
    }
}
