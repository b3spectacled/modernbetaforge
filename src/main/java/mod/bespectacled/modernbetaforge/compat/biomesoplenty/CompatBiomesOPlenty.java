package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.api.block.BOPBlocks;
import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiPredicates;
import mod.bespectacled.modernbetaforge.compat.BiomeCompat;
import mod.bespectacled.modernbetaforge.compat.CarverCompat;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.NetherCompat;
import mod.bespectacled.modernbetaforge.compat.SurfaceCompat;
import mod.bespectacled.modernbetaforge.world.biome.source.ReleaseBiomeSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

/*
 * Mod Compat for Biomes-o-Plenty
 * 
 * For normal biomes list, see: https://github.com/Glitchfiend/BiomesOPlenty/blob/BOP-1.12.x-7.0.x/src/main/java/biomesoplenty/api/biome/BOPBiomes.java
 * For biome climates, see: https://github.com/Glitchfiend/BiomesOPlenty/blob/BOP-1.12.x-7.0.x/src/main/java/biomesoplenty/api/enums/BOPClimates.java
 * 
 */
public class CompatBiomesOPlenty implements Compat, ClientCompat, BiomeCompat, SurfaceCompat, CarverCompat, NetherCompat {
    public static final String MOD_ID = "biomesoplenty";
    public static final String ADDON_ID = "compat" + MOD_ID;
    
    public static final ResourceLocation KEY_USE_COMPAT = createKey("useCompat");
    public static final ResourceLocation KEY_CORAL_REEF_RESOLVER = createKey("resolverCoralReef");
    public static final ResourceLocation KEY_KELP_FOREST_RESOLVER = createKey("resolverKelpForest");
    
    @Override
    public void load() {
        ModernBeta.log(Level.WARN, "Biomes O' Plenty has been detected, classic Nether settings will be disabled due to incompatibilties!");
        
        ModernBetaRegistries.PROPERTY.register(KEY_USE_COMPAT, new BooleanProperty(true));
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_CORAL_REEF_RESOLVER, BiomesOPlentyCoralReefResolver::new);
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_KELP_FOREST_RESOLVER, BiomesOPlentyKelpForestResolver::new);
    }
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
    
    @Override
    public void loadClient() {
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_COMPAT, new GuiPredicate(settings ->
            GuiPredicates.isBiomeInstanceOf(settings, ReleaseBiomeSource.class)
        ));
    }

    @Override
    public List<BiomeEntry>[] getBiomeEntries() {
        @SuppressWarnings("unchecked")
        List<BiomeEntry>[] biomeEntries = new ArrayList[BiomeType.values().length];
        
        for (BiomeType type : BiomeType.values()) {
            biomeEntries[type.ordinal()] = new ArrayList<BiomeEntry>();
        }
        
        // In multiple climate zones:
        // * grassland
        
        // Add DESERT
        int ndx = BiomeType.DESERT.ordinal();
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.brushland, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.chaparral, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.lush_desert, 2);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.outback, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.steppe, 5);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.wasteland, 5);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.xeric_shrubland, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.oasis, 5);
        
        // Add WARM
        ndx = BiomeType.WARM.ordinal();
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.bamboo_forest, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.bayou, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.eucalyptus_forest, 5);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.flower_field, 2);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.grassland, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.lavender_fields, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.lush_swamp, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.mangrove, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.marsh, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.mystic_grove, 1);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.orchard, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.overgrown_cliffs, 2);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.prairie, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.rainforest, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.sacred_springs, 1);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.shrubland, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.temperate_rainforest, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.tropical_rainforest, 5);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.woodland, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.pasture, 5);
        
        // Add COOL
        ndx = BiomeType.COOL.ordinal();
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.bog, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.boreal_forest, 5);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.cherry_blossom_grove, 2);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.coniferous_forest, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.crag, 2);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.dead_forest, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.dead_swamp, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.fen, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.grassland, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.grove, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.highland, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.land_of_lakes, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.maple_woods, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.meadow, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.moor, 5);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.mountain, 3);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.ominous_woods, 1);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.quagmire, 2);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.redwood_forest, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.seasonal_forest, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.shield, 5);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.tundra, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.wetland, 5);
        
        // Add ICY
        ndx = BiomeType.ICY.ordinal();
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.alps, 5);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.cold_desert, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.snowy_coniferous_forest, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.snowy_forest, 7);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.snowy_tundra, 10);
        this.addBiomeEntry(biomeEntries, ndx, BOPBiomes.glacier, 5);
        
        return biomeEntries;
    }

    @Override
    public List<BiomeEntry>[] getBiomeEntries(ModernBetaGeneratorSettings settings) {
        return this.getBiomeEntries();
    }
    
    @Override
    public boolean shouldGetBiomeEntries(ModernBetaGeneratorSettings settings) {
        return settings.getBooleanProperty(KEY_USE_COMPAT);
    }

    @Override
    public List<Biome> getBiomesWithCustomSurfaces() {
        ImmutableList.Builder<Biome> builder = new ImmutableList.Builder<>();

        this.addBiomeSurface(builder, BOPBiomes.alps);
        this.addBiomeSurface(builder, BOPBiomes.bamboo_forest);
        this.addBiomeSurface(builder, BOPBiomes.bayou);
        this.addBiomeSurface(builder, BOPBiomes.bog);
        this.addBiomeSurface(builder, BOPBiomes.chaparral);
        this.addBiomeSurface(builder, BOPBiomes.cold_desert);
        this.addBiomeSurface(builder, BOPBiomes.crag);
        this.addBiomeSurface(builder, BOPBiomes.dead_swamp);
        this.addBiomeSurface(builder, BOPBiomes.glacier);
        this.addBiomeSurface(builder, BOPBiomes.lush_swamp);
        this.addBiomeSurface(builder, BOPBiomes.mangrove);
        this.addBiomeSurface(builder, BOPBiomes.moor);
        this.addBiomeSurface(builder, BOPBiomes.mystic_grove);
        this.addBiomeSurface(builder, BOPBiomes.overgrown_cliffs);
        this.addBiomeSurface(builder, BOPBiomes.quagmire);
        this.addBiomeSurface(builder, BOPBiomes.redwood_forest);
        this.addBiomeSurface(builder, BOPBiomes.shield);
        this.addBiomeSurface(builder, BOPBiomes.wetland);
        this.addBiomeSurface(builder, BOPBiomes.xeric_shrubland);
        this.addBiomeSurface(builder, BOPBiomes.coral_reef);
        this.addBiomeSurface(builder, BOPBiomes.kelp_forest);
        
        return builder.build();
    }
    
    @Override
    public List<Block> getCarvables() {
        ImmutableList.Builder<Block> builder = new ImmutableList.Builder<>();
        
        this.addCarverBlock(builder, BOPBlocks.grass);
        this.addCarverBlock(builder, BOPBlocks.dirt);
        
        return builder.build();
    }
    
    private void addBiomeEntry(List<BiomeEntry>[] biomeEntries, int ndx, Optional<Biome> biome, int weight) {
        if (biome != null && biome.isPresent()) {
            biomeEntries[ndx].add(new BiomeEntry(biome.get(), weight));
        }
    }
    
    private void addBiomeSurface(ImmutableList.Builder<Biome> builder, Optional<Biome> biome) {
        if (biome != null && biome.isPresent()) {
            builder.add(biome.get());
        }
    }
    
    private void addCarverBlock(ImmutableList.Builder<Block> builder, Block block) {
        if (block != null) {
            builder.add(block);
        }
    }
    
    private static ResourceLocation createKey(String path) {
        return new ResourceLocation(ADDON_ID, path);
    }
}
