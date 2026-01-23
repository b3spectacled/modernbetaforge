package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.io.IOException;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModalConfirmSettings extends GuiModal<GuiModalConfirmSettings> {
    private static final String PREFIX = String.format("createWorld.customize.custom.%s.", ModernBeta.MODID);
    private static final String GUI_LABEL_DISCARD = I18n.format(PREFIX + "discard");
    
    private static final int MODAL_WIDTH = 300;
    private static final int MODAL_HEIGHT = 200;
    private static final int PAGE_LIST_PADDING_TOP = 24;
    private static final int PAGE_LIST_PADDING_BOTTOM = 27;
    private static final int PAGE_LIST_SLOT_HEIGHT = 25;
    private static final int GUI_ID_DISCARD = 2;
    
    private final Consumer<GuiModalConfirmSettings> onDiscard;
    
    private GuiListExtendedNoOverlay changeList;

    public GuiModalConfirmSettings(GuiScreen parent, String title, Consumer<GuiModalConfirmSettings> onConfirm, Consumer<GuiModalConfirmSettings> onCancel, Consumer<GuiModalConfirmSettings> onDiscard) {
        super(parent, title, MODAL_WIDTH, MODAL_HEIGHT, onConfirm, onCancel);

        this.onDiscard = onDiscard;
    }
	
    @Override
    public void initGui() {
        super.initGui();
        
        int curScroll = 0;
        if (this.changeList != null) {
            curScroll = this.changeList.getAmountScrolled();
        }
        
        this.createChangeList();
        this.changeList.scrollBy(curScroll);
        
        int centerY = this.height / 2;
        
        int confirmX = this.width / 2 - BUTTON_S_WIDTH / 2 - BUTTON_S_WIDTH - BUTTON_PADDING;
        int cancelX = this.width / 2 + BUTTON_S_WIDTH / 2 + BUTTON_PADDING;
        
        int discardX = this.width / 2 - BUTTON_S_WIDTH / 2;
        int discardY = centerY + this.modalHeight / 2 - BUTTON_HEIGHT - GUI_PADDING;
        
        this.buttonConfirm.x = confirmX;
        this.buttonConfirm.y = discardY;
        
        this.buttonCancel.x = cancelX;
        this.buttonCancel.y = discardY;
        
        this.addButton(new GuiButton(GUI_ID_DISCARD, discardX, discardY, BUTTON_S_WIDTH, BUTTON_HEIGHT, GUI_LABEL_DISCARD));
    }

    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.changeList.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.parent.drawScreen(mouseX, mouseY, partialTicks);
        this.drawModal(mouseX, mouseY, partialTicks);
        this.changeList.drawScreen(mouseX, mouseY, partialTicks);
        for (int i = 0; i < this.buttonList.size(); ++i) {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }
       
        int titleX = this.width / 2;
        int titleY = this.height / 2 - this.modalHeight / 2 + this.fontRenderer.FONT_HEIGHT;
        this.drawCenteredString(this.fontRenderer, this.title, titleX, titleY, 16777215);
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
            case GUI_ID_DISCARD:
                this.onDiscard.accept(this);
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.changeList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.changeList.mouseReleased(mouseX, mouseY, mouseButton);
    }
    
    private void createChangeList() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.modalHeight = (int)(this.height * 0.8);
        
        int modalLeft = centerX - this.modalWidth / 2;
        int modalRight = centerX + this.modalWidth / 2;
        int modalTop = centerY - this.modalHeight / 2 + PAGE_LIST_PADDING_TOP;
        int modalBottom = centerY + this.modalHeight / 2 - PAGE_LIST_PADDING_BOTTOM;
        
        this.changeList = new GuiListExtendedNoOverlay(
            this.mc,
            this.modalWidth,
            this.modalHeight,
            modalTop,
            modalBottom,
            PAGE_LIST_SLOT_HEIGHT
        );
        
        this.changeList.left = modalLeft;
        this.changeList.right = modalRight;
        this.changeList.top = modalTop;
    }
    
    @SideOnly(Side.CLIENT)
    private static class GuiListExtendedNoOverlay extends GuiListExtended {
        public GuiListExtendedNoOverlay(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
            super(mc, width, height, top, bottom, slotHeight);
        }
		
        @Override
        protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) { }

        @Override
        public IGuiListEntry getListEntry(int index) {
            return null;
        }

        @Override
        protected int getSize() {
            return 0;
        }
    }
}
