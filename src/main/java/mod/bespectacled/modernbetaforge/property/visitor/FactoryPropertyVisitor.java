package mod.bespectacled.modernbetaforge.property.visitor;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.api.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.EntityEntryProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.property.StringProperty;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;

public interface FactoryPropertyVisitor {
    void visit(BooleanProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(FloatProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(IntProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(StringProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(ListProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(BiomeProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(BlockProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(EntityEntryProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
}
