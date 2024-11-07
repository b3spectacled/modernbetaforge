package mod.bespectacled.modernbetaforge;

import mod.bespectacled.modernbetaforge.event.BlockColorsEventHandler;
import mod.bespectacled.modernbetaforge.event.DebugInfoEventHandler;
import mod.bespectacled.modernbetaforge.event.InitGuiEventHandler;
import mod.bespectacled.modernbetaforge.event.WorldEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class ModernBetaClientProxy implements ModernBetaProxy {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new DebugInfoEventHandler());
        MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
        MinecraftForge.EVENT_BUS.register(new InitGuiEventHandler());
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new BlockColorsEventHandler());
    }
}
