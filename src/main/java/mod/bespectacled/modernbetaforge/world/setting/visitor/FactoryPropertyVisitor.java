package mod.bespectacled.modernbetaforge.world.setting.visitor;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.api.world.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.world.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.world.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.world.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.world.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.world.property.StringProperty;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;

public interface FactoryPropertyVisitor {
    void visit(BooleanProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(FloatProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(IntProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(StringProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(ListProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    void visit(BiomeProperty property, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
}
