package mod.bespectacled.modernbetaforge.config;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizePresets.FilterType;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import net.minecraftforge.common.config.Config.SlidingOption;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ModernBeta.MODID)
public class ModernBetaConfig {
    public static CategoryGui guiOptions = new CategoryGui();
    public static CategoryGenerator generatorOptions = new CategoryGenerator();
    public static CategoryVisual visualOptions = new CategoryVisual();
    public static CategorySpawn spawnOptions = new CategorySpawn();
    public static CategoryServer serverOptions = new CategoryServer();
    public static CategoryDebug debugOptions = new CategoryDebug();
    
    public static class CategoryGui {
        @Comment({
            "Default Modern Beta world preset."
        })
        @RequiresWorldRestart
        public String defaultPreset = "";
        
        @Comment({
            "Default Modern Beta world preset filter."
        })
        @RequiresWorldRestart
        public FilterType defaultPresetFilter = FilterType.ALL;

        @Comment({
            "Set Modern Beta world type as default."
        })
        @RequiresWorldRestart
        public boolean useModernBetaAsDefault = false;
        
        @Comment({
            "Use customization menus for selecting basic generator settings. Applies to chunk source, biome source, surface builder, and cave carver options."
        })
        @RequiresWorldRestart
        public boolean useMenusForBasicSettings = true;
    }
    
    public static class CategoryGenerator {
        @Comment({
            "Add biomes with custom surface builders for compatibility, requires fully-qualified biome registry name."
        })
        @RequiresWorldRestart
        public String[] biomesWithCustomSurfaces = {};
        
        @Comment({
            "Save Indev level to separate file and try to load instead of regenerating level."
        })
        @RequiresWorldRestart
        public boolean saveIndevLevels = true;
        
        @Comment({
            "Max size of chunk cache. Higher values may improve performance at the cost of memory consumption, particularly in heavily-modded worlds. You probably don't need to change this!"
        })
        @RequiresWorldRestart
        @SlidingOption
        @RangeInt(min = ChunkCache.MIN_CACHE_SIZE, max = ChunkCache.MAX_CACHE_SIZE)
        public int chunkCacheSize = ChunkCache.DEFAULT_CACHE_SIZE;
    }

    public static class CategoryVisual {
        @Comment({
            "Render Beta-accurate biome colors."
        })
        @RequiresWorldRestart
        public boolean useBetaBiomeColors = true;
        
        @Comment({
            "Render Beta-accurate sky colors."
        })
        @RequiresWorldRestart
        public boolean useBetaSkyColors = true;
        
        @Comment({
            "Render old sugar cane colors."
        })
        @RequiresWorldRestart
        public boolean useOldSugarCaneColor = true;
        
        @Comment({
            "Render fog with old fog color blending algorithm."
        })
        public boolean useOldFogColorBlending = true;
        
        @Comment({
            "Modern Beta world cloud height.",
        })
        @RangeInt(min = 0, max = 255)
        public int cloudHeight = 108;
        
        @Comment({
            "Override default cloud height."
        })
        public boolean useCustomCloudHeight = false;
        
        @Comment({
            "Render biome colors with increasingly lower temperatures above y128."
        })
        @RequiresWorldRestart
        public boolean useHeightTempGradient = true;
    }
    
    public static class CategorySpawn {
        @Comment({
            "Use spawn fuzz for players who join the world."
        })
        @RequiresWorldRestart
        public boolean useSpawnFuzz = false;
    }
    
    public static class CategoryServer {
        @Comment({
            "Send world cloud height to clients."
        })
        @RequiresWorldRestart
        public boolean sendCloudHeight = true;
        
        @Comment({
            "Send world information for rendering Beta-accurate biome colors on the client-side. Warning: This will transmit the world seed and generator settings to clients."
        })
        @RequiresWorldRestart
        public boolean sendWorldInfo = false;
    }
    
    public static class CategoryDebug {
        @Comment({
            "Display Modern Beta debug info in debug screen."
        })
        public boolean displayDebugInfo = false;
        
        @Comment({
            "Register debug properties at mod start-up."
        })
        @RequiresMcRestart
        public boolean registerDebugProperties = false;
        
        @Comment({
            "Debug block sources."
        })
        @RequiresWorldRestart
        public boolean debugBlockSources = false;
        
        @Comment({
            "Debug Indev level saving functionality."
        })
        @RequiresWorldRestart
        public boolean debugIndevLevelSaves = false;
    }
    
    @Mod.EventBusSubscriber(modid = ModernBeta.MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ModernBeta.MODID)) {
                ConfigManager.sync(ModernBeta.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
