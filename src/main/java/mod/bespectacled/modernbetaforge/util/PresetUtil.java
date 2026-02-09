package mod.bespectacled.modernbetaforge.util;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;

public class PresetUtil {
    private static final Gson GSON = new Gson();
    
    public static boolean hasChangedDefaultPreset() {
        return !ModernBetaConfig.guiOptions.defaultPreset.isEmpty();
    }
    
    public static String getDefaultPreset() {
        String defaultPreset = ModernBetaConfig.guiOptions.defaultPreset.trim();
        
        try {
            GSON.fromJson(defaultPreset, JsonObject.class);
        } catch (JsonSyntaxException e) {
            ModernBeta.log(Level.ERROR, "Default preset is malformed, defaulting to empty string..");
            defaultPreset = "";
        }
        
        return defaultPreset;
    }
}
