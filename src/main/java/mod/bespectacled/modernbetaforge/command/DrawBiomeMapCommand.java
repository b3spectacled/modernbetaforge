package mod.bespectacled.modernbetaforge.command;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.WorldServer;

public class DrawBiomeMapCommand extends DrawMapCommand {
    private static final String NAME = "drawbiomemap";
    private static final String PATH = "biome_map.png";
    
    public DrawBiomeMapCommand() {
        super(NAME, PATH);
    }
    
    @Override
    public BufferedImage drawMap(WorldServer worldServer, int length, int width, Consumer<Float> progressTracker) throws IllegalStateException {
        if (worldServer.getBiomeProvider() instanceof ModernBetaBiomeProvider) {
            ModernBetaBiomeProvider biomeProvider = (ModernBetaBiomeProvider)worldServer.getBiomeProvider();
            MutableBlockPos mutablePos = new MutableBlockPos();
            
            return DrawUtil.createBiomeMap(
                (x, z) -> biomeProvider.getBiome(mutablePos.setPos(x, 0, z)),
                width,
                length,
                true,
                progressTracker
            );
        }
        
        throw new IllegalStateException();
    }
}
