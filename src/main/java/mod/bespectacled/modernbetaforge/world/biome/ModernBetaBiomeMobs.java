package mod.bespectacled.modernbetaforge.world.biome;

import java.util.List;

import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class ModernBetaBiomeMobs {

    public static List<SpawnListEntry> modifySpawnList(
        List<SpawnListEntry> spawnEntries,
        EnumCreatureType creatureType,
        Biome biome,
        ModernBetaChunkGeneratorSettings settings
    ) {
        if (biome instanceof ModernBetaBiome) {
            ModernBetaBiome modernBetaBiome = (ModernBetaBiome)biome;
            
            switch(creatureType) {
                case MONSTER:        // New Monsters
                    addMobs(spawnEntries, modernBetaBiome.getAdditionalMonsters(), settings.spawnNewMonsterMobs);
                    break;
                case CREATURE:       // New Passives, wolves
                    addMobs(spawnEntries, modernBetaBiome.getAdditionalCreatures(), settings.spawnNewCreatureMobs);
                    addMobs(spawnEntries, modernBetaBiome.getAdditionalWolves(), settings.spawnWolves);
                    break;
                case AMBIENT:        // Bats
                    clearMobs(spawnEntries, !settings.spawnAmbientMobs);
                    break;
                case WATER_CREATURE: // Squid
                    clearMobs(spawnEntries, !settings.spawnWaterMobs);
                    break;
            }
        }
        
        return spawnEntries;
    }
    
    private static void addMobs(List<SpawnListEntry> spawnEntries, List<SpawnListEntry> additionalMobs, boolean shouldAdd) {
        if (shouldAdd) {
            spawnEntries.addAll(additionalMobs);
        }
    }
    
    private static void clearMobs(List<SpawnListEntry> spawnEntries, boolean shouldClear) {
        if (shouldClear) {
            spawnEntries.clear();
        }
    }
}
