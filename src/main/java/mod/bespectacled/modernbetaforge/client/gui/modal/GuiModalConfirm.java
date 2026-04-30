package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModalConfirm extends GuiModal<GuiModalConfirm> {
    private static final int PADDING_LR = 10;
    private static final int PADDING_TEXT_Y = 2;
    private static final int OFFSET_TEXT_Y = 3;
    
    private final String text;
    private final int textColor;
    
    public GuiModalConfirm(GuiScreen parent, String title, int modalWidth, int modalHeight, Consumer<GuiModalConfirm> onConfirm, Consumer<GuiModalConfirm> onCancel, String text, int textColor) {
        super(parent, title, modalWidth, modalHeight, onConfirm, onCancel);
        
        this.text = text;
        this.textColor = textColor;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int rowLen = this.modalWidth - PADDING_LR * 2;
        List<String> textList = this.fontRenderer.listFormattedStringToWidth(this.text, rowLen);
        
        int textListHeight = this.fontRenderer.getWordWrappedHeight(this.text, rowLen);
        textListHeight += PADDING_TEXT_Y * (textList.size() - 1);

        int textX = this.width / 2;
        int textY = this.height / 2 - textListHeight / 2 - OFFSET_TEXT_Y;
        
        for (String text : textList) {
            this.drawCenteredString(this.fontRenderer, text.trim(), textX, textY, this.textColor);
            textY += this.fontRenderer.FONT_HEIGHT + PADDING_TEXT_Y;
        }
    }
}
