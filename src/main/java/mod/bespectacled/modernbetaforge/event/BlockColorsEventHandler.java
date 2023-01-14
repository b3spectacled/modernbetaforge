package mod.bespectacled.modernbetaforge.event;

import javax.annotation.Nullable;

import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.beta.BiomeBeta;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockColorsEventHandler {
    @SubscribeEvent
    public void onColorHandlerEventBlock(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(
            new IBlockColor() {
                @Override
                public int colorMultiplier(IBlockState blockState, @Nullable IBlockAccess blockAccess, @Nullable BlockPos blockPos, int tintNdx) {
                    if (blockAccess != null && blockPos != null) {
                        if (blockAccess.getBiome(blockPos) instanceof BiomeBeta && BetaColorSampler.INSTANCE.canSampleBiomeColor()) {
                            return BetaColorSampler.INSTANCE.getTallGrassColor(blockPos);
                        }
                        
                        return BiomeColorHelper.getGrassColorAtPos(blockAccess, blockPos);
                    }
                    
                    return (blockState.<BlockTallGrass.EnumType>getValue(BlockTallGrass.TYPE) == BlockTallGrass.EnumType.DEAD_BUSH) ? 16777215 : ColorizerGrass.getGrassColor(0.5, 1.0);
                }
            }, Blocks.TALLGRASS
        );
        
        event.getBlockColors().registerBlockColorHandler(
            new IBlockColor() {
                @Override
                public int colorMultiplier(IBlockState blockState, @Nullable IBlockAccess blockAccess, @Nullable BlockPos blockPos, int tintNdx) {
                    if (blockAccess != null && blockPos != null) {
                        if (blockAccess.getBiome(blockPos) instanceof ModernBetaBiome && ModernBetaConfig.visualOptions.useOldSugarCaneColor) {
                            return 16777215;
                        }
                        
                        return BiomeColorHelper.getGrassColorAtPos(blockAccess, blockPos);
                    }
                    
                    return -1;
                }
            }, Blocks.REEDS
        );
    }
}
