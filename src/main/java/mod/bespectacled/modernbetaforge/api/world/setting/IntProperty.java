package mod.bespectacled.modernbetaforge.api.world.setting;

import com.google.common.base.Predicate;
import com.google.common.primitives.Ints;

import net.minecraft.util.math.MathHelper;

public class IntProperty extends RangedProperty<Integer> {
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
     * @param minValue The minimum int property value.
     * @param maxValue The maximum int property value.
     * @param guiType The {@link PropertyGuiType}.
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
    public Predicate<String> getStringPredicate() {
        return string -> {
            Integer value = Ints.tryParse(string);
            
            return string.isEmpty() || (value != null && value >= this.getMinValue() && value <= this.getMaxValue());
        };
    }
}
