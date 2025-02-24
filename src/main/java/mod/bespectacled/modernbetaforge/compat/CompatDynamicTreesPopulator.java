package mod.bespectacled.modernbetaforge.compat;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.logging.log4j.Level;

import com.ferreusveritas.dynamictrees.api.WorldGenRegistry.BiomeDataBasePopulatorRegistryEvent;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.util.JsonHelper;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBasePopulatorJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompatDynamicTreesPopulator implements IBiomeDataBasePopulator {
    private static final String CONFIG_FILE_NAME = ModernBeta.MODID + "_dynamictrees.cfg";
    private static final ResourceLocation DEFAULT_PATH = ModernBeta.createRegistryKey("worldgen/dynamic_trees.json");

    private final BiomeDataBasePopulatorJson jsonPopulator;

    public CompatDynamicTreesPopulator() {
        DynamicTreesConfigHandler configHandler = new DynamicTreesConfigHandler(DEFAULT_PATH);
        this.jsonPopulator = new BiomeDataBasePopulatorJson(configHandler.readConfig());
    }

    @Override
    public void populate(BiomeDataBase database) {
        this.jsonPopulator.populate(database);
    }
    
    @SubscribeEvent
    public static void registerDataBasePopulators(BiomeDataBasePopulatorRegistryEvent event) {
        event.register(new CompatDynamicTreesPopulator());
    }
    
    private static class DynamicTreesConfigHandler {
        private static final File CONFIG_FILE = new File(new File(Minecraft.getMinecraft().gameDir, "config"), CONFIG_FILE_NAME);
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        
        private final JsonElement defaultJson;
        
        public DynamicTreesConfigHandler(ResourceLocation defaultPath) {
            this.defaultJson = JsonHelper.load(defaultPath);
        }
        
        public JsonElement readConfig() {
            JsonElement loadedJson;
            
            if (!CONFIG_FILE.isFile()) {
                ModernBeta.log(Level.WARN, String.format("Dynamic Trees config file '%s' is missing and couldn't be loaded! A new one will be created!", CONFIG_FILE_NAME));
                this.writeConfig();
            }
            
            if ((loadedJson = JsonHelper.load(CONFIG_FILE)) == null) {
                ModernBeta.log(Level.WARN, String.format("Dynamic Trees config file '%s' is corrupted and couldn't be loaded! The default config will be used!", CONFIG_FILE_NAME));
                loadedJson = this.defaultJson;
            }
            
            return loadedJson;
        }
        
        private void writeConfig() {
            if (!CONFIG_FILE.isFile()) {
                try (Writer writer = new FileWriter(CONFIG_FILE)) {
                    GSON.toJson(this.defaultJson, JsonElement.class, writer);
                } catch (Exception e) {
                    ModernBeta.log(Level.ERROR, String.format("Dynamic Trees config file '%s' couldn't be saved!", CONFIG_FILE_NAME));
                    ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
                }
            }
        }
    }
}
