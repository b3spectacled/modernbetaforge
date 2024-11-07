package mod.bespectacled.modernbetaforge.config;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ModernBeta.MODID)
public class ModernBetaConfig {
    public static CategoryGenerator generatorOptions = new CategoryGenerator();
    public static CategoryVisual visualOptions = new CategoryVisual();
    public static CategorySpawn spawnOptions = new CategorySpawn();
    public static CategoryHud hudOptions = new CategoryHud();
    
    public static class CategoryGenerator {
        @Comment({
            "Add biomes with custom surface builders for compatibility, requires fully-qualified biome registry name"
        })
        @RequiresWorldRestart
        public String[] biomesWithCustomSurfaces = {};

        @Comment({
            "Add custom Modern Beta world presets"
        })
        @RequiresWorldRestart
        public String[] customPresets = {};

        @Comment({
            "Set Modern Beta world type as default"
        })
        @RequiresWorldRestart
        public boolean useModernBetaAsDefault = false;
        
    }

    public static class CategoryVisual {
        @Comment({
            "Render Beta-accurate biome colors"
        })
        @RequiresWorldRestart
        public boolean useBetaBiomeColors = true;
        
        @Comment({
            "Render Beta-accurate sky colors"
        })
        @RequiresWorldRestart
        public boolean useBetaSkyColors = true;
        
        @Comment({
            "Render old sugar cane colors"
        })
        @RequiresWorldRestart
        public boolean useOldSugarCaneColor = true;
        
        @Comment({
            "Render fog with old fog color blending algorithm"
        })
        public boolean useOldFogColorBlending = true;
        
        @Comment({
            "Modern Beta world cloud height",
        })
        @RangeInt(min = 0, max = 255)
        public int cloudHeight = 108;
        
        @Comment({
            "Render biome colors with increasingly lower temperatures above y128"
        })
        @RequiresWorldRestart
        public boolean useHeightTempGradient = true;
    }
    
    public static class CategoryHud {
        @Comment({
            "Render version text in the top-left corner of the HUD"
        })
        public boolean useVersionText = false;
        
        @Comment({
            "Render custom version text"
        })
        public boolean useCustomVersionText = false;
        
        @Comment({
            "Custom version text"
        })
        public String customVersionText = "Minecraft Beta 1.7.3";
    }
    
    public static class CategorySpawn {
        @Comment({
            "Use spawn fuzz for players who join the world"
        })
        @RequiresWorldRestart
        public boolean useSpawnFuzz = false;
        
        @Comment({
            "Use old spawn algorithm for initial player spawns"
        })
        @RequiresWorldRestart
        public boolean useOldSpawns = true;
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
