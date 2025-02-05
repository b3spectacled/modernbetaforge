package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiCustomizePreset;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiCustomizePresetsDataHandler.PresetData;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.SoundUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
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
public class GuiScreenCustomizePresets extends GuiScreen {
    public enum FilterType {
        ALL, BUILTIN, ADDON, CUSTOM
    }
    
    private enum ModalState {
        NONE, SAVE, EDIT, DELETE, OVERWRITE
    }
    
    private static final ResourceLocation[] ICON_IDENTIFIERS = {
        new ResourceLocation("textures/misc/unknown_pack.png"),
        ModernBeta.createRegistryKey("textures/gui/presets/pack.png"),
        new ResourceLocation("textures/blocks/grass_side.png"),
        new ResourceLocation("textures/blocks/dirt.png"),
        new ResourceLocation("textures/blocks/sand.png"),
        new ResourceLocation("textures/blocks/gravel.png"),
        new ResourceLocation("textures/blocks/stone.png"),
        new ResourceLocation("textures/blocks/cobblestone.png"),
        new ResourceLocation("textures/blocks/log_oak.png"),
        new ResourceLocation("textures/blocks/planks_oak.png"),
        new ResourceLocation("textures/blocks/coal_ore.png"),
        new ResourceLocation("textures/blocks/iron_ore.png"),
        new ResourceLocation("textures/blocks/gold_ore.png"),
        new ResourceLocation("textures/blocks/redstone_ore.png"),
        new ResourceLocation("textures/blocks/diamond_ore.png"),
        new ResourceLocation("textures/blocks/lapis_ore.png"),
        new ResourceLocation("textures/blocks/obsidian.png"),
        new ResourceLocation("textures/blocks/netherrack.png"),
        new ResourceLocation("textures/blocks/glowstone.png"),
        new ResourceLocation("textures/blocks/end_stone.png"),
        new ResourceLocation("textures/blocks/sponge.png"),
        new ResourceLocation("textures/blocks/tnt_side.png"),
        new ResourceLocation("textures/blocks/pumpkin_face_off.png")
    };
    
    private static final String PREFIX = "createWorld.customize.presets.";
    private static final String PREFIX_FILTER = "createWorld.customize.presets.filter";
    
    private static final int SLOT_HEIGHT = 32;
    private static final int SLOT_PADDING = 6;
    private static final int MAX_PRESET_LENGTH = 50000;
    private static final int MAX_PRESET_DESC_LINE_LENGTH = 188;
    private static final int MAX_PRESET_NAME_LENGTH = 25;
    private static final int MAX_PRESET_DESC_LENGTH = 50;
    private static final int MODAL_WIDTH = 320 / 2;
    private static final int MODAL_HEIGHT = 200 / 2;
    private static final int MODAL_WIDTH_SMALL = 225 / 2;
    private static final int MODAL_HEIGHT_SMALL = 125 / 2;
    private static final int MODAL_NAME_FIELD_LENGTH = 220;
    private static final int MODAL_DESC_FIELD_LENGTH = 220;
    private static final int MODAL_SETTINGS_FIELD_LENGTH = 300;
    private static final int MODAL_ICON_PADDING_R = 20;
    private static final int MODAL_ICON_PADDING_T = 36;
    private static final int MODAL_ICON_SIZE = 50;
    
    private static final int GUI_ID_FILTER = 0;
    private static final int GUI_ID_SELECT = 1;
    private static final int GUI_ID_CANCEL = 2;
    private static final int GUI_ID_SAVE = 3;
    private static final int GUI_ID_EDIT = 4;
    private static final int GUI_ID_DELETE = 5;
    private static final int GUI_ID_EXPORT = 6;
    
    private static final int GUI_ID_MODAL_CONFIRM = 10;
    private static final int GUI_ID_MODAL_CANCEL = 11;
    private static final int GUI_ID_MODAL_NAME = 12;
    private static final int GUI_ID_MODAL_DESC = 13;
    private static final int GUI_ID_MODAL_SETTINGS = 14;
    private static final int GUI_ID_MODAL_NEXT = 15;
    private static final int GUI_ID_MODAL_PREV = 16;
    
    private final GuiScreenCustomizeWorld parent;
    private final FilterType filterType;
    private final GuiCustomizePresetsDataHandler dataHandler;
    private final List<Info> presets;
    private final int initialPreset;
    
    private ListPreset list;
    private GuiTextField fieldExport;
    private GuiTextField fieldModalName;
    private GuiTextField fieldModalDesc;
    private GuiTextField fieldModalSettings;
    private GuiButton buttonSave;
    private GuiButton buttonEdit;
    private GuiButton buttonDelete;
    private GuiButton buttonFilter;
    private GuiButton buttonSelect;
    private GuiButton buttonCancel;
    private GuiButton buttonModalConfirm;
    private GuiButton buttonModalCancel;
    private GuiButton buttonModalPrev;
    private GuiButton buttonModalNext;
    private int selectedIcon;
    private String shareText;
    private int hoveredElement;
    @SuppressWarnings("unused") private long hoveredTime;
    private ModalState modalState;
    private long confirmExitTime;
    
    protected String title;
    
    public GuiScreenCustomizePresets(GuiScreenCustomizeWorld parent) {
        this(parent, ModernBetaConfig.guiOptions.defaultPresetFilter, -1);
    }
    
    public GuiScreenCustomizePresets(GuiScreenCustomizeWorld parent, FilterType filterType, int initialPreset) {
        this.title = I18n.format(PREFIX + "title");
        this.parent = parent;
        this.filterType = filterType;
        this.dataHandler = new GuiCustomizePresetsDataHandler(Minecraft.getMinecraft());
        this.presets = this.loadPresets(filterType, this.dataHandler);
        this.initialPreset = initialPreset;
        this.hoveredElement = -1;
        this.modalState = ModalState.NONE;
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int modalWidth = this.modalState == ModalState.DELETE || this.modalState == ModalState.OVERWRITE ? MODAL_WIDTH_SMALL : MODAL_WIDTH;
        int modalHeight = this.modalState == ModalState.DELETE || this.modalState == ModalState.OVERWRITE ? MODAL_HEIGHT_SMALL : MODAL_HEIGHT;
        
        int boxL = centerX + modalWidth - MODAL_ICON_SIZE - 1 - MODAL_ICON_PADDING_R;
        int boxR = centerX + modalWidth - 0 - MODAL_ICON_PADDING_R;
        int boxB = centerY - modalHeight + MODAL_ICON_SIZE + 1 + MODAL_ICON_PADDING_T;
        
        this.buttonList.clear();
        this.buttonFilter = this.addButton(new GuiButton(GUI_ID_FILTER, centerX + 2, this.height - 27, 75, 20, this.getFilterText()));
        this.buttonSelect = this.addButton(new GuiButton(GUI_ID_SELECT, centerX + 2, this.height - 50, 152, 20, I18n.format(PREFIX + "select")));
        this.buttonCancel = this.addButton(new GuiButton(GUI_ID_CANCEL, centerX + 80, this.height - 27, 75, 20, I18n.format("gui.cancel")));
        this.buttonSave = this.addButton(new GuiButton(GUI_ID_SAVE, centerX - 154, this.height - 50, 152, 20, I18n.format(PREFIX + "save")));
        this.buttonEdit = this.addButton(new GuiButton(GUI_ID_EDIT, centerX - 154, this.height - 27, 75, 20, I18n.format(PREFIX + "edit")));
        this.buttonDelete = this.addButton(new GuiButton(GUI_ID_DELETE, centerX - 76, this.height - 27, 75, 20, I18n.format(PREFIX + "delete")));
        
        this.shareText = I18n.format(PREFIX + "share");
        this.list = this.list != null ? new ListPreset(this.list.selected) : new ListPreset(this.initialPreset);
        
        int numDisplayed = (this.list.height - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM) / (SLOT_HEIGHT + SLOT_PADDING);
        if (this.list.selected > numDisplayed - 1) {
            this.list.scrollBy(SLOT_HEIGHT * (this.list.selected + 1));
        }
        
        String initialExportText = this.getInitialExportText();
        String initialModalNameText = this.fieldModalName != null ? this.fieldModalName.getText() : "";
        String initialModalDescText = this.fieldModalDesc != null ? this.fieldModalDesc.getText() : "";
        String intialModalSettingsText = this.fieldModalSettings != null ? this.fieldModalSettings.getText() : initialExportText;

        this.fieldExport = this.createInitialField(this.fieldExport, GUI_ID_EXPORT, 50, 40, this.width - 100, 20, initialExportText, MAX_PRESET_LENGTH);
        
        this.buttonModalConfirm = this.addButton(new GuiButton(GUI_ID_MODAL_CONFIRM, centerX - 62, centerY + modalHeight - 25, 60, 20, I18n.format(PREFIX + "confirm")));
        this.buttonModalCancel = this.addButton(new GuiButton(GUI_ID_MODAL_CANCEL, centerX + 2, centerY + modalHeight - 25, 60, 20, I18n.format("gui.cancel")));
        this.buttonModalPrev = this.addButton(new GuiButton(GUI_ID_MODAL_PREV, boxL + 5, boxB + 5, 20, 20, I18n.format(PREFIX + "prev")));
        this.buttonModalNext = this.addButton(new GuiButton(GUI_ID_MODAL_NEXT, boxR - 23, boxB + 5, 20, 20, I18n.format(PREFIX + "next")));
        
        this.fieldModalName = this.createInitialField(this.fieldModalName, GUI_ID_MODAL_NAME, centerX - modalWidth + 10, centerY - 50, MODAL_NAME_FIELD_LENGTH, 20, initialModalNameText, MAX_PRESET_NAME_LENGTH);
        this.fieldModalDesc = this.createInitialField(this.fieldModalDesc, GUI_ID_MODAL_DESC, centerX - modalWidth + 10, centerY - 10, MODAL_DESC_FIELD_LENGTH, 20, initialModalDescText, MAX_PRESET_DESC_LENGTH);
        this.fieldModalSettings = this.createInitialField(this.fieldModalSettings, GUI_ID_MODAL_SETTINGS, centerX - modalWidth + 10, centerY + 30, MODAL_SETTINGS_FIELD_LENGTH, 20, intialModalSettingsText, MAX_PRESET_LENGTH);
        
        ModernBeta.log("INITIAL PRESET: " + this.initialPreset);
        
        this.updateButtonValidity();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        if (this.canInteract() && this.modalState == ModalState.NONE) {
            this.list.handleMouseInput();
        }
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 12, 16777215);
        this.drawString(this.fontRenderer, this.shareText, 50, 30, 10526880);
        this.fieldExport.drawTextBox();
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        if (this.modalState != ModalState.NONE) {
            Info selectedPreset = this.presets.size() > 0 ? this.presets.get(this.list.selected > -1 ? this.list.selected : 0) : new Info();
            
            Gui.drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
            
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            
            int modalWidth = this.modalState == ModalState.DELETE || this.modalState == ModalState.OVERWRITE ? MODAL_WIDTH_SMALL : MODAL_WIDTH;
            int modalHeight = this.modalState == ModalState.DELETE || this.modalState == ModalState.OVERWRITE ? MODAL_HEIGHT_SMALL : MODAL_HEIGHT;
            
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
            bufferBuilder.pos(centerX - modalWidth, centerY + modalHeight, 0.0).tex(0.0, 2.65625).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(centerX + modalWidth, centerY + modalHeight, 0.0).tex(5.625, 2.65625).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(centerX + modalWidth, centerY - modalHeight, 0.0).tex(5.625, 0.0).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(centerX - modalWidth, centerY - modalHeight, 0.0).tex(0.0, 0.0).color(64, 64, 64, 64).endVertex();
            
            tessellator.draw();
            
            String confirmTitle = "";
            
            switch (this.modalState) {
                case SAVE:
                    confirmTitle = I18n.format(PREFIX + "save.title");
                    this.drawSaveScreen(centerX, centerY, modalWidth, modalHeight);
                    break;
                case EDIT:
                    confirmTitle = I18n.format(PREFIX + "edit.title");
                    this.drawSaveScreen(centerX, centerY, modalWidth, modalHeight);
                    break;
                case DELETE:
                    confirmTitle = I18n.format(PREFIX + "delete.title");
                    this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "delete.confirm"), centerX, centerY - 16, 16752800);
                    this.drawCenteredString(this.fontRenderer, selectedPreset.name, centerX, centerY + 2, 16777215);
                    break;
                case OVERWRITE:
                    confirmTitle = I18n.format(PREFIX + "overwrite.title");
                    this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "overwrite.confirm"), centerX, centerY - 16, 16752800);
                    this.drawCenteredString(this.fontRenderer, this.fieldModalName.getText(), centerX, centerY + 2, 16777215);
                    break;
                case NONE:
                    break;
            }

            this.drawCenteredString(this.fontRenderer, confirmTitle, centerX, centerY - modalHeight + 10, 16777215);
            
            this.buttonModalConfirm.drawButton(this.mc, mouseX, mouseY, partialTicks);
            this.buttonModalCancel.drawButton(this.mc, mouseX, mouseY, partialTicks);
            this.buttonModalPrev.drawButton(this.mc, mouseX, mouseY, partialTicks);
            this.buttonModalNext.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    public void updateScreen() {
        this.fieldExport.updateCursorCounter();
        this.fieldModalName.updateCursorCounter();
        this.fieldModalDesc.updateCursorCounter();
        this.fieldModalSettings.updateCursorCounter();
        super.updateScreen();
    }
    
    public void updateButtonValidity() {
        boolean editable = this.list.selected > -1 && this.presets.get(this.list.selected).custom;
        
        this.buttonSave.enabled = false;
        this.buttonEdit.enabled = false;
        this.buttonDelete.enabled = false;
        this.buttonSelect.enabled = false;
        this.buttonFilter.enabled = false;
        this.buttonCancel.enabled = false;
        this.buttonModalConfirm.visible = false;
        this.buttonModalCancel.visible = false;
        this.buttonModalPrev.visible = false;
        this.buttonModalNext.visible = false;
        this.buttonModalConfirm.enabled = false;
        this.buttonModalCancel.enabled = false;
        this.buttonModalPrev.enabled = false;
        this.buttonModalNext.enabled = false;
        this.fieldModalName.setVisible(false);
        this.fieldModalDesc.setVisible(false);
        this.fieldModalSettings.setVisible(false);
        
        switch (this.modalState) {
            case NONE:
                this.buttonSave.enabled = true;
                this.buttonEdit.enabled = editable;
                this.buttonDelete.enabled = editable;
                this.buttonSelect.enabled = this.hasValidSelection();
                this.buttonFilter.enabled = true;
                this.buttonCancel.enabled = true;
                break;
            case OVERWRITE:
                this.buttonModalConfirm.visible = true;
                this.buttonModalCancel.visible = true;
                this.buttonModalConfirm.enabled =  true;
                this.buttonModalCancel.enabled = true;
                break;
            case SAVE:
            case EDIT:
                this.buttonModalConfirm.visible = true;
                this.buttonModalCancel.visible = true;
                this.buttonModalPrev.visible = true;
                this.buttonModalNext.visible = true;
                this.buttonModalConfirm.enabled = !this.fieldModalName.getText().isEmpty();
                this.buttonModalCancel.enabled = true;
                this.buttonModalPrev.enabled = this.selectedIcon > 0;
                this.buttonModalNext.enabled = this.selectedIcon < ICON_IDENTIFIERS.length - 1;
                this.fieldModalName.setVisible(true);
                this.fieldModalDesc.setVisible(true);
                this.fieldModalSettings.setVisible(true);
                break;
            case DELETE:
                this.buttonModalConfirm.visible = true;
                this.buttonModalCancel.visible = true;
                this.buttonModalConfirm.enabled =  true;
                this.buttonModalCancel.enabled = true;
                break;
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clicked) throws IOException {
        super.mouseClicked(mouseX, mouseY, clicked);
        
        switch (this.modalState) {
            case NONE:
                this.fieldExport.mouseClicked(mouseX, mouseY, clicked);
                break;
            case SAVE:
            case EDIT:
                this.fieldModalName.mouseClicked(mouseX, mouseY, clicked);
                this.fieldModalDesc.mouseClicked(mouseX, mouseY, clicked);
                this.fieldModalSettings.mouseClicked(mouseX, mouseY, clicked);
                break;
            default:
        }
        
        this.updateButtonValidity();
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        boolean fieldTyped = 
            this.fieldExport.textboxKeyTyped(character, keyCode) ||
            this.fieldModalName.textboxKeyTyped(character, keyCode) ||
            this.fieldModalDesc.textboxKeyTyped(character, keyCode) ||
            this.fieldModalSettings.textboxKeyTyped(character, keyCode);
        
        if (!fieldTyped) {
            super.keyTyped(character, keyCode);
        }
        
        this.updateButtonValidity();
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        Info selectedPreset = this.presets.size() > 0 ? this.presets.get(this.list.selected > -1 ? this.list.selected : 0) : new Info();
        
        switch (guiButton.id) {
            case GUI_ID_FILTER:
                FilterType[] values = FilterType.values();
                int increment = GuiScreen.isShiftKeyDown() ? -1 : 1;
                int index = this.filterType.ordinal() + increment;
                if (index < 0) index = values.length - 1;
                FilterType filterType = values[index % values.length];
                
                this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, filterType, -1));
                break;
            case GUI_ID_SELECT:
                this.parent.loadValues(this.fieldExport.getText());
                this.parent.isSettingsModified();
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_CANCEL:
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_SAVE:
                this.fieldModalName.setText("");
                this.fieldModalDesc.setText("");
                this.fieldModalSettings.setText(this.getInitialExportText());
                this.selectedIcon = 0;
                this.updateModalState(ModalState.SAVE);
                break;
            case GUI_ID_EDIT:
                this.fieldModalName.setText(selectedPreset.name);
                this.fieldModalDesc.setText(selectedPreset.desc);
                this.fieldModalSettings.setText(selectedPreset.settings.toString());
                this.selectedIcon = MathHelper.clamp(this.dataHandler.getPreset(selectedPreset.name).icon, 0, ICON_IDENTIFIERS.length - 1);
                this.updateModalState(ModalState.EDIT);
                break;
            case GUI_ID_DELETE:
                this.updateModalState(ModalState.DELETE);
                break;
            case GUI_ID_MODAL_CONFIRM:
                switch (this.modalState) {
                    case SAVE:
                        if (this.dataHandler.containsPreset(this.fieldModalName.getText())) {
                            this.updateModalState(ModalState.OVERWRITE);
                        } else {
                            this.dataHandler.addPreset(this.selectedIcon, this.fieldModalName.getText(), this.fieldModalDesc.getText(), this.fieldModalSettings.getText());
                            this.dataHandler.writePresets();
                            this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, this.filterType, this.presets.size()));
                        }
                        break;
                    case DELETE:
                        this.dataHandler.removePreset(selectedPreset.name);
                        this.dataHandler.writePresets();
                        this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, this.filterType, this.list.selected - 1));
                        break;
                    case EDIT:
                        this.dataHandler.replacePreset(this.selectedIcon, this.fieldModalName.getText(), this.fieldModalDesc.getText(), this.fieldModalSettings.getText());
                        this.dataHandler.writePresets();
                        this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, this.filterType, this.list.selected));
                        break;
                    case OVERWRITE:
                        int ndx = this.dataHandler.replacePreset(this.selectedIcon, this.fieldModalName.getText(), this.fieldModalDesc.getText(), this.fieldModalSettings.getText());
                        this.dataHandler.writePresets();
                        this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, this.filterType, this.presets.size() - this.dataHandler.getPresets().size() + ndx));
                        break;
                    default:
                }
                break;
            case GUI_ID_MODAL_PREV:
                this.selectedIcon--;
                break;
            case GUI_ID_MODAL_NEXT:
                this.selectedIcon++;
                break;
            case GUI_ID_MODAL_CANCEL:
                this.updateModalState(ModalState.NONE);
                break;
        }
        
        this.updateModalButtons(this.modalState);
        this.updateButtonValidity();
    }
    
    private void drawSaveScreen(int centerX, int centerY, int modalWidth, int modalHeight) {
        int textStartX = centerX - modalWidth + 10;
        
        this.drawString(this.fontRenderer, I18n.format(PREFIX + "name"), textStartX, centerY - 60, 10526880);
        this.drawString(this.fontRenderer, I18n.format(PREFIX + "desc"), textStartX, centerY - 20, 10526880);
        this.drawString(this.fontRenderer, I18n.format(PREFIX + "settings"), textStartX, centerY + 20, 10526880);
        this.fieldModalName.drawTextBox();
        this.fieldModalDesc.drawTextBox();
        this.fieldModalSettings.drawTextBox();

        String nameNumChars = String.format("%d", MAX_PRESET_NAME_LENGTH - this.fieldModalName.getText().length());
        String descNumChars = String.format("%d", MAX_PRESET_DESC_LENGTH - this.fieldModalDesc.getText().length());
        
        int nameNumCharsLen = this.fontRenderer.getStringWidth(nameNumChars);
        int descNumCharsLen = this.fontRenderer.getStringWidth(descNumChars);
        
        int nameNumCharsCol = MAX_PRESET_NAME_LENGTH - this.fieldModalName.getText().length() > 4 ? 10526880 : 16752800;
        int descNumCharsCol = MAX_PRESET_DESC_LENGTH - this.fieldModalDesc.getText().length() > 4 ? 10526880 : 16752800;
        
        this.drawString(this.fontRenderer, nameNumChars, textStartX + MODAL_NAME_FIELD_LENGTH - nameNumCharsLen, centerY - 60, nameNumCharsCol);
        this.drawString(this.fontRenderer, descNumChars, textStartX + MODAL_DESC_FIELD_LENGTH - descNumCharsLen, centerY - 20, descNumCharsCol);
        
        int boxL = centerX + modalWidth - MODAL_ICON_SIZE - 1 - MODAL_ICON_PADDING_R;
        int boxR = centerX + modalWidth - 0 - MODAL_ICON_PADDING_R;
        int boxT = centerY - modalHeight - 0 + MODAL_ICON_PADDING_T;
        int boxB = centerY - modalHeight + MODAL_ICON_SIZE + 1 + MODAL_ICON_PADDING_T;

        this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "icon"), boxL + MODAL_ICON_SIZE / 2 + 1, boxT - 10, 10526880);
        this.drawHorizontalLine(boxL, boxR, boxT, -2039584);
        this.drawHorizontalLine(boxL, boxR, boxB, -6250336);
        this.drawVerticalLine(boxL, boxT, boxB, -2039584);
        this.drawVerticalLine(boxR, boxT, boxB, -6250336);
        
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(ICON_IDENTIFIERS[this.selectedIcon]);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(boxL + 1, boxB, 0.0).tex(0.0, 1.0).endVertex();
        bufferBuilder.pos(boxR, boxB, 0.0).tex(1.0, 1.0).endVertex();
        bufferBuilder.pos(boxR, boxT + 1, 0.0).tex(1.0, 0.0).endVertex();
        bufferBuilder.pos(boxL + 1, boxT + 1, 0.0).tex(0.0, 0.0).endVertex();
        
        tessellator.draw();
    }

    private List<Info> loadPresets(FilterType filterType, GuiCustomizePresetsDataHandler dataHandler) {
        List<Info> presets = new ArrayList<>();
        
        String name;
        String desc;
        ResourceLocation texture;
        ModernBetaGeneratorSettings.Factory factory;
        
        List<ResourceLocation> filteredKeys = ModernBetaClientRegistries.GUI_PRESET.getKeys()
            .stream()
            .filter(key -> {
                switch (filterType) {
                    case ALL:
                        return true;
                    case BUILTIN:
                        return key.getNamespace().equals(ModernBeta.MODID);
                    case ADDON:
                        return !key.getNamespace().equals(ModernBeta.MODID);
                    case CUSTOM:
                        return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        for (ResourceLocation key : filteredKeys) {
            GuiCustomizePreset preset = ModernBetaClientRegistries.GUI_PRESET.get(key);
            
            name = GuiCustomizePreset.formatName(key);
            desc = GuiCustomizePreset.formatInfo(key);
            texture = GuiCustomizePreset.formatTexture(key);
            factory = ModernBetaGeneratorSettings.Factory.jsonToFactory(preset.settings);
            
            presets.add(new Info(name, desc, texture, factory, false));
        }
    
        if (filterType == FilterType.ALL || filterType == FilterType.CUSTOM) {
            for (PresetData presetData : dataHandler.getPresets()) {
                name = presetData.name;
                desc = presetData.desc;
                texture = ICON_IDENTIFIERS[MathHelper.clamp(presetData.icon, 0, ICON_IDENTIFIERS.length - 1)];
                factory = ModernBetaGeneratorSettings.Factory.jsonToFactory(presetData.settings);
                
                presets.add(new Info(name, desc, texture, factory, true));
            }
        }
        
        return presets;
    }

    private GuiTextField createInitialField(GuiTextField textField, int id, int x, int y, int width, int height, String initialText, int maxLength) {
        GuiTextField newTextField = new GuiTextField(id, this.fontRenderer, x, y, width, height);
        boolean initialized = textField != null;
        
        boolean initialFocused = initialized ? textField.isFocused() : false;
        int initialCursorPos = initialized ? textField.getCursorPosition() : -1;
        
        newTextField.setMaxStringLength(maxLength);
        newTextField.setText(initialText);
        newTextField.setFocused(initialFocused);
        if (initialCursorPos != -1) newTextField.setCursorPosition(initialCursorPos);
        
        return newTextField;
    }

    private String getInitialExportText() {
        return this.fieldExport != null ?
            this.fieldExport.getText() :
            this.list != null && this.list.selected > -1 ?
                this.presets.get(this.list.selected).settings.toString() :
                this.parent.getSettingsString();
    }

    private String getFilterText() {
        return I18n.format(PREFIX_FILTER) + ": " + I18n.format(PREFIX_FILTER + "." + this.filterType.name().toLowerCase());
    }

    private void updateModalState(ModalState state) {
        if (this.modalState != state) {
            this.confirmExitTime = System.currentTimeMillis();
        }
        
        this.modalState = state;
    }
    
    private void updateModalButtons(ModalState state) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int modalWidth = state == ModalState.DELETE || state == ModalState.OVERWRITE ? MODAL_WIDTH_SMALL : MODAL_WIDTH;
        int modalHeight = state == ModalState.DELETE || state == ModalState.OVERWRITE ? MODAL_HEIGHT_SMALL : MODAL_HEIGHT;
        
        int buttonModalY = centerY + modalHeight - 25;
        int fieldModalX = centerX - modalWidth + 10;
        
        int boxL = centerX + modalWidth - MODAL_ICON_SIZE - 1 - MODAL_ICON_PADDING_R;
        int boxR = centerX + modalWidth - 0 - MODAL_ICON_PADDING_R;
        int boxB = centerY - modalHeight + MODAL_ICON_SIZE + 1 + MODAL_ICON_PADDING_T;
        
        this.buttonModalConfirm.y = buttonModalY;
        this.buttonModalCancel.y = buttonModalY;
        this.buttonModalPrev.x = boxL + 5;
        this.buttonModalPrev.y = boxB + 5;
        this.buttonModalNext.x = boxR - 23;
        this.buttonModalNext.y = boxB + 5;
        
        this.fieldModalName.x = fieldModalX;
        this.fieldModalDesc.x = fieldModalX;
        this.fieldModalSettings.x = fieldModalX;
    }

    private boolean hasValidSelection() {
        return (this.list.selected > -1 && this.list.selected < this.presets.size()) || this.fieldExport.getText().length() > 1;
    }
    
    private boolean canInteract() {
        return System.currentTimeMillis() - this.confirmExitTime > 100L;
    }
    
    @SideOnly(Side.CLIENT)
    private class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 66;
        private static final int LIST_PADDING_BOTTOM = 54;
        
        public int selected;
        
        public ListPreset(int selected) {
            super(
                GuiScreenCustomizePresets.this.mc,
                GuiScreenCustomizePresets.this.width,
                GuiScreenCustomizePresets.this.height,
                LIST_PADDING_TOP,
                GuiScreenCustomizePresets.this.height - LIST_PADDING_BOTTOM,
                SLOT_HEIGHT + SLOT_PADDING
            );
            
            this.selected = MathHelper.clamp(selected, -1, GuiScreenCustomizePresets.this.presets.size() - 1);
        }
        
        @Override
        public void handleMouseInput() {
            super.handleMouseInput();

            int paddingR = 0;
            int listL = (this.width - this.getListWidth()) / 2;
            int listR = (this.width + this.getListWidth()) / 2 + paddingR;
            int listMouseY = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
            int element = listMouseY / this.slotHeight;
            
            boolean inListBounds = this.isMouseYWithinSlotBounds(this.mouseY) && this.mouseY >= this.top && this.mouseY <= this.bottom;
            boolean inSlotBounds = this.mouseX >= listL && this.mouseX <= listR;

            if (inListBounds && inSlotBounds && listMouseY >= 0 && element < this.getSize()) {
                GuiScreenCustomizePresets.this.hoveredElement = element;
                GuiScreenCustomizePresets.this.hoveredTime = System.currentTimeMillis();
                
            } else {
                GuiScreenCustomizePresets.this.hoveredElement = -1;
                
            }
        }
        
        @Override
        public int getListWidth() {
            return 233;
        }
        
        @Override
        protected int getSize() {
            return GuiScreenCustomizePresets.this.presets.size();
        }
        
        @Override
        protected void elementClicked(int selected, boolean doubleClicked, int mouseX, int mouseY) {
            this.selected = selected;
            
            GuiScreenCustomizePresets.this.updateButtonValidity();
            GuiScreenCustomizePresets.this.fieldExport.setText(GuiScreenCustomizePresets.this.presets.get(GuiScreenCustomizePresets.this.list.selected).settings.toString());
            
            if (doubleClicked) {
                SoundUtil.playClickSound(this.mc.getSoundHandler());
                
                GuiScreenCustomizePresets.this.parent.loadValues(GuiScreenCustomizePresets.this.fieldExport.getText());
                GuiScreenCustomizePresets.this.parent.isSettingsModified();
                GuiScreenCustomizePresets.this.mc.displayGuiScreen(GuiScreenCustomizePresets.this.parent);
            } 
        }
        
        @Override
        protected boolean isSelected(int selected) {
            return selected == this.selected;
        }
        
        @Override
        protected void drawBackground() { }
        
        @Override
        protected void drawSelectionBox(int insideLeft, int insideTop, int mouseX, int mouseY, float partialTicks) {
            int size = this.getSize();
            int paddingL = 4;
            int paddingR = 0;
            int paddingY = 1;
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for (int preset = 0; preset < size; ++preset) {
                int y = insideTop + preset * this.slotHeight + this.headerPadding;
                int height = this.slotHeight - 4;

                if (y > this.bottom || y + height < this.top) {
                    this.updateItemPos(preset, insideLeft, y, partialTicks);
                }

                if (this.showSelectionBox && this.isSelected(preset)) {
                    int l = this.left + (this.width / 2 - this.getListWidth() / 2) + paddingL;
                    int r = this.left + this.width / 2 + this.getListWidth() / 2 + paddingR;
                    
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableTexture2D();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                    
                    bufferbuilder.pos((double)l, (double)(y + height + 2 + paddingY), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)r, (double)(y + height + 2 + paddingY), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                    
                    bufferbuilder.pos((double)r, (double)(y - 2 + paddingY), 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)l, (double)(y - 2 + paddingY), 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                    
                    bufferbuilder.pos((double)(l + 1), (double)(y + height + 1 + paddingY), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos((double)(r - 1), (double)(y + height + 1 + paddingY), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    
                    bufferbuilder.pos((double)(r - 1), (double)(y - 1 + paddingY), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos((double)(l + 1), (double)(y - 1 + paddingY), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                    
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                }

                this.drawSlot(preset, insideLeft, y, height, mouseX, mouseY, partialTicks);
            }
        }
        
        @Override
        protected void drawSlot(int preset, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
            Info info = GuiScreenCustomizePresets.this.presets.get(preset);
            int paddingY = 2;
            
            boolean hovered = GuiScreenCustomizePresets.this.hoveredElement == preset;
            int nameColor = hovered ? 16777120 : 16777215;
            int descColor = hovered ? 10526785 : 10526880;
            
            // Render preset icon
            this.blitIcon(x, y + paddingY, info.getTexture());
            
            // Render preset name
            GuiScreenCustomizePresets.this.fontRenderer.drawString(info.name, x + SLOT_HEIGHT + 10, y + 2 + paddingY, nameColor);
            
            // Render preset description, splitting if too long
            List<String> splitString = GuiScreenCustomizePresets.this.fontRenderer.listFormattedStringToWidth(info.desc, MAX_PRESET_DESC_LINE_LENGTH);
            if (splitString.size() > 1) {
                for (int i = 0; i < splitString.size(); ++i) {
                    String line = splitString.get(i);
                    GuiScreenCustomizePresets.this.fontRenderer.drawString(line, x + SLOT_HEIGHT + 10, y + 13 + paddingY + i * 10, descColor);
                }
                
            } else {
                GuiScreenCustomizePresets.this.fontRenderer.drawString(info.desc, x + SLOT_HEIGHT + 10, y + 13 + paddingY, descColor);
            }
        }

        private void blitIcon(int x, int y, ResourceLocation resourceLocation) {
            int iX = x + 5;
            int iY = y;
            
            GuiScreenCustomizePresets.this.drawHorizontalLine(iX - 1, iX + SLOT_HEIGHT, iY - 1, -2039584);
            GuiScreenCustomizePresets.this.drawHorizontalLine(iX - 1, iX + SLOT_HEIGHT, iY + SLOT_HEIGHT, -6250336);
            GuiScreenCustomizePresets.this.drawVerticalLine(iX - 1, iY - 1, iY + SLOT_HEIGHT, -2039584);
            GuiScreenCustomizePresets.this.drawVerticalLine(iX + SLOT_HEIGHT, iY - 1, iY + SLOT_HEIGHT, -6250336);
            
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(resourceLocation);
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferBuilder.pos(iX + 0, iY + SLOT_HEIGHT, 0.0).tex(0.0, 1.0).endVertex();
            bufferBuilder.pos(iX + SLOT_HEIGHT, iY + SLOT_HEIGHT, 0.0).tex(1.0, 1.0).endVertex();
            bufferBuilder.pos(iX + SLOT_HEIGHT, iY + 0, 0.0).tex(1.0, 0.0).endVertex();
            bufferBuilder.pos(iX + 0, iY + 0, 0.0).tex(0.0, 0.0).endVertex();
            
            tessellator.draw();
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static class Info {
        public final String name;
        public final String desc;
        public final ResourceLocation texture;
        public final ModernBetaGeneratorSettings.Factory settings;
        public final boolean custom;
        
        public Info() {
            this("", "",  null, null, false);
        }
        
        public Info(String name, String desc, ResourceLocation texture, ModernBetaGeneratorSettings.Factory factory, boolean custom) {
            this.name = name;
            this.desc = desc;
            this.texture = texture;
            this.settings = factory;
            this.custom = custom;
        }
        
        public ResourceLocation getTexture() {
            if (this.texture != null) {
                return this.texture;
            }
            
            return ICON_IDENTIFIERS[0];
        }
    }
}