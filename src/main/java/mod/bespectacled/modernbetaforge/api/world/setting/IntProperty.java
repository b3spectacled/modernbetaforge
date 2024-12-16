package mod.bespectacled.modernbetaforge.api.world.setting;

import com.google.common.base.Predicate;
import com.google.common.primitives.Ints;

import net.minecraft.util.math.MathHelper;

public class IntProperty extends RangedProperty<Integer> {
    public IntProperty(int value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    public IntProperty(int value, int minValue, int maxValue) {
        super(value, minValue, maxValue);
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
