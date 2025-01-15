package mod.bespectacled.modernbetaforge.command;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.world.WorldServer;
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
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
            
            return DrawUtil.createTerrainMap(
                chunkSource,
                worldServer.getBiomeProvider(),
                width,
                length,
                true,
                progressTracker
            );
        }
        
        throw new IllegalStateException();
    }
}
