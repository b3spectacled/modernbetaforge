package mod.bespectacled.modernbetaforge.compat;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import biomesoplenty.api.biome.BOPBiomes;
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
public class CompatBOP implements Compat, BiomeCompat {
    @SuppressWarnings("unchecked")
    private List<BiomeEntry>[] biomeEntries = new ArrayList[BiomeType.values().length];
    
    @Override
    public void load() {
        for (BiomeType type : BiomeType.values()) {
            this.biomeEntries[type.ordinal()] = new ArrayList<BiomeEntry>();
        }
        
        // In multiple climate zones:
        // * cherry_blossom_grove
        // * flower_field
        // * grassland
        // * grove
        // * lavender_fields
        // * marsh
        // * orchard
        
        // Add DESERT
        int ndx = BiomeType.DESERT.ordinal();
        this.addBiomeEntry(ndx, BOPBiomes.brushland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.chaparral, 10);
        this.addBiomeEntry(ndx, BOPBiomes.lush_desert, 10);
        this.addBiomeEntry(ndx, BOPBiomes.outback, 10);
        this.addBiomeEntry(ndx, BOPBiomes.prairie, 10);
        this.addBiomeEntry(ndx, BOPBiomes.steppe, 10);
        this.addBiomeEntry(ndx, BOPBiomes.wasteland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.xeric_shrubland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.oasis, 5);
        
        // Add WARM
        ndx = BiomeType.WARM.ordinal();
        this.addBiomeEntry(ndx, BOPBiomes.bamboo_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.bayou, 10);
        this.addBiomeEntry(ndx, BOPBiomes.cherry_blossom_grove, 10);
        this.addBiomeEntry(ndx, BOPBiomes.eucalyptus_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.flower_field, 10);
        this.addBiomeEntry(ndx, BOPBiomes.grassland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.grove, 10);
        this.addBiomeEntry(ndx, BOPBiomes.lavender_fields, 10);
        this.addBiomeEntry(ndx, BOPBiomes.lush_swamp, 10);
        this.addBiomeEntry(ndx, BOPBiomes.mangrove, 10);
        this.addBiomeEntry(ndx, BOPBiomes.marsh, 10);
        this.addBiomeEntry(ndx, BOPBiomes.orchard, 10);
        this.addBiomeEntry(ndx, BOPBiomes.overgrown_cliffs, 10);
        this.addBiomeEntry(ndx, BOPBiomes.rainforest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.sacred_springs, 1);
        this.addBiomeEntry(ndx, BOPBiomes.shrubland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.temperate_rainforest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.tropical_rainforest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.wetland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.woodland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.pasture, 10);
        
        // Add COOL
        ndx = BiomeType.COOL.ordinal();
        this.addBiomeEntry(ndx, BOPBiomes.bog, 10);
        this.addBiomeEntry(ndx, BOPBiomes.boreal_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.cherry_blossom_grove, 10);
        this.addBiomeEntry(ndx, BOPBiomes.coniferous_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.crag, 10);
        this.addBiomeEntry(ndx, BOPBiomes.dead_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.dead_swamp, 10);
        this.addBiomeEntry(ndx, BOPBiomes.fen, 10);
        this.addBiomeEntry(ndx, BOPBiomes.flower_field, 10);
        this.addBiomeEntry(ndx, BOPBiomes.grassland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.grove, 10);
        this.addBiomeEntry(ndx, BOPBiomes.highland, 10);
        this.addBiomeEntry(ndx, BOPBiomes.land_of_lakes, 10);
        this.addBiomeEntry(ndx, BOPBiomes.lavender_fields, 10);
        this.addBiomeEntry(ndx, BOPBiomes.maple_woods, 10);
        this.addBiomeEntry(ndx, BOPBiomes.meadow, 10);
        this.addBiomeEntry(ndx, BOPBiomes.moor, 10);
        this.addBiomeEntry(ndx, BOPBiomes.mountain, 10);
        this.addBiomeEntry(ndx, BOPBiomes.mystic_grove, 1);
        this.addBiomeEntry(ndx, BOPBiomes.ominous_woods, 1);
        this.addBiomeEntry(ndx, BOPBiomes.orchard, 10);
        this.addBiomeEntry(ndx, BOPBiomes.quagmire, 10);
        this.addBiomeEntry(ndx, BOPBiomes.redwood_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.seasonal_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.shield, 10);
        this.addBiomeEntry(ndx, BOPBiomes.tundra, 10);
        
        // Add ICY
        ndx = BiomeType.ICY.ordinal();
        this.addBiomeEntry(ndx, BOPBiomes.alps, 10);
        this.addBiomeEntry(ndx, BOPBiomes.cold_desert, 10);
        this.addBiomeEntry(ndx, BOPBiomes.snowy_coniferous_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.snowy_forest, 10);
        this.addBiomeEntry(ndx, BOPBiomes.snowy_tundra, 10);
        this.addBiomeEntry(ndx, BOPBiomes.glacier, 5);
    }

    @Override
    public List<BiomeEntry>[] getBiomeEntries() {
        return this.biomeEntries;
    }

    @Override
    public List<Biome> getCustomSurfaces() {
        ImmutableList.Builder<Biome> builder = new ImmutableList.Builder<>();

        this.addBiomeSurface(builder, BOPBiomes.bamboo_forest);
        this.addBiomeSurface(builder, BOPBiomes.bog);
        this.addBiomeSurface(builder, BOPBiomes.chaparral);
        this.addBiomeSurface(builder, BOPBiomes.cold_desert);
        this.addBiomeSurface(builder, BOPBiomes.dead_swamp);
        this.addBiomeSurface(builder, BOPBiomes.mangrove);
        this.addBiomeSurface(builder, BOPBiomes.overgrown_cliffs);
        this.addBiomeSurface(builder, BOPBiomes.quagmire);
        this.addBiomeSurface(builder, BOPBiomes.redwood_forest);
        this.addBiomeSurface(builder, BOPBiomes.shield);
        this.addBiomeSurface(builder, BOPBiomes.xeric_shrubland);
        
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
}
