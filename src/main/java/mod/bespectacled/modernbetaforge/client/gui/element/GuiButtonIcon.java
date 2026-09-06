package mod.bespectacled.modernbetaforge.client.gui.element;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonIcon extends GuiButton {
    public static final int ICON_SIZE = 20;
    
    private final boolean checkEnabled;
    
    public GuiButtonIcon(int id, int x, int y, String iconStr) {
        this(id, x, y, iconStr, true);
    }
    
    public GuiButtonIcon(int id, int x, int y, String iconStr, boolean checkEnabled) {
        super(id, x, y, ICON_SIZE, ICON_SIZE, iconStr);
        
        this.checkEnabled = checkEnabled;
    }

    @Override
    public boolean isMouseOver() {
        return (!this.checkEnabled || this.enabled) && this.hovered;
    }
}
