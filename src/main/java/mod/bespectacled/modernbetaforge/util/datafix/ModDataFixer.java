package mod.bespectacled.modernbetaforge.util.datafix;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.datafix.DataFix;
import mod.bespectacled.modernbetaforge.api.datafix.ModDataFix;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ModDataFixer {
    public static final ModDataFixer INSTANCE = new ModDataFixer();
    
    private final CompoundDataFixer forgeDataFixer;
    private final ModFixs modFixer;
    
    private ModDataFixer() {
        this.forgeDataFixer = FMLCommonHandler.instance().getDataFixer();
        this.modFixer = this.forgeDataFixer.init(ModernBeta.MODID, ModernBeta.DATA_VERSION);
    }
    
    public void register() {
        ModernBetaRegistries.MOD_DATA_FIX.getValues().forEach(value -> this.registerModDataFix(value));
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
    
    private void registerModDataFix(ModDataFix fix) {
        this.modFixer.registerFix(fix.getFixType(), fix.getFixableData());
    }
}
