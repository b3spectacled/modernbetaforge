package mod.bespectacled.modernbetaforge.world.biome.injector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

public class BiomeInjectionRules {
    private final List<BiomeInjectionRule> rules;
    
    private BiomeInjectionRules(List<BiomeInjectionRule> rules) {
        this.rules = rules;
    }
    
    public Biome test(BiomeInjectionContext context, int x, int z) {
        for (BiomeInjectionRule rule : this.rules) {
            Biome biome = rule.test(context).apply(x, z);
            
            if (biome != null)
                return biome;
        }
        
        return null;
    }

    public static class Builder {
        private final List<BiomeInjectionRule> rules;
        
        public Builder() {
            this.rules = new ArrayList<>();
        }
        
        public Builder add(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver) {
            this.rules.add(new BiomeInjectionRule(rule, resolver));
            
            return this;
        }
        
        public BiomeInjectionRules build() {
            return new BiomeInjectionRules(this.rules);
        }
    }
    
    private static class BiomeInjectionRule {
        private final Predicate<BiomeInjectionContext> rule;
        private final BiomeInjectionResolver resolver;
        
        public BiomeInjectionRule(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver) {
            this.rule = rule;
            this.resolver = resolver;
        }
        
        public BiomeInjectionResolver test(BiomeInjectionContext context) {
            if (this.rule.test(context))
                return this.resolver;
            
            return BiomeInjectionResolver.DEFAULT;
        }
    }
    
    public static class BiomeInjectionContext {
        protected final int topHeight;
        protected final IBlockState topState;
        
        public BiomeInjectionContext(int topHeight, IBlockState topState) {
            this.topHeight = topHeight;
            this.topState = topState;
        }
    }
}