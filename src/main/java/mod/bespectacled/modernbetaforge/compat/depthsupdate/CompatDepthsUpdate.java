package mod.bespectacled.modernbetaforge.compat.depthsupdate;

import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.HeightCompat;
import net.minecraftforge.common.MinecraftForge;

public class CompatDepthsUpdate implements Compat, HeightCompat {
	public static final String MOD_ID = "depthsupdate";

	@Override
	public void load() { 
	    MinecraftForge.EVENT_BUS.register(DepthsUpdateConfig.class);
	}

	@Override
	public String getModId() {
		return MOD_ID;
	}

    @Override
    public boolean extendHeight() {
        return DepthsUpdateConfig.INSTANCE.extendHeight();
    }

    @Override
    public int getMinHeight() {
        return DepthsUpdateConfig.INSTANCE.getMinY();
    }

    @Override
    public int getMaxHeight() {
        return DepthsUpdateConfig.INSTANCE.getMaxY();
    }
}
