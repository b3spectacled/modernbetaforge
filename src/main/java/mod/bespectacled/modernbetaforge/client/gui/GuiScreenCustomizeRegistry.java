package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.lwjgl.input.Keyboard;

import mod.bespectacled.modernbetaforge.util.SoundUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizeRegistry extends GuiScreen {
    private static final String PREFIX_REGISTRY = "createWorld.customize.registry.modernbetaforge.";
    private static final String PREFIX_SETTINGS = "createWorld.customize.custom.modernbetaforge.";
    
    private static final int DEFAULT_SLOT_HEIGHT = 32;
    private static final int MAX_SEARCH_LENGTH = 40;
    private static final int SEARCH_BAR_LENGTH = 360;
    
    private static final int GUI_ID_SELECT = 0;
    private static final int GUI_ID_CANCEL = 1;
    private static final int GUI_ID_SEARCH = 2;
    private static final int GUI_ID_RESET = 3;
    
    private final GuiScreenCustomizeWorld parent;
    private final BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer;
    private final Function<ResourceLocation, String> nameFormatter;
    private final int slotHeight;
    private final boolean displayIcons;
    private final String initialEntry;
    private final boolean startSearchFocused;
    
    private final String langName;
    private final List<ResourceLocation> registryKeys;
    private final List<Info> entries;
    
    protected String title;

    private ModernBetaGeneratorSettings.Factory settings;
    private ListPreset list;
    private GuiTextField fieldSearch;
    private GuiButton buttonSelect;
    private GuiButton buttonSearch;
    private GuiButton buttonReset;
    private String searchText;
    private String searchEntry;
    private int hoveredElement;
    @SuppressWarnings("unused") private long hoveredTime;
    private int prevMouseX;
    private int prevMouseY;
    
    public GuiScreenCustomizeRegistry(
        GuiScreenCustomizeWorld parent,
        BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer,
        Function<ResourceLocation, String> nameFormatter,
        int slotHeight,
        String initialEntry,
        String langName,
        List<ResourceLocation> registryKeys
    ) {
        this(parent, consumer, nameFormatter, slotHeight, false, initialEntry, "", false, langName, registryKeys);
    }
    
    public GuiScreenCustomizeRegistry(
        GuiScreenCustomizeWorld parent,
        BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer,
        Function<ResourceLocation, String> nameFormatter,
        String initialEntry,
        String langName,
        List<ResourceLocation> registryKeys
    ) {
        this(parent, consumer, nameFormatter, DEFAULT_SLOT_HEIGHT, false, initialEntry, "", false, langName, registryKeys);
    }
    
    public GuiScreenCustomizeRegistry(
        GuiScreenCustomizeWorld parent,
        BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer,
        Function<ResourceLocation, String> nameFormatter,
        int slotHeight,
        boolean displayIcons,
        String initialEntry,
        String searchEntry,
        boolean startSearchFocused,
        String langName,
        List<ResourceLocation> registryKeys
    ) {
        this.title = String.format(
            "%s %s",
            I18n.format(PREFIX_REGISTRY + "title"),
            I18n.format(PREFIX_SETTINGS + langName)
        );
        this.parent = parent;
        this.consumer = consumer;
        this.nameFormatter = nameFormatter;
        this.slotHeight = slotHeight;
        this.displayIcons = displayIcons;
        this.initialEntry = initialEntry;
        this.searchEntry = searchEntry;
        this.startSearchFocused = startSearchFocused;
        
        this.langName = langName;
        this.registryKeys = registryKeys;
        this.entries = this.loadEntries();
        this.hoveredElement = -1;
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        
        this.buttonList.clear();
        this.buttonSelect = this.addButton(new GuiButton(GUI_ID_SELECT, this.width / 2 - 122, this.height - 27, 120, 20, I18n.format(PREFIX_REGISTRY + "select") + " " + I18n.format(PREFIX_SETTINGS + langName)));
        this.buttonList.add(new GuiButton(GUI_ID_CANCEL, this.width / 2 + 3, this.height - 27, 120, 20, I18n.format("gui.cancel")));
        this.buttonSearch = this.addButton(new GuiButton(GUI_ID_SEARCH, this.width / 2 + SEARCH_BAR_LENGTH / 2 - 100, 40, 50, 20, I18n.format(PREFIX_REGISTRY + "search")));
        this.buttonReset = this.addButton(new GuiButton(GUI_ID_RESET, this.width / 2 + SEARCH_BAR_LENGTH / 2 - 50, 40, 50, 20, I18n.format(PREFIX_REGISTRY + "reset")));
        
        this.searchText = I18n.format(PREFIX_REGISTRY + "search.info");
        
        this.settings = ModernBetaGeneratorSettings.Factory.jsonToFactory(this.parent.getSettingsString());
        this.list = this.list != null ? new ListPreset(this, this.list.selected) : new ListPreset(this, this.initialEntry);
        
        int slotHeight = this.slotHeight;
        int slotSelected = this.list.selected;
        int slotsDisplayed = (this.list.height - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM) / slotHeight;

        if (slotSelected > slotsDisplayed - 1) {
            this.list.scrollBy(slotHeight * (slotSelected - slotsDisplayed) + slotHeight * slotsDisplayed);
        }
        
        this.fieldSearch = new GuiTextField(5, this.fontRenderer, this.width / 2 - SEARCH_BAR_LENGTH / 2, 40, SEARCH_BAR_LENGTH, 20);
        this.fieldSearch.setMaxStringLength(MAX_SEARCH_LENGTH);
        this.fieldSearch.setText(this.searchEntry);
        this.fieldSearch.setFocused(this.startSearchFocused);
        
        this.updateButtonValidity();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.fieldSearch.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 12, 16777215);
        this.drawString(this.fontRenderer, this.searchText, this.width / 2 - SEARCH_BAR_LENGTH / 2, 30, 10526880);
    
        /*
        if (this.hoveredElement != -1) {
            String tooltip = this.entries.get(this.hoveredElement).tooltip;
            
            if (!tooltip.isEmpty() && System.currentTimeMillis() - this.hoveredTime > 500L) {
                this.drawHoveringText(this.fontRenderer.listFormattedStringToWidth(tooltip, 120), mouseX, mouseY);
            }
        }
        */
    }
    
    @Override
    public void updateScreen() {
        this.fieldSearch.updateCursorCounter();
        super.updateScreen();
    }
    
    public void updateButtonValidity() {
        this.buttonSelect.enabled = this.hasValidSelection();
        this.buttonSearch.enabled = !this.fieldSearch.getText().isEmpty();
        this.buttonReset.enabled = true;
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clicked) throws IOException {
        this.fieldSearch.mouseClicked(mouseX, mouseY, clicked);
        
        super.mouseClicked(mouseX, mouseY, clicked);
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!this.fieldSearch.textboxKeyTyped(character, keyCode)) {
            super.keyTyped(character, keyCode);
        }
        
        if (this.fieldSearch.isFocused() && (keyCode == 28 || keyCode == 156) && !this.fieldSearch.getText().isEmpty()) {
            SoundUtil.playClickSound(this.mc.getSoundHandler());
            this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(
                this.parent,
                this.consumer,
                this.nameFormatter,
                this.slotHeight,
                this.displayIcons,
                this.initialEntry,
                this.fieldSearch.getText(),
                true,
                this.langName,
                this.registryKeys
            ));
        }
        
        this.searchEntry = this.fieldSearch.getText();
        this.updateButtonValidity();
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_SELECT:
                this.consumer.accept(this.entries.get(this.list.selected).registryName, this.settings);
                this.parent.loadValues(this.settings.toString());
                this.parent.setSettingsModified(!this.settings.equals(this.parent.getDefaultSettings()));
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_CANCEL:
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_SEARCH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(
                    this.parent,
                    this.consumer,
                    this.nameFormatter,
                    this.slotHeight,
                    this.displayIcons,
                    this.initialEntry,
                    this.fieldSearch.getText(),
                    this.fieldSearch.isFocused() && !this.fieldSearch.getText().isEmpty(),
                    this.langName,
                    this.registryKeys
                ));
                break;
            case GUI_ID_RESET:
                this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(
                    this.parent,
                    this.consumer,
                    this.nameFormatter,
                    this.slotHeight,
                    this.displayIcons,
                    this.initialEntry,
                    "",
                    false,
                    this.langName,
                    this.registryKeys
                ));
                break;
        }
    }

    private boolean hasValidSelection() {
        return this.list.selected > -1 && this.list.selected < this.entries.size();
    }
    
    private List<Info> loadEntries() {
        List<Info> entries = new ArrayList<>();
        
        for (ResourceLocation registryKey : this.registryKeys) {
            String formattedName = this.nameFormatter.apply(registryKey);
            
            if (this.searchEntry == null ||
                this.searchEntry.isEmpty() ||
                formattedName.toLowerCase().contains(this.searchEntry.toLowerCase()) ||
                registryKey.toString().toLowerCase().contains(this.searchEntry.toLowerCase())
            ) {
                entries.add(new Info(formattedName, registryKey.toString()));
            }
        }
        
        return entries;
    }
    
    @SideOnly(Side.CLIENT)
    private static class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 66;
        private static final int LIST_PADDING_BOTTOM = 32;
        
        private final GuiScreenCustomizeRegistry parent;
        public int selected;
        
        public ListPreset(GuiScreenCustomizeRegistry parent, int initialEntry) {
            super(
                parent.mc,
                parent.width,
                parent.height,
                LIST_PADDING_TOP,
                parent.height - LIST_PADDING_BOTTOM,
                parent.slotHeight
            );
            
            this.parent = parent;
            this.selected = initialEntry;
        }
        
        public ListPreset(GuiScreenCustomizeRegistry parent, String initialEntry) {
            super(
                parent.mc,
                parent.width,
                parent.height,
                LIST_PADDING_TOP,
                parent.height - LIST_PADDING_BOTTOM,
                parent.slotHeight
            );

            this.parent = parent;
            this.selected = -1;
            
            for (int i = 0; i < parent.entries.size(); ++i) {
                Info info = parent.entries.get(i);
                
                if (info.registryName.equals(initialEntry.toString())) {
                    this.selected = i;
                }
            }
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
                
            } else {
                this.parent.hoveredElement = -1;
                
            }
        }
        
        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            
            if (this.parent.hoveredElement != -1 && mouseX != this.parent.prevMouseX && mouseY != this.parent.prevMouseY) {
                this.parent.hoveredTime = System.currentTimeMillis();
                this.parent.prevMouseX = mouseX;
                this.parent.prevMouseY = mouseY;
            }
        }
        
        @Override
        protected int getSize() {
            return this.parent.entries.size();
        }
        
        @Override
        protected void elementClicked(int selected, boolean doubleClicked, int mouseX, int mouseY) {
            this.selected = selected;
            this.parent.updateButtonValidity();
            
            if (doubleClicked) {
                SoundUtil.playClickSound(this.mc.getSoundHandler());

                this.parent.consumer.accept(this.parent.entries.get(this.parent.list.selected).registryName, this.parent.settings);
                this.parent.parent.loadValues(this.parent.settings.toString());
                this.parent.parent.setSettingsModified(!this.parent.settings.equals(this.parent.parent.getDefaultSettings()));
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
            int paddingL = 4;
            int paddingR = 0;
            int paddingY = 1;
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for (int entry = 0; entry < this.getSize(); ++entry) {
                int y = insideTop + entry * this.slotHeight + this.headerPadding;
                int height = this.slotHeight - 4;

                if (y > this.bottom || y + height < this.top) {
                    this.updateItemPos(entry, insideLeft, y, partialTicks);
                }

                if (this.showSelectionBox && this.isSelected(entry)) {
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

                this.drawSlot(entry, insideLeft, y, height, mouseX, mouseY, partialTicks);
            }
        }
        
        @Override
        protected void drawSlot(int entry, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
            Info info = this.parent.entries.get(entry);
            int paddingL = 6;
            int paddingY = 3;
            
            boolean hovered = this.parent.hoveredElement == entry;
            int nameColor = hovered ? 16777120 : 16777215;
            int registryNameColor = hovered ? 10526785 : 10526880;
            
            // Render name
            this.parent.fontRenderer.drawString(info.name, x + paddingL, y + paddingY, nameColor);
            
            // Render registry name
            this.parent.fontRenderer.drawString(info.registryName, x + paddingL, y + 12 + paddingY, registryNameColor);
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static class Info {
        public String name;
        public String registryName;

        public Info(String name, String registryName) {
            this.name = name;
            this.registryName = registryName;
        }
    }
}
;