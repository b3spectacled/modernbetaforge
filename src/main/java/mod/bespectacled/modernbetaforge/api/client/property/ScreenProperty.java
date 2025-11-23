package mod.bespectacled.modernbetaforge.api.client.property;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPropertyScreen;
import mod.bespectacled.modernbetaforge.api.client.property.ScreenProperty.PropertyScreenCreator;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeWorld;
import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.PropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList.GuiListEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScreenProperty extends GuiProperty<PropertyScreenCreator> {
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
