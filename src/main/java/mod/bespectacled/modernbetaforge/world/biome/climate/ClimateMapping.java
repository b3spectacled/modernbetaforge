package mod.bespectacled.modernbetaforge.world.biome.climate;

import net.minecraft.world.biome.Biome;

public class ClimateMapping {
    public enum ClimateType {
        LAND,
        OCEAN,
        BEACH
    }
    
    private final Biome biome;
    private final Biome oceanBiome;
    private final Biome beachBiome;
    
    public ClimateMapping(Biome biome, Biome oceanBiome, Biome beachBiome) {
        this.biome = biome;
        this.oceanBiome = oceanBiome;
        this.beachBiome = beachBiome;
    }
    
    public Biome biome() {
        return this.biome;
    }
    
    public Biome oceanBiome() {
        return this.oceanBiome;
    }
    
    public Biome beachBiome() {
        return this.beachBiome;
    }
    
    public Biome biomeByClimateType(ClimateType type) {
        switch(type) {
            case LAND: return this.biome;
            case OCEAN: return this.oceanBiome;
            case BEACH: return this.beachBiome;
            default: return this.biome;
        }
    }
    
}
