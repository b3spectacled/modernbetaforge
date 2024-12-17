package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

import mod.bespectacled.modernbetaforge.util.MathUtil;

public class NoiseSource {
    private final NoiseColumnSampler noiseColumnSampler;
    
    private final int noiseResX;
    private final int noiseResY;
    private final int noiseResZ;
    private final int noiseSize;
    
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
    
    /**
     * Constructs a new noise source with which to sample terrain densities.
     * 
     * @param noiseColumnSampler Noise sampler for all densities in a column for a particular x/z noise coordinate.
     * @param noiseSizeX Number of subchunks in the x-axis of a chunk.
     * @param noiseSizeY Number of subchunks in world height.
     * @param noiseSizeZ Number of subchunks in the z-axis of a chunk.
     */
    public NoiseSource(
        NoiseColumnSampler noiseColumnSampler,
        int noiseSizeX, 
        int noiseSizeY, 
        int noiseSizeZ
    ) {
        this.noiseColumnSampler = noiseColumnSampler;
        
        this.noiseResX = noiseSizeX + 1;
        this.noiseResY = noiseSizeY + 1;
        this.noiseResZ = noiseSizeZ + 1;
        this.noiseSize = this.noiseResX * this.noiseResY * this.noiseResZ;
    }
    
    /**
     * Samples the initial densities for an entire chunk, given starting noise coordinates.
     * 
     * @param startNoiseX x-coordinate in noise coordinates.
     * @param startNoiseZ z-coordinate in noise coordinates.
     */
    public final void sampleInitialNoise(int startNoiseX, int startNoiseZ) {
        this.noise = this.sampleNoise(startNoiseX, startNoiseZ);
        
        if (this.noise.length != this.noiseSize)
            throw new IllegalStateException("[Modern Beta] Noise array length is invalid!");
    }
    
    /**
     * Gets and stores the densities from the corners of the subchunk, given noise coordinates.
     * 
     * @param noiseX x-coordinate in noise coordinates.
     * @param noiseY y-coordinate in noise coordinates.
     * @param noiseZ z-coordinate in noise coordinates.
     */
    public final void sampleNoiseCorners(int noiseX, int noiseY, int noiseZ) {
        this.lowerNW = this.noise[((noiseX + 0) * this.noiseResX + (noiseZ + 0)) * this.noiseResY + (noiseY + 0)];
        this.lowerSW = this.noise[((noiseX + 0) * this.noiseResX + (noiseZ + 1)) * this.noiseResY + (noiseY + 0)];
        this.lowerNE = this.noise[((noiseX + 1) * this.noiseResX + (noiseZ + 0)) * this.noiseResY + (noiseY + 0)];
        this.lowerSE = this.noise[((noiseX + 1) * this.noiseResX + (noiseZ + 1)) * this.noiseResY + (noiseY + 0)];
        
        this.upperNW = this.noise[((noiseX + 0) * this.noiseResX + (noiseZ + 0)) * this.noiseResY + (noiseY + 1)]; 
        this.upperSW = this.noise[((noiseX + 0) * this.noiseResX + (noiseZ + 1)) * this.noiseResY + (noiseY + 1)];
        this.upperNE = this.noise[((noiseX + 1) * this.noiseResX + (noiseZ + 0)) * this.noiseResY + (noiseY + 1)];
        this.upperSE = this.noise[((noiseX + 1) * this.noiseResX + (noiseZ + 1)) * this.noiseResY + (noiseY + 1)];
    }
    
    /**
     * Interpolates along the y-axis given a delta.
     * 
     * @param deltaY The current delta, based on current y-position within a subchunk.
     */
    public final void sampleNoiseY(double deltaY) {
        this.nw = MathUtil.lerp(deltaY, this.lowerNW, this.upperNW);
        this.sw = MathUtil.lerp(deltaY, this.lowerSW, this.upperSW);
        this.ne = MathUtil.lerp(deltaY, this.lowerNE, this.upperNE);
        this.se = MathUtil.lerp(deltaY, this.lowerSE, this.upperSE);
    }

    /**
     * Interpolates along the x-axis given a delta.
     * 
     * @param deltaX The current delta, based on current x-position within a subchunk.
     */
    public final void sampleNoiseX(double deltaX) {
        this.n = MathUtil.lerp(deltaX, this.nw, this.ne);
        this.s = MathUtil.lerp(deltaX, this.sw, this.se);
    }
    
    /**
     * Interpolates the final density along the z-axis given a delta
     * 
     * @param deltaZ The current delta, based on current z-position within a subchunk.
     */
    public final void sampleNoiseZ(double deltaZ) {
        this.density = MathUtil.lerp(deltaZ, this.n, this.s);
    }
    
    /**
     * Gets the interpolated density.
     * 
     * @return The terrain density.
     */
    public final double sample() {
        return this.density;
    }

    /**
     * Samples the initial densities for an entire chunk, given starting noise coordinates.
     * 
     * @param startNoiseX x-coordinate in noise coordinates.
     * @param startNoiseZ z-coordinate in noise coordinates.
     * @return Array containing the initial densities for the chunk.
     */
    private double[] sampleNoise(int startNoiseX, int startNoiseZ) {
        double[] buffer = new double[this.noiseResY];
        double[] noise = new double[this.noiseSize];
        
        int ndx = 0;
        for (int localNoiseX = 0; localNoiseX < this.noiseResX; ++localNoiseX) {
            for (int localNoiseZ = 0; localNoiseZ < this.noiseResZ; ++localNoiseZ) {
                this.noiseColumnSampler.sampleNoiseColumn(
                    buffer,
                    startNoiseX,
                    startNoiseZ,
                    localNoiseX,
                    localNoiseZ,
                    this.noiseResX - 1,
                    this.noiseResZ - 1,
                    this.noiseResY - 1
                );
                
                for (int nY = 0; nY < this.noiseResY; ++nY) {
                    noise[ndx++] = buffer[nY];
                }
            }
        }
        
        return noise;
    }

    @FunctionalInterface
    public static interface NoiseColumnSampler {
        /**
         * 
         * @param buffer The array to store initial densities in for the column.
         * @param startNoiseX x-coordinate in noise coordinates, at the beginning of the chunk.
         * @param startNoiseZ z-coordinate in noise coordinates, at the beginning of the chunk.
         * @param localNoiseX x-coordinate in noise coordinates, the current position in the chunk.
         * @param localNoiseZ z-coordinate in noise coordinates, the current position in the chunk.
         * @param noiseSizeX Number of subchunks in the x-axis of a chunk.
         * @param noiseSizeY Number of subchunks in world height.
         * @param noiseSizeZ Number of subchunks in the z-axis of a chunk.
         */
        public void sampleNoiseColumn(
            double[] buffer,
            int startNoiseX,
            int startNoiseZ,
            int localNoiseX,
            int localNoiseZ,
            int noiseSizeX,
            int noiseSizeZ,
            int noiseSizeY
        );
    }
}
