package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.io.IOException;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeWorld;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiModal<T extends GuiModal<?>> extends GuiScreen {
    private static final String PREFIX = String.format("createWorld.customize.custom.%s.", ModernBeta.MODID);
    
    protected static final int GUI_ID_CONFIRM = 0;
    protected static final int GUI_ID_CANCEL = 1;
    
    protected static final int MODAL_S_WIDTH = 100;
    protected static final int MODAL_S_HEIGHT = 50;
    protected static final int MODAL_L_HEIGHT = 60;
    
    protected static final int BUTTON_G_WIDTH = 160;
    protected static final int BUTTON_S_WIDTH = 60;
    protected static final int BUTTON_HEIGHT = 20;
    
    protected static final int GUI_PADDING = 4;
    protected static final int TEXT_PADDING = 4;
    protected static final int BUTTON_PADDING = 2;
    
    protected static final String GUI_LABEL_CONFIRM = I18n.format(PREFIX + "confirm");
    protected static final String GUI_LABEL_CANCEL = I18n.format("gui.cancel");
    
    protected final GuiScreen parent;
    protected final String title;
    protected final int modalWidth;
    protected final int modalHeight;
    protected final Consumer<T> onConfirm;
    protected final Consumer<T> onCancel;
    
    protected GuiButton buttonConfirm;
    protected GuiButton buttonCancel;
    
    public GuiModal(GuiScreen parent, String title, int modalWidth, int modalHeight, Consumer<T> onConfirm, Consumer<T> onCancel) {
        this.parent = parent;
        this.title = title;
        this.modalWidth = modalWidth;
        this.modalHeight = modalHeight;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    @Override
    public void initGui() {
        this.parent.setGuiSize(this.width, this.height);
        this.parent.initGui();
        this.buttonList.clear();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int confirmX = centerX - 62;
        int confirmY = centerY + this.modalHeight - BUTTON_HEIGHT - GUI_PADDING;
        
        int cancelX = centerX + 2;
        int cancelY = confirmY;

        this.buttonConfirm = this.addButton(new GuiButton(GUI_ID_CONFIRM, confirmX, confirmY, BUTTON_S_WIDTH, BUTTON_HEIGHT, GUI_LABEL_CONFIRM));
        this.buttonCancel = this.addButton(new GuiButton(GUI_ID_CANCEL, cancelX, cancelY, BUTTON_S_WIDTH, BUTTON_HEIGHT, GUI_LABEL_CANCEL));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.parent.drawScreen(mouseX, mouseY, partialTicks);
        this.drawModal(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        int titleX = this.width / 2;
        int titleY = this.height / 2 - this.modalHeight + this.fontRenderer.FONT_HEIGHT;
        this.drawCenteredString(this.fontRenderer, this.title, titleX, titleY, 16777215);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_CONFIRM:
                this.onConfirm.accept((T)this);
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_CANCEL:
                this.onCancel.accept((T)this);
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
    
    private void drawModal(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int modalWidth = this.modalWidth;
        int modalHeight = this.modalHeight;
        
        double texU = modalWidth * 0.0625;
        double texV = modalHeight * 0.0625;
        
        this.drawHorizontalLine(centerX - modalWidth - 1, centerX + modalWidth, centerY - modalHeight - 1, -2039584);
        this.drawHorizontalLine(centerX - modalWidth - 1, centerX + modalWidth, centerY + modalHeight, -6250336);
        this.drawVerticalLine(centerX - modalWidth - 1, centerY - modalHeight - 1, centerY + modalHeight, -2039584);
        this.drawVerticalLine(centerX + modalWidth, centerY - modalHeight - 1, centerY + modalHeight, -6250336);
        
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        
        this.mc.getTextureManager().bindTexture(GuiScreenCustomizeWorld.OPTIONS_BACKGROUND);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferBuilder.pos(centerX - modalWidth, centerY + modalHeight, 0.0).tex(0.0, texV).color(64, 64, 64, 64).endVertex();
        bufferBuilder.pos(centerX + modalWidth, centerY + modalHeight, 0.0).tex(texU, texV).color(64, 64, 64, 64).endVertex();
        bufferBuilder.pos(centerX + modalWidth, centerY - modalHeight, 0.0).tex(texU, 0.0).color(64, 64, 64, 64).endVertex();
        bufferBuilder.pos(centerX - modalWidth, centerY - modalHeight, 0.0).tex(0.0, 0.0).color(64, 64, 64, 64).endVertex();
        
        tessellator.draw();
    }
}
