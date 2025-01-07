package mod.bespectacled.modernbetaforge.api.property;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.setting.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.GuiPropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;

public class StringProperty extends Property<String> {
    /**
     * Constructs a new StringProperty with an initial string.
     * 
     * @param value The initial String value.
     */
    public StringProperty(String value) {
        super(value);
    }

    @Override
    public String getType() {
        return "string";
    }
    
    @Override
    public void visitFactory(FactoryPropertyVisitor visitor, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
        visitor.visit(this, factory, registryKey, jsonObject);
    }

    @Override
    public GuiPageButtonList.GuiListEntry visitGui(GuiPropertyVisitor visitor, int guiIdentifier) {
        return visitor.visit(this, guiIdentifier);
    }
    
    @Override
    public void visitEntryValue(EntryValuePropertyVisitor visitor, int guiIdentifier, Object value, ResourceLocation registryKey) {
        visitor.visit(this, guiIdentifier, (String)value, registryKey);
    }

    @Override
    public String getFormatter() {
        return "%s";
    }
}
