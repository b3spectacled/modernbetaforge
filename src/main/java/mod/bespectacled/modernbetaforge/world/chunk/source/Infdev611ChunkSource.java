package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class Infdev611ChunkSource extends AlphaChunkSource {
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    public Infdev611ChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings, true);

        // Invoke noise constructors to seed surface and forest noise generators correctly,
        // and also spin up new instance of Random since class member has been used in super constructor.
        
        Random random = new Random(seed);
        
        new PerlinOctaveNoise(random, 16, true);
        new PerlinOctaveNoise(random, 16, true);
        new PerlinOctaveNoise(random, 8, true);
        this.beachOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(random, 4, true);
        new PerlinOctaveNoise(random, 10, true);
        new PerlinOctaveNoise(random, 16, true);
        this.forestOctaveNoise = new PerlinOctaveNoise(random, 8, true);

        this.setBeachOctaveNoise(this.beachOctaveNoise);
        this.setSurfaceOctaveNoise(this.surfaceOctaveNoise);
        this.setForestOctaveNoise(this.forestOctaveNoise);
    }
}
