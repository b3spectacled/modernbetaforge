package mod.bespectacled.modernbetaforge.world.biome.layer;

import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.BiomeCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorGenLayerBiome;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiome;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public class GenLayerBiomeExtended extends GenLayerBiome {
    public GenLayerBiomeExtended(
        long seed,
        GenLayer parent,
        WorldType worldType,
        ChunkGeneratorSettings vanillaSettings,
        ModernBetaGeneratorSettings settings
    ) {
        super(seed, parent, worldType, vanillaSettings);
        
        if (settings.useModdedBiomes) {
            AccessorGenLayerBiome accessor = (AccessorGenLayerBiome)this;
            this.populateAdditionalBiomes(accessor.getBiomes());
        }
    }
    
    private void populateAdditionalBiomes(List<BiomeEntry>[] biomes) {
        for (Entry<String, Compat> entry : ModCompat.LOADED_MODS.entrySet()) {
            Compat compat = entry.getValue();
            if (compat instanceof BiomeCompat) {
                ModernBeta.log(Level.DEBUG, String.format("Adding biomes to Release Biome Source from mod '%s'", entry.getKey()));
                BiomeCompat biomeCompat = (BiomeCompat)compat;
                
                for (BiomeType type : BiomeType.values()) {
                    int ndx = type.ordinal();
                    biomes[ndx].addAll(biomeCompat.getBiomeEntries()[ndx]);
                }
            }
        }
    }
}
