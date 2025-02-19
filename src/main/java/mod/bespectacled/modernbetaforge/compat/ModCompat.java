package mod.bespectacled.modernbetaforge.compat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraftforge.fml.common.Loader;

public class ModCompat {
    public static final String MOD_BIOMES_O_PLENTY = "biomesoplenty";
    public static final String MOD_GALACTICRAFT = "galacticraftcore";
    public static final String MOD_NETHER_API = "nether_api";
    public static final String MOD_DYNAMIC_TREES = "dynamictrees";
    
    public static final Map<String, Compat> LOADED_MODS = new LinkedHashMap<>();
    
    public static void loadModCompat() {
        if (!isMixinLoaderLoaded()) {
            ModernBeta.log(Level.WARN, "MixinBooter was not found or an alternate mixin loader was installed..");
        }
        
        loadModCompat(MOD_BIOMES_O_PLENTY, new CompatBiomesOPlenty());
        loadModCompat(MOD_GALACTICRAFT, new CompatGalacticraft());
        loadModCompat(MOD_NETHER_API, new CompatNetherAPI());
        loadModCompat(MOD_DYNAMIC_TREES, new CompatDynamicTrees());
    }
    
    public static boolean isMixinLoaderLoaded() {
        return Loader.isModLoaded("mixinbooter");
    }
    
    public static boolean isModLoaded(String mod) {
        return LOADED_MODS.containsKey(mod);
    }
    
    public static boolean isNetherCompatible() {
        for (Compat compat : LOADED_MODS.values()) {
            if (compat instanceof NetherCompat && !((NetherCompat)compat).isCompatible()) {
                return false;
            }
        }
        
        return true;
    }
    
    private static void loadModCompat(String modName, Compat compat) {
        if (Loader.isModLoaded(modName)) {
            try {
                ModernBeta.log(Level.INFO, String.format("Found mod '%s'..", modName));
                compat.load();
                LOADED_MODS.put(modName, compat);
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, String.format("Couldn't load compat for mod '%s'!", modName));
            }
        }
    }
} 
