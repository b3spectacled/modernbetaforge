package mod.bespectacled.modernbetaforge.client.gui.element;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPageButtonListExtended extends GuiPageButtonList {
    private static final int BUTTON_SPACE = 5;
    private static final int PADDING_WIDTH = 10;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_SCROLLBAR_X = 5;
    
    private static final int MAX_WIDTH = 360;
    private static final int MIN_WIDTH = 300;
    
    public GuiPageButtonListExtended(Minecraft mc, int width, int height, int top, int bottom, int slotHeight, GuiResponder responder, GuiListEntry[][] entries) {
        super(mc, width, height, top, bottom, slotHeight, responder, entries);
    }
    
    @Override
    public int getListWidth() {
        return getListWidth(this.width);
    }

    @Override
    protected void populateComponents() {
        int entryX0 = this.width / 2 + OFFSET_X - getEntryWidth(this.width) - BUTTON_SPACE / 2;
        int entryX1 = this.width / 2 + OFFSET_X + BUTTON_SPACE / 2;
        
        for (GuiListEntry[] listEntry : this.pages) {
            for (int i = 0; i < listEntry.length; i += 2) {
                GuiListEntry guiEntry0 = listEntry[i];
                GuiListEntry guiEntry1 = i < listEntry.length - 1 ? listEntry[i + 1] : null;
                
                Gui gui0 = this.createEntry(guiEntry0, entryX0, guiEntry1 == null);
                Gui gui1 = this.createEntry(guiEntry1, entryX1, guiEntry0 == null);
                
                this.addComponent(guiEntry0, gui0);
                this.addComponent(guiEntry1, gui1);
                
                this.entries.add(new GuiPageButtonList.GuiEntry(gui0, gui1));
            }
        }
    }

    @Nullable
    @Override
    protected Gui createEntry(@Nullable GuiListEntry guiEntry, int entryX, boolean blankSpace) {
        if (guiEntry instanceof GuiSlideEntry) {
            return this.createSlider(entryX, 0, (GuiSlideEntry)guiEntry);
            
        } else if (guiEntry instanceof GuiButtonEntry) {
            return this.createButton(entryX, 0, (GuiButtonEntry)guiEntry);
            
        } else if (guiEntry instanceof EditBoxEntry) {
            return this.createTextField(entryX, 0, (EditBoxEntry)guiEntry);
            
        } else {
            return guiEntry instanceof GuiLabelEntry ? this.createLabel(entryX, 0, (GuiLabelEntry)guiEntry, blankSpace) : null;
            
        }
    }
    
    @Override
    protected GuiSlider createSlider(int x, int y, GuiSlideEntry entry) {
        GuiSlider guiSlider = new GuiSlider(this.responder, entry.getId(), x, y, entry.getCaption(), entry.getMinValue(), entry.getMaxValue(), entry.getInitalValue(), entry.getFormatter());
        guiSlider.visible = entry.shouldStartVisible();
        guiSlider.width = getEntryWidth(this.width);
        
        return guiSlider;
    }

    @Override
    protected GuiListButton createButton(int x, int y, GuiButtonEntry entry) {
        GuiListButton guiButton = new GuiListButton(this.responder, entry.getId(), x, y, entry.getCaption(), entry.getInitialValue());

        guiButton.visible = entry.shouldStartVisible();
        guiButton.width = getEntryWidth(this.width);
        
        return guiButton;
    }

    @Override
    protected GuiTextField createTextField(int x, int y, EditBoxEntry entry) {
        GuiTextField guiField = new GuiTextField(entry.getId(), this.mc.fontRenderer, x, y, getEntryWidth(this.width), 20);
        guiField.setText(entry.getCaption());
        guiField.setGuiResponder(this.responder);
        guiField.setVisible(entry.shouldStartVisible());
        guiField.setValidator(entry.getFilter());
        
        return guiField;
    }
    
    @Override
    protected GuiLabel createLabel(int x, int y, GuiLabelEntry entry, boolean blankSpace) {
        GuiLabel guiLabel;
        List<String> captions = this.mc.fontRenderer.listFormattedStringToWidth(entry.getCaption(), this.getListWidth() - OFFSET_SCROLLBAR_X * 3);

        if (blankSpace) {
            guiLabel = new GuiLabel(this.mc.fontRenderer, entry.getId(), x, y, this.width - x * 2 + OFFSET_X * 2, 20, -1);
        } else {
            guiLabel = new GuiLabel(this.mc.fontRenderer, entry.getId(), x, y, getEntryWidth(this.width), 20, -1);
        }

        guiLabel.visible = entry.shouldStartVisible();
        captions.forEach(caption -> guiLabel.addLine(caption));
        guiLabel.setCentered();
        
        return guiLabel;
    }
    
    @Override
    protected int getScrollBarX() {
        return this.width / 2 + this.getListWidth() / 2 + OFFSET_X - OFFSET_SCROLLBAR_X;
    }
    
    private void addComponent(GuiListEntry guiEntry, Gui gui) {
        if (guiEntry != null && gui != null) {
            this.componentMap.addKey(guiEntry.getId(), gui);

            if (gui instanceof GuiTextField) {
                this.editBoxes.add((GuiTextField)gui);
            }
        }
    }
    
    public static int getListWidth(int width) {
        return MathHelper.clamp((int)(width * 0.8), MIN_WIDTH, MAX_WIDTH);
    }

    public static int getEntryWidth(int width) {
        return getListWidth(width) / 2 - BUTTON_SPACE / 2 - PADDING_WIDTH;
    }
}
