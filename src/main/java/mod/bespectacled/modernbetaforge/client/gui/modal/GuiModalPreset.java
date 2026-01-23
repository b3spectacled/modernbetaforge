package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.io.IOException;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.client.gui.GuiBoundsChecker;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizePresets;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModalPreset extends GuiModal<GuiModalPreset> {
    public enum State {
        SAVE, EDIT
    }
    
    private static final String PREFIX = "createWorld.customize.presets.modernbetaforge.";

    private static final ResourceLocation SCROLL_UP = ModernBeta.createRegistryKey("textures/gui/scroll_up.png");
    private static final ResourceLocation SCROLL_DOWN = ModernBeta.createRegistryKey("textures/gui/scroll_down.png");
    private static final ResourceLocation KZ = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
    private static final IconTexture[] ICON_TEXTURES = {
        new IconTexture(new ResourceLocation("textures/misc/unknown_pack.png")),
        new IconTexture(ModernBeta.createRegistryKey("textures/gui/presets/pack.png")),
        new IconTexture(new ResourceLocation("textures/blocks/grass_side.png")),
        new IconTexture(new ResourceLocation("textures/blocks/dirt.png")),
        new IconTexture(new ResourceLocation("textures/blocks/sand.png")),
        new IconTexture(new ResourceLocation("textures/blocks/gravel.png")),
        new IconTexture(new ResourceLocation("textures/blocks/stone.png")),
        new IconTexture(new ResourceLocation("textures/blocks/cobblestone.png")),
        new IconTexture(new ResourceLocation("textures/blocks/log_oak.png")),
        new IconTexture(new ResourceLocation("textures/blocks/planks_oak.png")),
        new IconTexture(new ResourceLocation("textures/blocks/coal_ore.png")),
        new IconTexture(new ResourceLocation("textures/blocks/iron_ore.png")),
        new IconTexture(new ResourceLocation("textures/blocks/gold_ore.png")),
        new IconTexture(new ResourceLocation("textures/blocks/redstone_ore.png")),
        new IconTexture(new ResourceLocation("textures/blocks/diamond_ore.png")),
        new IconTexture(new ResourceLocation("textures/blocks/lapis_ore.png")),
        new IconTexture(new ResourceLocation("textures/blocks/obsidian.png")),
        new IconTexture(new ResourceLocation("textures/blocks/netherrack.png")),
        new IconTexture(new ResourceLocation("textures/blocks/glowstone.png")),
        new IconTexture(new ResourceLocation("textures/blocks/end_stone.png")),
        new IconTexture(new ResourceLocation("textures/blocks/sponge.png")),
        new IconTexture(new ResourceLocation("textures/blocks/tnt_side.png")),
        new IconTexture(new ResourceLocation("textures/blocks/pumpkin_face_off.png")),
        new IconTexture(new ResourceLocation("textures/entity/steve.png"), 1.0 / 8.0, 1.0 / 8.0, 1.0 / 8.0),
        new IconTexture(new ResourceLocation("textures/entity/alex.png"), 1.0 / 8.0, 1.0 / 8.0, 1.0 / 8.0),
        new IconTexture(new ResourceLocation("textures/entity/zombie/zombie.png"), 1.0 / 8.0, 1.0 / 8.0, 1.0 / 8.0),
        new IconTexture(new ResourceLocation("textures/entity/skeleton/skeleton.png"), 1.0 / 8.0, 1.0 / 4.0, 1.0 / 8.0, 1.0 / 4.0),
        new IconTexture(new ResourceLocation("textures/entity/creeper/creeper.png"), 1.0 / 8.0, 1.0 / 4.0, 1.0 / 8.0, 1.0 / 4.0),
        new IconTexture(KZ, 0.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0),
        new IconTexture(KZ, 1.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0),
        new IconTexture(KZ, 2.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0),
        new IconTexture(KZ, 3.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0),
        new IconTexture(KZ, 4.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0),
        new IconTexture(KZ, 5.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0),
        new IconTexture(KZ, 6.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0),
        new IconTexture(KZ, 0.0 / 16.0, 0.0 / 16.0, 1.0 / 16.0),
        new IconTexture(KZ, 0.0 / 16.0, 8.0 / 16.0, 2.0 / 16.0),
        new IconTexture(KZ, 2.0 / 16.0, 8.0 / 16.0, 2.0 / 16.0),
        new IconTexture(KZ, 4.0 / 16.0, 8.0 / 16.0, 2.0 / 16.0),
        new IconTexture(KZ, 6.0 / 16.0, 8.0 / 16.0, 2.0 / 16.0),
        new IconTexture(KZ, 8.0 / 16.0, 8.0 / 16.0, 2.0 / 16.0),
        new IconTexture(KZ, 10.0 / 16.0, 8.0 / 16.0, 2.0 / 16.0),
        new IconTexture(KZ, 0.0 / 16.0, 12.0 / 16.0, 4.0 / 16.0),
        new IconTexture(KZ, 4.0 / 16.0, 12.0 / 16.0, 4.0 / 16.0),
        new IconTexture(KZ, 8.0 / 16.0, 12.0 / 16.0, 4.0 / 16.0)
    };
    
    private static final int MODAL_WIDTH = 320;
    private static final int MODAL_HEIGHT = 200;
    private static final int NAME_FIELD_LENGTH = 220;
    private static final int DESC_FIELD_LENGTH = 220;
    private static final int SETTINGS_FIELD_LENGTH = 300;
    private static final int ICON_PADDING_R = 20;
    private static final int ICON_PADDING_T = 36;
    private static final int ICON_SIZE = 50;
    private static final int SCROLL_TEXTURE_SIZE_W = 13;
    private static final int SCROLL_TEXTURE_SIZE_H = 9;
    
    private static final int MAX_PRESET_NAME_LENGTH = 30;
    private static final int MAX_PRESET_DESC_LENGTH = 60;
    
    private static final int GUI_ID_NAME = 12;
    private static final int GUI_ID_DESC = 13;
    private static final int GUI_ID_SETTINGS = 14;
    private static final int GUI_ID_NEXT = 15;
    private static final int GUI_ID_PREV = 16;

    private final GuiBoundsChecker iconBounds;
    private final String initialNameText;
    private final String initialDescText;
    private final String initialExportText;
    
    private GuiTextField fieldName;
    private GuiTextField fieldDesc;
    private GuiTextField fieldSettings;
    private GuiButton buttonPrev;
    private GuiButton buttonNext;
    
    private int selectedIcon;

    public GuiModalPreset(GuiScreenCustomizePresets parent, Consumer<GuiModalPreset> onConfirm, Consumer<GuiModalPreset> onCancel, State state) {
        super(parent, state == State.SAVE ? I18n.format(PREFIX + "save.title") : I18n.format(PREFIX + "edit.title"), MODAL_WIDTH, MODAL_HEIGHT, onConfirm, onCancel);

        this.iconBounds = new GuiBoundsChecker();
        this.initialNameText = state == State.SAVE ? "" : parent.getSelectedPresetName();
        this.initialDescText = state == State.SAVE ? "" : parent.getSelectedPresetDesc();
        this.initialExportText = state == State.SAVE ? parent.getInitialSettings() : parent.getSelectedPresetSettings();
        this.selectedIcon = state == State.SAVE ? 0 : MathHelper.clamp(parent.getSelectedPresetIcon(), 0, ICON_TEXTURES.length - 1);
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        super.initGui();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int boxL = centerX + modalWidth - ICON_SIZE - 1 - ICON_PADDING_R;
        int boxR = centerX + modalWidth - 0 - ICON_PADDING_R;
        int boxT = centerY - modalHeight - 0 + ICON_PADDING_T;
        int boxB = centerY - modalHeight + ICON_SIZE + 1 + ICON_PADDING_T;
        
        String initialModalNameText = this.fieldName != null ? this.fieldName.getText() : this.initialNameText;
        String initialModalDescText = this.fieldDesc != null ? this.fieldDesc.getText() : this.initialDescText;
        String intialModalSettingsText = this.fieldSettings != null ? this.fieldSettings.getText() : this.initialExportText;
        
        this.buttonPrev = this.addButton(new GuiButton(GUI_ID_PREV, boxL + 5, boxB + 5, 20, 20, I18n.format(PREFIX + "prev")));
        this.buttonNext = this.addButton(new GuiButton(GUI_ID_NEXT, boxR - 23, boxB + 5, 20, 20, I18n.format(PREFIX + "next")));
        
        this.fieldName = this.createInitialField(this.fieldName, GUI_ID_NAME, centerX - modalWidth + 10, centerY - 50, NAME_FIELD_LENGTH, 20, initialModalNameText, MAX_PRESET_NAME_LENGTH);
        this.fieldDesc = this.createInitialField(this.fieldDesc, GUI_ID_DESC, centerX - modalWidth + 10, centerY - 10, DESC_FIELD_LENGTH, 20, initialModalDescText, MAX_PRESET_DESC_LENGTH);
        this.fieldSettings = this.createInitialField(this.fieldSettings, GUI_ID_SETTINGS, centerX - modalWidth + 10, centerY + 30, SETTINGS_FIELD_LENGTH, 20, intialModalSettingsText, ModernBetaGeneratorSettings.MAX_PRESET_LENGTH);
        
        this.iconBounds.updateBounds(boxL + 1, boxT + 1, ICON_SIZE, ICON_SIZE);
        this.updateButtonValidity();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawSaveScreen(this.width / 2, this.height / 2, mouseX, mouseY);
    }
    
    @Override
    public void updateScreen() {
        this.fieldName.updateCursorCounter();
        this.fieldDesc.updateCursorCounter();
        this.fieldSettings.updateCursorCounter();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        
        if (this.iconBounds.inBounds(mouseX, mouseY) && Mouse.hasWheel()) {
            int dWheel = Mouse.getEventDWheel();
            
            if (dWheel != 0) {
                this.incrementSelectedIcon(dWheel < 0 ? -1 : 1);
                this.updateButtonValidity();
            }
        }
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    public String getNameText() {
        return this.fieldName.getText();
    }
    
    public String getDescText() {
        return this.fieldDesc.getText();
    }
    
    public String getSettingsText() {
        return this.fieldSettings.getText();
    }
    
    public int getIcon() {
        return this.selectedIcon;
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        int amount = GuiScreen.isShiftKeyDown() ? 5 : 1;

        switch (guiButton.id) {
            case GUI_ID_CONFIRM:
                this.onConfirm.accept(this);
                break;
            case GUI_ID_CANCEL:
                this.onCancel.accept(this);
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_PREV:
                this.incrementSelectedIcon(-amount);
                break;
            case GUI_ID_NEXT:
                this.incrementSelectedIcon(amount);
                break;
        }
        
        this.updateButtonValidity();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        
        this.fieldName.mouseClicked(mouseX, mouseY, mouseButton);
        this.fieldDesc.mouseClicked(mouseX, mouseY, mouseButton);
        this.fieldSettings.mouseClicked(mouseX, mouseY, mouseButton);
        
        this.updateButtonValidity();
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);
     
        this.fieldName.textboxKeyTyped(character, keyCode);
        this.fieldDesc.textboxKeyTyped(character, keyCode);
        this.fieldSettings.textboxKeyTyped(character, keyCode);
        
        this.updateButtonValidity();
    }
    
    private void updateButtonValidity() {
        this.buttonConfirm.enabled = !this.fieldName.getText().isEmpty();
        this.buttonPrev.enabled = this.selectedIcon > 0;
        this.buttonNext.enabled = this.selectedIcon < ICON_TEXTURES.length - 1;
    }

    private void drawSaveScreen(int centerX, int centerY, int mouseX, int mouseY) {
        IconTexture icon = ICON_TEXTURES[this.selectedIcon];
        int textStartX = centerX - modalWidth + 10;
        
        this.drawString(this.fontRenderer, I18n.format(PREFIX + "name"), textStartX, centerY - 60, 10526880);
        this.drawString(this.fontRenderer, I18n.format(PREFIX + "desc"), textStartX, centerY - 20, 10526880);
        this.drawString(this.fontRenderer, I18n.format(PREFIX + "settings"), textStartX, centerY + 20, 10526880);
        this.fieldName.drawTextBox();
        this.fieldDesc.drawTextBox();
        this.fieldSettings.drawTextBox();

        String nameNumChars = String.format("%d", MAX_PRESET_NAME_LENGTH - this.fieldName.getText().length());
        String descNumChars = String.format("%d", MAX_PRESET_DESC_LENGTH - this.fieldDesc.getText().length());
        String settingsNumChars = String.format("%d", ModernBetaGeneratorSettings.MAX_PRESET_LENGTH - this.fieldSettings.getText().length());
        
        int nameNumCharsLen = this.fontRenderer.getStringWidth(nameNumChars);
        int descNumCharsLen = this.fontRenderer.getStringWidth(descNumChars);
        int settingsNumCharsLen = this.fontRenderer.getStringWidth(settingsNumChars);
        
        int nameNumCharsCol = MAX_PRESET_NAME_LENGTH - this.fieldName.getText().length() > 4 ? 10526880 : 16752800;
        int descNumCharsCol = MAX_PRESET_DESC_LENGTH - this.fieldDesc.getText().length() > 4 ? 10526880 : 16752800;
        int settingsNumCharsCol = ModernBetaGeneratorSettings.MAX_PRESET_LENGTH - this.fieldSettings.getText().length() > 100 ? 10526880 : 16752800;
        
        this.drawString(this.fontRenderer, nameNumChars, textStartX + NAME_FIELD_LENGTH - nameNumCharsLen, centerY - 60, nameNumCharsCol);
        this.drawString(this.fontRenderer, descNumChars, textStartX + DESC_FIELD_LENGTH - descNumCharsLen, centerY - 20, descNumCharsCol);
        this.drawString(this.fontRenderer, settingsNumChars, textStartX + SETTINGS_FIELD_LENGTH - settingsNumCharsLen, centerY + 20, settingsNumCharsCol);
        
        int boxL = centerX + this.modalWidth - ICON_SIZE - 1 - ICON_PADDING_R;
        int boxR = centerX + this.modalWidth - 0 - ICON_PADDING_R;
        int boxT = centerY - this.modalHeight - 0 + ICON_PADDING_T;
        int boxB = centerY - this.modalHeight + ICON_SIZE + 1 + ICON_PADDING_T;

        String iconText = String.format("%d/%d", this.selectedIcon + 1, ICON_TEXTURES.length);
        
        this.drawCenteredString(this.fontRenderer, iconText, boxL + ICON_SIZE / 2 + 1, boxT - 10, 10526880);
        this.drawHorizontalLine(boxL, boxR, boxT, -2039584);
        this.drawHorizontalLine(boxL, boxR, boxB, -6250336);
        this.drawVerticalLine(boxL, boxT, boxB, -2039584);
        this.drawVerticalLine(boxR, boxT, boxB, -6250336);
        
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(icon.identifier);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(boxL + 1, boxB, 0.0).tex(icon.u, icon.v + icon.h).endVertex();
        bufferBuilder.pos(boxR, boxB, 0.0).tex(icon.u + icon.w, icon.v + icon.h).endVertex();
        bufferBuilder.pos(boxR, boxT + 1, 0.0).tex(icon.u + icon.w, icon.v).endVertex();
        bufferBuilder.pos(boxL + 1, boxT + 1, 0.0).tex(icon.u, icon.v).endVertex();
        
        tessellator.draw();
        
        if (this.iconBounds.inBounds(mouseX, mouseY) && Mouse.hasWheel()) {
            int offsetX = 6;
            int offsetY = 12;
            
            int scrollL = mouseX + offsetX;
            int scrollR = mouseX + (int)(SCROLL_TEXTURE_SIZE_W / 1.0) + offsetX;
            int scrollT = mouseY - offsetY;
            int scrollB = mouseY + (int)(SCROLL_TEXTURE_SIZE_H / 1.0) - offsetY;
            
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            
            if (this.selectedIcon < ICON_TEXTURES.length - 1) {
                this.mc.getTextureManager().bindTexture(SCROLL_UP);
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferBuilder.pos(scrollL, scrollB, 0.0).tex(0.0, 1.0).endVertex();
                bufferBuilder.pos(scrollR, scrollB, 0.0).tex(1.0, 1.0).endVertex();
                bufferBuilder.pos(scrollR, scrollT, 0.0).tex(1.0, 0.0).endVertex();
                bufferBuilder.pos(scrollL, scrollT, 0.0).tex(0.0, 0.0).endVertex();
                tessellator.draw();
            }
            
            if (this.selectedIcon > 0) {
                scrollT += SCROLL_TEXTURE_SIZE_H + 1;
                scrollB += SCROLL_TEXTURE_SIZE_H + 1;
                
                this.mc.getTextureManager().bindTexture(SCROLL_DOWN);
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferBuilder.pos(scrollL, scrollB, 0.0).tex(0.0, 1.0).endVertex();
                bufferBuilder.pos(scrollR, scrollB, 0.0).tex(1.0, 1.0).endVertex();
                bufferBuilder.pos(scrollR, scrollT, 0.0).tex(1.0, 0.0).endVertex();
                bufferBuilder.pos(scrollL, scrollT, 0.0).tex(0.0, 0.0).endVertex();
                tessellator.draw();
            }
        }
    }

    private void incrementSelectedIcon(int amount) {
        this.selectedIcon = MathHelper.clamp(this.selectedIcon + amount, 0, ICON_TEXTURES.length - 1);
    }

    private GuiTextField createInitialField(GuiTextField textField, int id, int x, int y, int width, int height, String initialText, int maxLength) {
        GuiTextField newTextField = new GuiTextField(id, this.fontRenderer, x, y, width, height);
        boolean initialized = textField != null;
        
        boolean initialFocused = initialized ? textField.isFocused() : false;
        int initialCursorPos = initialized ? textField.getCursorPosition() : -1;
        
        newTextField.setMaxStringLength(maxLength);
        newTextField.setText(initialText);
        newTextField.setFocused(initialFocused);
        if (initialCursorPos != -1) {
            newTextField.setCursorPosition(initialCursorPos);
        }
        
        return newTextField;
    }
    
    public static IconTexture getIconTexture(int ndx) {
        return ICON_TEXTURES[MathHelper.clamp(ndx, 0, ICON_TEXTURES.length - 1)];
    }

    @SideOnly(Side.CLIENT)
    public static class IconTexture {
        public final double u;
        public final double v;
        public final double w;
        public final double h;
        
        private ResourceLocation identifier;
        private boolean checked;
        
        public IconTexture(ResourceLocation identifier) {
            this(identifier, 0.0, 0.0, 1.0, 1.0);
        }
        
        public IconTexture(ResourceLocation identifier, double u, double v, double w) {
            this(identifier, u, v, w, w);
        }
        
        public IconTexture(ResourceLocation identifier, double u, double v, double w, double h) {
            this.identifier = identifier;
            this.u = u;
            this.v = v;
            this.w = w;
            this.h = h;
        }
        
        public ResourceLocation getIdentifier() {
            return this.identifier;
        }
        
        public boolean wasChecked() {
            return this.checked;
        }
        
        public void setIdentifier(ResourceLocation identifier) {
            this.identifier = identifier;
        }
        
        public void setChecked() {
            this.checked = true;
        }
    }
}
