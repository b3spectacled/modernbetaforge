package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraft.client.gui.GuiPageButtonList.GuiLabelEntry;

public class GuiColoredLabelEntry extends GuiLabelEntry {
    private final int color;
    
    public GuiColoredLabelEntry(int id, String caption, boolean startVisible) {
        this(id, caption, startVisible, -1);
    }
    
    public GuiColoredLabelEntry(int id, String caption, boolean startVisible, int color) {
        super(id, caption, startVisible);
        
        this.color = color;
    }
    
    public int getColor() {
        return this.color;
    }
}
