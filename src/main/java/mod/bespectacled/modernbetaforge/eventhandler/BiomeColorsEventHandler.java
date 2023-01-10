package mod.bespectacled.modernbetaforge.eventhandler;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeLists;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.source.BetaBiomeSource;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BiomeColorsEventHandler {
    @SubscribeEvent
    public void onWorldEventLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        
        /*
         * Climate samplers should only be set on the logical server (!isRemote) on world load,
         * but reset every time the logical client loads a world,
         * except if logical server is on the physical client (i.e. single player world).
         * 
         */
        
        if (!world.isRemote && world.provider.isSurfaceWorld()) {
            BiomeProvider biomeProvider = event.getWorld().getBiomeProvider();
            
            if (biomeProvider instanceof ModernBetaBiomeProvider) {
                ModernBetaBiomeProvider modernBetaBiomeProvider = (ModernBetaBiomeProvider)biomeProvider;
                
                // Reset climate samplers if using SingleBiomeSource
                if (modernBetaBiomeProvider.getBiomeSource() instanceof BetaBiomeSource) {
                    BetaBiomeSource betaBiomeSource = (BetaBiomeSource)modernBetaBiomeProvider.getBiomeSource();
                    
                    ModernBetaBiomeLists.setBetaClimateSamplers(betaBiomeSource, betaBiomeSource);
                } else {
                    ModernBetaBiomeLists.resetBetaClimateSamplers();
                }
            }
            
        } else if (!Minecraft.getMinecraft().isSingleplayer()) {
            ModernBetaBiomeLists.resetBetaClimateSamplers();
        }
    }
}
