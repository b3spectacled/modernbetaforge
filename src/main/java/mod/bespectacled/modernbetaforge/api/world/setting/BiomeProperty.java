package mod.bespectacled.modernbetaforge.api.world.setting;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.setting.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.GuiPropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public final class BiomeProperty extends StringProperty {
    /**
     * Constructs a new BiomeProperty with an initial biome, storing the biome's registry name.
     * 
     * @param value The initial Biome value.
     */
    public BiomeProperty(Biome value) {
        super(value.getRegistryName().toString());
    }

    @Override
    public String getType() {
        return "biome";
    }

    @Override
    public void visitFactory(FactoryPropertyVisitor visitor, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
        visitor.visit(this, factory, registryKey, jsonObject);
    }

    @Override
    public GuiPageButtonList.GuiListEntry visitGui(GuiPropertyVisitor visitor, int guiIdentifier) {
        return visitor.visit(this, guiIdentifier);
    }
    
    @Override
    public void visitEntryValue(EntryValuePropertyVisitor visitor, int guiIdentifier, Object value, ResourceLocation registryKey) {
        visitor.visit(this, guiIdentifier, registryKey);
    }
    
    public Biome getBiome() {
        return BiomeUtil.getBiome(new ResourceLocation(this.getValue()), "biome_property");
    }
}
