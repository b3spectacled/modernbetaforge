package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.NoiseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class Infdev227SurfaceBuilder extends NoiseSurfaceBuilder {    
    public Infdev227SurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings, false, false, false);
    }

    @Override
    public boolean isBeach(int x, int z, Random random) {
        return false;
    }

    @Override
    public boolean isGravelBeach(int x, int z, Random random) {
        return false;
    }
    
    @Override
    public int sampleSurfaceDepth(int x, int z, Random random) {
        return 1;
    }

    @Override
    public boolean isBasin(int surfaceDepth) {
        return false;
    }
    
    @Override
    public boolean atBeachDepth(int y) {
        return false;
    }
}
