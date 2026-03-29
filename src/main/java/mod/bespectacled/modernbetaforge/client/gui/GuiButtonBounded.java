package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonBounded extends GuiButton {
    private final GuiBoundsChecker bounds;
    
    public GuiButtonBounded(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButtonBounded(int buttonId, int x, int y, int width, int height, String buttonText) {
        super(buttonId, x, y, width, height, buttonText);

        this.bounds = new GuiBoundsChecker();
        this.bounds.updateBounds(x, y, width, height);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return this.bounds.inBounds(mouseX, mouseY);
    }
}
