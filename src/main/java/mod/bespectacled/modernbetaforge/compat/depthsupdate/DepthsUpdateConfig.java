package mod.bespectacled.modernbetaforge.compat.depthsupdate;

import java.io.File;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DepthsUpdateConfig {
	public static final DepthsUpdateConfig INSTANCE = new DepthsUpdateConfig();
    
    private static final String CATEGORY = "general.height extension";
	private static final String KEY_MAX_Y = "Global Maximum Y";
	private static final String KEY_MIN_Y = "Global Minimum Y";
	private static final String KEY_DIM_OVERRIDES = "Dimension Overrides";
	private static final String KEY_EXT_DIMS = "Extended Dimensions";
	
	private final Configuration config;
	
	private int minY;
	private int maxY;
	private boolean applyGlobally;
	private String[] override;
	
	private DepthsUpdateConfig() {
	    this.config = new Configuration(new File(ModernBeta.getConfigDirectory(), CompatDepthsUpdate.MOD_ID + ".cfg"));
	    
	    this.minY = this.readMinY();
	    this.maxY = this.readMaxY();
	    this.applyGlobally = this.readApplyGlobally();
	    this.override = this.readOverride();
	}
	
	public void reloadConfig() {
		this.config.load();
		
		this.minY = this.readMinY();
        this.maxY = this.readMaxY();
        this.applyGlobally = this.readApplyGlobally();
        this.override = this.readOverride();
	}
	
	public int getMinY() {
	    return this.minY;
	}
	
	public int getMaxY() {
	    return this.maxY;
	}
	
	public boolean extendHeight() {
	    return this.applyGlobally || this.override != null;
	}
	
	private int readMinY() {
    	final int minY = 0;
    	String[] override = this.readOverride();
    	
    	if (override != null) {
    		return Integer.parseInt(override[1]);
    	} else if (this.readApplyGlobally()) {
    		return this.config.get(CATEGORY, KEY_MIN_Y, minY).getInt();
    	}
    	
    	return minY;
    }

	private int readMaxY() {
		final int maxY = 255;
		String[] override = this.readOverride();
		
		if (override != null) {
			return Integer.parseInt(override[2]);
		} else if (this.readApplyGlobally()) {
			return this.config.get(CATEGORY, KEY_MAX_Y, maxY).getInt();
		}
		
		return maxY;
	}
	
	private boolean readApplyGlobally() {
    	int[] dims = this.config.get(CATEGORY, KEY_EXT_DIMS, new int[]{}).getIntList();
    	
    	for (int i = 0; i < dims.length; ++i) {
    		if (dims[i] == 0) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    private String[] readOverride() {
		String[] overrides = this.config.get(CATEGORY, KEY_DIM_OVERRIDES, new String[]{}).getStringList();
		
		for (int i = 0; i < overrides.length; ++i) {
			String override = overrides[i];
			String[] values = override.split(":");
			
			if ((values.length == 3 || values.length == 5) && Integer.parseInt(values[0]) == 0) {
				return values;
			}
		}
		
		return null;
	}
    
    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(CompatDepthsUpdate.MOD_ID)) {
            INSTANCE.reloadConfig();
        }
    }
}