package mod.bespectacled.modernbetaforge.network;

import io.netty.buffer.ByteBuf;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClimateInfoMessage implements IMessage {
    public static final ClimateInfoMessage EMPTY = new ClimateInfoMessage("", 0L, 0.0f, 0.0f, 0.0f);
    
    private String biomeSource;
    private long seed;
    private float tempNoiseScale;
    private float rainNoiseScale;
    private float detailNoiseScale;
    
    public ClimateInfoMessage() { }
    
    public ClimateInfoMessage(String biomeSource, long seed, float tempNoiseScale, float rainNoiseScale, float detailNoiseScale) {
        this.biomeSource = biomeSource;
        this.seed = seed;
        this.tempNoiseScale = tempNoiseScale;
        this.rainNoiseScale = rainNoiseScale;
        this.detailNoiseScale = detailNoiseScale;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.biomeSource);
        buf.writeLong(this.seed);
        buf.writeFloat(this.tempNoiseScale);
        buf.writeFloat(this.rainNoiseScale);
        buf.writeFloat(this.detailNoiseScale);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.biomeSource = ByteBufUtils.readUTF8String(buf);
        this.seed = buf.readLong();
        this.tempNoiseScale = buf.readFloat();
        this.rainNoiseScale = buf.readFloat();
        this.detailNoiseScale = buf.readFloat();
    }

    public static class ClimateInfoMessageHandler implements IMessageHandler<ClimateInfoMessage, IMessage> {
        @Override
        public IMessage onMessage(ClimateInfoMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                BetaColorSampler.INSTANCE.resetClimateSamplers();
                
                // Assume sendClimateInfo is true, if seed is not the default 0L
                if (message.seed != 0L) {
                    ModernBetaGeneratorSettings.Factory factory = new ModernBetaGeneratorSettings.Factory();
                    factory.biomeSource = message.biomeSource;
                    factory.tempNoiseScale = message.tempNoiseScale;
                    factory.rainNoiseScale = message.rainNoiseScale;
                    factory.detailNoiseScale = message.detailNoiseScale;
                    
                    BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE
                        .get(new ResourceLocation(message.biomeSource))
                        .apply(message.seed, factory.build());

                    if (biomeSource instanceof ClimateSampler && ModernBetaConfig.visualOptions.useBetaBiomeColors) {
                        BetaColorSampler.INSTANCE.setClimateSampler((ClimateSampler)biomeSource);
                    }
                    
                    if (biomeSource instanceof SkyClimateSampler && ModernBetaConfig.visualOptions.useBetaSkyColors) {
                        BetaColorSampler.INSTANCE.setSkyClimateSampler((SkyClimateSampler)biomeSource);
                    }
                }
            });

            return null;
        }
    }
}
