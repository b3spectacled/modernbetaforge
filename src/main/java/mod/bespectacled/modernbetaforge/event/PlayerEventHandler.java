package mod.bespectacled.modernbetaforge.event;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.network.CloudHeightMessage;
import mod.bespectacled.modernbetaforge.network.ModernBetaPacketHandler;
import mod.bespectacled.modernbetaforge.network.WorldInfoMessage;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProvider;
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
        BiomeProvider biomeProvider = worldServer.getBiomeProvider();
        
        if (!(worldServer.getWorldType() instanceof ModernBetaWorldType)) {
            ModernBeta.log(Level.DEBUG, "Not a Modern Beta world..");
            return;
        }
        
        if (!isSinglePlayer) {
            if (biomeProvider instanceof ModernBetaBiomeProvider) {
                // Sanity check generator settings string length
                WorldInfoMessage message = ModernBetaConfig.serverOptions.sendWorldInfo ?
                    new WorldInfoMessage(
                        worldServer.getSeed(),
                        worldServer.getWorldInfo().getGeneratorOptions().length() < ModernBetaGeneratorSettings.MAX_PRESET_LENGTH ?
                            worldServer.getWorldInfo().getGeneratorOptions() :
                            ""
                    ) : WorldInfoMessage.EMPTY;
                
                ModernBetaPacketHandler.INSTANCE.sendTo(message, player);
            }
            
            if (ModernBetaConfig.serverOptions.sendCloudHeight) {
                int cloudHeight = (int)ModernBetaWorldType.INSTANCE.getCloudHeight();
                
                ModernBetaPacketHandler.INSTANCE.sendTo(new CloudHeightMessage(cloudHeight), player);
            }
            
        } else {
            if (biomeProvider instanceof ModernBetaBiomeProvider) {
                BiomeSource biomeSource = ((ModernBetaBiomeProvider)worldServer.getBiomeProvider()).getBiomeSource();
                ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(worldServer);
                BetaColorSampler.INSTANCE.resetClimateSamplers();

                if (biomeSource instanceof ClimateSampler && ModernBetaConfig.visualOptions.useBetaBiomeColors) {
                    BetaColorSampler.INSTANCE.setClimateSampler((ClimateSampler)biomeSource, settings.snowLineOffset);
                }
                
                if (biomeSource instanceof SkyClimateSampler && ModernBetaConfig.visualOptions.useBetaSkyColors) {
                    BetaColorSampler.INSTANCE.setSkyClimateSampler((SkyClimateSampler)biomeSource);
                }
            }
        }
    }
}
