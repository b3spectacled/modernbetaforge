package mod.bespectacled.modernbetaforge.api.world.setting;

import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class BiomeProperty extends StringProperty {
    /**
     * Constructs a new BiomeProperty with an initial string.
     * 
     * @param value The initial String value.
     */
    public BiomeProperty(String value) {
        super(value);
    }

    @Override
    public String getType() {
        return "biome";
    }
    
    public Biome getBiome() {
        return BiomeUtil.getBiome(new ResourceLocation(this.getValue()), "biome_property");
    }
}
