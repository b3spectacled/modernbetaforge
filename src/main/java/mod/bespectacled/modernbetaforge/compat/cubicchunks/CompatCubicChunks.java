package mod.bespectacled.modernbetaforge.compat.cubicchunks;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiCustomizePresets;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.HeightCompat;
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
        return I18n.format(String.format("createWorld.customize.custom.%s.tooltip", ADDON_ID));
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
