package mod.bespectacled.modernbetaforge.compat.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.util.IntRange;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType;
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator;
import io.github.opencubicchunks.cubicchunks.core.world.provider.ICubicWorldProvider;
import mod.bespectacled.modernbetaforge.world.AdjustableCloudHeightWorldType;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CubicModernBetaWorldType extends ModernBetaWorldType implements ICubicWorldType, AdjustableCloudHeightWorldType {
    public CubicModernBetaWorldType() {
        super("modernbetacubic");
    }

    @Override
    public IntRange calculateGenerationHeightRange(WorldServer worldServer) {
        return new IntRange(0, ((ICubicWorldProvider)worldServer.provider).getOriginalActualHeight());
    }

    @Override
    public ICubeGenerator createCubeGenerator(World world) {
        return new CubicModernBetaChunkGenerator(new ModernBetaChunkGenerator(world, world.getWorldInfo().generatorOptions), world);
    }

    @Override
    public boolean hasCubicGeneratorForWorld(World world) {
        return world.provider.getDimensionType() == DimensionManager.getProviderType(0);
    }

    public static void register() { }
}
