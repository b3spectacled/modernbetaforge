package mod.bespectacled.modernbetaforge.world.biome.injector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.biome.Biome;

public class BiomeInjectionRules {
    private final Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap;
    private final boolean isEmpty;
    
    private BiomeInjectionRules(Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap) {
        this.ruleMap = ruleMap;
        this.isEmpty = checkIfEmpty(this.ruleMap);
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
    
    public boolean isEmpty() {
        return this.isEmpty;
    }
    
    private List<BiomeInjectionRule> getRules(BiomeInjectionStep step) {
        return this.ruleMap.get(step);
    }
    
    private static boolean checkIfEmpty(Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap) {
        boolean isEmpty = true;
        
        for (BiomeInjectionStep step : ruleMap.keySet()) {
            if (!ruleMap.get(step).isEmpty()) {
                isEmpty = false;
            }
        }
        
        return isEmpty;
    }

    public static class Builder {
        private final Map<BiomeInjectionStep, List<BiomeInjectionRule>> ruleMap;
        
        public Builder() {
            this.ruleMap = new LinkedHashMap<>();
            
            this.ruleMap.put(BiomeInjectionStep.PRE_SURFACE, new ArrayList<>());
            this.ruleMap.put(BiomeInjectionStep.CUSTOM, new ArrayList<>());
            this.ruleMap.put(BiomeInjectionStep.POST_SURFACE, new ArrayList<>());
        }
        
        public Builder add(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, BiomeInjectionStep step) {
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
        private MutableBlockPos pos;
        private IBlockState state;
        private IBlockState stateAbove;
        private Biome biome;
        
        public BiomeInjectionContext() {
            this(BlockPos.ORIGIN, null, null, null);
        }
        
        public BiomeInjectionContext(BlockPos pos, IBlockState state, IBlockState stateAbove, Biome biome) {
            this.pos = new MutableBlockPos(pos);
            this.state = state;
            this.stateAbove = stateAbove;
            this.biome = biome;
        }
        
        public BlockPos getPos() {
            return this.pos;
        }
        
        public IBlockState getState() {
            return this.state;
        }
        
        public IBlockState getStateAbove() {
            return this.stateAbove;
        }
        
        public Biome getBiome() {
            return this.biome;
        }
        
        public void setPos(int x, int y, int z) {
            this.pos.setPos(x, y, z);
        }
        
        public void setState(IBlockState blockState) {
            this.state = blockState;
        }
        
        public void setStateAbove(IBlockState blockState) {
            this.stateAbove = blockState;
        }
        
        public void setBiome(Biome biome) {
            this.biome = biome;
        }
    }
}