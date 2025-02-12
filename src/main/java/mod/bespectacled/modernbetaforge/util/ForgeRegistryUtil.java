package mod.bespectacled.modernbetaforge.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class ForgeRegistryUtil<T> {
    public static <T> T get(ResourceLocation registryKey, IForgeRegistry<? extends T> registry) {
        T t = registry.getValue(registryKey);
        
        if (t == null) {
            String errorStr = String.format("[Modern Beta] Forge registry entry '%s' does not exist!", registryKey.toString());
            
            throw new IllegalArgumentException(errorStr);
        }
        
        return t;
    }
    
    public static <T> T getOrElse(ResourceLocation registryKey, ResourceLocation alternateKey, IForgeRegistry<? extends T> registry) {
        T t = registry.getValue(registryKey);
        
        if (t == null) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate entry.", registryKey.toString(), registry.getRegistrySuperType());
            ModernBeta.log(Level.WARN, warning);
            
            t = get(alternateKey, registry);
        }
        
        return t;
    }
    
    public static <T> T getRandom(Random random, IForgeRegistry<? extends T> registry) {
        List<T> entries = new ArrayList<>(registry.getValuesCollection());
        
        return entries.get(random.nextInt(entries.size()));
    }
}
