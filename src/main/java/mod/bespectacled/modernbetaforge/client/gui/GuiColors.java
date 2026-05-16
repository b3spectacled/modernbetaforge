package mod.bespectacled.modernbetaforge.client.gui;

import mod.bespectacled.modernbetaforge.util.MathUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiColors {
    public static final int RGB_WHITE = 16777215;
    public static final int RGB_GRAY = 10526880;
    public static final int RGB_DARK_GREY = 4276545;
    public static final int RGB_LIGHT_RED = 16752800;
    public static final int RGB_LIGHT_YELLOW = 16777120;
    public static final int RGB_DARK_YELLOW = 10526785;
    
    public static final int ARGB_LIGHT_GREY = -2039584;
    public static final int ARGB_DARK_GREY = -6250336;
    
    public static final int ARGB_TRANS_GREY = MathUtil.convertARGBComponentsToInt(50, 0, 0, 0);
    public static final int ARGB_TRANS_DARK_GREY = MathUtil.convertARGBComponentsToInt(200, 0, 0, 0);
    public static final int ARGB_TRANS_GREEN = MathUtil.convertARGBComponentsToInt(160, 128, 255, 128);
}
