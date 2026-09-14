package mod.bespectacled.modernbetaforge.util.datafix;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.datafix.DataFix;

public class DataFixer {
    public static void runDataFixer(DataFix[] dataFixes, JsonObject jsonObject, String worldName, int fixVersion) {
        logDataFixer(worldName, fixVersion);
        
        for (int i = 0; i < dataFixes.length; ++i) {
            DataFix dataFix = dataFixes[i];
            String tag = dataFix.getTag();
            
            // logDataFix(tag, worldName, fixVersion);
            
            JsonElement newValue = dataFix.getDataFixer().apply(ModDataFixer.getAsJsonObject(jsonObject.toString(), worldName));
            if (newValue != null) {
                jsonObject.add(tag, newValue);
            }
        }
    }
    
    private static void logDataFixer(String worldName, int fixVersion) {
        ModernBeta.log(Level.DEBUG, String.format("[DataFix] Found world '%s' to be fixed for version '%d'..", worldName, fixVersion));
    }
    
    @SuppressWarnings("unused")
    private static void logDataFix(String key, String worldName, int fixVersion) {
        ModernBeta.log(Level.DEBUG, String.format("[DataFix] Found property '%s' in world '%s' to be fixed for version '%d'..", key, worldName, fixVersion));
    }
}
