package mod.bespectacled.modernbetaforge.compat.cubicchunks;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiCustomizePresets;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.HeightCompat;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;

public class CompatCubicChunks implements Compat, ClientCompat, HeightCompat {
    public static final String MOD_ID = "cubicchunks";
    public static final String RECOMMENDED_MOD_VERSION = "1.12.2-0.0.1271.0-SNAPSHOT";
    public static final String ADDON_ID = "compat" + MOD_ID;

    @Override
    public void load() {
        MinecraftForge.EVENT_BUS.register(CubicChunksConfigManager.class);
    }

    @Override
    public void loadClient() {
        ModernBetaClientRegistries.GUI_PRESET.register(GuiCustomizePresets.CUBIC_CHUNKS_BETA, GuiCustomizePresets.PRESET_CUBIC_CHUNKS_BETA);
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }
    
    @Override
    public String getRecommendedModVersion() {
        return RECOMMENDED_MOD_VERSION;
    }
    
    @Override
    public String getModTooltip() {
        int minHeight = ModernBetaGeneratorSettings.MIN_MIN_HEIGHT;
        int maxHeight = ModernBetaGeneratorSettings.MAX_MAX_HEIGHT;
        
        return I18n.format(String.format("createWorld.customize.custom.%s.tooltip", ADDON_ID), minHeight, maxHeight);
    }

    @Override
    public boolean extendHeight() {
        return true;
    }

    @Override
    public int getMinHeight() {
        return CubicChunksConfigManager.INSTANCE.getDefaultMinHeight();
    }

    @Override
    public int getMaxHeight() {
        return CubicChunksConfigManager.INSTANCE.getDefaultMaxHeight();
    }
}
