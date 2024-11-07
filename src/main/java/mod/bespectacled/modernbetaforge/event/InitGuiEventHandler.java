package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InitGuiEventHandler {
    @SubscribeEvent
    public void onInitGuiEventPre(InitGuiEvent.Pre event) {
        GuiScreen guiScreen = event.getGui();
        
        if (ModernBetaConfig.generatorOptions.useModernBetaAsDefault && guiScreen instanceof GuiCreateWorld) {
            GuiCreateWorld guiCreateWorld = (GuiCreateWorld)guiScreen;
            
            if (guiCreateWorld.selectedIndex == WorldType.DEFAULT.getId()) {
                guiCreateWorld.selectedIndex = ModernBetaWorldType.INSTANCE.getId();
            }
        }
    }
}
