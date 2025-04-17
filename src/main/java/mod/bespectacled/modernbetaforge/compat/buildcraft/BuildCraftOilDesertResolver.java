package mod.bespectacled.modernbetaforge.compat.buildcraft;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverCustom;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.world.biome.climate.SimpleClimateSampler;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BuildCraftOilDesertResolver implements BiomeResolverCustom {
    public static final ResourceLocation BIOME_ID = new ResourceLocation(CompatBuildCraftEnergy.MOD_ID, "oil_desert");
    private static final List<Type> REQUIRED_TYPES = Arrays.asList(BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
    
    private final Biome biome;
    private final SimpleClimateSampler climateSampler;

    private final boolean isReleaseChunkSource;
    private final boolean useCompat;
    private final float chance;
    
    public BuildCraftOilDesertResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        this.biome = ForgeRegistryUtil.get(BIOME_ID, ForgeRegistries.BIOMES);
        this.climateSampler = new SimpleClimateSampler(chunkSource.getSeed(), 1847L, 74531L);

        this.isReleaseChunkSource = chunkSource instanceof ReleaseChunkSource;
        this.useCompat = settings.getBooleanProperty(CompatBuildCraftEnergy.KEY_USE_COMPAT);
        this.chance = settings.getFloatProperty(CompatBuildCraftEnergy.KEY_OIL_DESERT_CHANCE);
    }
    
    @Override
    public Predicate<BiomeInjectionContext> getCustomPredicate() {
        return context -> !this.isReleaseChunkSource && this.useCompat && BiomeDictionary.getTypes(context.biome).containsAll(REQUIRED_TYPES);
    }

    @Override
    public Biome getCustomBiome(int x, int z) {
        if (this.chance <= 0.0) {
            return null;
        }
        
        if (this.climateSampler.sample(x, z) <= this.chance) {
            return this.biome;
        }
        
        return null;
    }

}
