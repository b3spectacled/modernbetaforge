package mod.bespectacled.modernbetaforge.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public interface AccessorEntityRenderer {
    @Accessor
    Minecraft getMC();
}
