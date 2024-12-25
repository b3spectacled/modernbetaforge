package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.DebugUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DebugInfoEventHandler {
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        
        if (minecraft.isSingleplayer() && Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            IntegratedServer integratedServer = minecraft.getIntegratedServer();
            MinecraftServer minecraftServer = integratedServer.getServer();
            WorldServer worldServer = (WorldServer)minecraftServer.getEntityWorld();
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            
            // Do not render if not in Overworld
            if (player.dimension != DimensionType.OVERWORLD.getId()) {
                return;
            }
            
            IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
            BiomeProvider biomeProvider = worldServer.getBiomeProvider();

            BlockPos playerPos = player.getPosition();
            int x = playerPos.getX();
            int y = playerPos.getY();
            int z = playerPos.getZ();
            
            event.getLeft().add("");
            
            if (chunkGenerator instanceof ModernBetaChunkGenerator) {
                ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
                ModernBetaGeneratorSettings settings = chunkSource.getGeneratorSettings();
                
                String chunkKey = settings.chunkSource;
                String biomeKey = settings.biomeSource;
                String surfaceKey = settings.surfaceBuilder;
                String carverKey = settings.caveCarver;
                
                String chunkText = String.format("[Modern Beta] Chunk Source: %s", chunkKey);
                String biomeText = String.format("[Modern Beta] Biome Source: %s", biomeKey);
                String surfaceText = String.format("[Modern Beta] Surface Builder: %s", surfaceKey);
                String carverText = String.format("[Modern Beta] Cave Carver: %s", carverKey);
                String seaLevelText = String.format("[Modern Beta] Sea level: %d", chunkSource.getSeaLevel());
                
                event.getLeft().add(chunkText);
                event.getLeft().add(biomeText);
                event.getLeft().add(surfaceText);
                if (settings.useCaves) event.getLeft().add(carverText);
                event.getLeft().add("");

                if (!(chunkSource instanceof FiniteChunkSource) ||
                    chunkSource instanceof FiniteChunkSource && ((FiniteChunkSource)chunkSource).hasPregenerated()
                ) {
                    String heightmapText = String.format(
                        "[Modern Beta] Surface Height: %d Ocean Height: %d Floor Height: %d",
                        chunkSource.getHeight(worldServer, x, z, HeightmapChunk.Type.SURFACE),
                        chunkSource.getHeight(worldServer, x, z, HeightmapChunk.Type.OCEAN),
                        chunkSource.getHeight(worldServer, x, z, HeightmapChunk.Type.FLOOR)
                    );
                    
                    event.getLeft().add(heightmapText);
                }
                event.getLeft().add(seaLevelText);
                
                if (chunkSource instanceof ReleaseChunkSource) {
                    ReleaseChunkSource releaseChunkSource = (ReleaseChunkSource)chunkSource;
                    String noisebiome = releaseChunkSource.getNoiseBiome(x, z).getBiomeName();
                    
                    String noiseBiomeText = String.format("[Modern Beta] Release Noise Biome: %s", noisebiome);
                    event.getLeft().add(noiseBiomeText);
                }
                
                if (chunkSource instanceof FiniteChunkSource) {
                    FiniteChunkSource finiteChunkSource = (FiniteChunkSource)chunkSource;
                    
                    int offsetX = finiteChunkSource.getLevelWidth() / 2;
                    int offsetZ = finiteChunkSource.getLevelLength() / 2;
                    boolean inLevel = finiteChunkSource.inWorldBounds(x, z);
                    
                    String finiteInLevelText = String.format("[Modern Beta] In Finite Level: %b", inLevel);
                    String finiteCoordsText = String.format("[Modern Beta] Finite XYZ: %d / %d / %d", x + offsetX, y, z + offsetZ);

                    event.getLeft().add(finiteInLevelText);
                    event.getLeft().add(finiteCoordsText);
                    
                    if (finiteChunkSource.hasPregenerated()) {
                        String finiteHeightmapText = String.format("[Modern Beta] Finite Surface Height: %d Ocean Height: %d Floor Height: %d",
                            finiteChunkSource.getLevelHeight(x + offsetX, z + offsetZ, HeightmapChunk.Type.SURFACE),
                            finiteChunkSource.getLevelHeight(x + offsetX, z + offsetZ, HeightmapChunk.Type.OCEAN),
                            finiteChunkSource.getLevelHeight(x + offsetX, z + offsetZ, HeightmapChunk.Type.FLOOR)
                        );
                        event.getLeft().add(finiteHeightmapText);
                    }
                }
            }
            
            if (biomeProvider instanceof ModernBetaBiomeProvider) {
                BiomeSource biomeSource = ((ModernBetaBiomeProvider)biomeProvider).getBiomeSource();
                
                if (biomeSource instanceof ClimateSampler) {
                    ClimateSampler climateSampler = (ClimateSampler)biomeSource;
                    
                    Clime clime = climateSampler.sample(x, z);
                    double temp = clime.temp();
                    double rain = clime.rain();
                    
                    String climateText = String.format("[Modern Beta] Climate Temp: %.3f Rainfall: %.3f", temp, rain);
                    event.getLeft().add(climateText);
                }

                String baseBiomeText = String.format("[Modern Beta] Base Biome: %s", biomeSource.getBiome(x, z).getBiomeName());
                event.getLeft().add(baseBiomeText);
                event.getLeft().add("");
            }
            
            String averageTime = DebugUtil.getAverageTime(DebugUtil.SECTION_GEN_CHUNK);
            String iterations = DebugUtil.getIterations(DebugUtil.SECTION_GEN_CHUNK);
            
            if (!averageTime.isEmpty()) event.getLeft().add("[Modern Beta] " + averageTime);
            if (!iterations.isEmpty()) event.getLeft().add("[Modern Beta] " + iterations);

            averageTime = DebugUtil.getAverageTime(DebugUtil.SECTION_GET_BASE_BIOMES);
            iterations = DebugUtil.getIterations(DebugUtil.SECTION_GET_BASE_BIOMES);

            if (!averageTime.isEmpty()) event.getLeft().add("[Modern Beta] " + averageTime);
            if (!iterations.isEmpty()) event.getLeft().add("[Modern Beta] " + iterations);
            
            if (chunkGenerator.getClass() == ChunkGeneratorOverworld.class) {
                averageTime = DebugUtil.getAverageTime(DebugUtil.SECTION_GEN_CHUNK_VANILLA);
                iterations = DebugUtil.getIterations(DebugUtil.SECTION_GEN_CHUNK_VANILLA);
                
                if (!averageTime.isEmpty()) event.getLeft().add("[Modern Beta] " + averageTime);
                if (!iterations.isEmpty()) event.getLeft().add("[Modern Beta] " + iterations);
            }
        }
    }
}
