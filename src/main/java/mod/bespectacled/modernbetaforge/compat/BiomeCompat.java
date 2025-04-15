package mod.bespectacled.modernbetaforge.compat;

import java.util.List;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public interface BiomeCompat {
    List<BiomeEntry>[] getBiomeEntries();
    
    boolean shouldGetBiomeEntries(ModernBetaGeneratorSettings settings);
}
