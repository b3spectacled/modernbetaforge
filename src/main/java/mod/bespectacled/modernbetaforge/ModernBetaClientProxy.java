package mod.bespectacled.modernbetaforge;

import mod.bespectacled.modernbetaforge.eventhandler.BiomeColorsEventHandler;
import mod.bespectacled.modernbetaforge.eventhandler.DebugInfoEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class ModernBetaClientProxy implements ModernBetaProxy {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new DebugInfoEventHandler());
        MinecraftForge.EVENT_BUS.register(new BiomeColorsEventHandler());
    }
}
