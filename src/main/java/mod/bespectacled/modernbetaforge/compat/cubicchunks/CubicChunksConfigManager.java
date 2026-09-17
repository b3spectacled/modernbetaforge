package mod.bespectacled.modernbetaforge.compat.cubicchunks;

import io.github.opencubicchunks.cubicchunks.core.CubicChunksConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CubicChunksConfigManager {
    public static final CubicChunksConfigManager INSTANCE = new CubicChunksConfigManager();
    
    private int defaultMinHeight;
    private int defaultMaxHeight;
    
    private CubicChunksConfigManager() {
        this.reloadConfig();
    }
    
    public void reloadConfig() {
        this.defaultMinHeight = CubicChunksConfig.defaultMinHeight;
        this.defaultMaxHeight = CubicChunksConfig.defaultMaxHeight;
    }
    
    public int getDefaultMinHeight() {
        return this.defaultMinHeight;
    }
    
    public int getDefaultMaxHeight() {
        return this.defaultMaxHeight;
    }
    
    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(CompatCubicChunks.MOD_ID)) {
            INSTANCE.reloadConfig();
        }
    }
}
