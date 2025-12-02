package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiScreen;

public class GuiModalConfirm extends GuiModal<GuiModalConfirm> {
    private final List<String> textList;
    private final int textColor;
    
    public GuiModalConfirm(GuiScreen parent, String title, int modalWidth, int modalHeight, Consumer<GuiModalConfirm> onConfirm, Consumer<GuiModalConfirm> onCancel, List<String> textList, int textColor) {
        super(parent, title, modalWidth, modalHeight, onConfirm, onCancel);
        
        this.textList = textList;
        this.textColor = textColor;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        int rows = this.textList.size();
        int textListHeight = this.fontRenderer.FONT_HEIGHT * rows + TEXT_PADDING * rows;
        int textY = this.height / 2 - textListHeight / 2;
        
        for (int i = 0; i < this.textList.size(); ++i) {
            this.drawCenteredString(this.fontRenderer, this.textList.get(i), this.width / 2, textY, textColor);
            textY += this.fontRenderer.FONT_HEIGHT + TEXT_PADDING;
        }
    }
}
