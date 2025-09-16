package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

import java.util.List;

import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.ObjectPool;

public final class NoiseSource {
    private final NoiseColumnSampler noiseColumnSampler;
    
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    private final double[] noise;
    
    private final ObjectPool<double[]> bufferPool;

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
    public NoiseSource(NoiseColumnSampler noiseColumnSampler, int noiseSizeX, int noiseSizeY, int noiseSizeZ) {
        this.noiseColumnSampler = noiseColumnSampler;
        
        this.noiseSizeX = noiseSizeX;
        this.noiseSizeY = noiseSizeY;
        this.noiseSizeZ = noiseSizeZ;
        this.noise = new double[(noiseSizeX + 1) * (noiseSizeY + 1) * (noiseSizeZ + 1)];
        
        this.bufferPool = new ObjectPool<>(() -> new double[(this.noiseSizeY + 1)]);
    }
    
    /**
     * Samples the initial densities for an entire chunk, given starting noise coordinates.
     * 
     * @param startNoiseX x-coordinate in noise coordinates.
     * @param startNoiseZ z-coordinate in noise coordinates.
     * @param noiseSettings Noise settings, including slides.
     * @param noiseSamplers List of {@link NoiseSampler noise samplers}.
     */
    public final void sampleInitialNoise(int startNoiseX, int startNoiseZ, NoiseSettings noiseSettings, List<NoiseSampler> noiseSamplers) {
        this.sampleNoise(startNoiseX, startNoiseZ, noiseSettings, noiseSamplers);
    }
    
    /**
     * Gets and stores the densities from the corners of the subchunk, given noise coordinates.
     * 
     * @param noiseX x-coordinate in noise coordinates.
     * @param noiseY y-coordinate in noise coordinates.
     * @param noiseZ z-coordinate in noise coordinates.
     */
    public final void sampleNoiseCorners(int noiseX, int noiseY, int noiseZ) {
        this.lowerNW = this.noise[((noiseX + 0) * (this.noiseSizeX + 1) + (noiseZ + 0)) * (this.noiseSizeY + 1) + (noiseY + 0)];
        this.lowerSW = this.noise[((noiseX + 0) * (this.noiseSizeX + 1) + (noiseZ + 1)) * (this.noiseSizeY + 1) + (noiseY + 0)];
        this.lowerNE = this.noise[((noiseX + 1) * (this.noiseSizeX + 1) + (noiseZ + 0)) * (this.noiseSizeY + 1) + (noiseY + 0)];
        this.lowerSE = this.noise[((noiseX + 1) * (this.noiseSizeX + 1) + (noiseZ + 1)) * (this.noiseSizeY + 1) + (noiseY + 0)];
        
        this.upperNW = this.noise[((noiseX + 0) * (this.noiseSizeX + 1) + (noiseZ + 0)) * (this.noiseSizeY + 1) + (noiseY + 1)]; 
        this.upperSW = this.noise[((noiseX + 0) * (this.noiseSizeX + 1) + (noiseZ + 1)) * (this.noiseSizeY + 1) + (noiseY + 1)];
        this.upperNE = this.noise[((noiseX + 1) * (this.noiseSizeX + 1) + (noiseZ + 0)) * (this.noiseSizeY + 1) + (noiseY + 1)];
        this.upperSE = this.noise[((noiseX + 1) * (this.noiseSizeX + 1) + (noiseZ + 1)) * (this.noiseSizeY + 1) + (noiseY + 1)];
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
     * @param noiseSettings Noise settings, including slides.
     * @param noiseSamplers List of {@link NoiseSampler noise samplers}.
     */
    private void sampleNoise(int startNoiseX, int startNoiseZ, NoiseSettings noiseSettings, List<NoiseSampler> noiseSamplers) {
        int ndx = 0;
        
        for (int localNoiseX = 0; localNoiseX < this.noiseSizeX + 1; ++localNoiseX) {
            for (int localNoiseZ = 0; localNoiseZ < this.noiseSizeZ + 1; ++localNoiseZ) {
                double[] buffer = this.bufferPool.get();
                this.sampleNoiseColumn(buffer, startNoiseX, startNoiseZ, localNoiseX, localNoiseZ, noiseSettings, noiseSamplers);
                
                for (int noiseY = 0; noiseY < buffer.length; ++noiseY) {
                    this.noise[ndx++] = buffer[noiseY];
                    
                    // Clear out buffer before returning to pool
                    buffer[noiseY] = 0.0;
                }
                
                this.bufferPool.release(buffer);
            }
        }
    }
    
    /**
     * Samples the initial densities for a noise column.
     * 
     * @param Buffer array for noise values.
     * @param startNoiseX x-coordinate in noise coordinates.
     * @param startNoiseZ z-coordinate in noise coordinates.
     * @param localNoiseX x-coordinate for current chunk in noise coordinates.
     * @param localNoiseZ z-coordinate for current chunk in noise coordinates.
     * @param noiseSettings Noise settings, including slides.
     * @param noiseSamplers List of {@link NoiseSampler noise samplers}.
     */
    private void sampleNoiseColumn(
        double[] buffer,
        int startNoiseX,
        int startNoiseZ,
        int localNoiseX,
        int localNoiseZ,
        NoiseSettings noiseSettings,
        List<NoiseSampler> noiseSamplers
    ) {
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        this.noiseColumnSampler.sampleNoiseColumn(
            buffer,
            startNoiseX,
            startNoiseZ,
            localNoiseX,
            localNoiseZ,
            this.noiseSizeX,
            this.noiseSizeY,
            this.noiseSizeZ
        );
        
        for (int noiseY = 0; noiseY < this.noiseSizeY + 1; ++noiseY) {
            double density = buffer[noiseY];
            
            for (int i = 0; i < noiseSamplers.size(); ++i) {
                density = noiseSamplers.get(i).sample(
                    density,
                    noiseX,
                    noiseY,
                    noiseZ,
                    this.noiseSizeX,
                    this.noiseSizeY,
                    this.noiseSizeZ
                );
            }
            
            density = noiseSettings.topSlideSettings.applyTopSlide(density, noiseY, this.noiseSizeY);
            density = noiseSettings.bottomSlideSettings.applyBottomSlide(density, noiseY);
            
            buffer[noiseY] = density;
        }
    }
}
