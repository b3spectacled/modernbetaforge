package mod.bespectacled.modernbetaforge.eventhandler;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeLists;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.source.BetaBiomeSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BiomeColorsEventHandler {
    @SubscribeEvent
    public void onWorldEventLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        
        if (!world.isRemote) {
            BiomeProvider biomeProvider = event.getWorld().getBiomeProvider();
            
            if (biomeProvider instanceof ModernBetaBiomeProvider) {
                ModernBetaBiomeProvider modernBetaBiomeProvider = (ModernBetaBiomeProvider)biomeProvider;
                
                if (modernBetaBiomeProvider.getBiomeSource() instanceof BetaBiomeSource) {
                    BetaBiomeSource betaBiomeSource = new BetaBiomeSource(world.getWorldInfo());
                    
                    ModernBetaBiomeLists.setBetaClimateSamplers(betaBiomeSource, betaBiomeSource);
                } else {
                    ModernBetaBiomeLists.resetBetaClimateSamplers();
                }
            }
        }
    }
}
