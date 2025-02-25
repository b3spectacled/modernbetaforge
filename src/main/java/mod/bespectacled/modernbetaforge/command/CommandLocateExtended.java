package mod.bespectacled.modernbetaforge.command;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandLocate;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.WorldServer;

public abstract class CommandLocateExtended extends CommandLocate {
    private final ResourceLocation name;
    private final PositionLocator locator;
    
    public CommandLocateExtended(String path, PositionLocator locator) {
        this.name = ModernBeta.createRegistryKey(path);
        this.locator = locator;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldServer worldServer = server.getWorld(sender.getEntityWorld().provider.getDimension());

        if (!(worldServer.getChunkProvider().chunkGenerator instanceof ModernBetaChunkGenerator)) {
            throw new CommandException(this.getLangString("generator"), new Object[0]);
        }
        
        if (args.length != 1) {
            throw new WrongUsageException(this.getLangString("usage"), new Object[0]);
        }
        
        String name = args[0].toLowerCase();
        BlockPos blockPos = this.locator.apply(server, sender, args);

        int senderX = sender.getPosition().getX();
        int senderZ = sender.getPosition().getZ();
        
        if (blockPos != null) {
            ModernBetaChunkGenerator chunkGenerator = (ModernBetaChunkGenerator)worldServer.getChunkProvider().chunkGenerator;
            
            int x = blockPos.getX();
            int z = blockPos.getZ();
            int height = Math.max(worldServer.getHeight(x, z), chunkGenerator.getChunkSource().getHeight(x, z, Type.OCEAN)) + 1;
            
            int distance = (int)MathUtil.distance(senderX, senderZ, x, z);
            
            ITextComponent textPosition = new TextComponentString(String.format("[%d, %d, %d]", x, height, z));
            textPosition.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/tp @s %d %d %d", x, height, z)));
            textPosition.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation(this.getLangString("hover"))));
            textPosition.getStyle().setColor(TextFormatting.GREEN);
            
            ITextComponent textDistance = new TextComponentString("(");
            textDistance.appendSibling(new TextComponentTranslation(this.getLangString("distance"), distance));
            textDistance.appendText(")");
            
            sender.sendMessage(new TextComponentTranslation(this.getLangString("success"), new Object[] { name, textPosition, textDistance }));
            
        } else {
            throw new CommandException(this.getLangString("failure"), new Object[] { name });
            
        }
    }

    @Override
    public String getName() {
        return this.name.toString();
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return getLangString("usage");
    }
    
    private String getLangString(String type) {
        return String.format("commands.%s.%s.%s", ModernBeta.MODID, this.name.getPath(), type);
    }
    
    @FunctionalInterface
    public static interface PositionLocator {
        BlockPos apply(MinecraftServer server, ICommandSender sender, String[] args);
    }
}
