package mod.bespectacled.modernbetaforge;

import mod.bespectacled.modernbetaforge.event.PlayerEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class ModernBetaServerProxy implements ModernBetaProxy {
    @Override
    public void preInit() { }
    
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
    }
}
