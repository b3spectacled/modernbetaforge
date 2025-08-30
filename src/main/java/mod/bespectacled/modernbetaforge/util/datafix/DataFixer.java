package mod.bespectacled.modernbetaforge.util.datafix;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.datafix.DataFix;

public class DataFixer {
    public static void runDataFixer(DataFix[] dataFixes, JsonObject jsonObject, String worldName, int fixVersion) {
        for (int i = 0; i < dataFixes.length; ++i) {
            DataFix dataFix = dataFixes[i];
            
            logDataFix(dataFix.getTag(), worldName, fixVersion);
            dataFixes[i].getDataFixConsumer().accept(jsonObject);
        }
    }
    
    private static void logDataFix(String key, String worldName, int fixVersion) {
        ModernBeta.log(Level.DEBUG, String.format("[DataFix] Found property '%s' in world '%s' to be fixed for version '%d'..", key, worldName, fixVersion));
    }
}
