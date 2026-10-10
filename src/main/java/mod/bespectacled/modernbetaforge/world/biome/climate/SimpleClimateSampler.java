package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import net.minecraft.util.math.MathHelper;

public class SimpleClimateSampler {
    public static final int VERSION_V1_10_2_0 = 11020;
    public static final int VERSION_V1_10_3_0 = 11030;
    
    private static final int CLIMATE_OCTAVES = 4;
    private static final int DETAIL_OCTAVES = 2;
    
    private final SimplexOctaveNoise climateOctaveNoise;
    private final SimplexOctaveNoise detailOctaveNoise;
    
    private final double climateScale;
    private final double detailScale;
    private final int version;
    
    public SimpleClimateSampler(long seed, long climateSeed, long detailSeed, int version) {
        this.climateOctaveNoise = new SimplexOctaveNoise(new Random(seed * climateSeed), CLIMATE_OCTAVES);
        this.detailOctaveNoise = new SimplexOctaveNoise(new Random(seed * detailSeed), DETAIL_OCTAVES);
        
        this.climateScale = 0.025;
        this.detailScale = 0.050;
        this.version = version;
    }
    
    public double sample(double x, double z) {
        if (this.version <= VERSION_V1_10_2_0) {
            return this.sampleV11020(x, z);
        }
        
        return this.sampleV11030(x, z);
    }
    
    private double sampleV11030(double x, double z) {
        double climate = this.climateOctaveNoise.sample(x, z, this.climateScale, this.climateScale, 0.25);
        double detail = this.detailOctaveNoise.sample(x, z, this.detailScale, this.detailScale, 0.33333333333333331);
        
        detail = ((detail * 0.675) + 1.0) / 2.0;
        climate = ((climate * 0.1675) + 1.0) / 2.0;
        climate = climate * 0.99 + detail * 0.01;
        
        return MathHelper.clamp(climate, 0.0, 1.0);
    }
    
    private double sampleV11020(double x, double z) {
        double climateScale = 0.025;
        double detailScale = 0.050;
        
        double climate = this.climateOctaveNoise.sample(x, z, climateScale, climateScale, 0.25);
        double detail = this.detailOctaveNoise.sample(x, z, detailScale, detailScale, 0.33333333333333331);
        
        detail = detail * 1.1 + 0.5;
        climate = climate * 0.15 + 0.5;
        climate = climate * 0.99 + detail * 0.01;
        
        return MathHelper.clamp(climate, 0.0, 1.0);
    }
    
    public static int getVersion() {
        return VERSION_V1_10_3_0;
    }
}
