package mod.bespectacled.modernbetaforge.compat;

import java.util.List;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public interface BiomeCompat {
    @Deprecated
    List<BiomeEntry>[] getBiomeEntries();
    
    default List<BiomeEntry>[] getBiomeEntries(ModernBetaGeneratorSettings settings) {
        return this.getBiomeEntries();
    }
    
    boolean shouldGetBiomeEntries(ModernBetaGeneratorSettings settings);
}
