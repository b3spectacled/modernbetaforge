package mod.bespectacled.modernbetaforge;

import mod.bespectacled.modernbetaforge.client.settings.KeyBindings;
import mod.bespectacled.modernbetaforge.compat.ClientModCompat;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.event.BlockColorsEventHandler;
import mod.bespectacled.modernbetaforge.event.DebugInfoEventHandler;
import mod.bespectacled.modernbetaforge.event.InitGuiEventHandler;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInRegistries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModernBetaClientProxy implements ModernBetaProxy {
    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new BlockColorsEventHandler());
    }
    
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new DebugInfoEventHandler());
        MinecraftForge.EVENT_BUS.register(new InitGuiEventHandler());
        
        ModernBetaBuiltInRegistries.registerPresets();
        ModernBetaBuiltInRegistries.registerPredicates();

        if (ModernBetaConfig.debugOptions.registerDebugProperties) {
            ModernBetaBuiltInRegistries.registerGuiProperties();
        }
        
        ClientRegistry.registerKeyBinding(KeyBindings.LEFT_NAV_KEY);
        ClientRegistry.registerKeyBinding(KeyBindings.RIGHT_NAV_KEY);

        ClientModCompat.loadCompat();
    }
}
