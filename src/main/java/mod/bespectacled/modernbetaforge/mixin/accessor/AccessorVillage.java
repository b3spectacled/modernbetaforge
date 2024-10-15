package mod.bespectacled.modernbetaforge.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.gen.structure.StructureVillagePieces;

@Mixin(StructureVillagePieces.Village.class)
public interface AccessorVillage {
    @Accessor("structureType")
    public void setStructureType(int structureType);
}
