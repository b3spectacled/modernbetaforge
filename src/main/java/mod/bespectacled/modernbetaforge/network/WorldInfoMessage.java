package mod.bespectacled.modernbetaforge.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WorldInfoMessage implements IMessage {
    public static final WorldInfoMessage EMPTY = new WorldInfoMessage(0L, "");
    
    private static final int BYTE_BUFFER_LEN = 2048;
    private static final String UTF8 = "UTF-8";
    
    private long seed;
    private String settings;
    
    public WorldInfoMessage() { }
    
    public WorldInfoMessage(long seed, String settings) {
        this.seed = seed;
        this.settings = settings;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.seed);
        
        try {
            byte[] bytes = this.compressBytes(this.settings.getBytes(UTF8));
            ModernBeta.log(Level.DEBUG, "Compressed size: " + bytes.length);
            buf.writeBytes(bytes);
            
        } catch (UnsupportedEncodingException e) {
            ModernBeta.log(Level.ERROR, String.format("Generator options couldn't be encoded!"));
            ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
            
        }
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.seed = buf.readLong();
        
        try {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            ModernBeta.log(Level.DEBUG, "Read size: " + bytes.length);
            this.settings = new String(this.decompressBytes(bytes), UTF8);
            
        } catch (UnsupportedEncodingException e) {
            ModernBeta.log(Level.ERROR, String.format("Generator options couldn't be decoded!"));
            ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
            
        }
    }
    
    private byte[] compressBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return new byte[0];
        }
        
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(bos)
        ) {            
            gos.write(bytes);
            gos.close();

            return bos.toByteArray();
           
        } catch (Exception e) {
            ModernBeta.log(Level.ERROR, String.format("Generator options couldn't be compressed!"));
            ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
        }
        
        return new byte[0];
    }
    
    private byte[] decompressBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return new byte[0];
        }
        
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPInputStream gis = new GZIPInputStream(bis)
        ) {
            byte[] buffer = new byte[BYTE_BUFFER_LEN];
            int len;
            
            while ((len = gis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            return bos.toByteArray();
           
       } catch (Exception e) {
            ModernBeta.log(Level.ERROR, String.format("Generator options couldn't be decompressed!"));
            ModernBeta.log(Level.ERROR, "Error: " + e.getMessage());
       }
        
        return new byte[0];
    }

    public static class WorldInfoMessageHandler implements IMessageHandler<WorldInfoMessage, IMessage> {
        @Override
        public IMessage onMessage(WorldInfoMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                BetaColorSampler.INSTANCE.resetClimateSamplers();
                
                // Assume sendClimateInfo is true, if seed is not the default 0L
                if (message.seed != 0L) {
                    ModernBetaGeneratorSettings.Factory factory = ModernBetaGeneratorSettings.Factory.jsonToFactory(message.settings);
                    
                    BiomeSource biomeSource = ModernBetaRegistries.BIOME_SOURCE
                        .get(new ResourceLocation(factory.biomeSource))
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
