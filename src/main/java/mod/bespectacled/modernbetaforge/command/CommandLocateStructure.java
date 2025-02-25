package mod.bespectacled.modernbetaforge.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandLocateStructure extends CommandLocateExtended {
    public CommandLocateStructure() {
        super("locate", CommandLocateStructure::locate);
    }
    
    private static BlockPos locate(MinecraftServer server, ICommandSender sender, String[] args) {
        return sender.getEntityWorld().findNearestStructure(args[0].toLowerCase(), sender.getPosition(), false);
    }
}
