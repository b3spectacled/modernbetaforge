package mod.bespectacled.modernbetaforge.api.property;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.PropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;

public class EntityEntryProperty extends StringProperty {
    /**
     * Constructs a new EntityEntryProperty with an initial entity entry, storing the entity entry registry name.
     * 
     * @param value The initial entity entry registry name value.
     */
    public EntityEntryProperty(ResourceLocation value) {
        super(value.toString());
    }

    @Override
    public String getType() {
        return "entity";
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
        visitor.visit(this, guiIdentifier, registryKey);
    }

    @Override
    public String visitNameFormatter(PropertyVisitor visitor) {
        return visitor.visit(this);
    }
}
