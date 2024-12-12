package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;

public class NoiseSource {
    private final NoiseColumnSampler noiseColumnSampler;
    
    private final int noiseResX;
    private final int noiseResY;
    private final int noiseResZ;
    private final int noiseSize;
    
    private final boolean sampleForHeight;
    
    protected final ModernBetaChunkGeneratorSettings settings;
    
    private double[] noise;

    private double lowerNW;
    private double lowerSW;
    private double lowerNE;
    private double lowerSE;
    
    private double upperNW; 
    private double upperSW;
    private double upperNE;
    private double upperSE;
    
    private double nw;
    private double ne;
    private double sw;
    private double se;
    
    private double n;
    private double s;
    
    private double density;
    
    public NoiseSource(
        NoiseColumnSampler noiseColumnSampler,
        int noiseSizeX, 
        int noiseSizeY, 
        int noiseSizeZ,
        boolean sampleForHeight,
        ModernBetaChunkGeneratorSettings settings
    ) {
        this.noiseColumnSampler = noiseColumnSampler;
        
        this.noiseResX = noiseSizeX + 1;
        this.noiseResY = noiseSizeY + 1;
        this.noiseResZ = noiseSizeZ + 1;
        this.noiseSize = this.noiseResX * this.noiseResY * this.noiseResZ;
        
        this.sampleForHeight = sampleForHeight;
        
        this.settings = settings;
    }
    
    public NoiseSource(
        NoiseColumnSampler noiseColumnSampler,
        int noiseSizeX, 
        int noiseSizeY, 
        int noiseSizeZ,
        ModernBetaChunkGeneratorSettings settings
    ) {
        this(noiseColumnSampler, noiseSizeX, noiseSizeY, noiseSizeZ, true, settings);
    }
    
    public final void sampleInitialNoise(int startNoiseX, int startNoiseZ) {
        this.noise = this.sampleNoise(startNoiseX, startNoiseZ);
        
        if (this.noise.length != this.noiseSize)
            throw new IllegalStateException("[Modern Beta] Noise array length is invalid!");
    }
    
    public final void sampleNoiseCorners(int subChunkX, int subChunkY, int subChunkZ) {
        this.lowerNW = this.noise[((subChunkX + 0) * this.noiseResX + (subChunkZ + 0)) * this.noiseResY + (subChunkY + 0)];
        this.lowerSW = this.noise[((subChunkX + 0) * this.noiseResX + (subChunkZ + 1)) * this.noiseResY + (subChunkY + 0)];
        this.lowerNE = this.noise[((subChunkX + 1) * this.noiseResX + (subChunkZ + 0)) * this.noiseResY + (subChunkY + 0)];
        this.lowerSE = this.noise[((subChunkX + 1) * this.noiseResX + (subChunkZ + 1)) * this.noiseResY + (subChunkY + 0)];
        
        this.upperNW = this.noise[((subChunkX + 0) * this.noiseResX + (subChunkZ + 0)) * this.noiseResY + (subChunkY + 1)]; 
        this.upperSW = this.noise[((subChunkX + 0) * this.noiseResX + (subChunkZ + 1)) * this.noiseResY + (subChunkY + 1)];
        this.upperNE = this.noise[((subChunkX + 1) * this.noiseResX + (subChunkZ + 0)) * this.noiseResY + (subChunkY + 1)];
        this.upperSE = this.noise[((subChunkX + 1) * this.noiseResX + (subChunkZ + 1)) * this.noiseResY + (subChunkY + 1)];
    }
    
    public final void sampleNoiseY(double deltaY) {
        this.nw = MathUtil.lerp(deltaY, this.lowerNW, this.upperNW);
        this.sw = MathUtil.lerp(deltaY, this.lowerSW, this.upperSW);
        this.ne = MathUtil.lerp(deltaY, this.lowerNE, this.upperNE);
        this.se = MathUtil.lerp(deltaY, this.lowerSE, this.upperSE);
    }
    
    public final void sampleNoiseX(double deltaX) {
        this.n = MathUtil.lerp(deltaX, this.nw, this.ne);
        this.s = MathUtil.lerp(deltaX, this.sw, this.se);
    }
    
    public final void sampleNoiseZ(double deltaZ) {
        this.density = MathUtil.lerp(deltaZ, this.n, this.s);
    }
    
    public final double sample() {
        return this.density;
    }
    
    public final boolean sampleForHeight() {
        return this.sampleForHeight;
    }

    private double[] sampleNoise(int startNoiseX, int startNoiseZ) {
        double[] buffer = new double[this.noiseResY];
        double[] noise = new double[this.noiseSize];
        
        int ndx = 0;
        for (int localNoiseX = 0; localNoiseX < this.noiseResX; ++localNoiseX) {
            for (int localNoiseZ = 0; localNoiseZ < this.noiseResZ; ++localNoiseZ) {
                this.noiseColumnSampler.sampleNoiseColumn(buffer, startNoiseX, startNoiseZ, localNoiseX, localNoiseZ);
                
                for (int nY = 0; nY < this.noiseResY; ++nY) {
                    noise[ndx++] = buffer[nY];
                }
            }
        }
        
        return noise;
    }

    @FunctionalInterface
    public static interface NoiseColumnSampler {
        public void sampleNoiseColumn(double[] buffer, int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ);
    }
}
