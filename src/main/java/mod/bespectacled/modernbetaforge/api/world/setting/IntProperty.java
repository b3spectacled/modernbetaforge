package mod.bespectacled.modernbetaforge.api.world.setting;

import net.minecraft.util.math.MathHelper;

public class IntProperty extends Property<Integer> implements RangedProperty<Integer> {
    private final int minValue;
    private final int maxValue;
    
    public IntProperty(int value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    public IntProperty(int value, int minValue, int maxValue) {
        super(value);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(MathHelper.clamp(value, this.minValue, this.maxValue));
    }
    
    @Override
    public String getType() {
        return "int";
    }

    @Override
    public Integer getMinValue() {
        return this.minValue;
    }

    @Override
    public Integer getMaxValue() {
        return this.maxValue;
    }
}
