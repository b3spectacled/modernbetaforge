package mod.bespectacled.modernbetaforge.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class CommandDrawMap extends ModernBetaCommand {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static final String NAME = "drawmap";
    private static final int MAX_SIZE = 5120;
    
    private int percentage = 0;
    
    public CommandDrawMap() {
        super(NAME);
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldServer worldServer = this.validateWorld(server, sender);
        this.validateArgsLength(sender, args, 2, 3);
        
        if (!server.isSinglePlayer()) {
            throw new CommandException(this.getLangString("sp"), new Object[0]);
        }
        
        boolean success = false;
        
        // Reverse for natural dimensions
        int length = MathHelper.clamp(CommandBase.parseInt(args[0]) >> 4 << 4, 0, MAX_SIZE);
        int width = MathHelper.clamp(CommandBase.parseInt(args[1]) >> 4 << 4, 0, MAX_SIZE);
        
        BlockPos center = args.length == 2 || !CommandBase.parseBoolean(args[2]) ? sender.getPosition() : BlockPos.ORIGIN;
        center = new BlockPos(center.getX() >> 4 << 4, center.getY(), center.getZ() >> 4 << 4);
    
        try {
            File dir = new File(Minecraft.getMinecraft().gameDir, "screenshots");
            dir.mkdir();
            
            File file = getTimestampedPNGFileForDirectory(dir);
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
            throw new CommandException(this.getLangString("failure"), new Object[0]);
        }
    }

    private BufferedImage drawMap(WorldServer worldServer, BlockPos center, int width, int length, Consumer<Float> progressTracker) throws IllegalStateException {
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        BiomeProvider biomeProvider = worldServer.getBiomeProvider();
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator && biomeProvider instanceof ModernBetaBiomeProvider) {
            ModernBetaChunkGenerator modernBetaChunkGenerator = (ModernBetaChunkGenerator)chunkGenerator;
            ModernBetaBiomeProvider modernBetaBiomeProvider = (ModernBetaBiomeProvider)biomeProvider;
            
            ChunkSource chunkSource = modernBetaChunkGenerator.getChunkSource();
            BiomeSource biomeSource = modernBetaBiomeProvider.getBiomeSource();
            BiomeInjectionRules injectionRules = chunkSource.createBiomeInjectionRules(biomeSource).build();
            
            SurfaceBuilder surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER
                .get(modernBetaChunkGenerator.getGeneratorSettings().surfaceBuilder)
                .apply(chunkSource, chunkSource.getGeneratorSettings());
            
            return DrawUtil.createTerrainMap(
                chunkSource,
                biomeSource,
                surfaceBuilder,
                injectionRules,
                center.getX(),
                center.getZ(),
                width,
                length,
                true,
                progressTracker,
                () -> false,
                null
            );
        }
        
        throw new IllegalStateException();
    }

    private void setProgress(float current, ICommandSender sender) {
        int percentage = (int)(current * 100.0f);
        
        if (this.percentage != percentage && percentage % 25 == 0) {
            ModernBeta.log(Level.INFO, I18n.format(getLangString("progress"), percentage));
        }
        
        this.percentage = percentage;
    }
    
    private static File getTimestampedPNGFileForDirectory(File dir) {
        String date = DATE_FORMAT.format(new Date()).toString();
        int i = 1;

        while (true) {
            File file = new File(dir, date + (i == 1 ? "" : "_" + i) + ".png");

            if (!file.exists()) {
                return file;
            }

            ++i;
        }
    }
}
