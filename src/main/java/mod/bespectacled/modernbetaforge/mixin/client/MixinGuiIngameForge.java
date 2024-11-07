package mod.bespectacled.modernbetaforge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.GuiIngameForge;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {
    @ModifyVariable(method = "renderHUDText(II)V", at = @At("STORE"), index = 6)
    private int injectRenderHUDText(int top) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRenderer;
        ModernBetaConfig.CategoryHud options = ModernBetaConfig.hudOptions;
        
        if (
            top == 2 &&
            !mc.gameSettings.showDebugInfo &&
            mc.world.getWorldType() instanceof ModernBetaWorldType &&
            options.useVersionText
        ) {
            String text = options.useCustomVersionText ? options.customVersionText : "Minecraft " + mc.getVersion();
            fontRenderer.drawStringWithShadow(text, 2, top, 14737632);
            top += fontRenderer.FONT_HEIGHT;
        }
        
        return top;
    }
}
