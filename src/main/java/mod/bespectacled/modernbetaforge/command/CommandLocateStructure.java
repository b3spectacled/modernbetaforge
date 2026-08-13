package mod.bespectacled.modernbetaforge.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries.StructureCreator;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class CommandLocateStructure extends CommandLocate {
    public CommandLocateStructure() {
        super("locate", CommandLocateStructure::locate);
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> structures = new ArrayList<>();
        
        for (Entry<ResourceLocation, StructureCreator> entry : ModernBetaRegistries.STRUCTURE.getEntries()) {
            structures.add(entry.getKey().getPath());
        }
        
        structures.add("endcity");
        structures.add("fortress");
        
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, structures.toArray(new String[0])) : Collections.emptyList();
    }
    
    private static BlockPos locate(MinecraftServer server, ICommandSender sender, String[] args) {
        return sender.getEntityWorld().findNearestStructure(args[0].toLowerCase(), sender.getPosition(), false);
    }
}
