package mod.bespectacled.modernbetaforge.world.setting.visitor;

import mod.bespectacled.modernbetaforge.api.world.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.world.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.world.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.world.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.world.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.world.property.StringProperty;
import net.minecraft.util.ResourceLocation;

public interface EntryValuePropertyVisitor {
    void visit(BooleanProperty property, int guiIdentifier, boolean value, ResourceLocation registryKeye);
    
    void visit(FloatProperty property, int guiIdentifier, Object value, ResourceLocation registryKey);
    
    void visit(IntProperty property, int guiIdentifier, Object value, ResourceLocation registryKey);
    
    void visit(StringProperty property, int guiIdentifier, String value, ResourceLocation registryKey);
    
    void visit(ListProperty property, int guiIdentifier, float value, ResourceLocation registryKey);
    
    void visit(BiomeProperty property, int guiIdentifier, ResourceLocation registryKey);
}
