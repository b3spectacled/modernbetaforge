package mod.bespectacled.modernbetaforge.util.datafix;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public class DataFixer {
    private static final Set<String> LOGGED_DATA_FIXES = new HashSet<>();
    private static ComparableVersion version = new ComparableVersion(ModernBeta.VERSION);
    
    public static void runDataFixer(JsonObject jsonObject) {
        ModernBetaRegistries.DATA_FIX.getKeys().stream().forEach(key -> {
            if (jsonObject.has(key)) {
                DataFix dataFix = ModernBetaRegistries.DATA_FIX.get(key);
                
                logDataFix(key, version);
                dataFix.tryFix(jsonObject, version);
            }
        });
    }
    
    public static void readModVersion(File directory) {
        File levelDat = new File(directory, "level.dat");
       
        if (!levelDat.exists())
            return;
        
        try {
            NBTTagCompound compoundLevel = CompressedStreamTools.readCompressed(new FileInputStream(levelDat));
            
            if (compoundLevel.hasKey("FML")) {
                NBTTagCompound compoundFML = compoundLevel.getCompoundTag("FML");
                
                compoundFML.getTagList("ModList", 10).forEach(nbt -> {
                    NBTTagCompound compoundMod = (NBTTagCompound)nbt;
                    
                    if (compoundMod.hasKey("ModId") && compoundMod.getString("ModId").equals(ModernBeta.MODID)) {
                        String modVersion = compoundMod.getString("ModVersion");
                        version = new ComparableVersion(modVersion);
                        
                        ModernBeta.log(Level.DEBUG, String.format("Save file version is '%s'.", modVersion));
                    }
                });;
            }
            
        } catch (Exception e) {
            ModernBeta.log(Level.ERROR, String.format("File '%s' not found!", levelDat.toString()));
        }
    }
    
    public static void clearLoggedDataFixes() {
        LOGGED_DATA_FIXES.clear();
    }
    
    private static void logDataFix(String key, ComparableVersion version) {
        if (!LOGGED_DATA_FIXES.contains(key)) {
            LOGGED_DATA_FIXES.add(key);
            
            ModernBeta.log(
                Level.INFO,
                String.format("[DataFix] Found old property '%s' from version '%s', fixing..", key, version.toString())
            );
        }
    }
    
    public static class DataFix {
        private final Predicate<ComparableVersion> dataFixPredicate;
        private final Consumer<JsonObject> dataFix;
        
        public DataFix(Predicate<ComparableVersion> dataFixPredicate, Consumer<JsonObject> dataFix) {
            this.dataFixPredicate = dataFixPredicate;
            this.dataFix = dataFix;
        }
        
        private void tryFix(JsonObject jsonObject, ComparableVersion version) {
            if (this.dataFixPredicate.test(version)) {
                this.dataFix.accept(jsonObject);
            }
        }
    }
}
