package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.LinkedHashMap;
import java.util.Map;

import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
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
        ClimateMapping iceDesert = new ClimateMapping(
            settings.iceDesertBiomeBase,
            settings.iceDesertBiomeOcean,
            settings.iceDesertBiomeBeach
        );
        ClimateMapping tundra = new ClimateMapping(
            settings.tundraBiomeBase,
            settings.tundraBiomeOcean,
            settings.tundraBiomeBeach
        );
        ClimateMapping savanna = new ClimateMapping(
            settings.savannaBiomeBase,
            settings.savannaBiomeOcean,
            settings.savannaBiomeBeach
        );
        ClimateMapping desert = new ClimateMapping(
            settings.desertBiomeBase,
            settings.desertBiomeOcean,
            settings.desertBiomeBeach
        );
        ClimateMapping swampland = new ClimateMapping(
            settings.swamplandBiomeBase,
            settings.swamplandBiomeOcean,
            settings.swamplandBiomeBeach
        );
        ClimateMapping taiga = new ClimateMapping(
            settings.taigaBiomeBase,
            settings.taigaBiomeOcean,
            settings.taigaBiomeBeach
        );
        ClimateMapping shrubland = new ClimateMapping(
            settings.shrublandBiomeBase,
            settings.shrublandBiomeOcean,
            settings.shrublandBiomeBeach
        );
        ClimateMapping forest = new ClimateMapping(
            settings.forestBiomeBase,
            settings.forestBiomeOcean,
            settings.forestBiomeBeach
        );
        ClimateMapping plains = new ClimateMapping(
            settings.plainsBiomeBase,
            settings.plainsBiomeOcean,
            settings.plainsBiomeBeach
        );
        ClimateMapping seasonal_forest = new ClimateMapping(
            settings.seasonalForestBiomeBase,
            settings.seasonalForestBiomeOcean,
            settings.seasonalForestBiomeBeach
        );
        ClimateMapping rainforest = new ClimateMapping(
            settings.rainforestBiomeBase,
            settings.rainforestBiomeOcean,
            settings.rainforestBiomeBeach
        );
        
        climateMap.put("ice_desert", iceDesert);
        climateMap.put("tundra", tundra);
        climateMap.put("savanna", savanna);
        climateMap.put("desert", desert);
        climateMap.put("swampland", swampland);
        climateMap.put("taiga", taiga);
        climateMap.put("shrubland", shrubland);
        climateMap.put("forest", forest);
        climateMap.put("plains", plains);
        climateMap.put("seasonal_forest", seasonal_forest);
        climateMap.put("rainforest", rainforest);
    }
    
    public Map<String, ClimateMapping> getMap() {
        return new LinkedHashMap<>(this.climateMap);
    }
    
    public ClimateMapping getMapping(double temp, double rain) {
        int t = (int) (temp * 63.0);
        int r = (int) (rain * 63.0);

        return this.climateTable[t + r * 64];
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
        public final Biome baseBiome;
        public final Biome oceanBiome;
        public final Biome beachBiome;
        
        // Used by datafixer
        public ClimateMapping(Map<String, String> settings) {
            this.baseBiome = BiomeUtil.getBiome(settings.get(NbtTags.DEPR_LAND_BIOME), "landBiome");
            this.oceanBiome = BiomeUtil.getBiome(settings.get(NbtTags.DEPR_OCEAN_BIOME), "oceanBiome");
            this.beachBiome = BiomeUtil.getBiome(settings.get(NbtTags.DEPR_BEACH_BIOME), "beachBiome");
        }
        
        public ClimateMapping(String baseBiome, String oceanBiome, String beachBiome) {
            this.baseBiome = BiomeUtil.getBiome(baseBiome, "landBiome");
            this.oceanBiome = BiomeUtil.getBiome(oceanBiome, "oceanBiome");
            this.beachBiome = BiomeUtil.getBiome(beachBiome, "beachBiome");
        }
        
        public Biome biomeByClimateType(ClimateType type) {
            switch(type) {
                case BASE: return this.baseBiome;
                case OCEAN: return this.oceanBiome;
                case BEACH: return this.beachBiome;
                default: return this.baseBiome;
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
                this.baseBiome.equals(other.baseBiome) &&
                this.oceanBiome.equals(other.oceanBiome) &&
                this.beachBiome.equals(other.beachBiome);
        }
    }
}
