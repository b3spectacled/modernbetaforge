package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
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
            
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            WorldServer worldServer = (WorldServer)minecraftServer.getEntityWorld();
            
            // Do not render if not in Overworld
            if (player.dimension != DimensionType.OVERWORLD.getId()) {
                return;
            }
            
            IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
            BiomeProvider biomeProvider = worldServer.getBiomeProvider();

            BlockPos playerPos = player.getPosition();
            int x = playerPos.getX();
            int z = playerPos.getZ();
            
            event.getLeft().add("");
            
            if (chunkGenerator instanceof ModernBetaChunkGenerator) {
                ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
                
                String chunkSourceName = chunkSource.getChunkGeneratorSettings().chunkSource;
                String biomeSourceName = chunkSource.getChunkGeneratorSettings().biomeSource;
                String fixedBiome = chunkSource.getChunkGeneratorSettings().fixedBiome;
                
                String sourceText = String.format("[Modern Beta] Chunk source: %s Biome source: %s", chunkSourceName, biomeSourceName);
                String fixedBiomeText = String.format("[Modern Beta] Fixed biome: %s", fixedBiome);
                
                String heightmapText = String.format(
                    "[Modern Beta] Surface Height: %d Ocean Height: %d Floor Height: %d",
                    chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE),
                    chunkSource.getHeight(x, z, HeightmapChunk.Type.OCEAN),
                    chunkSource.getHeight(x, z, HeightmapChunk.Type.FLOOR)
                );
                String seaLevelText = String.format("[Modern Beta] Sea level: %d", chunkSource.getSeaLevel());
                
                event.getLeft().add(sourceText);
                
                if (biomeSourceName.equals(ModernBetaBuiltInTypes.Biome.SINGLE.id))
                    event.getLeft().add(fixedBiomeText);
                
                event.getLeft().add(heightmapText);
                event.getLeft().add(seaLevelText);
            }
            
            if (biomeProvider instanceof ModernBetaBiomeProvider) {
                BiomeSource biomeSource = ((ModernBetaBiomeProvider)biomeProvider).getBiomeSource();
                
                if (biomeSource instanceof ClimateSampler) {
                    ClimateSampler climateSampler = (ClimateSampler)biomeSource;
                    
                    Clime clime = climateSampler.sample(x, z);
                    double temp = clime.temp();
                    double rain = clime.rain();
                    
                    String climateText = String.format("[Modern Beta] Climate Temp: %.3f Rainfall: %.3f", temp, rain);
                    String baseBiomeText = String.format("[Modern Beta] Base Biome: %s", biomeSource.getBiome(x, z).getBiomeName());

                    event.getLeft().add(climateText);
                    event.getLeft().add(baseBiomeText);
                }
            }
        }
        
    }
}
