package mod.bespectacled.modernbetaforge.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

@Mixin(StructureStart.class)
public interface AccessorStructureStart {
    @Accessor
    List<StructureComponent> getComponents();
    
    @Invoker("updateBoundingBox")
    void invokeUpdateBoundingBox();
}
