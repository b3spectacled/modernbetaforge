package mod.bespectacled.modernbetaforge.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;

public abstract class DrawMapCommand extends CommandBase {
    private final String name;
    private final String path;
    
    private int percentage = 0;
    
    public DrawMapCommand(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    public abstract BufferedImage drawMap(WorldServer worldServer, BlockPos center, int width, int length, Consumer<Float> progressTracker) throws IllegalStateException;

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldServer worldServer = server.getWorld(DimensionType.OVERWORLD.getId());
        boolean success = false;
        
        if (args.length != 3) {
            throw new WrongUsageException(this.getUsage(sender), new Object[0]);
        }
        
        int width = MathHelper.clamp(CommandBase.parseInt(args[0]), 0, 5120);
        int length = MathHelper.clamp(CommandBase.parseInt(args[1]), 0, 5120);
        BlockPos center = CommandBase.parseBoolean(args[2]) ? new BlockPos(0, 0, 0) : sender.getPosition();

        try { 
            File file = new File(worldServer.getSaveHandler().getWorldDirectory(), this.path);
            file = file.getCanonicalFile(); // Fixes '/./' being inserted in path

            notifyCommandListener(sender, this, this.getLangString("start"), new Object[] { width, length, center.getX(), center.getZ() });
            BufferedImage image = this.drawMap(worldServer, center, length, width, current -> setProgress(current, sender));
            ImageIO.write(image, "png", file);
            
            ITextComponent textComponent = new TextComponentString(file.getName());
            textComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getPath()));
            textComponent.getStyle().setUnderlined(Boolean.valueOf(true));
            
            notifyCommandListener(sender, this, this.getLangString("success"), new Object[] { textComponent });
            success = true;
            
        } catch (Exception e) {
            ModernBeta.log(Level.WARN, String.format("Command '%s' failed!", this.name));
            
        }

        if (!success) {
            throw new WrongUsageException(this.getLangString("failure"), new Object[0]);
        }
    }

    @Override
    public String getName() {
        return this.name;
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
    
    private String getLangString(String type) {
        return String.format("commands.%s.%s.%s", ModernBeta.MODID, this.name, type);
    }
}
