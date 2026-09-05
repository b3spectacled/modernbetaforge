package mod.bespectacled.modernbetaforge.client.gui.element;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiListButtonExtended extends GuiListButton {
    private final GuiScreen parent;
    
    public GuiListButtonExtended(GuiScreen parent, GuiResponder responder, int id, int x, int y, String localizationStr, boolean value) {
        super(responder, id, x, y, localizationStr, value);
        
        this.parent = parent;
        this.displayString = this.buildDisplayString();
    }

    
    @Override
    protected String buildDisplayString() {
        String formattedValue = I18n.format(this.value ? "gui.yes" : "gui.no");
        String formattedCaption = I18n.format(this.localizationStr);
        String formattedEntry = formattedCaption + ": " + formattedValue;
        
        // Hack to get around this getting called in the super constructor
        if (this.parent == null) {
            return formattedEntry;
        }
        
        FontRenderer fontRenderer = this.parent.mc.fontRenderer;
        
        int formattedValueWidth = fontRenderer.getStringWidth(formattedValue);
        int formattedEntryWidth = fontRenderer.getStringWidth(formattedEntry);
        
        int trimWidth = GuiPageButtonListExtended.getTrimWidth(this.parent.width);
        
        if (formattedEntryWidth > trimWidth) {
            int colonWidth = fontRenderer.getStringWidth(": ");
            formattedCaption = fontRenderer.trimStringToWidth(formattedCaption, trimWidth - formattedValueWidth - colonWidth) + "...";
        }
        
        return formattedCaption + ": " + formattedValue;
    }
}
