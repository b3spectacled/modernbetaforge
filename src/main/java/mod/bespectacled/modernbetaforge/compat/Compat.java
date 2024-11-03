package mod.bespectacled.modernbetaforge.compat;

import java.util.List;

import net.minecraftforge.common.BiomeManager.BiomeEntry;

public interface Compat {
    void load();

    List<BiomeEntry>[] getBiomeEntries();
}
