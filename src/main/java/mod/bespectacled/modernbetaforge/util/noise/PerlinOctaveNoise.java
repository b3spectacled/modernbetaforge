package mod.bespectacled.modernbetaforge.util.noise;

import java.util.Random;

public class PerlinOctaveNoise {
    private final PerlinNoise noises[];
    private final int octaves;
    
    public PerlinOctaveNoise(Random random, int octaves, boolean useOffset) {
        this.noises = new PerlinNoise[octaves];
        this.octaves = octaves;
        
        for (int i = 0; i < octaves; i++) {
            this.noises[i] = new PerlinNoise(random, useOffset);
        }
    }
    
    /**
     * Beta 3D noise sampler. This creates a flattened 3D array and samples all values at once.
     * 
     * @param x Initial x-coordinate
     * @param y Initial y-coordinate
     * @param z Initial z-coordinate
     * @param sizeX Length along x-axis
     * @param sizeY Length along y-axis
     * @param sizeZ Length along z-axis
     * @param scaleX x-coordinate scale
     * @param scaleY y-coordinate scale
     * @param scaleZ z-cooridnate scale
     * @return An array of noise values from the starting coordinates to end coordinates specified by the dimensions.
     */
    public final double[] sampleBeta(
        double x,
        double y,
        double z, 
        int sizeX,
        int sizeY,
        int sizeZ, 
        double scaleX,
        double scaleY,
        double scaleZ
    ) {
        double[] noise = new double[sizeX * sizeY * sizeZ];
        double frequency = 1.0;
        
        for (int i = 0; i < octaves; i++) {
            this.noises[i].sampleBeta(
                noise, 
                x,
                y,
                z, 
                sizeX,
                sizeY,
                sizeZ,
                scaleX * frequency,
                scaleY * frequency,
                scaleZ * frequency,
                frequency
            );
            
            frequency /= 2.0;
        }
        
        return noise;
    }
    
    /**
     * Alpha 3D noise sampler. This creates a flattened 3D array and samples all values at once.
     * 
     * @param x Initial x-coordinate
     * @param y Initial y-coordinate
     * @param z Initial z-coordinate
     * @param sizeX Length along x-axis
     * @param sizeY Length along y-axis
     * @param sizeZ Length along z-axis
     * @param scaleX x-coordinate scale
     * @param scaleY y-coordinate scale
     * @param scaleZ z-cooridnate scale
     * @return An array of noise values from the starting coordinates to end coordinates specified by the dimensions
     */
    public final double[] sampleAlpha(
        double x,
        double y,
        double z, 
        int sizeX,
        int sizeY,
        int sizeZ, 
        double scaleX,
        double scaleY,
        double scaleZ
    ) {
        double[] noise = new double[sizeX * sizeY * sizeZ];
        double frequency = 1.0;
        
        for (int i = 0; i < octaves; i++) {
            this.noises[i].sampleAlpha(
                noise, 
                x,
                y,
                z, 
                sizeX,
                sizeY,
                sizeZ,
                scaleX * frequency,
                scaleY * frequency,
                scaleZ * frequency,
                frequency
            );
            
            frequency /= 2.0;
        }

        return noise;
    }

    /**
     * Standard 2D Perlin noise sampler.
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     * @return A noise value at the coordinate
     */
    public final double sample(double x, double y) {
        double total = 0.0;
        double frequency = 1.0;
        
        for (int i = 0; i < this.octaves; ++i) {
            total += this.noises[i].sample(x / frequency, y / frequency) * frequency;
            frequency *= 2.0;
        }
        
        return total;
    }
    
    /**
     * Standard 3D Perlin noise sampler.
     * 
     * @param x x-coordinate
     * @param y y-cooridnate
     * @param z z-coordinate
     * @return A noise value at the coordinate
     */
    public final double sample(double x, double y, double z) {
        double total = 0.0;
        double frequency = 1.0;
        
        for (int i = 0; i < this.octaves; ++i) {
            total += this.noises[i].sample(x / frequency, y / frequency, z / frequency) * frequency;
            frequency *= 2.0;
        }
        
        return total;
    }

    /**
     * Beta 2D Perlin noise sampler. This functions like sample(x, 0.0, z), except yOrigin is ignored.
     * 
     * @param x x-coordinate
     * @param z z-coordinate
     * @param scaleX x-coordinate scale
     * @param scaleZ z-coordinate scale
     * @return A noise value at the coordinate
     */
    public final double scaledSample(double x, double z, double scaleX, double scaleZ) {
        double total = 0.0;
        double frequency = 1.0;
        
        for (int i = 0; i < this.octaves; ++i) {
            total += this.noises[i].sampleXZ(
                x * scaleX * frequency, 
                z * scaleZ * frequency, 
                frequency
            );
            frequency /= 2.0;
        }
        
        return total;
    }
    
    /**
     * Alpha/Beta 3D Perlin noise sampler.
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param scaleX x-coordinate scale
     * @param scaleY y-coordinate scale
     * @param scaleZ z-coordinate scale
     * @return A noise value at the coordinate
     */
    public final double scaledSample(double x, double y, double z, double scaleX, double scaleY, double scaleZ) {
        double total = 0.0;
        double frequency = 1.0;
        
        for (int i = 0; i < this.octaves; ++i) {
            total += this.noises[i].sampleXYZ(
                x * scaleX * frequency, 
                y * scaleY * frequency, 
                z * scaleZ * frequency, 
                scaleY * frequency, 
                y * scaleY * frequency
            ) / frequency;
            
            frequency /= 2.0;
        }

        return total;
    }
}