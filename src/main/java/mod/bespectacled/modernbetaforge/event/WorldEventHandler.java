package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorWorldServer;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandler {
    /*
     * Modify player spawning algorithm
     */
    
    @SubscribeEvent
    public void onCreateSpawnPosition(WorldEvent.CreateSpawnPosition event) {
        WorldServer world = (WorldServer)event.getWorld();
        WorldSettings settings = event.getSettings();
        
        IChunkGenerator chunkGenerator = world.getChunkProvider().chunkGenerator;
        BiomeProvider biomeProvider = world.getBiomeProvider();
        
        if (!(settings.getTerrainType() instanceof ModernBetaWorldType))
            return;
        
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
                
                if (settings.isBonusChestEnabled()) {
                    AccessorWorldServer accessor = (AccessorWorldServer)world;
                    
                    accessor.invokeCreateBonusChest();
                }
                
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void onWorldEventLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        
        boolean isRemote = world.isRemote;
        boolean isSinglePlayer = Minecraft.getMinecraft().isSingleplayer();
        
        boolean isModernBeta = world.getWorldInfo().getTerrainType() instanceof ModernBetaWorldType;
        boolean isOverworld = world.provider.isSurfaceWorld();
        
        /*
         * Climate samplers should only be set on the logical server (!isRemote) on world load,
         * but reset every time the logical client loads a world,
         * except if logical server is on the physical client (i.e. single player world).
         * Climate samplers should be set once for all dimensions loaded in a save file.
         * 
         * isRemote --> checks if logical server
         * isSinglePlayer --> checks if physical client (logical client + logical server) 
         * 
         */
        
        // Logical server on physical client (SP),
        // filter for Modern Beta world type / save files
        if (!isRemote && isSinglePlayer && isModernBeta) {
            
            // Filter for overworld dimension and whether Modern Beta biome provider is used
            if (isOverworld && world.getBiomeProvider() instanceof ModernBetaBiomeProvider) {
                ModernBetaBiomeProvider biomeProvider = (ModernBetaBiomeProvider)world.getBiomeProvider();
                BetaColorSampler.INSTANCE.resetClimateSamplers();
                
                if (biomeProvider.getBiomeSource() instanceof ClimateSampler)
                    BetaColorSampler.INSTANCE.setClimateSampler((ClimateSampler)biomeProvider.getBiomeSource());  
                
                if (biomeProvider.getBiomeSource() instanceof SkyClimateSampler)
                    BetaColorSampler.INSTANCE.setSkyClimateSampler((SkyClimateSampler)biomeProvider.getBiomeSource());
            }
            
            // Return early so we do not reset climate samplers
            // when checking other dimensions in a Modern Beta save file
            return;
        }

        // Logical client on physical client (SP)
        if (isRemote && isSinglePlayer) {
            return;
        }
        
        BetaColorSampler.INSTANCE.resetClimateSamplers();
    }
}
