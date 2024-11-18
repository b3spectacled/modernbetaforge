package mod.bespectacled.modernbetaforge.world.biome;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.common.BiomeManager;

public class ModernBetaBiomeStructures {
    public static void registerStructures() {}
    
    public static void registerStructureBiomes() {
        /* Beta */

        // Add stronghold biomes
        for (Biome biome : ModernBetaBiomeLists.BETA_BIOMES) {
            if (biome != ModernBetaBiomeHolders.BETA_OCEAN && biome != ModernBetaBiomeHolders.BETA_FROZEN_OCEAN)
                addStrongholdBiome(biome);
        }
        
        // Add village biomes
        addVillageBiome(ModernBetaBiomeHolders.BETA_DESERT);
        addVillageBiome(ModernBetaBiomeHolders.BETA_SAVANNA);
        addVillageBiome(ModernBetaBiomeHolders.BETA_SHRUBLAND);
        addVillageBiome(ModernBetaBiomeHolders.BETA_TAIGA);
        addVillageBiome(ModernBetaBiomeHolders.BETA_ICE_DESERT);
        
        /* Alpha */
        
        for (Biome biome : ModernBetaBiomeLists.ALPHA_BIOMES) {
            addStrongholdBiome(biome);
            addVillageBiome(biome);
        }
        
        /* Infdev */ 
        
        for (Biome biome : ModernBetaBiomeLists.INFDEV_BIOMES) {
            addStrongholdBiome(biome);
            addVillageBiome(biome);
        }
        
        /* Indev */ 
        
        for (Biome biome : ModernBetaBiomeLists.INDEV_BIOMES) {
            addStrongholdBiome(biome);
            addVillageBiome(biome);
        }
    }
    
    private static void addStrongholdBiome(Biome biome) {
        BiomeManager.addStrongholdBiome(biome);
    }
    
    private static void addVillageBiome(Biome biome) {
        BiomeManager.addVillageBiome(biome, false);
    }
    
    static {
        // Modify vanilla structures and add Modern Beta biomes to valid biomes list
        StructureOceanMonument.WATER_BIOMES = new ArrayList<>(StructureOceanMonument.WATER_BIOMES);
        StructureOceanMonument.WATER_BIOMES.addAll(Arrays.asList(ModernBetaBiomeHolders.BETA_FROZEN_OCEAN, ModernBetaBiomeHolders.BETA_OCEAN));

        StructureOceanMonument.SPAWN_BIOMES = new ArrayList<>(StructureOceanMonument.SPAWN_BIOMES);
        StructureOceanMonument.SPAWN_BIOMES.addAll(Arrays.asList(ModernBetaBiomeHolders.BETA_OCEAN));
        
        WoodlandMansion.ALLOWED_BIOMES = new ArrayList<>(WoodlandMansion.ALLOWED_BIOMES);
        WoodlandMansion.ALLOWED_BIOMES.addAll(Arrays.asList(ModernBetaBiomeHolders.BETA_SEASONAL_FOREST));
        
        MapGenScatteredFeature.BIOMELIST = new ArrayList<>(MapGenScatteredFeature.BIOMELIST);
        MapGenScatteredFeature.BIOMELIST.addAll(Arrays.asList(
            ModernBetaBiomeHolders.BETA_DESERT,
            ModernBetaBiomeHolders.BETA_RAINFOREST,
            ModernBetaBiomeHolders.BETA_SWAMPLAND,
            ModernBetaBiomeHolders.BETA_TUNDRA,
            ModernBetaBiomeHolders.BETA_TAIGA
        ));
    }
}
