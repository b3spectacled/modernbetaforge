package mod.bespectacled.modernbetaforge.network;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.network.CloudHeightMessage.CloudHeightMessageHandler;
import mod.bespectacled.modernbetaforge.network.WorldInfoMessage.WorldInfoMessageHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModernBetaPacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModernBeta.MODID);
    
    public static void register() {
        INSTANCE.registerMessage(CloudHeightMessageHandler.class, CloudHeightMessage.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(WorldInfoMessageHandler.class, WorldInfoMessage.class, 1, Side.CLIENT);
    }
}
