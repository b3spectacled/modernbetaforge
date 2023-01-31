package mod.bespectacled.modernbetaforge.world.biome;

import mod.bespectacled.modernbetaforge.world.structure.MapGenBetaScatteredFeature;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.BiomeManager;

public class ModernBetaBiomeStructures {
    public static void registerStructures() {
        MapGenStructureIO.registerStructure(MapGenBetaScatteredFeature.Start.class, "Temple");
    }
    
    public static void registerStructureBiomes() {
        /* Beta */

        // Add stronghold biomes
        for (Biome biome : ModernBetaBiomeLists.BETA_BIOMES) {
            addStrongholdBiome(biome);
        }
        
        // Add village biomes
        addVillageBiome(ModernBetaBiomeHolders.BETA_DESERT);
        addVillageBiome(ModernBetaBiomeHolders.BETA_SAVANNA);
        addVillageBiome(ModernBetaBiomeHolders.BETA_SHRUBLAND);
        
        /* Alpha */
        
        addStrongholdBiome(ModernBetaBiomeHolders.ALPHA);
        addStrongholdBiome(ModernBetaBiomeHolders.ALPHA_WINTER);
        
        addVillageBiome(ModernBetaBiomeHolders.ALPHA);
        addVillageBiome(ModernBetaBiomeHolders.ALPHA_WINTER);
        
        /* Infdev 415 */ 
        
        addStrongholdBiome(ModernBetaBiomeHolders.INFDEV_415);
        addVillageBiome(ModernBetaBiomeHolders.INFDEV_415);
    }
    
    public static void addStrongholdBiome(Biome biome) {
        BiomeManager.addStrongholdBiome(biome);
    }
    
    public static void addVillageBiome(Biome biome) {
        BiomeManager.addVillageBiome(biome, false);
    }
}
