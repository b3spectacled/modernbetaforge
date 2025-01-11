package mod.bespectacled.modernbetaforge.command;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.ChunkUtil;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.IChunkGenerator;

public class DrawTerrainMapCommand extends CommandBase {
    private static final String NAME = "drawterrainmap";
    
    private int percentage = 0;

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldServer worldServer = server.getWorld(DimensionType.OVERWORLD.getId());
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        boolean success = false;
        
        if (args.length != 1) {
            throw new WrongUsageException(this.getUsage(sender), new Object[0]);
        }
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
            int width = MathHelper.clamp(CommandBase.parseInt(args[0]), 0, 5000);
            int length = MathHelper.clamp(CommandBase.parseInt(args[0]), 0, 5000);

            try { 
                File file = new File(worldServer.getSaveHandler().getWorldDirectory(), "terrain_map.png");
                file = file.getCanonicalFile(); // Fixes '/./' being inserted in path
    
                notifyCommandListener(sender, this, getLangString("start"), new Object[] { width, length });
                BufferedImage image = ChunkUtil.createTerrainMap(
                    worldServer,
                    chunkSource,
                    width,
                    length,
                    current -> setProgress(current, sender)
                );
                ImageIO.write(image, "png", file);
                
                ITextComponent textComponent = new TextComponentString(file.getName());
                textComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getPath()));
                textComponent.getStyle().setUnderlined(Boolean.valueOf(true));
                
                notifyCommandListener(sender, this, getLangString("success"), new Object[] { textComponent });
                success = true;
                
            } catch (Exception e) {
                ModernBeta.log(Level.WARN, "Couldn't save terrain map!");
                
            }
        }

        if (!success) {
            throw new WrongUsageException(getLangString("failure"), new Object[0]);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return getLangString("usage");
    }
    
    private void setProgress(float current, ICommandSender sender) {
        int percentage = (int)(current * 100.0f);
        
        if (this.percentage != percentage && percentage % 25 == 0) {
            ModernBeta.log(Level.INFO, I18n.format(getLangString("progress"), percentage));
        }
        
        this.percentage = percentage;
    }
    
    private static String getLangString(String type) {
        return String.format("commands.%s.%s.%s", ModernBeta.MODID, NAME, type);
    }
}
