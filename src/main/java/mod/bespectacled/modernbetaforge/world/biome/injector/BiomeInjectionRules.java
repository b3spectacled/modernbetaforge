package mod.bespectacled.modernbetaforge.world.biome.injector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class BiomeInjectionRules {
    private final Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap;
    
    private BiomeInjectionRules(Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap) {
        this.ruleMap = ruleMap;
    }
    
    public Biome test(BiomeInjectionContext context, int x, int z, BiomeInjectionStep step) {
        List<BiomeInjectionRule> rules = this.getRules(step);
        
        if (rules == null) {
            return null;
        }
        
        for (int i = 0; i < rules.size(); ++i) {
            Biome biome = rules.get(i).test(context).apply(x, z);
            
            if (biome != null)
                return biome;
        }
        
        return null;
    }
    
    private List<BiomeInjectionRule> getRules(BiomeInjectionStep step) {
        if (step == BiomeInjectionStep.ALL) {
            return this.ruleMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        
        return this.ruleMap.get(step);
    }

    public static class Builder {
        private final Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap;
        
        public Builder() {
            this.ruleMap = new LinkedHashMap<>();
        }
        
        public Builder add(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, BiomeInjectionStep step) {
            if (!this.ruleMap.containsKey(step)) {
                this.ruleMap.put(step, new ArrayList<>());
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