package mod.bespectacled.modernbetaforge.compat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraftforge.fml.common.Loader;

public class ModCompat {
    public static final String MOD_BOP = "biomesoplenty";
    public static final Map<String, Compat> LOADED_MODS = new LinkedHashMap<>();
    
    public static void loadModCompat() {
        loadModCompat(MOD_BOP, new CompatBOP());
    }
    
    public static boolean isBoPLoaded() {
        return LOADED_MODS.containsKey(MOD_BOP);
    }
    
    public static boolean isModLoaded(String mod) {
        return LOADED_MODS.containsKey(mod);
    }
    
    private static void loadModCompat(String modName, Compat compat) {
        if (Loader.isModLoaded(modName)) {
            try {
                ModernBeta.log(Level.INFO, String.format("Found mod '%s', attempting to load..", modName));
                compat.load();
                LOADED_MODS.put(modName, compat);
                ModernBeta.log(Level.INFO, String.format("Done loading mod '%s'..", modName));
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, String.format("Couldn't load mod '%s'!", modName));
            }
        }
    }
} 
