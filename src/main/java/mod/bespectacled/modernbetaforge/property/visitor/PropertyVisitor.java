package mod.bespectacled.modernbetaforge.property.visitor;

import mod.bespectacled.modernbetaforge.api.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.EntityEntryProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.property.StringProperty;

public interface PropertyVisitor {
    String visit(BooleanProperty property);
    
    String visit(FloatProperty property);
    
    String visit(IntProperty property);
    
    String visit(StringProperty property);
    
    String visit(ListProperty property);
    
    String visit(BiomeProperty property);
    
    String visit(BlockProperty property);
    
    String visit(EntityEntryProperty property);
}
