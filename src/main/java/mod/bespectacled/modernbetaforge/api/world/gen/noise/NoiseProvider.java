package mod.bespectacled.modernbetaforge.api.world.gen.noise;

import mod.bespectacled.modernbetaforge.util.MathUtil;

public abstract class NoiseProvider {
    protected final int noiseSizeX;
    protected final int noiseSizeY;
    protected final int noiseSizeZ;
    
    protected final int noiseResX;
    protected final int noiseResY;
    protected final int noiseResZ;
    
    protected final int noiseSize;
    
    protected double[] noise;

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
    
    public NoiseProvider(
        int noiseSizeX, 
        int noiseSizeY, 
        int noiseSizeZ
    ) {
        this.noiseSizeX = noiseSizeX;
        this.noiseSizeY = noiseSizeY;
        this.noiseSizeZ = noiseSizeZ;
        
        this.noiseResX = noiseSizeX + 1;
        this.noiseResY = noiseSizeY + 1;
        this.noiseResZ = noiseSizeZ + 1;
        
        this.noiseSize = this.noiseResX * this.noiseResY * this.noiseResZ;
    }
    
    public void sampleInitialNoise(int startNoiseX, int startNoiseZ) {
        this.noise = this.sampleNoise(startNoiseX, startNoiseZ);
        
        if (this.noise.length != this.noiseSize)
            throw new IllegalStateException("[Modern Beta] Noise array length is invalid!");
    }
    
    public void sampleNoiseCorners(int subChunkX, int subChunkY, int subChunkZ) {
        this.lowerNW = this.noise[((subChunkX + 0) * this.noiseResX + (subChunkZ + 0)) * this.noiseResY + (subChunkY + 0)];
        this.lowerSW = this.noise[((subChunkX + 0) * this.noiseResX + (subChunkZ + 1)) * this.noiseResY + (subChunkY + 0)];
        this.lowerNE = this.noise[((subChunkX + 1) * this.noiseResX + (subChunkZ + 0)) * this.noiseResY + (subChunkY + 0)];
        this.lowerSE = this.noise[((subChunkX + 1) * this.noiseResX + (subChunkZ + 1)) * this.noiseResY + (subChunkY + 0)];
        
        this.upperNW = this.noise[((subChunkX + 0) * this.noiseResX + (subChunkZ + 0)) * this.noiseResY + (subChunkY + 1)]; 
        this.upperSW = this.noise[((subChunkX + 0) * this.noiseResX + (subChunkZ + 1)) * this.noiseResY + (subChunkY + 1)];
        this.upperNE = this.noise[((subChunkX + 1) * this.noiseResX + (subChunkZ + 0)) * this.noiseResY + (subChunkY + 1)];
        this.upperSE = this.noise[((subChunkX + 1) * this.noiseResX + (subChunkZ + 1)) * this.noiseResY + (subChunkY + 1)];
    }
    
    public void sampleNoiseY(double deltaY) {
        this.nw = MathUtil.lerp(deltaY, this.lowerNW, this.upperNW);
        this.sw = MathUtil.lerp(deltaY, this.lowerSW, this.upperSW);
        this.ne = MathUtil.lerp(deltaY, this.lowerNE, this.upperNE);
        this.se = MathUtil.lerp(deltaY, this.lowerSE, this.upperSE);
    }
    
    public void sampleNoiseX(double deltaX) {
        this.n = MathUtil.lerp(deltaX, this.nw, this.ne);
        this.s = MathUtil.lerp(deltaX, this.sw, this.se);
    }
    
    public void sampleNoiseZ(double deltaZ) {
        this.density = MathUtil.lerp(deltaZ, this.n, this.s);
    }
    
    public double sample() {
        return this.density;
    }
    
    protected abstract double[] sampleNoise(int startNoiseX, int startNoiseZ);
}
