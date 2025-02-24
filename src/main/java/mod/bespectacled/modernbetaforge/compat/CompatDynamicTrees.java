package mod.bespectacled.modernbetaforge.compat;

import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import net.minecraftforge.common.MinecraftForge;

public class CompatDynamicTrees implements Compat {
    @Override
    public void load() { 
        MinecraftForge.EVENT_BUS.register(CompatDynamicTreesPopulator.class);
    }
    
    public static boolean isEnabled() {
        return WorldGenRegistry.isWorldGenEnabled();
    }
}
