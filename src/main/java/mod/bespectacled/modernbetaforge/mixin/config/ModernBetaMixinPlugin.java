package mod.bespectacled.modernbetaforge.mixin.config;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.common.collect.ImmutableMap;

import mod.bespectacled.modernbetaforge.ModernBeta;

/*
 * This plugin is of dubious usefulness at the moment;
 * Mixins are loaded before Forge can either refresh the mod configs or detect loaded mods,
 * so there is no convenient way to conditionally load particular mixins.
 * 
 */
public class ModernBetaMixinPlugin implements IMixinConfigPlugin {
    private static final Map<String, Supplier<Boolean>> CONDITIONAL_MIXINS;
    
    private static final String MIXIN_PATH = "mod.bespectacled.modernbetaforge.mixin";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (CONDITIONAL_MIXINS.containsKey(mixinClassName)) {
            boolean shouldApply = CONDITIONAL_MIXINS.get(mixinClassName).get();
            ModernBeta.log(Level.INFO, String.format("Applying conditional mixin '%s': %b", mixinClassName, shouldApply));
            
            return shouldApply;
        }

        return true;
    }

    @Override
    public void onLoad(String mixinPackage) { }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    
    @SuppressWarnings("unused")
    private static String getMixinPath(String mixinClassName) {
        return MIXIN_PATH + "." + mixinClassName;
    }

    static {
        CONDITIONAL_MIXINS = ImmutableMap.<String, Supplier<Boolean>>builder().build();
    }
}
