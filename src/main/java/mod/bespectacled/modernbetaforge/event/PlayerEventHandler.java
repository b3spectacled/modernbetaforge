package mod.bespectacled.modernbetaforge.event;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.network.ClimateInfoMessage;
import mod.bespectacled.modernbetaforge.network.CloudHeightMessage;
import mod.bespectacled.modernbetaforge.network.ModernBetaPacketHandler;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerEventHandler {
    @SubscribeEvent
    public void onPlayerEventPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayerMP player = (EntityPlayerMP)event.player;
        
        ModernBeta.log(Level.DEBUG, "Firing PlayerLoggedInEvent..");
        ModernBeta.log(Level.DEBUG, "Is single player: " + player.server.isSinglePlayer());
        this.sendMessages(player);
    }
    
    @SubscribeEvent
    public void onPlayerEventPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ModernBeta.log(Level.DEBUG, "Firing PlayerChangedDimensionEvent..");
        this.sendMessages((EntityPlayerMP)event.player);
    }
    
    private void sendMessages(EntityPlayerMP player) {
        boolean isSinglePlayer = player.server.isSinglePlayer();
        WorldServer worldServer = player.getServerWorld();
        
        if (!(worldServer.getWorldType() instanceof ModernBetaWorldType)) {
            ModernBeta.log(Level.DEBUG, "Not a Modern Beta world..");
            return;
        }
        
        if (!isSinglePlayer) {
            ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(worldServer);
            
            ModernBetaPacketHandler.INSTANCE.sendTo(
                !ModernBetaConfig.serverOptions.sendClimateInfo ?
                    ClimateInfoMessage.EMPTY :
                    new ClimateInfoMessage(
                        settings.biomeSource.toString(),
                        worldServer.getSeed(),
                        settings.tempNoiseScale,
                        settings.rainNoiseScale,
                        settings.detailNoiseScale
                    ),
                player
            );
            
            if (ModernBetaConfig.serverOptions.sendCloudHeight) {
                int cloudHeight = (int)ModernBetaWorldType.INSTANCE.getCloudHeight();
                
                ModernBetaPacketHandler.INSTANCE.sendTo(new CloudHeightMessage(cloudHeight), player);
            }
            
        } else {
            ModernBetaBiomeProvider biomeProvider = (ModernBetaBiomeProvider)worldServer.getBiomeProvider();
            BiomeSource biomeSource = biomeProvider.getBiomeSource();
            
            BetaColorSampler.INSTANCE.resetClimateSamplers();

            if (biomeSource instanceof ClimateSampler && ModernBetaConfig.visualOptions.useBetaBiomeColors) {
                BetaColorSampler.INSTANCE.setClimateSampler((ClimateSampler)biomeSource);
            }
            
            if (biomeSource instanceof SkyClimateSampler && ModernBetaConfig.visualOptions.useBetaSkyColors) {
                BetaColorSampler.INSTANCE.setSkyClimateSampler((SkyClimateSampler)biomeSource);
            }
            
        }
    }
}
