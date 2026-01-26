package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeWorld;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModalConfirmSettings extends GuiModal<GuiModalConfirmSettings> {
    private static final Gson GSON = new Gson();
    
    private static final String PREFIX_ADDON = "createWorld.customize.custom.";
    private static final String PREFIX = String.format("createWorld.customize.custom.%s.", ModernBeta.MODID);
    private static final String GUI_LABEL_DISCARD = I18n.format(PREFIX + "discard");
    
    private static final int MODAL_WIDTH = 300;
    private static final int MODAL_HEIGHT = 200;
    private static final int LIST_PADDING_TOP = 24;
    private static final int LIST_PADDING_BOTTOM = 27;
    private static final int LIST_SLOT_HEIGHT = 32;
    private static final int GUI_ID_DISCARD = 2;
    
    private final Consumer<GuiModalConfirmSettings> onDiscard;
    private final Map<String, Tuple<JsonElement, JsonElement>> changeMap;
    private final List<String> modIds;
    
    private ChangeList changeList;

    public GuiModalConfirmSettings(GuiScreenCustomizeWorld parent, String title, Consumer<GuiModalConfirmSettings> onConfirm, Consumer<GuiModalConfirmSettings> onCancel, Consumer<GuiModalConfirmSettings> onDiscard) {
        super(parent, title, MODAL_WIDTH, MODAL_HEIGHT, onConfirm, onCancel);

        this.onDiscard = onDiscard;
        this.changeMap = this.createChangeMap(parent.getPreviousSettingsString(), parent.getSettingsString());
        this.modIds = this.getModIds(this.changeMap);
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
    
    private Map<String, Tuple<JsonElement, JsonElement>> createChangeMap(String prevStr, String nextStr) {
        JsonObject prev = GSON.fromJson(prevStr, JsonObject.class);
        JsonObject next = GSON.fromJson(nextStr, JsonObject.class);
        
        Map<String, Tuple<JsonElement, JsonElement>> changeMap = new LinkedHashMap<>();
        for (Entry<String, JsonElement> entry : prev.entrySet()) {
            String key = entry.getKey();
            JsonElement prevSetting = prev.get(key);
            JsonElement nextSetting = next.get(key);
            
            if (!prevSetting.equals(nextSetting)) {
                ModernBeta.log(Level.DEBUG, "SETTING CHANGED: " + key);
                changeMap.put(key, new Tuple<>(prevSetting, nextSetting));
            }
        }
        
        return changeMap;
    }
    
    private List<String> getModIds(Map<String, Tuple<JsonElement, JsonElement>> changeMap) {
        List<String> modIds = new ArrayList<>();
        modIds.add(ModernBeta.MODID);
        
        for (String key : changeMap.keySet()) {
            if (isResourceFormat(key)) {
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
        
        this.modalHeight = (int)(this.height * 0.8);
        
        int modalL = centerX - this.modalWidth / 2;
        int modalR = centerX + this.modalWidth / 2;
        int modalT = centerY - this.modalHeight / 2 + LIST_PADDING_TOP;
        int modalB = centerY + this.modalHeight / 2 - LIST_PADDING_BOTTOM;
        
        this.changeList = new ChangeList(this, modalT, modalB, LIST_SLOT_HEIGHT);
        this.changeList.left = modalL;
        this.changeList.right = modalR;
    }
    
    private static boolean isResourceFormat(String resourceString) {
        return resourceString.split(":").length == 2;
    }
    
    @SideOnly(Side.CLIENT)
    private static class ChangeList extends GuiSlot {
        private final GuiModalConfirmSettings parent;
        private final List<ChangeListEntry> changeList;
        
        public ChangeList(GuiModalConfirmSettings parent, int top, int bottom, int slotHeight) {
            super(parent.mc, parent.width, parent.height, top, bottom, slotHeight);
            
            this.parent = parent;
            this.changeList = this.createChangeList();
        }
        
        @Override
        public void handleMouseInput() {
            super.handleMouseInput();
            
            if (this.isMouseYWithinSlotBounds(this.mouseY)) {
                //int slotIndex = this.getSlotIndexFromScreenCoords(this.mouseX, this.mouseY);
            }
        }
        
        @Override
        public int getListWidth() {
            return super.getListWidth() + 30;
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
            double r = this.left + this.parent.modalWidth;
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
            
            ChangeListEntry entry = this.changeList.get(slotIndex);
            
            if (entry.isTitle) {
                String title = entry.title;
                int titleX = this.width / 2;
                
                this.parent.drawCenteredString(this.parent.fontRenderer, I18n.format(PREFIX_ADDON + title), titleX, textY, 16777215);
                
            } else {
                String key = entry.entry.getKey();
                String modId = isResourceFormat(key) ? key.split(":")[0] : ModernBeta.MODID;
                String modSetting = isResourceFormat(key) ? key.split(":")[1] : key;
                 
                String setting = I18n.format(PREFIX_ADDON + String.format("%s.%s", modId, modSetting)) + ":";
                setting = setting.trim();
                
                String settingTrimmed = this.parent.fontRenderer.trimStringToWidth(setting, this.getListWidth());
                if (!setting.equals(settingTrimmed)) {
                    settingTrimmed = settingTrimmed + TextFormatting.RESET + "...";
                }
                
                int settingX = this.width / 2 - this.parent.modalWidth / 2 + 10;
                int settingY = y - 5;
                
                String arrow = TextFormatting.RESET + "" + TextFormatting.BOLD + " \u2192 ";
                String change0 = TextFormatting.RED + entry.entry.getValue().getFirst().getAsString();
                String change1 = TextFormatting.GREEN + entry.entry.getValue().getSecond().getAsString();
                String changes = change0 + arrow + change1;
                changes = changes.trim();
                
                String changesTrimmed = this.parent.fontRenderer.trimStringToWidth(changes, this.getListWidth());
                if (!changes.equals(changesTrimmed)) {
                    changesTrimmed = changesTrimmed + TextFormatting.RESET + "...";
                }
                
                int changeX = settingX;
                int changeY = y + 10;
                
                this.parent.drawString(this.parent.fontRenderer, settingTrimmed, settingX, settingY, 16777215);
                this.parent.drawString(this.parent.fontRenderer, changesTrimmed, changeX, changeY, 16777215);
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
        
        @SideOnly(Side.CLIENT)
        private static class ChangeListEntry {
            private final boolean isTitle;
            private final String title;
            private final Entry<String, Tuple<JsonElement, JsonElement>> entry;
            
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
    }
}
