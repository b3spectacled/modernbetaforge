package mod.bespectacled.modernbetaforge.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.WorldServer;

@Mixin(WorldServer.class)
public interface AccessorWorldServer {
    @Invoker("createBonusChest")
    void invokeCreateBonusChest();
}
