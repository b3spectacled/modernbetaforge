package mod.bespectacled.modernbetaforge.util;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;

public class PresetUtil {
    private static final Gson GSON = new Gson();
    
    public static boolean isPresetEmpty(String preset) {
        return ModernBetaConfig.guiOptions.defaultPreset.isEmpty();
    }
    
    public static String readPreset(String preset) {
        preset = preset.trim();
        
        try {
            GSON.fromJson(preset, JsonObject.class);
        } catch (JsonSyntaxException e) {
            ModernBeta.log(Level.ERROR, "Default preset is malformed, defaulting to empty string..");
            preset = "";
        }
        
        return preset;
    }
    
    public static JsonObject readPresetAsJson(String preset) {
        JsonObject jsonObject;
        
        try {
            jsonObject = GSON.fromJson(preset, JsonObject.class);
        } catch (JsonSyntaxException e) {
            ModernBeta.log(Level.ERROR, "Preset is malformed, defaulting to empty json..");
            jsonObject = new JsonObject();
        }
        
        return jsonObject;
    }
}
