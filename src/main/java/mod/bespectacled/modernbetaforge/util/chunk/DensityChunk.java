package mod.bespectacled.modernbetaforge.util.chunk;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.util.ResourceLocation;

public class DensityChunk {
    public static final ResourceLocation INITIAL = ModernBeta.createRegistryKey("initial");
    
    private final Map<ResourceLocation, double[]> densityMap;
    private final int worldFloor;
    
    public DensityChunk(Map<ResourceLocation, double[]> densityMap, int worldFloor) {
        this.densityMap = ImmutableMap.copyOf(densityMap);
        this.worldFloor = worldFloor;
    }
    
    public DensityChunk(double[] densities, int worldMinY) {
        this(ImmutableMap.of(INITIAL, densities), worldMinY);
    }
    
    public double sample(int x, int y, int z) {
        return this.sample(INITIAL, x, y, z);
    }
    
    public double sample(ResourceLocation key, int x, int y, int z) {
        if (!this.densityMap.containsKey(key)) {
            String error = String.format("[Modern Beta] Density map does not contain key '%s'!", key);
            
            throw new IllegalArgumentException(error);
        }
        
        return this.densityMap.get(key)[getNdx(x, y - this.worldFloor, z)];
    }
    
    public static int getNdx(int x, int y, int z) {
        return (((y << 4) + (x & 0xF)) << 4) + (z & 0xF);
    }
}
