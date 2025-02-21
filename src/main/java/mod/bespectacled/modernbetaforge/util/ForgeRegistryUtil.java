package mod.bespectacled.modernbetaforge.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;

import com.google.common.base.Predicate;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
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
    
    public static <T> List<ResourceLocation> getKeys(IForgeRegistry<? extends T> registry, Predicate<ResourceLocation> filter) {
        return registry.getEntries()
                .stream()
                .map(e -> e.getKey())
                .filter(filter)
                .collect(Collectors.toCollection(LinkedList::new));
    }
    
    public static <T> List<ResourceLocation> getKeys(IForgeRegistry<? extends T> registry) {
        return getKeys(registry, e -> true);
    }
    
    public static Fluid getFluid(ResourceLocation registryKey) {
        return FluidRegistry.getRegisteredFluids()
            .values()
            .stream()
            .filter(f -> f.getBlock() != null && f.getBlock().getRegistryName().equals(registryKey))
            .findFirst()
            .orElse(FluidRegistry.WATER);
    }
    
    public static List<Block> getFluidBlocks() {
        return FluidRegistry.getRegisteredFluids()
            .values()
            .stream()
            .filter(f -> f.getBlock() != null)
            .map(f -> f.getBlock())
            .collect(Collectors.toCollection(LinkedList::new));
    }
    
    public static List<ResourceLocation> getFluidBlockRegistryNames() {
        return FluidRegistry.getRegisteredFluids()
            .values()
            .stream()
            .filter(f -> f.getBlock() != null)
            .map(f -> f.getBlock().getRegistryName())
            .collect(Collectors.toCollection(LinkedList::new));
    }
    
    public static List<String> getFluidNames() {
        return FluidRegistry.getRegisteredFluids()
            .values()
            .stream()
            .filter(f -> f.getBlock() != null)
            .map(f -> f.getLocalizedName(new FluidStack(f, 0)))
            .collect(Collectors.toCollection(LinkedList::new));
    }
    
    public static String getFluidLocalizedName(ResourceLocation blockKey) {
        Optional<Fluid> fluid = FluidRegistry.getRegisteredFluids()
            .values()
            .stream()
            .filter(f -> f.getBlock() != null)
            .filter(f -> f.getBlock().getRegistryName().equals(blockKey))
            .findFirst();
            
        if (fluid.isPresent()) {
            return fluid.get().getLocalizedName(new FluidStack(fluid.get(), 0));
        }
        
        return "Unknown Fluid";
    }
    
    public static boolean isForgeFluid(Block block) {
        return FluidRegistry.getRegisteredFluids()
            .values()
            .stream()
            .filter(f -> f.getBlock() != null)
            .anyMatch(f -> f.getBlock() == block);
    }
    
    public static ResourceLocation getRandomFluidRegistryName(Random random) {
        List<ResourceLocation> entries = new ArrayList<>(getFluidBlockRegistryNames());

        return entries.get(random.nextInt(entries.size()));
    }
 }
