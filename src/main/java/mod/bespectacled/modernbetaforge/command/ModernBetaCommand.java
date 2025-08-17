package mod.bespectacled.modernbetaforge.command;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

public abstract class ModernBetaCommand extends CommandBase {
    private static final int MIN_PERMISSION_LEVEL = 4;
    
    protected final ResourceLocation name;
    
    public ModernBetaCommand(String name) {
        this.name = ModernBeta.createRegistryKey(name);
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return MIN_PERMISSION_LEVEL;
    }

    @Override
    public String getName() {
        return this.name.toString();
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return getLangString("usage");
    }
    
    protected String getLangString(String type) {
        return String.format("commands.%s.%s.%s", ModernBeta.MODID, this.name.getPath(), type);
    }
    
    protected WorldServer validateWorld(MinecraftServer server, ICommandSender sender) throws CommandException {
        WorldServer worldServer = server.getWorld(sender.getEntityWorld().provider.getDimension());

        if (!(worldServer.getChunkProvider().chunkGenerator instanceof ModernBetaChunkGenerator)) {
            throw new CommandException(this.getLangString("generator"), new Object[0]);
        }
        
        return worldServer;
    }
    
    protected void validateArgsLength(ICommandSender sender, String[] args, int... validLengths) throws CommandException {
        boolean valid = false;
        
        for (int i = 0; i < validLengths.length; ++i) {
            if (args.length == validLengths[i]) {
                valid = true;
                break;
            }
        }
        
        if (!valid) {
            throw new WrongUsageException(this.getLangString("usage"), new Object[0]);
        }
    }
}
