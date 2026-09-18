package mod.bespectacled.modernbetaforge.compat.cubicchunks;

import io.github.opencubicchunks.cubicchunks.api.util.IntRange;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorldType;
import io.github.opencubicchunks.cubicchunks.api.worldgen.ICubeGenerator;
import mod.bespectacled.modernbetaforge.world.AdjustableCloudHeightWorldType;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CubicModernBetaWorldType extends ModernBetaWorldType implements ICubicWorldType, AdjustableCloudHeightWorldType {
    public CubicModernBetaWorldType() {
        super("modernbetacubic");
    }

    @Override
    public IntRange calculateGenerationHeightRange(WorldServer worldServer) {
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(worldServer);
        return new IntRange(settings.floor, settings.height);
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
