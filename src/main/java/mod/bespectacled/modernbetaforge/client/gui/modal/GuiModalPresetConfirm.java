package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModalPresetConfirm extends GuiModal<GuiModalPresetConfirm> {
    private static final int MODAL_WIDTH = 220;
    private static final int MODAL_HEIGHT = 120;
    
    private final List<String> textList;
    private final List<Integer> textColors;
    
    public GuiModalPresetConfirm(GuiScreen parent, String title, Consumer<GuiModalPresetConfirm> onConfirm, Consumer<GuiModalPresetConfirm> onCancel, List<String> textList, List<Integer> textColors) {
        super(parent, title, MODAL_WIDTH, MODAL_HEIGHT, onConfirm, onCancel);
        
        this.textList = textList;
        this.textColors = textColors;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        int rows = this.textList.size();
        int textListHeight = this.fontRenderer.FONT_HEIGHT * rows + TEXT_PADDING * rows;
        int textY = this.height / 2 - textListHeight / 2;
        
        for (int i = 0; i < this.textList.size(); ++i) {
            this.drawCenteredString(this.fontRenderer, this.textList.get(i), this.width / 2, textY, this.textColors.get(i));
            textY += this.fontRenderer.FONT_HEIGHT + TEXT_PADDING;
        }
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_CONFIRM:
                this.onConfirm.accept(this);
                break;
            case GUI_ID_CANCEL:
                this.onCancel.accept(this);
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
}
