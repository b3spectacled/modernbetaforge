package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.api.block.BOPBlocks;
import mod.bespectacled.modernbetaforge.compat.BiomeCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.NetherCompat;
import net.minecraft.block.Block;
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
public class CompatBiomesOPlenty implements Compat, BiomeCompat, NetherCompat {
    public static final String MOD_ID = "biomesoplenty";
    public static final String ADDON_ID = "compat" + MOD_ID;
    
    @SuppressWarnings("unchecked")
    private List<BiomeEntry>[] biomeEntries = new ArrayList[BiomeType.values().length];
    
    @Override
    public void load() {
        for (BiomeType type : BiomeType.values()) {
            this.biomeEntries[type.ordinal()] = new ArrayList<BiomeEntry>();
        }
        
        // In multiple climate zones:
        // * grassland
        
        // Add DESERT
        int ndx = BiomeType.DESERT.ordinal();
        this.addBiomeEntry(ndx, BOPBiomes.brushland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.chaparral, 10);
        this.addBiomeEntry(ndx, BOPBiomes.lush_desert, 2);
        this.addBiomeEntry(ndx, BOPBiomes.outback, 7);
        this.addBiomeEntry(ndx, BOPBiomes.steppe, 5);
        this.addBiomeEntry(ndx, BOPBiomes.wasteland, 5);
        this.addBiomeEntry(ndx, BOPBiomes.xeric_shrubland, 3);
        this.addBiomeEntry(ndx, BOPBiomes.oasis, 5);
        
        // Add WARM
        ndx = BiomeType.WARM.ordinal();
        this.addBiomeEntry(ndx, BOPBiomes.bamboo_forest, 3);
        this.addBiomeEntry(ndx, BOPBiomes.bayou, 10);
        this.addBiomeEntry(ndx, BOPBiomes.eucalyptus_forest, 5);
        this.addBiomeEntry(ndx, BOPBiomes.flower_field, 2);
        this.addBiomeEntry(ndx, BOPBiomes.grassland, 3);
        this.addBiomeEntry(ndx, BOPBiomes.lavender_fields, 3);
        this.addBiomeEntry(ndx, BOPBiomes.lush_swamp, 10);
        this.addBiomeEntry(ndx, BOPBiomes.mangrove, 7);
        this.addBiomeEntry(ndx, BOPBiomes.marsh, 7);
        this.addBiomeEntry(ndx, BOPBiomes.mystic_grove, 1);
        this.addBiomeEntry(ndx, BOPBiomes.orchard, 3);
        this.addBiomeEntry(ndx, BOPBiomes.overgrown_cliffs, 2);
        this.addBiomeEntry(ndx, BOPBiomes.prairie, 7);
        this.addBiomeEntry(ndx, BOPBiomes.rainforest, 7);
        this.addBiomeEntry(ndx, BOPBiomes.sacred_springs, 1);
        this.addBiomeEntry(ndx, BOPBiomes.shrubland, 7);
        this.addBiomeEntry(ndx, BOPBiomes.temperate_rainforest, 7);
        this.addBiomeEntry(ndx, BOPBiomes.tropical_rainforest, 5);
        this.addBiomeEntry(ndx, BOPBiomes.woodland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.pasture, 5);
        
        // Add COOL
        ndx = BiomeType.COOL.ordinal();
        this.addBiomeEntry(ndx, BOPBiomes.bog, 7);
        this.addBiomeEntry(ndx, BOPBiomes.boreal_forest, 5);
        this.addBiomeEntry(ndx, BOPBiomes.cherry_blossom_grove, 2);
        this.addBiomeEntry(ndx, BOPBiomes.coniferous_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.crag, 2);
        this.addBiomeEntry(ndx, BOPBiomes.dead_forest, 3);
        this.addBiomeEntry(ndx, BOPBiomes.dead_swamp, 3);
        this.addBiomeEntry(ndx, BOPBiomes.fen, 7);
        this.addBiomeEntry(ndx, BOPBiomes.grassland, 7);
        this.addBiomeEntry(ndx, BOPBiomes.grove, 7);
        this.addBiomeEntry(ndx, BOPBiomes.highland, 7);
        this.addBiomeEntry(ndx, BOPBiomes.land_of_lakes, 3);
        this.addBiomeEntry(ndx, BOPBiomes.maple_woods, 10);
        this.addBiomeEntry(ndx, BOPBiomes.meadow, 7);
        this.addBiomeEntry(ndx, BOPBiomes.moor, 5);
        this.addBiomeEntry(ndx, BOPBiomes.mountain, 3);
        this.addBiomeEntry(ndx, BOPBiomes.ominous_woods, 1);
        this.addBiomeEntry(ndx, BOPBiomes.quagmire, 2);
        this.addBiomeEntry(ndx, BOPBiomes.redwood_forest, 7);
        this.addBiomeEntry(ndx, BOPBiomes.seasonal_forest, 7);
        this.addBiomeEntry(ndx, BOPBiomes.shield, 5);
        this.addBiomeEntry(ndx, BOPBiomes.tundra, 10);
        this.addBiomeEntry(ndx, BOPBiomes.wetland, 5);
        
        // Add ICY
        ndx = BiomeType.ICY.ordinal();
        this.addBiomeEntry(ndx, BOPBiomes.alps, 5);
        this.addBiomeEntry(ndx, BOPBiomes.cold_desert, 10);
        this.addBiomeEntry(ndx, BOPBiomes.snowy_coniferous_forest, 7);
        this.addBiomeEntry(ndx, BOPBiomes.snowy_forest, 7);
        this.addBiomeEntry(ndx, BOPBiomes.snowy_tundra, 10);
        this.addBiomeEntry(ndx, BOPBiomes.glacier, 5);
    }
    
    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public List<BiomeEntry>[] getBiomeEntries() {
        return this.biomeEntries;
    }

    @Override
    public List<Biome> getCustomSurfaces() {
        ImmutableList.Builder<Biome> builder = new ImmutableList.Builder<>();

        this.addBiomeSurface(builder, BOPBiomes.alps);
        this.addBiomeSurface(builder, BOPBiomes.bamboo_forest);
        this.addBiomeSurface(builder, BOPBiomes.bayou);
        this.addBiomeSurface(builder, BOPBiomes.bog);
        this.addBiomeSurface(builder, BOPBiomes.chaparral);
        this.addBiomeSurface(builder, BOPBiomes.cold_desert);
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
        
        return builder.build();
    }
    
    @Override
    public List<Block> getCustomCarvables() {
        ImmutableList.Builder<Block> builder = new ImmutableList.Builder<>();
        
        this.addCarverBlock(builder, BOPBlocks.grass);
        this.addCarverBlock(builder, BOPBlocks.dirt);
        
        return builder.build();
    }
    
    private void addBiomeEntry(int ndx, Optional<Biome> biome, int weight) {
        if (biome != null && biome.isPresent()) {
            this.biomeEntries[ndx].add(new BiomeEntry(biome.get(), weight));
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
}
