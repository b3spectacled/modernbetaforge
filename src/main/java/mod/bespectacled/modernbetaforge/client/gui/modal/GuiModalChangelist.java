package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.property.Property;
import mod.bespectacled.modernbetaforge.client.gui.GuiColors;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeWorld;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeWorld.NameFormatterPropertyVisitor;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.util.PresetUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModalChangelist extends GuiModal<GuiModalChangelist> {
    private static final Map<String, Formatter> FORMATTERS;
    
    private static final String PREFIX = "createWorld.customize.custom";
    private static final String GUI_LABEL_DISCARD = I18n.format(String.format("%s.%s.%s", PREFIX, ModernBeta.MODID, "discard"));
    
    private static final TextFormatting FORMATTING_PREV = TextFormatting.DARK_RED;
    private static final TextFormatting FORMATTING_NEXT = TextFormatting.DARK_GREEN;
    
    private static final int MODAL_MAX_WIDTH = 500;
    private static final int MODAL_MIN_WIDTH = 300;
    private static final int MODAL_HEIGHT = 200;
    private static final int LIST_PADDING_TOP = 24;
    private static final int LIST_PADDING_BOTTOM = 27;
    private static final int LIST_SLOT_HEIGHT = 32;
    private static final int LIST_WIDTH_OFFSET = 25;
    private static final int GUI_ID_DISCARD = 2;
    
    private final Map<ResourceLocation, Property<?>> prevProperties;
    private final Map<ResourceLocation, Property<?>> nextProperties;
    
    private final Consumer<GuiModalChangelist> onDiscard;
    private final Map<String, Tuple<JsonElement, JsonElement>> changeMap;
    private final List<String> modIds;
    
    private ChangeList changeList;

    public GuiModalChangelist(GuiScreenCustomizeWorld parent, String title, Consumer<GuiModalChangelist> onConfirm, Consumer<GuiModalChangelist> onCancel, Consumer<GuiModalChangelist> onDiscard) {
        super(parent, title, MODAL_MIN_WIDTH, MODAL_HEIGHT, onConfirm, onCancel);
        
        String prevString = parent.getPreviousSettingsString();
        String nextString = parent.getSettingsString();
        
        this.prevProperties = ModernBetaGeneratorSettings.Factory.jsonToFactory(prevString).customProperties;
        this.nextProperties = ModernBetaGeneratorSettings.Factory.jsonToFactory(nextString).customProperties;

        this.onDiscard = onDiscard;
        this.changeMap = this.createChangeMap(prevString, nextString);
        this.modIds = this.getModIds(this.changeMap);
    }
    
    @Override
    public void initGui() {
        // Short circuit if change map is empty (should only happen if only hidden items were changed)
        if (this.changeMap.size() == 0) {
            this.onDiscard.accept(this);
        }
        
        super.initGui();
        
        this.modalWidth = MathHelper.clamp((int)(this.width * 0.8), MODAL_MIN_WIDTH, MODAL_MAX_WIDTH);
        this.modalHeight = (int)(this.height * 0.8);
        
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
        this.drawCenteredString(this.fontRenderer, this.title, titleX, titleY, GuiColors.RGB_WHITE);
        
        if (this.changeList.hoveredSlot >= 0) {
            ChangeListEntry listEntry = this.changeList.changeList.get(this.changeList.hoveredSlot);
            boolean shouldDraw = false;
            
            String key = listEntry.entry.getKey();
            String modId = isResourceFormat(key) ? key.split(":")[0] : ModernBeta.MODID;
            String modSetting = isResourceFormat(key) ? key.split(":")[1] : key;
            
            String setting = I18n.format(String.format("%s.%s.%s", PREFIX, modId, modSetting)) + ":";
            setting = setting.trim();

            Tuple<JsonElement, JsonElement> entryValue = listEntry.entry.getValue();
            String arrow = TextFormatting.RESET + "" + TextFormatting.BOLD + " \u2192 ";
            String change0 = FORMATTING_PREV + this.formatValue(modId, modSetting, entryValue.getFirst(), this.prevProperties);
            String change1 = FORMATTING_NEXT + this.formatValue(modId, modSetting, entryValue.getSecond(), this.nextProperties);
            String changes = change0 + arrow + change1;
            changes = changes.trim();
            
            String changesTrimmed = this.fontRenderer.trimStringToWidth(changes, this.changeList.getListWidth());
            if (!changes.equals(changesTrimmed)) {
                shouldDraw = true;

                changesTrimmed = this.fontRenderer.trimStringToWidth(changes, 2500);
                if (!changes.equals(changesTrimmed)) {
                    changes = changesTrimmed + TextFormatting.RESET + "...";
                }
            }
            
            if (shouldDraw) {
                List<String> hoverTexts = new ArrayList<>();
                hoverTexts.add(setting);
                hoverTexts.add(changes);
                
                this.parent.drawHoveringText(hoverTexts, mouseX, mouseY);
            }
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
            case GUI_ID_DISCARD:
                this.onDiscard.accept(this);
                break;
        }
    }
    
    private Map<String, Tuple<JsonElement, JsonElement>> createChangeMap(String prevStr, String nextStr) {
        JsonObject prev = PresetUtil.readPresetAsJson(prevStr);
        JsonObject next = PresetUtil.readPresetAsJson(nextStr);
        
        Map<String, Tuple<JsonElement, JsonElement>> changeMap = new LinkedHashMap<>();
        for (Entry<String, JsonElement> entry : prev.entrySet()) {
            String key = entry.getKey();
            JsonElement prevSetting = prev.get(key);
            JsonElement nextSetting = next.get(key);
            
            if (!prevSetting.equals(nextSetting)) {
                changeMap.put(key, new Tuple<>(prevSetting, nextSetting));
            }
        }
        
        return changeMap;
    }
    
    private List<String> getModIds(Map<String, Tuple<JsonElement, JsonElement>> changeMap) {
        List<String> modIds = new ArrayList<>();
        
        for (String key : changeMap.keySet()) {
            if (!isResourceFormat(key)) {
                if (!modIds.contains(ModernBeta.MODID)) {
                    modIds.add(ModernBeta.MODID);
                }
            } else {
                String namespace = key.split(":")[0];
                
                if (!modIds.contains(namespace)) {
                    modIds.add(namespace);
                }
            }
        }
        
        return modIds;
    }
    
    private void createChangeList() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int modalL = centerX - this.modalWidth / 2;
        int modalR = centerX + this.modalWidth / 2;
        int modalT = centerY - this.modalHeight / 2 + LIST_PADDING_TOP;
        int modalB = centerY + this.modalHeight / 2 - LIST_PADDING_BOTTOM;
        
        this.changeList = new ChangeList(this, modalT, modalB, LIST_SLOT_HEIGHT);
        this.changeList.left = modalL;
        this.changeList.right = modalR;
    }
    
    private String formatValue(String modId, String modSetting, JsonElement element, Map<ResourceLocation, Property<?>> properties) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsJsonPrimitive().getAsBoolean() ? I18n.format("gui.yes") : I18n.format("gui.no");
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return this.formatString(modId, modSetting, element.getAsString(), properties);
        }
        
        return element.getAsString();
    }
    
    private String formatString(String modId, String modSetting, String value, Map<ResourceLocation, Property<?>> properties) {
        ResourceLocation registryKey = new ResourceLocation(modId, modSetting);
        
        if (modId.equals(ModernBeta.MODID) && FORMATTERS.containsKey(modSetting)) {
            return FORMATTERS.get(modSetting).apply(modId, modSetting, value);
        } else if (properties.containsKey(registryKey)) {
            Property<?> property = properties.get(registryKey);
            return property.visitNameFormatter(new NameFormatterPropertyVisitor(), registryKey);
        }
        
        return value;
    }

    private static String getFormattedRegistryString(String modId, String modSetting, String value) {
        String valueNamespace = value.split(":")[0];
        String valuePath = value.split(":")[1];
        
        return I18n.format(String.format("%s.%s.%s.%s.%s", PREFIX, modId, modSetting, valueNamespace, valuePath));
    }
    
    private static String getFormattedMiscString(String modId, String modSetting, String value) {
        return I18n.format(String.format("%s.%s.%s.%s", PREFIX, modId, modSetting, value));
    }
    
    private static String getFormattedForgeBiomeString(String modId, String modSetting, String value) {
        return ForgeRegistries.BIOMES.getValue(new ResourceLocation(value)).getBiomeName();
    }
    
    private static String getFormattedForgeBlockString(String modId, String modSetting, String value) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(value)).getLocalizedName();
    }
    
    private static String getFormattedForgeFluidString(String modId, String modSetting, String value) {
        return ForgeRegistryUtil.getFluidLocalizedName(new ResourceLocation(value));
    }
    
    private static boolean isResourceFormat(String resourceString) {
        return resourceString.split(":").length == 2;
    }
    
    @SideOnly(Side.CLIENT)
    private static class ChangeList extends GuiSlot {
        private final GuiModalChangelist parent;
        private final List<ChangeListEntry> changeList;
        
        private int hoveredSlot;
        
        public ChangeList(GuiModalChangelist parent, int top, int bottom, int slotHeight) {
            super(parent.mc, parent.width, parent.height, top, bottom, slotHeight);
            
            this.parent = parent;
            this.changeList = this.createChangeList();
            this.hoveredSlot = -1;
        }
        
        @Override
        public void handleMouseInput() {
            super.handleMouseInput();
            
            this.hoveredSlot = -1;
            if (this.isMouseYWithinSlotBounds(this.mouseY)) {
                int slotIndex = this.getSlotIndexFromScreenCoords(this.mouseX, this.mouseY);
                
                if (slotIndex >= 0 && slotIndex < this.changeList.size()) {
                    ChangeListEntry listEntry = this.changeList.get(slotIndex);
                    
                    if (!listEntry.isTitle) {
                        this.hoveredSlot = slotIndex;
                    }
                }
            }
        }
        
        @Override
        public int getListWidth() {
            return this.parent.modalWidth - LIST_WIDTH_OFFSET;
        }
        
        @Override
        public int getSlotIndexFromScreenCoords(int posX, int posY) {
            int l = this.left;
            int r = this.right;
            int y = posY - this.top - this.headerPadding + (int)this.amountScrolled + 5;
            int slotIndex = y / this.slotHeight;
            
            return posX < this.getScrollBarX() && posX >= l && posX <= r && slotIndex >= 0 && y >= 0 && slotIndex < this.getSize() ? slotIndex : -1;
        }
                
        @Override
        protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) { 
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.mc.getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

            int centerY = this.parent.height / 2;
            double f = 32.0;
            double l = this.left;
            double r = this.left + this.parent.modalWidth - 1; // Offset by one to prevent edge overlap with unusual widths
            double t = startY == 0 ? centerY - this.parent.modalHeight / 2 : startY;
            double b = endY == this.height ? centerY + this.parent.modalHeight / 2 : endY;
            
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(l, b, 0.0).tex(0.0, b / f).color(64, 64, 64, endAlpha).endVertex();
            bufferbuilder.pos(r, b, 0.0).tex(this.parent.modalWidth / f, b / f).color(64, 64, 64, endAlpha).endVertex();
            bufferbuilder.pos(r, t, 0.0).tex(this.parent.modalWidth / f, t / f).color(64, 64, 64, startAlpha).endVertex();
            bufferbuilder.pos(l, t, 0.0).tex(0.0, t / f).color(64, 64, 64, startAlpha).endVertex();
            tessellator.draw();
        }

        @Override
        protected void drawBackground() { }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) { }

        @Override
        protected int getSize() {
            return this.parent.changeMap.size() + this.parent.modIds.size();
        }
        
        @Override
        protected int getScrollBarX() {
            return this.width / 2 + this.parent.modalWidth / 2 - 7;
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return false;
        }

        @Override
        protected void drawSlot(int slotIndex, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
            int centerY = this.parent.height / 2;
            int topY = centerY - this.parent.modalHeight / 2 + LIST_PADDING_TOP;
            int bottomY = centerY + this.parent.modalHeight / 2 - LIST_PADDING_BOTTOM;
            int fontHeight = this.parent.fontRenderer.FONT_HEIGHT;
            
            int textY = y + this.slotHeight / 2 - fontHeight;
            
            if (textY + fontHeight < topY || textY - fontHeight > bottomY) {
                return;
            }
            
            ChangeListEntry listEntry = this.changeList.get(slotIndex);
            
            if (listEntry.isTitle) {
                String title = listEntry.title;
                int titleX = this.width / 2;
                
                this.parent.drawCenteredString(this.parent.fontRenderer, I18n.format(PREFIX + "." + title), titleX, textY, GuiColors.RGB_WHITE);
                
            } else {
                String key = listEntry.entry.getKey();
                String modId = isResourceFormat(key) ? key.split(":")[0] : ModernBeta.MODID;
                String modSetting = isResourceFormat(key) ? key.split(":")[1] : key;

                String setting = I18n.format(String.format("%s.%s.%s", PREFIX, modId, modSetting)) + ":";
                setting = setting.trim();
                
                String settingTrimmed = this.parent.fontRenderer.trimStringToWidth(setting, this.getListWidth());
                if (!setting.equals(settingTrimmed)) {
                    settingTrimmed = settingTrimmed + TextFormatting.RESET + "...";
                }
                
                int settingX = this.width / 2 - this.parent.modalWidth / 2 + 10;
                int settingY = y - 5;
                
                Tuple<JsonElement, JsonElement> entryValue = listEntry.entry.getValue();
                String arrow = TextFormatting.RESET + "" + TextFormatting.BOLD + " \u2192 ";
                String change0 = FORMATTING_PREV + this.parent.formatValue(modId, modSetting, entryValue.getFirst(), this.parent.prevProperties);
                String change1 = FORMATTING_NEXT + this.parent.formatValue(modId, modSetting, entryValue.getSecond(), this.parent.nextProperties);
                String changes = change0 + arrow + change1;
                changes = changes.trim();
                
                String changesTrimmed = this.parent.fontRenderer.trimStringToWidth(changes, this.getListWidth());
                if (!changes.equals(changesTrimmed)) {
                    changesTrimmed += TextFormatting.RESET + "...";
                }
                
                int changeX = settingX;
                int changeY = y + 10;
                
                this.parent.drawString(this.parent.fontRenderer, settingTrimmed, settingX, settingY, GuiColors.RGB_WHITE);
                this.parent.drawString(this.parent.fontRenderer, changesTrimmed, changeX, changeY, GuiColors.RGB_WHITE);
            }
        }
        
        private List<ChangeListEntry> createChangeList() {
            List<ChangeListEntry> changeList = new ArrayList<>();
            
            for (int i = 0; i < this.parent.modIds.size(); ++i) {
                String modId = this.parent.modIds.get(i);
                changeList.add(new ChangeListEntry(modId));
                
                for (String key : this.parent.changeMap.keySet()) {
                    if (modId.equals(ModernBeta.MODID) && !isResourceFormat(key) || modId.equals(key.split(":")[0])) {
                        changeList.add(new ChangeListEntry(new SimpleEntry<>(key, this.parent.changeMap.get(key))));
                    }
                }
            }
            
            return changeList;
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static class ChangeListEntry {
        public final boolean isTitle;
        public final String title;
        public final Entry<String, Tuple<JsonElement, JsonElement>> entry;
        
        public ChangeListEntry(String title) {
            this.isTitle = true;
            this.title = title;
            this.entry = null;
        }
        
        public ChangeListEntry(Entry<String, Tuple<JsonElement, JsonElement>> entry) {
            this.isTitle = false;
            this.title = null;
            this.entry = entry;
        }
    }

    @SideOnly(Side.CLIENT)
    @FunctionalInterface
    private static interface Formatter {
        String apply(String modId, String modSetting, String value);
    }
    
    static {
        FORMATTERS = ImmutableMap.<String, Formatter>builder()
            .put(NbtTags.CHUNK_SOURCE, GuiModalChangelist::getFormattedRegistryString)
            .put(NbtTags.BIOME_SOURCE, GuiModalChangelist::getFormattedRegistryString)
            .put(NbtTags.SURFACE_BUILDER, GuiModalChangelist::getFormattedRegistryString)
            .put(NbtTags.CAVE_CARVER, GuiModalChangelist::getFormattedRegistryString)
            .put(NbtTags.WORLD_SPAWNER, GuiModalChangelist::getFormattedRegistryString)
            .put(NbtTags.LEVEL_THEME, GuiModalChangelist::getFormattedMiscString)
            .put(NbtTags.LEVEL_TYPE, GuiModalChangelist::getFormattedMiscString)
            .put(NbtTags.LEVEL_HOUSE, GuiModalChangelist::getFormattedMiscString)
            .put(NbtTags.LAYER_TYPE, GuiModalChangelist::getFormattedMiscString)
            .put(NbtTags.ORE_TYPE, GuiModalChangelist::getFormattedMiscString)
            .put(NbtTags.SINGLE_BIOME, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.DEFAULT_BLOCK, GuiModalChangelist::getFormattedForgeBlockString)
            .put(NbtTags.DEFAULT_FLUID, GuiModalChangelist::getFormattedForgeFluidString)
            .put(NbtTags.DESERT_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.DESERT_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.DESERT_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.FOREST_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.FOREST_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.FOREST_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.ICE_DESERT_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.ICE_DESERT_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.ICE_DESERT_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.PLAINS_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.PLAINS_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.PLAINS_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.RAINFOREST_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.RAINFOREST_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.RAINFOREST_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SAVANNA_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SAVANNA_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SAVANNA_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SHRUBLAND_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SHRUBLAND_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SHRUBLAND_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SEASONAL_FOREST_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SEASONAL_FOREST_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SEASONAL_FOREST_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SWAMPLAND_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SWAMPLAND_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.SWAMPLAND_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.TAIGA_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.TAIGA_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.TAIGA_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.TUNDRA_BIOME_BASE, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.TUNDRA_BIOME_OCEAN, GuiModalChangelist::getFormattedForgeBiomeString)
            .put(NbtTags.TUNDRA_BIOME_BEACH, GuiModalChangelist::getFormattedForgeBiomeString)
            .build();
    }
}
