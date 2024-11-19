package mod.bespectacled.modernbetaforge.event;

import mod.bespectacled.modernbetaforge.network.CloudHeightMessage;
import mod.bespectacled.modernbetaforge.network.ModernBetaPacketHandler;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerEventHandler {
    @SubscribeEvent
    public void onPlayerEventPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayerMP player = (EntityPlayerMP)event.player;
        int cloudHeight = (int)ModernBetaWorldType.INSTANCE.getCloudHeight();
        
        ModernBetaPacketHandler.INSTANCE.sendTo(new CloudHeightMessage(cloudHeight), player);
    }
}
