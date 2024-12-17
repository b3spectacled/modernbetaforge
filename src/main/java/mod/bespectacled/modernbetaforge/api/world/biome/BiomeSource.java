package mod.bespectacled.modernbetaforge.api.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;

public abstract class BiomeSource {
    protected final WorldInfo worldInfo;
    /**
     * Constructs a Modern Beta biome source, given the world's worldInfo.
     * 
     * @param worldInfo WorldInfo associated with the world.
     */
    public BiomeSource(WorldInfo worldInfo) {
        this.worldInfo = worldInfo;
    }
    
    /**
     * Gets a biome for biome source at given coordinates.
     * 
     * @param x x-coordinate.
     * @param z z-coordinate.
     * @return A biome at given biome coordinates.
     */
    public abstract Biome getBiome(int x, int z);
}
