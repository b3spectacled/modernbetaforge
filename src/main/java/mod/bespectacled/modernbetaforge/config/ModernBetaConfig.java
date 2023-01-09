package mod.bespectacled.modernbetaforge.config;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ModernBeta.MODID)
public class ModernBetaConfig {
    
    @Name("Default Generator Options")
    @Comment({
        "Default Modern Beta world generation options"
    })
    public static String defaultGeneratorOptions = "";

    @Name("Use Beta Biome Colors")
    @Comment({
        "Render Beta-accurate biome colors"
    })
    @RequiresWorldRestart
    public static boolean useBetaBiomeColors = true;
    
    @Name("Use Beta Sky Colors")
    @Comment({
        "Render Beta-accurate sky colors"
    })
    @RequiresWorldRestart
    public static boolean useBetaSkyColors = true;

    @Name("Modern Beta Cloud Height")
    @Comment({
        "Modern Beta world cloud height",
    })
    @RangeInt(min = 0, max = 128)
    @RequiresMcRestart
    public static int cloudHeight = 108;

    @Name("Use Spawn Fuzz")
    @Comment({
        "Use spawn fuzz for players who join the world"
    })
    @RequiresMcRestart
    public static boolean useSpawnFuzz = false;
    
    @Name("Biomes with Custom Surfaces")
    @Comment({
        "Add biomes with custom surface builders for compatibility, requires fully-qualified biome registry name"
    })
    @RequiresMcRestart
    public static String[] biomesWithCustomSurfaces = {};
    
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
