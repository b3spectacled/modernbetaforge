package mod.bespectacled.modernbetaforge.world.biome.layer.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.BiomeCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.init.Biomes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public class GenLayerBiomeExtended extends GenLayerBiome {
    @SuppressWarnings("unchecked")
    private final List<BiomeEntry>[] biomes = new ArrayList[BiomeType.values().length];
    
    public GenLayerBiomeExtended(
        long seed,
        GenLayer parent,
        WorldType worldType,
        ChunkGeneratorSettings vanillaSettings,
        ModernBetaGeneratorSettings settings
    ) {
        super(seed, parent, worldType, vanillaSettings);

        this.populateInitialBiomes(this.biomes, worldType);
        this.populateAdditionalBiomes(this.biomes, settings);
    }
    
    @Override
    protected BiomeEntry getWeightedBiomeEntry(BiomeType type)  {
        List<BiomeEntry> biomeList = this.biomes[type.ordinal()];
        
        int totalWeight = WeightedRandom.getTotalWeight(biomeList);
        int weight = BiomeManager.isTypeListModded(type) ? nextInt(totalWeight) : nextInt(totalWeight / 10) * 10;
        
        return (BiomeEntry)WeightedRandom.getRandomItem(biomeList, weight);
    }
    
    private void populateInitialBiomes(List<BiomeEntry>[] biomes, WorldType worldType) {
        for (BiomeType type : BiomeType.values()) {
            List<BiomeEntry> biomesToAdd = BiomeManager.getBiomes(type);
            int idx = type.ordinal();

            if (biomes[idx] == null) {
                biomes[idx] = new ArrayList<>();
            }
            
            if (biomesToAdd != null) {
                biomes[idx].addAll(biomesToAdd);
            }
        }

        int desertNdx = BiomeType.DESERT.ordinal();

        biomes[desertNdx].add(new BiomeEntry(Biomes.DESERT, 30));
        biomes[desertNdx].add(new BiomeEntry(Biomes.SAVANNA, 20));
        biomes[desertNdx].add(new BiomeEntry(Biomes.PLAINS, 10));

        if (worldType == WorldType.DEFAULT_1_1) {
            biomes[desertNdx].clear();
            biomes[desertNdx].add(new BiomeEntry(Biomes.DESERT, 10));
            biomes[desertNdx].add(new BiomeEntry(Biomes.FOREST, 10));
            biomes[desertNdx].add(new BiomeEntry(Biomes.EXTREME_HILLS, 10));
            biomes[desertNdx].add(new BiomeEntry(Biomes.SWAMPLAND, 10));
            biomes[desertNdx].add(new BiomeEntry(Biomes.PLAINS, 10));
            biomes[desertNdx].add(new BiomeEntry(Biomes.TAIGA, 10));
        }
    }
    
    private void populateAdditionalBiomes(List<BiomeEntry>[] biomes, ModernBetaGeneratorSettings settings) {
        for (Entry<String, Compat> entry : ModCompat.LOADED_MODS.entrySet()) {
            Compat compat = entry.getValue();
            
            if (compat instanceof BiomeCompat) {
                ModernBeta.log(Level.DEBUG, String.format("Adding biomes to Release Biome Source from mod '%s'", entry.getKey()));
                BiomeCompat biomeCompat = (BiomeCompat)compat;
                
                if (biomeCompat.shouldGetBiomeEntries(settings)) {
                    for (BiomeType type : BiomeType.values()) {
                        int ndx = type.ordinal();
                        biomes[ndx].addAll(biomeCompat.getBiomeEntries()[ndx]);
                    }
                    
                }
            }
        }
    }
}
