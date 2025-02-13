package mod.bespectacled.modernbetaforge.api.property;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeRegistry;
import net.minecraft.util.ResourceLocation;

public abstract class RegistryProperty extends StringProperty {
    private final Predicate<ResourceLocation> filter;

    /**
     * Constructs a new RegistryFilterProperty with an initial value, storing the registry name, and a predicate used
     * to filter Forge registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @param value The initial registry name value.
     * @param filter The predicate used to filter the Forge Registry collection values.
     */
    public RegistryProperty(ResourceLocation value, Predicate<ResourceLocation> filter) {
        super(value.toString());
        
        this.filter = filter;
    }

    /**
     * Gets the predicate used to filter Forge registry entries when populating {@link GuiScreenCustomizeRegistry} list.
     * 
     * @return The predicate used to filter the Forge Registry collection values.
     */
    public final Predicate<ResourceLocation> getFilter() {
        return this.filter;
    }
}
