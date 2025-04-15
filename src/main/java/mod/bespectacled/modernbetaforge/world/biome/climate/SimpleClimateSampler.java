package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import net.minecraft.util.math.MathHelper;

public class SimpleClimateSampler {
    private final SimplexOctaveNoise climateOctaveNoise;
    private final SimplexOctaveNoise detailOctaveNoise;
    
    private final double climateScale;
    private final double detailScale;
    
    public SimpleClimateSampler(long seed, long climateSeedMultiplier, long detailSeedMultiplier) {
        this.climateOctaveNoise = new SimplexOctaveNoise(new Random(seed * climateSeedMultiplier), 4);
        this.detailOctaveNoise = new SimplexOctaveNoise(new Random(seed * detailSeedMultiplier), 2);
        
        this.climateScale = 0.025;
        this.detailScale = 0.050;
    }
    
    public double sample(int x, int z) {
        double climate = this.climateOctaveNoise.sample(x, z, this.climateScale, this.climateScale, 0.25);
        double detail = this.detailOctaveNoise.sample(x, z, this.detailScale, this.detailScale, 0.33333333333333331);

        detail = detail * 1.1 + 0.5;
        climate = (climate * 0.15 + 0.5) * 0.99 + detail * 0.01;
        
        return MathHelper.clamp(climate, 0.0, 1.0);
    }
}
