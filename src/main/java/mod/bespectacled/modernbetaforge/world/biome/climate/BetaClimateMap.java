package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.LinkedHashMap;
import java.util.Map;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import net.minecraft.world.biome.Biome;

public class BetaClimateMap {
    private final Map<String, BetaClimateMapping> climateMap;
    private final BetaClimateMapping[] climateTable;
    
    public BetaClimateMap() {
        this.climateMap = new LinkedHashMap<>();
        this.climateTable = new BetaClimateMapping[4096];
        

        this.populateBiomeMap();
        this.generateBiomeLookup();
    }
    
    public void populateBiomeMap() {
        this.climateMap.put("ice_desert", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_TUNDRA, ModernBetaBiomeHolders.BETA_FROZEN_OCEAN, ModernBetaBiomeHolders.BETA_SNOWY_BEACH));
        this.climateMap.put("tundra", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_TUNDRA, ModernBetaBiomeHolders.BETA_FROZEN_OCEAN, ModernBetaBiomeHolders.BETA_SNOWY_BEACH));
        this.climateMap.put("savanna", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_SAVANNA, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("desert", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_DESERT, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_DESERT));
        this.climateMap.put("swampland", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_SWAMPLAND, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("taiga", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_TAIGA, ModernBetaBiomeHolders.BETA_FROZEN_OCEAN, ModernBetaBiomeHolders.BETA_SNOWY_BEACH));
        this.climateMap.put("shrubland", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_SHRUBLAND, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("forest", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_FOREST, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("plains", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_PLAINS, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("seasonal_forest", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_SEASONAL_FOREST, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("rainforest", new BetaClimateMapping(ModernBetaBiomeHolders.BETA_RAINFOREST, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
    }
    
    public Map<String, BetaClimateMapping> getMap() {
        return new LinkedHashMap<>(this.climateMap);
    }
    
    public Biome getBiome(double temp, double rain, BetaClimateType type) {
        int t = (int) (temp * 63D);
        int r = (int) (rain * 63D);

        return this.climateTable[t + r * 64].biomeByClimateType(type);
    }
    
    private void generateBiomeLookup() {
        for (int t = 0; t < 64; t++) {
            for (int r = 0; r < 64; r++) {
                this.climateTable[t + r * 64] = this.getBiome((float) t / 63F, (float) r / 63F);
            }
        }
    }
    
    private BetaClimateMapping getBiome(float temp, float rain) {
        rain *= temp;

        if (temp < 0.1F) {
            return this.climateMap.get("ice_desert");
        }

        if (rain < 0.2F) {
            if (temp < 0.5F) {
                return this.climateMap.get("tundra");
            }
            if (temp < 0.95F) {
                return this.climateMap.get("savanna");
            } else {
                return this.climateMap.get("desert");
            }
        }

        if (rain > 0.5F && temp < 0.7F) {
            return this.climateMap.get("swampland");
        }

        if (temp < 0.5F) {
            return this.climateMap.get("taiga");
        }

        if (temp < 0.97F) {
            if (rain < 0.35F) {
                return this.climateMap.get("shrubland");
            } else {
                return this.climateMap.get("forest");
            }
        }

        if (rain < 0.45F) {
            return this.climateMap.get("plains");
        }

        if (rain < 0.9F) {
            return this.climateMap.get("seasonal_forest");
        } else {
            return this.climateMap.get("rainforest");
        }
    }
    
    public static class BetaClimateMapping {
        private final Biome biome;
        private final Biome oceanBiome;
        private final Biome beachBiome;
        
        public BetaClimateMapping(Biome biome, Biome oceanBiome, Biome beachBiome) {
            this.biome = biome;
            this.oceanBiome = oceanBiome;
            this.beachBiome = beachBiome;
        }
        
        public Biome biomeByClimateType(BetaClimateType type) {
            switch(type) {
                case LAND: return this.biome;
                case OCEAN: return this.oceanBiome;
                case BEACH: return this.beachBiome;
                default: return this.biome;
            }
        }
    }
}
