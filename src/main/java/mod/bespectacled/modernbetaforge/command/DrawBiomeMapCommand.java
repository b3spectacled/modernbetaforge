package mod.bespectacled.modernbetaforge.command;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class DrawBiomeMapCommand extends DrawMapCommand {
    private static final String NAME = "drawbiomemap";
    private static final String PATH = "biome_map.png";
    
    public DrawBiomeMapCommand() {
        super(NAME, PATH);
    }
    
    @Override
    public BufferedImage drawMap(WorldServer worldServer, BlockPos center, int width, int length, Consumer<Float> progressTracker) throws IllegalStateException {
        if (worldServer.getBiomeProvider() instanceof ModernBetaBiomeProvider) {
            ModernBetaBiomeProvider biomeProvider = (ModernBetaBiomeProvider)worldServer.getBiomeProvider();
            
            return DrawUtil.createBiomeMap(
                biomeProvider,
                center,
                worldServer.getSeaLevel(),
                width,
                length,
                false,
                progressTracker
            );
        }
        
        throw new IllegalStateException();
    }
}
