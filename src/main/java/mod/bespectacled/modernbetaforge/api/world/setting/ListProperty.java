package mod.bespectacled.modernbetaforge.api.world.setting;

import java.util.Arrays;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.setting.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.GuiPropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class ListProperty extends StringProperty {
    private final String[] values;
    
    /**
     * Constructs a new ListProperty with the index of the initial value from an array of valid values.
     * 
     * @param index Index of the initial value from values.
     * @param values Array of valid String values.
     */
    public ListProperty(int index, String[] values) {
        super(values[MathHelper.clamp(index, 0, values.length - 1)]);
        
        this.values = values;
    }
    
    /**
     * Constructs a new ListProperty with the String value of the initial value from an array of valid values.
     * If the initial value isn't actually valid, then the value at index 0 of values is used.
     * 
     * @param value The initial value from values.
     * @param values Array of valid String values.
     */
    public ListProperty(String value, String[] values) {
        this(indexOfOrDefault(value, values), values);
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
    
    @Override
    public void visitEntryValue(EntryValuePropertyVisitor visitor, int guiIdentifier, Object value, ResourceLocation registryKey) {
        visitor.visit(this, guiIdentifier, (Float)value, registryKey);
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

    private static int indexOfOrDefault(String value, String[] values) {
        int index = 0;
        
        for (int i = 0; i < values.length; ++i) {
            if (value.equals(values[i])) {
                index = i;
            }
        }
        
        return index;
    }
}
