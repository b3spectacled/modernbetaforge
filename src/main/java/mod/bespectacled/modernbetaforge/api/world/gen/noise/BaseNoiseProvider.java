package mod.bespectacled.modernbetaforge.api.world.gen.noise;

public class BaseNoiseProvider extends NoiseProvider {
    private final BaseColumnSampler bufferSampler;
    
    protected double[] heightmapNoise;
    
    public BaseNoiseProvider(
        int noiseSizeX, 
        int noiseSizeY, 
        int noiseSizeZ, 
        BaseColumnSampler bufferSampler
    ) {
        super(noiseSizeX, noiseSizeY, noiseSizeZ);
        
        this.bufferSampler = bufferSampler;
    }

    @Override
    protected double[] sampleNoise(int startNoiseX, int startNoiseZ) {
        double[] buffer = new double[this.noiseResY];
        double[] noise = new double[this.noiseSize];
        
        int ndx = 0;
        for (int localNoiseX = 0; localNoiseX < this.noiseResX; ++localNoiseX) {
            for (int localNoiseZ = 0; localNoiseZ < this.noiseResZ; ++localNoiseZ) {
                this.bufferSampler.sampleColumn(buffer, startNoiseX, startNoiseZ, localNoiseX, localNoiseZ);
                
                for (int nY = 0; nY < this.noiseResY; ++nY) {
                    noise[ndx] = buffer[nY];
                    
                    ndx++;
                }
            }
        }
        
        return noise;
    }
    
    @FunctionalInterface
    public static interface BaseColumnSampler {
        public void sampleColumn(double[] buffer, int startNoiseX, int startNoiseZ, int localNoiseX, int localNoiseZ);
    }
}
