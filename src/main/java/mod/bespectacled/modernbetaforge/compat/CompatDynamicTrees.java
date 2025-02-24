package mod.bespectacled.modernbetaforge.compat;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import org.apache.logging.log4j.Level;

import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompatDynamicTrees implements Compat {
    private static final String CONFIG_FILE_NAME = ModernBeta.MODID + "_dynamictrees.cfg";
    
    @Override
    public void load() { 
        MinecraftForge.EVENT_BUS.register(CompatDynamicTrees.class);
    }
    
    @SubscribeEvent
    public static void registerDataBasePopulators(BiomeDataBasePopulatorRegistryEvent event) {
        event.register(new BiomeDataBasePopulator());
    }
    
    public static boolean isEnabled() {
        return WorldGenRegistry.isWorldGenEnabled();
    }

    private static class BiomeDataBasePopulator implements IBiomeDataBasePopulator {
        public static final ResourceLocation PATH = ModernBeta.createRegistryKey("worldgen/dynamic_trees.json");

        private final BiomeDataBasePopulatorJson jsonPopulator;

        public BiomeDataBasePopulator() {
            DynamicTreesConfigHandler configHandler = new DynamicTreesConfigHandler(PATH);
            configHandler.readConfig();

            this.jsonPopulator = new BiomeDataBasePopulatorJson(configHandler.loadedJson);
        }

        @Override
        public void populate(BiomeDataBase database) {
            this.jsonPopulator.populate(database);
        }
    }
    
    private static class DynamicTreesConfigHandler {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        private static final File CONFIG = new File(new File(Minecraft.getMinecraft().gameDir, "config"), CONFIG_FILE_NAME);
        
        private final JsonElement defaultJson;
        private JsonElement loadedJson;
        
        public DynamicTreesConfigHandler(ResourceLocation defaultPath) {
            this.defaultJson = JsonHelper.load(defaultPath);
        }
        
        public void readConfig() {
            if (!CONFIG.isFile()) {
                ModernBeta.log(Level.WARN, String.format("Dynamic Trees config file '%s' is missing and couldn't be loaded! A new one will be created!", CONFIG));
                this.writeConfig();
            }
            
            if ((this.loadedJson = JsonHelper.load(CONFIG)) == null) {
                ModernBeta.log(Level.WARN, String.format("Dynamic Trees config file '%s' is corrupted and couldn't be loaded! The default config will be used!", CONFIG));
                this.loadedJson = defaultJson;
            }
        }
        
        private void writeConfig() {
            try {
                if (!CONFIG.isFile()) {
                    try (Writer writer = new FileWriter(CONFIG)) {
                        GSON.toJson(this.defaultJson, JsonElement.class, writer);
                    }
                }
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, String.format("Dynamic Trees config file '%s' couldn't be saved!", CONFIG));
                ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
            }
        }
    }
}
