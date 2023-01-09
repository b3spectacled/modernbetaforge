package mod.bespectacled.modernbetaforge.world.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.world.biome.beta.BiomeBeta;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class ModernBetaBiomeLists {
    public static final List<Biome> ALL_BIOMES;
    public static final List<Biome> BETA_BIOMES;
    public static final List<Biome> ALPHA_BIOMES;
    public static final List<Biome> INFDEV_BIOMES;
    
    public static final List<Biome> BIOMES_WITH_CUSTOM_SURFACES;
    
    public static void setBetaClimateSamplers(ClimateSampler climateSampler, SkyClimateSampler skyClimateSampler) {
        for (Biome biome : BETA_BIOMES) {
            BiomeBeta biomeBeta = (BiomeBeta)biome;
            
            biomeBeta.setClimateSamplers(climateSampler, skyClimateSampler);
        }
    }
    
    public static void resetBetaClimateSamplers() {
        for (Biome biome : BETA_BIOMES) {
            BiomeBeta biomeBeta = (BiomeBeta)biome;
            
            biomeBeta.resetClimateSamplers();
        }
    }
    
    static {
        BETA_BIOMES = Arrays.asList(
            ModernBetaBiomeHolders.BETA_ICE_DESERT,
            ModernBetaBiomeHolders.BETA_TUNDRA,
            ModernBetaBiomeHolders.BETA_SAVANNA,
            ModernBetaBiomeHolders.BETA_DESERT,
            ModernBetaBiomeHolders.BETA_SWAMPLAND,
            ModernBetaBiomeHolders.BETA_TAIGA,
            ModernBetaBiomeHolders.BETA_SHRUBLAND,
            ModernBetaBiomeHolders.BETA_FOREST,
            ModernBetaBiomeHolders.BETA_PLAINS,
            ModernBetaBiomeHolders.BETA_SEASONAL_FOREST,
            ModernBetaBiomeHolders.BETA_RAINFOREST,
            
            ModernBetaBiomeHolders.BETA_OCEAN,
            ModernBetaBiomeHolders.BETA_FROZEN_OCEAN,
            
            ModernBetaBiomeHolders.BETA_BEACH,
            ModernBetaBiomeHolders.BETA_SNOWY_BEACH
        );
        
        ALPHA_BIOMES = Arrays.asList(
            ModernBetaBiomeHolders.ALPHA,
            ModernBetaBiomeHolders.ALPHA_WINTER
        );
        
        INFDEV_BIOMES = Arrays.asList(
            ModernBetaBiomeHolders.INFDEV_415
        );
        
        ALL_BIOMES = new ArrayList<>();
        ALL_BIOMES.addAll(BETA_BIOMES);
        ALL_BIOMES.addAll(ALPHA_BIOMES);
        ALL_BIOMES.addAll(INFDEV_BIOMES);
        
        BIOMES_WITH_CUSTOM_SURFACES = Arrays.asList(
            // Badlands
            Biomes.MESA,
            Biomes.MESA_CLEAR_ROCK,
            Biomes.MESA_ROCK,
            Biomes.MUTATED_MESA,
            Biomes.MUTATED_MESA_CLEAR_ROCK,
            Biomes.MUTATED_MESA_ROCK,
            
            // Mountains
            Biomes.EXTREME_HILLS,
            Biomes.MUTATED_EXTREME_HILLS,
            Biomes.MUTATED_EXTREME_HILLS_WITH_TREES,
            
            // Giant Taigas
            Biomes.MUTATED_REDWOOD_TAIGA,
            Biomes.MUTATED_REDWOOD_TAIGA_HILLS,
            Biomes.REDWOOD_TAIGA,
            Biomes.REDWOOD_TAIGA_HILLS,
            
            // Savanna
            Biomes.MUTATED_SAVANNA,
            Biomes.MUTATED_SAVANNA_ROCK,
            
            // Swamp
            Biomes.SWAMPLAND,
            Biomes.MUTATED_SWAMPLAND,
            
            // Other
            Biomes.STONE_BEACH,
            Biomes.MUSHROOM_ISLAND,
            Biomes.MUSHROOM_ISLAND_SHORE
        );
    }
}
