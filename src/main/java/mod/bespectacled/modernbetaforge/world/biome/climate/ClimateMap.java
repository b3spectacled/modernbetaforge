package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.LinkedHashMap;
import java.util.Map;

import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings.ClimateMappingSettings;
import net.minecraft.world.biome.Biome;

public class ClimateMap {
    private final Map<String, ClimateMapping> climateMap;
    private final Map<String, ClimateMapping> defaultMap;
    private final ClimateMapping[] climateTable;
    private final boolean modifiedMap;
    
    public ClimateMap(ModernBetaChunkGeneratorSettings settings) {
        this.climateMap = new LinkedHashMap<>();
        this.defaultMap = new LinkedHashMap<>();
        this.climateTable = new ClimateMapping[4096];
        
        this.populateBiomeMap(this.climateMap, settings);
        this.populateBiomeMap(this.defaultMap, new ModernBetaChunkGeneratorSettings.Factory().build());
        this.generateBiomeLookup();
        
        this.modifiedMap = !this.climateMap.equals(this.defaultMap);
    }
    
    public void populateBiomeMap(Map<String, ClimateMapping> climateMap, ModernBetaChunkGeneratorSettings settings) {
        climateMap.put("ice_desert", new ClimateMapping(settings.iceDesertBiomes));
        climateMap.put("tundra", new ClimateMapping(settings.tundraBiomes));
        climateMap.put("savanna", new ClimateMapping(settings.savannaBiomes));
        climateMap.put("desert", new ClimateMapping(settings.desertBiomes));
        climateMap.put("swampland", new ClimateMapping(settings.swamplandBiomes));
        climateMap.put("taiga", new ClimateMapping(settings.taigaBiomes));
        climateMap.put("shrubland", new ClimateMapping(settings.shrublandBiomes));
        climateMap.put("forest", new ClimateMapping(settings.forestBiomes));
        climateMap.put("plains", new ClimateMapping(settings.plainsBiomes));
        climateMap.put("seasonal_forest", new ClimateMapping(settings.seasonalForestBiomes));
        climateMap.put("rainforest", new ClimateMapping(settings.rainforestBiomes));
    }
    
    public Map<String, ClimateMapping> getMap() {
        return new LinkedHashMap<>(this.climateMap);
    }
    
    public Biome getBiome(double temp, double rain, ClimateType type) {
        int t = (int) (temp * 63D);
        int r = (int) (rain * 63D);

        return this.climateTable[t + r * 64].biomeByClimateType(type);
    }
    
    public boolean isModifiedMap() {
        return this.modifiedMap;
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
    
    public static class ClimateMapping {
        public final Biome landBiome;
        public final Biome oceanBiome;
        public final Biome beachBiome;
        
        public ClimateMapping(ClimateMappingSettings settings) {
            this.landBiome = BiomeUtil.getBiome(settings.landBiome, "landBiome");
            this.oceanBiome = BiomeUtil.getBiome(settings.oceanBiome, "oceanBiome");
            this.beachBiome = BiomeUtil.getBiome(settings.beachBiome, "beachBiome");
        }
        
        public ClimateMapping(Biome biome, Biome oceanBiome, Biome beachBiome) {
            this.landBiome = biome;
            this.oceanBiome = oceanBiome;
            this.beachBiome = beachBiome;
        }
        
        public Biome biomeByClimateType(ClimateType type) {
            switch(type) {
                case LAND: return this.landBiome;
                case OCEAN: return this.oceanBiome;
                case BEACH: return this.beachBiome;
                default: return this.landBiome;
            }
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            
            if (!(o instanceof ClimateMapping)) {
                return false;
            }
            
            ClimateMapping other = (ClimateMapping) o;
            
            return
                this.landBiome.equals(other.landBiome) &&
                this.oceanBiome.equals(other.oceanBiome) &&
                this.beachBiome.equals(other.beachBiome);
        }
    }
}
