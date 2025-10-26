package mod.bespectacled.modernbetaforge.api.property;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPropertyScreen;
import mod.bespectacled.modernbetaforge.api.property.ScreenProperty.PropertyScreenCreator;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeWorld;
import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.PropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings.Factory;
import net.minecraft.client.gui.GuiPageButtonList.GuiListEntry;
import net.minecraft.util.ResourceLocation;

public class ScreenProperty extends Property<PropertyScreenCreator> {
    /**
     * Constructs a new ScreenProperty.
     * 
     * @param value The screen creator.
     */
    public ScreenProperty(PropertyScreenCreator value) {
        super(value);
    }

    @Override
    public String getType() {
        return "screen";
    }

    @Override
    public void visitFactory(FactoryPropertyVisitor visitor, Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
        visitor.visit(this, factory, registryKey, jsonObject);
    }

    @Override
    public GuiListEntry visitGui(GuiPropertyVisitor visitor, int guiIdentifier) {
        return visitor.visit(this, guiIdentifier);
    }

    @Override
    public void visitEntryValue(EntryValuePropertyVisitor visitor, int guiIdentifier, Object value, ResourceLocation registryKey) {
        visitor.visit(this, guiIdentifier, registryKey);
    }

    @Override
    public String visitNameFormatter(PropertyVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getFormatter() {
        return "%s";
    }

    @FunctionalInterface
    public static interface PropertyScreenCreator {
        GuiPropertyScreen apply(GuiScreenCustomizeWorld parent, ResourceLocation registryKey);
    }
}
