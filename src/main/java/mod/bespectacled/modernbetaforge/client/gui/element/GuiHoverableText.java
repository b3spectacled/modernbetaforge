package mod.bespectacled.modernbetaforge.client.gui.element;

import java.util.List;

import mod.bespectacled.modernbetaforge.client.gui.GuiColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHoverableText extends Gui {
    private static final ResourceLocation TOOLTIP_BACKGROUND = new ResourceLocation("textures/blocks/cobblestone.png");
    private static final int TOOLTIP_LINE_SPACING = 3;
    private static final long TOOLTIP_DELAY = 250L;
    
    private static boolean hoveredOnce = false;
    
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String text;
    private final List<String> tooltips;
    private final GuiBoundsChecker bounds;
    private final FontRenderer fontRenderer;
    
    private long lastHovered;
    
    public GuiHoverableText(Minecraft mc, int x, int y, String text, List<String> tooltips) {
        this.x = x;
        this.y = y;
        this.width = mc.fontRenderer.getStringWidth(text);
        this.height = mc.fontRenderer.FONT_HEIGHT;
        this.text = text;
        this.tooltips = tooltips;
        this.bounds = new GuiBoundsChecker();
        this.fontRenderer = mc.fontRenderer;
    }
    
    public void draw(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        FontRenderer fontRenderer = mc.fontRenderer;
        boolean lastHovered = this.bounds.isHovered();
        
        this.bounds.updateBounds(this.x, this.y, this.width, this.height);
        this.bounds.updateHovered(mouseX, mouseY);

        int textColor = this.bounds.isHovered() ?
            GuiColors.RGB_LIGHT_YELLOW :
            (System.currentTimeMillis() / 500) % 2 == 0 || hoveredOnce ? GuiColors.RGB_GREY : GuiColors.RGB_LIGHT_GREY;
        
        if (this.bounds.isHovered()) {
            if (!lastHovered) {
                this.lastHovered = System.currentTimeMillis();
            }
            
            if (System.currentTimeMillis() > this.lastHovered + TOOLTIP_DELAY) {
                this.drawTooltip(mc);
            }
            
            if (!hoveredOnce) {
                hoveredOnce = true;
            }
        }
        
        fontRenderer.drawStringWithShadow(this.text, this.x, this.y, textColor);
    }
    
    private void drawTooltip(Minecraft mc) {
        if (this.tooltips == null || this.tooltips.isEmpty()) {
            return;
        }
        
        FontRenderer fontRenderer = mc.fontRenderer;
        
        int paddingL = 5;
        int paddingT = 5;
        int paddingR = 2;
        int paddingB = 3;
        
        int tooltipHeight = fontRenderer.FONT_HEIGHT * this.tooltips.size() + TOOLTIP_LINE_SPACING * (this.tooltips.size() - 1);
        int tooltipWidth = this.getMaxStringWidth(this.tooltips);
        
        boolean drawLeft = this.x - tooltipWidth < 0;
        
        int rectH = tooltipHeight + paddingT + paddingB;
        int rectW = tooltipWidth + paddingL + paddingR;
        
        int tooltipX = this.x + (drawLeft ? this.width + 2 : -rectW - 4);
        int tooltipY = this.y;
       
        int rectL = tooltipX;
        int rectR = tooltipX + rectW;
        int rectT = tooltipY;
        int rectB = tooltipY + rectH;
        
        int texL = rectL + 1;
        int texR = rectR;
        int texT = rectT + 1;
        int texB = rectB;
        
        double texU = (texR - texL) * 0.03125;
        double texV = (texB - texT) * 0.03125;
        
        this.drawHorizontalLine(rectL, rectR, rectT, GuiColors.ARGB_LIGHT_GREY);
        this.drawHorizontalLine(rectL, rectR, rectB, GuiColors.ARGB_DARK_GREY);
        this.drawVerticalLine(rectL, rectT, rectB, GuiColors.ARGB_LIGHT_GREY);
        this.drawVerticalLine(rectR, rectT, rectB, GuiColors.ARGB_DARK_GREY);
        
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        
        mc.getTextureManager().bindTexture(TOOLTIP_BACKGROUND);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferBuilder.pos(texL, texB, 0.0).tex(0.0, texV).color(64, 64, 64, 64).endVertex();
        bufferBuilder.pos(texR, texB, 0.0).tex(texU, texV).color(64, 64, 64, 64).endVertex();
        bufferBuilder.pos(texR, texT, 0.0).tex(texU, 0.0).color(64, 64, 64, 64).endVertex();
        bufferBuilder.pos(texL, texT, 0.0).tex(0.0, 0.0).color(64, 64, 64, 64).endVertex();
        
        tessellator.draw();

        this.drawSplitString(this.tooltips, rectL + paddingL, rectT + paddingT, GuiColors.RGB_WHITE);
    }
    
    private int getMaxStringWidth(List<String> strings) {
        int maxWidth = 0;
        
        for (String s : strings) {
            int width = this.fontRenderer.getStringWidth(s); 
            
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        
        return maxWidth;
    }

    private void drawSplitString(List<String> strings, int x, int y, int color) {
        for (String str : strings) {
            this.fontRenderer.drawStringWithShadow(str, x, y, color);
            y += this.fontRenderer.FONT_HEIGHT + TOOLTIP_LINE_SPACING;
        }
    }
}
