package mod.bespectacled.modernbetaforge.config;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ModernBeta.MODID)
public class ModernBetaConfig {
    public static CategoryGenerator generatorOptions = new CategoryGenerator();
    public static CategoryVisual visualOptions = new CategoryVisual();
    public static CategoryMob mobOptions = new CategoryMob();
    public static CategorySpawn spawnOptions = new CategorySpawn();
    
    public static class CategoryGenerator {
        @Comment({
            "Add biomes with custom surface builders for compatibility, requires fully-qualified biome registry name"
        })
        @RequiresMcRestart
        public String[] biomesWithCustomSurfaces = {};
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
        public boolean useOldSugarCaneColor = false;
        
        @Comment({
            "Modern Beta world cloud height",
        })
        @RangeInt(min = 0, max = 255)
        @RequiresMcRestart
        public int cloudHeight = 108;
    }
    
    public static class CategoryMob {
        @Comment({
            "Use new mobs when initializing Beta biomes"
        })
        @RequiresMcRestart
        public boolean useNewMobs = true;
    }
    
    public static class CategorySpawn {
        @Comment({
            "Use spawn fuzz for players who join the world"
        })
        @RequiresMcRestart
        public boolean useSpawnFuzz = false;
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
