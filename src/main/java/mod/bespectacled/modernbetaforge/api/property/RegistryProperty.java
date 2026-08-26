package mod.bespectacled.modernbetaforge.api.property;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistry;
import mod.bespectacled.modernbetaforge.client.gui.screen.GuiScreenCustomizeRegistry;
import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.FormattedPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;

public class RegistryProperty<T> extends StringFilterProperty {
    private final ModernBetaRegistry<T> registry;
    
    /**
     * Constructs a new RegistryProperty with an initial registry entry and respective {@link ModernBetaRegistry}, with default predicate
     * not filtering any registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial registry name value.
     * @param registry The {@link ModernBetaRegistry} used to populate the list.
     */
    public RegistryProperty(ResourceLocation value, ModernBetaRegistry<T> registry) {
        this(value, true, registry, key -> true);
    }
    
    /**
     * Constructs a new RegistryProperty with an initial registry entry and respective {@link ModernBetaRegistry}, and a predicate
     * used to filter registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial registry name value.
     * @param registry The {@link ModernBetaRegistry} used to populate the list.
     * @param filter The predicate used to filter the {@link ModernBetaRegistry} collection values.
     */
    public RegistryProperty(ResourceLocation value, ModernBetaRegistry<T> registry, Predicate<ResourceLocation> filter) {
        this(value, true, registry, filter);
    }
    
    /**
     * Constructs a new RegistryProperty with an initial registry entry and respective {@link ModernBetaRegistry}, with default predicate
     * not filtering any registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial registry name value.
     * @param display The initial display value.
     * @param registry The {@link ModernBetaRegistry} used to populate the list.
     */
    public RegistryProperty(ResourceLocation value, boolean display, ModernBetaRegistry<T> registry) {
        this(value, display, registry, key -> true);
    }
    
    /**
     * Constructs a new RegistryProperty with an initial registry entry and respective {@link ModernBetaRegistry}, and a predicate
     * used to filter registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial registry name value.
     * @param display The initial display value.
     * @param registry The {@link ModernBetaRegistry} used to populate the list.
     * @param filter The predicate used to filter the {@link ModernBetaRegistry} collection values.
     */
    public RegistryProperty(ResourceLocation value, boolean display, ModernBetaRegistry<T> registry, Predicate<ResourceLocation> filter) {
        super(value, display, filter);
        
        this.registry = registry;
    }

    @Override
    public String getType() {
        return "registry";
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
    public String visitNameFormatter(FormattedPropertyVisitor visitor, ResourceLocation registryKey) {
        return visitor.visit(this, registryKey);
    }
    
    /**
     * Gets the {@link ModernBetaRegistry}.
     * 
     * @return The {@link ModernBetaRegistry}.
     */
    public ModernBetaRegistry<T> getRegistry() {
        return this.registry;
    }
}
