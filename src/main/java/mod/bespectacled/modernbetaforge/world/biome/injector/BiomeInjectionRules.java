package mod.bespectacled.modernbetaforge.world.biome.injector;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class BiomeInjectionRules {
    private final Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap;
    
    private BiomeInjectionRules(Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap) {
        this.ruleMap = ruleMap;
    }
    
    public Biome test(BiomeInjectionContext context, int x, int z, BiomeInjectionStep step) {
        List<BiomeInjectionRule> rules = this.ruleMap.get(step);
        
        if (rules == null) {
            return null;
        }
        
        for (BiomeInjectionRule rule : rules) {
            Biome biome = rule.test(context).apply(x, z);
            
            if (biome != null)
                return biome;
        }
        
        return null;
    }

    public static class Builder {
        private final Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap;
        
        public Builder() {
            this.ruleMap = new LinkedHashMap<>();
        }
        
        public Builder add(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, BiomeInjectionStep step) {
            if (!this.ruleMap.containsKey(step)) {
                this.ruleMap.put(step, new LinkedList<>());
            }
            
            this.ruleMap.get(step).add(new BiomeInjectionRule(rule, resolver));
            
            return this;
        }
        
        public BiomeInjectionRules build() {
            return new BiomeInjectionRules(this.ruleMap);
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
        public final BlockPos pos;
        public final IBlockState state;
        public final IBlockState stateAbove;
        public final Biome biome;
        
        public BiomeInjectionContext(BlockPos pos, IBlockState state, IBlockState stateAbove, Biome biome) {
            this.pos = pos;
            this.state = state;
            this.stateAbove = stateAbove;
            this.biome = biome;
        }
    }
}