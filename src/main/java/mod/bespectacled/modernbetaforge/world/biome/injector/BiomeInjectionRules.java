package mod.bespectacled.modernbetaforge.world.biome.injector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class BiomeInjectionRules {
    public static final byte BASE = -1;
    public static final byte BEACH = 0;
    public static final byte OCEAN = 1;
    public static final byte DEEP_OCEAN = 2;
    public static final byte RIVER = 3;
    
    public static final Map<Byte, String> RULES_IDS = ImmutableMap.of(
        BASE, "BASE",
        BEACH, "BEACH",
        OCEAN, "OCEAN",
        DEEP_OCEAN, "DEEP_OCEAN",
        RIVER, "RIVER"
    );
    
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
    
    public byte testId(BiomeInjectionContext context, int x, int z) {
        for (BiomeInjectionRule rule : this.rules) {
            byte id = rule.testId(context);
            
            if (id != BASE)
                return id;
        }
        
        return BASE;
    }

    public static class Builder {
        private final List<BiomeInjectionRule> rules;
        
        public Builder() {
            this.rules = new ArrayList<>();
        }
        
        public Builder add(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, byte id) {
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
        private final byte id;
        
        public BiomeInjectionRule(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, byte id) {
            this.rule = rule;
            this.resolver = resolver;
            this.id = id;
        }
        
        public BiomeInjectionResolver test(BiomeInjectionContext context) {
            if (this.rule.test(context))
                return this.resolver;
            
            return BiomeInjectionResolver.DEFAULT;
        }
        
        public Byte testId(BiomeInjectionContext context) {
            if (this.rule.test(context))
                return this.id;
            
            return BASE;
        }
    }
    
    public static class BiomeInjectionContext {
        public final BlockPos topPos;
        public final IBlockState topState;
        public final Biome biome;
        
        public BiomeInjectionContext(BlockPos topPos, IBlockState topState, Biome biome) {
            this.topPos = topPos;
            this.topState = topState;
            this.biome = biome;
        }
    }
}