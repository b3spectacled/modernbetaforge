package mod.bespectacled.modernbetaforge.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public interface BiomeCompat {
    List<BiomeEntry>[] getBiomeEntries();
    
    default List<Biome> getCustomSurfaces() {
        return new ArrayList<>();
    }
    
    default List<Block> getCustomCarvables() {
        return new ArrayList<>();
    }
}
