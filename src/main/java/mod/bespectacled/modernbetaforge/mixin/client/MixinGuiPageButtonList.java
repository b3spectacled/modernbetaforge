package mod.bespectacled.modernbetaforge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mod.bespectacled.modernbetaforge.client.gui.GuiColoredLabelEntry;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorGuiSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiPageButtonList;

@Mixin(GuiPageButtonList.class)
public class MixinGuiPageButtonList {
    @Inject(method = "createLabel", at = @At("HEAD"), cancellable = true)
    private void injectCreateLabel(int x, int y, GuiPageButtonList.GuiLabelEntry entry, boolean isEntryNull, CallbackInfoReturnable<GuiLabel> info) {
        if (entry instanceof GuiColoredLabelEntry) {
            GuiColoredLabelEntry colorEntry = (GuiColoredLabelEntry)entry;
            AccessorGuiSlot accessor = (AccessorGuiSlot)this;
            
            Minecraft mc = accessor.getMC();
            GuiLabel guiLabel;

            if (isEntryNull) {
                guiLabel = new GuiLabel(mc.fontRenderer, entry.getId(), x, y, accessor.getWidth() - x * 2, 20, colorEntry.getColor());
                
            } else {
                guiLabel = new GuiLabel(mc.fontRenderer, entry.getId(), x, y, 150, 20, colorEntry.getColor());
                
            }
    
            guiLabel.visible = entry.shouldStartVisible();
            guiLabel.addLine(entry.getCaption());
            guiLabel.setCentered();

            info.setReturnValue(guiLabel);
        }
    }
}
