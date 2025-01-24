package mod.bespectacled.modernbetaforge.command;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
    public BufferedImage drawMap(WorldServer worldServer, BlockPos center, int width, int length, Consumer<Float> progressTracker) throws IllegalStateException {
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        BiomeProvider biomeProvider = worldServer.getBiomeProvider();
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator && biomeProvider instanceof ModernBetaBiomeProvider) {
            ModernBetaChunkGenerator modernBetaChunkGenerator = (ModernBetaChunkGenerator)chunkGenerator;
            ChunkSource chunkSource = modernBetaChunkGenerator.getChunkSource();
            
            ResourceLocation surfaceBuilderKey = new ResourceLocation(modernBetaChunkGenerator.getGeneratorSettings().surfaceBuilder);
            SurfaceBuilder surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER.get(surfaceBuilderKey).apply(chunkSource, chunkSource.getGeneratorSettings());
            
            return DrawUtil.createTerrainMap(
                ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource(),
                (ModernBetaBiomeProvider)biomeProvider,
                surfaceBuilder,
                center,
                width,
                length,
                false,
                progressTracker
            );
        }
        
        throw new IllegalStateException();
    }
}
