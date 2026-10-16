package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.io.IOException;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import mod.bespectacled.modernbetaforge.client.gui.GuiColors;
import mod.bespectacled.modernbetaforge.client.gui.GuiUtil;
import mod.bespectacled.modernbetaforge.client.gui.screen.GuiScreenCustomizeRegistry;
import mod.bespectacled.modernbetaforge.util.ExecutorWrapper;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class GuiModalLocateBiome extends GuiModal<GuiModalLocateBiome> {
    private enum ProgressState {
        NOT_STARTED, STARTED, FAILED, SUCCEEDED, CANCELED, CANCELING
    }
    
    private static final String PREFIX = "createWorld.customize.preview.modernbetaforge.locateBiome.";
    
    private static final int MODAL_WIDTH = 220;
    private static final int MODAL_HEIGHT = 100;
    private static final int BUTTON_WIDTH = 80;
    private static final int BIOME_BUTTON_WIDTH = 180;
    
    private static final int GUI_ID_LOCATE = 2;
    private static final int GUI_ID_BIOME = 3;
    
    private final ExecutorWrapper executor;
    private final BiFunction<Integer, Integer, Biome> biomeLocator;
    
    private GuiButton buttonLocate;
    private GuiButton buttonBiome;
    private Biome biome;
    private ProgressState state;
    private String infoString;
    private int infoColor;
    private int distance;
    private boolean haltSearch;
    
    public GuiModalLocateBiome(
        GuiScreen parent,
        String title,
        Consumer<GuiModalLocateBiome> onConfirm,
        Consumer<GuiModalLocateBiome> onCancel,
        BiFunction<Integer, Integer, Biome> biomeLocator,
        Biome biome
    ) {
        super(parent, title, MODAL_WIDTH, MODAL_HEIGHT, onConfirm, onCancel);
        
        this.executor = new ExecutorWrapper(1, "locate_biome");
        this.biomeLocator = biomeLocator;
        
        this.biome = biome;
        this.state = ProgressState.NOT_STARTED;
        
        this.infoString = this.getInfoString();
        this.infoColor = this.getInfoColor();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonConfirm.enabled = false;
        this.buttonConfirm.visible = false;
        this.buttonCancel.width = BUTTON_WIDTH;
        
        int centerX = this.width / 2;
        
        int buttonLocateX = centerX - GuiUtil.BUTTON_SPACE / 2 - BUTTON_WIDTH;
        int buttonLocateY = this.buttonConfirm.y;
        int buttonLocateW = BUTTON_WIDTH;
        int buttonLocateH = this.buttonConfirm.height;
        
        int buttonBiomeX = centerX - BIOME_BUTTON_WIDTH / 2;
        int buttonBiomeY = this.titleY + 20;
        
        this.buttonBiome = this.addButton(new GuiButton(GUI_ID_BIOME, buttonBiomeX, buttonBiomeY, BIOME_BUTTON_WIDTH, 20, GuiUtil.trimString(this.fontRenderer, this.biome.getBiomeName(), BIOME_BUTTON_WIDTH - 10)));
        this.buttonLocate = this.addButton(new GuiButton(GUI_ID_LOCATE, buttonLocateX, buttonLocateY, buttonLocateW, buttonLocateH, I18n.format("createWorld.customize.preview.modernbetaforge.locateBiome")));
    
        this.updateButtonValidity();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        int infoX = this.width / 2;
        int infoY = this.buttonBiome.y + this.buttonBiome.height + 6;
        
        this.drawCenteredString(this.fontRenderer, this.infoString, infoX, infoY, this.infoColor);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        
        if (this.state == ProgressState.CANCELED) {
            this.exit();
        }
    }
    
    public Biome getBiome() {
        return this.biome;
    }
    
    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_LOCATE:
                this.locateBiome();
                break;
            case GUI_ID_BIOME:
                Function<ResourceLocation, String> nameFormatter = key -> ForgeRegistries.BIOMES.getValue(key).getBiomeName();
                BiConsumer<String, GuiScreen> consumer = (str, screen) -> {
                    this.biome = ForgeRegistryUtil.get(new ResourceLocation(str), ForgeRegistries.BIOMES);
                    this.state = ProgressState.NOT_STARTED;
                    this.infoString = this.getInfoString();
                    this.infoColor = this.getInfoColor();
                };
                String biomeKey = this.biome.getRegistryName().toString();
                
                this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(this, consumer, nameFormatter, biomeKey, "biome", ForgeRegistryUtil.getKeys(ForgeRegistries.BIOMES)));
                break;
            case GUI_ID_CANCEL:
                this.haltSearch = true;
                this.state = this.state != ProgressState.STARTED ? ProgressState.CANCELED : ProgressState.CANCELING;
                break;
        }
        
        this.updateButtonValidity();
    }
    
    private void exit() {
        this.onCancel.accept(this);
        this.executor.shutdown();
        this.mc.displayGuiScreen(this.parent);
    }
    
    private void locateBiome() {
        this.state = ProgressState.STARTED;
        this.haltSearch = false;
        
        this.executor.queueRunnable(() -> {
            int[] levelWidths = ModernBetaGeneratorSettings.LEVEL_WIDTHS;
            int steps = 16;
            
            BlockPos closestPos = ModernBetaBiomeProvider.locateBiome(0, 0, levelWidths[levelWidths.length - 1], steps, this.biome, new Random(), this.biomeLocator, () -> this.haltSearch);
            
            if (closestPos != null) {
                this.distance = (int)MathUtil.distance(0, 0, closestPos.getX(), closestPos.getZ());
                this.state = ProgressState.SUCCEEDED;
            } else {
                this.distance = -1;
                this.state = ProgressState.FAILED;
            }
            
            this.infoString = this.getInfoString();
            this.infoColor = this.getInfoColor();
            this.updateButtonValidity();
        });
        
        this.infoString = this.getInfoString();
        this.infoColor = this.getInfoColor();
    }
    
    private void updateButtonValidity() {
        boolean enabled = this.state != ProgressState.STARTED && this.state != ProgressState.CANCELING;
        
        this.buttonBiome.enabled = enabled;
        this.buttonLocate.enabled = enabled;
        this.buttonCancel.enabled = this.state != ProgressState.CANCELING;
    }
    
    private String getInfoString() {
        String info;
        
        switch (this.state) {
            case STARTED:
                info = I18n.format(PREFIX + "success", getFormattedValue("123", TextFormatting.YELLOW, TextFormatting.OBFUSCATED));
                break;
            case FAILED:
                info = I18n.format(PREFIX + "failure");
                break;
            case SUCCEEDED:
                info = I18n.format(PREFIX + "success", getFormattedValue(new Integer(this.distance).toString(), TextFormatting.GREEN));
                break;
            default:
                info = I18n.format(PREFIX + "success", getFormattedValue("?", TextFormatting.YELLOW));
        }
        
        return info;
    }
    
    private int getInfoColor() {
        int color;
        
        switch (this.state) {
            case FAILED:
                color = GuiColors.RGB_LIGHT_RED;
                break;
            default:
                color = GuiColors.RGB_WHITE;
        }
        
        return color;
    }
    
    private static String getFormattedValue(String value, TextFormatting ...formattings) {
        String formatting = "";
        
        for (TextFormatting f : formattings) {
            formatting += f;
        }
        
        return formatting + value + TextFormatting.RESET;
    }
}
