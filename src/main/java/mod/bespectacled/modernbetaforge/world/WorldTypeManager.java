package mod.bespectacled.modernbetaforge.world;

import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.compat.cubicchunks.CompatCubicChunks;
import mod.bespectacled.modernbetaforge.compat.cubicchunks.CubicModernBetaWorldTypeRegistrar;
import net.minecraft.world.WorldType;

public class WorldTypeManager {
    public static final WorldTypeManager INSTANCE = new WorldTypeManager();
    
    private WorldType worldType;
    
    private WorldTypeManager() { }
    
    public void registerWorldType() {
        if (this.worldType != null) {
            return;
        }
        
        if (ModCompat.isCompatLoaded(CompatCubicChunks.MOD_ID)) {
            this.worldType = CubicModernBetaWorldTypeRegistrar.createCubicChunksWorldType();
        } else {
            this.worldType = new ModernBetaWorldType();
        }
    }
    
    public WorldType getWorldType() {
        this.registerWorldType();
        return this.worldType;
    }

    public int getWorldTypeId() {
        this.registerWorldType();
        return this.worldType.getId();
    }
    
    public float getCloudHeight() {
        this.registerWorldType();
        return this.worldType.getCloudHeight();
    }
    
    public void setCloudHeight(int cloudHeight) {
        this.registerWorldType();
        if (this.worldType instanceof AdjustableCloudHeightWorldType) {
            ((AdjustableCloudHeightWorldType)this.worldType).setCloudHeight(cloudHeight);
        }
    }
}
