package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.LinkedHashMap;
import java.util.Map;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import mod.bespectacled.modernbetaforge.world.biome.climate.ClimateMapping.ClimateType;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class BetaClimateMap {
    private final Map<String, ClimateMapping> climateMap;
    private final ClimateMapping[] climateTable;
    
    public BetaClimateMap() {
        this.climateMap = new LinkedHashMap<>();
        this.climateTable = new ClimateMapping[4096];
        
        this.populateMapBeta();
        //this.populateMapBetaPlus();
        this.generateBiomeLookup();
    }
    
    public void populateMapBeta() {
        this.climateMap.put("ice_desert", new ClimateMapping(ModernBetaBiomeHolders.BETA_TUNDRA, ModernBetaBiomeHolders.BETA_FROZEN_OCEAN, ModernBetaBiomeHolders.BETA_SNOWY_BEACH));
        this.climateMap.put("tundra", new ClimateMapping(ModernBetaBiomeHolders.BETA_TUNDRA, ModernBetaBiomeHolders.BETA_FROZEN_OCEAN, ModernBetaBiomeHolders.BETA_SNOWY_BEACH));
        this.climateMap.put("savanna", new ClimateMapping(ModernBetaBiomeHolders.BETA_SAVANNA, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("desert", new ClimateMapping(ModernBetaBiomeHolders.BETA_DESERT, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_DESERT));
        this.climateMap.put("swampland", new ClimateMapping(ModernBetaBiomeHolders.BETA_SWAMPLAND, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("taiga", new ClimateMapping(ModernBetaBiomeHolders.BETA_TAIGA, ModernBetaBiomeHolders.BETA_FROZEN_OCEAN, ModernBetaBiomeHolders.BETA_SNOWY_BEACH));
        this.climateMap.put("shrubland", new ClimateMapping(ModernBetaBiomeHolders.BETA_SHRUBLAND, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("forest", new ClimateMapping(ModernBetaBiomeHolders.BETA_FOREST, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("plains", new ClimateMapping(ModernBetaBiomeHolders.BETA_PLAINS, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("seasonal_forest", new ClimateMapping(ModernBetaBiomeHolders.BETA_SEASONAL_FOREST, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
        this.climateMap.put("rainforest", new ClimateMapping(ModernBetaBiomeHolders.BETA_RAINFOREST, ModernBetaBiomeHolders.BETA_OCEAN, ModernBetaBiomeHolders.BETA_BEACH));
    }
    
    public void populateMapBetaPlus() {
        this.climateMap.put("ice_desert", new ClimateMapping(Biomes.ICE_PLAINS, Biomes.FROZEN_OCEAN, Biomes.COLD_BEACH));
        this.climateMap.put("tundra", new ClimateMapping(Biomes.ICE_PLAINS, Biomes.FROZEN_OCEAN, Biomes.COLD_BEACH));
        this.climateMap.put("savanna", new ClimateMapping(Biomes.MESA, Biomes.OCEAN, Biomes.BEACH));
        this.climateMap.put("desert", new ClimateMapping(Biomes.DESERT, Biomes.OCEAN, Biomes.DESERT));
        this.climateMap.put("swampland", new ClimateMapping(Biomes.SWAMPLAND, Biomes.OCEAN, Biomes.BEACH));
        this.climateMap.put("taiga", new ClimateMapping(Biomes.COLD_TAIGA, Biomes.FROZEN_OCEAN, Biomes.COLD_BEACH));
        this.climateMap.put("shrubland", new ClimateMapping(Biomes.PLAINS, Biomes.OCEAN, Biomes.BEACH));
        this.climateMap.put("forest", new ClimateMapping(Biomes.FOREST, Biomes.OCEAN, Biomes.BEACH));
        this.climateMap.put("plains", new ClimateMapping(Biomes.PLAINS, Biomes.OCEAN, Biomes.BEACH));
        this.climateMap.put("seasonal_forest", new ClimateMapping(Biomes.ROOFED_FOREST, Biomes.OCEAN, Biomes.BEACH));
        this.climateMap.put("rainforest", new ClimateMapping(Biomes.JUNGLE, Biomes.OCEAN, Biomes.BEACH));
    }
    
    public Map<String, ClimateMapping> getMap() {
        return new LinkedHashMap<>(this.climateMap);
    }
    
    public Biome getBiome(double temp, double rain, ClimateType type) {
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
    
    private ClimateMapping getBiome(float temp, float rain) {
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
}
