package mod.bespectacled.modernbetaforge.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

@Mixin(GuiSlot.class)
public interface AccessorGuiSlot {
    @Accessor
    Minecraft getMC();
    
    @Accessor
    int getWidth();
}
