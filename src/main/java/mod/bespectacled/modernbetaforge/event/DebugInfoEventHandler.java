package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.DebugUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.SkylandsChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProvider;
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
            
            addDebugText(event, "");
            
            if (chunkGenerator instanceof ModernBetaChunkGenerator) {
                ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
                ModernBetaGeneratorSettings settings = chunkSource.getGeneratorSettings();
                
                String chunkText = String.format("[Modern Beta] Chunk Source: %s", settings.chunkSource);
                String biomeText = String.format("[Modern Beta] Biome Source: %s", settings.biomeSource);
                String surfaceText = String.format("[Modern Beta] Surface Builder: %s", settings.surfaceBuilder);
                String carverText = String.format("[Modern Beta] Cave Carver: %s", settings.caveCarver);
                String spawnerText = String.format("[Modern Beta] World Spawner: %s", settings.worldSpawner);
                String seaLevelText = String.format("[Modern Beta] Sea level: %d", chunkSource.getSeaLevel());
                
                addDebugText(event, chunkText);
                addDebugText(event, biomeText);
                
                if (!(chunkSource instanceof SkylandsChunkSource || chunkSource instanceof FiniteChunkSource))
                    addDebugText(event, surfaceText);
                
                addDebugText(event, carverText);
                addDebugText(event, spawnerText);
                addDebugText(event, "");

                if (!(chunkSource instanceof FiniteChunkSource) ||
                    chunkSource instanceof FiniteChunkSource && ((FiniteChunkSource)chunkSource).hasPregenerated()
                ) {
                    String heightmapText = String.format(
                        "[Modern Beta] Surface Height: %d Ocean Height: %d Floor Height: %d",
                        chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE),
                        chunkSource.getHeight(x, z, HeightmapChunk.Type.OCEAN),
                        chunkSource.getHeight(x, z, HeightmapChunk.Type.FLOOR)
                    );
                    
                    addDebugText(event, heightmapText);
                }
                addDebugText(event, seaLevelText);
                
                if (chunkSource instanceof NoiseChunkSource) {
                    NoiseChunkSource noiseChunkSource = (NoiseChunkSource)chunkSource;
                    
                    addDebugText(event, "[Modern Beta] " + noiseChunkSource.debugNoiseSettings());
                    addDebugText(event, "[Modern Beta] " + noiseChunkSource.debugNoiseCoordinates(x, y, z));
                    addDebugText(event, "[Modern Beta] " + noiseChunkSource.debugNoiseModifiers(x, y, z));
                }
                
                if (chunkSource instanceof FiniteChunkSource) {
                    FiniteChunkSource finiteChunkSource = (FiniteChunkSource)chunkSource;
                    
                    int offsetX = finiteChunkSource.getLevelWidth() / 2;
                    int offsetZ = finiteChunkSource.getLevelLength() / 2;
                    boolean inLevel = finiteChunkSource.inWorldBounds(x, z);
                    
                    String finiteInLevelText = String.format("[Modern Beta] In Finite Level: %b", inLevel);
                    String finiteCoordsText = String.format("[Modern Beta] Finite XYZ: %d / %d / %d", x + offsetX, y, z + offsetZ);

                    addDebugText(event, finiteInLevelText);
                    addDebugText(event, finiteCoordsText);
                    
                    if (finiteChunkSource.hasPregenerated()) {
                        String finiteHeightmapText = String.format("[Modern Beta] Finite Surface Height: %d Ocean Height: %d Floor Height: %d",
                            finiteChunkSource.getLevelHeight(x + offsetX, z + offsetZ, HeightmapChunk.Type.SURFACE),
                            finiteChunkSource.getLevelHeight(x + offsetX, z + offsetZ, HeightmapChunk.Type.OCEAN),
                            finiteChunkSource.getLevelHeight(x + offsetX, z + offsetZ, HeightmapChunk.Type.FLOOR)
                        );
                        addDebugText(event, finiteHeightmapText);
                    }
                }
                
                if (chunkSource instanceof ReleaseChunkSource) {
                    ReleaseChunkSource releaseChunkSource = (ReleaseChunkSource)chunkSource;
                    String noisebiome = releaseChunkSource.getNoiseBiome(x, z).getBiomeName();
                    
                    String noiseBiomeText = String.format("[Modern Beta] Release Noise Biome: %s", noisebiome);
                    String layerTypeText = String.format("[Modern Beta] Release Layer Type: %s", settings.layerType);
                    
                    addDebugText(event, noiseBiomeText);
                    addDebugText(event, layerTypeText);
                }
            }
            
            if (biomeProvider instanceof ModernBetaBiomeProvider) {
                ModernBetaBiomeProvider modernBetaBiomeProvider = (ModernBetaBiomeProvider)biomeProvider;
                BiomeSource biomeSource = modernBetaBiomeProvider.getBiomeSource();
                
                if (biomeSource instanceof ClimateSampler) {
                    ClimateSampler climateSampler = (ClimateSampler)biomeSource;
                    
                    Clime clime = climateSampler.sample(x, z);
                    double temp = clime.temp();
                    double rain = clime.rain();
                    
                    String climateText = String.format("[Modern Beta] Climate Temp: %.3f Rainfall: %.3f", temp, rain);
                    addDebugText(event, climateText);
                }

                String baseBiomeText = String.format("[Modern Beta] Base Biome: %s", modernBetaBiomeProvider.getBaseBiome(x, z).getBiomeName());
                addDebugText(event, baseBiomeText);
                addDebugText(event, "");
            }
            
            addGenDebugText(event, DebugUtil.SECTION_GEN_CHUNK);
            addGenDebugText(event, DebugUtil.SECTION_POP_CHUNK);
            addGenDebugText(event, DebugUtil.SECTION_GET_BASE_BIOMES);
        }
    }
    
    private static void addDebugText(RenderGameOverlayEvent.Text event, String text) {
        if (ModernBetaConfig.debugOptions.displayDebugInfo) {
            event.getLeft().add(text);
        }
    }
    
    private static void addGenDebugText(RenderGameOverlayEvent.Text event, String section) {
        String averageTime = DebugUtil.getAverageTime(section);
        String iterations = DebugUtil.getIterations(section);
        
        if (!averageTime.isEmpty()) addDebugText(event, "[Modern Beta] " + averageTime);
        if (!iterations.isEmpty()) addDebugText(event, "[Modern Beta] " + iterations);
    }
}
