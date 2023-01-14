package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.source.BetaBiomeSource;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaWorldType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BiomeColorsEventHandler {
    @SubscribeEvent
    public void onWorldEventLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        
        boolean isRemote = world.isRemote;
        boolean isSinglePlayer = Minecraft.getMinecraft().isSingleplayer();
        boolean isModernBetaWorldType = world.getWorldInfo().getTerrainType() instanceof ModernBetaWorldType;
        
        /*
         * Climate samplers should only be set on the logical server (!isRemote) on world load,
         * but reset every time the logical client loads a world,
         * except if logical server is on the physical client (i.e. single player world).
         * 
         * isRemote --> checks if logical server
         * isSinglePlayer --> checks if physical client (logical client + logical server) 
         * 
         */
        
        // Logical server on physical client (SP),
        // filter out non-Modern Beta world types
        if (!isRemote && isSinglePlayer && isModernBetaWorldType) {
            
            // Filter out other dimensions
            if (world.getBiomeProvider() instanceof ModernBetaBiomeProvider) {
                ModernBetaBiomeProvider biomeProvider = (ModernBetaBiomeProvider)world.getBiomeProvider();
                
                // Filter out non-Beta biome sources
                if (biomeProvider.getBiomeSource() instanceof BetaBiomeSource) {
                    ModernBetaBiomeProvider modernBetaBiomeProvider = (ModernBetaBiomeProvider)world.getBiomeProvider();
                    BetaBiomeSource betaBiomeSource = (BetaBiomeSource)modernBetaBiomeProvider.getBiomeSource();
                    
                    BetaColorSampler.INSTANCE.setClimateSamplers(betaBiomeSource, betaBiomeSource);
                } else {
                    BetaColorSampler.INSTANCE.resetClimateSamplers();
                }
            }
            
            return;
        }

        // Logical client on physical client (SP)
        if (isRemote && isSinglePlayer) {
            return;
        }
        
        BetaColorSampler.INSTANCE.resetClimateSamplers();
    }
}
