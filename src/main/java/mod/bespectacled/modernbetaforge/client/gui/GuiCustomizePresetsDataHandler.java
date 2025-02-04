package mod.bespectacled.modernbetaforge.client.gui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.client.Minecraft;

public class GuiCustomizePresetsDataHandler {
    public static final String FILE_NAME = ModernBeta.MODID + "_presets.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private final File configDirectory;
    private final Set<PresetData> presets;

    public GuiCustomizePresetsDataHandler(Minecraft mc) {
        this.configDirectory = new File(mc.gameDir, "config");
        this.presets = this.readPresets();
    }
    
    public void writePresets() {
        try {
            this.writeToDisk();
            
        } catch (Exception e) {
            ModernBeta.log(Level.ERROR, String.format("Preset file '%s' couldn't be saved!", FILE_NAME));
            ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
        }
    }
    
    public void addPreset(String name, String desc, String settings) {
        this.presets.add(new PresetData(name, desc, settings));
    }
    
    public void removePreset(String name) {
        if (this.containsPreset(name)) {
            this.presets.remove(new PresetData(name));
        }
    }
    
    public boolean containsPreset(String name) {
        return this.presets.contains(new PresetData(name));
    }
    
    public List<PresetData> getPresets() {
        List<PresetData> list = new LinkedList<>();
        list.addAll(this.presets);
        
        return list;
    }
    
    private Set<PresetData> readPresets() {
        Set<PresetData> presets;
        
        try {
            presets = readFromDisk();
            
        } catch (Exception e) {
            presets = new LinkedHashSet<>();
            
            ModernBeta.log(Level.WARN, String.format("Preset file '%s' is missing or corrupted and couldn't be loaded!", FILE_NAME));
            ModernBeta.log(Level.WARN, "Error: " + e.getMessage());
        }
        
        return presets;
    }
    
    private void writeToDisk() throws Exception {
        try (Writer writer = new FileWriter(new File(this.configDirectory, FILE_NAME))) {
            GSON.toJson(this.presets, LinkedHashSet.class, writer);
        }
    }

    private Set<PresetData> readFromDisk() throws Exception {
        try (Reader reader = new FileReader(new File(this.configDirectory, FILE_NAME))) {
            Type type = new TypeToken<Set<PresetData>>(){}.getType();
            return GSON.fromJson(reader, type);
        }
    }
    
    public static class PresetData implements Serializable {
        private static final long serialVersionUID = 3417451508681238782L;
        
        public final String name;
        public final String desc;
        public final String settings;
        
        private PresetData(String name) {
            this(name, "", "");
        }
        
        private PresetData(String name, String desc, String settings) {
            this.name = name.trim();
            this.desc = desc.trim();
            this.settings = settings.trim();
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            
            if (!(o instanceof PresetData)) {
                return false;
            }
            
            return this.name.equals(((PresetData)o).name);
        }
        
        // This also needs to overridden for equality checks to work properly
        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}
