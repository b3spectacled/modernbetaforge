package mod.bespectacled.modernbetaforge.world.carver;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class MapGenBetaCaveDeep extends MapGenBeta18Cave {
    public MapGenBetaCaveDeep(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource.getDefaultBlock(),
            chunkSource.getDefaultFluid(),
            BlockStates.AIR,
            settings.deepCaveWidth,
            32,
            settings.deepCaveCount,
            settings.deepCaveChance,
            settings.floor,
            settings.floor,
            settings.height
        );
    }
    
    @Override
    protected boolean isPositionForRegionUncarvable(int localX, int y, int localZ, Block block) {
        return super.isPositionForRegionUncarvable(localX, y, localZ, block) ||
            (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) && y > this.worldFloor + ModernBetaGeneratorSettings.CARVER_LAVA_LEVEL;
    }
}
