package mod.bespectacled.modernbetaforge.world.biome;

import java.util.List;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class ModernBetaBiomeMobs {
    public static List<SpawnListEntry> modifySpawnList(
        List<SpawnListEntry> spawnEntries,
        EnumCreatureType creatureType,
        Biome biome,
        ModernBetaGeneratorSettings settings
    ) {
        if (biome instanceof ModernBetaBiome) {
            ModernBetaBiome modernBetaBiome = (ModernBetaBiome)biome;
            
            switch(creatureType) {
                case MONSTER:        // New Monsters
                    addMobs(spawnEntries, creatureType, modernBetaBiome, settings.spawnNewMonsterMobs, false);
                    break;
                case CREATURE:       // New Passives, wolves
                    addMobs(spawnEntries, creatureType, modernBetaBiome, settings.spawnNewCreatureMobs, false);
                    addMobs(spawnEntries, creatureType, modernBetaBiome, settings.spawnWolves, true);
                    break;
                case AMBIENT:        // Bats
                    addMobs(spawnEntries, creatureType, modernBetaBiome, settings.spawnAmbientMobs, false);
                    break;
                case WATER_CREATURE: // Squid
                    addMobs(spawnEntries, creatureType, modernBetaBiome, settings.spawnWaterMobs, false);
                    break;
            }
        }
        
        return spawnEntries;
    }
    
    private static void addMobs(List<SpawnListEntry> spawnEntries, EnumCreatureType creatureType, ModernBetaBiome biome, boolean shouldAdd, boolean addWolves) {
        if (shouldAdd) {
            spawnEntries.addAll(biome.getAdditionalSpawnableList(creatureType, addWolves));
        }
    }
}
