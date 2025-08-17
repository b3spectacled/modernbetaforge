package mod.bespectacled.modernbetaforge.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandGetGeneratorSetting extends CommandGeneratorSetting {
    private static final String NAME = "getgeneratorsetting";

    public CommandGetGeneratorSetting() {
        super(NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldServer worldServer = this.validateWorld(server, sender);
        this.validateArgsLength(sender, args, 1);
        
        WorldInfo worldInfo = worldServer.getWorldInfo();
        JsonObject jsonObject = this.Gson.fromJson(worldInfo.getGeneratorOptions(), JsonObject.class);
        
        ITextComponent settingText = new TextComponentString(args[0]);
        settingText.getStyle().setColor(TextFormatting.YELLOW);
        
        if (!jsonObject.has(args[0])) {
            throw new CommandException(this.getLangString("failure"), new Object[] { settingText });
        }
        
        JsonElement element = jsonObject.get(args[0]);
        
        String type = "unknown";
        if (element.getAsJsonPrimitive().isBoolean()) {
            type = "boolean";
        } else if (element.getAsJsonPrimitive().isNumber()) {
            type = "Number";
        } else if (element.getAsJsonPrimitive().isString()) {
            type = "String";
        } 
        
        ITextComponent valueText = new TextComponentString(element.toString());
        valueText.getStyle().setColor(TextFormatting.AQUA);

        notifyCommandListener(sender, this, this.getLangString("success"), new Object[] { settingText, type, valueText });
    }
}
