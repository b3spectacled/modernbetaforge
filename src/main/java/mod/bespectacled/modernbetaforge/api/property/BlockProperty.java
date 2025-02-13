package mod.bespectacled.modernbetaforge.api.property;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeRegistry;
import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.PropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;

public final class BlockProperty extends RegistryProperty {
    /**
     * Constructs a new BlockProperty with an initial block, storing the block registry name, with default predicate
     * not filtering any Forge registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial block registry name value.
     */
    public BlockProperty(ResourceLocation value) {
        this(value, key -> true);
    }
    
    /**
     * Constructs a new BlockProperty with an initial block, storing the block registry name, and a predicate used
     * to filter Forge registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial block registry name value.
     * @param filter The predicate used to filter the Forge Registry collection values.
     */
    public BlockProperty(ResourceLocation value, Predicate<ResourceLocation> filter) {
        super(value, filter);
    }

    @Override
    public String getType() {
        return "block";
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
