package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiUtil {
    public static final int DEFAULT_WIDTH = 854;
    public static final int DEFAULT_HEIGHT = 480;
    
    public static final int DEFAULT_STANDARD_BUTTON_WIDTH = 70;
    public static final int MIN_STANDARD_BUTTON_WIDTH = 60;
    public static final int MAX_STANDARD_BUTTON_WIDTH = 80;
    
    public static final int DEFAULT_WIDE_BUTTON_WIDTH = 144;
    public static final int MIN_WIDE_BUTTON_WIDTH = 124;
    public static final int MAX_WIDE_BUTTON_WIDTH = 164;
    
    public static final int BUTTON_SPACE = 4;
    
    public static int getButtonWidth(int defaultWidth, int maxWidth, float currentDisplayWidth) {
        return getButtonWidth(defaultWidth, defaultWidth, maxWidth, currentDisplayWidth);
    }
    
    public static int getButtonWidth(int defaultWidth, int minWidth, int maxWidth, float currentDisplayWidth) {
        float scale = MathHelper.clamp(currentDisplayWidth / DEFAULT_WIDTH, 0.0f, 1.2f);
        
        return MathHelper.clamp((int)(scale * defaultWidth), minWidth, maxWidth);
    }
    
    public static int getStandardButtonWidth(float currentDisplayWidth) {
        return getButtonWidth(DEFAULT_STANDARD_BUTTON_WIDTH, MIN_STANDARD_BUTTON_WIDTH, MAX_STANDARD_BUTTON_WIDTH, currentDisplayWidth);
    }
    
    public static int getWideButtonWidth(float currentDisplayWidth) {
        return getButtonWidth(DEFAULT_WIDE_BUTTON_WIDTH, MIN_WIDE_BUTTON_WIDTH, MAX_WIDE_BUTTON_WIDTH, currentDisplayWidth);
    }
}
