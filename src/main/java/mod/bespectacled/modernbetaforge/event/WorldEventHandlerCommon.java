package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevHouse;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandlerCommon {
    /*
     * Modify player spawning algorithm
     */
    @SubscribeEvent
    public void onWorldEventCreateSpawnPosition(WorldEvent.CreateSpawnPosition event) {
        WorldServer world = (WorldServer)event.getWorld();
        WorldSettings settings = event.getSettings();
        
        IChunkGenerator chunkGenerator = world.getChunkProvider().chunkGenerator;
        BiomeProvider biomeProvider = world.getBiomeProvider();
        
        if ((settings.getTerrainType() instanceof ModernBetaWorldType)) {
            BlockPos currentSpawnPos = world.getSpawnPoint();
            
            if (chunkGenerator instanceof ModernBetaChunkGenerator && biomeProvider instanceof ModernBetaBiomeProvider) {
                ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
                BiomeSource biomeSource = ((ModernBetaBiomeProvider)biomeProvider).getBiomeSource();
                ModernBetaGeneratorSettings generatorSettings = chunkSource.getGeneratorSettings();
                
                BlockPos newSpawnPos = chunkSource.getSpawnLocator().locateSpawn(currentSpawnPos, chunkSource, biomeSource);

                if (newSpawnPos != null) {
                    world.getWorldInfo().setSpawn(newSpawnPos);
                    
                    if (chunkSource instanceof FiniteChunkSource) {
                        ((FiniteChunkSource)chunkSource).buildHouse(world, newSpawnPos, settings.isBonusChestEnabled());
                    }

                    if (settings.isBonusChestEnabled()) {
                        // Handle bonus chest placement in Indev house generation method
                        if (!(chunkSource instanceof FiniteChunkSource && IndevHouse.fromId(generatorSettings.levelHouse) != IndevHouse.NONE)) {
                            world.createBonusChest();
                        }
                    }
                    
                    event.setCanceled(true);
                }
            }
        }
    }
}
