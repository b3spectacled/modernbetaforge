package mod.bespectacled.modernbetaforge.util;

import java.util.Random;

public class NoOpRandom extends Random {
    private static final long serialVersionUID = -1284630038060495005L;
    
    @Override
    public float nextFloat() {
        return 0.0f;
    }
    
    @Override
    public double nextDouble() {
        return 0.0;
    }
}
