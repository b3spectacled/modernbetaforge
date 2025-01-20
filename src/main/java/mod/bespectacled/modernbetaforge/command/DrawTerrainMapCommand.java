package mod.bespectacled.modernbetaforge.command;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class DrawTerrainMapCommand extends DrawMapCommand {
    private static final String NAME = "drawterrainmap";
    private static final String PATH = "terrain_map.png";
    
    public DrawTerrainMapCommand() {
        super(NAME, PATH);
    }

    @Override
    public BufferedImage drawMap(WorldServer worldServer, int length, int width, Consumer<Float> progressTracker) throws IllegalStateException {
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        BiomeProvider biomeProvider = worldServer.getBiomeProvider();
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator && biomeProvider instanceof ModernBetaBiomeProvider) {
            return DrawUtil.createTerrainMap(
                ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource(),
                (ModernBetaBiomeProvider)biomeProvider,
                width,
                length,
                true,
                progressTracker
            );
        }
        
        throw new IllegalStateException();
    }
}
