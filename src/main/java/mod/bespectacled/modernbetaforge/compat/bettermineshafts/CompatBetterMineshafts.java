package mod.bespectacled.modernbetaforge.compat.bettermineshafts;

import mod.bespectacled.modernbetaforge.compat.Compat;
import net.minecraftforge.common.MinecraftForge;

public class CompatBetterMineshafts implements Compat {
    public static final String MOD_ID = "bettermineshafts";
    public static final String ADDON_ID = "compat" + MOD_ID;

    @Override
    public void load() {
        MinecraftForge.TERRAIN_GEN_BUS.register(new EventMineshaftGenOptimized());
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }
}
