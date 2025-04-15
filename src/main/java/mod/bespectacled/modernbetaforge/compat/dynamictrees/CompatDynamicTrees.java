package mod.bespectacled.modernbetaforge.compat.dynamictrees;

import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;

import mod.bespectacled.modernbetaforge.compat.Compat;
import net.minecraftforge.common.MinecraftForge;

public class CompatDynamicTrees implements Compat {
    public static final String MOD_ID = "dynamictrees";
    
    @Override
    public void load() { 
        MinecraftForge.EVENT_BUS.register(DynamicTreesPopulator.class);
    }
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
    
    public static boolean isEnabled() {
        return WorldGenRegistry.isWorldGenEnabled();
    }
}
