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
        
        public Builder add(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, String id) {
            this.rules.add(new BiomeInjectionRule(rule, resolver, id));
            
            return this;
        }
        
        public BiomeInjectionRules build() {
            return new BiomeInjectionRules(this.rules);
        }
    }
    
    private static class BiomeInjectionRule {
        private final Predicate<BiomeInjectionContext> rule;
        private final BiomeInjectionResolver resolver;
        private final String id;
        
        public BiomeInjectionRule(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, String id) {
            this.rule = rule;
            this.resolver = resolver;
            this.id = id;
        }
        
        public BiomeInjectionResolver test(BiomeInjectionContext context) {
            if (this.rule.test(context))
                return this.resolver;
            
            return BiomeInjectionResolver.DEFAULT;
        }
        
        public String getId() {
            return this.id;
        }
    }
    
    public static class BiomeInjectionContext {
        protected final int topHeight;
        protected final IBlockState topState;
        protected final Biome biome;
        
        public BiomeInjectionContext(int topHeight, IBlockState topState, Biome biome) {
            this.topHeight = topHeight;
            this.topState = topState;
            this.biome = biome;
        }
    }
}