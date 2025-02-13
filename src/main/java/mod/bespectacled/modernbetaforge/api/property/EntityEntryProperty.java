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
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class EntityEntryProperty extends RegistryProperty {
    /**
     * Constructs a new EntityEntryProperty with an initial entity entry, storing the entity entry registry name,
     * with default predicate not filtering any Forge registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial entity entry registry name value.
     */
    public EntityEntryProperty(ResourceLocation value) {
        this(value, key -> EntityLiving.class.isAssignableFrom(ForgeRegistries.ENTITIES.getValue(key).getEntityClass()));
    }
    
    /**
     * Constructs a new EntityEntryProperty with an initial entity entry, storing the entity entry registry name,
     * and a predicate used to filter Forge registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial entity entry registry name value.
     * @param filter The predicate used to filter the Forge Registry collection values.
     */
    public EntityEntryProperty(ResourceLocation value, Predicate<ResourceLocation> filter) {
        super(value, filter);
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
