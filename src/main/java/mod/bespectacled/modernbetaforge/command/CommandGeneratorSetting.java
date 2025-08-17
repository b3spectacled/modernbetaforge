package mod.bespectacled.modernbetaforge.command;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public abstract class CommandGeneratorSetting extends ModernBetaCommand {
    protected final Gson gson = new Gson();

    public CommandGeneratorSetting(String name) {
        super(name);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        JsonObject jsonObject = this.gson.fromJson(new ModernBetaGeneratorSettings.Factory().toString(), JsonObject.class);
        String[] keys = jsonObject.entrySet().stream().map(e -> e.getKey()).toArray(String[]::new);
        
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, keys) : Collections.emptyList();
    }
}
