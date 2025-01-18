package mod.bespectacled.modernbetaforge.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.util.ExecutorWrapper;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizePreview extends GuiScreen implements GuiResponder, FormatHelper {
    private enum ProgressState {
        IDLE, STARTED, FAILED, SUCCEEDED;
    }
    
    private static final String PREFIX = "createWorld.customize.preview.";
    private static final int TEXTURE_Y_OFFSET = 10;
    private static final int HINT_TEXT_OFFSET = 20;
    private static final int PROGRESS_TEXT_OFFSET = 7;
    
    private static final int GUI_ID_BIOME_COLORS = 0;
    private static final int GUI_ID_RESOLUTION = 1;
    private static final int GUI_ID_GENERATE = 2;
    private static final int GUI_ID_CANCEL = 3;
    
    private static final float MIN_RES = 128.0f;
    
    private final GuiScreenCustomizeWorld parent;
    private final String worldSeed;
    private final int maxResolution;
    private final ModernBetaGeneratorSettings settings;
    private final ExecutorWrapper executor;
    private final ResourceLocation mapLocation;
    private final BoundChecker boundsChecker;
    private final MutableBlockPos mutablePos;
    
    private final ChunkSource chunkSource;
    private final BiomeSource biomeSource;
    private final SurfaceBuilder surfaceBuilder;
    private final BiomeInjectionRules injectionRules;
    
    private GuiListButton biomeColors;
    private GuiSlider resolutionSlider;
    private GuiButton generate;
    private GuiButton cancel;
    private ListPreset list;
    private BufferedImage mapImage;
    private DynamicTexture mapTexture;
    private ProgressState state;
    private int resolution;
    private boolean useBiomeColors;
    private float progress;
    private String hintText;
    private String progressText;
    
    protected String title;

    public GuiScreenCustomizePreview(GuiScreenCustomizeWorld parent, String worldSeed, int maxResolution, ModernBetaGeneratorSettings settings) {
        this.title = I18n.format(PREFIX + "title");
        this.parent = parent;
        this.worldSeed = worldSeed;
        this.maxResolution = maxResolution;
        this.settings = settings;
        this.executor = new ExecutorWrapper(1, "map_preview");
        this.mapLocation = ModernBeta.createRegistryKey("map_preview");
        this.boundsChecker = new BoundChecker();
        this.mutablePos = new MutableBlockPos();
        
        long seed = new Random().nextLong();
        if (!this.worldSeed.isEmpty()) {
            try {
                seed = Longs.tryParse(this.worldSeed);
                
            } catch (Exception e) {
                seed = this.worldSeed.hashCode();
                
            }
        }
        
        ResourceLocation chunkKey = new ResourceLocation(settings.chunkSource);
        ResourceLocation biomeKey = new ResourceLocation(settings.biomeSource);
        ResourceLocation surfaceKey = new ResourceLocation(settings.surfaceBuilder);
        
        this.chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(chunkKey).apply(seed, this.settings);
        this.biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(biomeKey).apply(seed, this.settings);
        this.surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER.get(surfaceKey).apply(this.chunkSource, this.settings);
        this.injectionRules = this.chunkSource.buildBiomeInjectorRules(this.biomeSource);
        
        this.state = ProgressState.IDLE;
        this.resolution = 512;
        this.useBiomeColors = true;
        this.progress = 0.0f;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        this.biomeColors = this.addButton(new GuiListButton(this, GUI_ID_BIOME_COLORS, this.width / 2 - 187, this.height - 27, I18n.format(PREFIX + "biomeColors"), true));
        this.resolutionSlider = this.addButton(new GuiSlider(this, GUI_ID_RESOLUTION, this.width / 2 - 92, this.height - 27, PREFIX + "resolution", MIN_RES, this.maxResolution, this.resolution, this));
        this.generate = this.addButton(new GuiButton(GUI_ID_GENERATE, this.width / 2 + 3, this.height - 27, 90, 20, I18n.format(PREFIX + "generate")));
        this.cancel =  this.addButton(new GuiButton(GUI_ID_CANCEL, this.width / 2 + 98, this.height - 27, 90, 20, I18n.format("gui.cancel")));
        
        this.biomeColors.width = 90;
        this.resolutionSlider.width = 90;
        this.biomeColors.setValue(this.useBiomeColors);
        
        this.hintText = I18n.format(PREFIX + "hint");
        this.progressText = "";
        
        this.list = new ListPreset();

        int viewportSize = Math.min(this.list.height, this.list.width) - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM - 32;
        int textureX = this.width / 2 - viewportSize / 2;
        int textureY = this.height / 2 - viewportSize / 2;
        textureY -= TEXTURE_Y_OFFSET;
        
        this.boundsChecker.updateBounds(textureX, textureY, viewportSize, viewportSize);
        this.updateButtonsEnabled(this.state);
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
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 14, 16777215);

        int viewportSize = Math.min(this.list.height, this.list.width) - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM - 32;
        int textureX = this.width / 2 - viewportSize / 2;
        int textureY = this.height / 2 - viewportSize / 2;
        textureY -= TEXTURE_Y_OFFSET;

        drawRect(textureX, textureY, textureX + viewportSize, textureY + viewportSize, MathUtil.convertARGBComponentsToInt(50, 0, 0, 0));
        this.drawHorizontalLine(textureX - 1, textureX + viewportSize, textureY - 1, -2039584);
        this.drawHorizontalLine(textureX - 1, textureX + viewportSize, textureY + viewportSize, -6250336);
        this.drawVerticalLine(textureX - 1, textureY - 1, textureY + viewportSize, -2039584);
        this.drawVerticalLine(textureX + viewportSize, textureY - 1, textureY + viewportSize, -6250336);
        
        this.loadMapTexture();
        switch(this.state) {
            case SUCCEEDED:
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(this.mapLocation);
                GlStateManager.enableBlend();
                Gui.drawModalRectWithCustomSizedTexture(
                    textureX,
                    textureY,
                    0.0f,
                    0.0f,
                    viewportSize,
                    viewportSize,
                    viewportSize,
                    viewportSize
                );
                GlStateManager.disableBlend();
                
                this.drawCenteredString(fontRenderer, "N", this.width / 2 + 2, this.height / 2 - viewportSize / 2 + 2 - TEXTURE_Y_OFFSET, 16776960);
                this.drawCenteredString(fontRenderer, "S", this.width / 2 + 2, this.height / 2 + viewportSize / 2 - 9 - TEXTURE_Y_OFFSET, 16776960);
                this.drawCenteredString(fontRenderer, "E", this.width / 2 + viewportSize / 2 - 4, this.height / 2 - 3 - TEXTURE_Y_OFFSET, 16776960);
                this.drawCenteredString(fontRenderer, "W", this.width / 2 - viewportSize / 2 + 5, this.height / 2 - 3 - TEXTURE_Y_OFFSET, 16776960);
                break;
                
            case STARTED:
                this.hintText = I18n.format(PREFIX + "progress");
                this.progressText = String.format("%d%%", (int)(this.progress * 100.0));
                
                this.drawCenteredString(this.fontRenderer, this.hintText, this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, 16777215);
                this.drawCenteredString(this.fontRenderer, this.progressText, this.width / 2, this.height / 2 - PROGRESS_TEXT_OFFSET, 16777215);
                break;
                
            case FAILED:
                this.hintText = I18n.format(PREFIX + "failure");
                
                this.drawCenteredString(this.fontRenderer, this.hintText, this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, 16777215);
                break;
                
            default:
                this.hintText = I18n.format(PREFIX + "hint");
                
                this.drawCenteredString(this.fontRenderer, this.hintText, this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, 16777215);
        }
        
        String seedTest = String.format("%s: %s", I18n.format(PREFIX + "seed"), this.worldSeed);
        this.drawCenteredString(this.fontRenderer, seedTest, this.width / 2, this.height / 2 + viewportSize / 2, 16777215);
        
        if (this.state == ProgressState.SUCCEEDED && this.boundsChecker.inBounds(mouseX, mouseY)) {
            float x = this.boundsChecker.getRelativeX(mouseX) / (float)viewportSize * this.resolution;
            float y = this.boundsChecker.getRelativeY(mouseY) / (float)viewportSize * this.resolution;

            x -= (float)this.resolution / 2f;
            y -= (float)this.resolution / 2f;
            
            int height = this.chunkSource.getHeight((int)x, (int)y, HeightmapChunk.Type.SURFACE);
            Biome biome = this.biomeSource.getBiome((int)x, (int)y);
            
            boolean inWater = height < chunkSource.getSeaLevel() - 1;
            IBlockState state = inWater ? BlockStates.WATER : BlockStates.STONE;
            IBlockState stateAbove = inWater ? BlockStates.WATER : BlockStates.AIR;
            
            BiomeInjectionContext context = new BiomeInjectionContext(this.mutablePos.setPos(x, height, y), state, stateAbove, biome);
            Biome injectedBiome = this.injectionRules.test(context, (int)x, (int)y, BiomeInjectionStep.PRE_SURFACE);
            biome = injectedBiome != null ? injectedBiome : biome;
            
            String coordinateText = String.format("%d, %d, %d", (int)x, height, (int)y);
            String biomeText = biome.getBiomeName();
            
            this.drawHoveringText(ImmutableList.of(coordinateText, biomeText), mouseX, mouseY);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
    }
    
    @Override
    public String getText(int entry, String entryString, float entryValue) {
        return String.format("%s: %d", entryString, (int)entryValue);
    }

    @Override
    public void setEntryValue(int id, boolean value) { 
        if (id == GUI_ID_BIOME_COLORS) {
            this.useBiomeColors = value;        
        }
    }

    @Override
    public void setEntryValue(int id, float value) {
        if (id == GUI_ID_RESOLUTION) {
            this.resolution = (int)value;
        }
    }

    @Override
    public void setEntryValue(int id, String value) { }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE && this.state == ProgressState.STARTED) {
            return;
        }
        
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_GENERATE:
                this.drawTerrainMap();
                break;
            case GUI_ID_CANCEL:
                this.executor.shutdown();
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
    
    private void drawTerrainMap() {
        this.mapImage = null;
        this.progress = 0.0f;
        this.state = ProgressState.STARTED;
        this.updateButtonsEnabled(this.state);
        this.deleteMapTexture();
        
        Runnable runnable = () -> {
            try {
                ModernBeta.log(Level.DEBUG, String.format("Drawing terrain map of size %s", this.resolution));
                this.mapImage = DrawUtil.createTerrainMapForPreview(
                    this.chunkSource,
                    this.biomeSource,
                    this.surfaceBuilder,
                    this.resolution,
                    this.useBiomeColors,
                    current -> this.progress = current
                );
                ModernBeta.log(Level.DEBUG, "Finished drawing terrain map!");
                this.state = ProgressState.SUCCEEDED;
                
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, "Failed to draw terrain map!");
                ModernBeta.log(Level.ERROR, "Error: " + e.getLocalizedMessage());
                this.state = ProgressState.FAILED;
                
            } finally {
                this.updateButtonsEnabled(this.state);
                
            }
        };
        
        this.executor.queueTask(runnable);
    }
    
    private void loadMapTexture() {
        if (this.mapImage != null) {
            this.mapTexture = new DynamicTexture(this.mapImage.getWidth(), this.mapImage.getHeight());
            this.mc.getTextureManager().loadTexture(this.mapLocation, this.mapTexture);

            this.mapImage.getRGB(0, 0, this.mapImage.getWidth(), this.mapImage.getHeight(), this.mapTexture.getTextureData(), 0, this.mapImage.getWidth());
            this.mapTexture.updateDynamicTexture();
            this.mapImage = null;
            
        }
    }
    
    private void deleteMapTexture() {
        this.mc.getTextureManager().deleteTexture(this.mapLocation);
        this.mapTexture = null;
    }
    
    private void updateButtonsEnabled(ProgressState state) {
        boolean enabled = state != ProgressState.STARTED;
        
        this.biomeColors.enabled = enabled;
        this.resolutionSlider.enabled = enabled;
        this.generate.enabled = enabled;
        this.cancel.enabled = enabled;
    }

    @SideOnly(Side.CLIENT)
    private class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 32;
        private static final int LIST_PADDING_BOTTOM = 32;
        
        public ListPreset() {
            super(
                GuiScreenCustomizePreview.this.mc,
                GuiScreenCustomizePreview.this.width,
                GuiScreenCustomizePreview.this.height,
                LIST_PADDING_TOP,
                GuiScreenCustomizePreview.this.height - LIST_PADDING_BOTTOM,
                1
            );
        }

        @Override
        protected int getSize() {
            return 1;
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) { }

        @Override
        protected boolean isSelected(int slotIndex) {
            return false;
        }

        @Override
        protected void drawBackground() { }

        @Override
        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) { }
        
    }
    
    @SideOnly(Side.CLIENT)
    private static class BoundChecker {
        private int x;
        private int y;
        private int width;
        private int height;
        
        public BoundChecker() {
            this.x = 0;
            this.y = 0;
            this.width = 0;
            this.height = 0;
        }
        
        public void updateBounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public boolean inBounds(int mouseX, int mouseY) {
            int left = this.x;
            int right = this.x + this.width;
            int top = this.y;
            int bottom = this.y + this.height;
            
            return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
        }
        
        public int getRelativeX(int mouseX) {
            return mouseX - this.x;
        }
        
        public int getRelativeY(int mouseY) {
            return mouseY - this.y;
        }
    }
}
