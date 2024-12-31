package mod.bespectacled.modernbetaforge.util.datafix;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.util.datafix.DataFixers.DataFix;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;

public class DataFixer {
    public static void runDataFixer(ResourceLocation registryKey, ModernBetaGeneratorSettings.Factory factory, JsonObject jsonObject, String worldName, int fixVersion) {
        DataFix dataFix = ModernBetaRegistries.DATA_FIX.get(registryKey);
        
        logDataFix(dataFix.getTag(), worldName, fixVersion);
        dataFix.getDataFixConsumer().accept(factory, jsonObject);
    }
    
    private static void logDataFix(String key, String worldName, int fixVersion) {
        ModernBeta.log(Level.INFO, String.format("[DataFix] Found old property '%s' in world '%s' to be fixed for version '%d'..", key, worldName, fixVersion));
    }
}
