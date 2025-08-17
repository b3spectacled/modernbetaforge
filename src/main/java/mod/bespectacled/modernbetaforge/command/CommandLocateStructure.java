package mod.bespectacled.modernbetaforge.command;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandLocateStructure extends CommandLocate {
    public CommandLocateStructure() {
        super("locate", CommandLocateStructure::locate);
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        String[] structures = new String[] {
            "Stronghold",
            "Monument",
            "Village",
            "Mansion",
            "EndCity",
            "Fortress",
            "Temple",
            "Mineshaft"
        };
        
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, structures) : Collections.emptyList();
    }
    
    private static BlockPos locate(MinecraftServer server, ICommandSender sender, String[] args) {
        return sender.getEntityWorld().findNearestStructure(args[0].toLowerCase(), sender.getPosition(), false);
    }
}
