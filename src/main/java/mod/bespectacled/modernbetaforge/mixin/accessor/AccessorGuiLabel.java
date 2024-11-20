package mod.bespectacled.modernbetaforge.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.GuiLabel;

@Mixin(GuiLabel.class)
public interface AccessorGuiLabel {
    @Accessor
    List<String> getLabels();
}
