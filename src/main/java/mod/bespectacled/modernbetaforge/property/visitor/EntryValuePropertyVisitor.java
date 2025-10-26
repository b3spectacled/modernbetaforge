package mod.bespectacled.modernbetaforge.property.visitor;

import mod.bespectacled.modernbetaforge.api.client.property.ScreenProperty;
import mod.bespectacled.modernbetaforge.api.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.EntityEntryProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.property.StringProperty;
import net.minecraft.util.ResourceLocation;

public interface EntryValuePropertyVisitor {
    void visit(BooleanProperty property, int guiIdentifier, boolean value, ResourceLocation registryKey);
    
    void visit(FloatProperty property, int guiIdentifier, Object value, ResourceLocation registryKey);
    
    void visit(IntProperty property, int guiIdentifier, Object value, ResourceLocation registryKey);
    
    void visit(StringProperty property, int guiIdentifier, String value, ResourceLocation registryKey);
    
    void visit(ListProperty property, int guiIdentifier, float value, ResourceLocation registryKey);
    
    void visit(BiomeProperty property, int guiIdentifier, ResourceLocation registryKey);
    
    void visit(BlockProperty property, int guiIdentifier, ResourceLocation registryKey);
    
    void visit(EntityEntryProperty property, int guiIdentifier, ResourceLocation registryKey);
    
    void visit(ScreenProperty property, int guiIdentifier, ResourceLocation registryKey);
}
