package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBoundsChecker {
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean hovered;
    
    public GuiBoundsChecker() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }
    
    public void updateBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void updateHovered(int mouseX, int mouseY) {
        this.hovered = this.inBounds(mouseX, mouseY);
    }
    
    public boolean inBounds(int mouseX, int mouseY) {
        int left = this.x;
        int right = this.x + this.width;
        int top = this.y;
        int bottom = this.y + this.height;
        
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }
    
    public int getRelativeX(int mouseX) {
        return mouseX - this.x;
    }
    
    public int getRelativeY(int mouseY) {
        return mouseY - this.y;
    }
    
    public boolean isHovered() {
        return this.hovered;
    }
}