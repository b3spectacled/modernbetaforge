package mod.bespectacled.modernbetaforge.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class ForgeRegistryUtil<T> {
    public static <T> T getRegistryEntry(ResourceLocation registryKey, IForgeRegistry<? extends T> registry) {
        T t = registry.getValue(registryKey);
        
        if (t == null) {
            String errorStr = String.format("[Modern Beta] Forge registry entry '%s' does not exist!", registryKey.toString());
            
            throw new IllegalArgumentException(errorStr);
        }
        
        return t;
    }
    
    public static <T> T getRandomEntry(Random random, IForgeRegistry<? extends T> registry) {
        List<T> entries = new ArrayList<>(registry.getValuesCollection());
        
        return entries.get(random.nextInt(entries.size()));
    }
}
