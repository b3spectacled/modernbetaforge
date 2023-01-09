package mod.bespectacled.modernbetaforge.api.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;

public abstract class BiomeSource {
    protected final WorldInfo worldInfo;
    
    /**
     * Constructs a Modern Beta biome provider initialized with seed.
     * Additional settings are supplied in NbtCompound parameter.
     * 
     * @param worldInfo WorldInfo.
     * 
     */
    public BiomeSource(WorldInfo worldInfo) {
        this.worldInfo = worldInfo;
    }
    
    /**
     * Gets a biome for biome source at given coordinates.
     * 
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param z z-coordinate.
     * 
     * @return A biome at given biome coordinates.
     */
    public abstract Biome getBiome(int x, int y, int z);
}
