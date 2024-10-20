package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.DataFix;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;

public class DataFixer {
    private static final Set<String> LOGGED_DATA_FIXES = new HashSet<>();
    
    public static void runDataFixer(ModernBetaChunkGeneratorSettings.Factory factory, JsonObject jsonObject) {
        ModernBetaRegistries.DATA_FIX.getKeys().stream().forEach(key -> {
            if (jsonObject.has(key)) {
                DataFix dataFix = ModernBetaRegistries.DATA_FIX.get(key);
                
                logDataFix(key);
                dataFix.apply(factory, jsonObject);
            }
        });
    }
    
    private static void logDataFix(String key) {
        ModernBeta.log(Level.INFO, String.format("[DataFix] Found old property '%s', fixing..", key));
    }
}
