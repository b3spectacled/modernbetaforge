package mod.bespectacled.modernbetaforge.world.biome.climate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ClimateMap {
    private final Map<String, ClimateMapping> climateMap;
    private final ClimateMapping[] climateTable;
    private final boolean containsNonBetaBiomes;
    private final boolean containsNonModernBetaBiomes;
    
    public ClimateMap(ModernBetaGeneratorSettings settings) {
        this.climateMap = new LinkedHashMap<>();
        this.climateTable = new ClimateMapping[4096];
        
        this.populateBiomeMap(settings);
        this.populateBiomeLookup();
        
        this.containsNonBetaBiomes = containsNonBetaBiomes(this.climateMap);
        this.containsNonModernBetaBiomes = containsNonModernBetaBiomes(this.climateMap);
    }
    
    public ClimateMapping getMapping(double temp, double rain) {
        int t = (int) (temp * 63.0);
        int r = (int) (rain * 63.0);

        return this.climateTable[t + r * 64];
    }
    
    public boolean containsNonBetaBiomes() {
        return this.containsNonBetaBiomes;
    }
    
    public boolean containsNonModernBetaBiomes() {
        return this.containsNonModernBetaBiomes;
    }
    
    private void populateBiomeMap(ModernBetaGeneratorSettings settings) {
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
        
        this.climateMap.put("ice_desert", iceDesert);
        this.climateMap.put("tundra", tundra);
        this.climateMap.put("savanna", savanna);
        this.climateMap.put("desert", desert);
        this.climateMap.put("swampland", swampland);
        this.climateMap.put("taiga", taiga);
        this.climateMap.put("shrubland", shrubland);
        this.climateMap.put("forest", forest);
        this.climateMap.put("plains", plains);
        this.climateMap.put("seasonal_forest", seasonal_forest);
        this.climateMap.put("rainforest", rainforest);
    }

    private void populateBiomeLookup() {
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
    
    private static boolean containsNonBetaBiomes(Map<String, ClimateMapping> climateMap) {
        for (Entry<String, ClimateMapping> mapping : climateMap.entrySet()) {
            if (!mapping.getValue().containsOnlyBetaBiomes) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean containsNonModernBetaBiomes(Map<String, ClimateMapping> climateMap) {
        for (Entry<String, ClimateMapping> mapping : climateMap.entrySet()) {
            if (!mapping.getValue().containsOnlyModernBetaBiomes) {
                return true;
            }
        }
        
        return false;
    }
    
    public static class ClimateMapping {
        public final Biome baseBiome;
        public final Biome oceanBiome;
        public final Biome beachBiome;
        
        public final boolean containsOnlyBetaBiomes;
        public final boolean containsOnlyModernBetaBiomes;
        
        public ClimateMapping(ResourceLocation baseBiome, ResourceLocation oceanBiome, ResourceLocation beachBiome) {
            this.baseBiome = ForgeRegistryUtil.get(baseBiome, ForgeRegistries.BIOMES);
            this.oceanBiome = ForgeRegistryUtil.get(oceanBiome, ForgeRegistries.BIOMES);
            this.beachBiome = ForgeRegistryUtil.get(beachBiome, ForgeRegistries.BIOMES);
            
            this.containsOnlyBetaBiomes = 
                this.baseBiome instanceof BiomeBeta &&
                this.oceanBiome instanceof BiomeBeta &&
                this.beachBiome instanceof BiomeBeta;
            
            this.containsOnlyModernBetaBiomes = 
                this.baseBiome instanceof ModernBetaBiome &&
                this.oceanBiome instanceof ModernBetaBiome &&
                this.beachBiome instanceof ModernBetaBiome;
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
