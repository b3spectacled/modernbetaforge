package mod.bespectacled.modernbetaforge.client.gui.element;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBoundsChecker {
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean hovered;
    private boolean enabled;
    
    public GuiBoundsChecker() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.enabled = true;
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
        
        return this.enabled && mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
}