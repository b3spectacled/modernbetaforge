package mod.bespectacled.modernbetaforge.util.datafix;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.DataFix;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;

public class DataFixer {
    public static void runDataFixer(String key, ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        if (jsonObject.has(key)) {
            DataFix dataFix = ModernBetaRegistries.DATA_FIX.get(key);
            
            logDataFix(key);
            dataFix.apply(factory, jsonObject);
        }
    }
    
    private static void logDataFix(String key) {
        ModernBeta.log(Level.INFO, String.format("[DataFix] Found old property '%s', fixing..", key));
    }
}
