package mod.bespectacled.modernbetaforge.command;

import java.text.NumberFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandSetGeneratorSetting extends CommandGeneratorSetting {
    private static final String NAME = "setgeneratorsetting";

    public CommandSetGeneratorSetting() {
        super(NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        WorldServer worldServer = this.validateWorld(server, sender);
        this.validateArgsLength(sender, args, 2);
        
        WorldInfo worldInfo = worldServer.getWorldInfo();
        String generatorOptions = worldInfo.getGeneratorOptions();
        
        // Initialize new settings string if no generator options present
        if (generatorOptions.isEmpty()) {
            generatorOptions = new ModernBetaGeneratorSettings.Factory().toString();
        }
        
        JsonObject jsonObject = this.gson.fromJson(generatorOptions, JsonObject.class);
        
        ITextComponent settingText = new TextComponentString(args[0]);
        settingText.getStyle().setColor(TextFormatting.YELLOW);
        
        if (!jsonObject.has(args[0])) {
            throw new CommandException(this.getLangString("failure"), new Object[] { settingText });
        }
        
        JsonElement element = jsonObject.get(args[0]);
        
        ITextComponent prevText = new TextComponentString(element.toString());
        prevText.getStyle().setColor(TextFormatting.AQUA);
        
        ITextComponent valueText = new TextComponentString(args[1]);
        valueText.getStyle().setColor(TextFormatting.AQUA);
        
        // Check if element is a primitive (boolean, Number, or String)
        if (!element.isJsonPrimitive()) {
            throw new CommandException(this.getLangString("primitive"), new Object[] { settingText });
        }
        
        // Try parsing value
        try {
            if (element.getAsJsonPrimitive().isBoolean()) {
                jsonObject.addProperty(args[0], parseBoolean(args[1]));
            } else if (element.getAsJsonPrimitive().isNumber()) {
                jsonObject.addProperty(args[0], NumberFormat.getInstance().parse(args[1]));
            } else if (element.getAsJsonPrimitive().isString()) {
                jsonObject.addProperty(args[0], args[1]);
            }   
        } catch (Exception e) {
            throw new CommandException(this.getLangString("parse"), new Object[] { valueText, settingText });
        }
        
        // Try saving value
        try {
            ModernBetaGeneratorSettings.Factory factory = ModernBetaGeneratorSettings.Factory.jsonToFactoryChecked(jsonObject.toString());

            // Get corrected value
            jsonObject = this.gson.fromJson(factory.toString(), JsonObject.class);
            ITextComponent newValueText = new TextComponentString(jsonObject.get(args[0]).toString());
            newValueText.getStyle().setColor(TextFormatting.AQUA);
            
            // Warning text
            ITextComponent warningText = new TextComponentTranslation(this.getLangString("warn"));
            warningText.getStyle().setColor(TextFormatting.RED);
            
            worldInfo.generatorOptions = factory.toString();
            worldServer.getSaveHandler().saveWorldInfo(worldInfo);
            
            notifyCommandListener(sender, this, this.getLangString("success"), new Object[] { settingText, newValueText, prevText });
            sender.sendMessage(warningText);
            
        } catch (Exception e) {
            throw new CommandException(this.getLangString("failure"));
        }
    }
}
