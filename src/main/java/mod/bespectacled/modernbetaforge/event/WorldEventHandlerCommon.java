package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorWorldServer;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
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
            boolean useOldSpawns = ModernBetaConfig.spawnOptions.useOldSpawns;
            
            if (chunkGenerator instanceof ModernBetaChunkGenerator && biomeProvider instanceof ModernBetaBiomeProvider) {
                ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
                BiomeSource biomeSource = ((ModernBetaBiomeProvider)biomeProvider).getBiomeSource();
                
                BlockPos newSpawnPos = useOldSpawns ?
                    chunkSource.getSpawnLocator().locateSpawn(currentSpawnPos, chunkSource, biomeSource) :
                    null;
                
                if (newSpawnPos != null) {
                    world.getWorldInfo().setSpawn(newSpawnPos);
                    
                    if (chunkSource instanceof FiniteChunkSource) {
                        ((FiniteChunkSource)chunkSource).buildHouse(world, newSpawnPos);
                    }

                    if (settings.isBonusChestEnabled()) {
                        AccessorWorldServer accessor = (AccessorWorldServer)world;
                        
                        accessor.invokeCreateBonusChest();
                    }
                    
                    event.setCanceled(true);
                }
            }
        }
    }
}
