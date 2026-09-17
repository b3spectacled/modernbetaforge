package mod.bespectacled.modernbetaforge.compat.cubicchunks;

import net.minecraft.world.WorldType;

public class CubicModernBetaWorldTypeRegistrar {
    public static WorldType createCubicChunksWorldType() {
        return new CubicModernBetaWorldType();
    }
}
