package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.datafix.DataFix;
import mod.bespectacled.modernbetaforge.api.datafix.ModDataFix;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaModRegistry;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ModDataFixer {
    public static final ModDataFixer INSTANCE = new ModDataFixer();

    private final Map<String, ModFixs> modFixers;
    
    private ModDataFixer() {
        this.modFixers = initModFixers(FMLCommonHandler.instance().getDataFixer());
    }
    
    public void register() {
        ModernBetaRegistries.MOD_DATA_FIX.getEntries()
            .forEach(entry -> this.registerModDataFix(entry.getKey(), entry.getValue()));
    }
    
    private void registerModDataFix(ResourceLocation registryKey, ModDataFix fix) {
        String namespace = registryKey.getNamespace();
        
        if (this.modFixers.containsKey(namespace)) {
            this.modFixers.get(namespace).registerFix(fix.getFixType(), fix.getFixableData());
        }
    }
    
    public static NBTTagCompound fixGeneratorSettings(NBTTagCompound compound, DataFix[] dataFixes, int fixVersion) {
        String worldName = compound.hasKey("LevelName") ? compound.getString("LevelName") : "";
        
        if (compound.hasKey("generatorName") &&
            compound.getString("generatorName").equals("modernbeta") &&
            compound.hasKey("generatorOptions")
        ) {
            JsonObject jsonObject = getAsJsonObject(compound.getString("generatorOptions"));
            DataFixer.runDataFixer(dataFixes, jsonObject, worldName, fixVersion);
            compound.setString("generatorOptions", jsonObject.toString().replace("\n", ""));
        }
        
        return compound;
    }
    
    public static JsonObject getAsJsonObject(String json) {
        JsonObject jsonObject;
        
        try {
            jsonObject = new JsonParser().parse(json).getAsJsonObject();
        } catch (Exception e) {
            ModernBeta.log(Level.ERROR, "Couldn't parse generator options for data fixer..");
            jsonObject = new JsonObject();      
        }
        
        return jsonObject;
    }
    
    private static Map<String, ModFixs> initModFixers(CompoundDataFixer forgeDataFixer) {
        List<Entry<String, Integer>> mods = ModernBetaModRegistry.INSTANCE.getEntries();
        Map<String, ModFixs> modFixers = new LinkedHashMap<>();
        
        for (Entry<String, Integer> mod : mods) {
            modFixers.put(mod.getKey(), forgeDataFixer.init(mod.getKey(), mod.getValue()));
        }
        
        return modFixers;
    }
}
