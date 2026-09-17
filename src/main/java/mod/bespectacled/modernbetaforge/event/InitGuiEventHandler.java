package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.PresetUtil;
import mod.bespectacled.modernbetaforge.world.WorldTypeManager;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InitGuiEventHandler {
    /*
     * This handles setting the default world preset just when clicking Create World directly.
     * Cycling through world presets resets the generator settings string, so if using default preset string,
     * then it must also be set in MixinGuiCreateWorld.
     * 
     */
    @SubscribeEvent
    public void onInitGuiEventPre(InitGuiEvent.Pre event) {
        GuiScreen guiScreen = event.getGui();
        
        if (ModernBetaConfig.guiOptions.useModernBetaAsDefault && guiScreen instanceof GuiCreateWorld) {
            GuiCreateWorld guiCreateWorld = (GuiCreateWorld)guiScreen;
            
            if (guiCreateWorld.selectedIndex == WorldType.DEFAULT.getId()) {
                guiCreateWorld.selectedIndex = WorldTypeManager.INSTANCE.getWorldTypeId();
                if (guiCreateWorld.chunkProviderSettingsJson.isEmpty() && PresetUtil.isDefaultPresetModified()) {
                    guiCreateWorld.chunkProviderSettingsJson = PresetUtil.getDefaultPreset();
                }
            }
        }
    }
}
