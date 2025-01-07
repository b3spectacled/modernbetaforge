package mod.bespectacled.modernbetaforge.api.world.property;

import com.google.common.base.Predicate;
import com.google.common.primitives.Ints;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.setting.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.GuiPropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class IntProperty extends RangedProperty<Integer> {
    /**
     * Constructs a new IntProperty with minimum and maximum value constraints.
     * 
     * @param value The initial int property value.
     * @param minValue The minimum int property value.
     * @param maxValue The maximum int property value.
     * @param guiType The {@link PropertyGuiType}.
     */
    public IntProperty(int value, int minValue, int maxValue, PropertyGuiType guiType) {
        super(value, minValue, maxValue, guiType);
    }
    
    /**
     * Constructs a new IntProperty with {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} value constraints.
     * The PropertyGuiType is set to {@link PropertyGuiType#FIELD}.
     * 
     * @param value The initial int property value.
     */
    public IntProperty(int value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE, PropertyGuiType.FIELD);
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(MathHelper.clamp(value, this.getMinValue(), this.getMaxValue()));
    }
    
    @Override
    public String getType() {
        return "int";
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
        visitor.visit(this, guiIdentifier, value, registryKey);
    }

    @Override
    public String getFormatter() {
        return "%d";
    }
    
    @Override
    public Predicate<String> getStringPredicate() {
        return string -> {
            Integer value = Ints.tryParse(string);
            
            return string.isEmpty() || string.equals("-") || value != null;
        };
    }
}
