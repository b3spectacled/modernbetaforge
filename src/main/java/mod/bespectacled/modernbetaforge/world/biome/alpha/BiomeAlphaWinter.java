package mod.bespectacled.modernbetaforge.world.biome.alpha;

public class BiomeAlphaWinter extends BiomeAlphaBase {
    public BiomeAlphaWinter() {
        super(new BiomeProperties("Alpha Winter")
            .setTemperature(0.0f)
            .setRainfall(0.5f)
            .setSnowEnabled()
        );
    }
}
