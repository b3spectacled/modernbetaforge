package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

public class NoiseHeight {
    public static final NoiseHeight ZERO = new NoiseHeight(0.0, 0.0);
    
    public final double scale;
    public final double depth;
    
    /**
     * Constructs a container for scale and depth values for shaping terrain.
     * 
     * @param scale Value representing terrain scale or height variation
     * @param depth Value representing terrain depth or base height.
     */
    public NoiseHeight(double scale, double depth) {
        this.scale = scale;
        this.depth = depth;
    }
}
