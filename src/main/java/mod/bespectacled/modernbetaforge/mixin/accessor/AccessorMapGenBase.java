package mod.bespectacled.modernbetaforge.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

@Mixin(MapGenBase.class)
public interface AccessorMapGenBase {
    @Accessor
    World getWorld();
}
