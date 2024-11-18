package mod.bespectacled.modernbetaforge.network;

import io.netty.buffer.ByteBuf;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CloudHeightMessage implements IMessage {
    private int cloudHeight;
    
    public CloudHeightMessage() { }
    
    public CloudHeightMessage(int cloudHeight) {
        this.cloudHeight = cloudHeight;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.cloudHeight);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.cloudHeight = MathHelper.clamp(buf.readInt(), -256, 256);
    }
    
    public int getCloudHeight() {
        return this.cloudHeight;
    }
    
    public static class CloudHeightMessageHandler implements IMessageHandler<CloudHeightMessage, IMessage> {
        @Override
        public IMessage onMessage(CloudHeightMessage message, MessageContext ctx) {
            int cloudHeight = message.getCloudHeight();

            Minecraft.getMinecraft().addScheduledTask(() -> {
                ModernBetaWorldType.INSTANCE.setCloudHeight(cloudHeight);
            });

            return null;
        }
    }
}
