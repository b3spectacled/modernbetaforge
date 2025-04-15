package mod.bespectacled.modernbetaforge.compat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.buildcraft.CompatBuildCraftEnergy;
import mod.bespectacled.modernbetaforge.compat.thaumcraft.CompatThaumcraft;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientModCompat {
    public static final Map<String, ClientCompat> LOADED_MODS = new LinkedHashMap<>();
    
    public static void loadCompat() {
        loadCompat(new CompatBuildCraftEnergy());
        loadCompat(new CompatThaumcraft());
    }
    
    public static boolean isModLoaded(String modId) {
        return LOADED_MODS.containsKey(modId);
    }
    
    private static void loadCompat(ClientCompat compat) {
        String modId = compat.getModId();
        
        if (Loader.isModLoaded(modId)) {
            try {
                ModernBeta.log(Level.INFO, String.format("Found client mod '%s'..", modId));
                compat.loadClient();
                LOADED_MODS.put(modId, compat);
                
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, String.format("Couldn't load client compat for mod '%s'!", modId));
                
            }
        }
    }
}
