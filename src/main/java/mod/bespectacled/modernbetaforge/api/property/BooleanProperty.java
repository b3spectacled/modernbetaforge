package mod.bespectacled.modernbetaforge.api.property;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.PropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;

public final class BooleanProperty extends Property<Boolean> {
    /**
     * Constructs a new BooleanProperty.
     * 
     * @param value The initial boolean value.
     */
    public BooleanProperty(boolean value) {
        super(value);
    }
    
    @Override
    public String getType() {
        return "boolean";
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
        visitor.visit(this, guiIdentifier, (Boolean)value, registryKey);
    }

    @Override
    public String visitNameFormatter(PropertyVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getFormatter() {
        return "%b";
    }
}
