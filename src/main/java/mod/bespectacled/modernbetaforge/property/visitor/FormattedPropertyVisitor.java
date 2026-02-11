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

public interface FormattedPropertyVisitor {
    String visit(BooleanProperty property, ResourceLocation registryKey);
    
    String visit(FloatProperty property, ResourceLocation registryKey);
    
    String visit(IntProperty property, ResourceLocation registryKey);
    
    String visit(StringProperty property, ResourceLocation registryKey);
    
    String visit(ListProperty property, ResourceLocation registryKey);
    
    String visit(BiomeProperty property, ResourceLocation registryKey);
    
    String visit(BlockProperty property, ResourceLocation registryKey);
    
    String visit(EntityEntryProperty property, ResourceLocation registryKey);
    
    String visit(ScreenProperty property, ResourceLocation registryKey);
}
