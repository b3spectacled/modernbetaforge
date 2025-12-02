package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiCustomizePreset;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiCustomizePresetsDataHandler.PresetData;
import mod.bespectacled.modernbetaforge.client.gui.modal.GuiModalPreset;
import mod.bespectacled.modernbetaforge.client.gui.modal.GuiModalPreset.IconTexture;
import mod.bespectacled.modernbetaforge.client.gui.modal.GuiModalPreset.State;
import mod.bespectacled.modernbetaforge.client.gui.modal.GuiModalPresetConfirm;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.SoundUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.SimpleTexture;
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
    
    private static final ResourceLocation UNKNOWN_PACK = new ResourceLocation("textures/misc/unknown_pack.png");
    
    private static final String PREFIX = "createWorld.customize.presets.modernbetaforge.";
    private static final String PREFIX_FILTER = "createWorld.customize.presets.modernbetaforge.filter";
    
    private static final int SLOT_HEIGHT = 32;
    private static final int SLOT_PADDING = 6;
    private static final int MAX_PRESET_DESC_LINE_LENGTH = 188;
    private static final int BUTTON_SPACE = 4;
    private static final int BUTTON_SMALL_WIDTH = 80;
    private static final int BUTTON_LARGE_WIDTH = BUTTON_SMALL_WIDTH * 2 + BUTTON_SPACE;
    
    private static final int GUI_ID_FILTER = 0;
    private static final int GUI_ID_SELECT = 1;
    private static final int GUI_ID_CANCEL = 2;
    private static final int GUI_ID_SAVE = 3;
    private static final int GUI_ID_EDIT = 4;
    private static final int GUI_ID_DELETE = 5;
    private static final int GUI_ID_EXPORT = 6;
    
    private final GuiScreenCustomizeWorld parent;
    private final FilterType filterType;
    private final GuiCustomizePresetsDataHandler dataHandler;
    private final List<Info> presets;
    private final int initialPreset;
    
    protected String title;
    
    private ListPreset list;
    private GuiTextField fieldExport;
    private GuiButton buttonSave;
    private GuiButton buttonEdit;
    private GuiButton buttonDelete;
    private GuiButton buttonFilter;
    private GuiButton buttonSelect;
    private GuiButton buttonCancel;
    private String shareText;
    private int hoveredElement;
    @SuppressWarnings("unused") private long hoveredTime;
    private int amountScrolled;
    private boolean isFocused;
    
    public GuiScreenCustomizePresets(GuiScreenCustomizeWorld parent) {
        this(parent, ModernBetaConfig.guiOptions.defaultPresetFilter, -1, 0);
    }
    
    public GuiScreenCustomizePresets(GuiScreenCustomizeWorld parent, FilterType filterType, int initialPreset, int amountScrolled) {
        this.title = I18n.format(PREFIX + "title");
        this.parent = parent;
        this.filterType = filterType;
        this.dataHandler = new GuiCustomizePresetsDataHandler();
        this.presets = this.loadPresets(filterType, this.dataHandler);
        this.initialPreset = initialPreset;
        
        this.hoveredElement = -1;
        this.amountScrolled = amountScrolled;
        this.isFocused = true;
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        
        int centerX = this.width / 2;
        
        int selectX = centerX + BUTTON_SPACE / 2;
        int filterX = centerX + BUTTON_SPACE / 2;
        int cancelX = centerX + BUTTON_SMALL_WIDTH + BUTTON_LARGE_WIDTH - BUTTON_SMALL_WIDTH * 2 + BUTTON_SPACE / 2;
        int saveX = centerX - BUTTON_LARGE_WIDTH - BUTTON_SPACE / 2;
        int editX = centerX - BUTTON_LARGE_WIDTH - BUTTON_SPACE / 2;
        int deleteX = centerX - BUTTON_SMALL_WIDTH - BUTTON_SPACE / 2;
        
        this.buttonList.clear();
        this.buttonSelect = this.addButton(new GuiButton(GUI_ID_SELECT, selectX, this.height - 50, BUTTON_LARGE_WIDTH, 20, I18n.format(PREFIX + "select")));
        this.buttonFilter = this.addButton(new GuiButton(GUI_ID_FILTER, filterX, this.height - 27, BUTTON_SMALL_WIDTH, 20, this.getFilterText()));
        this.buttonCancel = this.addButton(new GuiButton(GUI_ID_CANCEL, cancelX, this.height - 27, BUTTON_SMALL_WIDTH, 20, I18n.format("gui.cancel")));
        this.buttonSave = this.addButton(new GuiButton(GUI_ID_SAVE, saveX, this.height - 50, BUTTON_LARGE_WIDTH, 20, I18n.format(PREFIX + "save")));
        this.buttonEdit = this.addButton(new GuiButton(GUI_ID_EDIT, editX, this.height - 27, BUTTON_SMALL_WIDTH, 20, I18n.format(PREFIX + "edit")));
        this.buttonDelete = this.addButton(new GuiButton(GUI_ID_DELETE, deleteX, this.height - 27, BUTTON_SMALL_WIDTH, 20, I18n.format(PREFIX + "delete")));
        
        this.shareText = I18n.format(PREFIX + "share");
        this.list = this.list != null ? new ListPreset(this, this.list.selected) : new ListPreset(this, this.initialPreset);
        this.list.scrollBy(this.amountScrolled);
        
        String initialExportText = this.getInitialSettings();
        this.fieldExport = this.createInitialField(this.fieldExport, GUI_ID_EXPORT, 50, 40, this.width - 100, 20, initialExportText, ModernBetaGeneratorSettings.MAX_PRESET_LENGTH);
        
        this.updateButtonValidity();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        this.list.handleMouseInput();
        this.amountScrolled = this.list.getAmountScrolled();
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
    }
    
    @Override
    public void updateScreen() {
        this.fieldExport.updateCursorCounter();
        super.updateScreen();
    }

    public String getSelectedPresetName() {
        return this.getSelectedPreset().name;
    }
    
    public String getSelectedPresetDesc() {
        return this.getSelectedPreset().desc;
    }
    
    public String getSelectedPresetSettings() {
        return this.getSelectedPreset().settings.toString();
    }
    
    public int getSelectedPresetIcon() {
        return this.dataHandler.getPreset(this.getSelectedPreset().name).icon;
    }

    public String getInitialSettings() {
        return this.fieldExport != null ?
            this.fieldExport.getText() :
            this.list != null && this.list.selected > -1 ?
                this.presets.get(this.list.selected).settings.toString() :
                this.parent.getSettingsString();
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clicked) throws IOException {
        super.mouseClicked(mouseX, mouseY, clicked);
        
        this.fieldExport.mouseClicked(mouseX, mouseY, clicked);
        this.updateButtonValidity();
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);
        this.fieldExport.textboxKeyTyped(character, keyCode);
        
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
                
                this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, filterType, -1, 0));
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
                Consumer<GuiModalPreset> onConfirmSave = modal -> {
                    if (this.dataHandler.containsPreset(modal.getNameText())) {
                        Consumer<GuiModalPresetConfirm> onConfirmOverwrite = modalOverwrite -> {
                            int ndx = this.dataHandler.replacePreset(modal.getIcon(), modal.getNameText(), modal.getNameText(), modal.getDescText(), modal.getSettingsText());
                            this.dataHandler.writePresets();
                            this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, this.filterType, this.presets.size() - this.dataHandler.getPresets().size() + ndx, this.amountScrolled));
                        };
                        List<String> textList = Arrays.asList(I18n.format(PREFIX + "overwrite.confirm"), modal.getNameText());
                        List<Integer> textColors = Arrays.asList(16752800, 16777215);
                        
                        this.mc.displayGuiScreen(new GuiModalPresetConfirm(this, I18n.format(PREFIX + "overwrite.title"), onConfirmOverwrite, modalOverwrite -> this.isFocused = true, textList, textColors));
                    } else {
                        this.dataHandler.addPreset(modal.getIcon(), modal.getNameText(), modal.getDescText(), modal.getSettingsText());
                        this.dataHandler.writePresets();
                        this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, this.filterType, this.presets.size(), this.amountScrolled));
                    }
                };
                
                this.isFocused = false;
                this.mc.displayGuiScreen(new GuiModalPreset(this, onConfirmSave, modal -> this.isFocused = true, State.SAVE));
                break;
            case GUI_ID_EDIT:
                Consumer<GuiModalPreset> onConfirmEdit = modal -> {
                    this.dataHandler.replacePreset(modal.getIcon(), selectedPreset.name, modal.getNameText(), modal.getDescText(), modal.getSettingsText());
                    this.dataHandler.writePresets();
                    this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, this.filterType, this.list.selected, this.amountScrolled));
                };
                
                this.isFocused = false;
                this.mc.displayGuiScreen(new GuiModalPreset(this, onConfirmEdit, modal -> isFocused = true, State.EDIT));
                break;
            case GUI_ID_DELETE:
                Consumer<GuiModalPresetConfirm> onConfirmDelete = modal -> {
                    this.dataHandler.removePreset(selectedPreset.name);
                    this.dataHandler.writePresets();
                    this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, this.filterType, this.list.selected - 1, this.amountScrolled));
                };
                List<String> textList = Arrays.asList(I18n.format(PREFIX + "delete.confirm"), selectedPreset.name);
                List<Integer> textColors = Arrays.asList(16752800, 16777215);
                
                this.isFocused = false;
                this.mc.displayGuiScreen(new GuiModalPresetConfirm(this, I18n.format(PREFIX + "delete.title"), onConfirmDelete, modal -> this.isFocused = true, textList, textColors));
                break;
        }
        
        this.updateButtonValidity();
    }

    private List<Info> loadPresets(FilterType filterType, GuiCustomizePresetsDataHandler dataHandler) {
        List<Info> presets = new ArrayList<>();
        
        String name;
        String desc;
        IconTexture texture;
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
            texture = new IconTexture(GuiCustomizePreset.formatTexture(key));
            factory = ModernBetaGeneratorSettings.Factory.jsonToFactory(preset.settings);
            
            presets.add(new Info(name, desc, texture, factory, false));
        }
    
        if (filterType == FilterType.ALL || filterType == FilterType.CUSTOM) {
            for (PresetData presetData : dataHandler.getPresets()) {
                name = presetData.name;
                desc = presetData.desc;
                texture = GuiModalPreset.getIconTexture(presetData.icon);
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

    private String getFilterText() {
        return I18n.format(PREFIX_FILTER) + ": " + I18n.format(PREFIX_FILTER + "." + this.filterType.name().toLowerCase());
    }

    private void updateButtonValidity() {
        boolean editable = this.list.selected > -1 && this.presets.get(this.list.selected).custom;
        
        this.buttonSave.enabled = this.isFocused;
        this.buttonEdit.enabled = this.isFocused && editable;
        this.buttonDelete.enabled = this.isFocused && editable;
        this.buttonSelect.enabled = this.isFocused && this.hasValidSelection();
        this.buttonFilter.enabled = this.isFocused;
        this.buttonCancel.enabled = this.isFocused;
        this.fieldExport.setEnabled(this.isFocused);
        this.fieldExport.setFocused(this.fieldExport.isFocused());
    }

    private boolean hasValidSelection() {
        return (this.list.selected > -1 && this.list.selected < this.presets.size()) || this.fieldExport.getText().length() > 1;
    }
    
    private Info getSelectedPreset() {
        return this.presets.size() > 0 ? this.presets.get(this.list.selected > -1 ? this.list.selected : 0) : new Info();
    }
    
    @SideOnly(Side.CLIENT)
    private static class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 66;
        private static final int LIST_PADDING_BOTTOM = 54;
        
        private final GuiScreenCustomizePresets parent;
        public int selected;
        
        public ListPreset(GuiScreenCustomizePresets parent, int selected) {
            super(
                parent.mc,
                parent.width,
                parent.height,
                LIST_PADDING_TOP,
                parent.height - LIST_PADDING_BOTTOM,
                SLOT_HEIGHT + SLOT_PADDING
            );
            
            this.parent = parent;
            this.selected = MathHelper.clamp(selected, -1, parent.presets.size() - 1);
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
                this.parent.hoveredElement = element;
                this.parent.hoveredTime = System.currentTimeMillis();
                
            } else {
                this.parent.hoveredElement = -1;
                
            }
        }
        
        @Override
        public int getListWidth() {
            return 233;
        }
        
        @Override
        protected int getSize() {
            return this.parent.presets.size();
        }
        
        @Override
        protected void elementClicked(int selected, boolean doubleClicked, int mouseX, int mouseY) {
            this.selected = selected;
            
            this.parent.updateButtonValidity();
            this.parent.fieldExport.setText(this.parent.presets.get(this.parent.list.selected).settings.toString());
            
            if (doubleClicked) {
                SoundUtil.playClickSound(this.mc.getSoundHandler());
                
                this.parent.parent.loadValues(this.parent.fieldExport.getText());
                this.parent.parent.isSettingsModified();
                this.parent.mc.displayGuiScreen(this.parent.parent);
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
            Info info = this.parent.presets.get(preset);
            int paddingY = 2;
            
            boolean hovered = this.parent.hoveredElement == preset;
            int nameColor = hovered ? 16777120 : 16777215;
            int descColor = hovered ? 10526785 : 10526880;
            
            int iX = x + 5;
            int iY = y + paddingY;
            
            // Cull if not in list frame
            if (iY + SLOT_HEIGHT <= LIST_PADDING_TOP || iY >= this.parent.height - LIST_PADDING_BOTTOM) {
                return;
            }
            
            // Render preset icon
            this.blitIcon(iX, iY, info.icon());
            
            // Render preset name
            this.parent.fontRenderer.drawString(info.name, x + SLOT_HEIGHT + 10, y + 2 + paddingY, nameColor);
            
            // Render preset description, splitting if too long
            List<String> splitString = this.parent.fontRenderer.listFormattedStringToWidth(info.desc, MAX_PRESET_DESC_LINE_LENGTH);
            if (splitString.size() > 1) {
                for (int i = 0; i < splitString.size(); ++i) {
                    String line = splitString.get(i);
                    this.parent.fontRenderer.drawString(line, x + SLOT_HEIGHT + 10, y + 13 + paddingY + i * 10, descColor);
                }
                
            } else {
                this.parent.fontRenderer.drawString(info.desc, x + SLOT_HEIGHT + 10, y + 13 + paddingY, descColor);
            }
        }

        private void blitIcon(int x, int y, IconTexture icon) {
            this.parent.drawHorizontalLine(x - 1, x + SLOT_HEIGHT, y - 1, -2039584);
            this.parent.drawHorizontalLine(x - 1, x + SLOT_HEIGHT, y + SLOT_HEIGHT, -6250336);
            this.parent.drawVerticalLine(x - 1, y - 1, y + SLOT_HEIGHT, -2039584);
            this.parent.drawVerticalLine(x + SLOT_HEIGHT, y - 1, y + SLOT_HEIGHT, -6250336);
            
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            
            SimpleTexture iconTexture = new SimpleTexture(icon.identifier);
            if (!this.mc.getTextureManager().loadTexture(icon.identifier, iconTexture)) {
                icon.identifier = UNKNOWN_PACK;
            }
            
            this.mc.getTextureManager().bindTexture(icon.identifier);
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferBuilder.pos(x + 0, y + SLOT_HEIGHT, 0.0).tex(icon.u, icon.v + icon.h).endVertex();
            bufferBuilder.pos(x + SLOT_HEIGHT, y + SLOT_HEIGHT, 0.0).tex(icon.u + icon.w, icon.v + icon.h).endVertex();
            bufferBuilder.pos(x + SLOT_HEIGHT, y + 0, 0.0).tex(icon.u + icon.w, icon.v).endVertex();
            bufferBuilder.pos(x + 0, y + 0, 0.0).tex(icon.u, icon.v).endVertex();
            
            tessellator.draw();
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static class Info {
        public final String name;
        public final String desc;
        public final IconTexture icon;
        public final ModernBetaGeneratorSettings.Factory settings;
        public final boolean custom;
        
        public Info() {
            this("", "",  null, null, false);
        }
        
        public Info(String name, String desc, IconTexture icon, ModernBetaGeneratorSettings.Factory factory, boolean custom) {
            this.name = name;
            this.desc = desc;
            this.icon = icon;
            this.settings = factory;
            this.custom = custom;
        }
        
        public IconTexture icon() {
            if (this.icon != null) {
                return this.icon;
            }
            
            return GuiModalPreset.getIconTexture(0);
        }
    }
}