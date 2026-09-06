package mod.bespectacled.modernbetaforge.client.gui.element;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonIcon extends GuiButton {
    private static final int ICON_SIZE = 20;
    
    public GuiButtonIcon(int id, int x, int y, String iconStr) {
        super(id, x, y, ICON_SIZE, ICON_SIZE, iconStr);
    }

    @Override
    public boolean isMouseOver() {
        return this.enabled && this.hovered;
    }
}
