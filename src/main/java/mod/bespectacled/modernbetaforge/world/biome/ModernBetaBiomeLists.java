package mod.bespectacled.modernbetaforge.world.biome;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class ModernBetaBiomeLists {
    public static final List<Biome> ALL_BIOMES;
    public static final List<Biome> BETA_BIOMES;
    public static final List<Biome> ALPHA_BIOMES;
    public static final List<Biome> INFDEV_BIOMES;
    public static final List<Biome> INDEV_BIOMES;
    
    public static final List<Biome> BUILTIN_BIOMES_WITH_CUSTOM_SURFACES;
    
    static {
        BETA_BIOMES = ImmutableList.<Biome>builder().add(
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
            ModernBetaBiomeHolders.BETA_SNOWY_BEACH,
            
            ModernBetaBiomeHolders.BETA_SKY
        ).build();
        
        ALPHA_BIOMES = ImmutableList.<Biome>builder().add(
            ModernBetaBiomeHolders.ALPHA,
            ModernBetaBiomeHolders.ALPHA_WINTER
        ).build();
        
        INFDEV_BIOMES = ImmutableList.<Biome>builder().add(
            ModernBetaBiomeHolders.INFDEV_227,
            ModernBetaBiomeHolders.INFDEV_415,
            ModernBetaBiomeHolders.INFDEV_420,
            ModernBetaBiomeHolders.INFDEV_611
        ).build();
        
        INDEV_BIOMES = ImmutableList.<Biome>builder().add(
            ModernBetaBiomeHolders.INDEV_NORMAL,
            ModernBetaBiomeHolders.INDEV_PARADISE,
            ModernBetaBiomeHolders.INDEV_WOODS
        ).build();
        
        ALL_BIOMES = ImmutableList.<Biome>builder()
            .addAll(BETA_BIOMES)
            .addAll(ALPHA_BIOMES)
            .addAll(INFDEV_BIOMES)
            .addAll(INDEV_BIOMES)
            .build();
        
        BUILTIN_BIOMES_WITH_CUSTOM_SURFACES = ImmutableList.<Biome>builder().add(
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
            Biomes.MUSHROOM_ISLAND_SHORE,
            
            // Oceans
            Biomes.OCEAN,
            Biomes.DEEP_OCEAN,
            Biomes.FROZEN_OCEAN
        ).build();
    }
}
