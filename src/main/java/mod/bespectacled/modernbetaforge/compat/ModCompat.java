package mod.bespectacled.modernbetaforge.compat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.biomesoplenty.CompatBiomesOPlenty;
import mod.bespectacled.modernbetaforge.compat.buildcraft.CompatBuildCraftEnergy;
import mod.bespectacled.modernbetaforge.compat.dynamictrees.CompatDynamicTrees;
import mod.bespectacled.modernbetaforge.compat.galacticraft.CompatGalacticraft;
import mod.bespectacled.modernbetaforge.compat.nether_api.CompatNetherAPI;
import mod.bespectacled.modernbetaforge.compat.thaumcraft.CompatThaumcraft;
import net.minecraftforge.fml.common.Loader;

public class ModCompat {
    public static final Map<String, Compat> LOADED_MODS = new LinkedHashMap<>();
    
    public static void loadCompat() {
        if (!Loader.isModLoaded("mixinbooter")) {
            ModernBeta.log(Level.WARN, "MixinBooter was not found or an alternate mixin loader was installed..");
        }
        
        loadCompat(new CompatBiomesOPlenty());
        loadCompat(new CompatGalacticraft());
        loadCompat(new CompatNetherAPI());
        loadCompat(new CompatDynamicTrees());
        loadCompat(new CompatBuildCraftEnergy());
        loadCompat(new CompatThaumcraft());
    }
    
    public static boolean isModLoaded(String modId) {
        return LOADED_MODS.containsKey(modId);
    }
    
    public static boolean isNetherCompatible() {
        for (Compat compat : LOADED_MODS.values()) {
            if (compat instanceof NetherCompat && !((NetherCompat)compat).isCompatible()) {
                return false;
            }
        }
        
        return true;
    }
    
    private static void loadCompat(Compat compat) {
        String modId = compat.getModId();
        
        if (Loader.isModLoaded(modId)) {
            try {
                ModernBeta.log(Level.INFO, String.format("Found mod '%s'..", modId));
                compat.load();
                LOADED_MODS.put(modId, compat);
                
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, String.format("Couldn't load compat for mod '%s'!", modId));
                
            }
        }
    }
} 
