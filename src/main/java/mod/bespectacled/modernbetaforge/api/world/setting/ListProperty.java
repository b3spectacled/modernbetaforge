package mod.bespectacled.modernbetaforge.api.world.setting;

import java.util.Arrays;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.setting.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.GuiPropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;

public final class ListProperty extends StringProperty {
    private final String[] values;
    
    /**
     * Constructs a new ListProperty with an initial string and an array of valid values.
     * 
     * @param value The initial String value.
     * @param values Array of valid String values.
     */
    public ListProperty(String value, String[] values) {
        super(value);
        
        this.values = values;
    }

    @Override
    public String getType() {
        return "list";
    }

    @Override
    public void visitFactory(FactoryPropertyVisitor visitor, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
        visitor.visit(this, factory, registryKey, jsonObject);
    }

    @Override
    public GuiPageButtonList.GuiListEntry visitGui(GuiPropertyVisitor visitor, int guiIdentifier) {
        return visitor.visit(this, guiIdentifier);
    }
    
    /**
     * Gets the array of valid String values.
     * 
     * @return A String array of valid values.
     */
    public String[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }
    
    /**
     * Gets the index of the matching given value from the {@link ListProperty#values} array.
     * 
     * @param value The String value to search with.
     * @return The index from {@link ListProperty#values} if the search value is present, -1 otherwise.
     */
    public int indexOf(String value) {
        for (int i = 0; i < this.values.length; ++i) {
            if (value.equals(this.values[i])) {
                return i;
            }
        }
        
        return -1;
    }
}
