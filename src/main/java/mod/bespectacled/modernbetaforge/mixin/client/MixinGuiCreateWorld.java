package mod.bespectacled.modernbetaforge.mixin.client;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;

@Mixin(GuiCreateWorld.class)
public class MixinGuiCreateWorld {
    @Shadow private int selectedIndex;
    @Shadow private String chunkProviderSettingsJson;
    
    /*
     * This handles setting the default world preset just when cycling world types.
     * If Modern Beta is set as default world type, then default world preset is set in InitGuiEventHandler.
     * 
     */
    @Inject(method = "actionPerformed", at = @At("RETURN"))
    private void injectActionPerformed(GuiButton button, CallbackInfo info) {
        if (button.enabled && button.id == 5 && this.selectedIndex == ModernBetaWorldType.INSTANCE.getId()) {
            if (this.chunkProviderSettingsJson.isEmpty() && !ModernBetaConfig.guiOptions.defaultPreset.isEmpty()) {
                this.chunkProviderSettingsJson = ModernBetaConfig.guiOptions.defaultPreset;
            }
        }
    }
}
