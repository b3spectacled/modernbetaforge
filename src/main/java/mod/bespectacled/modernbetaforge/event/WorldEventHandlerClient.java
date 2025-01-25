package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandlerClient {
    /*
     * Set Climate Samplers
     */
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
